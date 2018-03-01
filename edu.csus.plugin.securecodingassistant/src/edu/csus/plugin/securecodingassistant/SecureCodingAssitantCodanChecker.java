package edu.csus.plugin.securecodingassistant;

import org.eclipse.cdt.codan.core.model.AbstractProblemLocation;
import org.eclipse.cdt.codan.core.model.IChecker;
import org.eclipse.cdt.codan.core.model.ICheckerInvocationContext;
import org.eclipse.cdt.codan.core.model.IProblemLocation;
import org.eclipse.cdt.codan.core.model.IProblemReporter;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICContainer;
import org.eclipse.cdt.core.model.ICModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ISourceRoot;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.OperationCanceledException;

import edu.csus.plugin.securecodingassistant.rules_C.RuleFactory_C;

public class SecureCodingAssitantCodanChecker implements IChecker {

	private IProblemReporter temp;
	private IProblemLocation loc;
	private ProblemLocationtemp locA;
	
	private String c_id;
	private ICProject[] CProject;
	private ISourceRoot[] CSource;
	private ICContainer[] CContainer;
	private ITranslationUnit trans1;
	private ITranslationUnit[] tuArray;
	private String tranlationOutput;
	private IASTTranslationUnit ASTTrans1;
	
	private IResource gg;
	
	
	public SecureCodingAssitantCodanChecker() {
		// TODO Auto-generated constructor stub
		System.out.println("InsideCodanCHECKER!!!!!!!!");
	}

	@Override
	public void before(IResource resource) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean processResource(IResource resource, ICheckerInvocationContext context)
			throws OperationCanceledException {
		// TODO Auto-generated method stub
		
		System.out.println("InsideCodanCHECKER32323232323232323!!!!!!!!");
		return false;
	}

	@Override
	public void after(IResource resource) {
		// TODO Auto-generated method stub
		System.out.println("InsideCodanCHECKER!!!!!!!!");
	}

	@Override
	public IProblemReporter getProblemReporter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean runInEditor() {
		// TODO Auto-generated method stub
		System.out.println("InsideCodanCHECKER!!!!!!!!");
		temp = getProblemReporter();
		
		//locA = new AbstractProblemLocation();
		try {
			 gg = getShit();
		} catch (CModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		locA = new ProblemLocationtemp(gg, 30);
		System.out.println("LocA" + locA.getFile());
		//if()
		temp.reportProblem("CODANEXAMPLE", locA, "sdfsdfsdf");
		return true;
	}
	
	public IFile getShit() throws CModelException
	{
		IWorkspace w = ResourcesPlugin.getWorkspace();
		
		
		IWorkspaceRoot cRoot = w.getRoot(); //grabs the C root workspace
		ICModel cmodel = CoreModel.create(cRoot);
		
		Globals.globalResource = cRoot;
		
		//return cmodel.getResource();
		
		//cmodel.get
		
		
		CProject = cmodel.getCProjects();
		
			CContainer = CProject[0].getAllSourceRoots();
			tuArray = CContainer[0].getTranslationUnits();
			return tuArray[0].getFile();
		/*
			for (ICProject o : CProject){
					System.out.println("CProjects");
					//CSource = o.getAllSourceRoots(); 
					CContainer = o.getAllSourceRoots();
					System.out.println("CContainer size: " + CContainer.length);
					
					for(ICContainer i : CContainer)
					{
						tuArray = i.getTranslationUnits();
						
						//get all CERT rules in RuleFactory_C
						//c_rules = RuleFactory_C.getAllCERTRules();
				
				
						
						//iterate through each ITranslationUnit
						for(ITranslationUnit trans1 : tuArray)
						{
							return trans1.getFile();
							Globals.globalTranslationUnit = trans1;
						
							System.out.println("translationUnit element: " + trans1.getElementName());
							try {
								ASTTrans1 = trans1.getAST();
							} catch (CoreException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								System.out.println("Inside core exception");
							}
						}
					}
				}
				*/
	
	}

}
