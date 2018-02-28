package edu.csus.plugin.securecodingassistant.rules_C;

import java.util.TreeMap;

import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.csus.plugin.securecodingassistant.Globals;

public class ERR34C_DetectErrorsWhenConvertingStringToNumber extends SecureCodingRule_C {

	boolean ruleViolated;
	String[] strArray = new String[] {"atoi(","atol(", "atoll(", "atof(", "sscanf("};
	//String tempString;
	
	@Override
	public boolean violate_CDT(IASTNode node) {
		ruleViolated = false;
		if(node instanceof IASTFunctionCallExpression)
		{
			
			for(String tempString: strArray)
			{
				//System.out.println(tempString);
				if(node.getRawSignature().contains(tempString))
				{
					//System.out.println("\nRULE VIOLATED: ERR34C_DetectErrorsWhenConvertingStringToNumber");
					ruleViolated = true;
				}
			}
		}
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		return "CERT Website- The process of parsing an integer or floating-point "
				+ "number from a string can produce many errors. The string might "
				+ "not contain a number. \nIt might contain a number of the correct "
				+ "type that is out of range (such as an integer that is larger "
				+ "than INT_MAX). The string may also contain extra information "
				+ "after the number, which may or may not be useful after the "
				+ "conversion. \nThese error conditions must be detected and "
				+ "addressed when a string-to-number conversion is performed using "
				+ "a C Standard Library function.";
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
		//TODO
		return "Use strol(), stroll(), strtoimax(), strtoul(), strtoull(), strtoumax(), "
				+ "strtof(), strtod(), and strtold() functions instead.";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_LOW;
	}

	//@Override
	public TreeMap<String, ASTRewrite> getSolutions_CDT(IASTNode node) {
		// TODO Auto-generated method stub
		return null;
	}

	//@Override
	public ITranslationUnit getITranslationUnit_CDT() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRuleURL() {
		
		return "https://wiki.sei.cmu.edu/confluence/display/c/ERR34-C.+Detect+errors+when+converting+a+string+to+a+number";
	}


}
