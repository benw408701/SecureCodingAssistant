package edu.csus.plugin.securecodingassistant.rules;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * Java Secure Coding Rule: ERR08-J. Do not catch NullPointerException or any of its
 * ancestors
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
 * @author Ben White
 * @see <a target="_blank" href="https://www.securecoding.cert.org/confluence/display/java/ERR08-J.+Do+not+catch+NullPointerException+or+any+of+its+ancestors">Java Secure Coding Rule: ERR08-J</a>
 */
class ERR08J_DoNotCatchNullPointerException implements IRule {

	@Override
	public boolean violated(ASTNode node) {
		// TODO See if NullPointerException or it's ancestors are in a catch block
		return false;
	}

	@Override
	public String getRuleText() {
		return "Catching a null pointer exception (or any of its ancestors) increases "
				+ "performance overhead, are difficult to troubleshoot and programs "
				+ "rarely remain in a usable state after they are thrown.";
	}

	@Override
	public String getRuleName() {
		return "ERR08-J. Do not catch NullPointerException or any of its ancestors";
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

}
