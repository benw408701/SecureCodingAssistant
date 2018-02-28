package edu.csus.plugin.securecodingassistant.rules_C;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.cdt.core.dom.ast.IASTArrayDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTArrayModifier;
import org.eclipse.cdt.core.dom.ast.IASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IArrayType;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.csus.plugin.securecodingassistant.Globals;

public class STR34C_CastCharacterstoUnsignedCharBeforeConvertingToLargerIntegerSizes implements IRule_C {

	private boolean ruleViolated;
	
	private IASTTranslationUnit currITU = null;
	private ArrayList<IASTNode> listOfDec = new ArrayList<IASTNode>();
	private Iterator<IASTNode> decIT;
	
	private ArrayList<String> listofVarName = new ArrayList<String>();
	private ArrayList<String> listofVarType = new ArrayList<String>();
	
	private Iterator<String> varNameIT;
	private Iterator<String> varTypeIT;
	
	
	private ArrayList<String> listofCharVarName = new ArrayList<String>(); //only includes "char" and not "unsigned char"
	private ArrayList<String> listofIntegerVarName = new ArrayList<String>(); //only includes integer and long variables
	
	private Iterator<String> pointerCharNameIT;
	private Iterator<String> pointerIntegerNameIT;
	//might not be needed
	private ArrayList<String> listofArrVarName = new ArrayList<String>();
	private ArrayList<String> listofPointerVarName = new ArrayList<String>();
	//private ArrayList<String> listofArrVarType = new ArrayList<String>();
	
	private Iterator<String> arrVarNameIT;
	private Iterator<String> pointerVarNameIT;
	
	@Override
	public boolean violate_CDT(IASTNode node) {
		ruleViolated = false;
		//currITU = null;
		
		//if(node.getContainingFilename().contains("STR34"))
		if(node.getFileLocation().getContextInclusionStatement() == null)
		{
			if(node.getTranslationUnit().getRawSignature().contains("char"))
			{
				//System.out.println("node: " + node.getRawSignature());
				
				if(currITU == null || currITU != node.getTranslationUnit())
				{
					listOfDec.clear();
					listofVarName.clear();
					listofVarType.clear();
					listofArrVarName.clear();
					listofPointerVarName.clear();
					listofCharVarName.clear();
					listofIntegerVarName.clear();
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
							
							//listofVarName.contains(arg0)
							listofVarName.add(varName);
							listofVarType.add(varType);
							
							if((varType.contains("char")) && !(varType.contains("unsigned"))  )
							{
								if(listofArrVarName.size() > 0)
								{
							
								
									if(!(listofArrVarName.get(listofArrVarName.size() - 1) == varName))
									{
									listofCharVarName.add(varName);
									}
								}
								else
								{
									listofCharVarName.add(varName);
								}
							}
							else if((varType.contains("int")) || (varType.contains("long")))
							{
								if(listofArrVarName.size() > 0)
								{
							
								
									if(!(listofArrVarName.get(listofArrVarName.size() - 1) == varName))
									{
										listofIntegerVarName.add(varName);
									}
								}
								else
								{
									listofIntegerVarName.add(varName);
								}
							}
							
							//System.out.println("varName: " + varName);
							//System.out.println("varType: " + varType);
							//System.out.println("");
							
					}
					decIT = listOfDec.iterator();
				}
				
				varNameIT = listofVarName.iterator();
				varTypeIT = listofVarType.iterator();
				pointerCharNameIT = listofCharVarName.iterator();
				pointerIntegerNameIT= listofIntegerVarName.iterator();
				
				arrVarNameIT = listofArrVarName.iterator();
				pointerVarNameIT = listofPointerVarName.iterator();
				decIT = listOfDec.iterator();
				
				/*
				while(pointerCharNameIT.hasNext())
				{
					System.out.println("Chars: " +pointerCharNameIT.next());
				}
				
				while(arrVarNameIT.hasNext())
				{
					System.out.println("Arr: " +arrVarNameIT.next());
				}
				
				while(pointerIntegerNameIT.hasNext())
				{
					System.out.println("int: " +pointerIntegerNameIT.next());
				}
				*/
							
				
				if(node instanceof IASTBinaryExpression)
				{
					
					if(((IASTBinaryExpression) node).getOperator() == 17)
					{
						//System.out.println("IASTBinaryExpression: " + node.getRawSignature() );
						IASTNode LHS = ((IASTBinaryExpression) node).getOperand1();
						IASTNode RHS = ((IASTBinaryExpression) node).getOperand2();
						
						//((IASTBinaryExpression) node).getOperator()
						//System.out.println("Operator: "+ ((IASTBinaryExpression) node).getOperator());
						//System.out.println("LHS: " + LHS.getRawSignature() );
						//System.out.println("RHS: " + RHS.getRawSignature() );
						
						pointerCharNameIT = listofCharVarName.iterator();
						pointerIntegerNameIT= listofIntegerVarName.iterator();
						while(pointerCharNameIT.hasNext())
						{
							ASTVisitorFindMatch visitor = new ASTVisitorFindMatch(pointerCharNameIT.next(),"FindMatch");
							RHS.accept(visitor);
							
							if(visitor.isMatch())
							{
								//System.out.println("Match!!");
								if(listofIntegerVarName.contains(LHS.getRawSignature()))
								{
									//System.out.println("Integer on LHS!!!!");
									if(!(RHS.getRawSignature().contains("(unsigned char)")))
									{
										//System.out.println("RULE VIOLATED!!!");
										ruleViolated = true;
										
									}
								}
								break;
							}
						}
					}
					//System.out.println();
					
				}
				else if(node instanceof IASTArraySubscriptExpression)
				{
					//System.out.println("IASTArraySubscriptExpression: " + node.getRawSignature() );
					
					pointerCharNameIT = listofCharVarName.iterator();
					
					while(pointerCharNameIT.hasNext())
					{
						ASTVisitorFindMatch visitor1 = new ASTVisitorFindMatch(pointerCharNameIT.next(),"FindMatch");
						node.accept(visitor1);
						
						if(visitor1.isMatch())
						{
							//System.out.println("MATCH!");
							
							if(!(node.getRawSignature().contains("(unsigned char)")))
							{
								//System.out.println("RULE VIOLATED!!!");
								ruleViolated = true;
								break;
								
							}
							
						}
					}
					
				}
				else if((node instanceof IASTDeclaration) && !(node instanceof IASTFunctionDefinition))
				{
					if(node.getRawSignature().contains("="))
					{
						//System.out.println("IASTDeclaration: " + node.getRawSignature());
						
						String checkIfintLong;
						
						checkIfintLong = getVarType(node);
						
						if((checkIfintLong.contains("int")) || (checkIfintLong.contains("long")))
						{
							//System.out.println("Match");
							
							pointerCharNameIT = listofCharVarName.iterator();
							
							while(pointerCharNameIT.hasNext())
							{
								ASTVisitorFindMatch visitor2 = new ASTVisitorFindMatch(pointerCharNameIT.next(), "FindMatch");
								node.accept(visitor2);
								
								if(visitor2.isMatch())
								{
									if(!(node.getRawSignature().contains("(unsigned char)")))
									{
										//System.out.println("ruleviolated!!!");
										ruleViolated = true;
										break;
									}
									
								}
							}
						}
						
						
						
						/*
						if(node.getChildren().length > 1)
						{
							IASTNode LHS = node.getChildren()[1].getChildren()[0];
							IASTNode RHS = node.getChildren()[1].getChildren()[1];
							System.out.println("ChildLHS: " + LHS.getRawSignature());
							System.out.println("ChildRHS: " + RHS.getRawSignature());
						}
						*/
					}
					//System.out.println();
				}
				
			}
		}
		return ruleViolated;
	}

	
	//function modified for this RULE!!!!
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
					
					listofPointerVarName.add(str);
				}
				
				if(astN.getRawSignature().contains("[") && astN.getRawSignature().contains("]") )
				{
					listofArrVarName.add(str);
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
					listofPointerVarName.add(str);
				}
				else if(str.startsWith("*"))
				{
					str = str.substring(1);
					//System.out.println("*SUBSTRING: " + str);
					listofPointerVarName.add(str);
				}
				else if (str.contains("["))
				{
					int pos = str.indexOf("[");
					//System.out.println("Position of [ :" + pos);
					
					str = str.substring(0, pos);
					//System.out.println("substring arr: " + str);
					listofArrVarName.add(str);
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
		
		return "CERT Website- Signed character data must be converted "
				+ "to unsigned char before being assigned or converted "
				+ "to a larger signed type. This rule applies to both "
				+ "signed char and (plain) char characters.";
	}

	@Override
	public String getRuleName() {
		
		return Globals.RuleNames.STR34_C;
	}

	@Override
	public String getRuleID() {
		
		return Globals.RuleID.STR34_C;
	}

	@Override
	public String getRuleRecommendation() {
		
		return "Cast 'signed char' and 'char' values to 'unsigned char' before"
				+ "assignment to an integer or array index.";
	}

	@Override
	public int securityLevel() {

		return Globals.Markers.SECURITY_LEVEL_MEDIUM;
	}

	@Override
	public String getRuleURL() {
		
		return "https://wiki.sei.cmu.edu/confluence/display/c/STR34-C.+Cast+characters+to+unsigned+char+before+converting+to+larger+integer+sizes";
	}

	@Override
	public ITranslationUnit getITranslationUnit() {
		// TODO Auto-generated method stub
		return null;
	}

}
