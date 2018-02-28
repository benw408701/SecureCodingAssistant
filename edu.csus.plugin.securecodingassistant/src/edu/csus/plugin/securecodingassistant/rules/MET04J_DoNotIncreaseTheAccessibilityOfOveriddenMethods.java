package edu.csus.plugin.securecodingassistant.rules;
import java.util.List;
import java.util.TreeMap;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * <b><i>The text and/or code below is from the CERT website: <a target="_blank" href="https://www.securecoding.cert.org">https://www.securecoding.cert.org</a></i></b>
 * <p>
 * Java Secure Coding Rule: MET04-J. Do not increase the accessibility of overridden or
 * hidden methods
 * </p>
 * <p>
 * CERT Website: Increasing the accessibility of overridden or hidden methods permits a
 * malicious subclass to offer wider access to the restricted method than was originally
 * intended. Consequently, programs must override methods only when necessary and must
 * declare methods final whenever possible to prevent malicious subclassing. When methods
 * cannot be declared final, programs must refrain from increasing the accessibility of
 * overridden methods.
 * </p>
 * @author Ben White (Plugin Logic), CERT (Rule Definition)
 * @see Java Secure Coding Rule defined by CERT: <a target="_blank" href="https://www.securecoding.cert.org/confluence/display/java/MET04-J.+Do+not+increase+the+accessibility+of+overridden+or+hidden+methods">MET04-J</a>
 */
class MET04J_DoNotIncreaseTheAccessibilityOfOveriddenMethods extends SecureCodingRule {
	
	private ICompilationUnit icu;

	@Override
	public boolean violated(ASTNode node) {
		boolean ruleViolated = false;
		
		// Is this a method declaration?
		if (node instanceof MethodDeclaration) {
			MethodDeclaration methodDec = (MethodDeclaration) node;
			// Is it overriding a method declared in the parent class?
			IMethodBinding parentMethod = Utility.getSuperClassDeclaration(methodDec.resolveBinding());
			// If there is a parent method and it isn't clone (the access level of clone is supposed
			// to be increased from protected to public)
			if (parentMethod != null && !parentMethod.getName().equals("clone"))
			{
				// Has the accessibility been increased?
				int parentAccessModifier = parentMethod.getModifiers() &
						(Modifier.PROTECTED | Modifier.PUBLIC | Modifier.FINAL);
				int accessModifier = methodDec.getModifiers() &
						(Modifier.PROTECTED | Modifier.PUBLIC);
				switch(parentAccessModifier) {
				case 0: // Package Private which is no modifier
					// Cannot increase to protected or public
					ruleViolated = (accessModifier & Modifier.PROTECTED) != 0
									|| (accessModifier & Modifier.PUBLIC) != 0;
					break;
				case Modifier.PROTECTED:
					// Cannot increase to public
					ruleViolated = (accessModifier & Modifier.PUBLIC) != 0;
					break;
				case Modifier.PUBLIC:
					// super class is already public, cannot increase visibility
					break;
				case Modifier.FINAL:
					// super class is already final, cannot increase visibility
					break;
				}
			}
			if (ruleViolated)
				ruleViolated = super.violated(node);
		}
		
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		return "CERT Website-Increasing the accessibility of overridden or hidden "
				+ "methods permits a malicious subclass to offer wider access to "
				+ "the restricted method than was originally intended. Consequently, "
				+ "programs must override methods only when necessary and must declare "
				+ "methods final whenever possible to prevent malicious subclassing. "
				+ "When methods cannot be declared final, programs must refrain from "
				+ "increasing the accessibility of overridden methods.";
	}

	@Override
	public String getRuleName() {
		return Globals.RuleNames.MET04_J;
	}

	@Override
	public String getRuleRecommendation() {
		return "Do not increase the accessibility of overridden methods.";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_MEDIUM;
	}

	@Override
	public String getRuleURL() {
		return "https://www.securecoding.cert.org/confluence/display/java/MET04-J.+Do+not+increase+the+accessibility+of+overridden+or+hidden+methods";
	}
	
	@Override
	public String getRuleID() {
		return Globals.RuleID.MET04_J;
	}

	@Override
	public TreeMap<String, ASTRewrite> getSolutions(ASTNode node) {

		TreeMap<String, ASTRewrite> list = new TreeMap<>();
		list.putAll(super.getSolutions(node));

		try {
			MethodDeclaration md = (MethodDeclaration) node;

			IMethodBinding parentMethod = Utility.getSuperClassDeclaration(md.resolveBinding());
			IMethod parentIM = (IMethod) parentMethod.getJavaElement();
			icu = parentIM.getCompilationUnit();

			ASTParser parser = ASTParser.newParser(AST.JLS8);
			parser.setSource(icu);
			parser.setResolveBindings(true);
			// parser.setStatementsRecovery(statementsRecovery);
			CompilationUnit cu = (CompilationUnit) parser.createAST(null);
			TypeDeclaration typeDeclaration = (TypeDeclaration) cu.types().get(0);
			MethodDeclaration[] parentMDs = typeDeclaration.getMethods();
			for (MethodDeclaration parentMD : parentMDs) {
				if (md.getName().getIdentifier().equals(parentMD.getName().getIdentifier())
						&& equals(md.parameters(), parentMD.parameters())) {
					for (Object obj : parentMD.modifiers()) {
						if (obj instanceof Modifier) {
							Modifier m = (Modifier) obj;
							if (m.isProtected()) {
								AST ast = cu.getAST();
								ASTRewrite rewrite = ASTRewrite.create(ast);
								ListRewrite listRewrite = rewrite.getListRewrite(parentMD,
										MethodDeclaration.MODIFIERS2_PROPERTY);
								listRewrite.insertAfter(ast.newModifier(ModifierKeyword.FINAL_KEYWORD), m, null);
								list.put("Add final to super class method", rewrite);

								break;
							}
						}
					}

				}
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}

		return list;
	}
	
	public ICompilationUnit getICompilationUnit() {
		return icu;
	}

	@SuppressWarnings("rawtypes")
	private boolean equals(List parameters, List parameters2) {
		if (parameters == null && parameters2 == null)
			return true;
		else if (parameters != null && parameters2 != null) {
			if (parameters.size() == parameters2.size()) {
				for (int i = 0; i < parameters.size(); i++) {
					Type type1 = null, type2 = null;
					if (parameters.get(i) instanceof SingleVariableDeclaration) 
						type1 = ((SingleVariableDeclaration) parameters.get(i)).getType();
					if (parameters2.get(i) instanceof SingleVariableDeclaration) 
						type2 = ((SingleVariableDeclaration) parameters2.get(i)).getType();
					if (type1 == null && type2 != null || type2 == null && type1 != null
							|| type1 != null && type2 != null && !type1.toString().equals(type2.toString()))
						return false;
				}
				return true;
			}
		} 
		return false;
	}
}
