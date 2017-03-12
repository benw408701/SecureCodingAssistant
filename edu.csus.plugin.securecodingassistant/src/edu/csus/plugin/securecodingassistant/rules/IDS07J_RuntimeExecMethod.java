package edu.csus.plugin.securecodingassistant.rules;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodInvocation;
import edu.csus.plugin.securecodingassistant.Globals;

/**
 * <b><i>The text and/or code below is from the CERT website: <a target="_blank" href="https://www.securecoding.cert.org">https://www.securecoding.cert.org</a></i></b>
 * <p>
 * Java Secure Coding Rule: IDS07-J. Sanitize untrusted data passed
 * to the <code>Runtime.exec()</code> method
 * </p>
 * <p>
 * Any command that is sent to <code>Runtime.exec()</code> must be sanitized.
 * Rather than using <code>Runtime.exec()</code>, try some other alternatives.
 * </p>
 * @author Ben White (Plugin Logic), CERT (Rule Definition)
 * @see Java Secure Coding Rule defined by CERT: <a target="_blank" href="https://www.securecoding.cert.org/confluence/display/java/IDS07-J.+Sanitize+untrusted+data+passed+to+the+Runtime.exec%28%29+method">IDS07-J</a>
 */
class IDS07J_RuntimeExecMethod implements IRule {

	@Override
	public boolean violated(ASTNode node) {
		boolean ruleViolated = false;
		
		// Runtime.exec() would be a MethodInvocation
		if(node instanceof MethodInvocation) {
			MethodInvocation method = (MethodInvocation)node;
			ruleViolated = Utility.calledMethod(method, Runtime.class.getCanonicalName(), "exec");
		}
		
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		return "CERT Website-External programs are commonly invoked to perform a function "
				+ "required by the overall system. This practice is a form of "
				+ "reuse and might even be considered a crude form of component"
				+ "-based software engineering. Command and argument injection"
				+ " vulnerabilities occur when an application fails to sanitize"
				+ " untrusted input and uses it in the execution of external "
				+ "programs.";
	}

	@Override
	public String getRuleName() {
		return "IDS07-J. Sanitize untrusted data passed to the Runtime.exec() method";
	}

	@Override
	public String getRuleRecommendation() {
		return "Avoid using Runtime.exec()";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_HIGH;
	}

	@Override
	public String getRuleURL() {
		return "https://www.securecoding.cert.org/confluence/display/java/IDS07-J.+Sanitize+untrusted+data+passed+to+the+Runtime.exec%28%29+method";
	}
}
