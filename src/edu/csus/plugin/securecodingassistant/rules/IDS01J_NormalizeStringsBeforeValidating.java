/**
 * 
 */
package edu.csus.plugin.securecodingassistant.rules;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
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
 * @see <a href="https://www.securecoding.cert.org/confluence/display/java/IDS01-J.+Normalize+strings+before+validating+them"
 * Java Secure Coding Rule: IDS07-J</a>
 *
 */
class IDS01J_NormalizeStringsBeforeValidating implements IRule {

	/* (non-Javadoc)
	 * @see edu.csus.plugin.securecodingassistant.rules.IRule#violated(org.eclipse.jdt.core.dom.ASTNode)
	 */
	@Override
	public boolean violated(ASTNode node) {
		boolean ruleViolated = false;
		
		// Check to see if Pattern.matcher is called
		if(node instanceof MethodInvocation) {
			MethodInvocation method = (MethodInvocation)node;
			ruleViolated = Utility.calledMethod(method, "Pattern", "matcher");
		}
		
		// Pattern.matcher was called, look to see if Normalizer.normalize
		// was called beforehand
		if(ruleViolated) {
			ASTNode parent = node.getParent();

			// Go to block level
			while(!(parent instanceof Block)) {
				parent = parent.getParent();
			}
			
			// Confirm at block level
			if (parent instanceof Block) {
				Block block = (Block)parent;
				ASTNodeProcessor processor = new ASTNodeProcessor();
				block.accept(processor);
				ArrayList<MethodInvocation> methods = processor.getMethods();
				
				MethodInvocation method = null;
				for (int i = 0; i < methods.size() && method != node; i++) {
					method = methods.get(i);
					// Check to see if normalize was called prior to pattern matcher,
					// If it was then the rule is no longer violated
					ruleViolated = ruleViolated && !Utility.calledMethod(method, "Normalizer", "normalize");
				}
			}
		}
		
		return ruleViolated;
	}

	/* (non-Javadoc)
	 * @see edu.csus.plugin.securecodingassistant.rules.IRule#getRuleText()
	 */
	@Override
	public String getRuleText() {
		// TODO Auto-generated method stub
		return "IDS01J";
	}

	@Override
	public String getRuleName() {
		return "IDS01-J. Normalize strings before validating them";
	}

	@Override
	public String getRuleRecommendation() {
		// TODO Auto-generated method stub
		return null;
	}

}
