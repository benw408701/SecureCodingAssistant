package edu.csus.plugin.securecodingassistant.rules;

import java.util.List;
import java.util.TreeMap;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

import edu.csus.plugin.securecodingassistant.Globals;

public class PostConditionCheck extends DesignByContractRule{

	@Override
	public boolean violated(ASTNode node) {
		
		boolean ruleViolated = false;
		
		// Is node a method declaration?
		if (node instanceof MethodDeclaration) {
			
			MethodDeclaration methodDeclaration = (MethodDeclaration) node;

			// find whether method contains @Ensures, @ThrowEnsure or @SkipPostconditionCheck annotation
			List<?> modifiers = methodDeclaration.modifiers();
			
			if (isPublic(methodDeclaration) && !(methodDeclaration.getName().getIdentifier().equals("main"))) {
				ruleViolated = true;
				for (Object object : modifiers) {
					if (object instanceof SingleMemberAnnotation) {
						SingleMemberAnnotation annotation = (SingleMemberAnnotation) object;
						String name = annotation.getTypeName().getFullyQualifiedName();
						if (name.equals(Globals.Annotations.ENSURES) 
								|| name.equals(Globals.Annotations.THROW_ENSURES)) {
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
		return "Public method should have postconditoin check";
	}

	@Override
	public String getRuleName() {
		return Globals.RuleNames.POSTCONDITION_CHECK;
	}

	@Override
	public String getRuleRecommendation() {
		final String res = String.format(
				"Add postcondition check @%s({\"\"}) or throw exception@%s({\"\"}) or skip postcondition check @%s",
				Globals.Annotations.ENSURES, Globals.Annotations.THROW_ENSURES,
				Globals.Annotations.SKIP_CONDITION_CHECK);
		return res;
	}

	@Override
	public TreeMap<String,ASTRewrite> getSolutions(ASTNode node) {
		if (!(node instanceof MethodDeclaration))
			throw new IllegalArgumentException("Expected MethodDeclaration, but " + node.getAST());
		
		AST ast = node.getAST();
		MethodDeclaration md = (MethodDeclaration)node;
		
		//Solution 1: add @Ensures check
		SingleMemberAnnotation annotation1 = ast.newSingleMemberAnnotation();
		annotation1.setTypeName(ast.newName(Globals.Annotations.ENSURES));
		annotation1.setValue(ast.newArrayInitializer());
		ASTRewrite rewrite1 = ASTRewrite.create(ast);		
		rewrite1.getListRewrite(md, md.getModifiersProperty()).insertFirst(annotation1, null);
		
		//Solution 2: add @ThrowEnsures 
		SingleMemberAnnotation annotation2 = ast.newSingleMemberAnnotation();
		annotation2.setTypeName(ast.newName(Globals.Annotations.THROW_ENSURES));
		annotation2.setValue(ast.newArrayInitializer());
		ASTRewrite rewrite2 = ASTRewrite.create(ast);		
		rewrite2.getListRewrite(md, md.getModifiersProperty()).insertFirst(annotation2, null);
		
		// Add solutions to list;
		TreeMap<String,ASTRewrite>  map = new TreeMap<String,ASTRewrite> ();
		map.putAll(super.getSolutions(node));
		map.put("Add @Ensures check", rewrite1);
		map.put("Add @ThrowEnsures exception", rewrite2);
		return map;		
	}
	

	
	@Override
	public String getRuleID() {
		return Globals.RuleID.POSTCONDITION;
	}

}
