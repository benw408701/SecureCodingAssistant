package edu.csus.plugin.securecodingassistant.rules_C;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.csus.plugin.securecodingassistant.Globals;

public class INT33C_EnsureDivisionAndRemainderDoNoResultDividebyZeroError extends SecureCodingRule_C {

	private boolean ruleViolated = false;
	
	private ArrayList<String> listNum = new ArrayList<String>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9"));
	
	@Override
	public boolean violate_CDT(IASTNode node) {
		
		ruleViolated = false;
		
		if((node.getFileLocation().getContextInclusionStatement() == null))
		{
			if((node instanceof IASTBinaryExpression) && ( (((IASTBinaryExpression) node).getOperator() == 2) || (((IASTBinaryExpression) node).getOperator() == 19)
					|| (((IASTBinaryExpression) node).getOperator() == 20) || (((IASTBinaryExpression) node).getOperator() == 3) ) )
			{
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
						if(operandLHS.getRawSignature().startsWith(currNum))
						{
							ruleViolated = false;
							return ruleViolated;
						}
					}
				}
				
				ASTNodeProcessor_C visitor = new ASTNodeProcessor_C();
				
				node.getTranslationUnit().accept(visitor);
				int expressionNum = 0;
				
				for(NodeNumPair_C o: visitor.getBinaryExpressions())
				{
					
					
					if(o.getNode() == node)
					{
						expressionNum = o.getNum();
						break;
					}
				}
				
				if(visitor.getIfStatements().isEmpty())
				{
					return true;
				}
				
				for(NodeNumPair_C o: visitor.getIfStatements())
				{
					if(o.getNum() < expressionNum)
					{
						if(Utility_C.isEmbedded(node, o.getNode()) )
						{
							ASTNodeProcessor_C visitor1 = new ASTNodeProcessor_C();
							
							o.getNode().accept(visitor1);
							
							for(NodeNumPair_C oo: visitor1.getConditionalStatements())
							{
									ASTVisitorFindMatch visitor_Find1 = new ASTVisitorFindMatch(operandNameLHS, "FindMatch");
									oo.getNode().accept(visitor_Find1);
									
									ASTVisitorFindMatch visitor_Find2 = new ASTVisitorFindMatch("0", "FindMatch");
									oo.getNode().accept(visitor_Find2);
									
									if(visitor_Find1.isMatch() && visitor_Find2.isMatch())
									{
										ruleViolated = false;
										return ruleViolated;
									}
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
