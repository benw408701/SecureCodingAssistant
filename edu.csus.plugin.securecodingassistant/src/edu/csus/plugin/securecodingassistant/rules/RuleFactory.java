package edu.csus.plugin.securecodingassistant.rules;

import java.util.ArrayList;

/**
 * Creates a set of rules that implement the <code>IRule</code> interface
 * @author Ben White
 * @see IRule
 */
public final class RuleFactory {
	/**
	 * Cannot instantiate
	 */
	private RuleFactory() {
		
	}
	
	/**
	 * Creates a collection of all possible rules to test
	 * @return A collection of all possible rules to test
	 */
	public static ArrayList<IRule> getAllRules() {
		ArrayList<IRule> rules = new ArrayList<IRule>();
		
		// Input Validation and Data Sanitization
		rules.add(new IDS00J_PreventSQLInjection());
		rules.add(new IDS01J_NormalizeStringsBeforeValidating());
		rules.add(new IDS07J_RuntimeExecMethod());
		rules.add(new IDS11J_ModyStringsBeforeValidation());

		// Characters and Strings
		rules.add(new STR00J_PartialCharFromVarWidthEnc());
		
		return rules;
	}
}
