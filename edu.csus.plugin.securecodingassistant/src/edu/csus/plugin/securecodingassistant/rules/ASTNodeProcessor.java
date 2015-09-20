package edu.csus.plugin.securecodingassistant.rules;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.MethodInvocation;

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
	private ArrayList<CompoundASTNode<MethodInvocation>> m_methods;
	
	/**
	 * A list of assignments that are in the syntax tree
	 */
	private ArrayList<CompoundASTNode<Assignment>> m_assignments;
	
	/**
	 * Counts the number of nodes visited
	 */
	private int m_nodeCounter;
	
	/**
	 * Create new node processor, no arguments required
	 */
	public ASTNodeProcessor() {
		super();
		
		m_methods = new ArrayList<CompoundASTNode<MethodInvocation>>();
		m_assignments = new ArrayList<CompoundASTNode<Assignment>>();
		m_nodeCounter = 0;
	}
	
	/**
	 * Will build a list of <code>MethodInvocation</code> objects that are in the
	 * syntax tree
	 */
	@Override
	public boolean visit(MethodInvocation methodInvocation) {
		m_methods.add(new CompoundASTNode<MethodInvocation>(methodInvocation, ++m_nodeCounter));
		return super.visit(methodInvocation);
	}
	
	/**
	 * Will build a list of <code>Assignment</code> objects that are in the
	 * syntax tree
	 */
	@Override
	public boolean visit(Assignment assignment) {
		m_assignments.add(new CompoundASTNode<Assignment>(assignment, ++m_nodeCounter));
		return super.visit(assignment);
	}
	
	/**
	 * Retrieve the list of <code>MethodInvocation</code> objects that are in the
	 * syntax tree
	 * @return The list of <code>MethodInvocation</code> objects that are in the
	 * syntax tree
	 */
	public ArrayList<CompoundASTNode<MethodInvocation>> getMethods() {
		return m_methods;
	}
	
	/**
	 * Retrieve the list of <code>Assignment</code> objects that are in the
	 * syntax tree
	 * @return The list of <code>Assignment</code> objects that are in the
	 * syntax tree
	 */
	public ArrayList<CompoundASTNode<Assignment>> getAssignments() {
		return m_assignments;
	}
}
