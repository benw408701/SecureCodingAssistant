package edu.csus.plugin.securecodingassistant.rules;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * Java Secure Coding Rule: DCL02-J. Do not modify the collection's elements during an
 * enhanced for statement
 * <p>
 * CERT Website: Unlike the basic for statement, assignments to the loop variable fail to affect the
 * loop's iteration order over the underlying set of objects. Consequently, an assignment
 * to the loop variable is equivalent to modifying a variable local to the loop body whose
 * initial value is the object referenced by the loop iterator. This modification is not
 * necessarily erroneous but can obscure the loop functionality or indicate a
 * misunderstanding of the underlying implementation of the enhanced for statement.
 * </p>
 * <p>
 * Declare all enhanced for statement loop variables final. The final declaration causes
 * Java compilers to flag and reject any assignments made to the loop variable.
 * </p>
 * @author Ben White
 * @see <a target="_blank" href="https://www.securecoding.cert.org/confluence/display/java/DCL02-J.+Do+not+modify+the+collection%27s+elements+during+an+enhanced+for+statement">Java Secure Coding Rule: DCL02-J</a>
 */
class DCL02J_DoNotModifyElements implements IRule {

	@Override
	public boolean violated(ASTNode node) {
		boolean ruleViolated = false;
		
		if(node instanceof EnhancedForStatement) {
			ruleViolated = true;
			EnhancedForStatement statement = (EnhancedForStatement)node;
			SingleVariableDeclaration dec = statement.getParameter();
			// Look to see if they declared as final
			for (Object o : dec.modifiers()) {
				assert o instanceof Modifier;
				Modifier m = (Modifier) o;
				if (m.isFinal()) {
					ruleViolated = false;
					break;
				}
			}
		}
		
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		return "CERT Website-Unlike the basic for statement, assignments to the loop "
				+ "variable fail to affect the loop's iteration order over the underlying "
				+ "set of objects. Consequently, an assignment to the loop variable is "
				+ "equivalent to modifying a variable local to the loop body whose initial"
				+ " value is the object referenced by the loop iterator. This modification"
				+ " is not necessarily erroneous but can obscure the loop functionality or"
				+ " indicate a misunderstanding of the underlying implementation of the "
				+ "enhanced for statement.";
	}

	@Override
	public String getRuleName() {
		return "DCL02-J. Do not modify the collection's elements during an enhanced for"
				+ " statement";
	}

	@Override
	public String getRuleRecommendation() {
		return "Declare all enhanced for statement loop variables final. The final "
				+ "declaration causes Java compilers to flag and reject any assignments"
				+ " made to the loop variable.";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_LOW;
	}

}
