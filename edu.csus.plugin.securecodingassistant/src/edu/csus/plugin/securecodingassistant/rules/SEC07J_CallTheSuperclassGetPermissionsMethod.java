package edu.csus.plugin.securecodingassistant.rules;

import java.security.CodeSource;
import java.security.Permissions;
import java.security.SecureClassLoader;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * <b><i>The text and/or code below is from the CERT website: <a target="_blank" href="https://www.securecoding.cert.org">https://www.securecoding.cert.org</a></i></b>
 * <p>
 * Java Secure Coding Rule: SEC07-J. Call the superclass's getPermissions() method when
 * writing a custom class loader
 * </p>
 * <p>
 * CERT Website: When a custom class loader must override the <code>getPermissions()</code>
 * method, the implementation must consult the default system policy by explicitly
 * invoking the superclass's <code>getPermissions()</code> method before assigning
 * arbitrary permissions to the code source. A custom class loader that ignores the
 * superclass's <code>getPermissions()</code> could load untrusted classes with
 * elevated privileges. <code>ClassLoader</code> is abstract and must not be directly
 * subclassed. 
 * </p>
 * @author Ben White (Plugin Logic), CERT (Rule Definition)
 * @see Java Secure Coding Rule defined by CERT: <a target="_blank" href="https://www.securecoding.cert.org/confluence/display/java/SEC07-J.+Call+the+superclass%27s+getPermissions%28%29+method+when+writing+a+custom+class+loader">SEC07-J</a>
 */
class SEC07J_CallTheSuperclassGetPermissionsMethod extends SecureCodingRule {

	@Override
	public boolean violated(ASTNode node) {
		// TODO Auto-generated method stub
		boolean ruleViolated = false;
		
		// Is this a method declaration for getPermissions?
		if (node instanceof MethodDeclaration) {
			MethodDeclaration methodDec = (MethodDeclaration) node;
			
			// Does the declaring class extend SecureClassLoader?
			if (methodDec.resolveBinding() != null &&
					methodDec.resolveBinding().getDeclaringClass() != null &&
					methodDec.resolveBinding().getDeclaringClass().getSuperclass() != null &&
					methodDec.resolveBinding().getDeclaringClass().getSuperclass().getQualifiedName().equals(SecureClassLoader.class.getCanonicalName())) {
				ruleViolated = true; // Rule is violated if method exists without call to super
				
				// Is there a call to super.getPermissions within the method declaration?
				ASTNodeProcessor processor = new ASTNodeProcessor();
				node.accept(processor);
				for (NodeNumPair methodNode : processor.getSuperMethodInvocations()) {
					assert methodNode.getNode() instanceof SuperMethodInvocation;
					SuperMethodInvocation method = (SuperMethodInvocation) methodNode.getNode();
					if (method.getName().getFullyQualifiedName().equals("getPermissions")) {
						ruleViolated = false;
						break;
					}
				}
			}
			
			if (ruleViolated)
				ruleViolated = super.violated(node);
		}
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		return "Any custom class loader must call the parent's getPermissions() method to"
				+ " consult the default system policy.";
	}

	@Override
	public String getRuleName() {
		return Globals.RuleNames.SEC07_J;
	}

	@Override
	public String getRuleRecommendation() {
		return "Call super.getPermissions() whenever overridding getPermissions.";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_HIGH;
	}

	@Override
	public String getRuleID() {
		return Globals.RuleID.SEC07_J;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TreeMap<String, ASTRewrite> getSolutions(ASTNode node){
		if (!violated(node))
			throw new IllegalArgumentException("Doesn't violate rule " + getRuleID());
		
		TreeMap<String, ASTRewrite> list = new TreeMap<>();
		
		MethodDeclaration methodDeclaration = (MethodDeclaration)node;
		
		//get the method's arguments variable name CodeSource cs
		List<?> paras = methodDeclaration.parameters();
		SimpleName argu = null;
		for (Object obj: paras) {
			if (obj instanceof SingleVariableDeclaration) {
				SingleVariableDeclaration svd = (SingleVariableDeclaration)obj;
				if (svd.resolveBinding() != null && svd.resolveBinding().getType() != null
						&& svd.resolveBinding().getType().getBinaryName().equals(CodeSource.class.getCanonicalName())) {
					argu = svd.getName();
					break;
				}
			}
		}
		
		AST ast = node.getAST();
		ASTRewrite rewrite = ASTRewrite.create(ast);
		
		//replace all the new Permission with super.getPermissions(argu)
		ClassInstanceCreationVisitor cicvisitor = new ClassInstanceCreationVisitor();
		node.accept(cicvisitor);
		HashSet<ClassInstanceCreation> set = cicvisitor.getClassInstanceCreation();
		
		//super.getPermission(argu)
		SuperMethodInvocation newMI = ast.newSuperMethodInvocation();
		newMI.setName(ast.newSimpleName("getPermissions"));
		if (argu != null)
			newMI.arguments().add(ast.newSimpleName(argu.getIdentifier()));
		
		
		if (set.isEmpty()) {
			// insert PermissionCollection pc = super.getPermission(argu);
			VariableDeclarationFragment vdf = ast.newVariableDeclarationFragment();
			vdf.setName(ast.newSimpleName("permissionCollection"));
			vdf.setInitializer(newMI);
			VariableDeclarationStatement vds = ast.newVariableDeclarationStatement(vdf);
			vds.setType(ast.newSimpleType(ast.newName("PermissionCollection")));
			
			Block block = methodDeclaration.getBody();
			ListRewrite listRewrite = rewrite.getListRewrite(block, Block.STATEMENTS_PROPERTY);
			listRewrite.insertFirst(vds, null);
		} else {
			//replace new Permission with super.getPermission(argu)
			for (ClassInstanceCreation cic: set) {
				rewrite.replace(cic, newMI, null);
			}
		}
		list.put("Call super.getPermissions()", rewrite);
		
		list.putAll(super.getSolutions(node));
		return list;
	}
	
	private class ClassInstanceCreationVisitor extends ASTVisitor{
		
		private HashSet<ClassInstanceCreation> sets;
		
		public ClassInstanceCreationVisitor() {
			sets = new HashSet<>();
		}
		
		public boolean visit(ClassInstanceCreation node) {
			if (node.resolveTypeBinding() != null && node.resolveTypeBinding().getQualifiedName().equals(Permissions.class.getName())) {
				sets.add(node);
			}
			return super.visit(node);
		}
		
		public HashSet<ClassInstanceCreation> getClassInstanceCreation() {
			return sets;
		}
	}
}
