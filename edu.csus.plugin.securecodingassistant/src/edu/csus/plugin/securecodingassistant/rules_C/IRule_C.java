package edu.csus.plugin.securecodingassistant.rules_C;

import java.util.TreeMap;

//import org.eclipse.jdt.core.dom.ASTNode;
//import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

import edu.csus.plugin.securecodingassistant.Globals;
import edu.csus.plugin.securecodingassistant.rules.IRule;


/**
 * Extending IRule interface for C Secure Coding Rules. 
 * 
 * @author Victor Melnik
 *
 */
public interface IRule_C {
	
	/**
	 * Checks to see if the rule has been violated in a given node
	 * @param node The node to be evaluated
	 * @return true if the rule was violated, false otherwise
	 */
	//public boolean violated(ASTNode node);
	
	//CDT violate method
	public boolean violate_CDT(IASTNode node);
	
	
	/**
	 * The description of the rule that was violated
	 * @return The description of the rule that was violated
	 */
	public String getRuleText();
	
	/**
	 * The name of the rule violated
	 * @return The name of the rule violated
	 */
	public String getRuleName();
	
	/**
	 * The ID of the rule violated
	 * @return The name of the rule violated
	 */
	public String getRuleID();
	
	/**
	 * The recommended action that will satisfy the rule
	 * @return The recommended action that will satisfy the rule
	 */
	public String getRuleRecommendation();
	
	/**
	 * The security level of the violated rule}
	 * @return A {@link Globals.Markers} security level
	 * @see Globals.Markers
	 */
	public int securityLevel();
	
	
	/**
	 * The website URL for the rule on the CERT website
	 * @return The URL of the rule from the CERT website
	 */
	public String getRuleURL();
	
	
	//CDT TreeMap function

	public ITranslationUnit getITranslationUnit();

}
