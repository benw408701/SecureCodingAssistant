package edu.csus.plugin.securecodingassistant.rules_C;

import org.eclipse.cdt.core.dom.ast.ExpansionOverlapsBoundaryException;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.model.ITranslationUnit;

import edu.csus.plugin.securecodingassistant.Globals;

public class SIG31C_DoNotAccessSharedObjectsInSignalHandlers implements IRule_C {

	private boolean ruleViolated;
	private IASTNode[] children;
	private boolean unsafe;
	private String unsafeVar;
	@Override
	public boolean violate_CDT(IASTNode node) {
		
		ruleViolated = false;
		unsafe = false;
		unsafeVar = null;
		
		//if(node.getContainingFilename().contains("SIG31C"))
		//{
			if(node instanceof IASTFunctionDefinition && node.getRawSignature().startsWith("void handler"))
			{
				//System.out.println("node: " + node);
				//System.out.println("node: " + node.getRawSignature());
				children = node.getChildren();
				
				/*
				for(IASTNode o: children)
				{
					System.out.println("nodeChild: " + o);
					System.out.println("nodeChild: " + o.getRawSignature());
					System.out.println("ParentProperty" + o.getPropertyInParent());
				}
				*/
				
				IASTTranslationUnit ITU = node.getTranslationUnit();
			
				
				ASTVisitorFindMatch visitor = new ASTVisitorFindMatch(null, "SIG31C");
				ITU.accept(visitor);
				
				for(IASTNode oo: visitor.listofDeclarations())//traverse through list of declarations
				{
					unsafe = false;
					if(oo == null)
					{
						//System.out.println("null");
						break;
				
					}
					else
					{
						
						//System.out.println("Declarations: " + oo.getRawSignature());
						try {
							//System.out.println("oo:: " + oo.getSyntax().toString());
							if(!(oo.getSyntax().toString().contentEquals("atomic_int")))
							{
								//System.out.println("DeclarationsIF: " + oo.getRawSignature());
								if(oo.getSyntax().toString().contentEquals("volatile"))
								{
									if(oo.getRawSignature().contains("sig_atomic_t"))
									{
										//System.out.println("DeclarationsELSEEEEEEEE121212: " + oo.getRawSignature());
									}
									else
									{
										System.out.println("UNSAFEAssign: " + oo.getRawSignature());
										unsafe = true;
									}
								}
								else
								{
									//System.out.println("DeclarationsIF/Else: " + oo.getRawSignature());
									//System.out.println("UNSAFEAssign: " + oo.getRawSignature());
									unsafe = true;
								}
								
								
							}
							else
							{
								//System.out.println("DeclarationsELSEEEEEEEE: " + oo.getRawSignature());
								
							}
						} catch (ExpansionOverlapsBoundaryException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						if(unsafe)
						{
							//System.out.println("Unasfe: " + oo.getRawSignature());
							if(oo.getChildren().length > 1)
							{
								//System.out.println("Unasfe: " + oo.getRawSignature());
								//System.out.println("Chid1: " + oo.getChildren()[1].getRawSignature());
								//System.out.println("UnasfeParentProperty: " + oo.getPropertyInParent());
								
								if(oo.getChildren()[1].getRawSignature().contains("=") ||oo.getChildren()[1].getRawSignature().contains("["))
								{
									//System.out.println("ChidChild1: " + oo.getChildren()[1].getChildren()[0].getRawSignature());
									unsafeVar = oo.getChildren()[1].getChildren()[0].getRawSignature();
								}
								else if(oo.getChildren()[1].getRawSignature().startsWith("*") && !(oo.getChildren()[1].getRawSignature().startsWith("**")))
								{
									//System.out.println("ChidChild1*: " + oo.getChildren()[1].getChildren()[1].getRawSignature());
									unsafeVar = oo.getChildren()[1].getChildren()[1].getRawSignature();
								}
								else if(oo.getChildren()[1].getRawSignature().startsWith("**"))
								{
									//System.out.println("ChidChild1**: " + oo.getChildren()[1].getChildren()[2].getRawSignature());
									unsafeVar = oo.getChildren()[1].getChildren()[2].getRawSignature();
								}
								else
								{
									//System.out.println("Chid1: " + oo.getChildren()[1].getRawSignature());
									unsafeVar = oo.getChildren()[1].getRawSignature();
								}
								
								//System.out.println("unsafeVar: " + unsafeVar);
								
																
								
								ASTVisitorFindMatch visitor1 = new ASTVisitorFindMatch(unsafeVar, "SIG31C");
								node.accept(visitor1);
								
								if(visitor1.isMatch())
								{
									ruleViolated = true;
								}
							}
							//System.out.println("Chid1: " + oo.getChildren()[0].getRawSignature());
							//System.out.println("Chidlen: " + oo.getChildren().length);
							
							
						}
						
					}
				}
				
				//makeVisitor that returns all declarations in the AST
				//check what variable is inside the handler
				//check if the type is "volatile sig_atomic_t" or "atomic_int"
				
			}
		//}
		
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
