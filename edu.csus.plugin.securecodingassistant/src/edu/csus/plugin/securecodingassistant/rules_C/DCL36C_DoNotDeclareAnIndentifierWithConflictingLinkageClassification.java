package edu.csus.plugin.securecodingassistant.rules_C;


import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * <b><i>The text and/or code below is from the CERT website:
 * <a target="_blank"href="https://wiki.sei.cmu.edu/confluence/display/seccode">
 * https://wiki.sei.cmu.edu/confluence/display/seccode </a></i></b>
 * <p>
 * C Secure Coding Rule: DCL36-C. Do not declare an identifier with conflicting
 * linkage classifications
 * </p>
 * <p>
 * Linkage can make an identifier declared in different scopes or declared
 *  multiple times within the same scope refer to the same object or function.
 *   Identifiers are classified as externally linked, internally linked, or 
 *   not linked. These three kinds of linkage have the following 
 *   characteristics [Kirch-Prinz 2002]:
 * </p>
 * 
 * <p>
 * External linkage: An identifier with external linkage represents the same
 *  object or function throughout the entire program, that is, in all 
 *  compilation units and libraries belonging to the program. The identifier 
 *  is available to the linker. When a second declaration of the same 
 *  identifier with external linkage occurs, the linker associates the 
 *  identifier with the same object or function.
 *  
 *  Internal linkage: An identifier with internal linkage represents the 
 *  same object or function within a given translation unit. The linker 
 *  has no information about identifiers with internal linkage. Consequently
 *  , these identifiers are internal to the translation unit.
 *  
 *  No linkage: If an identifier has no linkage, then any further declaration
 *   using the identifier declares something new, such as a new variable or 
 *   a new type.
 * </p>
 * 
 * <p>
 * Use of an identifier (within one translation unit) classified as both 
 * internally and externally linked is undefined behavior. A 
 * translation unit includes the source file together with its headers
 *  and all source files included via the preprocessing directive #include.
 * </p>
 * 
 * @author Victor Melnik (Plugin Logic), CERT (Rule Definition)
 * @see C Secure Coding Rule define by CERT: <a target="_blank" 
 * href="https://wiki.sei.cmu.edu/confluence/display/c/DCL36-C.+Do+not+declare
 * +an+identifier+with+conflicting+linkage+classifications">DCL36-C</a>
 *
 */

public class DCL36C_DoNotDeclareAnIndentifierWithConflictingLinkageClassification extends SecureCodingRule_C {

	private boolean ruleViolated;
	
	@Override
	public boolean violate_CDT(IASTNode node) {
		
		ruleViolated= false;
		
		if((node.getFileLocation().getContextInclusionStatement() == null))
		{
				
			if((node instanceof IASTDeclaration) && !(node instanceof IASTFunctionDefinition) && !(node instanceof IASTFunctionDefinition)
					&& (node.getChildren().length > 1))
			{
				ASTNodeProcessor_C visitor = new ASTNodeProcessor_C();
				node.getTranslationUnit().accept(visitor);
				
				
				for(VariableNameTypePair o: visitor.getvarNamePairList())
				{
					if(o.getNode() == node)
					{
						 String VarName = o.getVarName();
						 boolean isStatic = o.isStatic();
						 
						 for(VariableNameTypePair oo: visitor.getvarNamePairList())
						 {
							 if(oo.getNode() == node)
							 {
								 break;
							 }
							 else if((oo.getVarName().contentEquals(VarName)))
							 {
								 if(isStatic != oo.isStatic())
								 {
									 ruleViolated = true;
								 }
							 }
						 }
						 break;
					}
				}
			}
		}
		
		return ruleViolated;
	}
	
	@Override
	public String getRuleText() {
		
		return "Use of an identifier (within one translation unit) "
				+ "classified as both internally and externally linked"
				+ " is undefined behavior. Linkage can make an "
				+ "identifier declared in different scopes or declared"
				+ " multiple times within the same scope refer to the"
				+ " same object or function.";
	}

	@Override
	public String getRuleName() {
		
		return Globals.RuleNames.DCL36_C;
	}

	@Override
	public String getRuleID() {
		
		return Globals.RuleID.DCL36_C;
	}

	@Override
	public String getRuleRecommendation() {
		
		return "Do not declare identifiers with both external and"
				+ "internal linkage";
	}

	@Override
	public int securityLevel() {
		
		return Globals.Markers.SECURITY_LEVEL_MEDIUM;
	}

	@Override
	public String getRuleURL() {
		
		return "https://wiki.sei.cmu.edu/confluence/display/c/DCL36-C.+Do+not+declare+an+identifier+with+conflicting+linkage+classifications";
	}

	@Override
	public ITranslationUnit getITranslationUnit() {
		// TODO Auto-generated method stub
		return null;
	}

}
