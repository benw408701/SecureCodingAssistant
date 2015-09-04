package edu.csus.plugin.securecodingassistant.rules;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodInvocation;

/**
 * Collection of utility methods used by the Secure Coding Assistant Rules
 * @author Ben White
 *
 */
final class Utility {
	
	/**
	 * Cannot instantiate
	 */
	private Utility() {
	}
	
	/**
	 * Use to check to see if a <code>MethodInvocation</code> node of an abstract syntax tree
	 * is calling a particular method from a class.
	 * @param method The method invocation from the {@link ASTNode}
	 * @param className The name of the class where the method is defined
	 * @param methodName The name of the method without the parameters. For instance, if the method
	 * that is being searched for is <code>System.out.println()</code>, then pass <code>println</code>
	 * @return True if the <code>MethodInvocation</code> is <code>className.methodName()</code>
	 * @see ASTNode
	 * @see MethodInvocation
	 */
	public static boolean calledMethod(MethodInvocation method, String className, String methodName) {
		String miClassName = method.resolveMethodBinding().getDeclaringClass().getName().toString();
		String miMethodName = method.getName().toString();
		
		return miClassName.equals(className) && miMethodName.equals(methodName);
	}
	
	/**
	 * Use to check to see if a method is being called prior to a given method
	 * @param method The method that was called
	 * @param className The name of the class of the method to be tested
	 * @param methodName The name of the method to be tested to see if it occurs prior to the
	 * given {@link MethodInvocation}
	 * @return True if the <code>className.methodName()</code> is called prior to the
	 * {@link MethodInvocation}
	 * @see ASTNode
	 * @see MethodInvocation
	 */
	public static boolean calledPrior(MethodInvocation method, String className, String methodName) {
		boolean foundMethod = false;
		
		ASTNode parent = method.getParent();

		// Go to block level
		while(!(parent instanceof Block)) {
			parent = parent.getParent();
		}
		
		// Confirm at block level
		if (parent instanceof Block) {
			Block block = (Block)parent;
			ASTNodeProcessor processor = new ASTNodeProcessor();
			block.accept(processor);
			ArrayList<MethodInvocation> blockMethods = processor.getMethods();
			
			// Go through all blockMethods prior to method and look for a match
			MethodInvocation blockMethod = null;
			for (int i = 0; i < blockMethods.size() && blockMethod != method; i++) {
				blockMethod = blockMethods.get(i);
				// Check to see if className.methodName() was called prior to method
				foundMethod = foundMethod || calledMethod(blockMethod, className, methodName);
			}
		}
		
		return foundMethod;
	}
}
