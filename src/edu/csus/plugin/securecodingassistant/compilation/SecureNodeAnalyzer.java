/**
 * 
 */
package edu.csus.plugin.securecodingassistant.compilation;

import java.util.ArrayList;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.compiler.ReconcileContext;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;

import edu.csus.plugin.securecodingassistant.Globals;
import edu.csus.plugin.securecodingassistant.rules.IRule;

/**
 * A custom <code>ASTVisitor</code> for the Secure Coding Assistant that checks each {@link ASTNode} on the
 * AST (Abstract Syntax Tree) to see if there has been a rule violation. This node visitor
 * must be given a list of {@link IRule} rules to check for as well as the existing list of the
 * {@link InsecureCodeSegment} objects to prevent the creation of duplicate violations.
 * @author Ben White
 * @see IRule
 * @see SecureCompilationParticipant
 * @see InsecureCodeSegment
 */
public class SecureNodeAnalyzer extends ASTVisitor {
	
	/**
	 * A collection of rules to check for
	 */
	private ArrayList<IRule> m_rules;
	
	/**
	 * A collection of existing <code>InsecureCodeSegment</code> objects that already
	 * exist. These are needed to prevent duplicates from being created
	 */
	private ArrayList<InsecureCodeSegment> m_existingInsecureCodeSegments;
	
	/**
	 * A collection of new <code>InseucreCodeSegment</code> objects. This collection is
	 * added to each time a new insecure section of code is identified.
	 */
	private ArrayList<InsecureCodeSegment> m_newInsecureCodeSegments;
	
	/**
	 * The <code>ReconcileContext</code> where new violations will be created.
	 */
	private ReconcileContext m_context;
	
	/**
	 * Creates a new <code>NodeVisitor</code>. You must call this constructor and not the default
	 * constructor. The <code>rules</code>, <code>insecureCodeSegments</code>, and <code>context</code>
	 * are required to scan for new insecure code.
	 * @param rules A list of rules to look for
	 * @param insecureCodeSegments A list of insecure code segments that already exist
	 * @param context The <code>ReconcileContext</code> where new violations will be created.
	 */
	public SecureNodeAnalyzer(ArrayList<IRule> rules, 
			ArrayList<InsecureCodeSegment> insecureCodeSegments,
			ReconcileContext context) {
		super();
		
		m_rules = rules;
		m_existingInsecureCodeSegments = insecureCodeSegments;
		m_newInsecureCodeSegments = new ArrayList<InsecureCodeSegment>();
		m_context = context;
	}
	
	/**
	 * Visits each node in the abstract syntax tree and
	 * adds to the <code>InsecureCodeSegment</code> collection that is returned by
	 * {@link SecureNodeAnalyzer#getNewInsecureCodeSegments()}
	 */
	@Override
	public void preVisit(ASTNode node) {
		// Iterate through rules
		for (IRule rule : m_rules) {
			if(rule.violated(node)) {
				System.out.printf("In %s, rule violated at node %s%n", this.toString(), node.toString());
				boolean capturedCode = false; // True if already found
				for (InsecureCodeSegment cs : m_existingInsecureCodeSegments) {
					try {
						IMarker existingMarker = cs.getResource().findMarker(cs.getMarker().getId());
						if (existingMarker != null &&
								// See if the existing marker is for the same rule violation
								existingMarker.getAttribute(Globals.Markers.VIOLATED_RULE) == rule.getRuleName()) {
							capturedCode = true;
							break;
						}
					} catch (CoreException e) {
						// From findMarker()
						e.printStackTrace();
					}

				}
				if (!capturedCode)
					m_newInsecureCodeSegments.add(new InsecureCodeSegment(node, rule, m_context));
			}
		}
	}
	
	/**
	 * Returns a lit of all of the insecure code segments that were detected after visiting
	 * all of the nodes in the abstract syntax tree.
	 * @return Collection of <code>InsecureCodeSegment</code> objects
	 */
	public ArrayList<InsecureCodeSegment> getNewInsecureCodeSegments() {
		return m_newInsecureCodeSegments;
	}
}
