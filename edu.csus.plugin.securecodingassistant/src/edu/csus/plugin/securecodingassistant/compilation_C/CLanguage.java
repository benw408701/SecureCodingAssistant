package edu.csus.plugin.securecodingassistant.compilation_C;


import java.util.ArrayList;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.ErrorParserManager;
import org.eclipse.cdt.core.dom.ICodeReaderFactory;
//import org.eclipse.cdt.core.dom.IncludeFileContentProvider;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTCompletionNode;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.INodeFactory;
import org.eclipse.cdt.core.dom.ast.gnu.c.GCCLanguage;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.GPPLanguage;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.AbstractLanguage;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.IBinaryContainer;
import org.eclipse.cdt.core.model.ICContainer;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICElementVisitor;
import org.eclipse.cdt.core.model.ICModel;
import org.eclipse.cdt.core.model.ICModelMarker;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.IContributedModelBuilder;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.model.ISourceRoot;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.parser.CodeReader;
import org.eclipse.cdt.core.parser.FileContent;

import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IScanner;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.core.parser.IncludeFileContentProvider;
import org.eclipse.cdt.ui.CDTUITools;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.cdt.core.IMarkerGenerator;

import edu.csus.plugin.securecodingassistant.rules_C.SecureCodeNodeVisitor_C;
import edu.csus.plugin.securecodingassistant.Globals;
import edu.csus.plugin.securecodingassistant.rules.IRule;
import edu.csus.plugin.securecodingassistant.rules.RuleFactory;
import edu.csus.plugin.securecodingassistant.rules_C.*;




@SuppressWarnings("deprecation")
public class CLanguage extends AbstractLanguage {

	
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
	
	private ICModelMarker mm1;
	private ErrorParserManager manager;
	private ArrayList<IRule_C> c_rules;
	
	private ArrayList<IASTNode> insecureNodes = new ArrayList<IASTNode>();
	
	//test
	private IMarkerGenerator mg;
	
	public CLanguage() throws CModelException {
		super();
		// TODO Auto-generated constructor stub
		//System.out.println("WE ARE HERE>");
		//c_id = this.getId();
		/*
		IWorkspace w = ResourcesPlugin.getWorkspace();
		
		
		IWorkspaceRoot cRoot = w.getRoot(); //grabs the C root workspace
		ICModel cmodel = CoreModel.create(cRoot);
		
		Globals.globalResource = cRoot;
		
		CProject = cmodel.getCProjects();
		
			
				for (ICProject o : CProject){
					System.out.println("CProjects");
					//CSource = o.getAllSourceRoots(); 
					CContainer = o.getAllSourceRoots();
					System.out.println("CContainer size: " + CContainer.length);
					
					for(ICContainer i : CContainer)
					{
						//trans1 = i.getTranslationUnit("");
						tuArray = i.getTranslationUnits();
						//System.out.println(i.getElementName());
						//System.out.println("tuArray size: " + tuArray.length);
						
						
						//tranlationOutput = trans1.getContents();
						//System.out.println("Translation Output: " + tranlationOutput);
					}
					
				
				}
				int insecureNodeCounter = 0;
				//get all CERT rules in RuleFactory_C
				c_rules = RuleFactory_C.getAllCERTRules();
				
				
				
				//iterate through each ITranslationUnit
				for(ITranslationUnit trans1 : tuArray)
				{
					
				//trans1 = tuArray[1];
					
					
					Globals.globalTranslationUnit = trans1;
						
					System.out.println("translationUnit element: " + trans1.getElementName());
					try {
						ASTTrans1 = trans1.getAST();
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("Inside core exception");
					}
				
				
					//Visiting AST Tree
					System.out.println("Traversing AST Tree:");
				
					SecureCodeNodeVisitor_C visitor1 = new SecureCodeNodeVisitor_C(c_rules);
				
					ASTTrans1.accept(visitor1) ;
				}
				//nFactory1 = ASTTrans1.getASTNodeFactory();
				
				//ASTTrans1.
				nodeList1 = ASTTrans1.getChildren();
				
				
				IASTPreprocessorStatement [] s2;
				
				s2 = ASTTrans1.getIncludeDirectives();
				
				int counter1 = 0;
				
				
				
				
				System.out.println("After ASTTranslation unit visit");
		
		*/
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		System.out.println("String getID()");
		
		return "first_string";
	}

	@Override
	public int getLinkageID() {
		System.out.println("getLinkageID");
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IContributedModelBuilder createModelBuilder(ITranslationUnit tu) {
		System.out.println("createModelBuilder");
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IASTName[] getSelectedNames(IASTTranslationUnit ast, int start, int length) {
		// TODO Auto-generated method stub
		System.out.println("getSelectedNames");
		return null;
	}

	@Override
	public IASTTranslationUnit getASTTranslationUnit(CodeReader reader, IScannerInfo scanInfo,
			ICodeReaderFactory fileCreator, IIndex index, IParserLogService log) throws CoreException {
		// TODO Auto-generated method stub
		System.out.println("getASTTranslation");
		return null;
	}
	
	//This is the more up to date version
	@Override
	public IASTTranslationUnit getASTTranslationUnit(FileContent content, IScannerInfo scanInfo, IncludeFileContentProvider fileCreator,
            IIndex index, int options, IParserLogService log) throws CoreException{
		
		
		
		return null;
	}

	@Override
	public IASTCompletionNode getCompletionNode(CodeReader reader, IScannerInfo scanInfo,
			ICodeReaderFactory fileCreator, IIndex index, IParserLogService log, int offset) throws CoreException {
		// TODO Auto-generated method stub
		System.out.println("getCompletionNode");
		return null;
	}

}
