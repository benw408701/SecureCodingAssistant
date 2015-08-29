package edu.csus.plugin.securecodingassistant.rules;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodInvocation;

/**
 * Java Secure Coding Rule: IDS07-J. Sanitize untrusted data passed
 * to the <code>Runtime.exec()</code> method
 * <p>
 * Any command that is sent to <code>Runtime.exec()</code> must be sanitized.
 * Rather than using <code>Runtime.exec()</code>, try some other alternatives.
 * </p>
 * 
 * @author Ben White
 * @see <a href="https://www.securecoding.cert.org/confluence/display/java/IDS07-J.+Sanitize+untrusted+data+passed+to+the+Runtime.exec%28%29+method">
 * 		Java Secure Coding Rule: IDS07-J</a>
 *
 */
public class IDS07J_RuntimeExecMethod implements IRule {

	@Override
	public boolean violated(ASTNode node) {
		boolean ruleViolated = false;
		
		// Runtime.exec() would be a MethodInvocation
		if(node instanceof MethodInvocation) {
			MethodInvocation method = (MethodInvocation)node;
			String className = method.resolveMethodBinding().getDeclaringClass().getName().toString();
			String methodName = method.getName().toString();
			System.err.printf("Found method %s for class %s%n", methodName, className);
			if (className.equals("Runtime") && methodName.equals("exec"))
				ruleViolated = true;
		}
		
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		return String.format("Java Secure Coding Rule: IDS07-J. Sanitize untrusted data passed"
				+ " to the Runtime.exec() method%n%n"
				+ "External programs are commonly invoked to perform a function "
				+ "required by the overall system. This practice is a form of "
				+ "reuse and might even be considered a crude form of component"
				+ "-based software engineering. Command and argument injection"
				+ " vulnerabilities occur when an application fails to sanitize"
				+ " untrusted input and uses it in the execution of external "
				+ "programs.");
	}

	@Override
	public String getRuleName() {
		// TODO Auto-generated method stub
		return "IDS07-J. Sanitize untrusted data passed to the Runtime.exec() method";
	}
}
