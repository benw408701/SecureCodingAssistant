package edu.csus.plugin.securecodingassistant.rules_C;

import java.util.ArrayList;
import java.util.Arrays;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;

import edu.csus.plugin.securecodingassistant.Globals;

public class FIO45C_AvoidTOCTOURaceConditionsWhileAccessingFiles extends SecureCodingRule_C {
	
	private boolean ruleViolated;
	private ArrayList<String> fileFunctions = new ArrayList<String>(
			Arrays.asList("fopen", "open", "fdopen")
			);

	@Override
	public boolean violate_CDT(IASTNode node) {
		
		ruleViolated = false;
		NodeNumPair_C curr_Node = null;
		ArrayList<String> currNodeParameter = new ArrayList<String>();
		
		if((node.getFileLocation().getContextInclusionStatement() == null))
		{
		if(node instanceof IASTFunctionCallExpression)
		{
			String functionName = ((IASTFunctionCallExpression)node).getFunctionNameExpression().getRawSignature();
			if(fileFunctions.contains(functionName))
			{
				currNodeParameter.clear();
				
				ASTNodeProcessor_C visitScope = new ASTNodeProcessor_C();
				Utility_C.getScope(node).accept(visitScope);
				
				//get current Nodes NodeNumPair_C
				for(NodeNumPair_C o: visitScope.getFunctionCalls())
				{
					if(o.getNode() == node)
					{
						curr_Node = o;
						currNodeParameter = Utility_C.getFunctionParameterVarName((IASTFunctionCallExpression) node);
					}
				}
				
				for(NodeNumPair_C o: visitScope.getFunctionCalls())
				{
					String allFileFuncScope = ((IASTFunctionCallExpression)o.getNode()).getFunctionNameExpression().getRawSignature();
					if(fileFunctions.contains(allFileFuncScope))
					{
						if(o != curr_Node && o.getNum() < curr_Node.getNum())
						{
							ArrayList<String> otherNodeParameter = new ArrayList<String>();
							otherNodeParameter = Utility_C.getFunctionParameterVarName((IASTFunctionCallExpression) o.getNode());
							
							if(!functionName.contentEquals("fdopen"))
							{
								if(currNodeParameter.get(0).contentEquals(otherNodeParameter.get(0)))
								{
									if(!(otherNodeParameter.get(1).contains("wx")) && !(otherNodeParameter.get(1).contains("O_CREAT"))
										&& !(otherNodeParameter.get(1).contains("O_EXCL")))
									{
										ruleViolated = true;
										return ruleViolated;
									}
								
								}
							}
							else
							{
								ASTNodeProcessor_C visitorVarName = new ASTNodeProcessor_C();
								node.getTranslationUnit().accept(visitorVarName);
								String fileVarName;
								for(NodeNumPair_C assignState: visitorVarName.getAssignmentStatements())
								{
									if(assignState.getNode().contains(o.getNode()))
									{
										fileVarName = ((IASTBinaryExpression)assignState.getNode()).getOperand1().getRawSignature();

										if(currNodeParameter.get(0).contentEquals(fileVarName))
										{
											if(!(otherNodeParameter.get(1).contains("wx")) && !(otherNodeParameter.get(1).contains("O_CREAT"))
												&& !(otherNodeParameter.get(1).contains("O_EXCL")))
											{
												ruleViolated = true;
												return ruleViolated;
											}
										}
									}
								}
								
								for(NodeNumPair_C decs: visitorVarName.getVariableDeclarations())
								{
									if(decs.getNode().contains(o.getNode()))
									{
										fileVarName = ((IASTSimpleDeclaration)decs.getNode()).getDeclarators()[0].getName().getRawSignature();
										
										if(currNodeParameter.get(0).contentEquals(fileVarName))
										{
											if(!(otherNodeParameter.get(1).contains("wx")) && !(otherNodeParameter.get(1).contains("O_CREAT"))
												&& !(otherNodeParameter.get(1).contains("O_EXCL")))
											{
												ruleViolated = true;
												return ruleViolated;
											}
										
										}
									}
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
		return "CERT Website-A program that performs two or more file operations on a "
				+"single file name or path name creates a race window between "
				+"the two file operations. This race window comes from the "
				+"assumption that the file name or path name refers to the "
				+"same resource both times.";
	}

	@Override
	public String getRuleName() {
		return Globals.RuleNames.FIO45_C;
	}

	@Override
	public String getRuleID() {
		return Globals.RuleID.FIO45_C;
	}

	@Override
	public String getRuleRecommendation() {
		
		return "Perform one one file operation on a sinlge file or open with appropriate flags.";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_MEDIUM;
	}


	@Override
	public String getRuleURL() {
		
		return "https://wiki.sei.cmu.edu/confluence/display/c/FIO45-C.+Avoid+TOCTOU+race+conditions+while+accessing+files";
	}

}
