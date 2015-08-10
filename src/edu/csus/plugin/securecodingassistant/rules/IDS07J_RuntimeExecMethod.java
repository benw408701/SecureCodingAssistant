package edu.csus.plugin.securecodingassistant.rules;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodInvocation;

public class IDS07J_RuntimeExecMethod implements IRule {

	@Override
	public boolean violated(ASTNode node) {
		boolean ruleViolated = false;
		
		if(node instanceof MethodInvocation) {
			MethodInvocation method = (MethodInvocation)node;
			if (method.getExpression().resolveTypeBinding().getName() == "Runtime" &&
					method.getName().toString() == "exec")
				ruleViolated = true;
		}
		
		return ruleViolated;
	}
}
