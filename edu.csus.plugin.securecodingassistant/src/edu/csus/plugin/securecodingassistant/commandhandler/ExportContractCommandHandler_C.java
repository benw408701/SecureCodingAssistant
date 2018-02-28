package edu.csus.plugin.securecodingassistant.commandhandler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.plugin.AbstractUIPlugin;

//CDT imports
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.ICModel;
import org.eclipse.cdt.core.model.ICProject;


import edu.csus.plugin.securecodingassistant.Globals;
import edu.csus.plugin.securecodingassistant.rules_C.NodeNumPair_C;

public class ExportContractCommandHandler_C extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// TODO Possible create separate class that represents a CModel
		IEditorInput editorInput = HandlerUtil.getActiveEditorInput(event);
		
		
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		
		//TODO how to intialize ICModel
		ICModel c_model = (ICModel)root;
		try {
			ICProject[] allProjects = c_model.getCProjects();
		} catch (CModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
