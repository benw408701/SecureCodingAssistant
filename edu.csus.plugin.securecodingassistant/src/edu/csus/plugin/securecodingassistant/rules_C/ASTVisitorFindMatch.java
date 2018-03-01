package edu.csus.plugin.securecodingassistant.rules_C;

import java.util.ArrayList;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.ExpansionOverlapsBoundaryException;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTSwitchStatement;
import org.eclipse.cdt.core.dom.ast.ICompositeType;
import org.eclipse.cdt.core.model.ITranslationUnit;

//import edu.csus.plugin.securecodingassistant.compilation.InsecureCodeSegment;
import edu.csus.plugin.securecodingassistant.rules.IRule;
import edu.csus.plugin.securecodingassistant.*;
import edu.csus.plugin.securecodingassistant.Globals;
import edu.csus.plugin.securecodingassistant.markerresolution_C.InsecureCodeSegment_C;


public class ASTVisitorFindMatch extends ASTVisitor{

	
	private IASTNode parentMatchFound = null;
	private boolean matchFound = false;
	private IASTNode node;
	private String findKeyWord;
	private String findRuleName;
	
	private IASTNode[] listOfDeclarations;
	private int listOfDeclarationsCounter;
		
	//CON40C
	private ArrayList<IASTNode> arrListOfDec = new ArrayList<IASTNode>();
	private ArrayList<IASTNode> arrListOfFuncCalls = new ArrayList<IASTNode>();
	
	private ArrayList<String> arrListOfHandlerNames = new ArrayList<String>();
	private int dupInExpression;
	private boolean duplicateInExpression;
	
	private boolean isStringFunc = false;
	private boolean isWideStringFunc = false;
	
	public ASTVisitorFindMatch(String keyWord, String ruleName)
	{
		
		this.shouldVisitStatements = true;
		this.shouldVisitDeclarations = true;
		this.shouldVisitExpressions = true;
		this.shouldVisitAmbiguousNodes = true;
		this.shouldVisitStatements = true;
		this.shouldVisitParameterDeclarations = true;
		findKeyWord = keyWord;
		findRuleName = ruleName;
		matchFound = false;		
		listOfDeclarations = new IASTNode[1000];
		listOfDeclarationsCounter = 0;
		parentMatchFound = null;
		
		arrListOfDec.clear();
		arrListOfHandlerNames.clear();
		//CONC40_C
		dupInExpression = 0;
		duplicateInExpression = false;
		
		//STR_38C
		isStringFunc = false;
		isWideStringFunc = false;
		
	}
	
	public int visit(IASTStatement stam)
	{
		node = stam.getOriginalNode();
		
		
		
		return PROCESS_CONTINUE;
			
		
	}
	
	/**
	 * Visits all IASTStatement nodes
	 */
	public int visit(IASTParameterDeclaration param)
	{
		
		node = param.getOriginalNode();
		
		if(findRuleName.contentEquals("FindMatch") && (node.getFileLocation().getContextInclusionStatement() == null))
		{
			if(node.getRawSignature().contentEquals(findKeyWord))
			{
				matchFound = true;
			
				return PROCESS_ABORT;
			}
		}
		
		return PROCESS_CONTINUE;
	}
	
	public int visit(IASTDeclaration dec)
	{
		node = dec.getOriginalNode();
		
		
		if(findRuleName.contentEquals("SIG31C") && !(dec instanceof IASTFunctionDeclarator) && !(dec instanceof IASTFunctionDefinition) && (findKeyWord == null))
		{
			//System.out.println("decASTVisitor: " + dec.getRawSignature());
			//System.out.println("ParentRAW: " + node.getParent().getRawSignature());
			//System.out.println("ParentFilename: " + node.getParent().getContainingFilename());
			
			if(node.getFileLocation().getContextInclusionStatement() == null)
			{
				//System.out.println("decASTVisitor: " + dec.getRawSignature());
				//System.out.println("PARENTProperty: " + dec.getPropertyInParent());
				listOfDeclarations[listOfDeclarationsCounter] = node;
				listOfDeclarationsCounter++;
			}
			
			//System.out.println("ContextInclusion " + node.getFileLocation().getContextInclusionStatement().getRawSignature() + "\n");
			
			//System.out.println("PropertyInParent " + node.getPropertyInParent().getName() + "\n");
			//if(node.getChildren()[0])
			//listOfDeclaration[decCounter] = node;
			//decCounter++;
		}
		
		//find all declarations with the keyword
		if(findRuleName.contentEquals("CON40C_FindDec") && !(dec instanceof IASTFunctionDeclarator) && !(dec instanceof IASTFunctionDefinition)
				&& !(dec.getRawSignature().endsWith(")")) && (node.getFileLocation().getContextInclusionStatement() == null))
		{
			//System.out.println("decASTVisitor: " + dec.getRawSignature());
			//System.out.println("ParentRAW: " + node.getParent().getRawSignature());
			//System.out.println("ParentFilename: " + node.getParent().getContainingFilename());
			
			if((node.getFileLocation().getContextInclusionStatement() == null) && node.getChildren().length > 1)
			{
				//System.out.println("decASTVisitor: " + dec.getRawSignature());
				//System.out.println("PARENTProperty: " + dec.getPropertyInParent());
				arrListOfDec.add(dec);
			}
			
			//System.out.println("ContextInclusion " + node.getFileLocation().getContextInclusionStatement().getRawSignature() + "\n");
			
			//System.out.println("PropertyInParent " + node.getPropertyInParent().getName() + "\n");
			//if(node.getChildren()[0])
			//listOfDeclaration[decCounter] = node;
			//decCounter++;
		}
		
		if(findRuleName.contentEquals("CON40C_assignedAtomic") && (node.getFileLocation().getContextInclusionStatement() == null)
				&& !(dec.getRawSignature().endsWith(")")) && (node.getChildren().length > 1))
		{
			
			if((node.getChildren()[1].getRawSignature().startsWith(findKeyWord)) && (node.getRawSignature().contains("=")) && (node.getRawSignature().contains("atomic_")))
			{
				//System.out.println("WE are here");
				matchFound = true;
				return PROCESS_ABORT;
			}
		}
		/*
		if(node.getRawSignature().matches(findKeyWord))
		{
			parentMatchFound = node.getParent();
			matchFound = true;
			System.out.println("Match Found\n");
			System.out.println("Parent Node: " + parentMatchFound.getRawSignature());
			return PROCESS_ABORT;
		}
		else
		{
			return PROCESS_CONTINUE;
		}
		*/
		if(findRuleName.contentEquals("EXP34C") && (node.getFileLocation().getContextInclusionStatement() == null))
		{
			/*
			if(node instanceof IASTFunctionDefinition)
			{
				System.out.println("node IASTFunctionDefinition: " + node.getRawSignature());
			}
			*/
			if(node.getRawSignature().startsWith(findKeyWord))
			{
				//System.out.println("Match is true");
				//System.out.println("node: " + node.getRawSignature() + "\n");
				matchFound = true;
				return PROCESS_ABORT;
			}
		}
		
		/**
		 * Check to see if function is from <string.h> or <wchar.h> library
		 */
		if(findRuleName.contentEquals("STR38_Include"))
		{
			//System.out.println("node: " + node.getRawSignature());
			
			if(node.getFileLocation().getContextInclusionStatement() != null)
			{
				if(node.getFileLocation().getContextInclusionStatement().getRawSignature().contentEquals("#include <string.h>") 
						&& (node.getRawSignature().contains(findKeyWord)))
				{
					//System.out.println("node: " + node.getRawSignature());
					//System.out.println("IncludeFile: " + node.getFileLocation().getContextInclusionStatement().getRawSignature() + "\n");
					isStringFunc = true;
					return PROCESS_ABORT;
				}
				else if(node.getFileLocation().getContextInclusionStatement().getRawSignature().contentEquals("#include <wchar.h>")
						&& (node.getRawSignature().contains(findKeyWord)))
				{
					//System.out.println("node: " + node.getRawSignature());
					//System.out.println("IncludeFile: " + node.getFileLocation().getContextInclusionStatement().getRawSignature() + "\n");
					isWideStringFunc = true;
					return PROCESS_ABORT;
				}
			}
			
		}
		
		if((findRuleName.contentEquals("UserDefinedFunctions")) && (node.getFileLocation().getContextInclusionStatement() == null) )
		{
			if(node instanceof IASTFunctionDefinition && node instanceof IASTDeclaration)
			{
				//System.out.println("IASTFunctionDefinition" + node.getRawSignature());
				arrListOfDec.add(node);
			}
		}
		
	
		
		return PROCESS_CONTINUE;
	}
	
	
	public int visit(IASTExpression fce)
	{
		node = fce.getOriginalNode();
		
		if(findRuleName.contentEquals("SIG31C") && (findKeyWord != null) && (node.getFileLocation().getContextInclusionStatement() == null) )
		{
					//System.out.println("node: " + node.getRawSignature());
					if(node.getRawSignature().contentEquals(findKeyWord))
					{
						//System.out.println("nodeMATCHFOUND: " + node.getRawSignature());
						matchFound = true;
						return PROCESS_ABORT;
					}
		}
		
		
		if((findRuleName.contentEquals("findDup")))
		{
			if(node.getRawSignature().contentEquals(findKeyWord))
			{
				dupInExpression++;
				
				if(dupInExpression > 1)
				{
					duplicateInExpression = true;
				}
			}
		}
		
		if(findRuleName.contentEquals("EXP34C") && (node.getFileLocation().getContextInclusionStatement() == null))
		{
			//System.out.println("node: " + node.getRawSignature());
			if(node.getRawSignature().startsWith(findKeyWord))
			{
				matchFound = true;
				//System.out.println("Match is true");
				//System.out.println("node: " + node.getRawSignature() + "\n");
				return PROCESS_ABORT;
			}
		}
		
		if(findRuleName.contentEquals("FindMatch") && (node.getFileLocation().getContextInclusionStatement() == null))
		{
			//System.out.println("node: " + node.getRawSignature());
			//System.out.println("AM I HERE?");
			if(node.getRawSignature().contentEquals(findKeyWord))
			{
				matchFound = true;
				//System.out.println("Match is true");
				//System.out.println("node: " + node.getRawSignature() + "\n");
				return PROCESS_ABORT;
			}
		}
		
		//MEM31_C Rule
		if((findRuleName.contentEquals("MEM31C_Free")) && (node.getFileLocation().getContextInclusionStatement() == null))
		{
			//System.out.println("MEM31C_Free_Expression: " + node.getRawSignature());
			if(node instanceof IASTFunctionCallExpression)
			{
				if(node.getRawSignature().startsWith("free"))
				{
					//System.out.println("MEM31C_Free_Expression: " + node.getRawSignature());
					//System.out.println("Found MACTH!!!!");
					parentMatchFound = node;
					arrListOfFuncCalls.add(node);
					//return PROCESS_ABORT; //uncommented
				}
			}
		}
		
		//SIG30_C Rule
		if((findRuleName.contentEquals("GetFunctions")) && (node.getFileLocation().getContextInclusionStatement() == null))
		{
			//System.out.println("MEM31C_Free_Expression: " + node.getRawSignature());
			if(findKeyWord != null)
			{
				if(findKeyWord.contentEquals("handler"))
				{
					if((node instanceof IASTFunctionCallExpression) )
					{
						//System.out.println("Inside GETFUNCTIONS");
						if(((IASTFunctionCallExpression) node).getFunctionNameExpression().getRawSignature().contentEquals("signal"))
						{
							//System.out.println("Func NAME: " + ((IASTFunctionCallExpression) node).getFunctionNameExpression().getRawSignature());
						
							//System.out.println("Func Arg2: " + ((IASTFunctionCallExpression) node).getArguments()[1].getRawSignature());
						
							arrListOfHandlerNames.add(((IASTFunctionCallExpression) node).getArguments()[1].getRawSignature());
							//((IASTFunctionCallExpression) node).getArguments()[1]
						}
					}
				}
			}
			else
			{
				if(node instanceof IASTFunctionCallExpression)
				{
						parentMatchFound = node;
						arrListOfFuncCalls.add(node);
				}
			}
		}
		
		return PROCESS_CONTINUE;
	}
	
	public int visit(IASTName name) 
	{
		node = name.getOriginalNode();
		if(findRuleName.contentEquals("FindMatch") && (node.getFileLocation().getContextInclusionStatement() == null))
		{
			//System.out.println("node: " + node.getRawSignature());
			//System.out.println("AM I HERE?");
			if(node.getRawSignature().contentEquals(findKeyWord))
			{
				matchFound = true;
				//System.out.println("Match is true");
				//System.out.println("node: " + node.getRawSignature() + "\n");
				return PROCESS_ABORT;
			}
		}
		return PROCESS_CONTINUE;
	}
	
	
	public boolean isMatch()
	{
		return matchFound;
	}
	
	public boolean isDuplicateInExpression()
	{
		return duplicateInExpression;
	}
	
	public IASTNode nodeParent()
	{
		return parentMatchFound;
	}
	
	public IASTNode[] listofDeclarations()
	{
		return listOfDeclarations;
	}
	
	public ArrayList<IASTNode> arrayListofDeclaration()
	{
		return arrListOfDec;
	}
	
	//STR38 Rule
	
	public boolean isStringFunction()
	{
		return isStringFunc;
	}
	
	public boolean isWideStringFunction()
	{
		return isWideStringFunc;
	}
	
	//MEM31 Rule
	public ArrayList<IASTNode> arrayListofFunctions()
	{
		return arrListOfFuncCalls;
	}
	
	public ArrayList<String> arrayListofHandlerNames()
	{
		return arrListOfHandlerNames;
	}
}
