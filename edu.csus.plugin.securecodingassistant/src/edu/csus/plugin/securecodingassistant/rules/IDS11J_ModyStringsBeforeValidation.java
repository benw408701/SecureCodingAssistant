/**
 * 
 */
package edu.csus.plugin.securecodingassistant.rules;

import org.eclipse.jdt.core.dom.ASTNode;

/**
 * Java Secure Coding Rule: IDS11-J. Perform any string modifications before validation
 * <p>
 * It is important that a string not be modified after validation has occurred because doing 
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
		// TODO Check to see if a string object has been modified. If it has, then make sure
		// Pattern.matcher wasn't called previously.
		return false;
	}

	@Override
	public String getRuleText() {
		return "It is important that a string not be modified after validation has occurred because "
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
		return "Do not modify a string after validating it";
	}

}
