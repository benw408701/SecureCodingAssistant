package edu.csus.plugin.securecodingassistant.rules_C;

import org.eclipse.cdt.core.dom.ast.IASTDoStatement;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTWhileStatement;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * <b><i>The text and/or code below is from the CERT website:
 * <a target="_blank"href="https://wiki.sei.cmu.edu/confluence/display/seccode">
 * https://wiki.sei.cmu.edu/confluence/display/seccode </a></i></b>
 * <p>
 * C Secure Coding Rule: FLP30-C. Do not use floating-point variables as loop
 * counter
 * </p>
 * <p>
 * Because floating-point numbers represent real numbers, it is often 
 * mistakenly assumed that they can represent any simple fraction exactly.
 * Floating-point numbers are subject to representational limitations just 
 * as integers are, and binary floating-point numbers cannot represent all 
 * real numbers exactly, even if they can be represented in a small number of 
 * decimal digits.
 * </p>
 * 
 * <p>
 * In addition, because floating-point numbers can represent large values, it 
 * is often mistakenly assumed that they can represent all significant digits 
 * of those values. To gain a large dynamic range, floating-point numbers 
 * maintain a fixed number of precision bits (also called the significand) and 
 * an exponent, which limit the number of significant digits they can represent.
 * </p>
 * 
 * <p>
 * Different implementations have different precision limitations, and to keep 
 * code portable, floating-point variables must not be used as the loop 
 * induction variable. See Goldberg's work for an introduction to this 
 * topic [Goldberg 1991].
 * </p>
 * 
 * <p>
 * For the purpose of this rule, a loop counter is an induction variable that 
 * is used as an operand of a comparison expression that is used as the 
 * controlling expression of a do, while, or for loop. An induction variable 
 * is a variable that gets increased or decreased by a fixed amount on 
 * every iteration of a loop [Aho 1986]. Furthermore, the change to the 
 * variable must occur directly in the loop body (rather than inside a 
 * function executed within the loop).
 * </p>
 * 
 * @author Victor Melnik (Plugin Logic), CERT (Rule Definition)
 * @see C Secure Coding Rule define by CERT: <a target="_blank" 
 * href="https://wiki.sei.cmu.edu/confluence/display/c/FLP30-C.+Do+not+use+
 * floating-point+variables+as+loop+counters">FLP30-C</a>
 *
 */

public class FLP30C_DoNotUseFloatingPointVariablesAsLoopCounter extends SecureCodingRule_C {

	private boolean ruleViolated = false;
	
	private IASTNode nodeToVisit;
	
	@Override
	public boolean violate_CDT(IASTNode node) {
		ruleViolated = false;
		nodeToVisit = null;

		if(node.getFileLocation().getContextInclusionStatement() == null)
		{
			
			if(node instanceof IASTForStatement)
			{
				nodeToVisit = ((IASTForStatement)node).getInitializerStatement();
			}
			else if(node instanceof IASTDoStatement)
			{
				nodeToVisit = ((IASTDoStatement)node).getCondition();
			}
			else if(node instanceof IASTWhileStatement)
			{
				nodeToVisit = ((IASTWhileStatement)node).getCondition();
			}
			
			if(node instanceof IASTWhileStatement || 
					node instanceof IASTDoStatement || 
					node instanceof IASTForStatement)
			{
				ASTNodeProcessor_C visitor_DO = new ASTNodeProcessor_C();
				node.getTranslationUnit().accept(visitor_DO);
				
				for(VariableNameTypePair o: visitor_DO.getvarNamePairList())
				{
					if(o.getVarType().contains("float") || o.getVarType().contains("double"))
					{
						ASTVisitorFindMatch visitorFind_DO = new ASTVisitorFindMatch(o.getVarName(), "FindMatch");
						nodeToVisit.accept(visitorFind_DO);
						
						if(visitorFind_DO.isMatch())
						{
							ruleViolated = true;
						}
					}
				}
			}
			
			
		}
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		
		return "Cert Website- Because floating-point numbers represent real numbers, it "
				+ "is often mistakenly assumed that they can represent any simple fraction exactly."
				+ "Floating-point counters can lead to infinite loops or can increment 1 less or more"
				+ "than predicted.";
	}

	@Override
	public String getRuleName() {
		
		return Globals.RuleNames.FLP30_C;
	}

	@Override
	public String getRuleID() {

		return Globals.RuleID.FLP30_C;
	}

	@Override
	public String getRuleRecommendation() {
		
		return "Use an integer as a loop counter";
	}

	@Override
	public int securityLevel() {
		
		return Globals.Markers.SECURITY_LEVEL_LOW;
	}

	@Override
	public String getRuleURL() {
		
		return "https://wiki.sei.cmu.edu/confluence/display/c/FLP30-C.+Do+not+use+floating-point+variables+as+loop+counters";
	}

	@Override
	public ITranslationUnit getITranslationUnit() {
		// TODO Auto-generated method stub
		return null;
	}

}
