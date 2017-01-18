package edu.csus.plugin.securecodingassistant.rules;

import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class SolutionNodeProcessor extends ASTVisitor{
	
	private HashMap<String, VariableDeclarationStatement> nameToVariableDeclarationStatement;
	private HashMap<String, MethodInvocation> variableNameToMethodInvocation;
	private HashMap<MethodInvocation, String> methodInvocationToVariableName;
	private HashMap<String, VariableDeclarationFragment> nameToVariableDeclarationFragment;
	
	public SolutionNodeProcessor() {
		super();
		nameToVariableDeclarationStatement = new HashMap<>();
		variableNameToMethodInvocation = new HashMap<>();
		methodInvocationToVariableName = new HashMap<>();
		nameToVariableDeclarationFragment = new HashMap<>();
	}
	
	@Override
	public boolean visit(VariableDeclarationStatement variableDeclarationStatement) {
		List<?> lists = variableDeclarationStatement.fragments();
		for (Object obj : lists) {
			if (obj instanceof VariableDeclarationFragment) {
				VariableDeclarationFragment vdf = (VariableDeclarationFragment) obj;
				final String name = vdf.getName().getFullyQualifiedName();
				nameToVariableDeclarationStatement.put(name, variableDeclarationStatement);
				nameToVariableDeclarationFragment.put(name, vdf);
				if (vdf.getInitializer() != null && vdf.getInitializer() instanceof MethodInvocation) {
					MethodInvocation mi =  (MethodInvocation) vdf.getInitializer();
					variableNameToMethodInvocation.put(name, mi);
					methodInvocationToVariableName.put(mi, name);
				}
				
			}
		}
		return super.visit(variableDeclarationStatement);
	}
	
	@Override
	public boolean visit(Assignment assignment){
		String name = assignment.getLeftHandSide().toString();
		if (nameToVariableDeclarationStatement.containsKey(name)) {
			Expression exp = assignment.getRightHandSide();
			if (exp != null && exp instanceof MethodInvocation){
				variableNameToMethodInvocation.put(name, (MethodInvocation) exp);
				methodInvocationToVariableName.put((MethodInvocation) exp, name);
				System.out.println(">>>>"+assignment.getLeftHandSide().toString());
			}
		}
		return super.visit(assignment);
	}
	
	public VariableDeclarationStatement getCorrespondingVariableDeclarationStatement(String name) {
		if (!nameToVariableDeclarationStatement.containsKey(name))
			throw new NoSuchElementException(name);
		return nameToVariableDeclarationStatement.get(name);
	}
	
	public Type getCorrespondingType(String name) {
		return getCorrespondingVariableDeclarationStatement(name).getType();
	}
	
	public MethodInvocation getCorrespondingMethodInvocation(String name) {
		if (!variableNameToMethodInvocation.containsKey(name))
			throw new NoSuchElementException(name);
		return variableNameToMethodInvocation.get(name);
	}
	
	public String getVariableNameFromMethodInvocation(MethodInvocation methodInvocation) {
		if (!methodInvocationToVariableName.containsKey(methodInvocation))
			throw new NoSuchElementException(methodInvocation.toString());
		return methodInvocationToVariableName.get(methodInvocation);
	}
	
	public VariableDeclarationFragment getCorrespondingVariableDeclarationFragment(String name) {
		if (!nameToVariableDeclarationFragment.containsKey(name))
			throw new NoSuchElementException(name);
		return nameToVariableDeclarationFragment.get(name);
	}
}
