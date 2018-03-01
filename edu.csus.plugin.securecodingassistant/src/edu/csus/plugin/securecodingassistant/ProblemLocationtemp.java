package edu.csus.plugin.securecodingassistant;

import org.eclipse.cdt.codan.core.model.AbstractProblemLocation;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;

public class ProblemLocationtemp extends AbstractProblemLocation {

	public ProblemLocationtemp(IFile file, int line) {
		super(file, line);
		// TODO Auto-generated constructor stub
	}

	public ProblemLocationtemp(IResource file, int line) {
		super(file, line);
		// TODO Auto-generated constructor stub
	}

	public ProblemLocationtemp(IFile file, int startChar, int endChar) {
		super(file, startChar, endChar);
		// TODO Auto-generated constructor stub
	}

	public ProblemLocationtemp(IResource file, int startChar, int endChar) {
		super(file, startChar, endChar);
		// TODO Auto-generated constructor stub
	}

}
