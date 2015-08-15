package edu.csus.plugin.securecodingassistant.rules;

import org.eclipse.jdt.core.dom.ASTNode;

/**
 * Interface for a secure coding rule
 * @author Ben White
 *
 */
public interface IRule {
	
	/**
	 * Checks to see if the rule has been violated in a given expression
	 * @param node The node to be evaluated
	 * @return true if the rule was violated, false otherwise
	 */
	public boolean violated(ASTNode node);
	
	/**
	 * Contains the name of the rule, a description of it and the 
	 * recommendation for correcting it
	 * @return The rule text containing the name, description and recommendation
	 */
	public String getRuleText();
}
