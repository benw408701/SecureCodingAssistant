package edu.csus.plugin.securecodingassistant.rules_C;

import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.csus.plugin.securecodingassistant.Globals;

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
										if(!vntP.isAtomic())
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
