package edu.csus.plugin.securecodingassistant.rules;

import java.util.TreeMap;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * <b><i>The text and/or code below is from the CERT website: <a target="_blank" href="https://www.securecoding.cert.org">https://www.securecoding.cert.org</a></i></b>
 * <p>
 * Java Secure Coding Rule: NUM07-J. Do not attempt comparisons with NaN
 * </p>
 * <p>
 * CERT Website: NaN (not-a-number) is unordered, so the numerical comparison
 * operators <, <=, >, and >= return false if either or both operands are NaN.
 * The equality operator == returns false if either operand is NaN, and the
 * inequality operator != returns true if either operand is NaN.
 * </p>
 * @author Ben White (Plugin Logic), CERT (Rule Definition)
 * @see Java Secure Coding Rule defined by CERT: <a target="_blank" href="https://www.securecoding.cert.org/confluence/display/java/NUM07-J.+Do+not+attempt+comparisons+with+NaN">NUM07-J</a>
 */
class NUM07J_DoNotAttemptComparisonsWithNaN extends SecureCodingRule {

	@Override
	public boolean violated(ASTNode node) {
		boolean ruleViolated = false;
		
		// Is the node an infix boolean expression?
		if(node instanceof InfixExpression) {
			InfixExpression expression = (InfixExpression)node;
			if(expression.resolveTypeBinding() != null &&
					expression.resolveTypeBinding().getName().equals("boolean")) {
				// Get left-hand-side and right-hand-side
				Expression lhs = expression.getLeftOperand(), rhs = expression.getRightOperand();
				if (lhs instanceof QualifiedName)
					ruleViolated = isNaN((QualifiedName)lhs);
				if (rhs instanceof QualifiedName)
					ruleViolated = ruleViolated || isNaN((QualifiedName)rhs);
			}
			if (ruleViolated)
				ruleViolated = super.violated(node);
		}
		
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		return "NaN (not-a-number) is unordered, so the numerical comparison operators "
				+ "<, <=, >, and >= return false if either or both operands are NaN. "
				+ "The equality operator == returns false if either operand is NaN, and "
				+ "the inequality operator != returns true if either operand is NaN.";
	}

	@Override
	public String getRuleName() {
		return Globals.RuleNames.NUM07_J;
	}

	@Override
	public String getRuleRecommendation() {
		return "Use Double.isNaN() or Float.isNaN() instead"; 
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_LOW;
	}

	@Override
	public String getRuleID() {
		return Globals.RuleID.NUM07_J;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TreeMap<String, ASTRewrite> getSolutions(ASTNode node) {

		TreeMap<String, ASTRewrite> list = new TreeMap<>();
		list.putAll(super.getSolutions(node));
		try {
			AST ast = node.getAST();
			ASTRewrite rewrite = ASTRewrite.create(ast);

			// Double.isNaN(result)
			MethodInvocation newMI = ast.newMethodInvocation();
			newMI.setName(ast.newSimpleName("isNaN"));

			InfixExpression ife = (InfixExpression) node;
			Expression leftExp = ife.getLeftOperand();
			Expression rightExp = ife.getRightOperand();
			if (leftExp instanceof QualifiedName && isNaN((QualifiedName) leftExp)) {
				newMI.setExpression(
						ast.newSimpleName(((QualifiedName) leftExp).getQualifier().getFullyQualifiedName()));
				newMI.arguments().add(rewrite.createCopyTarget(rightExp));
			} else {
				newMI.setExpression(
						ast.newSimpleName(((QualifiedName) rightExp).getQualifier().getFullyQualifiedName()));
				newMI.arguments().add(rewrite.createCopyTarget(leftExp));
			}
			rewrite.replace(ife, newMI, null);

			list.put("Use .isNaN instead", rewrite);

		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}

		return list;
	}

	private boolean isNaN(QualifiedName name) {
		return (name.getQualifier().getFullyQualifiedName().equals("Float")
							|| name.getQualifier().getFullyQualifiedName().equals("Double"))
				&& name.getName().getFullyQualifiedName().equals("NaN");
	}
	@Override
	public String getRuleURL() {
		return "https://www.securecoding.cert.org/confluence/display/java/NUM07-J.+Do+not+attempt+comparisons+with+NaN";
	}

}
