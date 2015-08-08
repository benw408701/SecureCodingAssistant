package edu.csus.plugin.securecodingassistant.compilation;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.compiler.IProblem;

public class SecureCodingProblem extends CategorizedProblem {
	
	private int m_sourceStart;
	private int m_sourceEnd;
	private int m_sourceLineNumber;
	private char[] m_fileName;
	private IMarker m_marker;

	public SecureCodingProblem(String fileName) {
		m_fileName = fileName.toCharArray();
	}
	
	public void CreateMarker(IResource resource, int line) {
		try {
			m_marker = resource.createMarker(IMarker.PROBLEM);
			m_marker.setAttribute(IMarker.MESSAGE, "This is a test marker");
			m_marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
			m_marker.setAttribute(IMarker.LINE_NUMBER, line);
			m_marker.setAttribute(IMarker.LOCATION, String.format("Line %d", line));
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public String[] getArguments() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return IProblem.AmbiguousConstructor;
	}

	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return "This is a problem";
	}

	@Override
	public char[] getOriginatingFileName() {
		// TODO Auto-generated method stub
		return m_fileName;
	}

	@Override
	public int getSourceEnd() {
		// TODO Auto-generated method stub
		return m_sourceEnd;
	}

	@Override
	public int getSourceLineNumber() {
		// TODO Auto-generated method stub
		return m_sourceLineNumber;
	}

	@Override
	public int getSourceStart() {
		// TODO Auto-generated method stub
		return m_sourceStart;
	}

	@Override
	public int getCategoryID() {
		// TODO Auto-generated method stub
		return CategorizedProblem.CAT_CODE_STYLE;
	}

	@Override
	public String getMarkerType() {
		// TODO Auto-generated method stub
		return IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER;
	}
	
	@Override
	public boolean isError() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isWarning() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void setSourceEnd(int sourceEnd) {
		m_sourceEnd = sourceEnd;

	}

	@Override
	public void setSourceLineNumber(int lineNumber) {
		m_sourceLineNumber = lineNumber;
	}

	@Override
	public void setSourceStart(int sourceStart) {
		m_sourceStart = sourceStart;

	}
}
