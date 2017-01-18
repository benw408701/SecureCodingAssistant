package edu.csus.plugin.securecodingassistant.compilation;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.compiler.ReconcileContext;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

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
	 * The resource where the marker is stored
	 */
	private IResource m_resource;
	
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
		String message_note = String.format("NOTE: The text and/or code below is from the "
				+ "CERT website https://www.securecoding.cert.org%n%n");
		
		//Change start and end position if node break precondition/postcondition/invariant check
		if (rule.getRuleName().equals(Globals.RuleNames.POSTCONDITION_CHECK)
				|| rule.getRuleName().equals(Globals.RuleNames.PRECONDITION_CHECK)) {
			MethodDeclaration md = (MethodDeclaration) node;
			start = md.getName().getStartPosition();
			end = md.getBody().getStartPosition();
			message_note = "";
		} else if (rule.getRuleName().equals(Globals.RuleNames.INVARIANT_CHECK)) {
			TypeDeclaration td = (TypeDeclaration) node;
			start = td.getName().getStartPosition();
			end = start + td.getName().getLength();
			message_note = "";
		}
		
		String severity = "";
		
		switch (rule.securityLevel()) {
		case Globals.Markers.SECURITY_LEVEL_HIGH:
			severity = "High";
			break;
		case Globals.Markers.SECURITY_LEVEL_MEDIUM:
			severity = "Medium";
			break;
		case Globals.Markers.SECURITY_LEVEL_LOW:
			severity = "Low";
			break;
		}
	
		try {
			IResource resource = context.getWorkingCopy().getResource();
			m_resource = resource;

			line = context.getAST8().getLineNumber(start - 1);
			
			m_marker = resource.createMarker(Globals.Markers.SECURE_MARKER);
			m_marker.setAttribute(IMarker.MESSAGE, String.format(
					"Rule violated: %s%nSeverity: %s%n%n%s" + "Rule description: %s%n" + "Rule Solution: %s",
					rule.getRuleName(), severity, message_note, rule.getRuleText(), rule.getRuleRecommendation()));
			m_marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
			m_marker.setAttribute(Globals.Markers.SECURITY_LEVEL, rule.securityLevel());
			m_marker.setAttribute(IMarker.LINE_NUMBER, line);
			m_marker.setAttribute(IMarker.CHAR_START, start);
			m_marker.setAttribute(IMarker.CHAR_END, end);
			m_marker.setAttribute(IMarker.LOCATION, String.format("line %d", line));
			m_marker.setAttribute(Globals.Markers.VIOLATED_RULE, rule.getRuleName());
			m_marker.setAttribute(Globals.Markers.HASHCODE, node.hashCode());
			m_marker.setAttribute(Globals.Markers.RULE_ID, rule.getRuleID());

			// Show high security levels as errors, lower as warnings
			if(rule.securityLevel() == Globals.Markers.SECURITY_LEVEL_HIGH)
				m_marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			else
				m_marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
				
			System.out.printf("In %s, violated rule: %s%n", this.toString(),
					m_marker.getAttribute(Globals.Markers.VIOLATED_RULE));
		} catch (CoreException e) {
			// From createMarker(), setAttribute()
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the resource in which the marker is stored
	 * @return The resource in which the marker is stored
	 */
	public IResource getResource() {
		return m_resource;
	}
	
	/**
	 * Deletes the associated marker, must call this to remove the marker from the IDE
	 * @throws CoreException If the marker could not be deleted
	 */
	public void deleteMarker() throws CoreException {
		m_marker.delete();
	}
}
