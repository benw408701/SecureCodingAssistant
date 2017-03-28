package edu.csus.plugin.securecodingassistant.rules;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Assignment.Operator;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * <b><i>The text and/or code below is from the CERT website: <a target="_blank" href="https://www.securecoding.cert.org">https://www.securecoding.cert.org</a></i></b>
 * <p>
 * Java Secure Coding Rule: IDS01-J. Normalize strings before validating
 * them
 * </p>
 * <p>
 * CERT Website: Many applications that accept untrusted input strings employ input filtering
 * and validation mechanisms based on the strings' character data. For example,
 * an application's strategy for avoiding cross-site scripting (XSS)
 * vulnerabilities may include forbidding {@code <script>} tags in
 * inputs. Such blacklisting mechanisms are a useful part of a security
 * strategy, even though they are insufficient for complete input validation
 * and sanitization.
 * </p>
 * @author Ben White (Plugin Logic), CERT (Rule Definition)
 * @see Java Secure Coding Rule defined by CERT: <a target="_blank" href="https://www.securecoding.cert.org/confluence/display/java/IDS01-J.+Normalize+strings+before+validating+them">IDS01-J</a>
 *
 */
class IDS01J_NormalizeStringsBeforeValidating extends SecureCodingRule {

	@Override
	public boolean violated(ASTNode node) {
		boolean ruleViolated = false;
		
		// Check to see if Normalizer.normalize was called. Rule is violated if
		// Pattern.matcher was called beforehand with the same argument
		if(node instanceof MethodInvocation) {
			MethodInvocation method = (MethodInvocation)node;
			if(Utility.calledMethod(method, Normalizer.class.getCanonicalName(), "normalize")) {
				SimpleName argument = null;
				if (method.arguments().get(0) instanceof SimpleName)
					argument = (SimpleName)method.arguments().get(0);
				ruleViolated = Utility.calledPrior(method, Pattern.class.getCanonicalName(), "matcher", argument);
			}
			if (ruleViolated)
				ruleViolated = super.violated(node);
		}
		
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		return "CERT Website-Many applications that accept untrusted input strings employ input filtering "
				+ "and validation mechanisms based on the strings' character data. For example,"
				+ " an application's strategy for avoiding cross-site scripting (XSS) "
				+ "vulnerabilities may include forbidding <script> tags in inputs. Such "
				+ "blacklisting mechanisms are a useful part of a security strategy, even though"
				+ " they are insufficient for complete input validation and sanitization.";
	}

	@Override
	public String getRuleName() {
		return Globals.RuleNames.IDS01_J;
	}

	@Override
	public String getRuleRecommendation() {
		return "Using Pattern.matcher() is a great way detect harmful things like angle brackets "
				+ "which could indicate script tags, but the normalize method MUST be called "
				+ "before running Pattern.matcher()";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_HIGH;
	}

	@Override
	public String getRuleID() {
		return Globals.RuleID.IDS01_J;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TreeMap<String, ASTRewrite> getSolutions(ASTNode node) {

		TreeMap<String, ASTRewrite> list = new TreeMap<>();
		list.putAll(super.getSolutions(node));

		try {
			// get ast and declare rewrite
			AST ast = node.getAST();
			ASTRewrite rewrite = ASTRewrite.create(ast);

			// get the enclosing block
			ASTNode blockAST = Utility.getEnclosingNode(node, Block.class);
			if (blockAST != null && blockAST instanceof Block) {
				Block block = (Block) blockAST;

				SecureCodingNodeVisitor visitor = new SecureCodingNodeVisitor();
				block.accept(visitor);

				// find the methodInvocations: s = Normalizer.normalize(s,
				// Form.NFKC)
				ArrayList<String> arguments = new ArrayList<>();
				arguments.add(String.class.getCanonicalName());
				arguments.add(Normalizer.Form.NFKC.getClass().getCanonicalName());
				ArrayList<MethodInvocation> normalizeMIs = visitor.getMethodInvocations("normalize",
						Normalizer.class.getCanonicalName(), 2, arguments);

				// create map to map argument name to methodInvocations
				HashMap<String, MethodInvocation> arguToMI = new HashMap<>();
				for (MethodInvocation mi : normalizeMIs) {
					arguToMI.put(mi.arguments().get(0).toString(), mi);
				}

				// find the method invocations: Matcher matcher =
				// pattern.matcher(s);
				ArrayList<String> matcherArgu = new ArrayList<>();
				matcherArgu.add(String.class.getCanonicalName());
				ArrayList<MethodInvocation> matchMIs = visitor.getMethodInvocations("matcher",
						Pattern.class.getCanonicalName(), 1, matcherArgu);

				// find all normalizeMI after matchMI
				for (MethodInvocation matchMI : matchMIs) {
					String arg = matchMI.arguments().get(0).toString();
					int position = matchMI.getStartPosition();
					MethodInvocation normalizeMI = arguToMI.get(arg);

					// no normalize MI with this arg or this MI is after matchMI
					if (normalizeMI == null || normalizeMI.getStartPosition() > position) {
						if (normalizeMI != null) {
							rewrite.remove(SecureCodingNodeVisitor.getStatement(normalizeMI), null);
						}
						MethodInvocation newNormalizeMI = ast.newMethodInvocation();
						newNormalizeMI.setName(ast.newSimpleName("normalize"));
						newNormalizeMI.setExpression(ast.newSimpleName("Normalizer"));
						newNormalizeMI.arguments().add(ast.newSimpleName(arg));
						newNormalizeMI.arguments()
								.add(ast.newQualifiedName(ast.newName("Form"), ast.newSimpleName("NFKC")));
						Assignment assign = ast.newAssignment();
						assign.setLeftHandSide(ast.newSimpleName(arg));
						assign.setOperator(Operator.ASSIGN);
						assign.setRightHandSide(newNormalizeMI);
						ExpressionStatement expStmt = ast.newExpressionStatement(assign);
						ListRewrite listRewrite = rewrite.getListRewrite(block, Block.STATEMENTS_PROPERTY);
						listRewrite.insertBefore(expStmt, SecureCodingNodeVisitor.getStatement(matchMI), null);
					}
				}
				list.put("Add Normalizer.normalize before Pattern.matcher", rewrite);
			}

		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
		return list;
	}

	@Override
	public String getRuleURL() {
		return "https://www.securecoding.cert.org/confluence/display/java/IDS01-J.+Normalize+strings+before+validating+them";
	}

}
