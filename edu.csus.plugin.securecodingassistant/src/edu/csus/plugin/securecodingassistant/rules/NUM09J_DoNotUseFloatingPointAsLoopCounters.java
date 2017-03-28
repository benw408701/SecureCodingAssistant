package edu.csus.plugin.securecodingassistant.rules;

import java.util.TreeMap;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * <b><i>The text and/or code below is from the CERT website: <a target="_blank" href="https://www.securecoding.cert.org">https://www.securecoding.cert.org</a></i></b>
 * <p>
 * Java Secure Coding Rule: NUM09-J. Do not use floating-point variables as loop counters
 * </p>
 * <p>
 * Floating point numbers are not an appropriate loop counter since floating point
 * arithmetic does not precisely represent decimal values
 * </p>
 * @author Ben White (Plugin Logic), CERT (Rule Definition)
 * @see Java Secure Coding Rule defined by CERT: <a target="_blank" href="https://www.securecoding.cert.org/confluence/display/java/NUM09-J.+Do+not+use+floating-point+variables+as+loop+counters">NUM09-J</a>
 */
class NUM09J_DoNotUseFloatingPointAsLoopCounters extends SecureCodingRule {

	@Override
	public boolean violated(ASTNode node) {
		boolean ruleViolated = false;
		// Is node a for loop?
		if (node instanceof ForStatement) {
			ForStatement statement = (ForStatement) node;
			for(Object o : statement.initializers()) {
				// Is the initializer a declaration?
				if (o instanceof VariableDeclarationExpression) {
					VariableDeclarationExpression dec = (VariableDeclarationExpression)o;
					if (dec.getType().resolveBinding().getName().equals("float")
							|| dec.getType().resolveBinding().getName().equals("Float")
							|| dec.getType().resolveBinding().getName().equals("double")
							|| dec.getType().resolveBinding().getName().equals("Double")) {
						ruleViolated = true;
						break;
					}
				}
			}
			
			if (ruleViolated)
				ruleViolated = super.violated(node);
		}
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		return "Floating point numbers are not an appropriate loop counter since floating "
				+ "point arithmetic does not precisely represent decimal values";
	}

	@Override
	public String getRuleName() {
		return Globals.RuleNames.NUM09_J;
	}

	@Override
	public String getRuleRecommendation() {
		return "Implement loop using a integer counter instead.";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_MEDIUM;
	}

	@Override
	public String getRuleID() {
		return Globals.RuleID.NUM09_J;
	}
	
	@Override
	public TreeMap<String, ASTRewrite> getSolutions(ASTNode node) {

		TreeMap<String, ASTRewrite> list = new TreeMap<>();
		list.putAll(super.getSolutions(node));

		try {
			AST ast = node.getAST();
			ASTRewrite rewrite = ASTRewrite.create(ast);

			ForStatement fs = (ForStatement) node;
			for (Object obj : fs.initializers()) {
				if (obj instanceof VariableDeclarationExpression) {
					VariableDeclarationExpression vde = (VariableDeclarationExpression) obj;
					if (vde.getType() != null && ("Double".equalsIgnoreCase(vde.getType().toString())
							|| "Float".equalsIgnoreCase(vde.getType().toString()))) {
						Type newType = ast.newPrimitiveType(PrimitiveType.INT);
						Type oldType = vde.getType();
						rewrite.replace(oldType, newType, null);
						list.put("Change type to int", rewrite);
					}
				}
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}

		return list;
	}

	@Override
	public String getRuleURL() {
		return "https://www.securecoding.cert.org/confluence/display/java/NUM09-J.+Do+not+use+floating-point+variables+as+loop+counters";
	}

}
