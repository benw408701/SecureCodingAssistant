package edu.csus.plugin.securecodingassistant.rules;

import org.eclipse.jdt.core.dom.ASTNode;

/**
 * This pair of an {@link ASTNode} and an <code>int</code> is to be used to keep track of
 * the location of a node in a syntax tree in relationship to other nodes.
 * @author Ben White
 * @see ASTNode
 * @see ASTNodeProcessor
 */
public class NodeNumPair {

	/**
	 * The stored node
	 */
	private ASTNode m_node;
	
	/**
	 * The number associated with the node
	 */
	private int m_num;
	
	/**
	 * Construct a new pair
	 * @param node The <code>ASTNode</code>
	 * @param num The number to associate with the node
	 */
	NodeNumPair (ASTNode node, int num) {
		m_node = node;
		m_num = num;
	}
	
	/**
	 * Retrieve the node
	 * @return the node
	 */
	public ASTNode getNode() {
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
