package edu.csus.plugin.securecodingassistant.rules_C;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.csus.plugin.securecodingassistant.Globals;

public class MEM31C_FreeDynamicallyAllocatedMemoryWhenNoLongerNeeded extends SecureCodingRule_C {

	private boolean ruleViolated;
	private boolean isMemFunc = false; 
	private NodeNumPair_C currNode;
	
	@Override
	public boolean violate_CDT(IASTNode node) {
		
		ruleViolated = false;
		isMemFunc = false;
		currNode = null;
		//IASTNode LHSNode = null;
		String LHSVar = "";
		
		if((node.getFileLocation().getContextInclusionStatement() == null))
		{
			if((node instanceof IASTDeclaration && !(node instanceof IASTFunctionDefinition)) || node instanceof IASTBinaryExpression)
			{
				boolean isMatch = false;
				ASTNodeProcessor_C visitorFunc = new ASTNodeProcessor_C();
				Utility_C.getScope(node).accept(visitorFunc);
				
				for(NodeNumPair_C o: visitorFunc.getAssignmentStatements())
				{
					if(node == o.getNode())
					{
						isMatch = true;
						currNode = o;
					}
				}
				
				for(NodeNumPair_C o: visitorFunc.getVariableDeclarations())
				{
					if(node == o.getNode())
					{
						isMatch = true;
						currNode = o;
					}
				}
				
				if(isMatch)
				{
					//checks to see if contains functioncall in a node is malloc/calloc/realloc
					for(NodeNumPair_C o: visitorFunc.getFunctionCalls())
					{
						if(node.contains(o.getNode()) && (o.getNode().getRawSignature().contains("malloc") ||
							(o.getNode().getRawSignature().contains("calloc")) || (o.getNode().getRawSignature().contains("realloc"))))
						{
							isMemFunc = true;
						}
					}
				}
				
				if(isMemFunc)
				{
					int freeCount = 0;
					ASTNodeProcessor_C visitorDec = new ASTNodeProcessor_C();
					node.getTranslationUnit().accept(visitorDec);
					
					if(node instanceof IASTBinaryExpression)
					{
						LHSVar = ((IASTBinaryExpression)node).getOperand1().getRawSignature();
						//LHSVar = LHSVar.replaceAll("\\\\*", "");
					}
					else
					{
						for(VariableNameTypePair LHSName: visitorDec.getvarNamePairList())
						{
							if(node instanceof IASTDeclaration )
							{
								if(node == LHSName.getNode())
								{
									IASTNode tempNode = ((IASTSimpleDeclaration)node).getDeclarators()[0];
									int equalIndex = tempNode.getRawSignature().indexOf("=");
									LHSVar = tempNode.getRawSignature().substring(0, equalIndex);
									
									LHSVar = LHSVar.replace(" ", "");
								}
							}
						}
					}
					if(LHSVar.contains("["))
					{
						int bIndex = LHSVar.indexOf("[");
						LHSVar = LHSVar.substring(0, bIndex);
					}
					LHSVar = LHSVar.replaceAll("\\*", "");
					LHSVar = LHSVar.replace(" ", "");
					//System.out.println("FileName: " + node.getContainingFilename() );
					//System.out.println("LHSVar: " + LHSVar + "\n");
					
					ASTNodeProcessor_C visitorMemFunc = new ASTNodeProcessor_C();
					Utility_C.getScope(node).accept(visitorMemFunc);
					for(NodeNumPair_C nnP : visitorMemFunc.getFunctionCalls())
					{
						if(nnP.getNode().getRawSignature().startsWith("free") && (currNode.getNum() < nnP.getNum()))
						{
							freeCount++;
							//System.out.println("FileName: " + node.getContainingFilename() );
							//System.out.println("LHSVar: " + LHSVar );
							for(String str: Utility_C.getFunctionParameterVarName(((IASTFunctionCallExpression)nnP.getNode())))
							{
								boolean emptyStr = false;
								if(str.contentEquals(""))
								{
									str = ((IASTFunctionCallExpression)nnP.getNode()).getRawSignature();
									int tempIndex = str.indexOf("(");
									//System.out.println("EMPTY_STRING_pre: " + str);
									str = str.substring(tempIndex + 1);
									emptyStr = true;
									//System.out.println("EMPTY_STRING_post: " + str);
								}
								
								//System.out.println("str: " + str );
								
								if(str.contentEquals(LHSVar))
								{
									ruleViolated = false;
									return ruleViolated;
								}
								else if(str.contains(LHSVar) && emptyStr)
								{
									ruleViolated = false;
									return ruleViolated;
								}
								else
								{
									ruleViolated = true;
								}
							}
							//System.out.println("\n");
							
						}
					}
					
					/*
					for(VariableNameTypePair oo: visitorDec.getvarNamePairList())
					{
						
						if(LHSVar.contains(oo.getVarName()))
						{
							ASTNodeProcessor_C visitorMemFunc = new ASTNodeProcessor_C();
							Utility_C.getScope(node).accept(visitorMemFunc);
							for(NodeNumPair_C nnP : visitorMemFunc.getFunctionCalls())
							{
								if(nnP.getNode().getRawSignature().startsWith("free") && (currNode.getNum() < nnP.getNum()))
								{
									System.out.println("FileName: " + node.getContainingFilename() );
									System.out.println("LHSVar: " + LHSVar );
									for(String str: Utility_C.getFunctionParameterVarName(((IASTFunctionCallExpression)nnP.getNode())))
									{
										//System.out.println("str: " + str );
										if(oo.getVarName().contentEquals(str))
										{
											ruleViolated = false;
											return ruleViolated;
										}
										else if(str.contains(LHSVar))
										{
											ruleViolated = false;
											return ruleViolated;
										}
										else
										{
											ruleViolated = true;
										}
									}
									System.out.println("\n");
									
								}
							}
						}
					}
					*/
					if(freeCount == 0)
					{
						ruleViolated = true;
					}
				}
				
			}	
		}
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		
		return "CERT Website- Before the lifetime of the last"
				+ " pointer that stores the return value of a"
				+ " call to a standard memory allocation "
				+ "function has ended, it must be matched by"
				+ " a call to free() with that pointer value.";
	}

	@Override
	public String getRuleName() {
		
		return Globals.RuleNames.MEM31_C;
	}

	@Override
	public String getRuleID() {
		
		return Globals.RuleID.MEM31_C;
	}

	@Override
	public String getRuleRecommendation() {
		
		return "Deallocated dynamically allocated memory with"
				+ "a call to free()";
	}

	@Override
	public int securityLevel() {
		
		return Globals.Markers.SECURITY_LEVEL_MEDIUM;
	}

	@Override
	public String getRuleURL() {
		
		return "https://wiki.sei.cmu.edu/confluence/display/c/MEM31-C.+Free+dynamically+allocated+memory+when+no+longer+needed";
	}

	@Override
	public ITranslationUnit getITranslationUnit() {
		// TODO Auto-generated method stub
		return null;
	}

}
