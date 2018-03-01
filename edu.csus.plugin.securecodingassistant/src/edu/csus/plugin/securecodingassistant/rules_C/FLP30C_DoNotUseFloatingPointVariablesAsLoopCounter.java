package edu.csus.plugin.securecodingassistant.rules_C;

import org.eclipse.cdt.core.dom.ast.IASTDoStatement;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTWhileStatement;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.csus.plugin.securecodingassistant.Globals;

public class FLP30C_DoNotUseFloatingPointVariablesAsLoopCounter implements IRule_C {

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
			
			if(node instanceof IASTWhileStatement || node instanceof IASTDoStatement || node instanceof IASTForStatement)
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
