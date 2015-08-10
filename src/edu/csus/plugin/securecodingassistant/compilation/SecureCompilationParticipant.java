package edu.csus.plugin.securecodingassistant.compilation;

import java.util.ArrayList;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.compiler.CompilationParticipant;
import org.eclipse.jdt.core.compiler.ReconcileContext;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;

import edu.csus.plugin.securecodingassistant.rules.IDS07J_RuntimeExecMethod;
import edu.csus.plugin.securecodingassistant.rules.IRule;

public class SecureCompilationParticipant extends CompilationParticipant {
	private ArrayList<IRule> m_rules;
	private ArrayList<InsecureCodeSegment> m_insecureCodeSegments;
	
	public SecureCompilationParticipant() {
		super();
		
		m_rules = new ArrayList<IRule>();
		m_insecureCodeSegments = new ArrayList<InsecureCodeSegment>();
		
		m_rules.add(new IDS07J_RuntimeExecMethod());
	}
	
	public boolean isActive(IJavaProject project) {
		return true;
	}
	
	@Override
	public void reconcile(final ReconcileContext context) {
		// Call Parent
		super.reconcile(context);
		
		// Used in expression loop
		//int start, end, line;
		
		// Check to see if content has changed
		IJavaElementDelta elementDelta = context.getDelta();
		IJavaElement element = elementDelta.getElement();
		if((elementDelta.getFlags() & IJavaElementDelta.F_CONTENT) != 0) {
			System.out.printf("The content of %s has changed%n",
					element.getElementName());
			try {
				CompilationUnit compilation = context.getAST8();
				IResource resource = element.getUnderlyingResource();
				//NodeVisitor visitor = new NodeVisitor();
				//compilation.accept(visitor);
				
				compilation.accept(new ASTVisitor() {
					public void preVisit(ASTNode node) {
						// Iterate through rules
						for (IRule rule : m_rules) {
							if(rule.violated(node)) {
								boolean capturedCode = false;
								for (InsecureCodeSegment cs : m_insecureCodeSegments)
									if (cs.getNode() == node && cs.getRule() == rule)
										capturedCode = true;
								if (!capturedCode)
									m_insecureCodeSegments.add(new InsecureCodeSegment(node, rule, context));
							}
						}
					}
				});

				// Iterate through expressions
				/*
				for (ExpressionStatement expressionStatement : visitor.getExpressionStatements()) {
					start = expressionStatement.getStartPosition();
					end = start + expressionStatement.getLength();
					line = compilation.getLineNumber(start - 1);
					System.out.printf("Expression: %s%n", expressionStatement.getExpression().toString());
					
					// Iterate through rules
					for (IRule rule : m_rules) {
						rule.violated(expressionStatement);
					}
					
					
					/*
					// Iterate through existing markers
					boolean newMarker = true;
					for (IMarker marker : m_markers) {
						if (line == marker.getAttribute(IMarker.LINE_NUMBER, -1))
							newMarker = false;
					}
					
					if(newMarker){
						IResource resource = element.getUnderlyingResource();
						IMarker marker = resource.createMarker(IMarker.PROBLEM);
						marker.setAttribute(IMarker.MESSAGE, "This is a test marker");
						marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
						marker.setAttribute(IMarker.LINE_NUMBER, line);
						marker.setAttribute(IMarker.CHAR_START, start);
						marker.setAttribute(IMarker.CHAR_END, end);
						marker.setAttribute(IMarker.LOCATION, String.format("line %d", line));
						m_markers.add(marker);
					}
					
				}
				*/
			} catch (CoreException e) {
				// From createMarker()
				e.printStackTrace();
			}
		}

	}
}
