package edu.csus.plugin.securecodingassistant.rules;

import java.io.InputStream;
import java.io.Reader;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import edu.csus.plugin.securecodingassistant.Globals;

/**
 * <b><i>The text and/or code below is from the CERT website: <a target="_blank" href="https://www.securecoding.cert.org">https://www.securecoding.cert.org</a></i></b>
 * <p>
 * Java Secure Coding Rule: FIO08-J. Distinguish between characters or bytes read from a
 * stream and -1
 * </p>
 * <p>
 * CERT Website: The abstract <code>InputStream.read()</code> and <code>Reader.read()</code>
 * methods are used to read a byte or character, respectively, from a stream. The
 * <code>InputStream.read()</code> method reads a single byte from an input source and
 * returns its value as an <code>int</code> in the range 0 to 255 (0x00-0xff). The
 * <code>Reader.read()</code> method reads a single character and returns its value as an
 * <code>int</code> in the range 0 to 65,535 (0x0000-0xffff). Both methods return the
 * 32-bit value -1 (0xffffffff) to indicate that the end of the stream has been reached
 * and no data is available. The larger <code>int</code> size is used by both methods to
 * differentiate between the end-of-stream indicator and the maximum byte (0xff) or
 * character (0xffff) value. The end-of-stream indicator is an example of an in-band
 * error indicator. In-band error indicators are problematic to work with, and the
 * creation of new in-band-error indicators is discouraged.
 * </p>
 * <p>
 * Prematurely converting the resulting <code>int</code> to a <code>byte</code> or
 * <code>char</code> before testing for the value -1 makes it impossible to distinguish
 * between characters read and the end of stream indicator. Programs must check for the
 * end of stream before narrowing the return value to a <code>byte</code> or
 * <code>char</code>.
 * </p>
 * <p>
 * This rule applies to any method that returns the value -1 to indicate the end of a
 * stream. It includes any <code>InputStream</code> or <code>Reader</code> subclass
 * that provides an implementation of the <code>read()</code> method. This rule is a
 * specific instance of NUM12-J. Ensure conversions of numeric types to narrower types
 * do not result in lost or misinterpreted data.
 * </p>
 * @author Ben White (Plugin Logic), CERT (Rule Definition)
 * @see Java Secure Coding Rule defined by CERT: <a target="_blank" href="https://www.securecoding.cert.org/confluence/display/java/FIO08-J.+Distinguish+between+characters+or+bytes+read+from+a+stream+and+-1">FIO08-J</a>
 */
class FIO08J_DistinguishBetweenCharactersOrBytes implements IRule {

	@Override
	public boolean violated(ASTNode node) {
		boolean ruleViolated = false;
		
		// Check to see if in CastExpression
		if (node instanceof CastExpression) {
			CastExpression cast = (CastExpression)node;
			// Check to see if expression portion is a method invocation
			if (cast.getExpression() instanceof MethodInvocation) {
				MethodInvocation method = (MethodInvocation)cast.getExpression();
				String castType = cast.getType().resolveBinding().getQualifiedName();
				// Rule is violated if InputStream.read() or ancestor is called and cast to byte
				// or if Reader.read() or ancestor is called and cast to char
				ruleViolated = (castType.equals(byte.class.getCanonicalName())
								&& Utility.calledMethod(method, InputStream.class.getCanonicalName(), "read", true))
						|| (castType.equals(char.class.getCanonicalName())
								&& Utility.calledMethod(method, Reader.class.getCanonicalName(), "read", true));
			}
		}

		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		return "The result of InputStream.read() or Reader.read() must never be cast "
				+ "to a byte or a character since the return type is an integer and -1 "
				+ "(0xffffffff) is used to indicate end of stream. If InputStream.read() "
				+ "returns 0xff or Reader.read returns 0xffff then casting to an int "
				+ "will make it impossible to distinguish between end of streem and the "
				+ "next byte or character.";
	}

	@Override
	public String getRuleName() {
		return "FIO08-J. Distinguish between characters or bytes read from a stream and -1";
	}

	@Override
	public String getRuleRecommendation() {
		return "Do not cast the result of InputStream.read() to a byte or Reader.read() "
				+ "to a byte.";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_HIGH;
	}

}
