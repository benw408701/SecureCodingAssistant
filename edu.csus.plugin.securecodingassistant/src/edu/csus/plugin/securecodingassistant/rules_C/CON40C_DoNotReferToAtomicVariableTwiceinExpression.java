package edu.csus.plugin.securecodingassistant.rules_C;

import java.util.ArrayList;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;


import edu.csus.plugin.securecodingassistant.Globals;

/**
 * <b><i>The text and/or code below is from the CERT website:
 * <a target="_blank"href="https://wiki.sei.cmu.edu/confluence/display/seccode">
 * https://wiki.sei.cmu.edu/confluence/display/seccode </a></i></b>
 * <p>
 * C Secure Coding Rule: CON40-C. Do not refer to an atomic variable twice in 
 * an expression
 * </p>
 * <p>
 * A consistent locking policy guarantees that multiple threads cannot 
 * simultaneously access or modify shared data. Atomic variables eliminate the
 *  need for locks by guaranteeing thread safety when certain operations are 
 *  performed on them. The thread-safe operations on atomic variables are 
 *  specified in the C Standard, subclauses 7.17.7 and 7.17.8 
 *  [ISO/IEC 9899:2011]. While atomic operations can be combined, combined
 *   operations do not provide the thread safety provided by individual 
 *   atomic operations.
 * </p>
 * 
 * <p>
 * Every time an atomic variable appears on the left side of an assignment 
 * operator, including a compound assignment operator such as *=, an atomic 
 * write is performed on the variable. The use of the increment (++) or 
 * decrement (--) operators on an atomic variable constitutes an atomic 
 * read-and-write operation and is consequently thread-safe. Any reference of
 *  an atomic variable anywhere else in an expression indicates a distinct 
 *  atomic read on the variable.
 * 
 * </p>
 * 
 * <p>
 * If the same atomic variable appears twice in an expression, then two atomic
 *  reads, or an atomic read and an atomic write, are required. Such a pair of
 *   atomic operations is not thread-safe, as another thread can modify the 
 *   atomic variable between the two operations. Consequently, an atomic 
 *   variable must not be referenced twice in the same expression.
 * </p>
 * 
 * @author Victor Melnik (Plugin Logic), CERT (Rule Definition)
 * @see C Secure Coding Rule define by CERT: <a target="_blank" 
 * href="https://wiki.sei.cmu.edu/confluence/display/c/CON40-C.+Do+not+refer+to
 * +an+atomic+variable+twice+in+an+expression">CON40-C</a>
 *
 */

public class CON40C_DoNotReferToAtomicVariableTwiceinExpression extends SecureCodingRule_C {

	private boolean ruleViolated;
	private boolean isMatch = false;
	private ArrayList<VariableNameTypePair> listofVarNameInScope = new ArrayList<VariableNameTypePair>();
	
	@Override
	public boolean violate_CDT(IASTNode node) {
		
		ruleViolated = false;
		isMatch = false;
		listofVarNameInScope.clear();
		
		//check if TranslationUnit contains include file <stdatomic.h>
		if((node.getFileLocation().getContextInclusionStatement() == null) &&
				node.getTranslationUnit().getRawSignature().contains("stdatomic.h"))
		{	
			IASTNode parent = node.getParent();
			boolean isBinary = false;
					
			if(node instanceof IASTBinaryExpression || node instanceof IASTUnaryExpression)
			{
				if(node instanceof IASTBinaryExpression)
				{
					isBinary = true;
				}
				else
				{
					isBinary = false;
				}
				
				
				//get varNameTypePair Arraylist for entire TranslationUnit
				ASTNodeProcessor_C visitNameType = new ASTNodeProcessor_C();
				node.getTranslationUnit().accept(visitNameType);
				
				ASTNodeProcessor_C visitParent = new ASTNodeProcessor_C();
				parent.accept(visitParent);
				
				for(NodeNumPair_C o: visitParent.getComputationStatements())
				{
					if(node == o.getNode())
					{
						isMatch = true;
					}
				}
				
				for(NodeNumPair_C o: visitParent.getAssignmentStatements())
				{
					if(node == o.getNode())
					{
						isMatch = true;
					}
				}
				
				
				if(isMatch)
				{
					if(!isBinary)
					{
						listofVarNameInScope = Utility_C.allVarNameType(visitNameType.getvarNamePairList(), node);
						
						for(VariableNameTypePair ooo: listofVarNameInScope)
						{
							if(ooo.isAtomic() || assignedAtomic(ooo, visitNameType.getAssignmentStatements())||
									assignedAtomic(ooo, visitNameType.getVariableDeclarations()))
							{
								ruleViolated = true;
								return ruleViolated;
							}
						}
						
					}
					else
					{
						listofVarNameInScope = Utility_C.allVarNameType(visitNameType.getvarNamePairList(),node);
						
						for(VariableNameTypePair ooo: listofVarNameInScope)
						{
							if(ooo.isAtomic() || assignedAtomic(ooo, visitNameType.getAssignmentStatements())||
									assignedAtomic(ooo, visitNameType.getVariableDeclarations()))
							{
								ASTVisitorFindMatch visitorFindDup = new ASTVisitorFindMatch(ooo.getVarName(), "findDup");
								
								node.accept(visitorFindDup);
								if(visitorFindDup.isDuplicateInExpression())
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
		return ruleViolated;
	}
	
	/**
	 * Check to see if variable was assigned to atomic value previously
	 * @param VariableNameTypePair varName
	 * @param ArrayList<NodeNumPair_C>list
	 * @return boolean
	 */
	private boolean assignedAtomic(VariableNameTypePair pair, ArrayList<NodeNumPair_C> list)
	{
		for(NodeNumPair_C listElem : list)
		{
			if(Utility_C.getScope(listElem.getNode()) == Utility_C.getScope(pair.getNode()))
			{
				if(listElem.getNode() instanceof IASTDeclaration)
				{
					if(listElem.getNode().getRawSignature().contains("atomic_"))
					{
						return true;
					}
				}
				else if(listElem.getNode() instanceof IASTBinaryExpression)
				{
					if(((IASTBinaryExpression)listElem.getNode()).getOperand2().getRawSignature().contains("atomic_"))
					{
						ASTVisitorFindMatch visitorFind = new ASTVisitorFindMatch(pair.getVarName(), "FindMatch");
						((IASTBinaryExpression)listElem.getNode()).getOperand1().accept(visitorFind);
						if(visitorFind.isMatch())
						{
							return true;
						}
					}
				}
			}
		}
		
		return false;
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
