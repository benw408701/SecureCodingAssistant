package edu.csus.plugin.securecodingassistant.rules;

import java.util.TreeMap;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * <b><i>The text and/or code below is from the CERT website: <a target="_blank" href="https://www.securecoding.cert.org">https://www.securecoding.cert.org</a></i></b>
 * <p>
 * Java Secure Coding Rule: ENV02-J. Do not trust the values of environment variables
 * </p>
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
 * @author Ben White (Plugin Logic), CERT (Rule Definition)
 * @see Java Secure Coding Rule defined by CERT: <a target="_blank" href="https://www.securecoding.cert.org/confluence/display/java/ENV02-J.+Do+not+trust+the+values+of+environment+variables">ENV02-J</a>
 */
class ENV02J_DoNotTurstTheValuesOfEnvironmentVariables extends SecureCodingRule {
	
	@Override
	public boolean violated(ASTNode node) {
		boolean ruleViolated = false;
		
		// Is node a method invocation?
		if(node instanceof MethodInvocation) {
			// Was System.getenv() called?
			ruleViolated = Utility.calledMethod((MethodInvocation) node,
					System.class.getCanonicalName(), "getenv");
			
			//if node violates rule, check whether method contains skip rule check
			if (ruleViolated) {
				ruleViolated = super.violated(node);
			}
		}

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
		return Globals.RuleNames.ENV02_J;
	}

	@Override
	public String getRuleRecommendation() {
		return "Avoid calls to the System.getenv() command";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_MEDIUM;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TreeMap<String, ASTRewrite> getSolutions(ASTNode node) {
		
		TreeMap<String, ASTRewrite> map = new TreeMap<>();
		map.putAll(super.getSolutions(node));
		
		try {

			AST ast = node.getAST();
			ASTRewrite rewrite = ASTRewrite.create(ast);
			MethodInvocation oldMethodInvocation = (MethodInvocation) node;
			MethodInvocation newMethodInvocation = ast.newMethodInvocation();
			SimpleName name = ast.newSimpleName("System");
			newMethodInvocation.setExpression(name);
			newMethodInvocation.setName(ast.newSimpleName("getProperty"));
			StringLiteral sl = ast.newStringLiteral();
			sl.setLiteralValue("user.name");
			newMethodInvocation.arguments().add(sl);

			rewrite.replace(oldMethodInvocation, newMethodInvocation, null);

			map.put("Change to System.getProperty(\"user.name\")", rewrite);
			
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
		return map;
	}

	@Override
	public String getRuleID() {
		return Globals.RuleID.ENV02_J;
	}

	
}
