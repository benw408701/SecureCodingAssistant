package edu.csus.plugin.securecodingassistant.rules_C;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.csus.plugin.securecodingassistant.Globals;

public class FIO47C_UseValidFormatStrings extends SecureCodingRule_C {

	private boolean ruleViolated;
	private ArrayList<String> formattedIOFunctions = new ArrayList<String>(
			Arrays.asList("fprintf", "printf", "sprintf", "vfprintf", "vprintf",
					"vsprintf", "fscanf", "sscanf", "scanf"
					));
	
	@Override
	public boolean violate_CDT(IASTNode node) {
		
		ruleViolated = false;
		ArrayList<String> funcParams = new ArrayList<String>();
		
		if((node.getFileLocation().getContextInclusionStatement() == null))
		{
			if(node instanceof IASTFunctionCallExpression)
			{
				String functionName = ((IASTFunctionCallExpression)node).getFunctionNameExpression().getRawSignature();
				if(formattedIOFunctions.contains(functionName))
				{
					funcParams = Utility_C.getFunctionParameterVarNamePrintf((IASTFunctionCallExpression) node);
					ASTNodeProcessor_C visitor = new ASTNodeProcessor_C();
					node.getTranslationUnit().accept(visitor);
					String firstParameter = funcParams.get(0);
					funcParams.remove(0);
					
					String tempFirst = firstParameter;
					int numberFormatChars = firstParameter.length() - (tempFirst.replace("%", "").length());
					
					if(numberFormatChars != funcParams.size())
					{
						if(functionName.contentEquals("fscanf") | functionName.contentEquals("sscanf") || functionName.contentEquals("scanf"))
						{
							firstParameter = funcParams.get(1);
							funcParams.remove(1);
						}
						else
						{
							return true;
						}
					}
					
					for(String strPost: funcParams)
					{
						for(VariableNameTypePair vntP: Utility_C.allVarNameType(visitor.getvarNamePairList(), node))
						{
							if(vntP.getVarName().contentEquals(strPost))
							{
								int indexOf = firstParameter.indexOf("%");
								//get index of % and print string
								
								firstParameter = firstParameter.substring(indexOf + 1);
						
								if((vntP.getVarType().contains("unsigned")|| vntP.getVarType().contains("uintmax_t")) && !vntP.getVarType().contains("char"))
								{
									if(!firstParameter.startsWith("o") && !firstParameter.startsWith("u") && !firstParameter.startsWith("x") &&
											!firstParameter.startsWith("X"))
									{
										ruleViolated = true;
									}
								}
								else if(vntP.getVarType().contains("double") || vntP.getVarType().contains("float") || vntP.getVarType().contains("long double"))
								{
									if(!firstParameter.startsWith("f") && !firstParameter.startsWith("F") && !firstParameter.startsWith("e") &&
											!firstParameter.startsWith("E") && !firstParameter.startsWith("g") && !firstParameter.startsWith("G") &&
											!firstParameter.startsWith("a") && !firstParameter.startsWith("A"))
									{
										ruleViolated = true;
									}
								}
								else if (vntP.getVarType().contains("char") && (vntP.isPointer() || vntP.isArray()))
								{
									if(!firstParameter.startsWith("s") && !firstParameter.startsWith("S"))
									{
										ruleViolated = true;
									}
								}
								else if (vntP.getVarType().contains("void") && vntP.isPointer())
								{
									if(!firstParameter.startsWith("n") )
									{
										ruleViolated = true;
									}
								}
								else if(vntP.getVarType().contains("size_t")|| vntP.getVarType().contains("ptrdiff_t"))
								{
									if(!firstParameter.startsWith("o") && !firstParameter.startsWith("u") && !firstParameter.startsWith("x") &&
											!firstParameter.startsWith("X") && !firstParameter.startsWith("d") && !firstParameter.startsWith("i"))
									{
										ruleViolated = true;
									}
								}
								else if(vntP.getVarType().contains("wint_t"))
								{
									if(!firstParameter.startsWith("c") && !firstParameter.startsWith("C"))
									{
										ruleViolated = true;
									}
								}
								else if(vntP.getVarType().contains("char") && vntP.getVarType().contains("unsigned"))
								{
									if(!firstParameter.startsWith("c") && !firstParameter.startsWith("C") && !firstParameter.startsWith("o") && 
											!firstParameter.startsWith("u") && !firstParameter.startsWith("x") && !firstParameter.startsWith("X"))
									{
										ruleViolated = true;
									}
								}
								else if(vntP.getVarType().contains("char") && vntP.getVarType().contains("signed"))
								{
									if(!firstParameter.startsWith("c") && !firstParameter.startsWith("C") && !firstParameter.startsWith("d") && 
											!firstParameter.startsWith("i"))
									{
										ruleViolated = true;
									}
								}
								else if(vntP.getVarType().contains("size_t")|| vntP.getVarType().contains("ptrdiff_t"))
								{
									if(!firstParameter.startsWith("o") && !firstParameter.startsWith("u") && !firstParameter.startsWith("x") &&
											!firstParameter.startsWith("X") && !firstParameter.startsWith("d") && !firstParameter.startsWith("i"))
									{
										ruleViolated = true;
									}
								}
								else if((vntP.getVarType().contains("short")|| vntP.getVarType().contains("long")|| vntP.getVarType().contains("intmax_t"))
										&& !vntP.isPointer())
								{
									if(!firstParameter.startsWith("d") && !firstParameter.startsWith("i"))
									{
										ruleViolated = true;
									}
								}
								else if((vntP.getVarType().contains("short")|| vntP.getVarType().contains("long")|| vntP.getVarType().contains("intmax_t")
										) && vntP.isPointer())
								{
									if(!firstParameter.startsWith("n"))
									{
										ruleViolated = true;
									}
								}
								else if(vntP.getVarType().contains("int"))
								{
									if(vntP.isPointer())
									{
										if(!firstParameter.startsWith("n"))
										{
											ruleViolated = true;
										}
									}
									else if(!firstParameter.startsWith("d") && !firstParameter.startsWith("c") && !firstParameter.startsWith("i") )
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
