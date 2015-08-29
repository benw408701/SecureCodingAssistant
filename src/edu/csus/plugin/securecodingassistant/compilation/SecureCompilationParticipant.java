package edu.csus.plugin.securecodingassistant.compilation;

import java.util.ArrayList;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.CompilationParticipant;
import org.eclipse.jdt.core.compiler.ReconcileContext;
import org.eclipse.jdt.core.dom.CompilationUnit;

import edu.csus.plugin.securecodingassistant.rules.IDS01J_NormalizeStringsBeforeValidating;
import edu.csus.plugin.securecodingassistant.rules.IDS07J_RuntimeExecMethod;
import edu.csus.plugin.securecodingassistant.rules.IRule;

/**
 * Uses the {@link NodeVisitor} to check each {@link org.eclipse.jdt.core.dom.ASTNode} to see if
 * an {@link IRule} has been violated. Overrides the {@link CompilationParticipant#reconcile(ReconcileContext)}
 * method. Adds to an {@link InsecureCodeSegment} list.
 * @author Ben White
 * @see NodeVisitor
 * @see IRule
 * @see InsecureCodeSegment
 */
public class SecureCompilationParticipant extends CompilationParticipant {
	
	/**
	 * Collection of rules to be tested when {@link #reconcile(ReconcileContext)} is called
	 */
	private ArrayList<IRule> m_rules;
	
	/**
	 * Collection of insecure code segments that have been detected
	 */
	private ArrayList<InsecureCodeSegment> m_insecureCodeSegments;
	
	/**
	 * Creates new <code>SecureCompilationParticipant</code>
	 */
	public SecureCompilationParticipant() {
		super();
		
		m_rules = new ArrayList<IRule>();
		m_insecureCodeSegments = new ArrayList<InsecureCodeSegment>();
		
		m_rules.add(new IDS01J_NormalizeStringsBeforeValidating());
		m_rules.add(new IDS07J_RuntimeExecMethod());
	}
	
	/**
	 * Always returns true
	 * @param project the <code>IJavaProject</code> that is being compiled 
	 * @return Always returns <code>true</code>
	 */
	public boolean isActive(IJavaProject project) {
		return true;
	}
	
	/**
	 * Overridden <code>reconcile()</code> method that creates the {@link NodeVisitor} to scan
	 * through the abstract syntax tree and look for secure code rule violations
	 * @param context The <code>ReconcileContext</code> that is being reconciled
	 */
	@Override
	public void reconcile(ReconcileContext context) {
		// Call Parent
		super.reconcile(context);
		
		// Check to see if content has changed
		IJavaElementDelta elementDelta = context.getDelta();
		IJavaElement element = elementDelta.getElement();
		if((elementDelta.getFlags() & IJavaElementDelta.F_CONTENT) != 0) {
			System.err.printf("The content of %s has changed%n",
					element.getElementName());
			
			// Check to see if any insecure code segments have been fixed
			for (InsecureCodeSegment cs : m_insecureCodeSegments) {
				if (!cs.getRule().violated(cs.getNode()))
					m_insecureCodeSegments.remove(cs);
			}
			
			try {
				CompilationUnit compilation = context.getAST8();
				
				// Create a new NodeVisitor to go through the AST and look for violated rules
				NodeVisitor visitor = new NodeVisitor(m_rules, m_insecureCodeSegments, context);
				compilation.accept(visitor);
				
				// Update insecure code segment list
				ArrayList<InsecureCodeSegment> newInsecureCodeSegments = visitor.getNewInsecureCodeSegments();
				if (newInsecureCodeSegments != null)
					m_insecureCodeSegments.addAll(newInsecureCodeSegments);
			} catch (JavaModelException e) {
				// From context.getAST8()
				e.printStackTrace();
			}
		
		}

	}
}
