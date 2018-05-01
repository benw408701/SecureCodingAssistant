package edu.csus.plugin.securecodingassistant.rules_C;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;

/**
 * ASTVisitor used to find matching variable names in a given node.
 * This help with rule detection logic.
 * @author Victor Melnik
 *
 *@see IRule_C
 */
public class ASTVisitorFindMatch extends ASTVisitor{

	private boolean matchFound = false;
	private IASTNode node;
	private String findKeyWord;
	private String findRuleName;
		
	private int dupInExpression;
	private boolean duplicateInExpression;
	
	private boolean isStringFunc = false;
	private boolean isWideStringFunc = false;
	
	public ASTVisitorFindMatch(String keyWord, String ruleName)
	{
		
		this.shouldVisitStatements = true;
		this.shouldVisitDeclarations = true;
		this.shouldVisitExpressions = true;
		this.shouldVisitAmbiguousNodes = true;
		this.shouldVisitStatements = true;
		this.shouldVisitParameterDeclarations = true;
		findKeyWord = keyWord;
		findRuleName = ruleName;
		matchFound = false;
		
		//CONC40_C
		dupInExpression = 0;
		duplicateInExpression = false;
		
		//STR_38C
		isStringFunc = false;
		isWideStringFunc = false;
		
	}
	
	public int visit(IASTStatement stam)
	{
		node = stam.getOriginalNode();
		
		
		return PROCESS_CONTINUE;
			
		
	}
	
	/**
	 * Visits all IASTStatement nodes
	 */
	public int visit(IASTParameterDeclaration param)
	{
		
		node = param.getOriginalNode();
		
		if(findRuleName.contentEquals("FindMatch") && (node.getFileLocation().getContextInclusionStatement() == null))
		{
			if(node.getRawSignature().contentEquals(findKeyWord))
			{
				matchFound = true;
			
				return PROCESS_ABORT;
			}
		}
		
		return PROCESS_CONTINUE;
	}
	
	public int visit(IASTDeclaration dec)
	{
		node = dec.getOriginalNode();
		
		/**
		 * Check to see if function is from <string.h> or <wchar.h> library
		 */
		if(findRuleName.contentEquals("STR38_Include"))
		{
			if(node.getFileLocation().getContextInclusionStatement() != null)
			{
				if(node.getFileLocation().getContextInclusionStatement().getRawSignature().contentEquals("#include <string.h>") 
						&& (node.getRawSignature().contains(findKeyWord)))
				{
					isStringFunc = true;
					return PROCESS_ABORT;
				}
				else if(node.getFileLocation().getContextInclusionStatement().getRawSignature().contentEquals("#include <wchar.h>")
						&& (node.getRawSignature().contains("("+findKeyWord)))
				{
					isWideStringFunc = true;
					return PROCESS_ABORT;
				}
			}
		}

		return PROCESS_CONTINUE;
	}
	
	
	public int visit(IASTExpression fce)
	{
		node = fce.getOriginalNode();
		
		if((findRuleName.contentEquals("findDup")))
		{
			if(node.getRawSignature().contentEquals(findKeyWord))
			{
				dupInExpression++;
				
				if(dupInExpression > 1)
				{
					duplicateInExpression = true;
				}
			}
		}
		
		if(findRuleName.contentEquals("FindMatch") && (node.getFileLocation().getContextInclusionStatement() == null))
		{
			if(node.getRawSignature().contentEquals(findKeyWord))
			{
				matchFound = true;
				return PROCESS_ABORT;
			}
		}
		
		return PROCESS_CONTINUE;
	}
	
	public int visit(IASTName name) 
	{
		node = name.getOriginalNode();
		
		if(findRuleName.contentEquals("FindMatch") && (node.getFileLocation().getContextInclusionStatement() == null))
		{
			if(node.getRawSignature().contentEquals(findKeyWord))
			{
				matchFound = true;
				return PROCESS_ABORT;
			}
		}
		return PROCESS_CONTINUE;
	}
	
	
	public boolean isMatch()
	{
		return matchFound;
	}
	
	//CONC40 Rule
	public boolean isDuplicateInExpression()
	{
		return duplicateInExpression;
	}
	
	//STR38 Rule
	public boolean isStringFunction()
	{
		return isStringFunc;
	}
	
	public boolean isWideStringFunction()
	{
		return isWideStringFunc;
	}
}
