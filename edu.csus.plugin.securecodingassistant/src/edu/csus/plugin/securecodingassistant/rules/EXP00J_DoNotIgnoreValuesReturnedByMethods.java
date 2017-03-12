package edu.csus.plugin.securecodingassistant.rules;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import edu.csus.plugin.securecodingassistant.Globals;

/**
 * <b><i>The text and/or code below is from the CERT website: <a target="_blank" href="https://www.securecoding.cert.org">https://www.securecoding.cert.org</a></i></b>
 * <p>
 * Java Secure Coding Rule: EXP00-J. Do not ignore values returned by methods
 * </p>
 * <p>
 * CERT Website: Methods can return values to communicate failure or success
 * or to update local objects or fields. Security risks can arise when method
 * return values are ignored or when the invoking method fails to take suitable
 * action. Consequently, programs must not ignore method return values.
 * </p>
 * <p>
 * When getter methods are named after an action, a programmer could fail to
 * realize that a return value is expected. For example, the only purpose of
 * the <code>ProcessBuilder.redirectErrorStream()</code> method is to report
 * via return value whether the process builder successfully merged standard
 * error and standard output. The method that actually performs redirection
 * of the error stream is the overloaded single-argument method
 * <code>ProcessBuilder.redirectErrorStream(boolean)</code>.
 * </p>
 * @author Ben White (Plugin Logic), CERT (Rule Definition)
 * @see Java Secure Coding Rule defined by CERT: <a target="_blank" href="https://www.securecoding.cert.org/confluence/display/java/EXP00-J.+Do+not+ignore+values+returned+by+methods">EXP00-J</a>
 */
class EXP00J_DoNotIgnoreValuesReturnedByMethods implements IRule {

	@Override
	public boolean violated(ASTNode node) {
		boolean ruleViolated = false;

		// Is the node a method invocation?
		if (node instanceof MethodInvocation) {
			MethodInvocation method = (MethodInvocation)node;
			ITypeBinding returnType = method.resolveMethodBinding() == null ? null : method.resolveMethodBinding().getReturnType();
			// Does it have a return type?
			if (returnType != null && !returnType.getQualifiedName().equals("void")) {
				ASTNode parent = method.getParent();
				// Was the return type used?
				// (If ExpressionStatement then it was not used
				ruleViolated = parent instanceof ExpressionStatement;
			}
		}
		
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		return "CERT Website-Methods can return values to communicate failure or "
				+ "success or to update local objects or fields. Security risks can "
				+ "arise when method return values are ignored or when the invoking "
				+ "method fails to take suitable action. Consequently, programs must "
				+ "not ignore method return values.";
	}

	@Override
	public String getRuleName() {
		return "EXP00-J. Do not ignore values returned by methods";
	}

	@Override
	public String getRuleRecommendation() {
		return "Capture the return value of the method call";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_MEDIUM;
	}

	@Override
	public String getRuleURL() {
		return "https://www.securecoding.cert.org/confluence/display/java/EXP00-J.+Do+not+ignore+values+returned+by+methods";
	}

}
