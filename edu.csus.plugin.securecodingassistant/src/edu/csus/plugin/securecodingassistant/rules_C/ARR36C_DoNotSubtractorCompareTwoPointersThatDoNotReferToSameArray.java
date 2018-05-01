package edu.csus.plugin.securecodingassistant.rules_C;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * <b><i>The text and/or code below is from the CERT website:
 * <a target="_blank"href="https://wiki.sei.cmu.edu/confluence/display/seccode">
 * https://wiki.sei.cmu.edu/confluence/display/seccode </a></i></b>
 * <p>
 * C Secure Coding Rule: ARR36-C. Do not subtract or compare two pointers that do
 * not refere to the same array.
 * </p>
 * <p>
 * CERT Website: When two pointers are subtracted, both must point to elements of the same array 
 * object or just one past the last element of the array object (C Standard, 6.5.6 
 * [ISO/IEC 9899:2011]); the result is the difference of the subscripts of the two 
 * array elements. Otherwise, the operation is undefined behavior.
 * </p>
 * 
 * <p>
 * Similarly, comparing pointers using the relational operators <, <=, >=, and > 
 * gives the positions of the pointers relative to each other. Subtracting or 
 * comparing pointers that do not refer to the same array is undefined behavior.
 * 
 * </p>
 * 
 * <p>
 * Comparing pointers using the equality operators == and != has well-defined 
 * semantics regardless of whether or not either of the pointers is null, points
 *  into the same object, or points one past the last element of an array object
 *   or function.
 * </p>
 * 
 * @author Victor Melnik (Plugin Logic), CERT (Rule Definition)
 * @see C Secure Coding Rule define by CERT: <a target="_blank" 
 * href="https://wiki.sei.cmu.edu/confluence/display/c/ARR36-C.+
 * Do+not+subtract+or+compare+two+pointers+that+do+not+refer+to+
 * the+same+array">ARR36-C</a>
 *
 */

public class ARR36C_DoNotSubtractorCompareTwoPointersThatDoNotReferToSameArray extends SecureCodingRule_C {

	private boolean ruleViolated = false;

	@Override
	public boolean violate_CDT(IASTNode node) {
		ruleViolated = false;
		
		if((node.getFileLocation().getContextInclusionStatement() == null))
		{
			if(node instanceof IASTBinaryExpression)
			{
				int operatorNum = ((IASTBinaryExpression)node).getOperator();
				
				if((operatorNum == 8) || (operatorNum == 9) || (operatorNum == 10) ||
						(operatorNum == 11) || (operatorNum == 5))
				{
					IASTExpression LHS = ((IASTBinaryExpression)node).getOperand1();
					IASTExpression RHS = ((IASTBinaryExpression)node).getOperand2();
					
					ASTNodeProcessor_C visitor = new ASTNodeProcessor_C();
					node.getTranslationUnit().accept(visitor);
					
					String LHSVarName = "";
					String RHSVarName = "";
					String LHSArrayRef = "null";
					String RHSArrayRef = "null";
					for(VariableNameTypePair vntP: visitor.getvarNamePairList())
					{
						ASTVisitorFindMatch visitLHS = new ASTVisitorFindMatch(vntP.getVarName(), "FindMatch");
						ASTVisitorFindMatch visitRHS = new ASTVisitorFindMatch(vntP.getVarName(), "FindMatch");
						LHS.accept(visitLHS);
						RHS.accept(visitRHS);
						
						if(visitLHS.isMatch())
						{
							LHSVarName = vntP.getVarName();
							
							if(vntP.isArray())
							{
								if(!LHS.getRawSignature().contains("sizeof") && !(LHS instanceof IASTFunctionCallExpression))
								{
								LHSArrayRef = LHSVarName;
								}
							}
							else if(vntP.isPointer())
							{
								LHSArrayRef = arrayReference(LHSVarName, node);
							}
							else
							{
								
								if(LHS.getRawSignature().contains("&"))
								{
									LHSArrayRef = "refOther";
								}
								else
								{
									LHSArrayRef = "null";
								}
							}
						}
						
						if(visitRHS.isMatch())
						{
							RHSVarName = vntP.getVarName();
							
							if(vntP.isArray())
							{
								if(!RHS.getRawSignature().contains("sizeof") && !(RHS instanceof IASTFunctionCallExpression))
								{
									RHSArrayRef = RHSVarName;
								}
							}
							else if(vntP.isPointer())
							{
								RHSArrayRef = arrayReference(RHSVarName, node);
							}
							else
							{
								if(RHS.getRawSignature().contains("&"))
								{
									RHSArrayRef = "refOther";
								}
								else
								{
									RHSArrayRef = "null";
								}
							}
						}
					}
					
					
					if(LHSArrayRef.contains("null") && !RHSArrayRef.contains("null"))
					{
						ruleViolated = false;
					}
					else if(!LHSArrayRef.contains("null") && RHSArrayRef.contains("null"))
					{
						ruleViolated = false;;
					}
					else if (!LHSArrayRef.contentEquals(RHSArrayRef))
					{
						ruleViolated = true;
					}
				}
			}
		}
		
		return ruleViolated;
	}

	/**
	 * Returns the array a pointer is referencing
	 * @param pointerVarName
	 * @param pointerNode
	 * @return
	 */
	private String arrayReference(String pointerVarName, IASTNode pointerNode)
	{
		String arrayRef = "null";
		NodeNumPair_C currNode = null;
		ASTNodeProcessor_C visitorRef = new ASTNodeProcessor_C();
		pointerNode.getTranslationUnit().accept(visitorRef);
		
		for(NodeNumPair_C binExp: visitorRef.getBinaryExpressions())
		{
			if(pointerNode == binExp.getNode())
			{
				currNode = binExp;
				break;
			}
		}
		
		
		
		
		for(NodeNumPair_C dec: visitorRef.getVariableDeclarations())
		{
			if(dec.getNum() < currNode.getNum())
			{
				if(!dec.getNode().getRawSignature().contains("struct") && 
						!dec.getNode().getRawSignature().contains("union"))
				{
				
				String currVarName = ((IASTSimpleDeclaration)dec.getNode()).getDeclarators()[0].getName().getRawSignature();
				
				if(currVarName.contentEquals(pointerVarName))
				{
					for(VariableNameTypePair vntP: visitorRef.getvarNamePairList())
					{
						if(vntP.isArray())
						{
							ASTVisitorFindMatch visitNode = new ASTVisitorFindMatch(vntP.getVarName(), "FindMatch");
							dec.getNode().accept(visitNode);
							
							if(visitNode.isMatch())
							{
								arrayRef = vntP.getVarName();
							}
						}
					}
				}
				}
				
			}
		}
		
		for(NodeNumPair_C assign: visitorRef.getAssignmentStatements())
		{
			if(assign.getNum() < currNode.getNum())
			{
				String currVarName = ((IASTBinaryExpression)assign.getNode()).getOperand1().getRawSignature();
				
				if(currVarName.contentEquals(pointerVarName))
				{
					for(VariableNameTypePair vntP: visitorRef.getvarNamePairList())
					{
						if(vntP.isArray())
						{
							ASTVisitorFindMatch visitNode = new ASTVisitorFindMatch(vntP.getVarName(), "FindMatch");
							assign.getNode().accept(visitNode);
							
							if(visitNode.isMatch())
							{
								arrayRef = vntP.getVarName();
							}
						}
					}
				}
				
				
			}
		}
		
		return arrayRef;
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
