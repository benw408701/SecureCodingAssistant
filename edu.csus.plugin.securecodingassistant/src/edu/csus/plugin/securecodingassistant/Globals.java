package edu.csus.plugin.securecodingassistant;

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
}
