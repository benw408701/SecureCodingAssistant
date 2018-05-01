package edu.csus.plugin.securecodingassistant.rules_C;

import org.eclipse.cdt.core.dom.ast.IASTNode;

/**
 * @author Victor Melnik
 * 
 * @see NodeNumPair.java ( @author Ben White)
 * @see org.eclipse.cdt.core.dom.ast.IASTNode
 * @see IRule_C
 */
public class NodeNumPair_C {
	
	/**
	 * The stored node
	 */
	
	private IASTNode m_node;
	
	/**
	 * The number associated with the node
	 */
	private int m_num;
	
	/**
	 * Construct a new pair
	 * @param node The <code>ASTNode</code>
	 * @param num The number to associate with the node
	 */
	public NodeNumPair_C (IASTNode node, int num) {
		m_node = node;
		m_num = num;
	}
	
	/**
	 * Retrieve the node
	 * @return the node
	 */
	public IASTNode getNode() {
		return m_node;
	}
	
	/**
	 * Retrieve the number
	 * @return the number
	 */
	public int getNum() {
		return m_num;
	}
}
