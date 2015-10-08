/**
 * 
 */
package edu.csus.plugin.securecodingassistant.rules;

import org.eclipse.jdt.core.dom.ASTNode;

/**
 * @author Ben
 *
 */
class THI05J_DoNotUseThreadStopToTerminateThreads implements IRule {

	/* (non-Javadoc)
	 * @see edu.csus.plugin.securecodingassistant.rules.IRule#violated(org.eclipse.jdt.core.dom.ASTNode)
	 */
	@Override
	public boolean violated(ASTNode node) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see edu.csus.plugin.securecodingassistant.rules.IRule#getRuleText()
	 */
	@Override
	public String getRuleText() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.csus.plugin.securecodingassistant.rules.IRule#getRuleName()
	 */
	@Override
	public String getRuleName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.csus.plugin.securecodingassistant.rules.IRule#getRuleRecommendation()
	 */
	@Override
	public String getRuleRecommendation() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see edu.csus.plugin.securecodingassistant.rules.IRule#securityLevel()
	 */
	@Override
	public int securityLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

}
