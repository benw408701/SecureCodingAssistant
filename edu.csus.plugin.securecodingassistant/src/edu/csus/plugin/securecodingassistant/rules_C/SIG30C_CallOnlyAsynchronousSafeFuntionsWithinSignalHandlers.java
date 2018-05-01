package edu.csus.plugin.securecodingassistant.rules_C;

import java.util.ArrayList;
import java.util.Arrays;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * <b><i>The text and/or code below is from the CERT website:
 * <a target="_blank"href="https://wiki.sei.cmu.edu/confluence/display/seccode">
 * https://wiki.sei.cmu.edu/confluence/display/seccode </a></i></b>
 * <p>
 * C Secure Coding Rule: SIG30-C. Call only asynchronous-safe functions within
 * signal handlers
 * </p>
 * <p>
 * Call only asynchronous-safe functions within signal handlers. For strictly
 * conforming programs, only the C standard library functions abort(), _Exit(),
 * quick_exit(), and signal() can be safely called from within a signal handler.
 * </p>
 * 
 * <p>
 * The C Standard, 7.14.1.1, paragraph 5 [ISO/IEC 9899:2011], states that if 
 * the signal occurs other than as the result of calling the abort() or 
 * raise() function, the behavior is undefined if
 * ...the signal handler calls any function in the standard library other 
 * than the abort function, the _Exit function, the quick_exit function, or 
 * the signal function with the first argument equal to the signal number 
 * corresponding to the signal that caused the invocation of the handler.
 * </p>
 * 
 * <p>
 * Implementations may define a list of additional asynchronous-safe functions. 
 * These functions can also be called within a signal handler. This 
 * restriction applies to library functions as well as application-defined 
 * functions.
 * </p>
 * 
 * <p>
 * According to the C Rationale, 7.14.1.1 [C99 Rationale 2003],
 * When a signal occurs, the normal flow of control of a program is 
 * interrupted. If a signal occurs that is being trapped by a signal 
 * handler, that handler is invoked. When it is finished, execution 
 * continues at the point at which the signal occurred. This arrangement 
 * can cause problems if the signal handler invokes a library function that 
 * was being executed at the time of the signal.
 * </p>
 * 
 * <p>
 * In general, it is not safe to invoke I/O functions from within signal 
 * handlers. Programmers should ensure a function is included in the list 
 * of an implementation's asynchronous-safe functions for all implementations 
 * the code will run on before using them in signal handlers.
 * </p>
 * 
 * @author Victor Melnik (Plugin Logic), CERT (Rule Definition)
 * @see C Secure Coding Rule define by CERT: <a target="_blank" 
 * href="https://wiki.sei.cmu.edu/confluence/display/c/SIG30-C.+Call+only+
 * asynchronous-safe+functions+within+signal+handlers">SIG30-C</a>
 *
 */

public class SIG30C_CallOnlyAsynchronousSafeFuntionsWithinSignalHandlers extends SecureCodingRule_C {

	private boolean ruleViolated = false;
	private ArrayList<String> safeFunctions = new ArrayList<String>(
				Arrays.asList("abort", "_Exit", "quick_exit", "signal", "fexecve", "posix_trace_event",
						"sigprocmask", "_exit", "fork", "pselect", "sigqueue", "fstat", "pthread_kill", 
						"sigset", "accept", "fstatat", "pthread_self", "sigsuspend", "access", "fsync",
						"pthread_sigmask", "sleep", "aio_error", "ftruncate", "raise", "sockatmark",
						"aio_return", "futimens", "read", "socket", "aio_suspend", "getegid", "readlink",
						"socketpair", "alarm", "geteuid", "readlinkat", "stat", "bind", "getgid", "recv",
						"symlink", "cfgetispeed", "getgroups", "recvfrom", "symlinkat", "cfgetospeed", "getpeername",
						"recvmsg", "tcdrain", "cfsetispeed", "getpgrp", "rename", "tcflow", "cfsetospeed",
						"getpid", "renameat", "tcflush", "chdir", "getppid", "rmdir", "tcgetattr", "chmod",
						"getsockname", "select", "tcgetpgrp", "chown", "getsockopt", "sem_post", "tcsendbreak",
						"clock_gettime", "getuid", "send", "tcsetattr", "close", "kill", "sendmsg", "tcsetpgrp",
						"connect", "link", "sendto", "time", "creat", "linkat", "setgid", "timer_getoverrun",
						"dup", "listen", "setpgid", "timer_gettime", "dup2", "lseek", "setsid", "timer_settime",
						"execl", "lstat", "setsockopt", "times", "execle", "mkdir", "setuid", "umask", "execv",
						"mkdirat", "shutdown", "uname", "execve", "mkfifo", "sigaction", "unlink", "faccessat",
						"mkfifoat", "sigaddset", "unlinkat", "fchdir", "mknod", "sigdelset", "utime", "fchmod",
						"mknodat", "sigemptyset", "utimensat", "fchmodat", "open", "sigfillset", "utimes", 
						"fchown", "openat", "sigismember", "wait", "fchownat", "pause", "waitpid", "fcntl",
						"pipe", "sigpause", "write", "fdatasync", "poll", "sigpending")
			);
	private ArrayList<String> unsafeUserDefinedFunctions = new ArrayList<String>();
	
	@Override
	public boolean violate_CDT(IASTNode node) {
		ruleViolated = false;
		unsafeUserDefinedFunctions.clear();
		
		if((node.getFileLocation().getContextInclusionStatement() == null)
				&& node.getTranslationUnit().getRawSignature().contains("signal.h"))
		{
						
			if(node instanceof IASTFunctionDefinition)
			{
				ASTNodeProcessor_C visitAll = new ASTNodeProcessor_C();
				node.getTranslationUnit().accept(visitAll);
				
				String functionName = ((IASTFunctionDefinition)node).getDeclarator().getName().getRawSignature();
				
				for(NodeNumPair_C nnP: visitAll.getFunctionCalls())
				{
					if(nnP.getNode().getRawSignature().startsWith("signal"))
					{
						for(String functionParam: Utility_C.getFunctionParameterVarName(((IASTFunctionCallExpression)nnP.getNode())))
						{
							if(functionName.contentEquals(functionParam))
							{
								unsafeUserDefinedFunctions = getUnSafeUserFunctions(visitAll);
								String origNodeName = ((IASTFunctionDefinition)node).getDeclarator().getName().getRawSignature();
								
								if(unsafeUserDefinedFunctions.contains(origNodeName))
								{
									ruleViolated = true;
									return ruleViolated;
								}
							}
						}
					}
				}
			}	
		}
		return ruleViolated;
	}
	
	
	/**
	 * Returns list of all unsafe functions in TranslationUnit
	 * @param ASTNodeProcessor_C visitorSafe
	 * @return ArrayList<String>
	 */
	private ArrayList<String> getUnSafeUserFunctions(ASTNodeProcessor_C visitorSafe)
	{
		ArrayList<String> listUnsafe = new ArrayList<String>();
		ArrayList<String> initialScan = new ArrayList<String>();
		
		int userFunctionCount = visitorSafe.getFunctionDefinitions().size();
		//get the name of all the user defined functions
		for(NodeNumPair_C userFuncInitial: visitorSafe.getFunctionDefinitions())
		{
			String initFunc = ((IASTFunctionDefinition)userFuncInitial.getNode()).getDeclarator().getName().getRawSignature();
			initialScan.add(initFunc);
		}
		
		//check if the user defined functions contain calls to any unsafeFunctions
		for(NodeNumPair_C userFuncDefinitions: visitorSafe.getFunctionDefinitions())
		{
			for(NodeNumPair_C internalFunctionCall : visitorSafe.getFunctionCalls())
			{
				String functionCallName = ((IASTFunctionCallExpression)internalFunctionCall.getNode()).getFunctionNameExpression().getRawSignature();
				if((!safeFunctions.contains(functionCallName) && !initialScan.contains(functionCallName) )
						&& userFuncDefinitions.getNode().contains(internalFunctionCall.getNode()))
				{
					String secFunc  = ((IASTFunctionDefinition)userFuncDefinitions.getNode()).getDeclarator().getName().getRawSignature();
					listUnsafe.add(secFunc);
					break;
				}
			}
		}
		
		//check if unsafe user defined functions are contained within other userdefined functions
		for(int num = 0; num > userFunctionCount; num++)
		{
			for(NodeNumPair_C userFuncDefinitions: visitorSafe.getFunctionDefinitions())
			{
				for(NodeNumPair_C internalFunctionCall : visitorSafe.getFunctionCalls())
				{
					String functionCallName = ((IASTFunctionCallExpression)internalFunctionCall.getNode()).getFunctionNameExpression().getRawSignature();
					if((listUnsafe.contains(functionCallName))
						&& userFuncDefinitions.getNode().contains(internalFunctionCall.getNode()))
					{
						String secFunc  = ((IASTFunctionDefinition)userFuncDefinitions.getNode()).getDeclarator().getName().getRawSignature();
						listUnsafe.add(secFunc);
						
					}
				}
			}
		}
		return listUnsafe;
	}

	@Override
	public String getRuleText() {
		
		return "CERT Website- Call only asynchronous-safe functions within signal handlers. For"
				+ " strictly conforming programs, only the C standard library functions abort()"
				+ ", _Exit(), quick_exit(), and signal() can be safely called from within a "
				+ "signal handler.";
	}

	@Override
	public String getRuleName() {
		
		return Globals.RuleNames.SIG30_C;
	}

	@Override
	public String getRuleID() {
		
		return Globals.RuleID.SIG30_C;
	}

	@Override
	public String getRuleRecommendation() {
		
		return "Only use asynchronous-safe functions within signal handler."
				+ "Go to URL for full list.";
	}

	@Override
	public int securityLevel() {
		
		return Globals.Markers.SECURITY_LEVEL_HIGH;
	}

	@Override
	public String getRuleURL() {
		
		return "https://wiki.sei.cmu.edu/confluence/display/c/SIG30-C.+Call+only+asynchronous-safe+functions+within+signal+handlers";
	}

	@Override
	public ITranslationUnit getITranslationUnit() {
		// TODO Auto-generated method stub
		return null;
	}

}
