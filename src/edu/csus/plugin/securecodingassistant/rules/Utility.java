package edu.csus.plugin.securecodingassistant.rules;

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
	 * @param method The method invocation from the {@link org.eclipse.jdt.core.dom.ASTNode}
	 * @param className The name of the class where the method is defined
	 * @param methodName The name of the method without the parameters. For instance, if the method
	 * that is being searched for is <code>System.out.println()</code>, then pass <code>println</code>
	 * @return True if the <code>MethodInvocation</code> is <code>className.methodName()</code>
	 * @see org.eclipse.jdt.core.dom.ASTNode
	 * @see MethodInvocation
	 */
	public static boolean calledMethod(MethodInvocation method, String className, String methodName) {
		String miClassName = method.resolveMethodBinding().getDeclaringClass().getName().toString();
		String miMethodName = method.getName().toString();
		
		if (miClassName.equals(className) && miMethodName.equals(methodName)) {
			System.out.printf("In %s, found %s class and %s method%n", "Utility", className, methodName);
		}
		
		return miClassName.equals(className) && miMethodName.equals(methodName);
	}
}
