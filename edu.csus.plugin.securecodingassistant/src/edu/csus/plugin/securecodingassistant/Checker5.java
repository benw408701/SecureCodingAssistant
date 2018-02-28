package edu.csus.plugin.securecodingassistant;

import org.eclipse.cdt.codan.core.cxx.model.AbstractIndexAstChecker;
import org.eclipse.cdt.codan.core.model.IChecker;
import org.eclipse.cdt.codan.core.model.IProblem;
import org.eclipse.cdt.codan.core.model.IProblemReporter;
import org.eclipse.cdt.codan.core.model.IProblemWorkingCopy;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

public class Checker5 extends AbstractIndexAstChecker implements IChecker {

	public static final String PROBLEM_1 = "edu.csus.plugin.securecodingassistant.problem1";
	
	private IProblemWorkingCopy[] wc = new IProblemWorkingCopy[20];
	private int counter = 0;
	
	public Checker5() {
		// TODO Auto-generated constructor stub
		//System.out.println("CHECKER5FIRST");
		counter = 0;
		
	}

	@Override
	public void processAst(IASTTranslationUnit ast) {
		// TODO Auto-generated method stub
		System.out.println("CHECKER555555");
		System.out.println("AST::" + ast.getContainingFilename());
		
		IProblemWorkingCopy wc;
		//this.
		//this.reportProblem(PROBLEM_1, ast.getOriginatingTranslationUnit().getFile(),6);
		
		//this.reportProblem(PROBLEM_1, ast.getOriginatingTranslationUnit().getFile(), 6, symbolName);
		//this.report
		//this.rport
		//IProblem p1 = new IProblem();;
		
	}
	
	@Override
	public boolean runInEditor() {
		/*
		System.out.println("runInEditor()");
		for (IProblemWorkingCopy w: wc)
		{
			if(w != null)
			{
				System.out.println("IProblemWOrkingcopy: " + w.getName());
		
			}
		}
		//IProject ip = this.getProject();
		IWorkspace w = ResourcesPlugin.getWorkspace();
		
		
		IWorkspaceRoot cRoot = w.getRoot(); //grabs the C root workspace
		ICModel cmodel = CoreModel.create(cRoot);
		
		ICProject temp1[] = null;
		
		try {
			temp1 = cmodel.getCProjects();
		} catch (CModelException e) {
			// TODO Auto-generated catch block
			System.out.println("HERE1");
			e.printStackTrace();
		}
		System.out.println("HERE1");
		IFile ifp = null;
		
		try {
			ifp = temp1[0].getAllSourceRoots()[0].getTranslationUnits()[0].getFile();
		} catch (CModelException e) {
			// TODO Auto-generated catch block
			System.out.println("HERE2");
			e.printStackTrace();
		}
		System.out.println("HERE2");
		
		//IFile ifp = this.getFile();
		try {
			System.out.println("IFILE:::" + temp1[0].getAllSourceRoots()[0].getTranslationUnits()[0].getElementName());
		} catch (CModelException e) {
			// TODO Auto-generated catch block
			System.out.println("HERE3");
			e.printStackTrace();
		}
		System.out.println("HERE3");
		
		//ProblemLocationtemp tp = new ProblemLocationtemp(ifp, 6);
		
		//wc[0].
		//this.reportProblem(wc[0], tp, "");
		if(this.shouldProduceProblem(wc[0], ifp.getFullPath()))
		{
			System.out.println("Am i here?");
			this.reportProblem(PROBLEM_1, ifp, 22);
		}
		this.reportProblem(PROBLEM_1, ifp, 22);
		//this.report
		
		//wc[0].getId();
		
		//ip.
		 
		 */
		return false;
	}
	
	@Override
	public void initPreferences(IProblemWorkingCopy problem) {
		/*
		System.out.println("INSIDE initPreferences" + problem.getId());
		if(counter == 0)
		{
			//problem.setDescription("Problem1: Description");
			//problem.setMessagePattern("Problem1: SetMessagePattern");
		}
			//problem.setSeverity(sev);
		wc[counter] = problem;
		counter = counter + 1;
	*/	
	}
	 

}
