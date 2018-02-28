package edu.csus.plugin.securecodingassistant.rules_C;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.csus.plugin.securecodingassistant.Globals;

public class ARR36C_DoNotSubtractorCompareTwoPointersThatDoNotReferToSameArray implements IRule_C {

	private boolean ruleViolated = false;
	private IASTTranslationUnit currITU = null;
	private ArrayList<IASTNode> listOfDec = new ArrayList<IASTNode>();
	private Iterator<IASTNode> decIT;
	
	private ArrayList<String> listofVarName = new ArrayList<String>();
	private ArrayList<String> listofVarType = new ArrayList<String>();
	
	private Iterator<String> varNameIT;
	private Iterator<String> varTypeIT;
	
	private ArrayList<String> listofArrVarName = new ArrayList<String>();
	private ArrayList<String> listofPointerVarName = new ArrayList<String>();
	//private ArrayList<String> listofArrVarType = new ArrayList<String>();
	
	private Iterator<String> arrVarNameIT;
	private Iterator<String> pointerVarNameIT;
	
	private IASTNode parent;
	
	private boolean once = true;
	@Override
	public boolean violate_CDT(IASTNode node) {
		ruleViolated = false;
		parent = null;
		
		/*
		if(node.getContainingFilename().contains("Test123"))
		{
			if(once)
			{
				System.out.println("Test123");
				System.out.println("node: " + node.getRawSignature());
			
				ASTNodeProcessor_C visitor = new ASTNodeProcessor_C();
				node.getTranslationUnit().accept(visitor);
				once = false;
			}
			return false;
		}
		*/
		//if(node.getContainingFilename().contains("ARR36C"))
		//{
		
			if(currITU == null || currITU != node.getTranslationUnit())
			{
				listOfDec.clear();
				listofVarName.clear();
				listofVarType.clear();
				listofArrVarName.clear();
				listofPointerVarName.clear();
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
			arrVarNameIT = listofArrVarName.iterator();
			pointerVarNameIT = listofPointerVarName.iterator();
			decIT = listOfDec.iterator();
						
			//remove arrayVar from list of variables
			while(arrVarNameIT.hasNext())
			{
				//System.out.println("AM I HERE?");
				listofVarName.remove(arrVarNameIT.next());
				//System.out.println("AM I HERE!!!!!!!?");
			}
			
		
		if(node instanceof IASTBinaryExpression)
		{
			//check if one of the variables is an array
			// or if variable is a pointer to an array
			
			//System.out.println("IASTBinaryExpression: " + node.getRawSignature());
			
			/*
			if((node.getRawSignature().contains("<")) || (node.getRawSignature().contains(">")))
			{
				System.out.println("IASTBinaryExpression_RELATIONSSSS: " + node.getRawSignature());
				
			}
			*/
			
			//check all IASTBinary Expression with "=" to see if an Array value was assigned to another variable
			if((node.getRawSignature().contains("=")) )
			{
				//System.out.println("IASTBinaryExpression: " + node.getRawSignature());
				//System.out.println("IASTBinaryExpressionChildRHS: " + node.getChildren()[1].getRawSignature());
				
				//System.out.println("IASTBinaryExpressionChildLHS: " + node.getChildren()[0].getRawSignature());
				
				IASTNode RHS = node.getChildren()[1];//get RHS child of node
				IASTNode LHS = node.getChildren()[0];//get LHS child of node
				//node = parent;
				
				/*
				while(!(parent instanceof IASTFunctionDefinition))
				{
					parent = parent.getParent();
				}
				*/
				
				arrVarNameIT = listofArrVarName.iterator();
				while(arrVarNameIT.hasNext())
				{
					String findMatch = arrVarNameIT.next();
					ASTVisitorFindMatch visitor1 = new ASTVisitorFindMatch(findMatch, "FindMatch");
					RHS.accept(visitor1);
					
					if(visitor1.isMatch())
					{
						//System.out.println("Arr in expression: " + node.getRawSignature());
						//System.out.println("nodeChild: " +LHS.getRawSignature());
						
						while(pointerVarNameIT.hasNext())
						{
							String findPointMatch = pointerVarNameIT.next();
							
							//System.out.println("findPointerMatch: " + findPointMatch);
							
							if(LHS.getRawSignature().contentEquals(findPointMatch))
							{
								//System.out.println("Match LHS");
								listofVarName.remove(findPointMatch);
								listofArrVarName.add(findPointMatch);
								break;
							}
							
							
						}
						
						break;
					}
				}
				
				varNameIT = listofVarName.iterator();
				//varTypeIT = listofVarType.iterator();
				//arrVarNameIT = listofArrVarName.iterator();
				pointerVarNameIT = listofPointerVarName.iterator();
				
				/*
				while(arrVarNameIT.hasNext())
				{
					//System.out.println("AM I HERE?");
					listofVarName.remove(arrVarNameIT.next());
					//System.out.println("AM I HERE!!!!!!!?");
				}
				*/
				
				/*
				varNameIT = listofVarName.iterator();
				while(varNameIT.hasNext())
				{
					System.out.println("AFTER REMOVAL!!!!!!: " + varNameIT.next());
				}
				*/
				
				//arrVarNameIT = listofArrVarName.iterator();
				//while(arrVarNameIT.hasNext())
				//{
					
				//}
				/*
				arrVarNameIT = listofArrVarName.iterator();
				while(arrVarNameIT.hasNext())
				{
					System.out.println("ARR!!!!!!: " + arrVarNameIT.next());
				}
				*/
				
				
				
			}
			//end of ifStatement: contains "=" 
			
			//start of ifStatement: contains "<", ">" or "-"
			if(((node.getRawSignature().contains("-")) || (node.getRawSignature().contains("<")) || (node.getRawSignature().contains(">"))) 
					&& !(node.getRawSignature().contains("=")))
			{
				//System.out.println("IASTBinaryExpression '><-' : " + node.getRawSignature());
				arrVarNameIT = listofArrVarName.iterator();
				while(arrVarNameIT.hasNext())
				{
					//if arrVar is contained in this BINary Expression, check if there is also a Non-array variable
					//arrVarNameIT.next();
					String findMatch2 = arrVarNameIT.next();
					ASTVisitorFindMatch visitor2 = new ASTVisitorFindMatch(findMatch2, "FindMatch");
					
					node.accept(visitor2);
					if(visitor2.isMatch())
					{
						//System.out.println("IS MACTH ><-");
						
						varNameIT = listofVarName.iterator();						
						while(varNameIT.hasNext())
						{
							String findMatch3 = varNameIT.next();
							ASTVisitorFindMatch visitor3 = new ASTVisitorFindMatch(findMatch3, "FindMatch");
							
							node.accept(visitor3);
							
							if(visitor3.isMatch())
							{
								//System.out.println("Rule VIOLATED!!.");
								ruleViolated = true;
								return ruleViolated;
							}
						}
						
					}
				}
			}
			//end of ifStatement: contains "=" 
			
			//System.out.println("");
			
		}
		else if((node instanceof IASTDeclaration) && !(node instanceof IASTFunctionDefinition))
		{
			if(node.getRawSignature().contains("=") && (node.getChildren().length > 1))
			{
				//System.out.println("IASTDeclaration: " + node.getRawSignature());
				
				
				//System.out.println("IASTDeclaration_LHS: " + node.getChildren()[0].getRawSignature());
				//System.out.println("IASTDeclaration_RHS: " + node.getChildren()[1].getRawSignature());
				if(node.getChildren()[1].getRawSignature().startsWith("*") && node.getChildren()[1].getChildren().length == 3)
				{
					//System.out.println("Number of children_RHS: " + node.getChildren()[1].getChildren().length);
					//System.out.println("IASTDeclaration_RHS_LHS: " + node.getChildren()[1].getChildren()[0].getRawSignature());
					//System.out.println("IASTDeclaration_RHS_RHS1: " + node.getChildren()[1].getChildren()[1].getRawSignature());
					//System.out.println("IASTDeclaration_RHS_RHS2: " + node.getChildren()[1].getChildren()[2].getRawSignature());
					
					IASTNode RHS = node.getChildren()[1].getChildren()[2];
					IASTNode LHS = node.getChildren()[1].getChildren()[1];
							
					arrVarNameIT = listofArrVarName.iterator();
					pointerVarNameIT= listofPointerVarName.iterator();
					
					while(arrVarNameIT.hasNext())
					{
						String findMatch = arrVarNameIT.next();
						ASTVisitorFindMatch visitor1 = new ASTVisitorFindMatch(findMatch, "FindMatch");
						RHS.accept(visitor1);
						
						if(visitor1.isMatch())
						{
							//System.out.println("Arr in expression: " + node.getRawSignature());
							//System.out.println("nodeChild: " +LHS.getRawSignature());
							
							while(pointerVarNameIT.hasNext())
							{
								String findPointMatch = pointerVarNameIT.next();
								
								//System.out.println("findPointerMatch: " + findPointMatch);
								
								if(LHS.getRawSignature().contentEquals(findPointMatch))
								{
									//System.out.println("Match LHS");
									listofVarName.remove(findPointMatch);
									listofArrVarName.add(findPointMatch);
									break;
								}
								
								
							}
							
							break;
						}
					}
				}
				
				/*
				varNameIT = listofVarName.iterator();
				while(varNameIT.hasNext())
				{
					System.out.println("AFTER REMOVAL!!!!!!: " + varNameIT.next());
				}
				*/
			}
			
			//System.out.println("");
		}
		//}
		return ruleViolated;
	}

	//function modified for this RULE!!!!
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
		
		return "When two pointers are subtract or compared using the relational"
				+ "operators <, <=, >= and >, both must point to elements of the"
				+ "same array object or just one past the last element of the"
				+ "array object.";
	}

	@Override
	public String getRuleName() {
		
		return Globals.RuleNames.ARR36_C;
	}

	@Override
	public String getRuleID() {
		
		return Globals.RuleID.ARR36_C;
	}

	@Override
	public String getRuleRecommendation() {
		
		return "Make sure pointers that are subtracted or comapared point to the same"
				+ "array object.";
	}

	@Override
	public int securityLevel() {
		
		return Globals.Markers.SECURITY_LEVEL_MEDIUM;
	}

	@Override
	public String getRuleURL() {
		
		return "https://wiki.sei.cmu.edu/confluence/display/c/ARR36-C.+Do+not+subtract+or+compare+two+pointers+that+do+not+refer+to+the+same+array";
	}

	@Override
	public ITranslationUnit getITranslationUnit() {
		// TODO Auto-generated method stub
		return null;
	}

}
