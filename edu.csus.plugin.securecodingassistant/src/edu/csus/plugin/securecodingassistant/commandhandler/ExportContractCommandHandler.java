package edu.csus.plugin.securecodingassistant.commandhandler;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.csus.plugin.securecodingassistant.Globals;

public class ExportContractCommandHandler extends AbstractHandler {

	@SuppressWarnings("rawtypes")
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
			IEditorInput editorInput = HandlerUtil.getActiveEditorInput(event);
			ICompilationUnit compilationUnit = JavaUI.getWorkingCopyManager().getWorkingCopy(editorInput);
			
			//ITranslationUnit translationUnit =

			ASTParser parser = ASTParser.newParser(AST.JLS8);
			parser.setSource(compilationUnit);
			parser.setResolveBindings(true);

			CompilationUnit cu = (CompilationUnit) parser.createAST(null);

			if (cu.types().isEmpty())
				return null;

			StringBuilder output = new StringBuilder();
			String className = ((TypeDeclaration) cu.types().get(0)).getName().getIdentifier();
			List typeDeclarations = cu.types();
			for (Object obj : typeDeclarations) {
				TypeDeclaration td = (TypeDeclaration) obj;
				ITypeBinding typeBinding = td.resolveBinding();
				if (typeBinding != null) {
					String invariant = getInvariant(typeBinding);

					output.append(String.format("%s\n", invariant));

				}
				MethodDeclaration[] mds = td.getMethods();
				for (MethodDeclaration md : mds) {

					if (md.resolveBinding() != null) {
						IMethodBinding methodBinding = md.resolveBinding();
						String annotation = getAnnotation(methodBinding);
						String returnType = methodBinding.getReturnType().getName();
						String methodName = methodBinding.getName();
						String parameters = getParamethers(md.parameters());
						output.append(String.format("%s%s %s(%s)\n\n", annotation, returnType, methodName, parameters));
					}
				}
			}
			if (output.length() == 0)
				return null;

			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IProject project = root.getProject("securecodingassistant_output");
			try {
				if (!project.exists()) {
					project.create(null);
				}
				project.open(null);

				IProjectDescription desp = project.getDescription();
				desp.setNatureIds(new String[] { JavaCore.NATURE_ID });
				project.setDescription(desp, null);

				@SuppressWarnings("unused")
				IJavaProject javaProject = JavaCore.create(project);

				InputStream stream = new ByteArrayInputStream(output.toString().getBytes());

				IFile file = project.getFile(className + ".txt");
				if (file.exists()) {
					file.setContents(stream, false, false, null);
				} else {
					file.create(stream, true, null);
				}

			} catch (CoreException e) {
				e.printStackTrace();
			}

		return null;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@SuppressWarnings("rawtypes")
	private String getParamethers(List parameters) {
		StringBuilder sb = new StringBuilder();
		for (Object paraObj: parameters) {
			SingleVariableDeclaration para = (SingleVariableDeclaration) paraObj;
			IVariableBinding ivb = para.resolveBinding();
			sb.append(String.format("%s %s, ", ivb.getType().getName(), ivb.getName()));
		}
		if (sb.length() > 2)
			sb.delete(sb.length()-2, sb.length());
		return sb.toString();
	}

	private String getAnnotation(IMethodBinding methodBinding) {
		StringBuilder sb = new StringBuilder();
		for (IAnnotationBinding anno : methodBinding.getAnnotations()) {
			if (anno.getName().equals(Globals.Annotations.ENSURES)
					|| anno.getName().equals(Globals.Annotations.REQUIRES)
					|| anno.getName().equals(Globals.Annotations.THROW_ENSURES)) {
				sb.append(String.format("%s\n", anno.toString()));
			}
		}
		return sb.toString();
	}

	private String getInvariant(ITypeBinding typeBinding) {
		
		StringBuilder sb = new StringBuilder();
		
		IAnnotationBinding[] annoBindings = typeBinding.getAnnotations();
		for (IAnnotationBinding iab : annoBindings) {
			if (iab.getName().equals(Globals.Annotations.INVARIANT)) {
				sb.append(String.format("%s\n", iab.toString()));
			}
		}
		return sb.toString();
	}


}
