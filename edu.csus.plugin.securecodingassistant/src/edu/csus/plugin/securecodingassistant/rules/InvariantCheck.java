package edu.csus.plugin.securecodingassistant.rules;

import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;

import edu.csus.plugin.securecodingassistant.Globals;

public class InvariantCheck extends DesignByContractRule{

	@Override
	public boolean violated(ASTNode node) {
		
		// Is node a type declaration?
		if (!(node instanceof TypeDeclaration))
			return false;
		
		TypeDeclaration typeDeclaration = (TypeDeclaration) node;
		// if not public class, invariantCheck is not mandatory
		if (!isPublic(typeDeclaration))
			return false;

		// get all variable name and all variable store in Invariant and SkipInvariant Check
		HashSet<String> variableNames = getAllVariables(typeDeclaration);
		HashSet<String> variableInInvariantCheck = getAllVariableInInvariant(typeDeclaration);
		HashSet<String> variableInSkipInvariantCheck = getAllVariableInSkipInvariant(typeDeclaration);
		
		// return true if no variable and no invariant check, or has
		// variable and match with invariant/skip invariant check
		if (variableNames.isEmpty() && variableInInvariantCheck.isEmpty() && variableInSkipInvariantCheck.isEmpty()) {
			return false;
		} else if (!variableNames.isEmpty()
				&& (!variableInInvariantCheck.isEmpty() || !variableInSkipInvariantCheck.isEmpty())) {
			// number of variable doesn't match with the sum of the number of
			// variable in Invariant/SkipInvariant Check
			if (variableNames.size() != (variableInInvariantCheck.size() + variableInSkipInvariantCheck.size())) {
				return true;
			} else {
				for (String s : variableNames) {
					if (!variableInInvariantCheck.contains(s) && !variableInSkipInvariantCheck.contains(s)){
						return true;
					}
				}
				return false;
			}
		} else
			return true;
	}

	@Override
	public String getRuleText() {
		return "Variables should have invariant check";
	}

	@Override
	public String getRuleName() {
		return Globals.RuleNames.INVARIANT_CHECK;
	}

	@Override
	public String getRuleRecommendation() {
		final String res = String.format("Add invariant check @%s({\"\"}) or skip invariant check @%s", Globals.Annotations.INVARIANT, Globals.Annotations.SKIP_INVARIANT_CHECK);
		return res;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TreeMap<String, ASTRewrite> getSolutions(ASTNode node) {
		if (!(node instanceof TypeDeclaration))
			throw new IllegalArgumentException("Expected TypeDeclaration, but " + node.getAST());
		
		// Solution list;
		TreeMap<String, ASTRewrite> map = new TreeMap<String, ASTRewrite>();

		TypeDeclaration typeDeclaration = (TypeDeclaration) node;
		AST ast = node.getAST();
		// get all variable name and all variable store in Invariant and SkipInvariant Check
		HashSet<String> variableNames = getAllVariables(typeDeclaration);
		HashSet<String> variableInInvariantCheck = getAllVariableInInvariant(typeDeclaration);
		HashSet<String> variableInSkipInvariantCheck = getAllVariableInSkipInvariant(typeDeclaration);
		
		//find the nodes of @Invariant and @SkipInvariant
		SingleMemberAnnotation invariant = null;
		SingleMemberAnnotation skipInvariant = null;
		List<?> modifiers = typeDeclaration.modifiers();
		for (Object object : modifiers) {
			if (object instanceof SingleMemberAnnotation) {
				SingleMemberAnnotation annotation = (SingleMemberAnnotation) object;
				String name = annotation.getTypeName().getFullyQualifiedName();
				if (name.equals(Globals.Annotations.INVARIANT)) {
					invariant = annotation;
				} else if (name.equals(Globals.Annotations.SKIP_INVARIANT_CHECK)) {
					skipInvariant = annotation;
				}
			}
		}
		
		//Case 1: no declared variable but has variable in @Invariant or @SkipInvariant, delete the annotation
		if (variableNames.isEmpty() && (invariant!= null || skipInvariant != null)) {
			ASTRewrite rewrite1 = ASTRewrite.create(ast);
			if (invariant != null) {
				rewrite1.remove(invariant, null);
			}
			if (skipInvariant != null)
				rewrite1.remove(skipInvariant, null);
			map.put("Element in @Invariant or @SkipInvariant is not defined, remove unused annotation", rewrite1);
		}
		
		//Case 2: some variable in @Invariant or @skipInvariant is not defined, delete those variable
		HashSet<String> undefinedVariableExistInSkipInvariantCheck = new HashSet<>(variableInSkipInvariantCheck);
		HashSet<String> undefinedVariableExistInInvariantCheck = new HashSet<>(variableInInvariantCheck);
		undefinedVariableExistInSkipInvariantCheck.removeAll(variableNames);
		undefinedVariableExistInInvariantCheck.removeAll(variableNames);
		if (!undefinedVariableExistInSkipInvariantCheck.isEmpty() || !undefinedVariableExistInInvariantCheck.isEmpty()) {
			ASTRewrite rewrite2 = ASTRewrite.create(ast);
			if (!undefinedVariableExistInInvariantCheck.isEmpty()) {
				ArrayInitializer array = (ArrayInitializer)invariant.getValue();
				for (Object obj: array.expressions()) {
					if (obj instanceof StringLiteral) {
						StringLiteral sl = (StringLiteral) obj;
						String slValue = sl.getLiteralValue();
						StringTokenizer st = new StringTokenizer(slValue, " .<>=!(");
						if (undefinedVariableExistInInvariantCheck.contains(st.nextToken())) {
							rewrite2.remove(sl, null);
						}
					}
				}
			} 
			if (!undefinedVariableExistInSkipInvariantCheck.isEmpty()) {
				ArrayInitializer array = (ArrayInitializer)skipInvariant.getValue();
				for (Object obj: array.expressions()) {
					if (obj instanceof StringLiteral) {
						StringLiteral sl = (StringLiteral) obj;
						if (undefinedVariableExistInSkipInvariantCheck.contains(sl.getLiteralValue())) {
							rewrite2.remove(sl, null);
						}
					}
				}
			}
			map.put("Remove undefied variable from annotation", rewrite2);
		}
		
		//Case 3: some variable is not included in either @Invariant or @SkipInvariant
		HashSet<String> unCheckedVariable = new HashSet<>(variableNames);
		unCheckedVariable.removeAll(variableInInvariantCheck);
		unCheckedVariable.removeAll(variableInSkipInvariantCheck);
		if (!unCheckedVariable.isEmpty()) {
			// Add those variable to @Invariant
			ASTRewrite rewrite3 = ASTRewrite.create(ast);
			if (invariant == null) {
				invariant = ast.newSingleMemberAnnotation();
				invariant.setTypeName(ast.newName(Globals.Annotations.INVARIANT));
				ArrayInitializer array = ast.newArrayInitializer();
				for (String s : unCheckedVariable) {
					StringLiteral sl = ast.newStringLiteral();
					sl.setLiteralValue(s);
					array.expressions().add(sl);
				}
				invariant.setValue(array);
				rewrite3.getListRewrite(typeDeclaration, typeDeclaration.getModifiersProperty()).insertFirst(invariant,
						null);
			} else {
				ListRewrite listRewrite3 = rewrite3.getListRewrite((ArrayInitializer) invariant.getValue(),
						ArrayInitializer.EXPRESSIONS_PROPERTY);
				for (String s : unCheckedVariable) {
					StringLiteral sl = ast.newStringLiteral();
					sl.setLiteralValue(s);
					listRewrite3.insertLast(sl, null);
				}
			}
			map.put("Add unchecked variables to @Invariant", rewrite3);
			
			// Add those variable to @SkipInvariant
			ASTRewrite rewrite4 = ASTRewrite.create(ast);
			if (skipInvariant == null) {
				skipInvariant = ast.newSingleMemberAnnotation();
				skipInvariant.setTypeName(ast.newName(Globals.Annotations.SKIP_INVARIANT_CHECK));
				ArrayInitializer array = ast.newArrayInitializer();
				for (String s : unCheckedVariable) {
					StringLiteral sl = ast.newStringLiteral();
					sl.setLiteralValue(s);
					array.expressions().add(sl);
				}
				skipInvariant.setValue(array);
				rewrite4.getListRewrite(typeDeclaration, typeDeclaration.getModifiersProperty())
						.insertFirst(skipInvariant, null);
			} else {
				ListRewrite listRewrite4 = rewrite4.getListRewrite((ArrayInitializer) skipInvariant.getValue(),
						ArrayInitializer.EXPRESSIONS_PROPERTY);
				for (String s : unCheckedVariable) {
					StringLiteral sl = ast.newStringLiteral();
					sl.setLiteralValue(s);
					listRewrite4.insertLast(sl, null);
				}
			}
			map.put("Add unchecked variables to @SkipInvariant", rewrite4);

		}
		
		return map;
	}
	
	@Override
	public String getRuleID() {
		return Globals.RuleID.INVARIANT;
	}

	private HashSet<String> getAllVariables(TypeDeclaration typeDeclaration){
		HashSet<String> variableNames = new HashSet<>();
		
		// get all FieldDeclaration
		FieldDeclaration[] fieldDeclarations = typeDeclaration.getFields();

		// get all variable name
		for (FieldDeclaration fd : fieldDeclarations) {
			for (Object obj : fd.fragments()) {
				if (obj instanceof VariableDeclarationFragment) {
					VariableDeclarationFragment vdf = (VariableDeclarationFragment) obj;
					variableNames.add(vdf.getName().getFullyQualifiedName());
				}
			}
		}
		return variableNames;
	}
	
	private HashSet<String> getAllVariableInInvariant(TypeDeclaration typeDeclaration){
		HashSet<String> variableInInvariantCheck = new HashSet<>();
		List<?> modifiers = typeDeclaration.modifiers();
		for (Object object : modifiers) {
			if (object instanceof SingleMemberAnnotation) {
				SingleMemberAnnotation annotation = (SingleMemberAnnotation) object;
				String name = annotation.getTypeName().getFullyQualifiedName();
				if (name.equals(Globals.Annotations.INVARIANT)) {
					ArrayInitializer array = (ArrayInitializer) annotation.getValue();
					for (Object obj : array.expressions()) {
						if (obj instanceof StringLiteral) {
							StringLiteral sl = (StringLiteral) obj;
							String value = sl.getLiteralValue();
							StringTokenizer defaultTokenizer = new StringTokenizer(value, ".( =><!");
							String variableName = "";
							if (defaultTokenizer.hasMoreTokens())
								variableName = defaultTokenizer.nextToken();
							variableInInvariantCheck.add(variableName);
						}
					}
				}  
			}
		}
		return variableInInvariantCheck;
	}
	
	private HashSet<String> getAllVariableInSkipInvariant(TypeDeclaration typeDeclaration) {
		HashSet<String> variableInSkipInvariantCheck = new HashSet<>();
		List<?> modifiers = typeDeclaration.modifiers();
		for (Object object : modifiers) {
			if (object instanceof SingleMemberAnnotation) {
				SingleMemberAnnotation annotation = (SingleMemberAnnotation) object;
				String name = annotation.getTypeName().getFullyQualifiedName();
				if (name.equals(Globals.Annotations.SKIP_INVARIANT_CHECK)) {
					ArrayInitializer array = (ArrayInitializer) annotation.getValue();
					for (Object obj : array.expressions()) {
						if (obj instanceof StringLiteral) {
							StringLiteral sl = (StringLiteral) obj;
							variableInSkipInvariantCheck.add(sl.getLiteralValue());
						}
					}
				} 
			}
		}
		return variableInSkipInvariantCheck;
	}
	
}
