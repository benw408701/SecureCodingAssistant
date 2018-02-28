package edu.csus.plugin.securecodingassistant.rules_C;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.csus.plugin.securecodingassistant.Globals;

public class PRE30C_DoNotCreateUniversalCharacterNameThroughConcatenation implements IRule_C {

	private boolean ruleViolated;
	
	@Override
	public boolean violate_CDT(IASTNode node) {
		// TODO Auto-generated method stub
		ruleViolated = false;
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		// TODO Auto-generated method stub
		return "CERT Website- The C Standard supports universal "
				+ "character names that may be used in identifiers"
				+ ", character constants, and string literals to "
				+ "\ndesignate characters that are not in the basic"
				+ " character set. If a character sequence that "
				+ "matches the syntax of a universal character "
				+ "\nname is produced by token concatenation "
				+ "(6.10.3.3), the behavior is undefined.";
	}

	@Override
	public String getRuleName() {
		
		return Globals.RuleNames.PRE30_C;
	}

	@Override
	public String getRuleID() {
		return Globals.RuleID.PRE30_C;
	}

	@Override
	public String getRuleRecommendation() {
		return "Use a universa; character name, but do not create it"
				+ "using token concatenation";
	}

	@Override
	public int securityLevel() {
		
		return Globals.Markers.SECURITY_LEVEL_LOW;
	}

	@Override
	public String getRuleURL() {
		return "https://wiki.sei.cmu.edu/confluence/display/c/"
				+ "PRE30-C.+Do+not+create+a+universal+character+"
				+ "name+through+concatenation";
	}

	@Override
	public ITranslationUnit getITranslationUnit() {
		// TODO Auto-generated method stub
		return null;
	}

}
