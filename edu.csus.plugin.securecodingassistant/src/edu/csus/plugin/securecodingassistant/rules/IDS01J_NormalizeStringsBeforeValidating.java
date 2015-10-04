/**
 * 
 */
package edu.csus.plugin.securecodingassistant.rules;

import java.text.Normalizer;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * Java Secure Coding Rule: IDS01-J. Normalize strings before validating
 * them
 * <p>
 * CERT Website: Many applications that accept untrusted input strings employ input filtering
 * and validation mechanisms based on the strings' character data. For example,
 * an application's strategy for avoiding cross-site scripting (XSS)
 * vulnerabilities may include forbidding {@code <script>} tags in
 * inputs. Such blacklisting mechanisms are a useful part of a security
 * strategy, even though they are insufficient for complete input validation
 * and sanitization.
 * </p>
 * @author Ben White
 * @see <a target="_blank" href="https://www.securecoding.cert.org/confluence/display/java/IDS01-J.+Normalize+strings+before+validating+them">Java Secure Coding Rule: IDS01-J</a>
 *
 */
class IDS01J_NormalizeStringsBeforeValidating implements IRule {

	@Override
	public boolean violated(ASTNode node) {
		boolean ruleViolated = false;
		
		// Check to see if Pattern.matcher is called and ensure that Normalizer.normalize
		// Was called beforehand
		if(node instanceof MethodInvocation) {
			MethodInvocation method = (MethodInvocation)node;
			if(Utility.calledMethod(method, Pattern.class.getCanonicalName(), "matcher")) {
				SimpleName argument = null;
				if (method.arguments().get(0) instanceof SimpleName)
					argument = (SimpleName)method.arguments().get(0);
				ruleViolated = !Utility.calledPrior(method, Normalizer.class.getCanonicalName(), "normalize", argument);
			}
		}
		
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		return "CERT Website-Many applications that accept untrusted input strings employ input filtering "
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

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_HIGH;
	}

}
