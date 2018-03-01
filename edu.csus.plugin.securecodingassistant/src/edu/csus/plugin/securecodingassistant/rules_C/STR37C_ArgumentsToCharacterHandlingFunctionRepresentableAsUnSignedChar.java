package edu.csus.plugin.securecodingassistant.rules_C;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.csus.plugin.securecodingassistant.Globals;

public class STR37C_ArgumentsToCharacterHandlingFunctionRepresentableAsUnSignedChar implements IRule_C {

	private boolean ruleViolated = false;
	
	private ArrayList<String> charHandlingFunctions = new ArrayList<String>(
			Arrays.asList("isalnum", "isalpha", "isascii", "isblank", "iscntrl",
					"isdigit", "isgraph", "islower", "isprint", "ispunct", "isspace",
					"isupper", "isxdigit", "toascii", "toupper", "tolower")
					
					);
	
	private IASTTranslationUnit currITU = null;
	private ArrayList<IASTNode> listOfDec = new ArrayList<IASTNode>();
	private Iterator<IASTNode> decIT;
	
	private ArrayList<String> listofVarName = new ArrayList<String>();
	private ArrayList<String> listofVarType = new ArrayList<String>();
	
	private Iterator<String> varNameIT;
	private Iterator<String> varTypeIT;
	
	private ArrayList<String> preViolatedNode = new ArrayList<String>(); //previously violated nodes
	
	
	@Override
	public boolean violate_CDT(IASTNode node) {

		ruleViolated = false;
		
		/*
		if(node instanceof IASTParameterDeclaration && node.getParent().getFileLocation().getContextInclusionStatement() == null )
		{
			System.out.println("IASTParameterDeclaration: " + node.getRawSignature() );
			//System.out.println("IASTParameterDeclaration_SCOPE: " + Utility_C.getScope(node).getRawSignature() );
			
			if(((IASTParameterDeclaration)node).getDeclSpecifier() != null && !((IASTParameterDeclaration)node).getDeclSpecifier().getRawSignature().contentEquals("void"))
			{
			System.out.println("IASTParameterDeclaration_getDeclSpecifier: " + ((IASTParameterDeclaration)node).getDeclSpecifier().getRawSignature() );
			System.out.println("IASTParameterDeclaration_getDeclarator: " + ((IASTParameterDeclaration)node).getDeclarator().getRawSignature() );
			}
			//System.out.println("IASTParameterDeclarationParent: " + node.getParent().getRawSignature() + "\n");
		}
		
		if(node instanceof IASTFunctionDefinition && node.getFileLocation().getContextInclusionStatement() == null)
		{
			//System.out.println("IASTFunctionDefinition: " + node.getRawSignature());
			//System.out.println("Nested getDeclarator: " + ((IASTFunctionDefinition)node).getDeclarator().getRawSignature());
			System.out.println("\ngetDeclSpecifier: " + ((IASTFunctionDefinition)node).getDeclSpecifier().getRawSignature());
			if(((IASTFunctionDefinition)node).getDeclarator() != null)
			{
				System.out.println("getDeclarator: : " + ((IASTFunctionDefinition)node).getDeclarator().getRawSignature() );
			}
			//System.out.println("getDeclSpecifier: " + ((IASTFunctionDefinition)node).getDeclSpecifier().getRawSignature());
		}
		*/
		
		if(node.getContainingFilename().contains("STR37") && (node.getTranslationUnit().getRawSignature().contains("ctype.h"))
				&& node.getParent().getFileLocation().getContextInclusionStatement() == null)
		{
			
			//System.out.println("node: " + node.getRawSignature());
				
				/*
				if(node instanceof IASTDeclaration)
				{
					System.out.println("IASTDeclaration: " + node.getRawSignature());
				}
				*/
				/*
				if(currITU == null || currITU != node.getTranslationUnit())
				{
					listOfDec.clear();
					listofVarName.clear();
					listofVarType.clear();
					preViolatedNode.clear();
					//System.out.println("currITU NULL");
					currITU = node.getTranslationUnit();
					ASTVisitorFindMatch visitor = new ASTVisitorFindMatch(null, "CON40C_FindDec");
					
					node.getTranslationUnit().accept(visitor);
					
					listOfDec = visitor.arrayListofDeclaration();
					decIT = listOfDec.iterator();
					
					while(decIT.hasNext())//get the Variable type and name of each declarator
					{
						IASTNode nextIT = decIT.next();	
						//System.out.println("decIT: " + nextIT.getRawSignature());
							String varName = this.getVarNames(nextIT);
							String varType = this.getVarType(nextIT);
							
							listofVarName.add(varName);
							listofVarType.add(varType);
							
							//System.out.println("varName: " + varName);
							//System.out.println("varType: " + varType);
							//System.out.println("");
							
					}
					decIT = listOfDec.iterator();
				}
				*/
				
				if(node instanceof IASTFunctionCallExpression)
				{
					System.out.println("IASTFunctionCallExpression: " + node.getRawSignature());
					String functionName =  ((IASTFunctionCallExpression)node).getFunctionNameExpression().getRawSignature();
					System.out.println("IASTFunctionCallExpressionNAME: " + functionName);
					//System.out.println("IASTFunctionCallExpression: " + ((IASTFunctionCallExpression)node).getFunctionNameExpression());
					
					
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
							node.accept(visitor);
							
							
							for(VariableNameTypePair ntPair: visitor.getvarNamePairList())//cant get variables within functioncall
							{
								System.out.println("Node: " + node.getRawSignature());
								System.out.println("VarName:" + ntPair.getVarName());
								System.out.println("Vartype:" + ntPair.getVarType() + "\n");
								if(ntPair.getVarType().contains("char") && !ntPair.getVarType().contains("unsigned"))
								{
									ASTVisitorFindMatch visitorMatch = new ASTVisitorFindMatch(ntPair.getVarName(), "FindMatch");
									node.accept(visitorMatch);
									
									if(visitorMatch.isMatch())
									{
										ruleViolated = true;
										System.out.println("RULEVIOLATED");
									}
								}
							}
							
							/*
							int firstIndex;
							int secondIndex;
							String varName;
							//custom code because ASTVisitor does not go deeper than IASTFunctionCallExpression Node
							if(node.getRawSignature().contains("[") && node.getRawSignature().contains("]"))
							{
								firstIndex = node.getRawSignature().indexOf("(");
								secondIndex = node.getRawSignature().indexOf("[");
								
								varName = node.getRawSignature().substring(firstIndex + 1, secondIndex);
								//System.out.println("varName: " + varName);
							}
							else if(node.getRawSignature().contains("*"))
							{
								firstIndex = node.getRawSignature().indexOf("*");
								secondIndex = node.getRawSignature().indexOf(")");
								
								varName = node.getRawSignature().substring(firstIndex + 1, secondIndex);
								//System.out.println("varName: " + varName);
							}
							else
							{
								firstIndex = node.getRawSignature().indexOf("(");
								secondIndex = node.getRawSignature().indexOf(")");
								
								varName = node.getRawSignature().substring(firstIndex + 1, secondIndex);
								//System.out.println("varName: " + varName);
							}
							
							varNameIT = listofVarName.iterator();
							varTypeIT = listofVarType.iterator();
							
							int arrayListPosition = listofVarName.indexOf(varName);
							String funcVariableType = listofVarType.get(arrayListPosition);
							
							//System.out.println("Position: "+ arrayListPosition);
							//System.out.println("funcVariableType: " + funcVariableType);
							
							if(!funcVariableType.contains("unsigned"))
							{
								if(!preViolatedNode.contains(node.getRawSignature()))
								{
									ruleViolated = true;
									//System.out.println("Rule Violated!!!");
									preViolatedNode.add(node.getRawSignature());
								}
							}
							
							*/
						}
						
					}
					
				}
		}
		return ruleViolated;
		
	}

		
	public String getVarNames(IASTNode astN)
		{
			String str = "default\n";
			if(astN.getRawSignature().contains("="))
			{
				//System.out.println("astN: "+ astN.getRawSignature());
				//System.out.println("getVarNameChildren: " + astN.getChildren()[0].getRawSignature() );
				//System.out.println("getVarNameChildren: " + astN.getChildren()[1].getChildren()[0].getRawSignature() );
				str = astN.getChildren()[1].getChildren()[0].getRawSignature();
				
				if(str.contentEquals("*"))
				{
					str = astN.getChildren()[1].getChildren()[1].getRawSignature();
					
					if(str.contentEquals("*"))
					{
						str = astN.getChildren()[1].getChildren()[2].getRawSignature();
					}
					//System.out.println("**SUBSTRING: " + str);
					
					//listofPointerVarName.add(str);
				}
				
				
			}
			else
			{
				//System.out.println("getVarNameChildren: " + astN.getChildren()[0].getRawSignature() );
				//System.out.println("getVarNameChildren: " + astN.getChildren()[1].getRawSignature() );
				str = astN.getChildren()[1].getRawSignature();
				
				if(str.startsWith("**"))
				{
					str = str.substring(2);
					//System.out.println("**SUBSTRING: " + str);
					//listofPointerVarName.add(str);
				}
				else if(str.startsWith("*"))
				{
					str = str.substring(1);
					//System.out.println("*SUBSTRING: " + str);
					//listofPointerVarName.add(str);
				}
				else if (str.contains("["))
				{
					int pos = str.indexOf("[");
					//System.out.println("Position of [ :" + pos);
					
					str = str.substring(0, pos);
					//System.out.println("substring arr: " + str);
					//listofArrVarName.add(str);
				}
			}
			return str;
		}
		
	public String getVarType(IASTNode astN)
	{
		String str = "default\n";
		
		if(astN.getRawSignature().contains("="))
		{
			//System.out.println("getVarNameChildren: " + astN.getChildren()[0].getRawSignature() );
			//System.out.println("getVarNameChildren: " + astN.getChildren()[1].getChildren()[0].getRawSignature() );
			str = astN.getChildren()[0].getRawSignature();
		}
		else
		{
			//System.out.println("getVarNameChildren: " + astN.getChildren()[0].getRawSignature() );
			//System.out.println("getVarNameChildren: " + astN.getChildren()[1].getRawSignature() );
			str = astN.getChildren()[0].getRawSignature();
		}
		return str;
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
