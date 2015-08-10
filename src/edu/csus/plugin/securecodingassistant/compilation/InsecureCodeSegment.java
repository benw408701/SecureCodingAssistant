package edu.csus.plugin.securecodingassistant.compilation;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.compiler.ReconcileContext;
import org.eclipse.jdt.core.dom.ASTNode;

import edu.csus.plugin.securecodingassistant.rules.IRule;

public class InsecureCodeSegment {
	private IRule m_ruleViolated;
	private ASTNode m_codeLocation;
	private IMarker m_marker;
	
	public InsecureCodeSegment(ASTNode node, IRule rule, ReconcileContext context) {
		int start, end, line;
		IResource resource;
		start = node.getStartPosition();
		end = start + node.getLength();
	
		m_ruleViolated = rule;
		m_codeLocation = node;
		
		try {
			resource =  context.getDelta().getElement().getUnderlyingResource();
			line = context.getAST8().getLineNumber(start - 1);
			
			m_marker = resource.createMarker(IMarker.PROBLEM);
			m_marker.setAttribute(IMarker.MESSAGE, "This is a test marker");
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
	
	public IRule getRule() {
		return m_ruleViolated;
	}
	
	public ASTNode getNode() {
		return m_codeLocation;
	}
}
