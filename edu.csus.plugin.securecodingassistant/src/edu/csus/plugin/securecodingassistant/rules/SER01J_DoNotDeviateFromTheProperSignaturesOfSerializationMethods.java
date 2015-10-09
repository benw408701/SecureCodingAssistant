package edu.csus.plugin.securecodingassistant.rules;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * Java Secure Coding Rule: SER01-J. Do not deviate from the proper signatures of
 * serialization methods
 * <p>
 * CERT Website: Unlike most interfaces, <code>Serializable</code> does not define the
 * method signatures it requires. Interfaces allow only public fields and methods, whereas
 * <code>readObject()</code>, <code>readObjectNoData</code>, and <code>writeObject()</code>
 * must be declared private. Similarly, the <code>Serializable</code> interface does not
 * prevent <code>readResolve()</code> and <code>writeReplace()</code> methods from being
 * declared <code>static</code>, <code>public</code>, or <code>private</code>.
 * Consequently, the Java serialization mechanism fails to let the compiler identify an
 * incorrect method signature for any of these methods.
 * </p>
 * <p>
 * The <code>writeObject</code>, <code>readObject</code> and <code>readObjectNodData<code>
 * methods must be implemented with specific signatures. They must be declared
 * <code>private</code> so that extending classes cannot invoke or override them.
 * </p>
 * @author Ben White
 * @see <a target="_blank" href="https://www.securecoding.cert.org/confluence/display/java/SER01-J.+Do+not+deviate+from+the+proper+signatures+of+serialization+methods">Java Secure Coding Rule: SER01-J</a>
 */
class SER01J_DoNotDeviateFromTheProperSignaturesOfSerializationMethods implements IRule {

	@Override
	public boolean violated(ASTNode node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getRuleText() {
		return "CERT Website-Unlike most interfaces, Serializable does not define the "
				+ "method signatures it requires. Interfaces allow only public fields "
				+ "and methods, whereas readObject(), readObjectNoData, and writeObject() "
				+ "must be declared private. Similarly, the Serializable interface does "
				+ "not prevent readResolve() and writeReplace() methods from being "
				+ "declared static, public, or private. Consequently, the Java "
				+ "serialization mechanism fails to let the compiler identify an "
				+ "incorrect method signature for any of these methods.";
	}

	@Override
	public String getRuleName() {
		return "SER01-J. Do not deviate from the proper signatures of serialization methods";
	}

	@Override
	public String getRuleRecommendation() {
		return "writeObject, readObject, and readObjectNoData must be marked as private. "
				+ "Also, readResolve and writeReplace cannot be static and must be protected.";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_HIGH;
	}

}
