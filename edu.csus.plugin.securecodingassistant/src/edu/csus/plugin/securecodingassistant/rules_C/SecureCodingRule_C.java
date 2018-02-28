package edu.csus.plugin.securecodingassistant.rules_C;

import java.util.TreeMap;

import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode;

//Depreciated 
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;

public abstract class SecureCodingRule_C implements IRule_C {

	/*
	//JDT function. violated
	@Override
	public boolean violated(ASTNode node) {
		// TODO Auto-generated method stub
		return false;
	}
	*/
	
	@Override
	public boolean violate_CDT(IASTNode node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getRuleText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRuleName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRuleID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRuleRecommendation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int securityLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ITranslationUnit getITranslationUnit() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	//JDT Function that must be implemented
	@Override
	public TreeMap<String, org.eclipse.jdt.core.dom.rewrite.ASTRewrite> getSolutions(ASTNode node) {
		// TODO Auto-generated method stub
		return null;
	}
	*/
	
	/*
	@Override
	public TreeMap<String, ASTRewrite> getSolutions(ASTNode node) {
		// TODO Auto-generated method stub
		return null;
	}
	
	*/
	
	/*
	@Override
	public ICompilationUnit getICompilationUnit() {
		// TODO Auto-generated method stub
		return null;
	}

	*/
	
	/*
	@Override
	public ICompilationUnit getICompilationUnit() {
		// TODO Auto-generated method stub
		return null;
	}
	*/
	public abstract String getRuleURL();

}
