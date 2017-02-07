package edu.csus.plugin.securecodingassistant.rules;

import java.util.TreeMap;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

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
class EXP00J_DoNotIgnoreValuesReturnedByMethods extends SecureCodingRule {

	@Override
	public boolean violated(ASTNode node) {
		boolean ruleViolated = false;

		// Is the node a method invocation?
		if (node instanceof MethodInvocation) {
			MethodInvocation method = (MethodInvocation) node;
			ITypeBinding returnType = method.resolveMethodBinding() == null ? null
					: method.resolveMethodBinding().getReturnType();
			// Does it have a return type?
			if (returnType != null && !returnType.getQualifiedName().equals("void")) {
				ASTNode parent = method.getParent();
				// Was the return type used?
				// (If ExpressionStatement then it was not used
				ruleViolated = parent instanceof ExpressionStatement;
			}
			
			//if node violates rule, check whether method contains skip rule check
			if (ruleViolated) {
				ruleViolated = super.violated(node);
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
		return Globals.RuleNames.EXP00_J;
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
	public TreeMap<String, ASTRewrite> getSolutions(ASTNode node) {
		TreeMap<String, ASTRewrite> map = new TreeMap<String, ASTRewrite>();
		map.putAll(super.getSolutions(node));

		try {
			AST ast = node.getAST();
			ASTRewrite rewrite = ASTRewrite.create(ast);

			MethodInvocation methodInvocation = (MethodInvocation) node;
			Type type = null;
			if (methodInvocation.resolveMethodBinding().getReturnType().isPrimitive()) {
				type = ast.newPrimitiveType(
						PrimitiveType.toCode(methodInvocation.resolveMethodBinding().getReturnType().getName()));
			} else {
				type = ast
						.newSimpleType(ast.newName(methodInvocation.resolveMethodBinding().getReturnType().getName()));
			}

			SingleVariableDeclaration newVariableDeclaration = ast.newSingleVariableDeclaration();
			newVariableDeclaration.setName(ast.newSimpleName("returnValue"));
			newVariableDeclaration.setType(type);
			newVariableDeclaration.setInitializer((Expression) rewrite.createCopyTarget(methodInvocation));

			rewrite.replace(methodInvocation, newVariableDeclaration, null);

			map.put("Store return value in variable returnValue", rewrite);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
		return map;
	}

	@Override
	public String getRuleID() {
		return Globals.RuleID.EXP00_J;
	}

}
