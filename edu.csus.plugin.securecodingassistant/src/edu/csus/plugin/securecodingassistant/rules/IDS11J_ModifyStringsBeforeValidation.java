package edu.csus.plugin.securecodingassistant.rules;

import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * <b><i>The text and/or code below is from the CERT website: <a target="_blank" href="https://www.securecoding.cert.org">https://www.securecoding.cert.org</a></i></b>
 * <p>
 * Java Secure Coding Rule: IDS11-J. Perform any string modifications before validation
 * </p>
 * <p>
 * CERT Website: It is important that a string not be modified after validation has occurred because doing 
 * so may allow an attacker to bypass validation. For example, a program may filter out the 
 * {@code <script>} tags from HTML input to avoid cross-site scripting (XSS) and other
 * vulnerabilities. If exclamation marks (!) are deleted from the input following validation,
 * an attacker may pass the string "{@code <scr!ipt>}" so that the validation check fails to detect the
 * {@code <script>} tag, but the subsequent removal of the exclamation mark creates a
 * {@code <script>} tag in the input.
 * </p>  
 * @author Ben White (Plugin Logic), CERT (Rule Definition)
 * @see Java Secure Coding Rule defined by CERT: <a target="_blank" href="https://www.securecoding.cert.org/confluence/display/java/IDS11-J.+Perform+any+string+modifications+before+validation">IDS11-J</a>
 */
class IDS11J_ModifyStringsBeforeValidation extends SecureCodingRule {

	@Override
	public boolean violated(ASTNode node) {
		boolean ruleViolated = false;
		
		// Check to see if Pattern.matcher was used and if it was make sure the string
		// wasn't modified afterwards
		if(node instanceof MethodInvocation &&
				Utility.calledMethod((MethodInvocation)node, Pattern.class.getCanonicalName(), "matcher")) {
			MethodInvocation method = (MethodInvocation)node;
			SimpleName str = (SimpleName)method.arguments().get(0);
			
			ruleViolated = Utility.modifiedAfter(method, str);
			
			if (ruleViolated)
				ruleViolated = super.violated(node);
		}
		
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		return "CERT Website-It is important that a string not be modified after validation has occurred because "
				+ "doing so may allow an attacker to bypass validation. For example, a program may "
				+ "filter out the <script> tags from HTML input to avoid cross-site scripting (XSS) "
				+ "and other vulnerabilities. If exclamation marks (!) are deleted from the input "
				+ "following validation, an attacker may pass the string \"<scr!ipt>\" so that the "
				+ "validation check fails to detect the <script> tag, but the subsequent removal of "
				+ "the exclamation mark creates a <script> tag in the input.";
	}

	@Override
	public String getRuleName() {
		return Globals.RuleNames.IDS11_J;
	}

	@Override
	public String getRuleRecommendation() {
		return "The Pattern.matcher() method should be called after all modifications to the string."
				+ " Move any string modifications so that they occur prior to Pattern.matcher().";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_HIGH;
	}

	@Override
	public String getRuleID() {
		return Globals.RuleID.IDS11_J;
	}

	@Override
	public TreeMap<String, ASTRewrite> getSolutions(ASTNode node){
		if (!violated(node))
			throw new IllegalArgumentException("Doesn't violate rule " + getRuleID());
		
		TreeMap<String, ASTRewrite> list = new TreeMap<>();
		AST ast = node.getAST();
		
		ASTRewrite rewrite = ASTRewrite.create(ast);
		
		//find the argument's simple name
		MethodInvocation mi = (MethodInvocation) node;
		List<?> arguments = mi.arguments();
		
		if (arguments != null && mi.arguments().get(0) instanceof SimpleName) {
			String name = mi.arguments().get(0).toString();
			
			//find whether there is assignment of with this simple name
			ASTNode blockNode = Utility.getEnclosingNode(mi, Block.class);
			if (blockNode != null && blockNode instanceof Block) {
				Block block = (Block)blockNode;
				SecureCodingNodeVisitor visitor = new SecureCodingNodeVisitor();
				block.accept(visitor);
				
				HashSet<Assignment> assigns = visitor.getAssignement(name);
				ListRewrite listRewrite = rewrite.getListRewrite(block, Block.STATEMENTS_PROPERTY);
				for (Assignment assign: assigns) {
					// if assignment is after patter.matches, move it before it. 
					if (assign.getStartPosition() > mi.getStartPosition()) {
						Statement stmt_assign = SecureCodingNodeVisitor.getStatement(assign);
						Statement stmt_mi = SecureCodingNodeVisitor.getStatement(mi);
						Statement stmt_assign_copy = (Statement) rewrite.createCopyTarget(stmt_assign);
						rewrite.remove(stmt_assign, null);
						listRewrite.insertBefore(stmt_assign_copy, stmt_mi, null);
					}
				}
				list.put("Move modification before calling Pattern.matcher() method", rewrite);
			}
		}
		list.putAll(super.getSolutions(node));
		return list;
	}

}
