package edu.csus.plugin.securecodingassistant.rules_C;

import java.util.ArrayList;

import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

/**
 * Collection of utility methods used by the Secure Coding Assistant Rules for C
 * @author Victor Melnik
 *
 */
public final class Utility_C {

	/**
	 * Cannot instantiate
	 */
	private Utility_C() {
	}
	
	
	public static IASTNode getScope(IASTNode node)
	{
		IASTNode parent = node;
		
		while(!(parent instanceof IASTTranslationUnit) && !(parent instanceof IASTFunctionDefinition))
		{
			parent = parent.getParent();
		}
		
		return parent;
	}
	
	/**
	 * Returns true if inner is embedded inside outer IASTNode
	 * 
	 * @param ASTNode inner 
	 * @param ASTNode outer
	 * @return boolean
	 */
	public static boolean isEmbedded(IASTNode inner, IASTNode outer)
	{
		boolean flag = false;
		IASTNode parent = inner;
		
		while(!(parent instanceof IASTTranslationUnit))
		{
			if(parent == outer)
			{
				flag = true;
			}
			parent = parent.getParent();
		}
		
		return flag;
	}
	
	/*
	public static boolean inExpressionTwice()
	{
		boolean flag = false;
		
		
		return flag;
	}
	*/
	
	
	//get varibales in scope and in expression
	public static ArrayList<VariableNameTypePair> allVarNameType(ArrayList<VariableNameTypePair> list, IASTNode node)
	{
		ArrayList<VariableNameTypePair> allVars = new ArrayList<VariableNameTypePair>();
		
		for(VariableNameTypePair o: list)
		{
			if(Utility_C.getScope(o.getNode()) instanceof IASTTranslationUnit || Utility_C.getScope(o.getNode()) == Utility_C.getScope(node))
			{
				ASTVisitorFindMatch visitorFind = new ASTVisitorFindMatch(o.getVarName(), "FindMatch");
				node.accept(visitorFind);
				
				if(visitorFind.isMatch())
				{
					allVars.add(o);
				}
			}
		}
		
		return allVars;
	}
}
