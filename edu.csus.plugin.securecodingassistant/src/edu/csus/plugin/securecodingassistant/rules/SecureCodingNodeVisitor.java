package edu.csus.plugin.securecodingassistant.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class SecureCodingNodeVisitor extends ASTVisitor {

	private HashMap<String, Type> variableToType;
	private HashMap<String, HashSet<Assignment>> variableToAssignments;
	private HashMap<Expression, String> expressionToVariable;
	private HashMap<String, VariableDeclarationFragment> variableToDeclarationFragment;
	private HashSet<MethodInvocation> methodInvocations;

	public SecureCodingNodeVisitor() {
		super();
		variableToType = new HashMap<>();
		variableToAssignments = new HashMap<>();
		expressionToVariable = new HashMap<>();
		variableToDeclarationFragment = new HashMap<>();
		methodInvocations = new HashSet<>();
	}

	@Override
	public boolean visit(VariableDeclarationStatement vds) {
		for (Object obj : vds.fragments()) {
			if (obj instanceof VariableDeclarationFragment) {
				VariableDeclarationFragment vdf = (VariableDeclarationFragment) obj;
				String name = vdf.getName().getIdentifier();
				variableToDeclarationFragment.put(name, vdf);
				variableToType.put(name, vds.getType());
				if (vdf.getInitializer() != null) {
					expressionToVariable.put(vdf.getInitializer(), name);
				}
			}
		}
		return super.visit(vds);
	}

	@Override
	public boolean visit(Assignment assignment) {
		Expression exp = assignment.getLeftHandSide();
		if (exp instanceof SimpleName) {
			SimpleName sn = (SimpleName) exp;
			if (assignment.getRightHandSide() != null) {
				expressionToVariable.put(assignment.getRightHandSide(), sn.getIdentifier());
				HashSet<Assignment> set = variableToAssignments.get(sn.toString());
				if (set == null) {
					set = new HashSet<>();
				}
				set.add(assignment);
				variableToAssignments.put(sn.toString(), set);
			}
		}
		return super.visit(assignment);
	}

	@Override
	public boolean visit(MethodInvocation mi) {
		methodInvocations.add(mi);
		return super.visit(mi);
	}

	public static Statement getStatement(ASTNode node) {
		ASTNode parent = node;
		while (parent != null && !(parent instanceof Statement)) {
			parent = parent.getParent();
		}
		if (parent != null) {
			return (Statement) parent;
		} else {
			return null;
		}
	}

	public VariableDeclarationFragment getVariableDeclarationFragment(String name) {
		if (!variableToDeclarationFragment.containsKey(name))
			return null;
		else
			return variableToDeclarationFragment.get(name);
	}

	public Type getType(String name) {
		if (!variableToType.containsKey(name))
			return null;
		else
			return variableToType.get(name);
	}

	public String getVariable(Expression exp) {
		if (!expressionToVariable.containsKey(exp))
			return null;
		else
			return expressionToVariable.get(exp);
	}

	public ArrayList<MethodInvocation> getMethodInvocations(String name, String expression, int arguSize, ArrayList<String> arguType) {
		ArrayList<MethodInvocation> res = new ArrayList<>();
		for (MethodInvocation mi: methodInvocations) {
			if (mi.getExpression() != null && mi.getExpression().resolveTypeBinding() != null
					&& mi.getExpression().resolveTypeBinding().getBinaryName() != null
					&& expression.equals(mi.getExpression().resolveTypeBinding().getBinaryName())
					&& mi.getName() != null && mi.getName().getIdentifier() != null
					&& name.equals(mi.getName().getIdentifier()) && mi.arguments() != null
					&& arguSize == mi.arguments().size()) {
				boolean match = true;
				for (int i = 0; i < arguSize; i++) {
					String arguVariable = mi.arguments().get(i).toString();
					Type arguType_mi = getType(arguVariable);
					if (arguType_mi != null && !arguType.get(i).equals(arguType_mi.resolveBinding().getQualifiedName())) {
						match = false;
						break;
					}
				}
				if (match) 
					res.add(mi);
			}
		}
		return res;
	}
	
	public HashSet<Assignment> getAssignement(String name) {
		return variableToAssignments.get(name);
	}

	public HashSet<MethodInvocation> getMethodInvocation() {
		return methodInvocations;
	}
}
