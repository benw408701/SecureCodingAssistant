package edu.csus.plugin.securecodingassistant.rules_C;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * <b><i>The text and/or code below is from the CERT website:
 * <a target="_blank"href="https://wiki.sei.cmu.edu/confluence/display/seccode">
 * https://wiki.sei.cmu.edu/confluence/display/seccode </a></i></b>
 * <p>
 * C Secure Coding Rule: STR37-C. Arguements to character-handling fuctions
 * must be representable as a unsigned char
 * </p>
 * <p>
 * According to the C Standard, 7.4 [ISO/IEC 9899:2011],
 * The header <ctype.h> declares several functions useful for classifying and 
 * mapping characters. In all cases the argument is an int, the value of 
 * which shall be representable as an unsigned char or shall equal the value 
 * of the macro EOF. If the argument has any other value, the behavior is 
 * undefined.
 * </p>
 * 
 * <p>
 * This rule is applicable only to code that runs on platforms where the char 
 * data type is defined to have the same range, representation, and behavior 
 * as signed char.
 * </p>
 * 
 * @author Victor Melnik (Plugin Logic), CERT (Rule Definition)
 * @see C Secure Coding Rule define by CERT: <a target="_blank" 
 * href="https://wiki.sei.cmu.edu/confluence/display/c/STR37-C.+Arguments+to+
 * character-handling+functions+must+be+representable+as+an+unsigned+
 * char">STR37-C</a>
 *
 */

public class STR37C_ArgumentsToCharacterHandlingFunctionRepresentableAsUnSignedChar extends SecureCodingRule_C {

	private boolean ruleViolated = false;
	
	private ArrayList<String> charHandlingFunctions = new ArrayList<String>(
			Arrays.asList("isalnum", "isalpha", "isascii", "isblank", "iscntrl",
					"isdigit", "isgraph", "islower", "isprint", "ispunct", "isspace",
					"isupper", "isxdigit", "toascii", "toupper", "tolower")
					
					);
	
	@Override
	public boolean violate_CDT(IASTNode node) {

		ruleViolated = false;
		
		if((node.getTranslationUnit().getRawSignature().contains("ctype.h")) && node.getFileLocation().getContextInclusionStatement() == null)
		{			
			if(node instanceof IASTFunctionCallExpression)
				{
					String functionName =  ((IASTFunctionCallExpression)node).getFunctionNameExpression().getRawSignature();
					
					if(functionName.contains("(") && functionName.contains(")"))
					{
						int positionParenthesis = functionName.indexOf("(");
						functionName = functionName.substring(0, positionParenthesis);
					}
					
					//check if function is character-handling function
					if(charHandlingFunctions.contains(functionName))
					{
						
						if(node.getRawSignature().contains("unsigned") && node.getRawSignature().contains("char"))
						{
							ruleViolated = false;
						}
						else if(node.getRawSignature().contains("EOF"))
						{
							ruleViolated = false;
						}
						else
						{
							ASTNodeProcessor_C visitor = new ASTNodeProcessor_C();
							
							Utility_C.getScope(node).accept(visitor);
							
							for(VariableNameTypePair ntPair: visitor.getvarNamePairList())
							{
								if(ntPair.getVarType().contains("char") && !ntPair.getVarType().contains("unsigned"))
								{
									for(String str: Utility_C.getFunctionParameterVarName(((IASTFunctionCallExpression)node)))
									{
										if(ntPair.getVarName().contentEquals(str))
										{
											ruleViolated = true;
										}
									}
								}
							}
						}
					}
				}
		}
		return ruleViolated;	
	}
	
	@Override
	public String getRuleText() {
		
		return "CERT Website- Passing values that cannot be represented as an unsigned"
				+ " char to character handling functions from header <ctype.h> is "
				+ "undefined behavior. ";
	}

	@Override
	public String getRuleName() {
		
		return Globals.RuleNames.STR37_C;
	}

	@Override
	public String getRuleID() {
		
		return Globals.RuleID.STR37_C;
	}

	@Override
	public String getRuleRecommendation() {
		
		return "Pass unsigned char or, signed char casted to unsigned char to character "
				+ "handling functions.";
	}

	@Override
	public int securityLevel() {
		
		return Globals.Markers.SECURITY_LEVEL_LOW;
	}

	@Override
	public String getRuleURL() {
		
		return "https://wiki.sei.cmu.edu/confluence/display/c/STR37-C.+Arguments+to+character-handling+functions+must+be+representable+as+an+unsigned+char";
	}

	@Override
	public ITranslationUnit getITranslationUnit() {
		// TODO Auto-generated method stub
		return null;
	}

}
