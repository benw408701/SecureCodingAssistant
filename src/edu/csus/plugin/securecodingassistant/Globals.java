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
		public static String SECURE_MARKER = "edu.csus.plugin.securecodingassistant.securecodingmarker";
		
		
		/**
		 * Custom string attribute, the rule that was violated
		 */
		public static String VIOLATED_RULE = "violatedRule";
	}
}
