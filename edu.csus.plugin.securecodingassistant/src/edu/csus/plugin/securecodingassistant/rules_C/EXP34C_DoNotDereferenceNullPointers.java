package edu.csus.plugin.securecodingassistant.rules_C;

import java.util.TreeMap;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.csus.plugin.securecodingassistant.Globals;

public class EXP34C_DoNotDereferenceNullPointers extends SecureCodingRule_C {

	private boolean ruleViolated;
	private String varInQuestion;
	private String[] nullStatementAppend = {"NULL ==", "NULL==", "NULL == ", "NULL== ", "null ==", "null==", "null == ", "null== "};
	private String[] nullStatementAppendCONST = {"NULL ==", "NULL==", "NULL == ", "NULL== ", "null ==", "null==", "null == ", "null== "};
	
	private String[] nullStatementPrepend = { "== NULL", "==NULL", " == NULL", " ==NULL", "== null", "==null", " == null", " ==null"};
	private String[] nullStatementPrependCONST = { "== NULL", "==NULL", " == NULL", " ==NULL", "== null", "==null", " == null", " ==null"};
	
	private IASTNode parentFunction;
	@Override
	public boolean violate_CDT(IASTNode node) {
		
		ruleViolated = false;
		varInQuestion = null;
		parentFunction = node;
		
		int counter = 0;
		//if(node.getContainingFilename().contains("EXP34C"))
		//{
			if((node.getRawSignature().contains("malloc(")) || (node.getRawSignature().contains("calloc(")) || (node.getRawSignature().contains("realloc("))
					|| (node.getRawSignature().contains("strchr(")))
			{
				
				if((node instanceof IASTDeclaration)  && !(node instanceof IASTFunctionDeclarator) && !(node instanceof IASTFunctionDefinition)
						&& (node.getChildren().length > 1))
				{
					//System.out.println("node IASTDeclaration: " + node.getRawSignature()+ "\n");
					
					//System.out.println("nodechildren0: " + node.getChildren()[1].getChildren()[1].getRawSignature());
					varInQuestion = node.getChildren()[1].getChildren()[1].getRawSignature();
					//System.out.println("varInQuestion: " + varInQuestion);
					
					while(counter < nullStatementAppend.length)
					{
						nullStatementAppend[counter] = nullStatementAppend[counter] + varInQuestion;
						nullStatementPrepend[counter] = varInQuestion + nullStatementPrepend[counter];
						counter++;
					}
					counter = 0;
					
					//IASTTranslationUnit ITU = node.getTranslationUnit();
					
					while(!(parentFunction instanceof IASTFunctionDefinition))
					{
						parentFunction = parentFunction.getParent();
					}
					
					//System.out.println("parentFunction" + parentFunction.getRawSignature());
					
					while(counter < nullStatementAppend.length)
					{
						//System.out.println(nullStatementAppend[counter]);
						//System.out.println(nullStatementPrepend[counter]);
						
						ASTVisitorFindMatch visitor = new ASTVisitorFindMatch(nullStatementAppend[counter],"EXP34C");
						ASTVisitorFindMatch visitor1 = new ASTVisitorFindMatch(nullStatementPrepend[counter],"EXP34C");
						
						parentFunction.accept(visitor);
						parentFunction.accept(visitor1);
						
						if(visitor.isMatch() || visitor1.isMatch())
						{
							ruleViolated = false;
							break;
						}
						else
						{
							ruleViolated = true;
							//break;
						}
						
						
						counter++;
					}
					
				}
				else if((node instanceof IASTExpression) && node.getRawSignature().contains("="))
				{
					//System.out.println("node: IASTExpression " + node.getRawSignature()+ "\n");
					//System.out.println("nodechildren0: " + node.getChildren()[0].getRawSignature());
					varInQuestion = node.getChildren()[0].getRawSignature();
					//System.out.println("varInQuestion: " + varInQuestion);
					
					while(counter < nullStatementAppend.length)
					{
						nullStatementAppend[counter] = nullStatementAppend[counter] + varInQuestion;
						nullStatementPrepend[counter] = varInQuestion + nullStatementPrepend[counter];
						counter++;
					}
					counter = 0;
					
					while(!(parentFunction instanceof IASTFunctionDefinition))
					{
						parentFunction = parentFunction.getParent();
					}
					
					//System.out.println("parentFunction" + parentFunction.getRawSignature());
					//IASTTranslationUnit ITU = node.getTranslationUnit();
					while(counter < nullStatementAppend.length)
					{
						//System.out.println(nullStatementAppend[counter]);
						//System.out.println(nullStatementPrepend[counter]);
						ASTVisitorFindMatch visitor = new ASTVisitorFindMatch(nullStatementAppend[counter],"EXP34C");
						ASTVisitorFindMatch visitor1 = new ASTVisitorFindMatch(nullStatementPrepend[counter],"EXP34C");
						
						parentFunction.accept(visitor);
						parentFunction.accept(visitor1);
						
						if(visitor.isMatch() || visitor1.isMatch())
						{
							ruleViolated = false;
							break;
						}
						else
						{
							ruleViolated = true;
							//break;
						}
						
						
						counter++;
					}
				}
				
				//System.out.println("nodeParent: " + node.getPropertyInParent().toString());
				
				//restore arrays
				counter = 0;
				while(counter < nullStatementAppendCONST.length)
				{
					nullStatementAppend[counter] = nullStatementAppendCONST[counter];
					nullStatementPrepend[counter] = nullStatementPrependCONST[counter];
					counter++;
				}
			}
		//}
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		return "CERT Website- Dereferencing a null pointer is undefined behavior. " 
				+"On many platforms, dereferencing a null pointer results in "
				+ "abnormal program termination, but this is not required by the "
				+ "standard.";
	}

	@Override
	public String getRuleName() {
		return Globals.RuleNames.EXP34_C;
	}

	@Override
	public String getRuleID() {
		return Globals.RuleID.EXP34_C;
	}

	@Override
	public String getRuleRecommendation() {
		//TODO
		return "Check if result of malloc(), calloc(), or realloc() is NULL before use";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_HIGH;
	}

	@Override
	public String getRuleURL() {
		
		return "https://wiki.sei.cmu.edu/confluence/display/c/EXP34-C.+Do+not+dereference+null+pointers";
	}

	

}
