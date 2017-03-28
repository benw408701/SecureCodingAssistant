package edu.csus.plugin.securecodingassistant.rules;


import java.util.List;
import java.util.TreeMap;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;

import edu.csus.plugin.securecodingassistant.Globals;

public abstract class DesignByContractRule implements IRule {
	
	@Override
	public boolean violated(ASTNode node) {
		//Check whether the condition check is skipped
				
		if (node instanceof MethodDeclaration) {

			MethodDeclaration methodDeclaration = (MethodDeclaration) node;

			// find whether method contains @SkipConditionCheck annotation
			List<?> modifiers = methodDeclaration.modifiers();
			SingleMemberAnnotation skipConditionAnnotation = null;

			for (Object object : modifiers) {
				if (object instanceof SingleMemberAnnotation) {
					SingleMemberAnnotation annotation = (SingleMemberAnnotation) object;
					Name typeName = annotation.getTypeName();
					if (Globals.Annotations.SKIP_CONDITION_CHECK.equals(typeName.getFullyQualifiedName())) {
						skipConditionAnnotation = annotation;
						break;
					}
				}
			}

			if (skipConditionAnnotation != null) {
				// method contains @SkipConditionCheck annotation
				ArrayInitializer newValue = (ArrayInitializer) skipConditionAnnotation.getValue();
				for (Object obj : newValue.expressions()) {
					if (obj instanceof StringLiteral) {
						StringLiteral sl = (StringLiteral) obj;
						if (sl.getLiteralValue().equals(getRuleID()))
							return false;
					}
				}
			}
		}
		return true;
	}
	
	
	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_HIGH;
	}
	
	public boolean isPublic(MethodDeclaration node) {
		List<?> modifiers = node.modifiers();
		for (Object obj: modifiers) {
			if (obj instanceof Modifier) {
				Modifier m = (Modifier) obj;
				if (m.isPublic())
					return true;
			}
		}
		return false;
	}
	
	public boolean isPublic(TypeDeclaration node) {
		List<?> modifiers = node.modifiers();
		for (Object obj: modifiers) {
			if (obj instanceof Modifier) {
				Modifier m = (Modifier) obj;
				if (m.isPublic())
					return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public TreeMap<String, ASTRewrite> getSolutions(ASTNode node) {
		
		TreeMap<String, ASTRewrite> solutionMap = new TreeMap<>();
		if (node instanceof MethodDeclaration) {
			MethodDeclaration methodDeclaration = (MethodDeclaration)node;
			AST methodAST = methodDeclaration.getAST();
			ASTRewrite rewrite = ASTRewrite.create(methodAST);
			
			// find whether method contains @SkipConditionCheck annotation
			List<?> modifiers = methodDeclaration.modifiers();
			SingleMemberAnnotation skipConditionAnnotation = null;

			for (Object object : modifiers) {
				if (object instanceof SingleMemberAnnotation) {
					SingleMemberAnnotation annotation = (SingleMemberAnnotation) object;
					Name typeName = annotation.getTypeName();
					if (Globals.Annotations.SKIP_CONDITION_CHECK.equals(typeName.getFullyQualifiedName())) {
						skipConditionAnnotation = annotation;
					}
				}
			}
			
			StringLiteral sl = methodAST.newStringLiteral();
			sl.setLiteralValue(getRuleID());
			ArrayInitializer newArrayInitializer = methodAST.newArrayInitializer();
			
			if (skipConditionAnnotation != null) {
				// method contains @SkipConditionCheck annotation, add ruleID to value
				Expression oldExpression = skipConditionAnnotation.getValue();
				ArrayInitializer array = (ArrayInitializer) oldExpression;
				ListRewrite listRewrite = rewrite.getListRewrite(array, ArrayInitializer.EXPRESSIONS_PROPERTY);
				listRewrite.insertLast(sl, null);
				
			} else {
				// method doesn't contain @SkipConditionCheck annotation, insert annotation
				SingleMemberAnnotation newAnnotation = methodAST.newSingleMemberAnnotation();
				newAnnotation.setTypeName(methodAST.newName(Globals.Annotations.SKIP_CONDITION_CHECK));
				newArrayInitializer.expressions().add(sl);
				newAnnotation.setValue(newArrayInitializer);
				rewrite.getListRewrite(methodDeclaration, methodDeclaration.getModifiersProperty())
						.insertFirst(newAnnotation, null);
			}
			solutionMap.put("Skip Condition Check", rewrite);
		}
		return solutionMap;
	}
	
	@Override
	public ICompilationUnit getICompilationUnit(){
		return null;
	}

}
