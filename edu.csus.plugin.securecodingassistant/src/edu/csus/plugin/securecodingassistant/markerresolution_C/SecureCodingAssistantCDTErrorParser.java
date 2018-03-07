package edu.csus.plugin.securecodingassistant.markerresolution_C;

import java.util.ArrayList;
import java.util.Iterator;
import org.eclipse.cdt.core.ErrorParserManager;
import org.eclipse.cdt.core.IErrorParser;
import org.eclipse.cdt.core.IMarkerGenerator;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICContainer;
import org.eclipse.cdt.core.model.ICModel;
import org.eclipse.cdt.core.model.ICProject;
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
	private ICProject[] CProject;
	private ICContainer[] CContainer;
	private ITranslationUnit[] tuArray;
	private IASTTranslationUnit ASTTrans1;

	private ArrayList<IRule_C> c_rules;
	
	private String markerOutput;
	private Iterator<InsecureCodeSegment_C> iterateInsecure;
	private InsecureCodeSegment_C insecureNode;
	private int severity = IMarkerGenerator.SEVERITY_WARNING;
	private String ruleLevel;
	private int ruleLevelNum;
	
	public SecureCodingAssistantCDTErrorParser() {
		
	}

	@Override
	public boolean processLine(String line, ErrorParserManager eoParser) {
	
		Globals.cdt_InsecureCodeSegments.clear();
		
		//Function that traverse all of the ITranslationUn it ASTTrees
		try {
			this.run();
		} catch (CModelException e) {
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
			 resource = insecureNode.getviolatedITU().getResource();
			 
			 eoParser.generateMarker(resource, insecureNode.getViolatedNode().getFileLocation().getStartingLineNumber(), markerOutput, severity, "Secure Coding Marker");
			 System.out.println("Rule violated: " + insecureNode.getViolatedRule().getRuleID()+ ": " + insecureNode.getViolatedRule().getRuleName());
		 }
		 System.out.println("Its running");
		 System.out.println("\n\n");
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
					CContainer = o.getAllSourceRoots();
					
					for(ICContainer i : CContainer)
					{
						tuArray = i.getTranslationUnits();
						c_rules = RuleFactory_C.getAllCERTRules();
				
						//iterate through each ITranslationUnit
						for(ITranslationUnit trans1 : tuArray)
						{
					Globals.globalTranslationUnit = trans1;
				
							try {
								ASTTrans1 = trans1.getAST();
							} catch (CoreException e) {
								e.printStackTrace();
								System.out.println("Inside core exception");
							}
								
							//Visiting AST Tree
							SecureCodeNodeVisitor_C visitor1 = new SecureCodeNodeVisitor_C(c_rules, trans1);
							ASTTrans1.accept(visitor1) ;
						}
					}
				}
	}

}
