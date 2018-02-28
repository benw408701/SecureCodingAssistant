package edu.csus.plugin.securecodingassistant.rules_C;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.csus.plugin.securecodingassistant.Globals;

public class SIG30C_CallOnlyAsynchronousSafeFuntionsWithinSignalHandlers implements IRule_C {

	private boolean ruleViolated = false;
	private ArrayList<IASTNode> arrListOfFuncCalls = new ArrayList<IASTNode>();
	private ArrayList<IASTNode> arrListOfUserFunctions = new ArrayList<IASTNode>();
	
	private ArrayList<String> arrListOfHandlerNames = new ArrayList<String>();
	
	private Iterator<IASTNode> funcIT;
	private Iterator<IASTNode> funcUserIT;
	
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
	private Iterator<String> unsafeUserDefIT;
	private ArrayList<String> arrListOfUserFunctionsNames = new ArrayList<String>();
	
	private Iterator<String> safeIT;
	private Iterator<String> handlerNameIT;
	
	IASTTranslationUnit currITU = null;
	
	@Override
	public boolean violate_CDT(IASTNode node) {
		ruleViolated = false;
		
		
		
		if(/*node.getContainingFilename().contains("SIG30C") && */ (node.getFileLocation().getContextInclusionStatement() == null)
				&& node.getTranslationUnit().getRawSignature().contains("signal"))
		{
			
			/*
			if(node instanceof IASTFunctionDefinition && node instanceof IASTDeclaration)
			{
				System.out.println("IASTFunctionDefinition" + node.getRawSignature());
			}
			*/
			
			if(currITU == null || currITU != node.getTranslationUnit())
			{
				currITU = node.getTranslationUnit();
				
				arrListOfHandlerNames.clear();
				arrListOfUserFunctions.clear();
				unsafeUserDefinedFunctions.clear();
				arrListOfUserFunctionsNames.clear();
				
				//**********************************************************************************************
				//get all handler functions
				ASTVisitorFindMatch visitor = new ASTVisitorFindMatch("handler", "GetFunctions");
				
				currITU.accept(visitor);
				
				arrListOfHandlerNames = visitor.arrayListofHandlerNames();
				//**********************************************************************************************
				
				
				//**********************************************************************************************
				//getalluserdefined functions
				ASTVisitorFindMatch visitorUserFunc = new ASTVisitorFindMatch(null, "UserDefinedFunctions");
				currITU.accept(visitorUserFunc);
				arrListOfUserFunctions = visitorUserFunc.arrayListofDeclaration();
				//**********************************************************************************************
				
				
				//**********************************************************************************************
				//get names of all user defined functions
				
					
					funcUserIT = arrListOfUserFunctions.iterator();
					while(funcUserIT.hasNext())
					{
						IASTNode userDefFunctionNameTemp = funcUserIT.next();
						String userDefFunctionNameTempString = ((IASTFunctionDefinition) userDefFunctionNameTemp).getDeclarator().getName().getRawSignature();
						arrListOfUserFunctionsNames.add(userDefFunctionNameTempString);
					}
				
				//************************************************************************************************
				
				
				//*****************************************************************************************
				//visit each User defined function to determin is Safe
				funcUserIT = arrListOfUserFunctions.iterator();
				
				while(funcUserIT.hasNext())
				{
					arrListOfFuncCalls.clear();
					ASTVisitorFindMatch visitorEachHandler = new ASTVisitorFindMatch(null, "GetFunctions");
					IASTNode userDefFunctionTemp = funcUserIT.next();
					String userDefFunctionTempString = ((IASTFunctionDefinition) userDefFunctionTemp).getDeclarator().getName().getRawSignature();
					
					userDefFunctionTemp.accept(visitorEachHandler); //visit each user defined function to see if it is safe
				
					arrListOfFuncCalls = visitorEachHandler.arrayListofFunctions();
					
					
					funcIT = arrListOfFuncCalls.iterator();
					while(funcIT.hasNext())
					{
						IASTNode tempFuncPRE = funcIT.next();
						String handlerFuncNamePRE = ((IASTFunctionCallExpression) tempFuncPRE).getFunctionNameExpression().getRawSignature();
						
						if(!(safeFunctions.contains(handlerFuncNamePRE)))
						{
							
							unsafeUserDefinedFunctions.add(userDefFunctionTempString);
							break;
						}
						
					}
				}
				
				//*****************************************************************************
				
				
				/*
				unsafeUserDefIT = unsafeUserDefinedFunctions.iterator();
				
				while(unsafeUserDefIT.hasNext())
				{
					//System.out.println("unsafeUserDefIT: " + unsafeUserDefIT.next());
				}
				
				funcUserIT = arrListOfUserFunctions.iterator();
				
				
				while(funcUserIT.hasNext())
				{
					//System.out.println("User functions: " + funcUserIT.next().getRawSignature());
				}
				*/
				
			}
			
			handlerNameIT = arrListOfHandlerNames.iterator();
			funcUserIT = arrListOfUserFunctions.iterator();
			
			/*
			while(funcUserIT.hasNext())
			{
				System.out.println("User functions: " + funcUserIT.next().getRawSignature() + "\n");
			}
			*/
			
			/*
			while(handlerNameIT.hasNext())
			{
				System.out.println("handlers: " + handlerNameIT.next() + "\n");
			}
			*/
			
			/*
			if((node instanceof IASTFunctionCallExpression) )
			{
				if(((IASTFunctionCallExpression) node).getFunctionNameExpression().getRawSignature().contentEquals("signal"))
				{
					System.out.println("Func NAME: " + ((IASTFunctionCallExpression) node).getFunctionNameExpression().getRawSignature());
					
					System.out.println("Func Arg2: " + ((IASTFunctionCallExpression) node).getArguments()[1].getRawSignature());
					//((IASTFunctionCallExpression) node).getArguments()[1]
				}
			}
			*/
			
			if((node instanceof IASTFunctionDefinition) && (arrListOfHandlerNames.contains(((IASTFunctionDefinition) node).getDeclarator().getName().getRawSignature())))
			{
				//System.out.println("Node: " + node.getRawSignature());
				arrListOfFuncCalls.clear();
				//System.out.println("NodegetDeclSpecifier: " + ((IASTFunctionDefinition) node).getDeclSpecifier().getRawSignature());
				//System.out.println("Node_getDeclarator: " + ((IASTFunctionDefinition) node).getDeclarator().getName().getRawSignature());
				//((IASTFunctionDefinition) node).getDeclarator().
				
				//funcCalls within the handler
				ASTVisitorFindMatch visitor = new ASTVisitorFindMatch(null, "GetFunctions");
				node.accept(visitor);
				
				arrListOfFuncCalls = visitor.arrayListofFunctions();
				
				funcIT = arrListOfFuncCalls.iterator();
				
				while(funcIT.hasNext())
				{
					IASTNode tempFunc = funcIT.next();
					String handlerFuncName = ((IASTFunctionCallExpression) tempFunc).getFunctionNameExpression().getRawSignature();
					
					//System.out.println("Func in Handler: " + tempFunc.getRawSignature());
					//System.out.println("Func NAME: " + handlerFuncName);
					
					if(safeFunctions.contains(handlerFuncName) )
					{
						//System.out.println("Function is safe!!");						
					}
					else
					{
						if(!(unsafeUserDefinedFunctions.contains(handlerFuncName)) && (arrListOfUserFunctionsNames.contains(handlerFuncName)))
						{
							//System.out.println("Function is safe!!");
						}
						else
						{
							//System.out.println("FUnction is not safe");
							ruleViolated = true;
							return ruleViolated;
						}
						//System.out.println("FUnction is not safe");
						/*
						funcUserIT = arrListOfUserFunctions.iterator();
						
						while(funcUserIT.hasNext())
						{
							IASTNode tempUserFunc = funcUserIT.next();
							System.out.println("tempUserfunc Namer: " + tempUserFunc.getRawSignature());
							System.out.println("tempUserfunc Namer: " + ((IASTFunctionDefinition) tempUserFunc).getDeclarator().getName().getRawSignature());
							
						}
						ruleViolated = true;
						return ruleViolated;
						*/
					}
					
					//((IASTFunctionCallExpression) tempFunc).getFunctionNameExpression();
				}
				
				safeIT = safeFunctions.iterator();
				/*
				while(safeIT.hasNext())
				{
					String safeFunc = safeIT.next();
					System.out.println("SafeFunction: " + safeFunc);
					
					//((IASTFunctionCallExpression) tempFunc).getFunctionNameExpression();
				}
				*/
			}
			
			
		}
		
		return ruleViolated;
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
