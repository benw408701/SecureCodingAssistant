package edu.csus.plugin.securecodingassistant.rules_C;

import java.util.TreeMap;

import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.csus.plugin.securecodingassistant.Globals;

public class FIO30C_ExcludeUserInputFromFormatStrings extends SecureCodingRule_C {

	@Override
	public boolean violate_CDT(IASTNode node) {
		
		return false;
	}

	@Override
	public String getRuleText() {
		return "CERT Website- Never call a formatted I/O function with a format "
				+" string containing a tainted value .  An attacker who can "
				+"fully or partially control the contents of a format string "
				+"can crash a vulnerable process, view the contents of the stack, "
				+"view memory content, or write to an arbitrary memory location. "
				+"Consequently, the attacker can execute arbitrary code with the "
				+"permissions of the vulnerable process [Seacord 2013b]. "
				+"Formatted output functions are particularly dangerous because "
				+"many programmers are unaware of their capabilities. ";
	}

	@Override
	public String getRuleName() {
		return Globals.RuleNames.FIO30_C;
	}

	@Override
	public String getRuleID() {
		return Globals.RuleID.FIO30_C;
	}

	@Override
	public String getRuleRecommendation() {
		//TODO
		return "";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_HIGH;
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
