package edu.csus.plugin.securecodingassistant.rules;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * Java Secure Coding Rule: NUM09-J. Do not use floating-point variables as loop counters
 * <p>
 * Floating point numbers are not an appropriate loop counter since floating point
 * arithmetic does not precisely represent decimal values
 * </p>
 * @author Ben White
 * @see <a target="_blank" href="https://www.securecoding.cert.org/confluence/display/java/NUM09-J.+Do+not+use+floating-point+variables+as+loop+counters">NUM09-J. Do not use floating-point variables as loop counters</a>
 */
class NUM09J_DoNotUseFloatingPointAsLoopCounters implements IRule {

	@Override
	public boolean violated(ASTNode node) {
		boolean ruleViolated = false;
		// Is node a for loop?
		if (node instanceof ForStatement) {
			ForStatement statement = (ForStatement) node;
			for(Object o : statement.initializers()) {
				// Is the initializer a declaration?
				if (o instanceof VariableDeclarationExpression) {
					VariableDeclarationExpression dec = (VariableDeclarationExpression)o;
					if (dec.getType().resolveBinding().getName().equals("float")
							|| dec.getType().resolveBinding().getName().equals("Float")
							|| dec.getType().resolveBinding().getName().equals("double")
							|| dec.getType().resolveBinding().getName().equals("Double")) {
						ruleViolated = true;
						break;
					}
				}
			}
		}
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		return "Floating point numbers are not an appropriate loop counter since floating "
				+ "point arithmetic does not precisely represent decimal values";
	}

	@Override
	public String getRuleName() {
		return "NUM09-J. Do not use floating-point variables as loop counters";
	}

	@Override
	public String getRuleRecommendation() {
		return "Implement loop using a integer counter instead.";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_MEDIUM;
	}

}
