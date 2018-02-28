package edu.csus.plugin.securecodingassistant.rules_C;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.csus.plugin.securecodingassistant.Globals;

public class MEM31C_FreeDynamicallyAllocatedMemoryWhenNoLongerNeeded implements IRule_C {

	private boolean ruleViolated;
	private ArrayList<IASTNode> arrListOfFuncCalls = new ArrayList<IASTNode>();
	private Iterator<IASTNode> funcIT;
	
	@Override
	public boolean violate_CDT(IASTNode node) {
		
		ruleViolated = false;
		
		if(/*node.getContainingFilename().contains("MEM31") && */  (node.getFileLocation().getContextInclusionStatement() == null))
		{
			//System.out.println("node: " + node.getRawSignature());
			
			if(node instanceof IASTFunctionCallExpression)
			{
				if(node.getRawSignature().contains("malloc"))
				{
					//System.out.println("IASTFunctionCallExpression MALLOC: " + node.getRawSignature());
					arrListOfFuncCalls.clear();
					funcIT = arrListOfFuncCalls.iterator();
					
					IASTNode parent = node.getParent();
					boolean correctParent = true;
					while(correctParent)
					{
						if((parent.getRawSignature().contains("=")) && !(parent.getRawSignature().startsWith("="))) 
						{
							
							//parent = parent.getParent()
							correctParent = false;
						}
						else
						{
							parent = parent.getParent();
						}
					}
					
					//System.out.println("IASTFunctionCallExpression MALLOC Parent:" + parent.getRawSignature());
					//System.out.println("IASTFunctionCallExpression MALLOC: " + ((IASTFunctionCallExpression) node).FUNCTION_NAME.toString());
					
					//get the variable name of the LHS arguement of malloc call
					String varNameLHS = null;
					if(parent.getRawSignature().startsWith("**"))
					{
						//System.out.println("Child0: " + parent.getChildren()[0].getRawSignature());
						//System.out.println("Child1: " + parent.getChildren()[1].getRawSignature());
						//System.out.println("Child2: " + parent.getChildren()[2].getRawSignature());
						varNameLHS = parent.getChildren()[2].getRawSignature();
					}
					else if(parent.getRawSignature().startsWith("*"))
					{

						//System.out.println("Child0: " + parent.getChildren()[0].getRawSignature());
						//System.out.println("Child1: " + parent.getChildren()[1].getRawSignature());
						varNameLHS = parent.getChildren()[1].getRawSignature();
					}
					else
					{
						//System.out.println("Child0: " + parent.getChildren()[0].getRawSignature());
						varNameLHS = parent.getChildren()[0].getRawSignature();
					}
					
					parent = node;
					
					while(!(parent instanceof IASTFunctionDefinition))
					{
						parent = parent.getParent();
					}
					//System.out.println("IASTFunctionDefinition MALLOC Parent:" + parent.getRawSignature());
					
					/*
					if(parent instanceof IASTDeclaration)
					{
						System.out.println("IASTDeclaration MALLOC Parent:" + parent.getRawSignature());
					}
					*/
					//System.out.println("varNameLHS: " + varNameLHS);
					ASTVisitorFindMatch visitor = new ASTVisitorFindMatch(null,"MEM31C_Free");
					
					parent.accept(visitor);
					
					arrListOfFuncCalls = visitor.arrayListofFunctions();
					funcIT = arrListOfFuncCalls.iterator();
					if(visitor.nodeParent() == null)
					{
						//System.out.println("RULE VIOLATED");
						ruleViolated = true;
					}
					else
					{
						//System.out.println("nodeParentfrom Visitor: " + visitor.nodeParent().getRawSignature());
						while(funcIT.hasNext())
						{
							ASTVisitorFindMatch visitor1 = new ASTVisitorFindMatch(varNameLHS, "FindMatch");
							funcIT.next().accept(visitor1);
						
							if(!(visitor1.isMatch()))
							{
								//System.out.println("Rule_Violated");
								ruleViolated = true;
								//break;
							}
							else
							{
								ruleViolated = false;
								break;
							}
						}
					}
					//System.out.println("NEW NODE ANALYSIS\n" );
				}
			}
		}
		
		
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		
		return "CERT Website- Before the lifetime of the last"
				+ " pointer that stores the return value of a"
				+ " call to a standard memory allocation "
				+ "function has ended, it must be matched by"
				+ " a call to free() with that pointer value.";
	}

	@Override
	public String getRuleName() {
		
		return Globals.RuleNames.MEM31_C;
	}

	@Override
	public String getRuleID() {
		
		return Globals.RuleID.MEM31_C;
	}

	@Override
	public String getRuleRecommendation() {
		
		return "Deallocated dynamically allocated memory with"
				+ "a call to free()";
	}

	@Override
	public int securityLevel() {
		
		return Globals.Markers.SECURITY_LEVEL_MEDIUM;
	}

	@Override
	public String getRuleURL() {
		
		return "https://wiki.sei.cmu.edu/confluence/display/c/MEM31-C.+Free+dynamically+allocated+memory+when+no+longer+needed";
	}

	@Override
	public ITranslationUnit getITranslationUnit() {
		// TODO Auto-generated method stub
		return null;
	}

}
