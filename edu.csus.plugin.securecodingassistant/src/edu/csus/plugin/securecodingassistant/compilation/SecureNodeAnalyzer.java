package edu.csus.plugin.securecodingassistant.compilation;

import java.util.ArrayList;
import org.eclipse.jdt.core.compiler.ReconcileContext;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
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
class SecureNodeAnalyzer extends ASTVisitor {
	
	/**
	 * A collection of rules to check for
	 */
	private ArrayList<IRule> m_rules;
	
	/**
	 * A collection of new <code>InseucreCodeSegment</code> objects. This collection is
	 * added to each time a new insecure section of code is identified.
	 */
	private ArrayList<InsecureCodeSegment> m_insecureCodeSegments;
	
	/**
	 * The <code>ReconcileContext</code> where new violations will be created.
	 */
	private ReconcileContext m_context;
	
	/**
	 * Creates a new <code>NodeVisitor</code>. You must call this constructor and not the default
	 * constructor. The <code>rules</code>, <code>insecureCodeSegments</code>, and <code>context</code>
	 * are required to scan for new insecure code.
	 * @param rules A list of rules to look for
	 * @param context The <code>ReconcileContext</code> where new violations will be created.
	 */
	public SecureNodeAnalyzer(ArrayList<IRule> rules, 
			ReconcileContext context) {
		super();
		
		m_rules = rules;
		m_insecureCodeSegments = new ArrayList<InsecureCodeSegment>();
		m_context = context;
	}
	
	/**
	 * Visits each node in the abstract syntax tree and
	 * adds to the <code>InsecureCodeSegment</code> collection that is returned by
	 * {@link SecureNodeAnalyzer#getInsecureCodeSegments()}
	 */
	@Override
	public void preVisit(ASTNode node) {
		// Iterate through rules
		for (IRule rule : m_rules)
			if(rule.violated(node))				
				m_insecureCodeSegments.add(new InsecureCodeSegment(node, rule, m_context));
	}
	
	/**
	 * Returns a lit of all of the insecure code segments that were detected after visiting
	 * all of the nodes in the abstract syntax tree.
	 * @return Collection of <code>InsecureCodeSegment</code> objects
	 */
	public ArrayList<InsecureCodeSegment> getInsecureCodeSegments() {
		return m_insecureCodeSegments;
	}
}
