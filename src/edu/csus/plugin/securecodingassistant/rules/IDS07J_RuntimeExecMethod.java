package edu.csus.plugin.securecodingassistant.rules;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodInvocation;

public class IDS07J_RuntimeExecMethod implements IRule {

	@Override
	public boolean violated(ASTNode node) {
		boolean ruleViolated = false;
		
		if(node instanceof MethodInvocation) {
			MethodInvocation method = (MethodInvocation)node;
			String className = method.resolveMethodBinding().getDeclaringClass().getName().toString();
			String methodName = method.getName().toString();
			System.out.printf("Found method %s for class %s%n", methodName, className);
			if (className.equals("Runtime") && methodName.equals("exec"))
				ruleViolated = true;
		}
		
		return ruleViolated;
	}
}
