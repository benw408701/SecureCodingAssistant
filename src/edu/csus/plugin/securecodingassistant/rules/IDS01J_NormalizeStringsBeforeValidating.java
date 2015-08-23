/**
 * 
 */
package edu.csus.plugin.securecodingassistant.rules;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Statement;

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
 * 		Java Secure Coding Rule: IDS07-J</a>
 *
 */
public class IDS01J_NormalizeStringsBeforeValidating implements IRule {

	/* (non-Javadoc)
	 * @see edu.csus.plugin.securecodingassistant.rules.IRule#violated(org.eclipse.jdt.core.dom.ASTNode)
	 */
	@Override
	public boolean violated(ASTNode node) {
		boolean ruleViolated = false;
		boolean patternMatcher = false;
		
		// Check to see if Pattern.matcher is called
		if(node instanceof MethodInvocation) {
			MethodInvocation method = (MethodInvocation)node;
			String className = method.resolveMethodBinding().getDeclaringClass().getName().toString();
			String methodName = method.getName().toString();
			System.out.printf("Found method %s for class %s%n", methodName, className);
			if (className.equals("Pattern") && methodName.equals("matcher"))
				patternMatcher = true;
		}
		
		// Pattern.matcher was called, look to see if Normalizer.normalize
		// was called beforehand
		if(patternMatcher) {
			ASTNode parent = node.getParent();

			// Go to block level
			while(!(parent instanceof Block)) {
				parent = node.getParent();
			}
			
			
			if (parent instanceof Block) {
				Block block = (Block)parent;
				List<?> statements = block.statements();
				
				for (Object statement : statements) {
					if (statement instanceof Statement) {
						;
					}
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

}
