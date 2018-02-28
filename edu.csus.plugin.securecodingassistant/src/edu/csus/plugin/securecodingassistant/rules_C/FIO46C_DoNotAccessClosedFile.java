package edu.csus.plugin.securecodingassistant.rules_C;

import java.util.TreeMap;

import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.csus.plugin.securecodingassistant.Globals;

public class FIO46C_DoNotAccessClosedFile extends SecureCodingRule_C {
	
	private String[] stdFileType = {"stdin", "stdout", "stderr"};
	private String[] stdFunctions = {"printf(", "perror(", "getc("};
	private boolean ruleViolated;
	
	@Override
	public boolean violate_CDT(IASTNode node) {
		ruleViolated = false;
		
		
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		return "CERT Website- Using the value of a pointer to a FILE "
				+ "object after the associated file is closed is "
				+ "undefined behavior. (See undefined behavior 148.) "
				+ "Programs that close the standard streams (especially "
				+ "stdout but also stderr and stdin) must be careful "
				+ "not to use these streams in subsequent function "
				+ "calls, particularly those that implicitly operate "
				+ "on them (such as printf(), perror(), and getc()).";
	}

	@Override
	public String getRuleName() {
		return Globals.RuleNames.FIO46_C;
	}

	@Override
	public String getRuleID() {
		return Globals.RuleID.FIO46_C;
	}

	@Override
	public String getRuleRecommendation() {
		//TODO
		return "";
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean violate_IASTExpression(IASTExpression e) {
		// TODO Auto-generated method stub
		return false;
	}

}
