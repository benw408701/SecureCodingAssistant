package edu.csus.plugin.securecodingassistant.compilation;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.compiler.CompilationParticipant;
import org.eclipse.jdt.core.compiler.ReconcileContext;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionStatement;

public class SecureCompilationParticipant extends CompilationParticipant {

	public boolean isActive(IJavaProject project) {
		return true;
	}
	
	@Override
	public void reconcile(ReconcileContext context) {
		// Call Parent
		super.reconcile(context);
		
		CategorizedProblem[] currentProblems = context.getProblems(IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER);
		if (currentProblems != null)
			for (CategorizedProblem currentProblem : currentProblems) {
				JOptionPane.showMessageDialog(null, String.format("Problem ID %n, Message %s",
						currentProblem.getID(), currentProblem.getMessage()));
			}
		
		// Used in expression loop
		int start, end, line;
		String fileName;
		
		// Check to see if content has changed
		IJavaElementDelta elementDelta = context.getDelta();
		IJavaElement element = elementDelta.getElement();
		if((elementDelta.getFlags() & IJavaElementDelta.F_CONTENT) != 0) {
			System.out.printf("The content of %s has changed%n",
					element.getElementName());
			try {
				CompilationUnit compilation = context.getAST8();
				NodeVisitor visitor = new NodeVisitor();
				compilation.accept(visitor);

				// Iterate through expressions
				for (ExpressionStatement expressionStatement : visitor.getExpressionStatements()) {
					start = expressionStatement.getStartPosition();
					end = start + expressionStatement.getLength();
					line = compilation.getLineNumber(start - 1);
					fileName = element.getElementName();
					System.out.printf("Expression: %s%n", expressionStatement.getExpression().toString());
					
					CategorizedProblem[] problems = new CategorizedProblem[0];
					ArrayList<CategorizedProblem> problemList = new ArrayList<CategorizedProblem>();
					
					// Put problems
					/*
					SecureCodingProblem problem	= new SecureCodingProblem(fileName);
					problem.setSourceStart(start);
					problem.setSourceEnd(end);
					problem.setSourceLineNumber(line);
					//problem.CreateMarker(element.getUnderlyingResource(), line);
					problemList.add(problem);
					context.putProblems(IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER, problemList.toArray(problems));
					*/
					
					IResource resource = element.getUnderlyingResource();
					IMarker marker = resource.createMarker(IMarker.PROBLEM);
					marker.setAttribute(IMarker.MESSAGE, "This is a test marker");
					marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
					marker.setAttribute(IMarker.LINE_NUMBER, line);
					marker.setAttribute(IMarker.CHAR_START, start);
					marker.setAttribute(IMarker.CHAR_END, end);
					marker.setAttribute(IMarker.LOCATION, String.format("Line %d", line));
				}
			} catch (JavaModelException e) {
				// From CompilationUnit compilation = context.getAST8();
				e.printStackTrace();
			} catch (CoreException e) {
				// From createMarker()
				e.printStackTrace();
			}
		}

	}
}
