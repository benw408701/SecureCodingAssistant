package edu.csus.plugin.securecodingassistant.markerresolution_C;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.cdt.core.ErrorParserManager;
import org.eclipse.cdt.core.IErrorParser;
import org.eclipse.cdt.core.IMarkerGenerator;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.INodeFactory;
import org.eclipse.cdt.core.model.BufferChangedEvent;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICContainer;
import org.eclipse.cdt.core.model.ICModel;
import org.eclipse.cdt.core.model.ICModelMarker;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ISourceRoot;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import edu.csus.plugin.securecodingassistant.Globals;
import edu.csus.plugin.securecodingassistant.rules_C.IRule_C;
import edu.csus.plugin.securecodingassistant.rules_C.RuleFactory_C;
import edu.csus.plugin.securecodingassistant.rules_C.SecureCodeNodeVisitor_C;
import edu.csus.plugin.securecodingassistant.markerresolution_C.InsecureCodeSegment_C;

public class SecureCodingAssistantCDTErrorParser implements IErrorParser {

	private IResource resource;
	
	private String c_id;
	private ICProject[] CProject;
	private ISourceRoot[] CSource;
	private ICContainer[] CContainer;
	private ITranslationUnit trans1;
	private ITranslationUnit[] tuArray;
	private String tranlationOutput;
	private IASTTranslationUnit ASTTrans1;
	
	private IASTPreprocessorStatement[] astPreprocStatement;
	private IASTDeclaration[] astDeclaration;
	
	private IASTNode node1;
	private IASTDeclaration [] IASTDec1;
	
	private INodeFactory nFactory1;
	private IASTNode [] nodeList1;
	
	private ArrayList<IRule_C> c_rules;
	
	//private ArrayList<IASTNode> insecureNodes = new ArrayList<IASTNode>();
	
	private String markerOutput;
	private Iterator<InsecureCodeSegment_C> iterateInsecure;
	private InsecureCodeSegment_C insecureNode;
	private int severity = IMarkerGenerator.SEVERITY_WARNING;
	private String ruleLevel;
	private int ruleLevelNum;
	
	public SecureCodingAssistantCDTErrorParser() {
		// TODO Auto-generated constructor stub
		//System.out.println("ERRORPARSER");
		
	}

	@Override
	public boolean processLine(String line, ErrorParserManager eoParser) {
		// TODO Auto-generated method stub
		//eoParser.generateMarker(file, lineNumber, desc, severity, varName);
		Globals.cdt_InsecureCodeSegments.clear();
		
		//Function that traverse all of the ITranslationUn it ASTTrees
		try {
			this.run();
		} catch (CModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 
		 
		 iterateInsecure = Globals.cdt_InsecureCodeSegments.iterator();
		 
		 while(iterateInsecure.hasNext())
		 {
			 insecureNode = iterateInsecure.next();
			 
			 ruleLevelNum = insecureNode.getViolatedRule().securityLevel();
			 
			 switch(ruleLevelNum) {
			 
			 	case 1: ruleLevel = "LOW";
			 			break;
			 	case 2: ruleLevel = "MEDIUM";
			 			break;
			 	case 3: ruleLevel = "HIGH";
			 			break;
			 	default: ruleLevel = "UNKNOWN";
			 			break;
			 }
			 
			 markerOutput = "Rule violated: " + insecureNode.getViolatedRule().getRuleID()
					 + " " + insecureNode.getViolatedRule().getRuleName()
					 + "\nSeverity: " + ruleLevel
					 + "\n\nNOTE: The text and/or code below is from the CERT website: "
					 + insecureNode.getViolatedRule().getRuleURL()
					 + "\n\nRule Description: " + insecureNode.getViolatedRule().getRuleText()
					 + "\n\nRule Solution: " + insecureNode.getViolatedRule().getRuleRecommendation();
			// System.out.println("\nInsecureNode: " + insecureNode.getViolatedNode().getRawSignature());
			 //System.out.println("RuleViolated: " + insecureNode.getViolatedRule().getRuleName());
			 //System.out.println("ITranslationUnit: " + insecureNode.getviolatedITU().getResource().getFileExtension());
			 resource = insecureNode.getviolatedITU().getResource();
			 
			 eoParser.generateMarker(resource, insecureNode.getViolatedNode().getFileLocation().getStartingLineNumber(), markerOutput, severity, "Secure Coding Marker");
			 System.out.println("Rule violated: " + insecureNode.getViolatedRule().getRuleID()+ ": " + insecureNode.getViolatedRule().getRuleName());
		 }
		 System.out.println();
		 //eoParser.
		 eoParser.shutdown(); //shutdown parser after 1 iteration
		 
		return false;
	}
	
	public void run() throws CModelException
	{
		IWorkspace w = ResourcesPlugin.getWorkspace();
		
		
		IWorkspaceRoot cRoot = w.getRoot(); //grabs the C root workspace
		ICModel cmodel = CoreModel.create(cRoot);
		
		Globals.globalResource = cRoot;
		
		CProject = cmodel.getCProjects();
		
				for (ICProject o : CProject){
					//System.out.println("CProjects");
					//CSource = o.getAllSourceRoots(); 
					CContainer = o.getAllSourceRoots();
					//System.out.println("CContainer size: " + CContainer.length);
					
					for(ICContainer i : CContainer)
					{
						tuArray = i.getTranslationUnits();
						
						//get all CERT rules in RuleFactory_C
						c_rules = RuleFactory_C.getAllCERTRules();
				
				
				
						//iterate through each ITranslationUnit
						for(ITranslationUnit trans1 : tuArray)
						{
					
							Globals.globalTranslationUnit = trans1;
						
							//System.out.println("translationUnit element: " + trans1.getElementName());
							try {
								ASTTrans1 = trans1.getAST();
							} catch (CoreException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								System.out.println("Inside core exception");
							}
				
				
							//Visiting AST Tree
							//System.out.println("Traversing AST Tree:");
				
							SecureCodeNodeVisitor_C visitor1 = new SecureCodeNodeVisitor_C(c_rules, trans1);
				
							ASTTrans1.accept(visitor1) ;
						}
					}
				}
	}

}
