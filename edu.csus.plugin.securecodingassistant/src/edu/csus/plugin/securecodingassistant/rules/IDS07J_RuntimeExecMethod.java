package edu.csus.plugin.securecodingassistant.rules;

import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression.Operator;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * <b><i>The text and/or code below is from the CERT website:
 * <a target="_blank" href=
 * "https://www.securecoding.cert.org">https://www.securecoding.cert.org</a></i></b>
 * <p>
 * Java Secure Coding Rule: IDS07-J. Sanitize untrusted data passed to the
 * <code>Runtime.exec()</code> method
 * </p>
 * <p>
 * Any command that is sent to <code>Runtime.exec()</code> must be sanitized.
 * Rather than using <code>Runtime.exec()</code>, try some other alternatives.
 * </p>
 * 
 * @author Ben White (Plugin Logic), CERT (Rule Definition)
 * @see Java Secure Coding Rule defined by CERT: <a target="_blank" href=
 *      "https://www.securecoding.cert.org/confluence/display/java/IDS07-J.+Sanitize+untrusted+data+passed+to+the+Runtime.exec%28%29+method">IDS07-J</a>
 */
class IDS07J_RuntimeExecMethod extends SecureCodingRule {

	@Override
	public boolean violated(ASTNode node) {
		boolean ruleViolated = false;

		// Runtime.exec() would be a MethodInvocation
		if (node instanceof MethodInvocation) {
			MethodInvocation method = (MethodInvocation) node;
			ruleViolated = Utility.calledMethod(method, Runtime.class.getCanonicalName(), "exec");
			
			if (ruleViolated)
				ruleViolated = super.violated(node);
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
				+ " untrusted input and uses it in the execution of external " + "programs.";
	}

	@Override
	public String getRuleName() {
		return Globals.RuleNames.IDS07_J;
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
	public String getRuleID() {
		return Globals.RuleID.IDS07_J;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TreeMap<String, ASTRewrite> getSolutions(ASTNode node) {

		TreeMap<String, ASTRewrite> list = new TreeMap<>();
		list.putAll(super.getSolutions(node));
		try {
			AST ast = node.getAST();

			// solution: remove runtime.exec()
			ASTRewrite rewrite1 = ASTRewrite.create(ast);
			rewrite1.remove(node, null);
			list.put("Remove Runtime.exec()", rewrite1);

			// solution 2: sanitizes the untrusted user input
			ASTNode blockAST = Utility.getEnclosingNode(node, Block.class);
			ASTRewrite rewirte2 = ASTRewrite.create(ast);
			if (blockAST != null && blockAST instanceof Block) {
				Block block = (Block) blockAST;

				// find out the argument
				List<?> arguments = ((MethodInvocation) node).arguments();
				if (arguments != null && arguments.get(0) instanceof ASTNode) {
					ASTNode argument = (ASTNode) arguments.get(0);
					SimpleNameVisitor smVisitor = new SimpleNameVisitor();
					argument.accept(smVisitor);
					HashSet<SimpleName> names = smVisitor.getSimpleNames();
					if (!names.isEmpty()) {
						for (SimpleName sn : names) {
							if (Character.isUpperCase(sn.toString().charAt(0)))
								continue;
							MethodInvocation mi = ast.newMethodInvocation();
							mi.setName(ast.newSimpleName("matches"));
							mi.setExpression(ast.newSimpleName("Pattern"));
							mi.arguments().add(ast.newSimpleName("regex"));
							mi.arguments().add(ast.newSimpleName(sn.getIdentifier()));
							PrefixExpression pfexp = ast.newPrefixExpression();
							pfexp.setOperator(Operator.NOT);
							pfexp.setOperand(mi);
							IfStatement ifstmt = ast.newIfStatement();
							ifstmt.setExpression(pfexp);
							ListRewrite listRewrite = rewirte2.getListRewrite(block, Block.STATEMENTS_PROPERTY);
							listRewrite.insertBefore(ifstmt, SecureCodingNodeVisitor.getStatement(sn), null);
						}

					}
				}
				list.put("Sanitizes the untrusted user input", rewirte2);
			}

		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
		return list;
	}

	private class SimpleNameVisitor extends ASTVisitor {
		private HashSet<SimpleName> name;
		
		public SimpleNameVisitor() {
			name = new HashSet<>();
		}
		
		public boolean visit(SimpleName node) {
			name.add(node);
			return super.visit(node);
		}
		public HashSet<SimpleName> getSimpleNames() {
			return name;
		}
	}
}
