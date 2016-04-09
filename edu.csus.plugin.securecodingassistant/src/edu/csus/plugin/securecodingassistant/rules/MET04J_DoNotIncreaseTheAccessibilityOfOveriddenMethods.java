package edu.csus.plugin.securecodingassistant.rules;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import edu.csus.plugin.securecodingassistant.Globals;

/**
 * <b><i>The text and/or code below is from the CERT website: <a target="_blank" href="https://www.securecoding.cert.org">https://www.securecoding.cert.org</a></i></b>
 * <p>
 * Java Secure Coding Rule: MET04-J. Do not increase the accessibility of overridden or
 * hidden methods
 * </p>
 * <p>
 * CERT Website: Increasing the accessibility of overridden or hidden methods permits a
 * malicious subclass to offer wider access to the restricted method than was originally
 * intended. Consequently, programs must override methods only when necessary and must
 * declare methods final whenever possible to prevent malicious subclassing. When methods
 * cannot be declared final, programs must refrain from increasing the accessibility of
 * overridden methods.
 * </p>
 * @author Ben White (Plugin Logic), CERT (Rule Definition)
 * @see Java Secure Coding Rule defined by CERT: <a target="_blank" href="https://www.securecoding.cert.org/confluence/display/java/MET04-J.+Do+not+increase+the+accessibility+of+overridden+or+hidden+methods">MET04-J</a>
 */
class MET04J_DoNotIncreaseTheAccessibilityOfOveriddenMethods implements IRule {

	@Override
	public boolean violated(ASTNode node) {
		boolean ruleViolated = false;
		
		// Is this a method declaration?
		if (node instanceof MethodDeclaration) {
			MethodDeclaration methodDec = (MethodDeclaration) node;
			// Is it overriding a method declared in the parent class?
			IMethodBinding parentMethod = Utility.getSuperClassDeclaration(methodDec.resolveBinding());
			// If there is a parent method and it isn't clone (the access level of clone is supposed
			// to be increased from protected to public)
			if (parentMethod != null && !parentMethod.getName().equals("clone"))
			{
				// Has the accessibility been increased?
				int parentAccessModifier = parentMethod.getModifiers() &
						(Modifier.PROTECTED | Modifier.PUBLIC);
				int accessModifier = methodDec.getModifiers() &
						(Modifier.PROTECTED | Modifier.PUBLIC);
				switch(parentAccessModifier) {
				case 0: // Package Private which is no modifier
					// Cannot increase to protected or public
					ruleViolated = (accessModifier & Modifier.PROTECTED) != 0
									|| (accessModifier & Modifier.PUBLIC) != 0;
					break;
				case Modifier.PROTECTED:
					// Cannot increase to public
					ruleViolated = (accessModifier & Modifier.PUBLIC) != 0;
					break;
				case Modifier.PUBLIC:
					// super class is already public, cannot increase visibility
					break;
				}
			}
		}
		
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		return "CERT Website-Increasing the accessibility of overridden or hidden "
				+ "methods permits a malicious subclass to offer wider access to "
				+ "the restricted method than was originally intended. Consequently, "
				+ "programs must override methods only when necessary and must declare "
				+ "methods final whenever possible to prevent malicious subclassing. "
				+ "When methods cannot be declared final, programs must refrain from "
				+ "increasing the accessibility of overridden methods.";
	}

	@Override
	public String getRuleName() {
		return "MET04-J. Do not increase the accessibility of overridden or hidden methods";
	}

	@Override
	public String getRuleRecommendation() {
		return "Do not increase the accessibility of overridden methods.";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_MEDIUM;
	}

}
