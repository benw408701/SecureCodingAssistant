package edu.csus.plugin.securecodingassistant.rules;

import java.security.SecureClassLoader;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
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
class SEC07J_CallTheSuperclassGetPermissionsMethod implements IRule {

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
		return "SEC07-J. Call the superclass's getPermissions() method when writing"
				+ "a custom class loader.";
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
	public String getRuleURL() {
		return "https://www.securecoding.cert.org/confluence/display/java/SEC07-J.+Call+the+superclass%27s+getPermissions%28%29+method+when+writing+a+custom+class+loader";
	}

}
