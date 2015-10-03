package edu.csus.plugin.securecodingassistant.rules;

import org.eclipse.jdt.core.dom.ASTNode;

public class NodeNumPair {

	private ASTNode m_node;
	private int m_num;
	
	NodeNumPair (ASTNode node, int num) {
		m_node = node;
		m_num = num;
	}
	
	public ASTNode getNode() {
		return m_node;
	}
	
	public int getNum() {
		return m_num;
	}
}
