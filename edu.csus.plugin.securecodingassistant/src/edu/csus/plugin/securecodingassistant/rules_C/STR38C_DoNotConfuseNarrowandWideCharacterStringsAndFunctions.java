package edu.csus.plugin.securecodingassistant.rules_C;

import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.csus.plugin.securecodingassistant.Globals;

public class STR38C_DoNotConfuseNarrowandWideCharacterStringsAndFunctions implements IRule_C {

	private boolean ruleViolated;
	private String functionName;
	
	@Override
	public boolean violate_CDT(IASTNode node) {
		
		ruleViolated = false;
		functionName = null;
		
		
			if(node.getTranslationUnit().getRawSignature().contains("wchar.h") || node.getTranslationUnit().getRawSignature().contains("wchar_t"))
			{
				
				if(node instanceof IASTFunctionCallExpression)
				{
					ASTNodeProcessor_C visitDec = new ASTNodeProcessor_C();
					node.getTranslationUnit().accept(visitDec);
					
					functionName = ((IASTFunctionCallExpression)node).getFunctionNameExpression().getRawSignature();
					
					ASTVisitorFindMatch visitor = new ASTVisitorFindMatch(functionName, "STR38_Include");
					node.getTranslationUnit().accept(visitor);
					
					if(visitor.isStringFunction())
					{
						for(VariableNameTypePair btPair: Utility_C.allVarNameType(visitDec.getvarNamePairList(), node) )
						{
							if(btPair.getVarType().contains("wchar_t"))
							{
								ruleViolated = true;
								return ruleViolated;
							}
						}
						
					}
					else if(visitor.isWideStringFunction())
					{
						for(VariableNameTypePair btPair: Utility_C.allVarNameType(visitDec.getvarNamePairList(), node) )
						{
							if(!(btPair.getVarType().contains("wchar_t")) && (btPair.getVarType().contains("char")))
							{
								ruleViolated = true;
								return ruleViolated;
							}
						}
					}
				}
			}
		return ruleViolated;
	}

	
	@Override
	public String getRuleText() {
		
		return "CERT Website- Passing narrow string arguments to wide string functions "
				+ "or wide string arguments to narrow string functions can lead to "
				+ "unexpected and undefined behavior. Scaling problems are likely "
				+ "because of the difference in size between wide and narrow characters.";
	}

	@Override
	public String getRuleName() {

		return Globals.RuleNames.STR38_C;
	}

	@Override
	public String getRuleID() {

		return Globals.RuleID.STR38_C;
	}

	@Override
	public String getRuleRecommendation() {
		
		return "Use the proper width function for wide strings and strings.";
	}

	@Override
	public int securityLevel() {

		return Globals.Markers.SECURITY_LEVEL_HIGH;
	}

	@Override
	public String getRuleURL() {
		
		return "https://wiki.sei.cmu.edu/confluence/display/c/STR38-C.+Do+not+confuse+narrow+and+wide+character+strings+and+functions";
	}

	@Override
	public ITranslationUnit getITranslationUnit() {
		// TODO Auto-generated method stub
		return null;
	}

}
