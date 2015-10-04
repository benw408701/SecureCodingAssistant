/**
 * 
 */
package edu.csus.plugin.securecodingassistant.rules;

import java.util.regex.Pattern;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * Java Secure Coding Rule: IDS11-J. Perform any string modifications before validation
 * <p>
 * CERT Website: It is important that a string not be modified after validation has occurred because doing 
 * so may allow an attacker to bypass validation. For example, a program may filter out the 
 * {@code <script>} tags from HTML input to avoid cross-site scripting (XSS) and other
 * vulnerabilities. If exclamation marks (!) are deleted from the input following validation,
 * an attacker may pass the string "{@code <scr!ipt>}" so that the validation check fails to detect the
 * {@code <script>} tag, but the subsequent removal of the exclamation mark creates a
 * {@code <script>} tag in the input.
 * </p>  
 * @author Ben White
 * @see <a target="_blank" href="https://www.securecoding.cert.org/confluence/display/java/IDS11-J.+Perform+any+string+modifications+before+validation">Java Secure Coding Rule: IDS11-J</a>
 */
public class IDS11J_ModyStringsBeforeValidation implements IRule {

	@Override
	public boolean violated(ASTNode node) {
		boolean ruleViolated = false;
		
		// Check to see if Pattern.matcher was used and if it was make sure the string
		// wasn't modified afterwards
		if(node instanceof MethodInvocation &&
				Utility.calledMethod((MethodInvocation)node, Pattern.class.getCanonicalName(), "matcher")) {
			MethodInvocation method = (MethodInvocation)node;
			SimpleName str = (SimpleName)method.arguments().get(0);
			
			ruleViolated = Utility.modifiedAfter(method, str);
		}
		
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		return "CERT Website-It is important that a string not be modified after validation has occurred because "
				+ "doing so may allow an attacker to bypass validation. For example, a program may "
				+ "filter out the <script> tags from HTML input to avoid cross-site scripting (XSS) "
				+ "and other vulnerabilities. If exclamation marks (!) are deleted from the input "
				+ "following validation, an attacker may pass the string \"<scr!ipt>\" so that the "
				+ "validation check fails to detect the <script> tag, but the subsequent removal of "
				+ "the exclamation mark creates a <script> tag in the input.";
	}

	@Override
	public String getRuleName() {
		return "IDS11-J. Perform any string modifications before validation";
	}

	@Override
	public String getRuleRecommendation() {
		return "The Pattern.matcher() method should be called after all modifications to the string."
				+ " Move any string modifications so that they occur prior to Pattern.matcher().";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_HIGH;
	}

}
