package edu.csus.plugin.securecodingassistant.compilation;

import java.util.ArrayList;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.CompilationParticipant;
import org.eclipse.jdt.core.compiler.ReconcileContext;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;

import edu.csus.plugin.securecodingassistant.rules.IDS01J_NormalizeStringsBeforeValidating;
import edu.csus.plugin.securecodingassistant.rules.IDS07J_RuntimeExecMethod;
import edu.csus.plugin.securecodingassistant.rules.IRule;

public class SecureCompilationParticipant extends CompilationParticipant {
	private ArrayList<IRule> m_rules;
	private ArrayList<InsecureCodeSegment> m_insecureCodeSegments;
	
	public SecureCompilationParticipant() {
		super();
		
		m_rules = new ArrayList<IRule>();
		m_insecureCodeSegments = new ArrayList<InsecureCodeSegment>();
		
		m_rules.add(new IDS01J_NormalizeStringsBeforeValidating());
		m_rules.add(new IDS07J_RuntimeExecMethod());
	}
	
	public boolean isActive(IJavaProject project) {
		return true;
	}
	
	@Override
	public void reconcile(final ReconcileContext context) {
		// Call Parent
		super.reconcile(context);
		
		// Check to see if content has changed
		IJavaElementDelta elementDelta = context.getDelta();
		IJavaElement element = elementDelta.getElement();
		if((elementDelta.getFlags() & IJavaElementDelta.F_CONTENT) != 0) {
			System.out.printf("The content of %s has changed%n",
					element.getElementName());
			
			// Check to see if any insecure code segments have been fixed
			for (InsecureCodeSegment cs : m_insecureCodeSegments) {
				if (!cs.getRule().violated(cs.getNode()))
					m_insecureCodeSegments.remove(cs);
			}
			
			try {
				CompilationUnit compilation = context.getAST8();
				
				// Anonymous class used to avoid storing all nodes in memory and then processing
				compilation.accept(new ASTVisitor() {
					public void preVisit(ASTNode node) {
						// Iterate through rules
						for (IRule rule : m_rules) {
							if(rule.violated(node)) {
								System.out.printf("Rule violated at node %s%n", node.toString());
								boolean capturedCode = false; // True if already found
								for (InsecureCodeSegment cs : m_insecureCodeSegments)
									// Check to see if this code segment is already captured
									if (cs.getNode().getStartPosition() == node.getStartPosition()
										&& cs.getNode().getLength() == node.getLength()
										&& cs.getRule() == rule)
										capturedCode = true;
								if (!capturedCode)
									m_insecureCodeSegments.add(new InsecureCodeSegment(node, rule, context));
							}
						}
					}
				});
			} catch (JavaModelException e) {
				// From context.getAST8()
				e.printStackTrace();
			}
		
		}

	}
}
