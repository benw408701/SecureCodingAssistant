package edu.csus.plugin.securecodingassistant.rules;

import java.io.Console;
import java.net.Socket;
import java.util.List;
import java.util.TreeMap;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * <b><i>The text and/or code below is from the CERT website: <a target="_blank" href="https://www.securecoding.cert.org">https://www.securecoding.cert.org</a></i></b>
 * <p>
 * Java Secure Coding Rule: LCK09-J. Do not perform operations that can block while
 * holding a lock
 * </p>
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
 * @author Ben White (Plugin Logic), CERT (Rule Definition)
 * @see Java Secure Coding Rule defined by CERT: <a target="_blank" href="https://www.securecoding.cert.org/confluence/display/java/LCK09-J.+Do+not+perform+operations+that+can+block+while+holding+a+lock">LCK09-J</a>
 */
class LCK09J_DoNotPerformOperationsThatCanBlockWhileHoldingLock extends SecureCodingRule {

	@Override
	public boolean violated(ASTNode node) {
		boolean ruleViolated = false;
		
		// Is node a method invocation for Thread.sleep(), Socket.getOutputStream(),
		// Socket.getInputStream() or Console.readLine()?
		if(node instanceof MethodInvocation) {
			MethodInvocation method = (MethodInvocation) node;
			if(Utility.calledMethod(method, Thread.class.getCanonicalName(), "sleep")
					|| Utility.calledMethod(method, Socket.class.getCanonicalName(), "getOutputStream")
					|| Utility.calledMethod(method, Socket.class.getCanonicalName(), "getInputStream")
					|| Utility.calledMethod(method, Console.class.getCanonicalName(), "readLine")) {
			
				// Is the method invocation in a method definition that is declared to be synchronized?
				ASTNode encNode = Utility.getEnclosingNode(method, MethodDeclaration.class);
				if(encNode != null && encNode instanceof MethodDeclaration) {
					MethodDeclaration methodDec = (MethodDeclaration)encNode;
					List<?> modList = methodDec.modifiers();
					for (Object o : modList) {
						if (o instanceof Modifier) {
							Modifier mod = (Modifier)o;
							ruleViolated = (mod.getKeyword().toFlagValue() & Modifier.SYNCHRONIZED) != 0;
						}
					}
				}
				
			}
			if (ruleViolated)
				ruleViolated = super.violated(node);
		}
		
		return ruleViolated;
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
		return Globals.RuleNames.LCK09_J;
	}

	@Override
	public String getRuleRecommendation() {
		return "Do not call Thread.sleep(), Socket.getOutputStream(), Socket.getInput"
				+ "Stream(), or Console.readLine() from a synchronized method. Instead of "
				+ "Thread.sleep(), try calling wait which immediately releases current monitor.";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_LOW;
	}

	@Override
	public String getRuleID() {
		return Globals.RuleID.LCK09_J;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TreeMap<String, ASTRewrite> getSolutions(ASTNode node){
		if (!violated(node))
			throw new IllegalArgumentException("Doesn't violate rule " + getRuleID());
		
		TreeMap<String, ASTRewrite> map = new TreeMap<>();
		
		AST ast = node.getAST();
		ASTRewrite rewrite = ASTRewrite.create(ast);
		
		MethodInvocation mi = (MethodInvocation)node;
		if ("Thread".equals(mi.getExpression().toString()) && "sleep".equals(mi.getName().getIdentifier())) {
			//get the sleep(time) stmt
			Statement stmt = SecureCodingNodeVisitor.getStatement(node);
			//get argument simple name
			String arg = "";
			if (mi.arguments() != null && mi.arguments().get(0) != null) {
				arg = mi.arguments().get(0).toString();
			}
			
			// wait(time)
			MethodInvocation wait = ast.newMethodInvocation();
			wait.setName(ast.newSimpleName("wait"));
			wait.arguments().add(ast.newSimpleName(arg));
			ExpressionStatement expstmt = ast.newExpressionStatement(wait);
			
			SimpleName sn = (SimpleName) rewrite.createStringPlaceholder("/*condition does not hold*/", ASTNode.SIMPLE_NAME);
			
			//while(<>) {wait(time);}
			WhileStatement wstmt = ast.newWhileStatement();
			wstmt.setExpression(sn);
			wstmt.setBody(expstmt);
			
			rewrite.replace(stmt, wstmt, null);
			map.put("Remove Thread.sleep", rewrite);
		}
		map.putAll(super.getSolutions(node));
		return map;
	}
}
