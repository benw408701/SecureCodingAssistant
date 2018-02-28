package edu.csus.plugin.securecodingassistant.rules_C;

import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.csus.plugin.securecodingassistant.Globals;

public class ENV33C_DoNotCallSystem extends SecureCodingRule_C {

	
	private String system_str = "system(";
	private boolean ruleViolated;
	
	@Override
	public boolean violate_CDT(IASTNode node) {
		ruleViolated = false;
		if(node instanceof IASTFunctionCallExpression)
		{
			if(node.getRawSignature().contains(system_str))
			{
				ruleViolated = true;	
				
			}
		}
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		return "CERT Website- Use of the system() function can result in exploitable "
				+ "vulnerabilities, in the worst case allowing execution "
				+ "of arbitrary system commands. Do not invoke a command processor via system() or "
				+ "equivalent functions to execute a command. ";
	}

	@Override
	public String getRuleName() {
		return Globals.RuleNames.ENV33_C;
	}

	@Override
	public String getRuleID() {
		return Globals.RuleID.ENV33_C;
	}

	@Override
	public String getRuleRecommendation() {
		//TODO
		return "For POSIX use the exec family or functions or implement functionality in the"
				+ "program using the exisiting library call. \nIn Microsoft Windows use the"
				+ "CreateProcess() API or SHGetKnownFolderPath() API with DeleteFile() API";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_HIGH;
	}


	//@Override
	public ITranslationUnit getITranslationUnit_CDT() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRuleURL() {
		
		return "https://wiki.sei.cmu.edu/confluence/pages/viewpage.action?pageId=87152177";
	}

	

}
