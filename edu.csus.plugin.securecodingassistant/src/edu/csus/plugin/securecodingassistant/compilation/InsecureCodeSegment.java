package edu.csus.plugin.securecodingassistant.compilation;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.ReconcileContext;
import org.eclipse.jdt.core.dom.ASTNode;
import edu.csus.plugin.securecodingassistant.Globals;
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
class InsecureCodeSegment {
	
	/**
	 * A marker in the development environment that alerts the programmer
	 * that they have an insecure segment of code
	 */
	private IMarker m_marker;
	
	/**
	 * Create new insecure code segment at given node that violates an {@link IRule}.
	 * @param node The AST node where the {@link IRule} was violated
	 * @param rule The {@link IRule} that was violated
	 * @param context The <code>ReconcileContext</code>, this is needed to create an {@link IMarker}
	 * at the code location
	 */
	public InsecureCodeSegment(ASTNode node, IRule rule, ReconcileContext context) {
		int start, end, line;
		start = node.getStartPosition();
		end = start + node.getLength();
	
		try {
			IResource resource = context.getDelta().getElement().getUnderlyingResource();
		
			line = context.getAST8().getLineNumber(start - 1);
				
			m_marker = resource.createMarker(Globals.Markers.SECURE_MARKER);
			m_marker.setAttribute(IMarker.MESSAGE,
					String.format("Rule violated: %s%n%nRule description: %s%n%n"
							+ "Rule Solution: %s", rule.getRuleName(),
							rule.getRuleText(), rule.getRuleRecommendation()));
			m_marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
			m_marker.setAttribute(IMarker.LINE_NUMBER, line);
			m_marker.setAttribute(IMarker.CHAR_START, start);
			m_marker.setAttribute(IMarker.CHAR_END, end);
			m_marker.setAttribute(IMarker.LOCATION, String.format("line %d", line));
			m_marker.setAttribute(Globals.Markers.VIOLATED_RULE, rule.getRuleName());
			System.out.printf("In %s, violated rule: %s%n", this.toString(),
					m_marker.getAttribute(Globals.Markers.VIOLATED_RULE));
		} catch (JavaModelException e) {
			// From getUnderlyingResource(), getAST8()
			e.printStackTrace();
		} catch (CoreException e) {
			// From createMarker(), setAttribute()
			e.printStackTrace();
		}
	}
	
	/**
	 * Deletes the associated marker, must call this to remove the marker from the IDE
	 * @throws CoreException If the marker could not be deleted
	 */
	public void deleteMarker() throws CoreException {
		m_marker.delete();
	}
}
