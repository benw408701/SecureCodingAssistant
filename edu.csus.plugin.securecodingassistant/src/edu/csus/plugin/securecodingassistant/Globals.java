package edu.csus.plugin.securecodingassistant;

import java.util.HashMap;
import java.util.TreeMap;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

/**
 * Global variables used in Secure Coding Assistant
 * @author Ben White
 *
 */
public class Globals {
	
	/**
	 * Marker-related global variables
	 * @author Ben White
	 *
	 */
	public static class Markers {
		/**
		 * Custom IMarker type
		 */
		public static final String SECURE_MARKER = "edu.csus.plugin.securecodingassistant.securecodingmarker";
		
		
		/**
		 * Custom string attribute, the rule that was violated
		 */
		public static final String VIOLATED_RULE = SECURE_MARKER + ".violatedRule";
	
		/**
		 * Custom int attribute, the node hashCode() 
		 */
		public static final String HASHCODE = SECURE_MARKER + ".hashCode";
		
		/**
		 * Custom int attribute, the total solution number for this rule 
		 */
		public static final String RULE_ID = SECURE_MARKER + ".ruleId";
		
		/**
		 * Custom string attribute, the security level of the violated rule.
		 * Possible values: {@link Globals.Markers#SECURITY_LEVEL_LOW},
		 * {@link Globals.Markers#SECURITY_LEVEL_MEDIUM} or {@link Globals.Markers#SECURITY_LEVEL_HIGH}
		 */
		public static final String SECURITY_LEVEL = SECURE_MARKER + ".securityLevel";
		
		/**
		 * L3 according to CERT website
		 */
		public static final int SECURITY_LEVEL_LOW = 1;
		
		/**
		 * L2 according to CERT website
		 */
		public static final int SECURITY_LEVEL_MEDIUM = 2;
		
		/**
		 * L1 according to CERT website
		 */
		public static final int SECURITY_LEVEL_HIGH = 3;
		
		
	}
	
	public static class RuleNames {
		public static final String PRECONDITION_CHECK = "Precondition check";
		public static final String POSTCONDITION_CHECK = "Postcondition check";
		public static final String INVARIANT_CHECK = "Invariant check";
		public static final String ENV02_J = "Do not trust the values of environment variables";
		public static final String DCL02_J = "Do not modify the collection's elements during an enhanced for"
		+ " statement";
		public static final String ERR08_J = "Do not catch NullPointerException or any of its ancestors";
		public static final String EXP00_J = "Do not ignore values returned by methods";
		public static final String EXP02_J = "Do not use the Object.equals() method to compare"
		+ " two arrays";
		public static final String FIO08_J = "Distinguish between characters or bytes read from a stream and -1";
		public static final String IDS00_J = "Prevent SQL injection";
		public static final String IDS01_J = "Normalize strings before validating them";
		public static final String IDS07_J = "Sanitize untrusted data passed to the Runtime.exec() method";
		public static final String IDS11_J = "Perform any string modifications before validation";
		public static final String LCK09_J = "Do not perform operations that can block while holding a lock";
		public static final String MET04_J = "Do not increase the accessibility of overridden or hidden methods";
		public static final String MET06_J = "Do not invoke overridable methods in clone()";
		public static final String MSC02_J = "Generate strong random numbers";
		public static final String NUM07_J = "Do not attempt comparisons with NaN";
		public static final String NUM09_J = "Do not use floating-point variables as loop counters";
		public static final String OBJ09_J = "Compare classes and not class names";
		public static final String SEC07_J = "Call the superclass's getPermissions() method when writing"
		+ "a custom class loader.";
		public static final String SER01_J = "Do not deviate from the proper signatures of serialization methods";
		public static final String STR00_J = "Don't form strings containing partial characters from variable-width encodings";
		public static final String THI05_J = "Do not use Thread.stop() to terminate threads";
	}
	
	public static class RuleID {
		public static final String PRECONDITION = "Precondition";
		public static final String POSTCONDITION = "Postcondition";
		public static final String INVARIANT = "Invariant";
		public static final String ENV02_J = "ENV02-J";
		public static final String DCL02_J = "DCL02-J";
		public static final String ERR08_J = "ERR08-J";
		public static final String EXP00_J = "EXP00-J";
		public static final String EXP02_J = "EXP02-J";
		public static final String FIO08_J = "FIO08-J";
		public static final String IDS00_J = "IDS00-J";
		public static final String IDS01_J = "IDS01-J";
		public static final String IDS07_J = "IDS07-J";
		public static final String IDS11_J = "IDS11-J";
		public static final String LCK09_J = "LCK09-J";
		public static final String MET04_J = "MET04-J";
		public static final String MET06_J = "MET06-J";
		public static final String MSC02_J = "MSC02-J"; 
		public static final String NUM07_J = "NUM07-J";
		public static final String NUM09_J = "NUM09-J";
		public static final String OBJ09_J = "OBJ09-J";
		public static final String SEC07_J = "SEC07-J"; 
		public static final String SER01_J = "SER01-J";
		public static final String STR00_J = "STR00-J";
		public static final String THI05_J = "THI05-J"; 
	}
	
	public static class Annotations {
		public static final String ENSURES = "Ensures";
		public static final String REQUIRES = "Requires";
		public static final String INVARIANT = "Invariant";
		public static final String SKIP_CONDITION_CHECK = "SkipConditionCheck";
		public static final String SKIP_INVARIANT_CHECK = "SkipInvariantCheck";
		public static final String THROW_ENSURES = "ThrowEnsures";
		public static final String SKIP_RULE_CHECK = "SkipRuleCheck";
	}
	
	public static HashMap<String,TreeMap<String, ASTRewrite>> RULE_SOLUTIONS = new HashMap<>();
	public static HashMap<String, ICompilationUnit> RULE_ICOMPILATIONUNIT = new HashMap<>();
}
