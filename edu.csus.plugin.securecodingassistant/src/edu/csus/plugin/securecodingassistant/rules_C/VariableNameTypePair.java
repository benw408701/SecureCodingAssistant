package edu.csus.plugin.securecodingassistant.rules_C;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.c.ICASTCompositeTypeSpecifier;

/**
 * Creates a collection of all the variables within
 * a translation unit.
 * 
 * @author Victor Melnik
 * 
 * @see ASTNodeProcessor_C
 */

public class VariableNameTypePair {

	/**
	 * Variable Name
	 * 
	 */
	private String m_varName;
	
	/**
	 * Variable Type
	 * 
	 */
	private String m_varType;
	
	/**
	 * Original declarator passed into class
	 * 
	 */
	private IASTDeclarator originalDeclarator;
	
	/**
	 * Original variable declaration node
	 * 
	 */
	private IASTNode originalNode;
	
	/**
	 * Check if variable is a pointer
	 * 
	 */
	private boolean isPointer_T;
	/**
	 * Check if variable is array
	 * 
	 */
	private boolean isArray_T;
	
	/**
	 *Variable Scope 
	 * 
	 */
	private IASTNode m_scope;
	
	/**
	 * Check if Variable is restrict type
	 * 
	 */
	
	private boolean isRestrictType;
	
	/**
	 * Check if Variable is const type
	 * 
	 */
	
	private boolean isConstType;
	
	/**
	 * Check if Variable is volatile type
	 * 
	 */
	
	private boolean isVolatileType;
	
	
	/**
	 * Check if Variable is atomic type
	 * 
	 */
	
	private boolean isAtomicType;
	
	/**
	 * Check if Variable is static type
	 * 
	 */
	
	private boolean isStaticType;
	
	/**
	 * Check if Variable is element in Struct or Union
	 * 
	 */
	
	private boolean isElement;
	
	/**
	 * Construct new NameTypeNode pair for a Variable declaration
	 * 
	 * @param node
	 */
	public VariableNameTypePair (IASTDeclarator VarName, IASTDeclSpecifier VarType, IASTNode node) {
		isPointer_T = false;
		isArray_T = false;
		isRestrictType = false;
		isConstType = false;
		isVolatileType = false;
		isAtomicType = false;
		isStaticType = false;
		
		originalDeclarator = VarName;
		originalNode = node;
		m_varType = getVarTypeProperties(VarType);
		m_varName = getVarNameString(VarName);
		
		isElement(node);
		m_scope = findScope(node);
		
	}
	
	
	
	/**
	 * Get Declaration Variable Name
	 * @param astN
	 * @return String
	 */
	private String getVarNameString(IASTDeclarator astN)
	{
		String str = null;
		
		str = astN.getName().getRawSignature();
		
		if(astN.getRawSignature().contains("**"))
		{
			str = astN.getChildren()[2].getRawSignature();
			isPointer_T = true;
		}
		else if(astN.getRawSignature().contains("*"))
		{
			str = astN.getChildren()[1].getRawSignature();
			isPointer_T = true;
		}
		
		
		if(astN.getRawSignature().contains("[") && astN.getRawSignature().contains("]"))
		{
			isArray_T = true;
		}
		
		if(astN.getRawSignature().contains("restrict"))
		{
			str = astN.getChildren()[1].getRawSignature();
			isRestrictType = true;
		}
		return str;
	}
	
	/**
	 * Get properties of variable type
	 * @param astN
	 * @return String
	 */
	private String getVarTypeProperties(IASTDeclSpecifier astN)
	{
		String str = astN.getRawSignature();
		
		
		if(astN.isConst())
		{
			isConstType = true;
		}
		
		if(astN.isVolatile())
		{
			isVolatileType = true;
		}
		
		if(astN.getRawSignature().contains("atomic"))
		{
			isAtomicType = true;
		}
		
		if(astN.getRawSignature().contains("static"))
		{
			isStaticType = true;
		}
		
		return str;
	}
	
	
	/**
	 * Returns true if variable is element in structure or union
	 * @param IASTNode astN
	 * @return boolean
	 */
	private void isElement(IASTNode astN)
	{
		
		if(astN.getParent() instanceof ICASTCompositeTypeSpecifier)
		{
			//System.out.println("ICASTCompositeTypeSpecifierICASTCompositeTypeSpecifier: ");
			isElement = true;
			
		}
		else 
		{
			isElement = false;
		}
	}
	
	
	/**
	 * Return scope for given variable
	 * @param IASTNode
	 * @return IASTNode
	 */
	private IASTNode findScope(IASTNode astN)
	{
		
		return Utility_C.getScope(astN);
	}
	
	
	/**
	 *  Get Variable original declarator
	 * @return IASTDeclarator
	 */
	public IASTDeclarator getDeclarator()
	{
		return originalDeclarator;
	}
	
	
	/**
	 * Get original IASTDeclaration Node
	 * @return IASTNode
	 */
	public IASTNode getNode()
	{
		return originalNode;
	}
	
	/**
	 * Get variable name
	 * @return String
	 */
	public String getVarName()
	{
		return m_varName;
	}
	
	/**
	 * Get variable type
	 * @return String
	 */
	public String getVarType()
	{
		return m_varType;
	}
	
	/**
	 * Get scope of variable
	 * @return IASTNode
	 */
	public IASTNode getScope()
	{
		return m_scope;
	}
	
	/**
	 * Returns if variable is a pointer
	 * @return boolean
	 */
	public boolean isPointer()
	{
		return isPointer_T;
	}
	
	
	/**
	 * Returns if variable is a element in struct/union
	 * @return boolean
	 */
	public boolean isElement()
	{
		return isElement;
	}
	
	/**
	 * Returns if variable is a array
	 * @return boolean
	 */
	public boolean isArray()
	{
		return isArray_T;
	}
	
	/**
	 * Returns if variable is restrict type
	 * @return boolean
	 */
	public boolean isRestrict()
	{
		return isRestrictType;
	}
	
	/**
	 * Returns if variable is const type
	 * @return boolean
	 */
	public boolean isConst()
	{
		return isConstType;
	}
	
	/**
	 * Returns if variable is volatile type
	 * @return boolean
	 */
	public boolean isVolatile()
	{
		return isVolatileType;
	}
	
	/**
	 * Returns if variable is atomic type
	 * @return boolean
	 */
	public boolean isAtomic()
	{
		return isAtomicType;
	}
	
	/**
	 * Return if variable is static type
	 * @return boolean
	 */
	public boolean isStatic()
	{
		return isStaticType;
	}
	
}
