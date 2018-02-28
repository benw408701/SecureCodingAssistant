package edu.csus.plugin.securecodingassistant;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IMarkerResolution;

public class MarkerResolution1 implements IMarkerResolution {

	public MarkerResolution1() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void run(IMarker marker) {
		// TODO Auto-generated method stub
		System.out.println("INSIDE MARKERRESOLUTION1");
	}

}
