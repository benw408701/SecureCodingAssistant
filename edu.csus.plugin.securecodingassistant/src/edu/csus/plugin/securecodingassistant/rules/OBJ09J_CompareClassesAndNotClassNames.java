package edu.csus.plugin.securecodingassistant.rules;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import edu.csus.plugin.securecodingassistant.Globals;

/**
 * <b><i>The text and/or code below is from the CERT website: <a target="_blank" href="https://www.securecoding.cert.org">https://www.securecoding.cert.org</a></i></b>
 * <p>
 * Java Secure Coding Rule: OBJ09-J. Compare classes and not class names
 * </p>
 * <p>
 * CERT Website: In a Java Virtual Machine (JVM), "Two classes are the same class (and
 * consequently the same type) if they are loaded by the same class loader and they
 * have the same fully qualified name" [JVMSpec 1999]. Two classes with the same name
 * but different package names are distinct, as are two classes with the same fully
 * qualified name loaded by different class loaders.
 * </p>
 * <p>
 * It could be necessary to check whether a given object has a specific class type or
 * whether two objects have the same class type associated with them, for example, when
 * implementing the <code>equals()</code> method. If the comparison is performed
 * incorrectly, the code could assume that the two objects are of the same class when
 * they are not. As a result, class names must not be compared.
 * </p>
 * <p>
 * Depending on the function that the insecure code performs, it could be vulnerable to
 * a mix-and-match attack. An attacker could supply a malicious class with the same
 * fully qualified name as the target class. If access to a protected resource is
 * granted based on the comparison of class names alone, the unprivileged class could
 * gain unwarranted access to the resource.
 * </p>
 * <p>
 * Conversely, the assumption that two classes deriving from the same codebase are the
 * same is error prone. Although this assumption is commonly observed to be true in
 * desktop applications, it is typically not the case with J2EE servlet containers.
 * The containers can use different class loader instances to deploy and recall
 * applications at runtime without having to restart the JVM. In such situations,
 * two objects whose classes come from the same codebase could appear to the JVM
 * to be two different classes. Also note that the <code>equals()</code> method might
 * not return true when comparing objects originating from the same codebase.
 * </p>
 * @author Ben White (Plugin Logic), CERT (Rule Definition)
 * @see Java Secure Coding Rule defined by CERT: <a target="_blank" href="https://www.securecoding.cert.org/confluence/display/java/OBJ09-J.+Compare+classes+and+not+class+names">OBJ09-J</a>
 */
class OBJ09J_CompareClassesAndNotClassNames implements IRule {

	@Override
	public boolean violated(ASTNode node) {
		boolean ruleViolated = false;
		
		// Is node a method invocation
		if (node instanceof MethodInvocation) {
			MethodInvocation method = (MethodInvocation) node;
			
			// Check to see if String.equals() is called
			if (Utility.calledMethod(method, String.class.getCanonicalName(), "equals")) {
				// Check to see if the expression is another method invocation
				Expression exp = method.getExpression();
				if (exp != null && exp instanceof MethodInvocation) {
					MethodInvocation outerMethod = (MethodInvocation)exp;
					// Rule is violated if the outer method was Class.getName
					ruleViolated = Utility.calledMethod(outerMethod, Class.class.getCanonicalName(), "getName");
				}
			}
		}
		

		
		int.class.getName().equals(Integer.class.getName());
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		return "When comparing two classes for equality make sure that the calsses "
				+ "themselves are being compared and not the class names.";
	}

	@Override
	public String getRuleName() {
		return "OBJ09-J. Compare classes and not class names";
	}

	@Override
	public String getRuleRecommendation() {
		return "Rather than obj.getClass().getName().equals(...), use "
				+ "obj.getClass() == obj2.getClass()";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_MEDIUM;
	}

}
