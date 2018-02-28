package edu.csus.plugin.securecodingassistant.compilation_C;

import java.util.Properties;

import org.eclipse.cdt.core.dom.IPDOMIndexer;
import org.eclipse.cdt.core.dom.IPDOMIndexerTask;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ITranslationUnit;

public class PDOMIndexer1 implements IPDOMIndexer {

	@Override
	public void setProject(ICProject project) {
		// TODO Auto-generated method stub

	}

	@Override
	public ICProject getProject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProperty(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setProperties(Properties props) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean needsToRebuildForProperties(Properties props) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IPDOMIndexerTask createTask(ITranslationUnit[] added, ITranslationUnit[] changed,
			ITranslationUnit[] removed) {
		// TODO Auto-generated method stub
		return null;
	}

}
