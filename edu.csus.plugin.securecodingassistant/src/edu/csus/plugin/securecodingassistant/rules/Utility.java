package edu.csus.plugin.securecodingassistant.rules;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;

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
	
	// TODO: Provide a mechanism that the programmer can use to ignore false positives

	/**
	 * Use to check to see if a <code>MethodInvocation</code> node of an abstract syntax tree
	 * is calling a particular method from a class.
	 * @param method The method invocation from the {@link ASTNode}
	 * @param className The qualified name of the class where the method is defined
	 * @param methodName The name of the method without the parameters. For instance, if the method
	 * that is being searched for is <code>System.out.println()</code>, then pass <code>println</code>
	 * @param <code>true</code> if desired search behavior is to search super class hierarchy up
	 * to <code>Object</code> when looking to see if a method was called. For instance, if
	 * <code>method()</code> is defined in class A but called from subclass B. Use this flag if
	 * passing class A as the class name.
	 * @return True if the <code>MethodInvocation</code> is <code>className.methodName()</code>
	 * @see ASTNode
	 * @see MethodInvocation
	 */
	public static boolean calledMethod(MethodInvocation method, String className, String methodName,
			boolean searchClassHierarchy) {
		return calledMethod(method, className, methodName, null, searchClassHierarchy);
	}
	
	/**
	 * Use to check to see if a <code>MethodInvocation</code> node of an abstract syntax tree
	 * is calling a particular method from a class.
	 * @param method The method invocation from the {@link ASTNode}
	 * @param className The qualified name of the class where the method is defined
	 * @param methodName The name of the method without the parameters. For instance, if the method
	 * that is being searched for is <code>System.out.println()</code>, then pass <code>println</code>
	 * @return True if the <code>MethodInvocation</code> is <code>className.methodName()</code>
	 * @see ASTNode
	 * @see MethodInvocation
	 */
	public static boolean calledMethod(MethodInvocation method, String className, String methodName) {
		return calledMethod(method, className, methodName, null, false);
	}
	
	/**
	 * Use to check to see if a <code>MethodInvocation</code> node of an abstract syntax tree
	 * is calling a particular method from a class with a given argument.
	 * @param method The method invocation from the {@link ASTNode}
	 * @param className The qualified name of the class where the method is defined
	 * @param methodName The name of the method without the parameters. For instance, if the method
	 * that is being searched for is <code>System.out.println()</code>, then pass <code>println</code>
	 * @param argument The argument that needs to occur in the method to be considered a match
	 * @param <code>true</code> if desired search behavior is to search super class hierarchy up
	 * to <code>Object</code> when looking to see if a method was called. For instance, if
	 * <code>method()</code> is defined in class A but called from subclass B. Use this flag if
	 * passing class A as the class name.
	 * @return <code>true</code> if the <code>MethodInvocation</code> is <code>className.methodName()</code>
	 * @see ASTNode
	 * @see MethodInvocation
	 */
	public static boolean calledMethod(MethodInvocation method, String className,
			String methodName, SimpleName argument, boolean searchClassHierarchy) {
		
		boolean nameMatch = false, withArgument = false;
		if(method.getExpression() != null && method.getExpression().resolveTypeBinding() != null) {
			String miClassName = method.getExpression().resolveTypeBinding().getBinaryName();
			String miMethodName = method.getName().toString();
			withArgument = argument == null; // Default to true if no argument required
			nameMatch = miClassName.equals(className) && miMethodName.equals(methodName);
			
			// If searching hierarchy then search declaring class then parent classes
			if (searchClassHierarchy && !nameMatch) {
				ITypeBinding superClass = method.resolveMethodBinding().getDeclaringClass();
				nameMatch = superClass.getBinaryName().equals(className);
				while(!nameMatch && !superClass.getBinaryName().equals(Object.class.getCanonicalName())) {
					superClass = superClass.getSuperclass();
					nameMatch = superClass.getBinaryName().equals(className);
				}
			}
			
			// Do argument search if required
			if(argument != null && nameMatch)
				withArgument = argumentMatch(method.arguments(), argument);
		}
		
		return nameMatch && withArgument;
	}

	/**
	 * Use to check to see if a method is being called prior to a given method
	 * @param method The method that was called
	 * @param className The qualified name of the class of the method to be tested
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
	 * @param className The qualified name of the class of the method to be tested
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
		ASTNode node = getEnclosingNode(method, Block.class);

		if (node != null && node instanceof Block) {
			Block block = (Block)node;
			ASTNodeProcessor processor = new ASTNodeProcessor();
			block.accept(processor);
			ArrayList<NodeNumPair> nodes = processor.getMethods();
			methodPosition = searchNodeNumList(nodes, method);
			
			// Go through all blockMethods prior to method and look for a match
			Iterator<NodeNumPair> nodeItr = nodes.iterator();
			while(nodeItr.hasNext() && continueSearch && !foundMethod) {
				NodeNumPair n = nodeItr.next();
				assert n.getNode() instanceof MethodInvocation;
				MethodInvocation blockMethod = (MethodInvocation)n.getNode();
				foundMethod = calledMethod(blockMethod, className, methodName, argument, false);
				continueSearch = searchNodeNumList(nodes, blockMethod) < methodPosition;
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
		ASTNode node = getEnclosingNode(method, Block.class);
		
		if (node != null && node instanceof Block) {
			Block block = (Block) node;
			ASTNodeProcessor processor = new ASTNodeProcessor();
			block.accept(processor);
			methodPosition = searchNodeNumList(processor.getMethods(), method);
			
			// Go through all assignments
			ArrayList<NodeNumPair> nodes = processor.getAssignments();
			for (NodeNumPair n : nodes) {
				assert n.getNode() instanceof Assignment;
				Assignment assignment = (Assignment)n.getNode();
				if (searchNodeNumList(nodes, assignment) > methodPosition && !isModified) {
					Expression lhs = assignment.getLeftHandSide();
					isModified = lhs.subtreeMatch(new ASTMatcher(), identifier);
				}
			}
		}
		
		return isModified;
	}
	
	/**
	 * Returns <code>true</code> if the given node contains an instantiation for the given class
	 * with the given argument
	 * @param node The node to search through
	 * @param className The qualified name of the class to look for a constructor for
	 * @param argument Only return <code>true</code> if the constructor contains this argument
	 * @return <code>true</code> if the given node contains the given constructor with the given argument
	 */
	public static boolean containsInstanceCreation(ASTNode node, String className, SimpleName argument) {
		boolean containsInstanceCreation = false;
		
		ASTNodeProcessor processor = new ASTNodeProcessor();
		node.accept(processor);
		ArrayList<NodeNumPair> nodes = processor.getInstanceCreations();
		
		for (NodeNumPair n : nodes) {
			assert n.getNode() instanceof ClassInstanceCreation;
			ClassInstanceCreation instanceCreation = (ClassInstanceCreation)n.getNode();
			if (instanceCreation.resolveTypeBinding() != null &&
					instanceCreation.resolveTypeBinding().getBinaryName().equals(className)) {
				containsInstanceCreation = argument == null; // true if none required
				if (!containsInstanceCreation)
					containsInstanceCreation = argumentMatch(instanceCreation.arguments(), argument);
			}
		}
		
		return containsInstanceCreation;
	}
	
	/**
	 * Returns an enclosing node for a given node
	 * @param node The node to look for in the statement
	 * @param nodeType The type of node to look for (e.g. Block, WhileStatement, etc.)
	 * @return The node if found, <code>null</code> otherwise
	 */
	public static ASTNode getEnclosingNode(ASTNode node, Class<? extends ASTNode> nodeType) {
		ASTNode parent = node.getParent();
		
		while(parent != null && !(nodeType.equals(parent.getClass())))
			parent = parent.getParent();
		
		return parent == null ? null : parent;
	}
	
	/**
	 * Returns <code>true</code> if the given argument occurs in the list of arguments
	 * @param arguments A <code>List</code> of {@link Expression} type arguments
	 * @param argument A {@link SimpleName} argument to look for in the list
	 * @return <code>true</code> if the given argument occurs in the list of arguments
	 * @see Expression
	 * @see SimpleName
	 * @see ClassInstanceCreation#arguments()
	 * @see MethodInvocation#arguments()
	 */
	public static boolean argumentMatch(List<?> arguments, SimpleName argument) {
		for (Object o : arguments)
			if (o instanceof Expression) {
				Expression e = (Expression)o;
				if (e.subtreeMatch(new ASTMatcher(), argument))
					return true;
			}
		
		return false;
	}
	
	/**
	 * When a method is overridding another method this retrieves the <code>IMethodBinding</code>
	 * object from the super class.
	 * @param methodDec The <code>IMethodBinding</code> that overrides a method declaration from
	 * a parent class.
	 * @return The <code>IMethodBinding</code> from the parent class that was overridden or
	 * <code>null</code> if none found.
	 */
	public static IMethodBinding getSuperClassDeclaration(IMethodBinding method) {
		// Get super class 
		ITypeBinding superClass = method.getDeclaringClass().getSuperclass();
		while (superClass != null) {
			// Loop through all declared methods in the super class
			for(IMethodBinding superMethod : superClass.getDeclaredMethods()) {
				if (method.overrides(superMethod)) {
					return superMethod;
				}
			}
			// Get next super class
			superClass = superClass.getSuperclass();
		}
		return null;
	}
	
	/**
	 * Searches through an array list of {@link NodeNumPair} objects for a particular
	 * {@link ASTNode} and returns the number associated with it
	 * @param nodeNumList A list of {@link NodeNumPair} objects to search through
	 * @param node The {@link ASTNode} to search for
	 * @return The number associated with the node found in the list or -1 if not found
	 */
	private static int searchNodeNumList(ArrayList<NodeNumPair> nodeNumList, ASTNode node) {
		for (NodeNumPair pair : nodeNumList) {
			if (pair.getNode().equals(node))
				return pair.getNum();
		}
		
		return -1;
	}
}
