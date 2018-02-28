package edu.csus.plugin.securecodingassistant.rules;

import java.util.List;
import java.util.TreeMap;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;

import edu.csus.plugin.securecodingassistant.Globals;

public abstract class SecureCodingRule implements IRule{
	
	@Override
	public boolean violated(ASTNode node) {
		//Check whether the rule check is skipped
		ASTNode parent = node;
		while (parent != null && !(parent instanceof MethodDeclaration)) {
			parent = parent.getParent();
		}
		
		if (parent != null) {

			MethodDeclaration methodDeclaration = (MethodDeclaration) parent;

			// find whether method contains @SkipRuleCheck annotation
			List<?> modifiers = methodDeclaration.modifiers();
			SingleMemberAnnotation skipRuleAnnotation = null;

			for (Object object : modifiers) {
				if (object instanceof SingleMemberAnnotation) {
					SingleMemberAnnotation annotation = (SingleMemberAnnotation) object;
					Name typeName = annotation.getTypeName();
					if (Globals.Annotations.SKIP_RULE_CHECK.equals(typeName.getFullyQualifiedName())) {
						skipRuleAnnotation = annotation;
						break;
					}
				}
			}

			if (skipRuleAnnotation != null) {
				// method contains @SkipRuleCheck annotation
				ArrayInitializer newValue = (ArrayInitializer) skipRuleAnnotation.getValue();
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

	@SuppressWarnings("unchecked")
	@Override
	public TreeMap<String,ASTRewrite> getSolutions(ASTNode node) {
		if (!violated(node))
			throw new IllegalArgumentException("This node doesn't violate rule, so no suggest solution");
		
		TreeMap<String, ASTRewrite> solutionMap = new TreeMap<>();
		// Add skip rule check to solutionList
		ASTNode parent = node;
		while (parent != null && !(parent instanceof MethodDeclaration)) {
			parent = parent.getParent();
		}

		if (parent != null) {

			MethodDeclaration methodDeclaration = (MethodDeclaration) parent;
			AST methodAST = methodDeclaration.getAST();
			ASTRewrite rewrite = ASTRewrite.create(methodAST);

			// find whether method contains @SkipRuleCheck annotation
			List<?> modifiers = methodDeclaration.modifiers();
			SingleMemberAnnotation skipRuleAnnotation = null;

			for (Object object : modifiers) {
				if (object instanceof SingleMemberAnnotation) {
					SingleMemberAnnotation annotation = (SingleMemberAnnotation) object;
					Name typeName = annotation.getTypeName();
					if (Globals.Annotations.SKIP_RULE_CHECK.equals(typeName.getFullyQualifiedName())) {
						skipRuleAnnotation = annotation;
					}
				}
			}

			
			StringLiteral sl = methodAST.newStringLiteral();
			sl.setLiteralValue(getRuleID());

			if (skipRuleAnnotation != null) {
				// method contains @SkipRuleCheck annotation
				Expression oldExpression = skipRuleAnnotation.getValue();
				ArrayInitializer array = (ArrayInitializer) oldExpression;
				ListRewrite listRewrite = rewrite.getListRewrite(array, ArrayInitializer.EXPRESSIONS_PROPERTY);
				listRewrite.insertLast(sl, null);
				
			} else {
				// method doesn't contain @SkipRuleCheck annotation
				SingleMemberAnnotation newAnnotation = methodAST.newSingleMemberAnnotation();
				newAnnotation.setTypeName(methodAST.newName(Globals.Annotations.SKIP_RULE_CHECK));
				ArrayInitializer newValue = methodAST.newArrayInitializer();
				newValue.expressions().add(sl);
				newAnnotation.setValue(newValue);
				rewrite.getListRewrite(methodDeclaration, methodDeclaration.getModifiersProperty())
						.insertFirst(newAnnotation, null);
			}
			solutionMap.put("Skip Rule Check", rewrite);

		}
		return solutionMap;
	}
	
	@Override
	public ICompilationUnit getICompilationUnit(){
		return null;
	}
	
	
	/**
	 * The URL for accessing the rule
	 * @return The URL for where the rule is defined
	 */
	public abstract String getRuleURL();
}
