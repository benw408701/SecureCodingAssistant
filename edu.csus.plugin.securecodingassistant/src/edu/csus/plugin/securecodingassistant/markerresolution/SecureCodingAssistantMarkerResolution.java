package edu.csus.plugin.securecodingassistant.markerresolution;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;

//JDT imports
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IMarkerResolution;


public class SecureCodingAssistantMarkerResolution implements IMarkerResolution {
	
	private String label;
	private ASTRewrite rewrite;
	private ICompilationUnit icu;
	
	public SecureCodingAssistantMarkerResolution(String label, ASTRewrite rewrite, ICompilationUnit icu) {
		this.icu = icu;
		this.rewrite = rewrite;
		this.label = label;
	}
	
	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void run(IMarker marker) {
		IResource resource = marker.getResource();	
		final ICompilationUnit compilationUnit =  icu == null? (ICompilationUnit)JavaCore.create(resource) : icu;
		try {
			final Document document = new Document(compilationUnit.getSource());
			TextEdit edits = rewrite.rewriteAST(document, null);
			edits.apply(document);
			compilationUnit.getBuffer().setContents(document.get());
		} catch (JavaModelException | MalformedTreeException | BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		int lineNumber = marker.getAttribute(Globals.Markers.RESOLUTION_LINENUMBER, 0);
//		
//		
//		try {
//			final Document document = new Document(compilationUnit.getSource());
//			int startPosition = marker.getAttribute(Globals.Markers.RESOLUTION_STARTPOSITION, 0);
//			if (resolution.equals("")) {
//				resolution = marker.getAttribute(Globals.Markers.RESOLUTION, "");
//				
//			} else {
//				startPosition = document.getLineOffset(lineNumber-1);
//			}
//			
//			TextEdit edit = new InsertEdit(startPosition, resolution);
//			MultiTextEdit rootEdit = new MultiTextEdit();
//			rootEdit.addChild(edit);
//			rootEdit.apply(document);
//			compilationUnit.getBuffer().setContents(document.get());
//		} catch (MalformedTreeException | BadLocationException | JavaModelException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
	
	 

}
