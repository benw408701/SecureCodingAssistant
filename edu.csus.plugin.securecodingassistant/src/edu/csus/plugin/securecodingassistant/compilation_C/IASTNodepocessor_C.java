package edu.csus.plugin.securecodingassistant.compilation_C;

import java.net.URI;
import java.util.List;

import org.eclipse.cdt.core.model.BufferChangedEvent;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.IBuffer;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICElementVisitor;
import org.eclipse.cdt.core.model.ICModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.resources.ResourcesPlugin;

public class IASTNodepocessor_C implements ICModel {

	@Override
	public boolean exists() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ICElement getAncestor(int ancestorType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getElementName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getElementType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ICModel getCModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICProject getCProject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICElement getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPath getPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URI getLocationURI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResource getUnderlyingResource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResource getResource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isReadOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStructureKnown() throws CModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void accept(ICElementVisitor visitor) throws CoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getHandleIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICElement[] getChildren() throws CModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ICElement> getChildrenOfType(int type) throws CModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void close() throws CModelException {
		// TODO Auto-generated method stub

	}

	@Override
	public IBuffer getBuffer() throws CModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasUnsavedChanges() throws CModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConsistent() throws CModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void makeConsistent(IProgressMonitor progress) throws CModelException {
		// TODO Auto-generated method stub

	}

	@Override
	public void makeConsistent(IProgressMonitor progress, boolean forced) throws CModelException {
		// TODO Auto-generated method stub

	}

	@Override
	public void open(IProgressMonitor progress) throws CModelException {
		// TODO Auto-generated method stub

	}

	@Override
	public void save(IProgressMonitor progress, boolean force) throws CModelException {
		// TODO Auto-generated method stub

	}

	@Override
	public void bufferChanged(BufferChangedEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void copy(ICElement[] elements, ICElement[] containers, ICElement[] siblings, String[] renamings,
			boolean replace, IProgressMonitor monitor) throws CModelException {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(ICElement[] elements, boolean force, IProgressMonitor monitor) throws CModelException {
		// TODO Auto-generated method stub

	}

	@Override
	public void move(ICElement[] elements, ICElement[] containers, ICElement[] siblings, String[] renamings,
			boolean replace, IProgressMonitor monitor) throws CModelException {
		// TODO Auto-generated method stub

	}

	@Override
	public void rename(ICElement[] elements, ICElement[] destinations, String[] names, boolean replace,
			IProgressMonitor monitor) throws CModelException {
		// TODO Auto-generated method stub

	}

	@Override
	public ICProject getCProject(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICProject[] getCProjects() throws CModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] getNonCResources() throws CModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IWorkspace getWorkspace() {
		// TODO Auto-generated method stub
		return null;
	}

}
