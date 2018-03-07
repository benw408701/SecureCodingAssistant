package edu.csus.plugin.securecodingassistant.rules_C;

import java.util.ArrayList;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.model.ITranslationUnit;
import edu.csus.plugin.securecodingassistant.Globals;
import edu.csus.plugin.securecodingassistant.markerresolution_C.InsecureCodeSegment_C;


public class SecureCodeNodeVisitor_C extends ASTVisitor{

	private IASTNode node;
	private ArrayList<IRule_C> c_rules;
	private ITranslationUnit localITU;
	
	/**
	 * Visit all nodes in ASTTranslationUnit to detect insecure segments
	 */
	
	public SecureCodeNodeVisitor_C(ArrayList<IRule_C> rules, ITranslationUnit ITU)
	{
		
		this.shouldVisitStatements = true;
		this.shouldVisitDeclarations = true;
		this.shouldVisitExpressions = true;
		this.shouldVisitAmbiguousNodes = true;
		this.shouldVisitStatements = true;
		this.shouldVisitParameterDeclarations = true;
		
		c_rules = rules;
		localITU = ITU;
		
	}
	
	public int visit(IASTStatement stam)
	{
		node = stam.getOriginalNode();	
		traverseRule(node);
		
		return PROCESS_CONTINUE;
		
	}
	
	public int visit(IASTParameterDeclaration parameterDeclaration)
	{
		node = parameterDeclaration.getOriginalNode();
		
		traverseRule(node);
		return PROCESS_CONTINUE;
	}
	
	public int visit(IASTDeclaration dec)
	{
		node = dec.getOriginalNode();
		
		if(!node.getRawSignature().startsWith("typdef"))
		{
			traverseRule(node);
		}
		//continues traversing the astTree
		return PROCESS_CONTINUE;
	}
	
	
	public int visit(IASTExpression fce)
	{
		node = fce.getOriginalNode();
		
		traverseRule(node);
		//continues traversing the astTree
		return PROCESS_CONTINUE;
	}
	
	
	
	public void traverseRule(IASTNode checkNode)
	{
		for (IRule_C rule : c_rules)
		{
			if(rule.violate_CDT(checkNode))
			{
				Globals.insecureGlobalNode = checkNode;
				Globals.cdt_InsecureCodeSegments.add(new InsecureCodeSegment_C(checkNode,rule, localITU));
			}
		}
	}
}
