package edu.csus.plugin.securecodingassistant.compilation;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.compiler.ReconcileContext;
import org.eclipse.jdt.core.dom.ASTNode;

import edu.csus.plugin.securecodingassistant.rules.IRule;

/**
 * Represents a segment of source code that is potentially insecure. Insecure code segments
 * are identified in {@link SecureCompilationParticipant} and added to a collection for
 * tracking. An <code>InsecureCodeSegment</code> keeps track of the rule that was violated
 * and also handles adding an {@link IMarker} in the development environment to alert the user.
 * @author Ben White
 *
 * @see SecureCompilationParticipant
 * @see IRule
 */
public class InsecureCodeSegment {
	
	/**
	 * The rule that was violated in this segment of code
	 */
	private IRule m_ruleViolated;
	
	/**
	 * The node in the abstract syntax tree where the code is located
	 */
	private ASTNode m_node;
	
	/**
	 * A marker in the development environment that alerts the programmer
	 * that they have an insecure segment of code
	 */
	private IMarker m_marker;
	
	/**
	 * Create new insecure code segment at given node that violates a rule.
	 * @param node The AST node where the rule was violated
	 * @param rule The rule that was violated
	 * @param context The reconcile context, this is needed to create a marker
	 * 			at the code location
	 */
	public InsecureCodeSegment(ASTNode node, IRule rule, ReconcileContext context) {
		int start, end, line;
		IResource resource;
		start = node.getStartPosition();
		end = start + node.getLength();
	
		m_ruleViolated = rule;
		m_node = node;
		
		try {
			resource =  context.getDelta().getElement().getUnderlyingResource();
			line = context.getAST8().getLineNumber(start - 1);
			
			m_marker = resource.createMarker(IMarker.PROBLEM);
			m_marker.setAttribute(IMarker.MESSAGE, m_ruleViolated.getRuleText());
			m_marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
			m_marker.setAttribute(IMarker.LINE_NUMBER, line);
			m_marker.setAttribute(IMarker.CHAR_START, start);
			m_marker.setAttribute(IMarker.CHAR_END, end);
			m_marker.setAttribute(IMarker.LOCATION, String.format("line %d", line));
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * The rule that was violated in the code segment
	 * @return The violated rule
	 */
	public IRule getRule() {
		return m_ruleViolated;
	}
	
	/**
	 * The node in the AST that has the rule violation
	 * @return The node with the rule violation
	 */
	public ASTNode getNode() {
		return m_node;
	}
}
