package edu.csus.plugin.securecodingassistant.rules;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodInvocation;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * Java Secure Coding Rule: ENV02-J. Do not trust the values of environment variables
 * <p>
 * CERT Website: Programs that execute in a more trusted domain than their environment must
 * assume that the values of environment variables are untrusted and must sanitize and
 * validate any environment variable values before use.
 * </p>
 * <p>
 * The default values of system properties are set by the Java Virtual Machine (JVM) upon
 * startup and can be considered trusted. However, they may be overridden by properties
 * from untrusted sources, such as a configuration file. System properties from untrusted
 * sources must be sanitized and validated before use.
 * </p>
 * <p>
 * Actually, relying on environment variables is more than a portability issue. An attacker
 * can essentially control all environment variables that enter a program using a mechanism
 * such as the <code>java.lang.ProcessBuilder</code> class.
 * </p>
 * <p>
 * Consequently, when an environment variable contains information that is available by
 * other means, including system properties, that environment variable must not be used.
 * Finally, environment variables must not be used without appropriate validation.
 * </p>
 * @author Ben White
 * @see <a target="_blank" href="https://www.securecoding.cert.org/confluence/display/java/ENV02-J.+Do+not+trust+the+values+of+environment+variables">Java Secure Coding Rule: ENV02-J</a>
 */
class ENV02J_DoNotTurstTheValuesOfEnvironmentVariables implements IRule {

	@Override
	public boolean violated(ASTNode node) {
		boolean ruleViolated = false;
		
		// Is node a method invocation?
		if(node instanceof MethodInvocation)
			// Was System.getenv() called?
			ruleViolated = Utility.calledMethod((MethodInvocation) node,
					System.class.getCanonicalName(), "getenv");

		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		return "CERT Website-Programs that execute in a more trusted domain than their "
				+ "environment must assume that the values of environment variables are "
				+ "untrusted and must sanitize and validate any environment variable values "
				+ "before use.";
	}

	@Override
	public String getRuleName() {
		return "ENV02-J. Do not trust the values of environment variables";
	}

	@Override
	public String getRuleRecommendation() {
		return "Avoid calls to the System.getenv() command";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_MEDIUM;
	}

}
