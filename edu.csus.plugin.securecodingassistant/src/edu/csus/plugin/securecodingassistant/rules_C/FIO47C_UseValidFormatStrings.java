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
 * C Secure Coding Rule: FIO47-C. Use valid format strings
 * </p>
 * <p>
 * The formatted output functions (fprintf() and related functions) convert, 
 * format, and print their arguments under control of a format string. The C 
 * Standard, 7.21.6.1, paragraph 3 [ISO/IEC 9899:2011], specifies:
 *   The format shall be a multibyte character sequence, beginning and 
 *   ending in its initial shift state. The format is composed of zero or 
 *   more directives: ordinary multibyte characters (not %), which are 
 *   copied unchanged to the output stream; and conversion specifications, 
 *   each of which results in fetching zero or more subsequent arguments, 
 *   converting them, if applicable, according to the corresponding conversion
 *   specifier, and then writing the result to the output stream.
 * </p>
 * 
 * <p>
 * Common mistakes in creating format strings include
 *		Providing an incorrect number of arguments for the format string
 *		Using invalid conversion specifiers
 *		Using a flag character that is incompatible with the conversion specifier
 *		Using a length modifier that is incompatible with the conversion specifier
 *		Mismatching the argument type and conversion specifier
 *		Using an argument of type other than int for width or precision
 * </p>
 * 
 * @author Victor Melnik (Plugin Logic), CERT (Rule Definition)
 * @see C Secure Coding Rule define by CERT: <a target="_blank" 
 * href="https://wiki.sei.cmu.edu/confluence/display/c/FIO47-C.+Use+valid
 * +format+strings">FIO47-C</a>
 *
 */

public class FIO47C_UseValidFormatStrings extends SecureCodingRule_C {

	private boolean ruleViolated;
	private ArrayList<String> formattedIOFunctions = new ArrayList<String>(
			Arrays.asList("fprintf", "printf", "sprintf", "vfprintf", "vprintf",
					"vsprintf", "fscanf", "sscanf", "scanf"
					));
	
	//private boolean isBracket;
	private boolean isAsterisk;
	private boolean isReference;
	
	@Override
	public boolean violate_CDT(IASTNode node) {
		
		ruleViolated = false;
		boolean isBracket = false;
		boolean isAsterisk = false;
		boolean isReference = false;
		ArrayList<String> funcParams = new ArrayList<String>();
		
		if((node.getFileLocation().getContextInclusionStatement() == null))
		{	
			
			if(node instanceof IASTFunctionCallExpression)
			{
				String functionName = ((IASTFunctionCallExpression)node).getFunctionNameExpression().getRawSignature();
				if(formattedIOFunctions.contains(functionName))
				{
					if(!node.getRawSignature().contains("\""))
					{
						return false;
					}
					funcParams = Utility_C.getFunctionParameterVarNamePrintf((IASTFunctionCallExpression) node);
					ASTNodeProcessor_C visitor = new ASTNodeProcessor_C();
					node.getTranslationUnit().accept(visitor);
					if(funcParams.size() <= 1)
					{
						return false;
					}
					String firstParameter = funcParams.get(0);
					funcParams.remove(0);
					
					
					if(!firstParameter.contains("\""))
					{
						firstParameter = funcParams.get(0);
						funcParams.remove(0);
					}
					
					String tempFirst = firstParameter;
					int numberFormatChars = firstParameter.length() - (tempFirst.replace("%", "").length());
					
					
					if((numberFormatChars != funcParams.size()))
					{
						return true;
					}
					else if((funcParams.size() < 1) )
					{
						return false;
					}
					
					
					for(String strPost: funcParams)
					{
						isReference = false;
						isAsterisk = false;
						if(strPost.contains("&"))
						{
							isReference = true;
							strPost = strPost.replace("&", "");
						}
						
						if(strPost.contains("*"))
						{
							isAsterisk = true;
							strPost = strPost.replaceAll("\\*","");
						}
						
						/**
						 * Check printf is using correct specifier per variable
						 * type.
						 */
						for(VariableNameTypePair vntP: Utility_C.allVarNameType(visitor.getvarNamePairList(), node))
						{
							if((vntP.getVarName().contentEquals(strPost)) || (strPost.startsWith(".") && strPost.contains(vntP.getVarName())))
							{
								int indexOf = firstParameter.indexOf("%");
								//get index of % and print string
								
								firstParameter = firstParameter.substring(indexOf + 1);
								
								
								if(firstParameter.startsWith("hho") || firstParameter.startsWith("hhu") || firstParameter.startsWith("hhx") || firstParameter.startsWith("hhX"))
								{
									if((!vntP.getVarType().contains("char")) || (!vntP.getVarType().contains("unsigned")))
									{
										ruleViolated = true;
									}
								}
								else if(firstParameter.startsWith("ho") || firstParameter.startsWith("hu") || firstParameter.startsWith("hx") || firstParameter.startsWith("hX"))
								{
									if((!vntP.getVarType().contains("short")) || (!vntP.getVarType().contains("unsigned")))
									{
										ruleViolated = true;
									}
								}
								else if(firstParameter.startsWith("lo") || firstParameter.startsWith("lu") || firstParameter.startsWith("lx") || firstParameter.startsWith("lX")
										|| firstParameter.startsWith("llo") || firstParameter.startsWith("llu") || firstParameter.startsWith("llx") || firstParameter.startsWith("llX"))
								{
									if((!vntP.getVarType().contains("long")) || (!vntP.getVarType().contains("unsigned")))
									{
										ruleViolated = true;
									}
								}
								else if(firstParameter.startsWith("o") || firstParameter.startsWith("u") || firstParameter.startsWith("x") || firstParameter.startsWith("X"))
								{
									if(((!vntP.getVarType().contains("int")) && (!vntP.getVarType().contains("short")) && (!vntP.getVarType().contains("char"))
											|| (!vntP.getVarType().contains("unsigned"))) && ( (!vntP.getVarType().contains("uintmax_t")) && (!vntP.getVarType().contains("size_t"))
											&& (!vntP.getVarType().contains("ptrdiff_t"))))
									{
										ruleViolated = true;
									}
								}
								else if(firstParameter.startsWith("hhd") || firstParameter.startsWith("hhi") )
								{
									if((!vntP.getVarType().contains("char")) && (vntP.getVarType().contains("unsigned")))
									{
										ruleViolated = true;
									}
								}
								else if(firstParameter.startsWith("hd") || firstParameter.startsWith("hi") )
								{
									if((!vntP.getVarType().contains("short")) )
									{
										ruleViolated = true;
									}
								}
								else if(firstParameter.startsWith("ld") || firstParameter.startsWith("lld") || firstParameter.startsWith("li") || firstParameter.startsWith("lli"))
								{
									if((!vntP.getVarType().contains("long")))
									{
										ruleViolated = true;
									}
								}
								//int
								else if(firstParameter.startsWith("d") || firstParameter.startsWith("i"))
								{
									if((!vntP.getVarType().contains("int")) && (!vntP.getVarType().contains("intmax_t")) && (!vntP.getVarType().contains("size_t"))
											&& (!vntP.getVarType().contains("ptrdiff_t")) && (!vntP.getVarType().contains("short")) && (!vntP.getVarType().contains("char"))
											&& (!vntP.getVarType().contains("_t")))
									{
										ruleViolated = true;
									}
								}
								else if(firstParameter.startsWith("Lf") || firstParameter.startsWith("LF") || firstParameter.startsWith("Le") || firstParameter.startsWith("LE")
										|| firstParameter.startsWith("Lg") || firstParameter.startsWith("LG") || firstParameter.startsWith("La") || firstParameter.startsWith("LA"))
								{
									if((!vntP.getVarType().contains("long")) && (!vntP.getVarType().contains("double")))
									{
										ruleViolated = true;
									}
								}
								else if(firstParameter.startsWith("f") || firstParameter.startsWith("F") || firstParameter.startsWith("e") || firstParameter.startsWith("E")
										|| firstParameter.startsWith("g") || firstParameter.startsWith("G") || firstParameter.startsWith("a") || firstParameter.startsWith("A"))
								{
									if((!vntP.getVarType().contains("float")) && (!vntP.getVarType().contains("double")))
									{
										ruleViolated = true;
									}
								}
								else if(firstParameter.startsWith("c") || firstParameter.startsWith("lc") )
								{
									if(!((vntP.getVarType().contains("int")) || (vntP.getVarType().contains("wint_t")) || ((vntP.getVarType().contains("char"))
											&& (!vntP.getVarType().contains("unsigned")))))
									{
										ruleViolated = true;
									}
								}
								else if(firstParameter.startsWith("s") || firstParameter.startsWith("ls"))
								{
									if(vntP.getVarType().contains("va_list"))
									{
										ruleViolated = false;
									}
									else if((!vntP.getVarType().contains("char")) && ((!vntP.isPointer() || !vntP.isArray())))
									{
										ruleViolated = true;
									}
								}
								else if(firstParameter.startsWith("p") )
								{
									if(!(isReference || (vntP.isPointer())))
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
		
		return "Cert Website- Do not supply an unknown or invalid conversion "
				+ "specification or an invalid combination of flag character, "
				+ "precision, length modifier, or conversion specifier to a "
				+ "formatted IO function. Likewise, do not provide a number "
				+ "or type of argument that does not match the argument type "
				+ "of the conversion specifier used in the format string.";
	}

	@Override
	public String getRuleName() {
		
		return Globals.RuleNames.FIO47_C;
	}

	@Override
	public String getRuleID() {
		
		return Globals.RuleID.FIO47_C;
	}

	@Override
	public String getRuleRecommendation() {
		
		return "Use the correct conversion specifier in the printf family of "
				+ "functions.";
	}

	@Override
	public int securityLevel() {
		
		return Globals.Markers.SECURITY_LEVEL_HIGH;
	}

	@Override
	public String getRuleURL() {
		
		return "https://wiki.sei.cmu.edu/confluence/display/c/FIO47-C.+Use+valid+format+strings";
	}

	@Override
	public ITranslationUnit getITranslationUnit() {
		// TODO Auto-generated method stub
		return null;
	}

}
