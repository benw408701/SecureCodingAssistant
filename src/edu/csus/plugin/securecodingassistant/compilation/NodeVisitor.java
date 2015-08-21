/**
 * 
 */
package edu.csus.plugin.securecodingassistant.compilation;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.ReconcileContext;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;

import edu.csus.plugin.securecodingassistant.rules.IRule;

/**
 * @author bwhite
 *
 */
public class NodeVisitor extends ASTVisitor {
	
	private ArrayList<IRule> m_rules;
	private ArrayList<InsecureCodeSegment> m_existingInsecureCodeSegments;
	private ArrayList<InsecureCodeSegment> m_newInsecureCodeSegments;
	private ReconcileContext m_context;
	
	public NodeVisitor(ArrayList<IRule> rules, 
			ArrayList<InsecureCodeSegment> insecureCodeSegments,
			ReconcileContext context) {
		super();
		
		m_rules = rules;
		m_existingInsecureCodeSegments = insecureCodeSegments;
		m_newInsecureCodeSegments = new ArrayList<InsecureCodeSegment>();
		m_context = context;
	}
	
	@Override
	public void preVisit(ASTNode node) {
		// Iterate through rules
		for (IRule rule : m_rules) {
			if(rule.violated(node)) {
				System.out.printf("Rule violated at node %s%n", node.toString());
				boolean capturedCode = false; // True if already found
				for (InsecureCodeSegment cs : m_existingInsecureCodeSegments) {
					try {
						if (m_context.getDelta().getElement().getUnderlyingResource().findMarker(cs.getMarker().getId()) != null) {
							capturedCode = true;
							break;
						}
					} catch (JavaModelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (!capturedCode)
					m_newInsecureCodeSegments.add(new InsecureCodeSegment(node, rule, m_context));
			}
		}
	}
	
	public ArrayList<InsecureCodeSegment> getNewInsecureCodeSegments() {
		return m_newInsecureCodeSegments;
	}
}
