/**
 * 
 */
package edu.csus.plugin.securecodingassistant.rules;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodInvocation;

/**
 * Java Secure Coding Rule: IDS01-J. Normalize strings before validating
 * them
 * <p>
 * Many applications that accept untrusted input strings employ input filtering
 * and validation mechanisms based on the strings' character data. For example,
 * an application's strategy for avoiding cross-site scripting (XSS)
 * vulnerabilities may include forbidding <code>&lt;script&gt;</code> tags in
 * inputs. Such blacklisting mechanisms are a useful part of a security
 * strategy, even though they are insufficient for complete input validation
 * and sanitization.
 * </p>
 * @author Ben White
 * @see <a href="https://www.securecoding.cert.org/confluence/display/java/IDS01-J.+Normalize+strings+before+validating+them">Java Secure Coding Rule: IDS01-J</a>
 *
 */
// TODO: Make sure that the parameter to Normalizer.normalize() is the same that was used for Pattern.matcher()
class IDS01J_NormalizeStringsBeforeValidating implements IRule {

	/* (non-Javadoc)
	 * @see edu.csus.plugin.securecodingassistant.rules.IRule#violated(org.eclipse.jdt.core.dom.ASTNode)
	 */
	@Override
	public boolean violated(ASTNode node) {
		boolean ruleViolated = false;
		
		// Check to see if Pattern.matcher is called and ensure that Normalizer.normalize
		// Was called beforehand
		if(node instanceof MethodInvocation) {
			MethodInvocation method = (MethodInvocation)node;
			ruleViolated = Utility.calledMethod(method, "Pattern", "matcher")
					&& !Utility.calledPrior(method, "Normalizer", "normalize");
		}
		
		return ruleViolated;
	}

	/* (non-Javadoc)
	 * @see edu.csus.plugin.securecodingassistant.rules.IRule#getRuleText()
	 */
	@Override
	public String getRuleText() {
		return "Many applications that accept untrusted input strings employ input filtering "
				+ "and validation mechanisms based on the strings' character data. For example,"
				+ " an application's strategy for avoiding cross-site scripting (XSS) "
				+ "vulnerabilities may include forbidding <script> tags in inputs. Such "
				+ "blacklisting mechanisms are a useful part of a security strategy, even though"
				+ " they are insufficient for complete input validation and sanitization.";
	}

	@Override
	public String getRuleName() {
		return "IDS01-J. Normalize strings before validating them";
	}

	@Override
	public String getRuleRecommendation() {
		return "Using Pattern.matcher() is a great way detect harmful things like angle brackets "
				+ "which could indicate script tags, but the normalize method MUST be called "
				+ "before running Pattern.matcher()";
	}

}
