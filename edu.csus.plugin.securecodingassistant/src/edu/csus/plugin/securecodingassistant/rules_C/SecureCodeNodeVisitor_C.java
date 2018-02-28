package edu.csus.plugin.securecodingassistant.rules_C;

import java.util.ArrayList;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTSwitchStatement;
import org.eclipse.cdt.core.dom.ast.ICompositeType;
import org.eclipse.cdt.core.model.ITranslationUnit;

//import edu.csus.plugin.securecodingassistant.compilation.InsecureCodeSegment;
import edu.csus.plugin.securecodingassistant.rules.IRule;
import edu.csus.plugin.securecodingassistant.*;
import edu.csus.plugin.securecodingassistant.Globals;
import edu.csus.plugin.securecodingassistant.markerresolution_C.InsecureCodeSegment_C;


public class SecureCodeNodeVisitor_C extends ASTVisitor{

	//private String rand = "rand()";
	//private MSC30C_DoNotUseRandFunctionForGeneratingPseudorandomNumbers MSC30C11 = new MSC30C_DoNotUseRandFunctionForGeneratingPseudorandomNumbers();
	
	private IASTNode node;
	private ArrayList<IRule_C> c_rules;
	private ITranslationUnit localITU;
	/**
	 * Collection of insecure code segments that have been detected
	 */
	private ArrayList<IASTNode> insecureNodes = new ArrayList<IASTNode>();
	private IASTNode testInsecureNode;
	
	public SecureCodeNodeVisitor_C(ArrayList<IRule_C> rules, ITranslationUnit ITU)
	{
		
		this.shouldVisitStatements = true;
		this.shouldVisitDeclarations = true;
		this.shouldVisitExpressions = true;
		this.shouldVisitAmbiguousNodes = true;
		this.shouldVisitStatements = true;
		
		
		c_rules = rules;
		localITU = ITU;
		
	}
	
	public int visit(IASTStatement stam)
	{
		node = stam.getOriginalNode();
		
		if(node.getContainingFilename().contains("DCL38C_UseCorrectSyntaxWhenDeclaringFlexibleArrayMember"))
		{
			//System.out.println("Inside Declaration: " + stam.getRawSignature());
			/*
			if(node instanceof IASTSwitchStatement)
			{
				System.out.println("\n\n\nNode is IASTSwitchStatement\n\n\n");
			}
			*/
		}
		
		traverseRule(node);
		
		return PROCESS_CONTINUE;
		
	}
	
	public int visit(IASTDeclaration dec)
	{
		node = dec.getOriginalNode();
		
		
		
		if(node.getContainingFilename().contains("DCL38C_UseCorrectSyntaxWhenDeclaringFlexibleArrayMember"))
		{
			//System.out.println("Inside Declaration: " + dec.getRawSignature());
			if(node instanceof ICompositeType)
			{
				System.out.println("DCL38C ICOmposite: " + node.getRawSignature());
			}
			if(node instanceof IASTSwitchStatement)
			{
				//System.out.println("\n\n\nNode is IASTSwitchStatement\n\n\n");
			}
		}
		
		
		traverseRule(node);
		
		//continues traversing the astTree
		return PROCESS_CONTINUE;
	}
	
	
	public int visit(IASTExpression fce)
	{
		//System.out.println("Inside IASTExpression");
		
		//System.out.println("Inside IASTExpression/IASTFunctionCallExpression: " + fce.getRawSignature());
		node = fce.getOriginalNode();
		
		
		if(node.getContainingFilename().contains("DCL38C_UseCorrectSyntaxWhenDeclaringFlexibleArrayMember"))
		{
			//System.out.println("Inside IASTExpression: " + fce.getRawSignature());
			
			/*
			if(node.getRawSignature().contains("fopen"))
			{
				if(node instanceof IASTFunctionCallExpression)
				{
					System.out.println("Node is FunctionCallExpression");
				}
				
				for(IASTNode nodeChild: node.getChildren())
				{
					System.out.println("NodeChildren: " + nodeChild.getRawSignature());
					if(nodeChild instanceof IASTStatement)
					{
						System.out.println("NodeChild is statement");
					}
					
					if(nodeChild instanceof IASTFunctionCallExpression)
					{
						System.out.println("NodeChild is FunctionCallExpression");
					}
				}
				System.out.println("End of parent\n\n");
			}
			
			*/
		}
		
		
		
		traverseRule(node);
		//continues traversing the astTree
		return PROCESS_CONTINUE;
	}
	
	
	
	public void traverseRule(IASTNode checkNode)
	{
		for (IRule_C rule : c_rules)
		{
			
			if(rule.violate_CDT(checkNode))
			{
				//System.out.println("RuleViolated: " + b1);
				//insecureNodes.add(node);
				//testInsecureNode = node;
				
				Globals.insecureGlobalNode = checkNode;
				
				Globals.cdt_InsecureCodeSegments.add(new InsecureCodeSegment_C(checkNode,rule, localITU));
				
				/*
				System.out.println("Original IASTNode: " + node.getRawSignature());
				IASTFileLocation fl = node.getFileLocation();
			
				System.out.println("IASTFileLocation NodeOffset: " + fl.getNodeOffset());
				System.out.println("IASTFileLocation Nodelength: " + fl.getNodeLength());
				System.out.println("IASTFileLocation StartingLineNumber: " + fl.getStartingLineNumber());
				System.out.println("IASTFileLocation EndingLineNumber: " + fl.getEndingLineNumber());
			
				System.out.println("containingFilename " + node.getContainingFilename());
			
				//n1.getContainingFilename();
				System.out.println("\n\n ");
				*/
			}
		}
	}
}
