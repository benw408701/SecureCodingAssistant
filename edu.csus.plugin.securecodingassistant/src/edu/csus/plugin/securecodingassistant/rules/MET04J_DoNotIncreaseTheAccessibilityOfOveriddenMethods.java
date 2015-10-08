package edu.csus.plugin.securecodingassistant.rules;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * Java Secure Coding Rule: MET04-J. Do not increase the accessibility of overridden or
 * hidden methods
 * <p>
 * CERT Website: Increasing the accessibility of overridden or hidden methods permits a
 * malicious subclass to offer wider access to the restricted method than was originally
 * intended. Consequently, programs must override methods only when necessary and must
 * declare methods final whenever possible to prevent malicious subclassing. When methods
 * cannot be declared final, programs must refrain from increasing the accessibility of
 * overridden methods.
 * </p>
 * @author Ben White
 * @see <a target="_blank" href="https://www.securecoding.cert.org/confluence/display/java/MET04-J.+Do+not+increase+the+accessibility+of+overridden+or+hidden+methods">Java Secure Coding Rule: MET04-J</a>
 */
class MET04J_DoNotIncreaseTheAccessibilityOfOveriddenMethods implements IRule {

	@Override
	public boolean violated(ASTNode node) {
		// TODO Auto-generated method stub
		return false;
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
		return "MET04-J. Do not increase the accessibility of overridden or hidden methods";
	}

	@Override
	public String getRuleRecommendation() {
		return "Do not increase the accessibility of overridden methods.";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_MEDIUM;
	}

}
