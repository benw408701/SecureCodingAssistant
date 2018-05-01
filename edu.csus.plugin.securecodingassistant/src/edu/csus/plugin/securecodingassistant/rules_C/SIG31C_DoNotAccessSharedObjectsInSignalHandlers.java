package edu.csus.plugin.securecodingassistant.rules_C;

import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * <b><i>The text and/or code below is from the CERT website:
 * <a target="_blank"href="https://wiki.sei.cmu.edu/confluence/display/seccode">
 * https://wiki.sei.cmu.edu/confluence/display/seccode </a></i></b>
 * <p>
 * C Secure Coding Rule: SIG31-C. Do not access shared objects in signal
 * handlers
 * </p>
 * <p>
 * Accessing or modifying shared objects in signal handlers can result in 
 * race conditions that can leave data in an inconsistent state. The two 
 * exceptions (C Standard, 5.1.2.3, paragraph 5) to this rule are the 
 * ability to read from and write to lock-free atomic objects and variables 
 * of type volatile sig_atomic_t. Accessing any other type of object from a 
 * signal handler is undefined behavior. (See undefined behavior 131.)
 * </p>
 * 
 * <p>
 * The type sig_atomic_t is the integer type of an object that can be 
 * accessed as an atomic entity even in the presence of asynchronous 
 * interrupts. The type of sig_atomic_t is implementation-defined, though 
 * it provides some guarantees. Integer values ranging from SIG_ATOMIC_MIN 
 * through SIG_ATOMIC_MAX, inclusive, may be safely stored to a variable of 
 * the type. In addition, when sig_atomic_t is a signed integer type, 
 * SIG_ATOMIC_MIN must be no greater than -127 and SIG_ATOMIC_MAX no less 
 * than 127. Otherwise, SIG_ATOMIC_MIN must be 0 and SIG_ATOMIC_MAX must be 
 * no less than 255. The macros SIG_ATOMIC_MIN and SIG_ATOMIC_MAX are defined 
 * in the header <stdint.h>.
 * </p>
 * 
 * <p>
 * According to the C99 Rationale [C99 Rationale 2003], other than calling a 
 * limited, prescribed set of library functions,
 * the C89 Committee concluded that about the only thing a strictly conforming 
 * program can do in a signal handler is to assign a value to a volatile 
 * static variable which can be written uninterruptedly and promptly return.
 * </p>
 * 
 * <p>
 * However, this issue was discussed at the April 2008 meeting of ISO/IEC 
 * WG14, and it was agreed that there are no known implementations in which 
 * it would be an error to read a value from a volatile sig_atomic_t 
 * variable, and the original intent of the committee was that both reading 
 * and writing variables of volatile sig_atomic_t would be strictly conforming.
 * </p>
 * 
 * <p>
 * The signal handler may also call a handful of functions, including abort(). 
 * (See SIG30-C. Call only asynchronous-safe functions within signal handlers 
 * for more information.)
 * </p>
 * 
 * @author Victor Melnik (Plugin Logic), CERT (Rule Definition)
 * @see C Secure Coding Rule define by CERT: <a target="_blank" 
 * href="https://wiki.sei.cmu.edu/confluence/display/c/SIG31-C.+Do+not+access
 * +shared+objects+in+signal+handlers">SIG31-C</a>
 *
 */

public class SIG31C_DoNotAccessSharedObjectsInSignalHandlers extends SecureCodingRule_C {

	private boolean ruleViolated;
	@Override
	public boolean violate_CDT(IASTNode node) {
		
		ruleViolated = false;

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
								for(VariableNameTypePair vntP: Utility_C.allVarNameType(visitAll.getvarNamePairList(), node))
								{
									if(!(vntP.getNode() instanceof IASTParameterDeclaration))
									{
										ASTVisitorFindMatch findMatch = new ASTVisitorFindMatch(vntP.getVarName(), "FindMatch");
										node.accept(findMatch);
										if(!vntP.isAtomic() && findMatch.isMatch())
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
			}
		}
		
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		
		return "CERT Website- Accessing or modifying shared objects in signal handlers can result "
				+ "in race conditions that can leave data in an inconsistent state. The two "
				+ "exceptions to this rule are the ability to read from and write to lock-free "
				+ "atomic objects and variables of type volatile sig_atomic_t.";
	}

	@Override
	public String getRuleName() {

		return Globals.RuleNames.SIG31_C;
	}

	@Override
	public String getRuleID() {

		return Globals.RuleID.SIG31_C;
	}

	@Override
	public String getRuleRecommendation() {
		
		return "Only use lock-free atomic objects and variables of type volatile sig_atomic_t in handlers.";
	}

	@Override
	public int securityLevel() {
		
		return Globals.Markers.SECURITY_LEVEL_HIGH;
	}

	@Override
	public String getRuleURL() {

		return "https://wiki.sei.cmu.edu/confluence/display/c/SIG31-C.+Do+not+access+shared+objects+in+signal+handlers";
	}

	@Override
	public ITranslationUnit getITranslationUnit() {
		// TODO Auto-generated method stub
		return null;
	}

}
