package edu.csus.plugin.securecodingassistant.rules_C;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.model.ITranslationUnit;

public class EXP40C_DoNotModifyConstObjects implements IRule_C {

	boolean ruleViolated;
	private IASTNode parent;
	private IASTNode[] childrenITranslationUnit;
	private IASTNode[] nodeChildren;
	private int nodeChildrenLength;
	private String constantVarName;
	
	private IASTNode[] ITUChildrenLoop;
	
	@Override
	public boolean violate_CDT(IASTNode node) {
		// TODO Auto-generated method stub
		ruleViolated = false;
		
		
		if(node.getContainingFilename().contains("EXP40C_DoNotModifyConstObjects"))
		{
			//System.out.println("InsideEXP40C: "+ node.getRawSignature());
			
			/*
			if((node instanceof IASTExpressionStatement) && node.getRawSignature().contains("="))
			{
				System.out.println("IASTExpressionStatement: " + node.getRawSignature());
				nodeChildren = node.getChildren();
				nodeChildren = nodeChildren[0].getChildren();
				
				for(IASTNode o: nodeChildren)
				{
					System.out.println("nodeChildren: " + o.getRawSignature());
				}
			}
			*/
			//return false;
			/*
			if(node instanceof IASTDeclaration)
			{
				if(node.getRawSignature().startsWith("const"))
				{	
					System.out.println("InsideEXP40C_DEC: "+ node.getRawSignature());
					
					nodeChildren = node.getChildren();
					nodeChildrenLength = nodeChildren.length;
					//System.out.println("nodeChildren[nodeChildrenLength - 1]: "+ nodeChildren[nodeChildrenLength - 1].getRawSignature());
					//System.out.println("nodeChildrenLength]: "+ nodeChildrenLength);
					
					if(nodeChildren[nodeChildrenLength - 1].getRawSignature().contains("="))
					{
						nodeChildren = nodeChildren[nodeChildrenLength - 1].getChildren();
						nodeChildrenLength = nodeChildren.length;
						constantVarName = nodeChildren[0].getRawSignature();
						
						System.out.println("nodeChildren[0]: "+ nodeChildren[0].getRawSignature());
						System.out.println("nodeChildrenLength]: "+ nodeChildrenLength);
						System.out.println("\n");
					}
					else
					{
						nodeChildren = nodeChildren[nodeChildrenLength - 1].getChildren();
						nodeChildrenLength = nodeChildren.length;
						constantVarName = nodeChildren[nodeChildrenLength - 1].getRawSignature();
						System.out.println("constantVarname: " + constantVarName);
						System.out.println("nodeChildren[nodeChildrenLength - 1]: "+ nodeChildren[nodeChildrenLength - 1].getRawSignature());
						System.out.println("nodeChildrenLength]: "+ nodeChildrenLength);
						IASTNode childChild[] = nodeChildren[nodeChildrenLength - 1].getChildren();
						System.out.println("ChildrenChildrenLength: "+ childChild.length); //left off here. Child of leafNode is null
						
					}
					//System.out.println("nodeChildren[2]: "+ nodeChildren[2].getRawSignature());
					//System.out.println("nodeChildren[3]: "+ nodeChildren[3].getRawSignature());
					//System.out.println("nodeChildren[4]: "+ nodeChildren[4].getRawSignature());
					
					
					parent = node.getTranslationUnit();
					//System.out.println("InsideEXP40C_DEC Parent: "+ parent.getRawSignature());
					
					
					//System.out.println("InsideEXP40C_DEC Parent: "+ parent.getRawSignature());
					childrenITranslationUnit = parent.getChildren();
						
					for(IASTNode o: childrenITranslationUnit)
					{
							if(!o.equals(node))
							{
								ASTVisitorFindMatch visitor = new ASTVisitorFindMatch(constantVarName);
								o.accept(visitor);
								
								//System.out.println("o !equals node: " + o.getRawSignature());
								//System.out.println("\n");
								
							}
							
							//ITUChildrenLoop = o.getChildren();
							
							//for(IASTNode jj: ITUChildrenLoop)
							//{
								
							//}
					}
					
				}
			}
			*/
		}
		return ruleViolated;
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
