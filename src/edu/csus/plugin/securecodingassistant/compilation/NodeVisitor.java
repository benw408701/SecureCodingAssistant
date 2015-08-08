package edu.csus.plugin.securecodingassistant.compilation;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class NodeVisitor extends ASTVisitor {
	  List<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();
	  List<ExpressionStatement> expressionStatements = new ArrayList<ExpressionStatement>();

	  @Override
	  public boolean visit(ExpressionStatement node) {
		  expressionStatements.add(node);
		  return super.visit(node);
	  }

	  @Override
	  public boolean visit(MethodDeclaration node) {
		  methods.add(node);
		  return super.visit(node);
	  }
	  
	  public List<MethodDeclaration> getMethods() {
	    return methods;
	  }
	  
	  public List<ExpressionStatement> getExpressionStatements() {
		  return expressionStatements;
	  }
}
