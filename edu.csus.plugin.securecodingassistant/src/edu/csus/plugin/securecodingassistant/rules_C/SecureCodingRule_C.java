package edu.csus.plugin.securecodingassistant.rules_C;

import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTNode;

public abstract class SecureCodingRule_C implements IRule_C {
	
	@Override
	public boolean violate_CDT(IASTNode node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getRuleText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRuleName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRuleID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRuleRecommendation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int securityLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ITranslationUnit getITranslationUnit() {
		// TODO Auto-generated method stub
		return null;
	}

	public abstract String getRuleURL();

}
