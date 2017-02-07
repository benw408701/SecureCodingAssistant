package edu.csus.plugin.securecodingassistant.compilation;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.State;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.CompilationParticipant;
import org.eclipse.jdt.core.compiler.ReconcileContext;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

import edu.csus.plugin.securecodingassistant.Globals;
import edu.csus.plugin.securecodingassistant.rules.IRule;
import edu.csus.plugin.securecodingassistant.rules.InvariantCheck;
import edu.csus.plugin.securecodingassistant.rules.PostConditionCheck;
import edu.csus.plugin.securecodingassistant.rules.PreConditionCheck;
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
		
		m_rules = RuleFactory.getAllCERTRules();
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
	@SuppressWarnings("deprecation")
	@Override
	public void reconcile(ReconcileContext context) {
		// Call Parent
		super.reconcile(context);
		
		// Check to see if AST has changed
		IJavaElementDelta elementDelta = context.getDelta();
		if(elementDelta != null &&
				(elementDelta.getFlags() & IJavaElementDelta.F_AST_AFFECTED) != 0) {
			CompilationUnit compilation = null;
			IResource resource = context.getWorkingCopy().getResource();
			try {
				// AST8 fails to properly recognize modifiers for some types
				compilation = context.getAST4();
			} catch (JavaModelException e) {
				// From context.getAST4()
				e.printStackTrace();
			}
			
			// Clear all existing markers
			clearMarkers(resource);
			
			// Empty all existing solutions
			Globals.RULE_SOLUTIONS.clear();
			
			if (isContractCheckDisable(compilation)) {
				removeContractRule();
				
			} else {
				addContractRule();
			}
			
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
	
	private void addContractRule() {
		Iterator<IRule> itor = m_rules.iterator();
		boolean containContractRule = false;
		while(itor.hasNext()) {
			IRule rule = itor.next();
			if (rule instanceof PostConditionCheck || rule instanceof PreConditionCheck || rule instanceof InvariantCheck) {
				containContractRule = true;
				break;
			}
		}	
		if (!containContractRule) {
			m_rules.addAll(RuleFactory.getAllContractRules());
		}
	}

	private void removeContractRule() {
		Iterator<IRule> itor = m_rules.iterator();
		while(itor.hasNext()) {
			IRule rule = itor.next();
			if (rule instanceof PostConditionCheck || rule instanceof PreConditionCheck || rule instanceof InvariantCheck) {
				itor.remove();
			}
		}
	}

	private boolean isContractCheckDisable(CompilationUnit compilation) {
		ICommandService service = (ICommandService)PlatformUI.getWorkbench().getService(ICommandService.class);
		Command command = service.getCommand("edu.csus.plugin.securecodingassistant.enablecontractchecking");
		State state = command.getState("org.eclipse.ui.commands.toggleState");
		boolean isDisable =  (boolean) state.getValue();
		return isDisable;
	}

	private void clearMarkers(IResource resource) {
		Iterator<InsecureCodeSegment> csItr = m_insecureCodeSegments.iterator();
		while (csItr.hasNext()) {
			InsecureCodeSegment cs = csItr.next();
			try {
				if(cs.getResource().equals(resource)) {
					cs.deleteMarker();
					csItr.remove();
				}
			} catch (CoreException e) {
				// Couldn't delete marker
				e.printStackTrace();
			}
		}
	}
}
