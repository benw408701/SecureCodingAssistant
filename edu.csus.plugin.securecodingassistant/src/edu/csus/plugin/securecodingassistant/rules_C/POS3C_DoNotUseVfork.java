package edu.csus.plugin.securecodingassistant.rules_C;

import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.csus.plugin.securecodingassistant.Globals;

public class POS3C_DoNotUseVfork implements IRule_C {
	
	private String vfork_str = "vfork(";
	private boolean ruleViolated;
	
	@Override
	public boolean violate_CDT(IASTNode node) {
		
		ruleViolated = false;
		if(node instanceof IASTFunctionCallExpression)
		{
			
			if(node.getRawSignature().startsWith(vfork_str))
			{
				//System.out.println("\nRULE VIOLATED: POS3C_DoNotUseVfork");
				ruleViolated = true;	
				
			}
		}
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		
		return "Do not use vfork() function. vfork() introduces many portobility "
				+ "and security issues.";
	}

	@Override
	public String getRuleName() {
		return Globals.RuleNames.POS33_C;
	}

	@Override
	public String getRuleID() {
		
		return Globals.RuleID.POS33_C;
	}

	@Override
	public String getRuleRecommendation() {
		
		return "Repalce the call to vfork() with a call to fork()";
	}

	@Override
	public int securityLevel() {
		
		return Globals.Markers.SECURITY_LEVEL_LOW;
	}

	@Override
	public ITranslationUnit getITranslationUnit() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRuleURL() {
		
		return "https://wiki.sei.cmu.edu/confluence/exportword?pageId=87152373";
	}

}
