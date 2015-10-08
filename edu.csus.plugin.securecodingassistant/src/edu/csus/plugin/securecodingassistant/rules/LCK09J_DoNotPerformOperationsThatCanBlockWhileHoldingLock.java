package edu.csus.plugin.securecodingassistant.rules;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * Java Secure Coding Rule: LCK09-J. Do not perform operations that can block while
 * holding a lock
 * <p>
 * CERT Website: Holding locks while performing time-consuming or blocking operations can
 * severely degrade system performance and can result in starvation. Furthermore, deadlock
 * can result if interdependent threads block indefinitely. Blocking operations include
 * network, file, and console I/O (for example, <code>Console.readLine()</code>) and
 * object serialization. Deferring a thread indefinitely also constitutes a blocking
 * operation. Consequently, programs must not perform blocking operations while holding
 * a lock.
 * </p>
 * <p>
 * When the Java Virtual Machine (JVM) interacts with a file system that operates over
 * an unreliable network, file I/O might incur a large performance penalty. In such cases,
 * avoid file I/O over the network while holding a lock. File operations (such as logging)
 * that could block while waiting for the output stream lock or for I/O to complete could
 * be performed in a dedicated thread to speed up task processing. Logging requests can be
 * added to a queue, assuming that the queue's <code>put()</code> operation incurs little
 * overhead as compared to file I/O [Goetz 2006].
 * </p>
 * @author Ben White
 * @see <a target="_blank" href="https://www.securecoding.cert.org/confluence/display/java/LCK09-J.+Do+not+perform+operations+that+can+block+while+holding+a+lock">Java Secure Coding Rule: LCK09-J</a>
 */
class LCK09J_DoNotPerformOperationsThatCanBlockWhileHoldingLock implements IRule {

	@Override
	public boolean violated(ASTNode node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getRuleText() {
		return "Holding locks while performing time-consuming or blocking operations can "
				+ "severely degrade system performance and can result in starvation. "
				+ "Furthermore, deadlock  can result if interdependent threads block "
				+ "indefinitely. Blocking operations include network, file, and console "
				+ "I/O (for example, Console.readLine()) and object serialization. "
				+ "Deferring a thread indefinitely also constitutes a blocking operation. "
				+ "Consequently, programs must not perform blocking operations while "
				+ "holding a lock.";
	}

	@Override
	public String getRuleName() {
		return "LCK09-J. Do not perform operations that can block while holding a lock";
	}

	@Override
	public String getRuleRecommendation() {
		return "Do not call Thread.sleep(), Socket.getOutputStream(), or Socket.getInput"
				+ "Stream() from a synchronized method. Instead of Thread.sleep(), try "
				+ "calling wait which immediately releases current monitor.";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_LOW;
	}

}
