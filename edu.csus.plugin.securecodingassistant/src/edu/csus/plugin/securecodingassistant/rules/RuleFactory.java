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
		
		rules.add(new IDS00J_PreventSQLInjection());
		rules.add(new IDS01J_NormalizeStringsBeforeValidating());
		rules.add(new IDS07J_RuntimeExecMethod());
		
		return rules;
	}
}
