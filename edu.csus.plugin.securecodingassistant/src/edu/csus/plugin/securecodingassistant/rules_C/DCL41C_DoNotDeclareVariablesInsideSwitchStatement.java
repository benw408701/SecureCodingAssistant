package edu.csus.plugin.securecodingassistant.rules_C;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSwitchStatement;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.csus.plugin.securecodingassistant.Globals;

public class DCL41C_DoNotDeclareVariablesInsideSwitchStatement implements IRule_C {

	private boolean ruleViolated;
	
	@Override
	public boolean violate_CDT(IASTNode node) {
		
		ruleViolated = false;
		
		if(node instanceof IASTSwitchStatement)
		{
			ASTNodeProcessor_C visitor = new ASTNodeProcessor_C();
			node.accept(visitor);
			
			if(visitor.getVariableDeclarations().size() != 0)
			{
				NodeNumPair_C caseZeroNodeNum = visitor.getCaseStatements().get(0);
				
				for(NodeNumPair_C o : visitor.getVariableDeclarations())
				{
					if(caseZeroNodeNum.getNum() > o.getNum())
					{
						ruleViolated = true;
					}
				}
			}	
		}
		
		
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		
		return "CERT Website- If a programmer declares variables, "
				+ "initializes them before the first case statement, "
				+ "and then tries to use them inside any of the "
				+ "case statements, those variables will have scope "
				+ "inside the switch block but will not be initialized "
				+ "and will consequently contain indeterminate values.";
	}

	@Override
	public String getRuleName() {
		
		return Globals.RuleNames.DCL41_C;
	}

	@Override
	public String getRuleID() {
		
		return Globals.RuleID.DCL41_C;
	}

	@Override
	public String getRuleRecommendation() {
		
		return "Do not declare variables within a switch statement.";
	}

	@Override
	public int securityLevel() {
		
		return Globals.Markers.SECURITY_LEVEL_MEDIUM;
	}

	@Override
	public String getRuleURL() {
		return "https://wiki.sei.cmu.edu/confluence/display/c/DCL41-C.+Do+not+declare+variables+inside+a+switch+statement+before+the+first+case+label";
	}

	@Override
	public ITranslationUnit getITranslationUnit() {
		// TODO Auto-generated method stub
		return null;
	}

}
