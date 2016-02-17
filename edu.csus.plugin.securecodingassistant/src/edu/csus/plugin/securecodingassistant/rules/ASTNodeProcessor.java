package edu.csus.plugin.securecodingassistant.rules;

import java.util.ArrayList;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

/**
 * A custom <code>ASTVisitor</code> that is used by rules to parse an abstract syntax tree.
 * This can be used to get a list of methods that are in the same block as another for instance.
 * @author Ben White
 * @see org.eclipse.jdt.core.dom.ASTNode
 * @see IRule
 */
class ASTNodeProcessor extends ASTVisitor {
	/**
	 * A list of methods invocations that are in the syntax tree
	 */
	private ArrayList<NodeNumPair> m_methods;
	
	/**
	 * A list of assignments that are in the syntax tree
	 */
	private ArrayList<NodeNumPair> m_assignments;
	
	/**
	 * A list of instantiations
	 */
	private ArrayList<NodeNumPair> m_instantiations;
	
	/**
	 * A list of enhanced for statements
	 */
	private ArrayList<NodeNumPair> m_enhancedForStatements;
	
	/**
	 * A list of super method invocations
	 */
	private ArrayList<NodeNumPair> m_superMethods;
	
	/**
	 * A list of variable declaration fragments
	 */
	private ArrayList<NodeNumPair> m_variableDeclarations;
	
	/**
	 * Counts the number of nodes visited
	 */
	private int m_nodeCounter;
	
	/**
	 * Create new node processor, no arguments required
	 */
	public ASTNodeProcessor() {
		super();
		
		m_methods = new ArrayList<NodeNumPair>();
		m_assignments = new ArrayList<NodeNumPair>();
		m_instantiations = new ArrayList<NodeNumPair>();
		m_enhancedForStatements = new ArrayList<NodeNumPair>();
		m_superMethods = new ArrayList<NodeNumPair>();
		m_nodeCounter = 0;
	}
	
	/**
	 * Will build a list of <code>MethodInvocation</code> objects that are in the
	 * syntax tree
	 */
	@Override
	public boolean visit(MethodInvocation methodInvocation) {
		m_methods.add(new NodeNumPair(methodInvocation, ++m_nodeCounter));
		return super.visit(methodInvocation);
	}
	
	/**
	 * Will build a list of <code>Assignment</code> objects that are in the
	 * syntax tree
	 */
	@Override
	public boolean visit(Assignment assignment) {
		m_assignments.add(new NodeNumPair(assignment, ++m_nodeCounter));
		return super.visit(assignment);
	}
	
	/**
	 * Will build a list of <code>ClassInstanceCreation</code> objects that are in the
	 * syntax tree
	 */
	@Override
	public boolean visit(ClassInstanceCreation instantiation) {
		m_instantiations.add(new NodeNumPair(instantiation, ++m_nodeCounter));
		return super.visit(instantiation);
	}
	
	/**
	 * Will build a list of <code>EnhancedForStatement</code> objects that are in the
	 * syntax tree
	 */
	@Override
	public boolean visit(EnhancedForStatement statement) {
		m_enhancedForStatements.add(new NodeNumPair(statement, ++m_nodeCounter));
		return super.visit(statement);
	}
	
	/**
	 * Will build a list of <code>SuperMethodInvocation</code> objects that are in the
	 * syntax tree
	 */
	@Override
	public boolean visit(SuperMethodInvocation method) {
		m_superMethods.add(new NodeNumPair(method, ++m_nodeCounter));
		return super.visit(method);
	}
	
	/**
	 * Will build a list of <code>VariableDeclarationFragment</code> objects that are in the
	 * syntax tree
	 */
	@Override
	public boolean visit(VariableDeclarationFragment declaration) {
		m_variableDeclarations.add(new NodeNumPair(declaration, ++m_nodeCounter));
		return super.visit(declaration);
	}
	
	/**
	 * Retrieve the list of <code>MethodInvocation</code> objects that are in the
	 * syntax tree
	 * @return The list of <code>MethodInvocation</code> objects that are in the
	 * syntax tree
	 */
	public ArrayList<NodeNumPair> getMethods() {
		return m_methods;
	}
	
	/**
	 * Retrieve the list of <code>Assignment</code> objects that are in the
	 * syntax tree
	 * @return The list of <code>Assignment</code> objects that are in the
	 * syntax tree
	 */
	public ArrayList<NodeNumPair> getAssignments() {
		return m_assignments;
	}
	
	/**
	 * Retrieve the list of <code>ClassInstanceCreation</code> objects that are in the
	 * syntax tree
	 * @return The list of <code>ClassInstanceCreation</code> objects that are in the
	 * syntax tree
	 */
	public ArrayList<NodeNumPair> getInstanceCreations() {
		return m_instantiations;
	}
	
	/**
	 * Retrieve the list of <code>EnhancedForStatement</code> objects that are in the
	 * syntax tree
	 * @return The list of <code>EnhancedForStatement</code> objects that are in the
	 * syntax tree
	 */
	public ArrayList<NodeNumPair> getEnhancedForStatements() {
		return m_enhancedForStatements;
	}
	
	/**
	 * Retrieve the list of <code>SuperMethodInvocation</code> objects that are in the
	 * syntax tree
	 * @return The list of <code>SuperMethodInvocation</code> objects that are in the
	 * syntax tree
	 */
	public ArrayList<NodeNumPair> getSuperMethodInvocations() {
		return m_superMethods;
	}
	
	/**
	 * Retrieve the list of <code>VariableDeclarationFragment</code> objects that are in the
	 * syntax tree
	 * @return The list of <code>VariableDeclarationFragment</code> objects that are in the
	 * syntax tree
	 */
	public ArrayList<NodeNumPair> getVariableDeclarationFragments() {
		return m_variableDeclarations;
	}
}
