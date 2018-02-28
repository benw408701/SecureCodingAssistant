package edu.csus.plugin.securecodingassistant.rules_C;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.model.ITranslationUnit;

public class TEST_RULE implements IRule_C {

	private boolean once = false;
	
	private ArrayList<NodeNumPair_C> assignmentStatements;
	private ArrayList<NodeNumPair_C> functionCalls;
	private ArrayList<NodeNumPair_C> variableDeclarations;
	private ArrayList<NodeNumPair_C> functionDefintions;
	
	private Iterator<NodeNumPair_C> IT;
	
	@Override
	public boolean violate_CDT(IASTNode node) {
		
		/*
		if(node.getContainingFilename().contains("TESTASTPROCESSOR"))
		{
			
			
			if(once == false)
			{
				System.out.println("1STnode:" + node.getContainingFilename());
				ASTNodeProcessor_C visitor = new ASTNodeProcessor_C();
				node.getTranslationUnit().accept(visitor);
				once = true;
				
				assignmentStatements = visitor.getAssignmentStatements();
				functionCalls = visitor.getFunctionCalls();
				variableDeclarations = visitor.getVariableDeclarations();
				functionDefintions = visitor.getFunctionDefinitions();
				
				//
				IT = assignmentStatements.iterator();
				
				System.out.println("******************************************************************************************"
						+ "\nassignmentStatements::\n");
				while(IT.hasNext())
				{
					NodeNumPair_C temp = IT.next();
					System.out.println("Node: " + temp.getNode().getRawSignature());
					System.out.println("Num: " + temp.getNum() + "\n");
				}
				
				//
				IT = functionCalls.iterator();
				
				System.out.println("******************************************************************************************"
						+ "\functionCalls::\n");
				while(IT.hasNext())
				{
					NodeNumPair_C temp = IT.next();
					System.out.println("Node: " + temp.getNode().getRawSignature());
					System.out.println("Num: " + temp.getNum() + "\n");
				}
				
				
				//
				IT = variableDeclarations.iterator();
				
				System.out.println("******************************************************************************************"
						+ "\nvariableDeclarations::\n");
				while(IT.hasNext())
				{
					NodeNumPair_C temp = IT.next();
					System.out.println("Node: " + temp.getNode().getRawSignature());
					System.out.println("Num: " + temp.getNum() + "\n");
				}
				
				
				//
				IT = functionDefintions.iterator();
				
				System.out.println("******************************************************************************************"
						+ "\nfunctionDefintions::\n");
				while(IT.hasNext())
				{
					NodeNumPair_C temp = IT.next();
					System.out.println("Node: " + temp.getNode().getRawSignature());
					System.out.println("Num: " + temp.getNum() + "\n");
				
				}
			}
			
		}
		*/
		return false;
	}

	@Override
	public String getRuleText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRuleName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRuleID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRuleRecommendation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int securityLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getRuleURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITranslationUnit getITranslationUnit() {
		// TODO Auto-generated method stub
		return null;
	}

}
