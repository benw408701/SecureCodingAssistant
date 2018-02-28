package edu.csus.plugin.securecodingassistant.rules_C;

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
}
