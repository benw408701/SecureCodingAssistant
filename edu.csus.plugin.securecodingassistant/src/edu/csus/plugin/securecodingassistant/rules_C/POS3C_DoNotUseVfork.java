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
 * C Secure Coding Rule: POS33-C. Do not use vfork()
 * </p>
 * <p>
 * Using the vfork function introduces many portability and security issues. 
 * There are many cases in which undefined and implementation-specific 
 * behavior can occur, leading to a denial-of-service vulnerability.
 * </p>
 * 
 * <p>
 * According to the vfork man page,
 * The vfork() function has the same effect as fork(), except that the 
 * behavior is undefined if the process created by vfork() either modifies 
 * any data other than a variable of type pid_t used to store the return 
 * value from vfork(), or returns from the function in which vfork() was 
 * called, or calls any other function before successfully calling _exit() 
 * or one of the exec family of functions.
 * </p>
 * 
 * <p>
 * Furthermore, older versions of Linux are vulnerable to a race condition, 
 * occurring when a privileged process calls vfork(), and then the child 
 * process lowers its privileges and calls execve(). The child process is 
 * executed with the unprivileged user's UID before it calls execve().
 * </p>
 * 
 * <p>
 * Because of the implementation of the vfork() function, the parent process 
 * is suspended while the child process executes. If a user sends a signal 
 * to the child process, delaying its execution, the parent process (which 
 * is privileged) is also blocked. This means that an unprivileged process 
 * can cause a privileged process to halt, which is a privilege inversion 
 * resulting in a denial of service.
 * </p>
 * 
 * @author Victor Melnik (Plugin Logic), CERT (Rule Definition)
 * @see C Secure Coding Rule define by CERT: <a target="_blank" 
 * href="https://wiki.sei.cmu.edu/confluence/pages/viewpage.action?
 * pageId=87152373">POS33-C</a>
 *
 */

public class POS3C_DoNotUseVfork extends SecureCodingRule_C {
	
	private String vfork_str = "vfork(";
	private boolean ruleViolated;
	
	@Override
	public boolean violate_CDT(IASTNode node) {
		
		ruleViolated = false;
		if(node instanceof IASTFunctionCallExpression)
		{
			if(node.getRawSignature().startsWith(vfork_str))
			{
				ruleViolated = true;	
				
			}
		}
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		
		return "Do not use vfork() function. vfork() introduces many portobility "
				+ "and security issues.";
	}

	@Override
	public String getRuleName() {
		return Globals.RuleNames.POS33_C;
	}

	@Override
	public String getRuleID() {
		
		return Globals.RuleID.POS33_C;
	}

	@Override
	public String getRuleRecommendation() {
		
		return "Repalce the call to vfork() with a call to fork()";
	}

	@Override
	public int securityLevel() {
		
		return Globals.Markers.SECURITY_LEVEL_LOW;
	}

	@Override
	public ITranslationUnit getITranslationUnit() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRuleURL() {
		
		return "https://wiki.sei.cmu.edu/confluence/exportword?pageId=87152373";
	}

}
