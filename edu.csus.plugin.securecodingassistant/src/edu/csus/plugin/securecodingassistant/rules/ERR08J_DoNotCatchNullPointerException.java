package edu.csus.plugin.securecodingassistant.rules;

import java.util.TreeMap;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * <b><i>The text and/or code below is from the CERT website: <a target="_blank" href="https://www.securecoding.cert.org">https://www.securecoding.cert.org</a></i></b>
 * <p>
 * Java Secure Coding Rule: ERR08-J. Do not catch NullPointerException or any of its
 * ancestors
 * </p>
 * <p>
 * CERT Webiste: Programs must not catch <code>java.lang.NullPointerException</code>.
 * A <code>NullPointerException</code> exception thrown at runtime indicates the existence
 * of an underlying null pointer dereference that must be fixed in the application
 * code (see EXP01-J. Do not use a null in a case where an object is required for more
 * information). Handling the underlying null pointer dereference by catching the
 * <code>NullPointerException</code> rather than fixing the underlying problem is
 * inappropriate for several reasons. First, catching <code>NullPointerException</code>
 * adds significantly more performance overhead than simply adding the necessary null
 * checks [Bloch 2008]. Second, when multiple expressions in a try block are capable of
 * throwing a <code>NullPointerException</code>, it is difficult or impossible to
 * determine which expression is responsible for the exception because the
 * <code>NullPointerException</code> catch block handles any <code>NullPointerException</code>
 * thrown from any location in the try block. Third, programs rarely remain in an expected
 * and usable state after a <code>NullPointerException</code> has been thrown. Attempts
 * to continue execution after first catching and logging (or worse, suppressing) the
 * exception rarely succeed.
 * </p>
 * @author Ben White (Plugin Logic), CERT (Rule Definition)
 * @see Java Secure Coding Rule defined by CERT: <a target="_blank" href="https://www.securecoding.cert.org/confluence/display/java/ERR08-J.+Do+not+catch+NullPointerException+or+any+of+its+ancestors">ERR08-J</a>
 */
class ERR08J_DoNotCatchNullPointerException extends SecureCodingRule {

	@Override
	public boolean violated(ASTNode node) {
		boolean ruleViolated = false;
		
		if (node instanceof CatchClause) {
			// Get the exception type being caught
			SingleVariableDeclaration exception = ((CatchClause)node).getException();
			String exceptionType = exception.resolveBinding().getType().getQualifiedName();
			// Is it NullPointerException or one of its ancestors?
			ruleViolated = exceptionType.equals(NullPointerException.class.getCanonicalName())
					|| exceptionType.equals(RuntimeException.class.getCanonicalName())
					|| exceptionType.equals(Exception.class.getCanonicalName())
					|| exceptionType.equals(Throwable.class.getCanonicalName());
			
			//if node violates rule, check whether method contains skip rule check
			if (ruleViolated) {
				ruleViolated = super.violated(node);
			}
		}
		
		return ruleViolated;
	}

	
	@Override
	public String getRuleText() {
		return "Catching a null pointer exception (or any of its ancestors) increases "
				+ "performance overhead, are difficult to troubleshoot and programs "
				+ "rarely remain in a usable state after they are thrown.";
	}

	@Override
	public String getRuleName() {
		return Globals.RuleNames.ERR08_J;
	}

	@Override
	public String getRuleRecommendation() {
		return "Test for null before dereferencing and permit the exception to be "
				+ "thrown.";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_HIGH;
	}

	@Override
	public TreeMap<String, ASTRewrite> getSolutions(ASTNode node) {
		TreeMap<String, ASTRewrite> list = new TreeMap<String, ASTRewrite>();
		list.putAll(super.getSolutions(node));

		try {
			AST ast = node.getAST();
			ASTRewrite rewrite = ASTRewrite.create(ast);
			rewrite.remove(node, null);

			list.put("Remove Catch Clause", rewrite);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
		return list;
	}


	@Override
	public String getRuleID() {
		return Globals.RuleID.ERR08_J;
	}

	@Override
	public String getRuleURL() {
		return "https://www.securecoding.cert.org/confluence/display/java/ERR08-J.+Do+not+catch+NullPointerException+or+any+of+its+ancestors";
	}

}
