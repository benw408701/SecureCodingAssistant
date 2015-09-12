package edu.csus.plugin.securecodingassistant.rules;

import java.util.ArrayList;
import org.eclipse.jdt.core.dom.ASTVisitor;
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
	private ArrayList<MethodInvocation> m_methods;
	
	/**
	 * Create new node processor, no arguments required
	 */
	public ASTNodeProcessor() {
		super();
		
		m_methods = new ArrayList<MethodInvocation>();
	}
	
	/**
	 * Will build a list of <code>MethodInvocation</code> objects that are in the
	 * syntax tree
	 */
	@Override
	public boolean visit(MethodInvocation methodInvocation) {
		m_methods.add(methodInvocation);
		return super.visit(methodInvocation);
	}
	
	/**
	 * Retrieve the list of <code>MethodInvocation</code> objects that are in the
	 * syntax tree
	 * @return The list of <code>MethodInvocation</code> objects that are in the
	 * syntax tree
	 */
	public ArrayList<MethodInvocation> getMethods() {
		return m_methods;
	}
}
