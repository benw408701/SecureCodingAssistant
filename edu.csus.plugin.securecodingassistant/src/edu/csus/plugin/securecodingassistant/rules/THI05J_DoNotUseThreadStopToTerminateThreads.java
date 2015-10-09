package edu.csus.plugin.securecodingassistant.rules;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * Java Secure Coding Rule: THI05-J. Do not use Thread.stop() to terminate threads
 * <p>
 * CERT Website: Certain thread APIs were introduced to facilitate thread suspension,
 * resumption, and termination but were later deprecated because of inherent design
 * weaknesses. For example, the <code>Thread.stop()</code> method causes the thread
 * to immediately throw a <code>ThreadDeath</code> exception, which usually stops
 * the thread. More information about deprecated methods is available in MET02-J.
 * Do not use deprecated or obsolete classes or methods.
 * </p>
 * <p>
 * Invoking <code>Thread.stop()</code> results in the release of all locks a thread
 * has acquired, potentially exposing the objects protected by those locks when those
 * objects are in an inconsistent state. The thread might catch the <code>ThreadDeath</code>
 * exception and use a <code>finally</code> block in an attempt to repair the inconsistent
 * object or objects. However, doing so requires careful inspection of all synchronized
 * methods and blocks because a <code>ThreadDeath</code> exception can be thrown at any
 * point during the thread's execution. Furthermore, code must be protected from
 * <code>ThreadDeath</code> exceptions that might occur while executing <code>catch</code>
 * or <code>finally</code> blocks [Sun 1999]. Consequently, programs must not invoke
 * <code>Thread.stop()</code>.
 * </p>
 * @author Ben White
 * @see <a target="_blank" href="https://www.securecoding.cert.org/confluence/display/java/THI05-J.+Do+not+use+Thread.stop%28%29+to+terminate+threads">Java Secure Coding Rule: THI05-J</a>
 */
class THI05J_DoNotUseThreadStopToTerminateThreads implements IRule {

	@Override
	public boolean violated(ASTNode node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getRuleText() {
		return "CERT Website-Invoking Thread.stop() results in the release of all locks a "
				+ "thread has acquired, potentially exposing the objects protected by those "
				+ "locks when those objects are in an inconsistent state. The thread might "
				+ "catch the ThreadDeath exception and use a finally block in an attempt to "
				+ "repair the inconsistent object or objects. However, doing so requires "
				+ "careful inspection of all synchronized methods and blocks because a "
				+ "ThreadDeath exception can be thrown at any point during the thread's execution. "
				+ "Furthermore, code must be protected from ThreadDeath exceptions that might "
				+ "occur while executing catch or finally blocks [Sun 1999]. Consequently, "
				+ "programs must not invoke Thread.stop().";
	}

	@Override
	public String getRuleName() {
		return "THI05-J. Do not use Thread.stop() to terminate threads";
	}

	@Override
	public String getRuleRecommendation() {
		return "Design the method to be interruptible or use a volitale flag to indicate"
				+ "termination requested.";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_LOW;
	}
}
