package edu.csus.plugin.securecodingassistant.rules_C;

import java.util.ArrayList;
import java.util.Arrays;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * <b><i>The text and/or code below is from the CERT website:
 * <a target="_blank"href="https://wiki.sei.cmu.edu/confluence/display/seccode">
 * https://wiki.sei.cmu.edu/confluence/display/seccode </a></i></b>
 * <p>
 * C Secure Coding Rule: FIO45-C. Avoid TOCTOU race conditions while accessing
 * files
 * </p>
 * <p>
 * A TOCTOU (time-of-check, time-of-use) race condition is possible when two or
 *  more concurrent processes are operating on a shared file system 
 *  [Seacord 2013b]. Typically, the first access is a check to verify some
 *  attribute of the file, followed by a call to use the file. An attacker
 *  can alter the file between the two accesses, or replace the file with a 
 *  symbolic or hard link to a different file. These TOCTOU conditions can be 
 *  exploited when a program performs two or more file operations on the same 
 *  file name or path name.
 *   
 * </p>
 * 
 * <p>
 * A program that performs two or more file operations on a single file name 
 * or path name creates a race window between the two file operations. This 
 * race window comes from the assumption that the file name or path name refers 
 * to the same resource both times. If an attacker can modify the file, remove 
 * it, or replace it with a different file, then this assumption will not hold.
 * </p>
 * 
 * @author Victor Melnik (Plugin Logic), CERT (Rule Definition)
 * @see C Secure Coding Rule define by CERT: <a target="_blank" 
 * href="https://wiki.sei.cmu.edu/confluence/display/c/FIO45-C.+Avoid+TOCTOU+
 * race+conditions+while+accessing+files">FIO45-C</a>
 *
 */

public class FIO45C_AvoidTOCTOURaceConditionsWhileAccessingFiles extends SecureCodingRule_C {
	
	private boolean ruleViolated;
	private ArrayList<String> fileFunctions = new ArrayList<String>(
			Arrays.asList("fopen", "open", "fdopen", "OPEN", "ACCESS", "STAT", 
					"stat", "fstat", "FSTAT", "access")
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
				
				IASTIfStatement ifStm = null;
				for(NodeNumPair_C oo: visitScope.getIfStatements())
				{
					if(oo.getNode().contains(node))
					{
						ifStm =((IASTIfStatement)oo.getNode());
						break;
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
							
							//check to see if both file operations are not in the same ifstatment body
							if(ifStm == null)
							{
								//do nothing
							}
							else if(ifStm.contains(o.getNode()))
							{
								if(ifStm.getElseClause() == null)
								{
									//do nothing
								}
								else if(ifStm.getElseClause().contains(node) && !ifStm.getElseClause().contains(o.getNode()))
								{
									return false;
								}
							}
							
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
