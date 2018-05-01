package edu.csus.plugin.securecodingassistant.markerresolution_C;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.csus.plugin.securecodingassistant.rules_C.IRule_C;

/**
 * Class holds a collection of insecure nodes that are discovered during the build 
 * processes in {@link SecureCodingAssistantCDTErrorParser}. An InsecureCodeSegment_C collects 
 * the node and the associated rule that was violated. 
 * 
 * @author Victor Melnik
 *
 *@see IRule_C
 *@see SecureCodingAssistantCDTErrorParser
 */
public class InsecureCodeSegment_C {
	
	private IASTNode insecureNode;
	private IRule_C violatedRule;
	private ITranslationUnit violatedITU;
	
	/**
	 * Creates InsecureCodeSegement_C for node that violates rule
	 * @param node
	 * @param rule
	 * @param ITU
	 */
	public InsecureCodeSegment_C(IASTNode node, IRule_C rule, ITranslationUnit ITU)
	{
		insecureNode = node;
		violatedRule = rule;
		violatedITU = ITU;
	}

	/**
	 * Returns node where rule violation occured.
	 * 
	 * @return insecureNode
	 */
	public IASTNode getViolatedNode()
	{
		return insecureNode;
		
	}
	
	/**
	 * Returns violated IRule_C.
	 * 
	 * @return violatedRule
	 */
	public IRule_C getViolatedRule()
	{
		return violatedRule;
	}
	
	/**
	 * Retuns translation unit where node is located.
	 * 
	 * @return violatedITU
	 */
	public ITranslationUnit getviolatedITU()
	{
		return violatedITU;
	}
}
