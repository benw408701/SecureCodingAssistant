package edu.csus.plugin.securecodingassistant.rules_C;

import java.util.TreeMap;

import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.csus.plugin.securecodingassistant.Globals;

public class EXP33C_DoNotReadUninitializedMemory extends SecureCodingRule_C {

	@Override
	public boolean violate_CDT(IASTNode node) {
		
		return false;
	}

	@Override
	public String getRuleText() {
		return "CERT Website- eLocal, automatic variables assume unexpected values "
				+ "if they are read before they are initialized.If an object that "
				+ "has automatic storage duration is not initialized explicitly, "
				+ "its value is indeterminate. Uninitialized automatic variables or "
				+ "dynamically allocated memory has indeterminate values, which for "
				+ "objects of some types, can be a trap representation. Reading such "
				+ "trap representations is undefined behavior; it can cause a "
				+ "program to behave in an unexpected manner and provide an avenue "
				+ "for attack. (See undefined behavior 10 and undefined behavior 12.)"
				+ "  In many cases, compilers issue a warning diagnostic message "
				+ "when reading uninitialized variables. ";
	}

	@Override
	public String getRuleName() {
		return Globals.RuleNames.EXP33_C;
	}

	@Override
	public String getRuleID() {
		return Globals.RuleID.EXP33_C;
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
