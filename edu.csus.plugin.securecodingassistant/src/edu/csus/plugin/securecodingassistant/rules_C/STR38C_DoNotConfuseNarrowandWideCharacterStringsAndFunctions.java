package edu.csus.plugin.securecodingassistant.rules_C;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.csus.plugin.securecodingassistant.Globals;

public class STR38C_DoNotConfuseNarrowandWideCharacterStringsAndFunctions implements IRule_C {

	private boolean ruleViolated;
	private String functionName;
	private IASTTranslationUnit currITU = null;
	private ArrayList<IASTNode> listOfDec = new ArrayList<IASTNode>();
	private Iterator<IASTNode> decIT;
	
	private ArrayList<String> listofVarName = new ArrayList<String>();
	private ArrayList<String> listofVarType = new ArrayList<String>();
	
	private Iterator<String> varNameIT;
	private Iterator<String> varTypeIT;
	
	@Override
	public boolean violate_CDT(IASTNode node) {
		
		ruleViolated = false;
		functionName = null;
		
		//if(node.getContainingFilename().contains("STR38"))
		//{
			//System.out.println("node: " + node.getRawSignature());
			
			if(node.getTranslationUnit().getRawSignature().contains("#include <wchar.h>") || node.getTranslationUnit().getRawSignature().contains("wchar_t"))
			{
				//System.out.println("node: " + node.getRawSignature());
				
				/*
				if(node instanceof IASTDeclaration)
				{
					System.out.println("IASTDeclaration: " + node.getRawSignature());
				}
				*/
				if(currITU == null || currITU != node.getTranslationUnit())
				{
					listOfDec.clear();
					listofVarName.clear();
					listofVarType.clear();
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
				
				varNameIT = listofVarName.iterator();
				varTypeIT = listofVarType.iterator();
				
				if(node instanceof IASTFunctionCallExpression)
				{
					//System.out.println("IASTFunctionCallExpression: " + node.getRawSignature());
					//System.out.println("Child1: " + node.getChildren()[0].getRawSignature());
					functionName = node.getChildren()[0].getRawSignature();
					//System.out.println("functionName: " + functionName);
					
					ASTVisitorFindMatch visitor = new ASTVisitorFindMatch(functionName, "STR38_Include");
					
					node.getTranslationUnit().accept(visitor);
					
					if(visitor.isStringFunction())
					{
						//System.out.println("String function");
						
						while(varNameIT.hasNext())
						{
							String varNameITTemp = varNameIT.next();
							String varTypeITTemp = varTypeIT.next();
							
							if(varTypeITTemp.startsWith("wchar"))
							{
								ASTVisitorFindMatch visitor1 = new ASTVisitorFindMatch(varNameITTemp, "FindMatch");
								
								node.accept(visitor1);
								
								if(visitor1.isMatch())
								{
									//System.out.println("RULEVIOLATED!!!!!");
									ruleViolated = true;
									break;
								}
							}
							
								
						}
					}
					else if(visitor.isWideStringFunction())
					{
						//System.out.println("Wide String function");
						while(varNameIT.hasNext())
						{
							String varNameITTemp = varNameIT.next();
							String varTypeITTemp = varTypeIT.next();
							
							if(varTypeITTemp.startsWith("char"))
							{
								ASTVisitorFindMatch visitor1 = new ASTVisitorFindMatch(varNameITTemp, "FindMatch");
								
								node.accept(visitor1);
								
								if(visitor1.isMatch())
								{
									//System.out.println("RULEVIOLATED!!!!!");
									ruleViolated = true;
									break;
								}
							}
							
								
						}
					}
					//check to see if function is in #include <wchar.h> or if it is in #include <string.h> header
					
				}
			}
		//}
		
		return ruleViolated;
	}

	public String getVarNames(IASTNode astN)
	{
		String str = "default\n";
		if(astN.getRawSignature().contains("="))
		{
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
			}
			else if(str.startsWith("*"))
			{
				str = str.substring(1);
				//System.out.println("*SUBSTRING: " + str);
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
		// TODO Auto-generated method stub
		return "CERT Website- Passing narrow string arguments to wide string functions "
				+ "or wide string arguments to narrow string functions can lead to "
				+ "unexpected and undefined behavior. Scaling problems are likely "
				+ "because of the difference in size between wide and narrow characters.";
	}

	@Override
	public String getRuleName() {

		return Globals.RuleNames.STR38_C;
	}

	@Override
	public String getRuleID() {

		return Globals.RuleID.STR38_C;
	}

	@Override
	public String getRuleRecommendation() {
		
		return "Use the proper width function for wide strings and strings.";
	}

	@Override
	public int securityLevel() {

		return Globals.Markers.SECURITY_LEVEL_HIGH;
	}

	@Override
	public String getRuleURL() {
		
		return "https://wiki.sei.cmu.edu/confluence/display/c/STR38-C.+Do+not+confuse+narrow+and+wide+character+strings+and+functions";
	}

	@Override
	public ITranslationUnit getITranslationUnit() {
		// TODO Auto-generated method stub
		return null;
	}

}
