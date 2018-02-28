package edu.csus.plugin.securecodingassistant.rules_C;


/**
 * A custom <code>ASTVisitor</code> that is used by rules to parse an abstract syntax tree.
 * This can be used to get a list of methods that are in the same block as another for instance.
 * @author Ben White
 * @see org.eclipse.jdt.core.dom.ASTNode
 * @see IRule
 */
import java.util.ArrayList;


import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTCaseStatement;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStandardFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTStatement;

//import edu.csus.plugin.securecodingassistant.rules.NodeNumPair;

import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTExpression;

/**
 * A custom <code>ASTVisitor</code> that is used by rules to parse an abstract syntax tree.
 * This can be used to get a list of methods that are in the same block as another for instance.
 * @author Victor Melnik
 * @see org.eclipse.cdt.core.dom.ast.ASTVisitor
 * @see IRule_C
 */


public class ASTNodeProcessor_C extends ASTVisitor{
	
	/**
	 * A list of all the variableDeclarations within AST
	 * //IASTDeclaration "Declared variables" -> !(dec instanceof IASTFunctionDeclarator) && !(dec instanceof IASTFunctionDefinition)
	 */
	private ArrayList<NodeNumPair_C> variableDeclarations;
	
	/**
	 * A list of all the UserDefined functions within AST
	 */
	private ArrayList<NodeNumPair_C> functionDefintions;
	
	/**
	 * A list of all IASTFunctionCallExpression. (Function Calls)
	 * 
	 */
	private ArrayList<NodeNumPair_C> functionCalls;
	
	/**
	 * A list of all Assignement Statements
	 * 
	 */
	private ArrayList<NodeNumPair_C> assignmentStatements;
	
	
	/**
	 * List of all Case Staments within Switch Statement
	 * 
	 */
	private ArrayList<NodeNumPair_C> caseStatement;
	
	/**
	 * List of all if Staments 
	 * 
	 */
	private ArrayList<NodeNumPair_C> ifStatement;
	
	/**
	 * List of all Binary Expressions
	 * 
	 */
	private ArrayList<NodeNumPair_C> binaryExpression;
	
	
	/**
	 * List of VariableNameTypePair
	 * 
	 * 
	 */
	
	private ArrayList<VariableNameTypePair> varNamePairList;
	
	/**
	 * Counts the number of nodes visited
	 */
	private int m_nodeCounter;
	
	/**
	 * ASTNodeProcessor_C constructor
	 */
	
	private IASTNode node;
	
	public ASTNodeProcessor_C() {
		//super();
		this.shouldVisitStatements = true;
		this.shouldVisitDeclarations = true;
		this.shouldVisitExpressions = true;
		this.shouldVisitAmbiguousNodes = true;
		
		variableDeclarations = new ArrayList<NodeNumPair_C>();
		functionDefintions = new ArrayList<NodeNumPair_C>();
		functionCalls = new ArrayList<NodeNumPair_C>();
		assignmentStatements = new ArrayList<NodeNumPair_C>();
		caseStatement = new ArrayList<NodeNumPair_C>();
		ifStatement = new ArrayList<NodeNumPair_C>();
		binaryExpression = new ArrayList<NodeNumPair_C>();
		
		varNamePairList = new ArrayList<VariableNameTypePair>();
		
		m_nodeCounter = 0;
		
	}
	
	
	/**
	 * Visits all IASTStatement nodes
	 */
	public int visit(IASTStatement stam)
	{
		
		node = stam.getOriginalNode();
		
		if(node.getFileLocation().getContextInclusionStatement() == null)
		{
			if(node instanceof IASTCaseStatement)
			{
				caseStatement.add(new NodeNumPair_C(node, ++m_nodeCounter));
			}
			else if(node instanceof IASTIfStatement)
			{
				ifStatement.add(new NodeNumPair_C(node, ++m_nodeCounter));
			}
		}	
		
		return PROCESS_CONTINUE;
	}
	
	
	/**
	 * Visits all IASTDeclaration nodes
	 */
	public int visit(IASTDeclaration dec)
	{
		node = dec.getOriginalNode();
		
		if(node.getFileLocation().getContextInclusionStatement() == null)
		{
			if(node instanceof IASTFunctionDefinition)
			{
				functionDefintions.add(new NodeNumPair_C(node, ++m_nodeCounter));
			}
			else if(!(node instanceof IASTFunctionDeclarator) && node.getRawSignature().endsWith(";") && (node instanceof IASTSimpleDeclaration  ))
			{
				
				variableDeclarations.add(new NodeNumPair_C(node, ++m_nodeCounter));
				
				IASTDeclSpecifier decSpecifier = ((IASTSimpleDeclaration)node).getDeclSpecifier();
				String decSpec = decSpecifier.getRawSignature();
				
				for(IASTDeclarator o: ((IASTSimpleDeclaration)node).getDeclarators())
				{
					if(!(o instanceof IASTFunctionDeclarator))
					{
						if(!(decSpec.startsWith("struct")) && !(decSpec.startsWith("union")))
						{
							varNamePairList.add(new VariableNameTypePair(o,decSpecifier, node ));
						}
						
					}
					
				}
				
			}
		}
		
		return PROCESS_CONTINUE;
	}
	
	
	
	/**
	 * Visits all IASTExpression nodes
	 */
	public int visit(IASTExpression fce)
	{
		node = fce.getOriginalNode();
		if(node.getFileLocation().getContextInclusionStatement() == null)
		{
			
			
			if(node instanceof IASTFunctionCallExpression)
			{
				functionCalls.add(new NodeNumPair_C(node, ++m_nodeCounter));
			}
			else if(node instanceof IASTBinaryExpression)
			{
				if((((IASTBinaryExpression) node).getOperator() == 17) ||
						(((IASTBinaryExpression) node).getOperator() == 25) ||
						(((IASTBinaryExpression) node).getOperator() == 27) ||
						(((IASTBinaryExpression) node).getOperator() == 26) ||
						(((IASTBinaryExpression) node).getOperator() == 19) ||
						(((IASTBinaryExpression) node).getOperator() == 22) ||
						(((IASTBinaryExpression) node).getOperator() == 20) ||
						(((IASTBinaryExpression) node).getOperator() == 18) ||
						(((IASTBinaryExpression) node).getOperator() == 21) ||
						(((IASTBinaryExpression) node).getOperator() == 23) ||
						(((IASTBinaryExpression) node).getOperator() == 24)
						)
				{
					assignmentStatements.add(new NodeNumPair_C(node, ++m_nodeCounter));
				}
			}
			
			if(node instanceof IASTBinaryExpression)
			{
				binaryExpression.add(new NodeNumPair_C(node, ++m_nodeCounter));
			}
			
		}
		return PROCESS_CONTINUE;
	}
	
	/**
	 * Gets ArrayList of variable declarations
	 * @return ArrayList<NodeNumPair_C>
	 */
	public ArrayList<NodeNumPair_C> getVariableDeclarations()
	{
		return variableDeclarations;
	}
	
	/**
	 * Gets ArrayList of function definitions
	 * @return ArrayList<NodeNumPair_C>
	 */
	public ArrayList<NodeNumPair_C> getFunctionDefinitions()
	{
		return functionDefintions;
	}
	
	/**
	 * Gets ArrayList of function calls
	 * @return ArrayList<NodeNumPair_C>
	 */
	public ArrayList<NodeNumPair_C> getFunctionCalls()
	{
		return functionCalls;
	}
	
	/**
	 * Gets ArrayList of assignment statements
	 * @return ArrayList<NodeNumPair_C>
	 */
	public ArrayList<NodeNumPair_C> getAssignmentStatements()
	{
		return assignmentStatements;
	}
	
	/**
	 * Gets ArrayList of caseStatements
	 * @return ArrayList<NodeNumPair_C>
	 */
	public ArrayList<NodeNumPair_C> getCaseStatements()
	{
		return caseStatement;
	}
	
	
	/**
	 * Gets ArrayList of ifStatement
	 * @return ArrayList<NodeNumPair_C>
	 */
	public ArrayList<NodeNumPair_C> getIfStatements()
	{
		return ifStatement;
	}
	
	/**
	 * Gets ArrayList of binaryExpression
	 * @return ArrayList<NodeNumPair_C>
	 */
	public ArrayList<NodeNumPair_C> getBinaryExpressions()
	{
		return binaryExpression;
	}
	
	
	/**
	 * Returns total node count
	 * @return int
	 */
	public int getCounter()
	{
		return m_nodeCounter;
	}
	
	/**
	 * Get ArrayList of VariableNameTypePair
	 * 
	 * @return ArrayList<VariableNameTypePair>
	 */
	public ArrayList<VariableNameTypePair> getvarNamePairList()
	{
		return varNamePairList;
	}
	
}
