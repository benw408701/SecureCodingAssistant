package edu.csus.plugin.securecodingassistant.rules_C;

import org.eclipse.cdt.core.dom.ast.IASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.csus.plugin.securecodingassistant.Globals;

public class STR34C_CastCharacterstoUnsignedCharBeforeConvertingToLargerIntegerSizes implements IRule_C {

	private boolean ruleViolated;
	private NodeNumPair_C currNode;
	
	@Override
	public boolean violate_CDT(IASTNode node) {
		ruleViolated = false;
		currNode = null;
		
		if(node.getFileLocation().getContextInclusionStatement() == null)
		{
			if(node.getTranslationUnit().getRawSignature().contains("char"))
			{
				
				
				ASTNodeProcessor_C visitDec = new ASTNodeProcessor_C();
				node.getTranslationUnit().accept(visitDec);
				
				if(node instanceof IASTBinaryExpression)
				{
					for(NodeNumPair_C binExp: visitDec.getAssignmentStatements())
					{
						if(node == binExp.getNode())
						{
							currNode = binExp;
						}
					}
					if(currNode == null)
					{
						return false;
					}
					
					IASTNode LHS = ((IASTBinaryExpression) node).getOperand1();
					IASTNode RHS = ((IASTBinaryExpression) node).getOperand2();
					
					if(RHS instanceof IASTFunctionCallExpression)
					{
						return false;
					}
					
					for(VariableNameTypePair ntPair: Utility_C.allVarNameType(visitDec.getvarNamePairList(), LHS))
					{
						if((ntPair.getVarType().contains("long")) || (ntPair.getVarType().contains("int")) || (ntPair.getVarType().contains("short")) )
						{
							for(VariableNameTypePair ntPairRHS: Utility_C.allVarNameType(visitDec.getvarNamePairList(), RHS))
							{
								if((ntPairRHS.getVarType().contains("char")) && !(ntPairRHS.getVarType().contains("unsigned")))
								{
									if(!(RHS.getRawSignature().contains("unsigned")) || !(RHS.getRawSignature().contains("char")))
									{
										ruleViolated = true;
										return ruleViolated;
									}
								}
							}
						}
					}
				}
				else if(node instanceof IASTArraySubscriptExpression)
				{
					IASTNode subscriptExpressionArg = ((IASTArraySubscriptExpression) node).getArgument();
					
					for(VariableNameTypePair ntPair: Utility_C.allVarNameType(visitDec.getvarNamePairList(), subscriptExpressionArg))
					{
						if((ntPair.getVarType().contains("char")) && !(ntPair.getVarType().contains("unsigned")))
						{
							if(!(subscriptExpressionArg.getRawSignature().contains("unsigned")) || !(subscriptExpressionArg.getRawSignature().contains("char")))
							{
								ruleViolated = true;
								return ruleViolated;
							}
						}
					}
				}
				else if(node instanceof IASTDeclaration)
				{
					for(NodeNumPair_C binExp: visitDec.getVariableDeclarations())
					{
						if(node == binExp.getNode())
						{
							currNode = binExp;
						}
					}
					if(currNode == null)
					{
						return false;
					}
					
					if(node instanceof IASTSimpleDeclaration && !(node.getRawSignature().startsWith("enum")))
					{
						IASTNode declSpecifier = ((IASTSimpleDeclaration)node).getDeclSpecifier();
						
						if(((IASTSimpleDeclaration)node).getDeclarators()[0].getInitializer() != null && ((declSpecifier.getRawSignature().contains("int")) ||
								(declSpecifier.getRawSignature().contains("long")) || (declSpecifier.getRawSignature().contains("short"))))
						{
							IASTNode initializerRHS =((IASTSimpleDeclaration)node).getDeclarators()[0].getInitializer();
							
							for(NodeNumPair_C nnPair: visitDec.getFunctionCalls())
							{
								if(initializerRHS.contains(nnPair.getNode()))
								{
									return false;
								}
							}
							
							for(VariableNameTypePair ntPair: Utility_C.allVarNameType(visitDec.getvarNamePairList(), initializerRHS))
							{
								if((ntPair.getVarType().contains("char")) && !(ntPair.getVarType().contains("unsigned")))
								{
									if(!(initializerRHS.getRawSignature().contains("unsigned")) || !(initializerRHS.getRawSignature().contains("char")))
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
		return ruleViolated;
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
