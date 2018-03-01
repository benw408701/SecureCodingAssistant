package edu.csus.plugin.securecodingassistant.rules_C;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.csus.plugin.securecodingassistant.Globals;

public class INT33C_EnsureDivisionAndRemainderDoNoResultDividebyZeroError implements IRule_C {

	private boolean ruleViolated = false;
	
	private ArrayList<String> listNum = new ArrayList<String>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9"));
	
	@Override
	public boolean violate_CDT(IASTNode node) {
		// TODO Auto-generated method stub
		
		ruleViolated = false;
		
		if((node.getFileLocation().getContextInclusionStatement() == null))
		{
			if((node instanceof IASTBinaryExpression) && ( (((IASTBinaryExpression) node).getOperator() == 2) || (((IASTBinaryExpression) node).getOperator() == 19)
					|| (((IASTBinaryExpression) node).getOperator() == 20) || (((IASTBinaryExpression) node).getOperator() == 3) ) )
			{
					
				//System.out.println("Node: " + node.getRawSignature());
				int operator = (((IASTBinaryExpression) node).getOperator());
				//System.out.println("Operator: " + operator);
				
				
				IASTNode operandLHS = ((IASTBinaryExpression)node).getOperand2();
				String operandNameLHS = operandLHS.getRawSignature();
				
				if(operandLHS.getRawSignature().equals("0"))
				{
					ruleViolated = true;
					return ruleViolated;
				}
				else
				{
					for(String currNum : listNum)
					{
						//System.out.println("currNum: " + currNum);
						if(operandLHS.getRawSignature().startsWith(currNum))
						{
							ruleViolated = false;
							return ruleViolated;
						}
					}
				}
				
				//System.out.println("operandLHS: " + operandLHS.getRawSignature());
				
				ASTNodeProcessor_C visitor = new ASTNodeProcessor_C();
				
				node.getTranslationUnit().accept(visitor);
				IASTNode BinaryExpressionNode = null;;
				int expressionNum = 0;
				String expressionName = null;
				for(NodeNumPair_C o: visitor.getBinaryExpressions())
				{
					
					
					if(o.getNode() == node)
					{
						BinaryExpressionNode = o.getNode();
						expressionNum = o.getNum();
						break;
					}
				}
				//System.out.println("BinaryExpressionNode: " + BinaryExpressionNode.getRawSignature());
				//System.out.println("BinaryExpressionNode_NUM: " + expressionNum );
				
				if(visitor.getIfStatements().isEmpty())
				{
					return true;
				}
				
				for(NodeNumPair_C o: visitor.getIfStatements())
				{
					if(o.getNum() < expressionNum)
					{
						//System.out.println("INSIDE GETIFSTATEMENTS: " + o.getNode().getRawSignature());
						if(Utility_C.isEmbedded(node, o.getNode()) )
						{
							//System.out.println("getIFStatements: " + o.getNode().getRawSignature());
							
							ASTNodeProcessor_C visitor1 = new ASTNodeProcessor_C();
							
							o.getNode().accept(visitor1);
							
							for(NodeNumPair_C oo: visitor1.getConditionalStatements())
							{
								IASTNode currNode = oo.getNode();
								//System.out.println("currNode: " + currNode.getRawSignature());
								//if((((IASTBinaryExpression)currNode).getOperator() == 10 ) || (((IASTBinaryExpression)currNode).getOperator() == 11 ) ||
								//		(((IASTBinaryExpression)currNode).getOperator() == 8 ) || (((IASTBinaryExpression)currNode).getOperator() == 9 ||
								//				(((IASTBinaryExpression)currNode).getOperator() == 29 )|| (((IASTBinaryExpression)currNode).getOperator() == 28 )))
								//{
									//System.out.println("getBinaryExpressions: " + oo.getNode().getRawSignature());
									
									ASTVisitorFindMatch visitor_Find1 = new ASTVisitorFindMatch(operandNameLHS, "FindMatch");
									oo.getNode().accept(visitor_Find1);
									
									ASTVisitorFindMatch visitor_Find2 = new ASTVisitorFindMatch("0", "FindMatch");
									oo.getNode().accept(visitor_Find2);
									
									if(visitor_Find1.isMatch() && visitor_Find2.isMatch())
									{
										ruleViolated = false;
										return ruleViolated;
									}
											
								//}
								
							}
							
						}
					}
					ruleViolated = true;
				}
			}

		}
		
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		
		return "CERT Website- Ensure that division and remainder operations do not result in divide-by-zero errors.";
	}

	@Override
	public String getRuleName() {
		return Globals.RuleNames.INT33_C;
	}

	@Override
	public String getRuleID() {
		return Globals.RuleID.INT33_C;
	}

	@Override
	public String getRuleRecommendation() {
		
		return "Check if second operand in division or remainder operation is the vaule 0";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_LOW;
	}

	@Override
	public String getRuleURL() {
		return "https://wiki.sei.cmu.edu/confluence/display/c/INT33-C.+Ensure+that+division+and+remainder+operations+do+not+result+in+divide-by-zero+errors";
	}

	@Override
	public ITranslationUnit getITranslationUnit() {
		// TODO Auto-generated method stub
		return null;
	}

}
