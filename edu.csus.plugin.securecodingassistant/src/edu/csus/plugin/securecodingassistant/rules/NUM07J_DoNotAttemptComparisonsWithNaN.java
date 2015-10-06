package edu.csus.plugin.securecodingassistant.rules;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * Java Secure Coding Rule: NUM07-J. Do not attempt comparisons with NaN
 * <p>
 * CERT Website: NaN (not-a-number) is unordered, so the numerical comparison
 * operators <, <=, >, and >= return false if either or both operands are NaN.
 * The equality operator == returns false if either operand is NaN, and the
 * inequality operator != returns true if either operand is NaN.
 * </p>
 * @author Ben White
 * @see <a target="_blank" href="https://www.securecoding.cert.org/confluence/display/java/NUM07-J.+Do+not+attempt+comparisons+with+NaN">NUM07-J. Do not attempt comparisons with NaN</a>
 */
class NUM07J_DoNotAttemptComparisonsWithNaN implements IRule {

	@Override
	public boolean violated(ASTNode node) {
		boolean ruleViolated = false;
		
		float test = 1/0;
		if(test == Float.NaN) {
			ruleViolated = true;
		}
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		return "NaN (not-a-number) is unordered, so the numerical comparison operators "
				+ "<, <=, >, and >= return false if either or both operands are NaN. "
				+ "The equality operator == returns false if either operand is NaN, and "
				+ "the inequality operator != returns true if either operand is NaN.";
	}

	@Override
	public String getRuleName() {
		return "NUM07-J. Do not attempt comparisons with NaN";
	}

	@Override
	public String getRuleRecommendation() {
		return "Use Double.isNaN() or Float.isNaN() instead";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_LOW;
	}

}
