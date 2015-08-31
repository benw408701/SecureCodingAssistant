package edu.csus.plugin.securecodingassistant.rules;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodInvocation;

class ASTNodeProcessor extends ASTVisitor {
	private ArrayList<MethodInvocation> m_methods;
	
	public ASTNodeProcessor() {
		super();
		
		m_methods = new ArrayList<MethodInvocation>();
	}
	
	@Override
	public boolean visit(MethodInvocation methodInvocation) {
		m_methods.add(methodInvocation);
		return super.visit(methodInvocation);
	}
	
	public ArrayList<MethodInvocation> getMethods() {
		return m_methods;
	}
}
