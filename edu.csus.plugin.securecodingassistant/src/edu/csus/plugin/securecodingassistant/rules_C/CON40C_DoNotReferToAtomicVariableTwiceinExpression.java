package edu.csus.plugin.securecodingassistant.rules_C;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;


import edu.csus.plugin.securecodingassistant.Globals;

public class CON40C_DoNotReferToAtomicVariableTwiceinExpression extends SecureCodingRule_C {

	private boolean ruleViolated;
	//private IASTNode[] listOfDec;
	private ArrayList<IASTNode> flaggedNode = new ArrayList<IASTNode>();
	private ArrayList<IASTNode> listOfDec = new ArrayList<IASTNode>();
	private Iterator<IASTNode> decIT;
	
	private ArrayList<String> listofVarName = new ArrayList<String>();
	private ArrayList<String> listofVarType = new ArrayList<String>();
	
	private Iterator<String> varNameIT;
	private Iterator<String> varTypeIT;
	
	private IASTTranslationUnit currITU = null;
	
	@Override
	public boolean violate_CDT(IASTNode node) {
		
		ruleViolated = false;
		
		//check if TranslationUnit contains "atomic_"
		//if(node.getContainingFilename().contains("CON40C") && node.getTranslationUnit().getRawSignature().contains("atomic_"))
		if(node.getTranslationUnit().getRawSignature().contains("atomic_"))
		{
			
			
			//System.out.println("Node: " + node.getRawSignature());
			//System.out.println("Nodeparentproperty: " + node.getPropertyInParent().toString());
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
		
			
					
			
			if(node instanceof IASTBinaryExpression)
			{
				
				
					//System.out.println("Node IASTBinaryExpression: " + node.getRawSignature());
					//System.out.println("Node PARENTIASTBinaryExpression: " + node.getParent().getRawSignature());
					
					
					while(varNameIT.hasNext())
					{
						String varNameITTemp = varNameIT.next();
						String varTypeITTemp = varTypeIT.next();
						
						if(node.getRawSignature().contains(varNameITTemp))
						{
							//System.out.println("varNameIT: " + varNameITTemp);
							//System.out.println("varTypeIT: " + varTypeITTemp);
							
							ASTVisitorFindMatch visitorFindDup = new ASTVisitorFindMatch(varNameITTemp, "CON40C_findDup");
							
							node.accept(visitorFindDup);
							
							if(visitorFindDup.isDuplicateInExpression())
							{
								//System.out.println("isDuplicateInExpression");
								
								if(varTypeITTemp.contains("atomic_"))
								{
									//System.out.println("Rule Vioalted!!!!");
									if(!flaggedNode.contains(node.getParent()))
									{
										//System.out.println("Rule Vioalted!!!!");
										ruleViolated = true;
										flaggedNode.add(node);
									}
									//System.out.println("NodeParent: " + node.getParent().getRawSignature());
								}
								else
								{
									ASTVisitorFindMatch visitorAssignedAtomic = new ASTVisitorFindMatch(varNameITTemp, "CON40C_assignedAtomic");
									
									node.getTranslationUnit().accept(visitorAssignedAtomic);
									
									if(visitorAssignedAtomic.isMatch())
									{
										if(!flaggedNode.contains(node.getParent()))
										{
											//System.out.println("Rule Vioalted!!!!");
											ruleViolated = true;
											flaggedNode.add(node);
										}
									}
								}
								
							}
						}
						
						}
					
					//System.out.println("");
				//System.out.println("Nodeparentproperty_IASTExpression: " + node.getPropertyInParent().toString());
			
			}
			
			
			if(node instanceof IASTUnaryExpression)
			{
				
				//System.out.println("Node IASTUnaryExpression: " + node.getRawSignature());
				
				if((node.getRawSignature().endsWith("++")) || (node.getRawSignature().endsWith("--")))
				{
					//System.out.println("Node IASTUnaryExpression: " + node.getRawSignature());
					while(varNameIT.hasNext())
					{
						String varNameITTemp = varNameIT.next();
						String varTypeITTemp = varTypeIT.next();
						
						if(node.getRawSignature().contains(varNameITTemp))
						{
							//System.out.println("varNameIT: " + varNameITTemp);
							//System.out.println("varTypeIT: " + varTypeITTemp);
							
							ASTVisitorFindMatch visitorFind = new ASTVisitorFindMatch(varNameITTemp, "CON40C_Expression");
							
							node.accept(visitorFind);
							
							if(visitorFind.isMatch())
							{
								//System.out.println("isMatch");
								
								if(varTypeITTemp.contains("atomic_"))
								{
									//System.out.println("Rule Vioalted!!!!");
									if(!flaggedNode.contains(node.getParent()))
									{
										//System.out.println("Rule Vioalted!!!!");
										ruleViolated = true;
										flaggedNode.add(node);
									}
									//System.out.println("NodeParent: " + node.getParent().getRawSignature());
								}
								else
								{
									ASTVisitorFindMatch visitorAssignedAtomic2 = new ASTVisitorFindMatch(varNameITTemp, "CON40C_assignedAtomic");
									
									node.getTranslationUnit().accept(visitorAssignedAtomic2);
									
									if(visitorAssignedAtomic2.isMatch())
									{
										if(!flaggedNode.contains(node.getParent()))
										{
											//System.out.println("Rule Vioalted!!!!");
											ruleViolated = true;
											flaggedNode.add(node);
										}
									}
								}
								
							}
						}
						
						}
				}
				
				//System.out.println("Nodeparentproperty_IASTExpression: " + node.getPropertyInParent().toString());
			
			}
			
		}
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
		return "CERT Website- If the same atomic variable "
				+ "appears twice in an expression, then two atomic reads, or an "
				+ "atomic read and an atomic write, are required. Such a pair of "
				+ "atomic operations is not thread-safe, as another thread can "
				+ "modify the atomic variable between the two operations.";
	}

	@Override
	public String getRuleName() {
		return Globals.RuleNames.CON40_C;
	}

	@Override
	public String getRuleID() {
		return Globals.RuleID.CON40_C;
	}

	@Override
	public String getRuleRecommendation() {
		//TODO
		return "Do not use an atomic variable or reference to atomic variable in the same expression";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_MEDIUM;
	}


	@Override
	public String getRuleURL() {
		
		return "https://wiki.sei.cmu.edu/confluence/display/c/CON40-C.+Do+not+refer+to+an+atomic+variable+twice+in+an+expression";
	}



}
