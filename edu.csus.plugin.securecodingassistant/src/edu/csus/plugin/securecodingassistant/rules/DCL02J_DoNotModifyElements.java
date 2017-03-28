package edu.csus.plugin.securecodingassistant.rules;

import java.util.TreeMap;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * <b><i>The text and/or code below is from the CERT website: <a target="_blank" href="https://www.securecoding.cert.org">https://www.securecoding.cert.org</a></i></b>
 * <p>
 * Java Secure Coding Rule: DCL02-J. Do not modify the collection's elements during an
 * enhanced for statement
 * </p>
 * <p>
 * CERT Website: Unlike the basic for statement, assignments to the loop variable fail to affect the
 * loop's iteration order over the underlying set of objects. Consequently, an assignment
 * to the loop variable is equivalent to modifying a variable local to the loop body whose
 * initial value is the object referenced by the loop iterator. This modification is not
 * necessarily erroneous but can obscure the loop functionality or indicate a
 * misunderstanding of the underlying implementation of the enhanced for statement.
 * </p>
 * <p>
 * Declare all enhanced for statement loop variables final. The final declaration causes
 * Java compilers to flag and reject any assignments made to the loop variable.
 * </p>
 * @author Ben White (Plugin Logic), CERT (Rule Definition)
 * @see Java Secure Coding Rule defined by CERT: <a target="_blank" href="https://www.securecoding.cert.org/confluence/display/java/DCL02-J.+Do+not+modify+the+collection%27s+elements+during+an+enhanced+for+statement">DCL02-J</a>
 */
class DCL02J_DoNotModifyElements extends SecureCodingRule {
	
	@Override
	public boolean violated(ASTNode node) {
		
		boolean ruleViolated = false;
		
		if(node instanceof EnhancedForStatement) {
			
			EnhancedForStatement statement = (EnhancedForStatement)node;
			
			ruleViolated = true;
			SingleVariableDeclaration dec = statement.getParameter();
			// Look to see if they declared as final
			for (Object o : dec.modifiers()) {
				assert o instanceof Modifier;
				Modifier m = (Modifier) o;
				if (m.isFinal()) {
					ruleViolated = false;
					break;
				}
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
		return "CERT Website-Unlike the basic for statement, assignments to the loop "
				+ "variable fail to affect the loop's iteration order over the underlying "
				+ "set of objects. Consequently, an assignment to the loop variable is "
				+ "equivalent to modifying a variable local to the loop body whose initial"
				+ " value is the object referenced by the loop iterator. This modification"
				+ " is not necessarily erroneous but can obscure the loop functionality or"
				+ " indicate a misunderstanding of the underlying implementation of the "
				+ "enhanced for statement.";
	}

	@Override
	public String getRuleName() {
		return Globals.RuleNames.DCL02_J;
	}

	@Override
	public String getRuleRecommendation() {
		return "Declare all enhanced for statement loop variables final. The final "
				+ "declaration causes Java compilers to flag and reject any assignments"
				+ " made to the loop variable.";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_LOW;
	}

	@Override
	public TreeMap<String, ASTRewrite> getSolutions(ASTNode node) {

		TreeMap<String, ASTRewrite> map = new TreeMap<String, ASTRewrite>();
		map.putAll(super.getSolutions(node));
		try {
			AST ast = node.getAST();
			EnhancedForStatement statement = (EnhancedForStatement) node;

			// Solution 1 add "final" before variable
			ASTRewrite rewrite1 = ASTRewrite.create(ast);
			SingleVariableDeclaration oldSingleVariableDeclaration = statement.getParameter();
			ListRewrite listRewrite = rewrite1.getListRewrite(oldSingleVariableDeclaration,
					SingleVariableDeclaration.MODIFIERS2_PROPERTY);
			listRewrite.insertFirst(ast.newModifier(ModifierKeyword.FINAL_KEYWORD), null);

			map.put("Add final before variable", rewrite1);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
		return map;
	}

	@Override
	public String getRuleID() {
		return Globals.RuleID.DCL02_J;
	}
	

	@Override
	public String getRuleURL() {
		return "https://www.securecoding.cert.org/confluence/display/java/DCL02-J.+Do+not+modify+the+collection%27s+elements+during+an+enhanced+for+statement";
	}

}
