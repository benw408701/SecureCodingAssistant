package edu.csus.plugin.securecodingassistant.rules;

import java.util.List;
import java.util.TreeMap;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

import edu.csus.plugin.securecodingassistant.Globals;

public class PreConditionCheck extends DesignByContractRule{

	@Override
	public boolean violated(ASTNode node) {
		boolean ruleViolated = false;
		
		// Is node a method declaration?
		if (node instanceof MethodDeclaration) {
			
			MethodDeclaration methodDeclaration = (MethodDeclaration) node;

			// find whether method contains @Requires 
			List<?> modifiers = methodDeclaration.modifiers();
			if (isPublic(methodDeclaration) && !(methodDeclaration.getName().getIdentifier().equals("main"))) {
				ruleViolated = true;
				for (Object object : modifiers) {
					if (object instanceof SingleMemberAnnotation) {
						SingleMemberAnnotation annotation = (SingleMemberAnnotation) object;
						String name = annotation.getTypeName().getFullyQualifiedName();
						if (name.equals(Globals.Annotations.REQUIRES)) {
							ruleViolated = false;
							break;
						}
					}
				}
			}
			
			if (ruleViolated)
				ruleViolated = super.violated(node);
		}
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		return "Public method should have preconditoin check";
	}

	@Override
	public String getRuleName() {
		return Globals.RuleNames.PRECONDITION_CHECK;
	}

	@Override
	public String getRuleRecommendation() {
		final String res = String.format("Add precondition check @%s({\"\"}) or skip precondition check @%s", Globals.Annotations.REQUIRES, Globals.Annotations.SKIP_CONDITION_CHECK);
		return res;
	}

	@Override
	public TreeMap<String, ASTRewrite> getSolutions(ASTNode node) {
		if (!(node instanceof MethodDeclaration))
			throw new IllegalArgumentException("Expected MethodDeclaration, but " + node.getAST());
		
		AST ast = node.getAST();
		MethodDeclaration md = (MethodDeclaration)node;
		
		//Solution 1: add @Requires check
		SingleMemberAnnotation annotation1 = ast.newSingleMemberAnnotation();
		annotation1.setTypeName(ast.newName(Globals.Annotations.REQUIRES));
		annotation1.setValue(ast.newArrayInitializer());
		ASTRewrite rewrite1 = ASTRewrite.create(ast);		
		rewrite1.getListRewrite(md, md.getModifiersProperty()).insertFirst(annotation1, null);
		
		// Add solutions to list;
		TreeMap<String, ASTRewrite> list = new TreeMap<String, ASTRewrite>();
		list.putAll(super.getSolutions(node));
		list.put("Add @Requires annotation", rewrite1);
		return list;
	}
	
	@Override
	public String getRuleID() {
		return Globals.RuleID.PRECONDITION;
	}

}
