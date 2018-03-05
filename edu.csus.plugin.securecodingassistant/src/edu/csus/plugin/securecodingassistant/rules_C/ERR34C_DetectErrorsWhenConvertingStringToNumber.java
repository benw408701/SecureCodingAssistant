package edu.csus.plugin.securecodingassistant.rules_C;

import java.util.ArrayList;
import java.util.Arrays;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;

import edu.csus.plugin.securecodingassistant.Globals;

public class ERR34C_DetectErrorsWhenConvertingStringToNumber extends SecureCodingRule_C {

	boolean ruleViolated;	
	private ArrayList<String> unSafeFunctions = new ArrayList<String>(
			Arrays.asList("atoi","atol", "atoll", "atof", "sscanf", "scanf", "fscanf")
			);
	
	@Override
	public boolean violate_CDT(IASTNode node) {
		ruleViolated = false;
		if(node instanceof IASTFunctionCallExpression)
		{
			
			String funcName = ((IASTFunctionCallExpression)node).getFunctionNameExpression().getRawSignature();
			
			if(unSafeFunctions.contains(funcName))
			{
				ruleViolated = true;
			}
		}
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		return "CERT Website- The atoi(), atol(), atoll(), and atof() functions convert the "
				+ "initial portion of a string token to int, long int, long long int, and "
				+ "double representation, respectively. Unfortunately, atoi(), related "
				+ "functions, and scanf family of functions lack a mechanism for reporting"
				+ " errors for invalid values.";
	}

	@Override
	public String getRuleName() {
		return Globals.RuleNames.ERR34_C;
	}

	@Override
	public String getRuleID() {
		return Globals.RuleID.ERR34_C;
	}

	@Override
	public String getRuleRecommendation() {
		return "Use one of the C Standard Library strto*() functions instead.";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_LOW;
	}

	@Override
	public String getRuleURL() {
		
		return "https://wiki.sei.cmu.edu/confluence/display/c/ERR34-C.+Detect+errors+when+converting+a+string+to+a+number";
	}


}
