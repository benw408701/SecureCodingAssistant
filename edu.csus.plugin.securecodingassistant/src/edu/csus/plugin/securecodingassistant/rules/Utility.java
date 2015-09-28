package edu.csus.plugin.securecodingassistant.rules;

import java.util.Iterator;

import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;

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
		return calledMethod(method, className, methodName, null);
	}
	
	/**
	 * Use to check to see if a <code>MethodInvocation</code> node of an abstract syntax tree
	 * is calling a particular method from a class with a given argument.
	 * @param method The method invocation from the {@link ASTNode}
	 * @param className The name of the class where the method is defined
	 * @param methodName The name of the method without the parameters. For instance, if the method
	 * that is being searched for is <code>System.out.println()</code>, then pass <code>println</code>
	 * @param argument The argument that needs to occur in the method to be considered a match
	 * @return <code>true</code> if the <code>MethodInvocation</code> is <code>className.methodName()</code>
	 * @see ASTNode
	 * @see MethodInvocation
	 */
	public static boolean calledMethod(MethodInvocation method, String className, String methodName, SimpleName argument) {
		
		String miClassName = method.getExpression().resolveTypeBinding().getName();
		String miMethodName = method.getName().toString();
		boolean withArgument = argument == null;
		boolean nameMatch = miClassName.equals(className) && miMethodName.equals(methodName);
		
		if(argument != null && nameMatch) {
			for(Object o : method.arguments()) {
				if (o instanceof Expression) {
					Expression arg = (Expression)o;
					withArgument = withArgument || arg.subtreeMatch(new ASTMatcher(), argument);
				}
			}
		}
		
		return nameMatch && withArgument;
	}

	/**
	 * Use to check to see if a method is being called prior to a given method
	 * @param method The method that was called
	 * @param className The name of the class of the method to be tested
	 * @param methodName The name of the method to be tested to see if it occurs prior to the
	 * given {@link MethodInvocation}
	 * @return <code>true</code> if the <code>className.methodName()</code> is called prior to the
	 * {@link MethodInvocation}
	 * @see ASTNode
	 * @see MethodInvocation
	 */
	public static boolean calledPrior(MethodInvocation method, String className, String methodName) {
		return calledPrior(method, className, methodName, null);
	}
	
	/**
	 * Use to check to see if a method is being called prior to a given method with a given argument
	 * @param method The method that was called
	 * @param className The name of the class of the method to be tested
	 * @param methodName The name of the method to be tested to see if it occurs prior to the
	 * given {@link MethodInvocation}
	 * @param argument The argument that needs to occur in the prior method for it to be considered
	 * a match
	 * @return <code>true</code> if the <code>className.methodName()</code> is called prior to the
	 * {@link MethodInvocation}
	 * @see ASTNode
	 * @see MethodInvocation
	 */
	public static boolean calledPrior(MethodInvocation method, String className, String methodName, SimpleName argument) {
		boolean foundMethod = false;
		int methodPosition; // the location of the method in the AST where searching should stop
		boolean continueSearch = true; // false when searching shouldn't continue
		Block block = getBlock(method);

		if (block != null) {
			ASTNodeProcessor processor = new ASTNodeProcessor();
			block.accept(processor);
			NodeArrayList<MethodInvocation> blockMethods = processor.getMethods();
			methodPosition = blockMethods.getNum(method);
			
			// Go through all blockMethods prior to method and look for a match
			Iterator<MethodInvocation> blockMethodItr = blockMethods.iterator();
			while(blockMethodItr.hasNext() && continueSearch) {
				MethodInvocation blockMethod = blockMethodItr.next();
				foundMethod = foundMethod || calledMethod(blockMethod, className, methodName, argument);
				continueSearch = blockMethods.getNum(blockMethod) < methodPosition;
			}
		}
		
		return foundMethod;
	}
	
	/**
	 * Use to see if a variable has been modified after a <code>MethodInvocation</code> in the syntax tree
	 * @param method The <code>MethodInvocation</code> in the syntax tree to start the search
	 * @param identifier The identifier to search for
	 * @return <code>true</code> if the identifier was found being modified after the node
	 */
	public static boolean modifiedAfter(MethodInvocation method, SimpleName identifier) {
		boolean isModified = false;
		int methodPosition; // The location of the method in the AST where searching should start
		Block block = getBlock(method);
		
		if (block != null) {
			ASTNodeProcessor processor = new ASTNodeProcessor();
			block.accept(processor);
			methodPosition = processor.getMethods().getNum(method);
			
			// Go through all assignments
			NodeArrayList<Assignment> assignments = processor.getAssignments();
			for (Assignment assignment : assignments)
				if (assignments.getNum(assignment) > methodPosition) {
					Expression lhs = assignment.getLeftHandSide();
					isModified = isModified || lhs.subtreeMatch(new ASTMatcher(), identifier);
				}
		}
		
		return isModified;
	}
	
	/**
	 * Returns <code>true</code> if the given node contains an instantiation for the given class
	 * with the given argument
	 * @param node The node to search through
	 * @param className The name of the class to look for a constructor for
	 * @param argument Only return <code>true</code> if the constructor contains this argument
	 * @return <code>true</code> if the given node contains the given constructor with the given argument
	 */
	public static boolean containsInstanceCreation(ASTNode node, String className, SimpleName argument) {
		boolean containsInstanceCreation = false;
		
		ASTNodeProcessor processor = new ASTNodeProcessor();
		node.accept(processor);
		NodeArrayList<ClassInstanceCreation> instantiations = processor.getInstanceCreations();
		
		for (ClassInstanceCreation instantiation : instantiations)
			// Note that a null argument will evaluate to true in the contains method
			containsInstanceCreation = containsInstanceCreation ||
					// TODO: The following does not work as expected
					instantiation.arguments().contains(argument) &&
					instantiation.resolveTypeBinding().getName().equals(className);
		
		return containsInstanceCreation;
	}
	
	/**
	 * Returns the block-level element that contains a given <code>ASTNode</code>
	 * @param node The node contained on the block
	 * @return The block that contains the node
	 */
	public static Block getBlock(ASTNode node) {
		ASTNode parent = node.getParent();
		
		// Go to block level
		while(parent != null && !(parent instanceof Block)) {
			parent = parent.getParent();
		}
		
		if (parent instanceof Block)
			return (Block)parent;
		else
			return null;
	}
	
	/**
	 * Returns an enclosing statement for a given node
	 * @param node The node to look for in the statement
	 * @param statementType The type of statement to look for
	 * @return The statement if found, <code>null</code> otherwise
	 */
	public static Statement getEnclosingStatement(ASTNode node, Class<? extends Statement> statementType) {
		ASTNode parent = node.getParent();
		
		while(parent != null && !(statementType.equals(parent.getClass())))
			parent = parent.getParent();
		
		return parent == null ? null : (Statement) parent;
	}
}
