package edu.csus.plugin.securecodingassistant.rules_C;

import java.lang.reflect.Array;
import java.util.TreeMap;

import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.parser.util.HashTable;

import edu.csus.plugin.securecodingassistant.Globals;

public class FIO45C_AvoidTOCTOURaceConditionsWhileAccessingFiles extends SecureCodingRule_C {
	
	private String fileOpen = "fopen(";
	private boolean ruleViolated;
	private boolean isFuncParent;
	private IASTNode funcParent;
	
	private IASTNode[] funcParents; //collection of IASTFunctionCallExpression node's IASTFunctionDefintion
	private IASTNode[] funcCall; //collection of fopen function calls in the same IASTFunctionDefinition node
	private IASTNode[] nodeChildren; //children of IASTFunctionCallExpression
	private String[] fileNameNode; //name of the file being opened
	private String[] fileOpNode; //operation conducted on file
	 
	
	private int counter;
	
	
	
	public FIO45C_AvoidTOCTOURaceConditionsWhileAccessingFiles()
	{
		counter = 0;
		funcParents = new IASTNode[100];
		fileNameNode = new String[100];
		fileOpNode = new String[100];
		nodeChildren = new IASTNode[5];
		
	}
	
	@Override
	public boolean violate_CDT(IASTNode node) {
		
		ruleViolated = false;
		isFuncParent = false;
		if(node instanceof IASTFunctionCallExpression)
		{
			if(node.getRawSignature().contains(fileOpen))
			{
				//System.out.println("\nINSIDE FIO45C_AvoidTOCTOURaceConditionsWhileAccessingFiles " + "//Counter: " + counter);
				
				funcParent = node.getParent();
				while(!isFuncParent )
				{
					funcParent = funcParent.getParent();
					if(funcParent instanceof IASTFunctionDefinition)
					{
						//System.out.println("Node: " + node.getRawSignature()
						//		+ "  Parent:" + funcParent.getRawSignature());
						isFuncParent = true;
					}
				}
				
				nodeChildren = node.getChildren();//get nodes children
				//System.out.println("0:" +nodeChildren[0].getRawSignature());
				//System.out.println("1:" +nodeChildren[1].getRawSignature());
				//System.out.println("2:" +nodeChildren[2].getRawSignature());
				
				if(counter == 0)
				{
					funcParents[counter] = funcParent;
					fileNameNode[counter] = nodeChildren[1].getRawSignature();
					fileOpNode[counter] = nodeChildren[2].getRawSignature();
					counter++;
				}
				
				else
				{
					
					if(funcParents[counter - 1] == funcParent)
					{
						if((nodeChildren[1].getRawSignature().compareTo(fileNameNode[counter -1])) == 0)//if they equal
						{
							//System.out.println("\nRULE VIOLATED: FIO45C_AvoidTOCTOURaceConditionsWhileAccessingFiles");
							ruleViolated = true;
						}
						else
						{
							//System.out.println("NOT EQUAL. Different files");
							//System.out.println("nodeChildren[1].getRawSignature()"+ nodeChildren[1].getRawSignature());
							//System.out.println("fileNameNode[counter -1]" + fileNameNode[counter -1]);
							funcParents[counter] = funcParent;
							fileNameNode[counter] = nodeChildren[1].getRawSignature();
							fileOpNode[counter] = nodeChildren[2].getRawSignature();
							counter++;
						}
					}
					else
					{
						counter = 0;
						funcParents[counter] = funcParent;
						fileNameNode[counter] = nodeChildren[1].getRawSignature();
						fileOpNode[counter] = nodeChildren[2].getRawSignature();
						counter++;
					}
					
				}
				
				
				/*
				System.out.println("Node: " + node.getRawSignature()
						+ "  \nParent:" + funcParents[counter - 1].getRawSignature()
						+ "\nfileNameNode: " + fileNameNode[counter -1]
						+ "\nfileOpNode: " + fileOpNode[counter -1]);
				System.out.println("Counter: "+ counter);
				*/
				
				/*
				for(IASTNode nodeChild: node.getChildren())
				{
					System.out.println("NodeChildren: " + nodeChild.getRawSignature());
					
				}
				*/
				System.out.println("\n");
			}
		}
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		return "CERT Website- A TOCTOU (time-of-check, time-of-use) race condition "
				+"is possible when two or more concurrent processes are operating "
				+"on a shared file system [Seacord 2013b]. \nThese TOCTOU "
				+"conditions can be exploited when a program performs two or "
				+"more file operations on the same file name or path name. " 
				+"\nA program that performs two or more file operations on a "
				+"single file name or path name creates a race window between "
				+"the two file operations. \nThis race window comes from the "
				+"assumption that the file name or path name refers to the "
				+"same resource both times.";
	}

	@Override
	public String getRuleName() {
		return Globals.RuleNames.FIO45_C;
	}

	@Override
	public String getRuleID() {
		return Globals.RuleID.FIO45_C;
	}

	@Override
	public String getRuleRecommendation() {
		
		return "Use fopen() at a signle location and use the x mode of open(). Can also use"
				+ "POSIX's open() function with the O_CREAT and O_EXCL flags.";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_MEDIUM;
	}

	//@Override
	public TreeMap<String, ASTRewrite> getSolutions_CDT(IASTNode node) {
		// TODO Auto-generated method stub
		return null;
	}

	//@Override
	public ITranslationUnit getITranslationUnit_CDT() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRuleURL() {
		
		return "https://wiki.sei.cmu.edu/confluence/display/c/FIO45-C.+Avoid+TOCTOU+race+conditions+while+accessing+files";
	}



}
