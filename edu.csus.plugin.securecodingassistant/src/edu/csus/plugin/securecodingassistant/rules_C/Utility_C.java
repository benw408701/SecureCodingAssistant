package edu.csus.plugin.securecodingassistant.rules_C;

import java.util.ArrayList;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

/**
 * Collection of utility methods used by the 
 * Secure Coding Assistant Rules for C
 * @author Victor Melnik
 *
 *
 */
public final class Utility_C {

	/**
	 * Cannot instantiate
	 */
	private Utility_C() {
	}
	
	/**
	 * Returns the scope of the node
	 * @param IASTNode node
	 * @return IASTNode
	 */
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
	
	/**
	 * Returns list of VarNameTypePair for all variables in scope of expression
	 * @param ArrayList<VariableNameTypePair> list
	 * @param IASTNode node
	 * @return ArrayList<VariableNameTypePair>
	 */
	public static ArrayList<VariableNameTypePair> allVarNameType(ArrayList<VariableNameTypePair> list, IASTNode node)
	{
		ArrayList<VariableNameTypePair> allVars = new ArrayList<VariableNameTypePair>();
		
		for(VariableNameTypePair o: list)
		{
			if(Utility_C.getScope(o.getNode()) == node.getTranslationUnit() || Utility_C.getScope(o.getNode()) == Utility_C.getScope(node))
			{
				allVars.add(o);
			}
		}
		
		return allVars;
	}
	
	/**
	 * Parses out variable names in function parameter in  IASTFunctionCallExpression 
	 * Do NOT use for format String functions
	 * 
	 * @param IASTFunctionCallExpression funcCall
	 * @return ArrayList<String>
	 */
	public static ArrayList<String> getFunctionParameterVarName(IASTFunctionCallExpression funcCall)
	{
		ArrayList<String> list = new ArrayList<String>();
		
		
		int firstParen = funcCall.getRawSignature().indexOf("(");
		int stringLen = funcCall.getRawSignature().length();
		String parameters = funcCall.getRawSignature().substring(firstParen + 1, stringLen - 1 );
		
		while(parameters != null)
		{
			if(parameters.contains(","))
			{
				int commaIndex = parameters.indexOf(",");
				int paramLen = parameters.length();
				String extractedParam = parameters.substring(0, commaIndex);
				parameters = parameters.substring(commaIndex + 1, paramLen);
				
				extractedParam = extractedParam.replaceAll("\\s+","");
				extractedParam = extractedParam.replaceAll("\\*","");
				extractedParam = extractedParam.replaceAll("&","");
				
				if(extractedParam.contains("["))
				{
					int paramSquBracket = extractedParam.indexOf("[");
					extractedParam = extractedParam.substring(0, paramSquBracket);
				}
				
				if(extractedParam.contains(")"))
				{
					int paramBracket = extractedParam.indexOf(")");
					paramLen = extractedParam.length();
					extractedParam = extractedParam.substring(paramBracket + 1, paramLen);
				}
				
				list.add(extractedParam);
			}
			else
			{
				parameters = parameters.replaceAll("\\s+","");
				parameters = parameters.replaceAll("\\*","");
				parameters = parameters.replaceAll("&","");
				
				if(parameters.contains("["))
				{
					int paramSquBracket = parameters.indexOf("[");
					parameters = parameters.substring(0, paramSquBracket);
				}
				
				if(parameters.contains(")"))
				{
					int paramBracket = parameters.indexOf(")");
					int parameterLen = parameters.length();
					parameters = parameters.substring(paramBracket + 1, parameterLen);
				}
				
				list.add(parameters);
				parameters = null;
			}
			
		}
		
		return list;
	}
	
	/**
	 * Parses out variable names in function parameter in  IASTFunctionCallExpression 
	 * Specialized for format string functions
	 *
	 * @param  IASTFunctionCallExpression funcCall
	 * @return ArrayList<String>
	 */
	public static ArrayList<String> getFunctionParameterVarNamePrintf(IASTFunctionCallExpression funcCall)
	{
		ArrayList<String> list = new ArrayList<String>();
		
		String functionCallString = funcCall.getRawSignature();
		
		functionCallString = functionCallString.replaceAll("%%", "");
		functionCallString = functionCallString.replaceAll("\\\\\"", "");
		
		int firstQuoteIndex = functionCallString.indexOf("\"", 0);
		int secondQuoteIndex = functionCallString.indexOf("\"", firstQuoteIndex + 1);
		int lastIndex = functionCallString.length() - 1;
		String firstThird, secondThird, thirdThird = null;
		
		if((firstQuoteIndex < 0) || (secondQuoteIndex < 0) || (lastIndex < 0))
		{
			return list;
		}
		
		firstThird = functionCallString.substring(0, firstQuoteIndex + 1);
		secondThird = functionCallString.substring(firstQuoteIndex + 1, secondQuoteIndex);
		thirdThird = functionCallString.substring(secondQuoteIndex, lastIndex + 1);
		
		//Parsing out format string qualifiers
		secondThird = secondThird.replace(",", "");
		secondThird = secondThird.replace("(", "");
		secondThird = secondThird.replace(")", "");
		secondThird = secondThird.replace("[", "");
		//remove digits and periods 
		secondThird = secondThird.replace("-", "");
		secondThird = secondThird.replace(".", "");
		secondThird = secondThird.replace("#", "");
		secondThird = secondThird.replace("'", "");
		//remove qualifiers
		
		//secondThird = secondThird.replace("h", "");
		//secondThird = secondThird.replace("l", "");
		secondThird = secondThird.replace("j", "");
		secondThird = secondThird.replace("z", "");
		secondThird = secondThird.replace("t", "");
		//secondThird = secondThird.replace("L", "");
		
		for(int counter = 0; counter <10; counter++)
		{
			secondThird = secondThird.replace(Integer.toString(counter), "");
		}
		
		functionCallString = firstThird + secondThird + thirdThird;
		
		int firstParen = functionCallString.indexOf("(");
		int stringLen = functionCallString.length();
		String parameters = functionCallString.substring(firstParen + 1, stringLen - 1 );
		
		while(parameters != null)
		{
			if(parameters.contains(","))
			{
				int commaIndex = parameters.indexOf(",");
				int paramLen = parameters.length();
				String extractedParam = parameters.substring(0, commaIndex);
				parameters = parameters.substring(commaIndex + 1, paramLen);
				
				extractedParam = extractedParam.replaceAll("\\s+","");
				
				if(extractedParam.contains("["))
				{
					int paramSquBracket = extractedParam.indexOf("[");
					extractedParam = extractedParam.substring(0, paramSquBracket);
				}
				
				if(extractedParam.contains(")"))
				{
					int paramBracket = extractedParam.indexOf(")");
					paramLen = extractedParam.length();
					extractedParam = extractedParam.substring(paramBracket + 1, paramLen);
				}
				
				list.add(extractedParam);
			}
			else
			{
				parameters = parameters.replaceAll("\\s+","");
				
				if(parameters.contains("["))
				{
					int paramSquBracket = parameters.indexOf("[");
					parameters = parameters.substring(0, paramSquBracket);
				}
				
				if(parameters.contains(")"))
				{
					int paramBracket = parameters.indexOf(")");
					int parameterLen = parameters.length();
					parameters = parameters.substring(paramBracket + 1, parameterLen);
				}
				
				list.add(parameters);
				parameters = null;
			}
			
		}
		
		return list;
	}
}
