package edu.csus.plugin.securecodingassistant.rules_C;

import org.eclipse.cdt.core.dom.ast.IASTArrayDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * <b><i>The text and/or code below is from the CERT website:
 * <a target="_blank"href="https://wiki.sei.cmu.edu/confluence/display/seccode">
 * https://wiki.sei.cmu.edu/confluence/display/seccode </a></i></b>
 * <p>
 * C Secure Coding Rule: DCL38-C. Use the correct syntax when declaring a flexible
 * array member.
 * </p>
 * <p>
 * Flexible array members are a special type of array in which the last element 
 * of a structure with more than one named member has an incomplete array type;
 *  that is, the size of the array is not specified explicitly within the 
 *  structure. This "struct hack" was widely used in practice and supported by
 *   a variety of compilers. Consequently, a variety of different syntaxes have
 *    been used for declaring flexible array members. For conforming C 
 *    implementations, use the syntax guaranteed to be valid by the C Standard.
 * </p>
 * 
 * <p>
 * Structures with a flexible array member can be used to produce code with 
 * defined behavior. However, some restrictions apply:
 *	1.The incomplete array type must be the last element within the structure.
 *	2.There cannot be an array of structures that contain a flexible array member.
 *	3.Structures that contain a flexible array member cannot be used as a member of
 *	 another structure.
 *	4.The structure must contain at least one named member in addition to the 
 *	flexible array member.
 * </p>
 * 
 * @author Victor Melnik (Plugin Logic), CERT (Rule Definition)
 * @see C Secure Coding Rule define by CERT: <a target="_blank" 
 * href="https://wiki.sei.cmu.edu/confluence/display/c/DCL38-C.+Use+the+correct
 * +syntax+when+declaring+a+flexible+array+member">DCL38-C</a>
 *
 */
public class DCL38C_UseCorrectSyntaxWhenDeclaringFlexibleArrayMember extends SecureCodingRule_C {

	private boolean ruleViolated;
	private String structDec = "struct";
	private IASTNode[] children;

	@Override
	public boolean violate_CDT(IASTNode node) {
		ruleViolated = false;
			
		if(node instanceof IASTDeclaration && node.getFileLocation().getContextInclusionStatement() == null)
		{
			if(node.getRawSignature().startsWith(structDec) && !node.getRawSignature().startsWith("typedef") && (node.getFileLocation().getContextInclusionStatement() == null))
			{		
					
				children = node.getChildren(); //get child to get node of type IASTCompositeTypeSpecifier from IASTDeclaration
					
				if(children.length >= 1)
				{
					if(children[0] instanceof IASTCompositeTypeSpecifier)
					{
						ASTNodeProcessor_C visitor = new ASTNodeProcessor_C();
							
						node.accept(visitor);
							
						if(visitor.getCounter() > 2)
						{
							//check if array is last element of struct
							if(visitor.getVariableDeclarations().get(visitor.getCounter() - 1).getNode().getChildren()[1] instanceof IASTArrayDeclarator) 
							{
								//check to see that array is 1 dimensional
								if(visitor.getVariableDeclarations().get(visitor.getCounter() - 1).getNode().getChildren()[1].getChildren().length < 3)
								{
									IASTNode arrayBracket =  visitor.getVariableDeclarations().get(visitor.getCounter() - 1).getNode().getChildren()[1].getChildren()[1];
									if(arrayBracket.getChildren().length != 0 && arrayBracket.getChildren()[0].getRawSignature().contentEquals("1"))
									{
											ruleViolated = true;
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

		return "CERT Website- Flexible array members are a special type of array in which "
				+ "the last element of a structure with more than one named member has an "
				+ "incomplete array type. For conforming C implementations, use the syntax "
				+ "guaranteed to be valid by the C Standard.";
	}

	@Override
	public String getRuleName() {
		
		return Globals.RuleNames.DCL38_C;
	}

	@Override
	public String getRuleID() {
		
		return Globals.RuleID.DCL38_C;
	}

	@Override
	public String getRuleRecommendation() {
		
		return "Use correct syntax for declaring flexible array. "
				+ "  Ex. \"int data [1]\" to \"int data[]\"";
	}

	@Override
	public int securityLevel() {
		
		return Globals.Markers.SECURITY_LEVEL_LOW;
	}

	@Override
	public String getRuleURL() {
		
		return "https://wiki.sei.cmu.edu/confluence/display/c/DCL38-C.+Use+the+correct+syntax+when+declaring+a+flexible+array+member";
	}

	@Override
	public ITranslationUnit getITranslationUnit() {
		// TODO Auto-generated method stub
		return null;
	}

}
