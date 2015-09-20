package edu.csus.plugin.securecodingassistant.rules;

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
	private NodeArrayList<MethodInvocation> m_methods;
	
	/**
	 * A list of assignments that are in the syntax tree
	 */
	private NodeArrayList<Assignment> m_assignments;
	
	/**
	 * Counts the number of nodes visited
	 */
	private int m_nodeCounter;
	
	/**
	 * Create new node processor, no arguments required
	 */
	public ASTNodeProcessor() {
		super();
		
		m_methods = new NodeArrayList<MethodInvocation>();
		m_assignments = new NodeArrayList<Assignment>();
		m_nodeCounter = 0;
	}
	
	/**
	 * Will build a list of <code>MethodInvocation</code> objects that are in the
	 * syntax tree
	 */
	@Override
	public boolean visit(MethodInvocation methodInvocation) {
		m_methods.addWithNum(methodInvocation, ++m_nodeCounter);
		return super.visit(methodInvocation);
	}
	
	/**
	 * Will build a list of <code>Assignment</code> objects that are in the
	 * syntax tree
	 */
	@Override
	public boolean visit(Assignment assignment) {
		m_assignments.addWithNum(assignment, ++m_nodeCounter);
		return super.visit(assignment);
	}
	
	/**
	 * Retrieve the list of <code>MethodInvocation</code> objects that are in the
	 * syntax tree
	 * @return The list of <code>MethodInvocation</code> objects that are in the
	 * syntax tree
	 */
	public NodeArrayList<MethodInvocation> getMethods() {
		return m_methods;
	}
	
	/**
	 * Retrieve the list of <code>Assignment</code> objects that are in the
	 * syntax tree
	 * @return The list of <code>Assignment</code> objects that are in the
	 * syntax tree
	 */
	public NodeArrayList<Assignment> getAssignments() {
		return m_assignments;
	}
}
