package edu.csus.plugin.securecodingassistant.rules;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * Java Secure Coding Rule: SEC07-J. Call the superclass's getPermissions() method when
 * writing a custom class loader
 * <p>
 * CERT Website: When a custom class loader must override the <code>getPermissions()</code>
 * method, the implementation must consult the default system policy by explicitly
 * invoking the superclass's <code>getPermissions()</code> method before assigning
 * arbitrary permissions to the code source. A custom class loader that ignores the
 * superclass's <code>getPermissions()</code> could load untrusted classes with
 * elevated privileges. <code>ClassLoader</code> is abstract and must not be directly
 * subclassed. 
 * </p>
 * @author Ben White
 * @see <a target="_blank" href="https://www.securecoding.cert.org/confluence/display/java/SEC07-J.+Call+the+superclass%27s+getPermissions%28%29+method+when+writing+a+custom+class+loader">Java Secure Coding Rule: SEC07-J</a>
 */
class SEC07J_CallTheSuperclassGetPermissionsMethod implements IRule {

	@Override
	public boolean violated(ASTNode node) {
		// TODO Auto-generated method stub
		return false;
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

}
