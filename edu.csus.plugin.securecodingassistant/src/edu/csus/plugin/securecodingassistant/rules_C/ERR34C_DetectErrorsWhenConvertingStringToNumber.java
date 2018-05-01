package edu.csus.plugin.securecodingassistant.rules_C;

import java.util.ArrayList;
import java.util.Arrays;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * <b><i>The text and/or code below is from the CERT website:
 * <a target="_blank"href="https://wiki.sei.cmu.edu/confluence/display/seccode">
 * https://wiki.sei.cmu.edu/confluence/display/seccode </a></i></b>
 * <p>
 * C Secure Coding Rule: ERR34-C. Detect errors when converting a string to a
 * number.
 * </p>
 * <p>
 * The process of parsing an integer or floating-point number from a string 
 * can produce many errors. The string might not contain a number. It might
 *  contain a number of the correct type that is out of range (such as an 
 *  integer that is larger than INT_MAX). The string may also contain extra
 *   information after the number, which may or may not be useful after the
 *    conversion. These error conditions must be detected and addressed when
 *     a string-to-number conversion is performed using a C Standard Library
 *      function.
 * </p>
 * 
 * <p>
 * The strtol(), strtoll(),  strtoimax(), strtoul(), strtoull(), strtoumax(),
 *  strtof(), strtod(), and strtold() functions convert the initial portion
 *   of a null-terminated byte string to a long int, long long int, intmax_t,
 *    unsigned long int, unsigned long long int, uintmax_t, float, double, and
 *     long double representation, respectively.
 * </p>
 * 
 * <p>
 * Use one of the C Standard Library strto*() functions to parse an integer or
 *  floating-point number from a string. These functions provide more robust
 *  error handling than alternative solutions. Also, use the strtol() function
 *  to convert to a smaller signed integer type such as signed int, signed
 *  short, and signed char, testing the result against the range limits for
 *  that type. Likewise, use the strtoul() function to convert to a smaller
 *  unsigned integer type such as unsigned int, unsigned short, and 
 *  unsigned char, and test the result against the range limits for that
 *  type. These range tests do nothing if the smaller type happens to 
 *  have the same size and representation for a particular implementation.
 * </p>
 * 
 * @author Victor Melnik (Plugin Logic), CERT (Rule Definition)
 * @see C Secure Coding Rule define by CERT: <a target="_blank" 
 * href="https://wiki.sei.cmu.edu/confluence/display/c/ERR34-C.+Detect+errors
 * +when+converting+a+string+to+a+number">ERR34-C</a>
 *
 */

public class ERR34C_DetectErrorsWhenConvertingStringToNumber extends SecureCodingRule_C {

	boolean ruleViolated;	
	private ArrayList<String> unSafeFunctions = new ArrayList<String>(
			Arrays.asList("atoi","atol", "atoll", "atof", "sscanf", "scanf", "fscanf")
			);
	
	@Override
	public boolean violate_CDT(IASTNode node) {
		ruleViolated = false;
		if(node instanceof IASTFunctionCallExpression)
		{
			
			String funcName = ((IASTFunctionCallExpression)node).getFunctionNameExpression().getRawSignature();
			
			if(unSafeFunctions.contains(funcName))
			{
				ruleViolated = true;
			}
		}
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		return "CERT Website- The atoi(), atol(), atoll(), and atof() functions convert the "
				+ "initial portion of a string token to int, long int, long long int, and "
				+ "double representation, respectively. Unfortunately, atoi(), related "
				+ "functions, and scanf family of functions lack a mechanism for reporting"
				+ " errors for invalid values.";
	}

	@Override
	public String getRuleName() {
		return Globals.RuleNames.ERR34_C;
	}

	@Override
	public String getRuleID() {
		return Globals.RuleID.ERR34_C;
	}

	@Override
	public String getRuleRecommendation() {
		return "Use one of the C Standard Library strto*() functions instead.";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_LOW;
	}

	@Override
	public String getRuleURL() {
		
		return "https://wiki.sei.cmu.edu/confluence/display/c/ERR34-C.+Detect+errors+when+converting+a+string+to+a+number";
	}


}
