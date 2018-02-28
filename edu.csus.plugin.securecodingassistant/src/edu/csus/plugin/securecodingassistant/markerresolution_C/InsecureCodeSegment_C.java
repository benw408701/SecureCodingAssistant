package edu.csus.plugin.securecodingassistant.markerresolution_C;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.csus.plugin.securecodingassistant.rules_C.IRule_C;

public class InsecureCodeSegment_C {
	
	private IASTNode insecureNode;
	private IRule_C violatedRule;
	private ITranslationUnit violatedITU;
	
	public InsecureCodeSegment_C(IASTNode node, IRule_C rule, ITranslationUnit ITU)
	{
		insecureNode = node;
		violatedRule = rule;
		violatedITU = ITU;
	}

	
	public IASTNode getViolatedNode()
	{
		return insecureNode;
		
	}
	
	public IRule_C getViolatedRule()
	{
		return violatedRule;
	}
	
	public ITranslationUnit getviolatedITU()
	{
		return violatedITU;
	}
}
