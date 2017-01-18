package edu.csus.plugin.securecodingassistant.rules;

import java.io.InputStream;
import java.util.HashSet;
import java.util.TreeMap;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * <b><i>The text and/or code below is from the CERT website: <a target="_blank" href="https://www.securecoding.cert.org">https://www.securecoding.cert.org</a></i></b>
 * <p>
 * Java Secure Coding Rule: STR00-J. Don't form strings containing partial characters from
 * variable-width encodings.
 * </p>
 * <p>
 * Programmers must not form strings containing partial characters, for example, when converting
 * variable-width encoded character data to strings. Avoid this by not building a text string until
 * confirmed that all data has been read from the buffer.
 * </p>
 * @author Ben White (Plugin Logic), CERT (Rule Definition)
 * @see Java Secure Coding Rule defined by CERT: <a target="_blank" href="https://www.securecoding.cert.org/confluence/display/java/STR00-J.+Don%27t+form+strings+containing+partial+characters+from+variable-width+encodings">STR00-J</a>
 */
class STR00J_PartialCharFromVarWidthEnc extends SecureCodingRule {

	@Override
	public boolean violated(ASTNode node) {
		boolean ruleViolated = false;
		
		// Is data being read from an input stream? Class = InputStream, method = read.
		if (node instanceof MethodInvocation
				&& Utility.calledMethod((MethodInvocation)node, InputStream.class.getCanonicalName(), "read")) {
			
			// TODO: What if in another type of loop?
			// check to see if in while loop
			ASTNode encNode = Utility.getEnclosingNode(node, WhileStatement.class);
			if (encNode instanceof WhileStatement) {
				WhileStatement wStmt = (WhileStatement)encNode;
				MethodInvocation method = (MethodInvocation)node;
				
				// Capture identifier of byte buffer
				SimpleName idBuffer = method.arguments().size() < 1 ? null : (SimpleName) method.arguments().get(0);
				
				// If string is being constructed in the while loop then rule violated. Calling string constructor
				// with byte buffer as the parameter
				ruleViolated = Utility.containsInstanceCreation(wStmt.getBody(), String.class.getCanonicalName(), idBuffer);
			}
			if (ruleViolated)
				ruleViolated = super.violated(node);
		}

		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		return "Programmers must not form strings containing partial characters, for example, when "
				+ "converting variable-width encoded character data to strings.";
	}

	@Override
	public String getRuleName() {
		return Globals.RuleNames.STR00_J;
	}

	@Override
	public String getRuleRecommendation() {
		return "Defer building text string until all data has been read by the buffer";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_HIGH;
	}

	@Override
	public String getRuleID() {
		return Globals.RuleID.STR00_J;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TreeMap<String, ASTRewrite> getSolutions(ASTNode node){
		if (!violated(node))
			throw new IllegalArgumentException("Doesn't violate rule " + getRuleID());
		
		TreeMap<String, ASTRewrite> list = new TreeMap<>();
		
		//get argument variable name bytesRead = in.read(data, offset, data.length - offset)
		MethodInvocation methodInvocation = (MethodInvocation)node;
		SimpleName data = methodInvocation.arguments().size() < 1 ? null : (SimpleName) methodInvocation.arguments().get(0);
		
		//get while loop stmt
		ASTNode whileNode = Utility.getEnclosingNode(node, WhileStatement.class);
		WhileStatement whilestmt = (WhileStatement)whileNode;
		
		//find all new String with data as argument
		StringInstanceCreationVisitor sicvisitor = new StringInstanceCreationVisitor(data);
		whilestmt.accept(sicvisitor);
		
		HashSet<ClassInstanceCreation> set = sicvisitor.getClassInstanceCreation();
		
		AST ast = node.getAST();
		ASTRewrite rewrite = ASTRewrite.create(ast);
		
		if (!set.isEmpty()) {
			//delete all class instance creation
			for (ClassInstanceCreation cic : set) {
				Statement stmt = (Statement) SecureCodingNodeVisitor.getStatement(cic);
				if (stmt != null) {
					rewrite.remove(stmt, null);
				}
			}
		}
		
		// insert String str = new String(data, 0, offset, "UTF-8");
		VariableDeclarationFragment vdf = ast.newVariableDeclarationFragment();
		vdf.setName(ast.newSimpleName("input"));
		//new String (data, 0, offset, "UTF-8");
		ClassInstanceCreation cic = ast.newClassInstanceCreation();
		cic.setType(ast.newSimpleType(ast.newName(String.class.getSimpleName())));
		if (data != null )
			cic.arguments().add(ast.newSimpleName(data.getIdentifier()));
		cic.arguments().add(ast.newNumberLiteral("0"));
		cic.arguments().add(ast.newName("offset"));
		StringLiteral sl = ast.newStringLiteral();
		sl.setLiteralValue("UTF-8");
		cic.arguments().add(sl);
		vdf.setInitializer(cic);
		VariableDeclarationStatement vds = ast.newVariableDeclarationStatement(vdf);
		vds.setType(ast.newSimpleType(ast.newName(String.class.getSimpleName())));
		
		ASTNode blockAST = Utility.getEnclosingNode(whileNode, Block.class);
		if (blockAST instanceof Block) {
			ListRewrite listRewrite = rewrite.getListRewrite(blockAST, Block.STATEMENTS_PROPERTY);
			listRewrite.insertAfter(vds, whilestmt, null);
			list.put("Create String after while loop", rewrite);
		}
		
		list.putAll(super.getSolutions(node));
		return list;
	}
	
	private class StringInstanceCreationVisitor extends ASTVisitor{
		private HashSet<ClassInstanceCreation> stringInstanceCreation;
		private SimpleName data;
		
		public StringInstanceCreationVisitor(SimpleName data) {
			this.data = data;
			stringInstanceCreation = new HashSet<>();
		}
		
		public boolean visit(ClassInstanceCreation cic) {
			if (cic.getType() != null && cic.getType().resolveBinding() != null
					&& cic.getType().resolveBinding().getBinaryName().equals(String.class.getCanonicalName())) {
				if (data == null || Utility.argumentMatch(cic.arguments(), data))
					stringInstanceCreation.add(cic);
			}
			return super.visit(cic);
		}
		
		public HashSet<ClassInstanceCreation> getClassInstanceCreation() {
			return stringInstanceCreation;
		}
	}
}
