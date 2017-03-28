package edu.csus.plugin.securecodingassistant.markerresolution;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;

import edu.csus.plugin.securecodingassistant.Globals;

public class SecureCodingAssistantMarkerResolutiongenerator implements IMarkerResolutionGenerator{

	@Override
	public IMarkerResolution[] getResolutions(IMarker marker) {

		List<IMarkerResolution> resolutions = new ArrayList<>();
		String ruleID = marker.getAttribute(Globals.Markers.RULE_ID, "");
		int hashcode = marker.getAttribute(Globals.Markers.HASHCODE, 0);
		TreeMap<String, ASTRewrite> map = Globals.RULE_SOLUTIONS.get(ruleID + hashcode);
		ICompilationUnit icu = Globals.RULE_ICOMPILATIONUNIT.get(ruleID + hashcode); 
		if (map == null) {
			return new IMarkerResolution[1];
		}
		for (String label : map.keySet()) {
			resolutions.add(new SecureCodingAssistantMarkerResolution(label, map.get(label), icu));
		}
		
		//final SecureCodingAssistantMarkerResolution skip_contract = new SecureCodingAssistantMarkerResolution(Globals.SKIP_RULE_CHECK, Globals.SKIP_RULE_ANNOTATION);
		return resolutions.toArray(new IMarkerResolution[resolutions.size()]);
	}

}
