package edu.csus.plugin.securecodingassistant.rules_C;

import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * <b><i>The text and/or code below is from the CERT website:
 * <a target="_blank"href="https://wiki.sei.cmu.edu/confluence/display/seccode">
 * https://wiki.sei.cmu.edu/confluence/display/seccode </a></i></b>
 * <p>
 * C Secure Coding Rule: ENV33-C. Do not call system()
 * </p>
 * <p>
 * The C Standard system() function executes a specified command by invoking
 *  an implementation-defined command processor, such as a UNIX shell or 
 *  CMD.EXE in Microsoft Windows. The POSIX popen() and Windows _popen() 
 *  functions also invoke a command processor but create a pipe between 
 *  the calling program and the executed command, returning a pointer to 
 *  a stream that can be used to either read from or write to the pipe 
 *  [IEEE Std 1003.1:2013]. 
 * </p>
 * 
 * <p>
 * Use of the system() function can result in exploitable vulnerabilities, in
 *  the worst case allowing execution of arbitrary system commands. Situations
 *   in which calls to system() have high risk include the following: 
 *
 *	When passing an unsanitized or improperly sanitized command string 
 *		originating from a tainted source
 *	If a command is specified without a path name and the command processor
 *		path name resolution mechanism is accessible to an attacker
 *	If a relative path to an executable is specified and control over the
 * 		current working directory is accessible to an attacker
 *	If the specified executable program can be spoofed by an attacker
 *	Do not invoke a command processor via system() or equivalent functions
 * 		to execute a command. 
 * </p>
 *  
 * @author Victor Melnik (Plugin Logic), CERT (Rule Definition)
 * @see C Secure Coding Rule define by CERT: <a target="_blank" 
 * href="https://wiki.sei.cmu.edu/confluence/pages/viewpage.action?
 * pageId=87152177">ENV33-C</a>
 *
 */

public class ENV33C_DoNotCallSystem extends SecureCodingRule_C {

	
	private String system_str = "system(";
	private boolean ruleViolated;
	
	@Override
	public boolean violate_CDT(IASTNode node) {
		ruleViolated = false;
		if(node instanceof IASTFunctionCallExpression)
		{
			if(node.getRawSignature().contains(system_str) || 
					node.getRawSignature().contains("SYSTEM("))
			{
				ruleViolated = true;	
				
			}
		}
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		return "CERT Website- Use of the system() function can result in exploitable "
				+ "vulnerabilities, in the worst case allowing execution "
				+ "of arbitrary system commands. Do not invoke a command processor via system() or "
				+ "equivalent functions to execute a command. ";
	}

	@Override
	public String getRuleName() {
		return Globals.RuleNames.ENV33_C;
	}

	@Override
	public String getRuleID() {
		return Globals.RuleID.ENV33_C;
	}

	@Override
	public String getRuleRecommendation() {
		//TODO
		return "For POSIX use the exec family or functions or implement functionality in the"
				+ "program using the exisiting library call. \nIn Microsoft Windows use the"
				+ "CreateProcess() API or SHGetKnownFolderPath() API with DeleteFile() API";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_HIGH;
	}


	//@Override
	public ITranslationUnit getITranslationUnit_CDT() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRuleURL() {
		
		return "https://wiki.sei.cmu.edu/confluence/pages/viewpage.action?pageId=87152177";
	}
}
