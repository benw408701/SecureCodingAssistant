package edu.csus.plugin.securecodingassistant.rules_C;

import java.util.TreeMap;

import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.csus.plugin.securecodingassistant.Globals;

public class INT30C_EnsureThatUnsignedIntegerOperationsDoNotWrap extends SecureCodingRule_C {

	@Override
	public boolean violate_CDT(IASTNode node) {
		
		return false;
	}

	@Override
	public String getRuleText() {
		return "CERT Website- A computation involving unsigned operands can never "
				+"overflow, because a result that cannot be represented by the "
				+"resulting unsigned integer type is reduced modulo the number "
				+"that is one greater than the largest value that can be "
				+"represented by the resulting type. This behavior is more "
				+"informally called unsigned integer wrapping. Unsigned "
				+"integer operations can wrap if the resulting value cannot "
				+"be represented by the underlying representation of the integer.";
	}

	@Override
	public String getRuleName() {
		return Globals.RuleNames.INT30_C;
	}

	@Override
	public String getRuleID() {
		return Globals.RuleID.INT30_C;
	}

	@Override
	public String getRuleRecommendation() {
		//TODO
		return "";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_MEDIUM;
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
