package edu.csus.plugin.securecodingassistant.rules;

import org.eclipse.jdt.core.dom.ASTNode;

/**
 * Allows an ASTNode to be associated with a number, use this for storing the order in
 * which nodes are scanned
 * @author Ben White
 *
 * @param <NType> The type of node being stored, must be a derivative of type {@link ASTNode}
 * @see ASTNode
 */
public class CompoundASTNode <NType extends ASTNode> {
	/**
	 * The node
	 */
	private NType m_node;
	/**
	 * The number associated with the node
	 */
	private int m_nodeNum;

	/**
	 * Create a new compound ASTNode
	 * @param node The node
	 * @param nodeNum The number associated with the node
	 */
	public CompoundASTNode(NType node, int nodeNum) {
		m_node = node;
		m_nodeNum = nodeNum;
	}
	
	/**
	 * Gets the node
	 * @return The node
	 */
	public NType getNode() {
		return m_node;
	}
	
	/**
	 * Gets the number associated with the node
	 * @return The number associated with the node
	 */
	public int getNodeNum() {
		return m_nodeNum;
	}
}
