package edu.csus.plugin.securecodingassistant.rules;

import java.util.ArrayList;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;

/**
 * Adds a numbering element that can be used to indicate a node number in an {@link AST}. This is
 * intended to be used with {@link ASTNodeProcessor}
 * @author Ben White
 *
 * @param <E> The type of {@link ASTNode} in the collection
 * @see ASTNode
 * @see ArrayList
 * @see ASTNodeProcessor
 */
public class NodeArrayList<E extends ASTNode> extends ArrayList<E> {

	/**
	 * Not planning on using serialization for this class, using default value of 1
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Numbers corresponding to each element in the {@link ArrayList}. The index of the number and the
	 * index of the corresponding item in the {@link ArrayList} are equal.
	 */
	private ArrayList<Integer> m_nodeNum;
	
	/**
	 * Only supported constructor, creates an empty <code>NodeArrayList</code>
	 */
	public NodeArrayList() {
		super();
		
		m_nodeNum = new ArrayList<Integer>();
	}
	
	/**
	 * Adds a new element to the list with an associated node number. This is the only method that adds
	 * a node number. If other add methods are called and there is no number associated then {@link #getNum(ASTNode)}
	 * will return <code>-1</code> for that node.
	 * @param element The element to add to the <code>NodeArrayList</code>
	 * @param nodeNum The number to associate with the element
	 * @return <code>true</code> if successful
	 */
	public boolean addWithNum(E element, int nodeNum) {
		boolean addSuccessful = super.add(element);
		
		if(addSuccessful) {
			// Check to make sure the size of m_nodeNum is one less than the size of the ArrayList
			for (int i = m_nodeNum.size(); i < size() - 1; i++) // Loop until i == size() - 1
				m_nodeNum.add(i, -1); // if no number was given then set to -1
			
			assert m_nodeNum.size() == size() - 1;
			m_nodeNum.add(nodeNum);
			assert m_nodeNum.size() == size();
		}
		
		return addSuccessful;
	}
	
	/**
	 * Retrieves the number that is associated with a given element in the <code>NodeArrayList</code>.
	 * If there isn't a number associate then <code>-1</code> will be returned.
	 * @param element The element to search for. The returned value will be the associated
	 * node number with this element.
	 * @return The associated node number for the given element. If there isn't an associated number
	 * then returns <code>-1</code>.
	 */
	public int getNum(E element) {
		int nodeNum = -1; // default value -1 means node doesn't have a number
		int index = indexOf(element);
		if(index > 0 /* Exists */ && index < m_nodeNum.size() /* Has Num */)
			nodeNum = m_nodeNum.get(index);
		return nodeNum;
	}
}
