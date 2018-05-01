package edu.csus.plugin.securecodingassistant.rules_C;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * <b><i>The text and/or code below is from the CERT website:
 * <a target="_blank"href="https://wiki.sei.cmu.edu/confluence/display/seccode">
 * https://wiki.sei.cmu.edu/confluence/display/seccode </a></i></b>
 * <p>
 * C Secure Coding Rule: EXP32-C. Do not access a volatile object through a 
 * nonvolatile reference
 * </p>
 * <p>
 * An object that has volatile-qualified type may be modified in ways unknown
 *  to the implementation or have other unknown side effects. Referencing a
 *   volatile object by using a non-volatile lvalue is undefined behavior. 
 *   The C Standard, 6.7.3 [ISO/IEC 9899:2011], states: 
 *   	If an attempt is made to refer to an object defined with a 
 *   	volatile-qualified type through use of an lvalue with 
 *   	non-volatile-qualified type, the behavior is undefined.
 *   
 * </p>
 * 
 * @author Victor Melnik (Plugin Logic), CERT (Rule Definition)
 * @see C Secure Coding Rule define by CERT: <a target="_blank" 
 * href="https://wiki.sei.cmu.edu/confluence/display/c/EXP32-C.+Do+not+access
 * +a+volatile+object+through+a+nonvolatile+reference">EXP32-C</a>
 *
 */

public class EXP32C_DoNotAccessVolatileObjectThroughNonvolatileRefernece extends SecureCodingRule_C {

	private boolean ruleViolated;
	private IASTNode m_LHSNode;
	private IASTNode m_RHSNode;
	
	@Override
	public boolean violate_CDT(IASTNode node) {
		
		ruleViolated = false;
		
		if((node.getFileLocation().getContextInclusionStatement() == null))
		{
			if(node instanceof IASTBinaryExpression)
			{
				IASTNode parent = node.getParent();
				ASTNodeProcessor_C visitAssign = new ASTNodeProcessor_C();
				parent.accept(visitAssign);
				
				for(NodeNumPair_C o : visitAssign.getAssignmentStatements())
				{
					if(node == o.getNode())
					{
						m_LHSNode =  ((IASTBinaryExpression)node).getOperand1();
						m_RHSNode = ((IASTBinaryExpression)node).getOperand2();
						
						ruleViolated = isViolated(m_LHSNode, m_RHSNode, node, false);
						break;
					}
				}
			}
			else if(node instanceof IASTDeclaration && !(node instanceof IASTFunctionDeclarator) && node.getRawSignature().endsWith(";") && (node instanceof IASTSimpleDeclaration  ))
			{
				if(node.getRawSignature().contains("&") && node.getRawSignature().contains("="))
				{
					IASTDeclSpecifier decSpecifier = ((IASTSimpleDeclaration)node).getDeclSpecifier();
					String decSpec = decSpecifier.getRawSignature();
					
					IASTDeclarator o = ((IASTSimpleDeclaration)node).getDeclarators()[0];
				
						if(!(o instanceof IASTFunctionDeclarator))
						{
							if(!(decSpec.startsWith("struct")) && !(decSpec.startsWith("union")) && !(decSpec.startsWith("typedef")))
							{
								int lastIndex = o.getChildren().length;
								
								m_LHSNode = o.getChildren()[lastIndex - 2];
								m_RHSNode = o.getChildren()[lastIndex - 1].getChildren()[0];
					
								ruleViolated = isViolated(m_LHSNode, m_RHSNode, node, true);
							}
						}
				}
			}
			
		}
		
		return ruleViolated;
	}

	private boolean isViolated (IASTNode LHSNode, IASTNode RHSNode, IASTNode nodeOriginal, boolean isDec )
	{
		boolean isViolated = false;
		
		if(RHSNode.getRawSignature().contains("&") || isDec)
		{
			ASTNodeProcessor_C visitor = new ASTNodeProcessor_C();
			nodeOriginal.getTranslationUnit().accept(visitor);
			
			for(VariableNameTypePair LHS : visitor.getvarNamePairList())
			{
				ASTVisitorFindMatch visitor1 = new ASTVisitorFindMatch(LHS.getVarName(), "FindMatch");
				LHSNode.accept(visitor1);
				
				if(visitor1.isMatch())
				{
					boolean isVolatile = LHS.isVolatile();
					if(isVolatile)
					{
						for(VariableNameTypePair RHS: visitor.getvarNamePairList())
						{
							ASTVisitorFindMatch visitor2 = new ASTVisitorFindMatch(RHS.getVarName(), "FindMatch");
							RHSNode.accept(visitor2);
							
							if(visitor2.isMatch())
							{
								if(!RHS.isVolatile())
								{
									isViolated = true;
								}
								break;
							}
						}
					}
					break;
				}
			}
				
		}
		return isViolated;
	}
	
	@Override
	public String getRuleText() {

		return "An object that has volatile-qualified type may be "
				+ "modified in ways unknown to the implementation "
				+ "or have other unknown side effects. Referencing "
				+ "a volatile object by using a non-volatile lvalue"
				+ " is undefined behavior.";
	}
	
	@Override
	public String getRuleName() {
		
		return Globals.RuleNames.EXP32_C;
	}

	@Override
	public String getRuleID() {
		
		return Globals.RuleID.EXP32_C;
	}

	@Override
	public String getRuleRecommendation() {
		
		return "Use a volatile identifier to reference volatile objects";
	}

	@Override
	public int securityLevel() {
		
		return Globals.Markers.SECURITY_LEVEL_LOW;
	}

	@Override
	public String getRuleURL() {
		
		return "https://wiki.sei.cmu.edu/confluence/display/c/EXP32-C.+Do+not+access+a+volatile+object+through+a+nonvolatile+reference";
	}

	@Override
	public ITranslationUnit getITranslationUnit() {
		// TODO Auto-generated method stub
		return null;
	}

}
