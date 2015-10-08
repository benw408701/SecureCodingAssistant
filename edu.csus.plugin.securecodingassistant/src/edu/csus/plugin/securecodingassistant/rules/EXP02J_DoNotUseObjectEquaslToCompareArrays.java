package edu.csus.plugin.securecodingassistant.rules;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodInvocation;
import edu.csus.plugin.securecodingassistant.Globals;

/**
 * Java Secure Coding Rule: EXP02-J. Do not use the Object.equals() method
 * to compare two arrays
 * <p>
 * CERT Website: In Java, arrays are objects and support object methods such
 * as <code>Object.equals()</code>. However, arrays do not support any methods
 * besides those provided by <code>Object</code>. Consequently, using 
 * <code>Object.equals()</code> on any array compares only array references, not
 * their contents. Programmers who wish to compare the contents of two arrays
 * must use the static two-argument <code>Arrays.equals()</code> method.
 * This method considers two arrays equivalent if both arrays contain the same
 * number of elements, and all corresponding pairs of elements in the two arrays
 * are equivalent, according to <code>Object.equals()</code>. In other words,
 * two arrays are equal if they contain equivalent elements in the same order.
 * To test for reference equality, use the reference equality operators, == and
 * !=.  
 * </p>
 * <p>
 * Because the effect of using <code>Object.equals()</code> to compare two arrays
 * is often misconstrued as content equality, and because a better alternative
 * exists in the use of reference equality operators, the use of the 
 * <code>Object.equals()</code> method to compare two arrays is disallowed.
 * </p>
 * @author Ben White
 * @see <a target="_blank" href="https://www.securecoding.cert.org/confluence/display/java/EXP02-J.+Do+not+use+the+Object.equals%28%29+method+to+compare+two+arrays">Java Secure Coding Rule: EXP02-J</a>
 */
class EXP02J_DoNotUseObjectEquaslToCompareArrays implements IRule {

	@Override
	public boolean violated(ASTNode node) {
		boolean ruleViolated = false;
		
		// Is node a method invocation?
		if (node instanceof MethodInvocation) {
			MethodInvocation method = (MethodInvocation)node;
			// Was equals called from an array?
			ruleViolated = method.getExpression().resolveTypeBinding().isArray()
					&& method.getName().toString().equals("equals");
		}
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		return "CERT Website-In Java, arrays are objects and support object "
				+ "methods such as Object.equals(). However, arrays do not "
				+ "support any methods besides those provided by Object. "
				+ "Consequently, using Object.equals() on any array compares only "
				+ "array references, not their contents.";
	}

	@Override
	public String getRuleName() {
		return "EXP02-J. Do not use the Object.equals() method to compare"
				+ " two arrays";
	}

	@Override
	public String getRuleRecommendation() {
		return "CERT Website-Programmers who wish to "
				+ "compare the contents of two arrays must use the static two-"
				+ "argument Arrays.equals() method. This method considers two "
				+ "arrays equivalent if both arrays contain the same number of "
				+ "elements, and all corresponding pairs of elements in the two "
				+ "arrays are equivalent, according to Object.equals(). In other "
				+ "words, two arrays are equal if they contain equivalent elements "
				+ "in the same order. To test for reference equality, use the "
				+ "reference equality operators, == and !=.";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_MEDIUM;
	}

}
