package edu.csus.plugin.securecodingassistant.rules;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.QualifiedName;

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
 * @see <a target="_blank" href="https://www.securecoding.cert.org/confluence/display/java/NUM07-J.+Do+not+attempt+comparisons+with+NaN">Java Secure Coding Rule: NUM07-J</a>
 */
class NUM07J_DoNotAttemptComparisonsWithNaN implements IRule {

	@Override
	public boolean violated(ASTNode node) {
		boolean ruleViolated = false;
		
		// Is the node an infix boolean expression?
		if(node instanceof InfixExpression) {
			InfixExpression expression = (InfixExpression)node;
			if(expression.resolveTypeBinding().getName().equals("boolean")) {
				// Get left-hand-side and right-hand-side
				Expression lhs = expression.getLeftOperand(), rhs = expression.getRightOperand();
				if (lhs instanceof QualifiedName)
					ruleViolated = isNaN((QualifiedName)lhs);
				if (rhs instanceof QualifiedName)
					ruleViolated = ruleViolated || isNaN((QualifiedName)rhs);
			}
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
	
	private boolean isNaN(QualifiedName name) {
		return (name.getQualifier().getFullyQualifiedName().equals("Float")
							|| name.getQualifier().getFullyQualifiedName().equals("Double"))
				&& name.getName().getFullyQualifiedName().equals("NaN");
	}

}
