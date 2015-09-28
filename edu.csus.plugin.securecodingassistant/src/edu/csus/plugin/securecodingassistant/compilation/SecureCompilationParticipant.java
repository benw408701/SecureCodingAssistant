package edu.csus.plugin.securecodingassistant.compilation;

import java.util.ArrayList;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.CompilationParticipant;
import org.eclipse.jdt.core.compiler.ReconcileContext;
import org.eclipse.jdt.core.dom.CompilationUnit;
import edu.csus.plugin.securecodingassistant.rules.IRule;
import edu.csus.plugin.securecodingassistant.rules.RuleFactory;

/**
 * Uses the {@link SecureNodeAnalyzer} to check each {@link org.eclipse.jdt.core.dom.ASTNode} to see if
 * an {@link IRule} has been violated. Overrides the {@link CompilationParticipant#reconcile(ReconcileContext)}
 * method. Adds to an {@link InsecureCodeSegment} list.
 * @author Ben White
 * @see SecureNodeAnalyzer
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
		
		m_rules = RuleFactory.getAllRules();
		m_insecureCodeSegments = new ArrayList<InsecureCodeSegment>();
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
	 * Overridden <code>reconcile()</code> method that creates the {@link SecureNodeAnalyzer} to scan
	 * through the abstract syntax tree and look for secure code rule violations
	 * @param context The <code>ReconcileContext</code> that is being reconciled
	 */
	@Override
	public void reconcile(ReconcileContext context) {
		// Call Parent
		super.reconcile(context);
		
		// Check to see if content has changed
		IJavaElementDelta elementDelta = context.getDelta();
		if((elementDelta.getFlags() & IJavaElementDelta.F_CONTENT) != 0) {
			CompilationUnit compilation = null;
			try {			
				compilation = context.getAST8();
			} catch (JavaModelException e) {
				// From context.getAST8()
				e.printStackTrace();
			}
			
			// Clear all existing markers
			clearMarkers();
			
			if (compilation != null) {
				// Create a new NodeVisitor to go through the AST and look for violated rules
				SecureNodeAnalyzer visitor = new SecureNodeAnalyzer(m_rules, context);
				compilation.accept(visitor);
				
				// Update insecure code segment list
				if (visitor != null) {
					ArrayList<InsecureCodeSegment> newInsecureCodeSegments = visitor.getInsecureCodeSegments();
					m_insecureCodeSegments.addAll(newInsecureCodeSegments);
				}
			}
		
		}

	}
	
	
	private void clearMarkers() {
		for (InsecureCodeSegment cs : m_insecureCodeSegments) {
			try {
				cs.getMarker().delete();
			} catch (CoreException e) {
				// Couldn't delete marker
				e.printStackTrace();
			}
		}
		
		m_insecureCodeSegments.clear();
	}
}
