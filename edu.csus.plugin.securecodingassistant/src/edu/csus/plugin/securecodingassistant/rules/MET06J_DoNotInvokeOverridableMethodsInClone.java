package edu.csus.plugin.securecodingassistant.rules;

import java.util.ArrayList;
import java.util.TreeMap;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * <b><i>The text and/or code below is from the CERT website: <a target="_blank" href="https://www.securecoding.cert.org">https://www.securecoding.cert.org</a></i></b>
 * <p>
 * Java Secure Coding Rule: MET06-J. Do not invoke overridable methods in clone()
 * </p>
 * <p>
 * CERT Website: Calling overridable methods from the <code>clone()</code> method
 * is insecure. First, a malicious subclass could override the method and affect
 * the behavior of the <code>clone()</code> method. Second, a trusted subclass
 * could observe (and potentially modify) the cloned object in a partially initialized
 * state before its construction has concluded. In either case, the subclass could
 * leave the clone, the object being cloned, or both in an inconsistent state.
 * Consequently, <code>clone()</code> methods may invoke only methods that are
 * <code>final</code> or <code>private</code>. 
 * </p>
 * @author Ben White (Plugin Logic), CERT (Rule Definition)
 * @see Java Secure Coding Rule defined by CERT: <a target="_blank" href="https://www.securecoding.cert.org/confluence/pages/viewpage.action?pageId=34668550">MET06-J</a>
 */
class MET06J_DoNotInvokeOverridableMethodsInClone extends SecureCodingRule {

	@Override
	public boolean violated(ASTNode node) {
		boolean ruleViolated = false;
		
		// Is this a MethodInvocation
		if (node instanceof MethodInvocation) {
			MethodInvocation method = (MethodInvocation)node;
			
			// Is this in a clone() method?
			ASTNode encNode = Utility.getEnclosingNode(node, MethodDeclaration.class);
			if (encNode != null && encNode instanceof MethodDeclaration) {
				MethodDeclaration methodDec = (MethodDeclaration) encNode;
				if (methodDec.resolveBinding().getName().equals("clone")) {
					// Verify that the class implements Cloneable
					boolean implementsCloneable = false;
					if (methodDec.resolveBinding() != null) {
						for (ITypeBinding typeBinding : methodDec.resolveBinding().getDeclaringClass().getInterfaces()) {
							if (typeBinding.getQualifiedName().equals(Cloneable.class.getCanonicalName())) {
								implementsCloneable = true;
								break;
							}
						}
					}
					
					// This is a method invocation that is in an overridden clone()
					ruleViolated = implementsCloneable &&
							(method.resolveMethodBinding().getModifiers() & Modifier.FINAL) == 0 &&
							(method.resolveMethodBinding().getModifiers() & Modifier.PRIVATE) == 0;
				}
				if (ruleViolated)
					ruleViolated = super.violated(node);
			}
		}
		
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		return "Calling overridable methods from the clone() method is insecure. "
				+ "First, a malicious subclass could override the method and affect "
				+ "the behavior of the clone() method. Second, a trusted subclass "
				+ "could observe (and potentially modify) the cloned object in a "
				+ "partially initialized state before its construction has concluded. "
				+ "In either case, the subclass could leave the clone, the object "
				+ "being cloned, or both in an inconsistent state. Consequently, "
				+ "clone() methods may invoke only methods that are final or private.";
	}

	@Override
	public String getRuleName() {
		return Globals.RuleNames.MET06_J;
	}

	@Override
	public String getRuleRecommendation() {
		return "When overridding the clone() method make sure that none of the methods "
				+ "that are called are also overriddable.";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_HIGH;
	}

	@Override
	public String getRuleURL() {
		return "https://www.securecoding.cert.org/confluence/pages/viewpage.action?pageId=34668550";
	}
	
	@Override
	public String getRuleID() {
		return Globals.RuleID.MET06_J;
	}


	@Override
	public TreeMap<String, ASTRewrite> getSolutions(ASTNode node) {

		TreeMap<String, ASTRewrite> list = new TreeMap<>();
		list.putAll(super.getSolutions(node));

		try {
			// get methodDeclaration and typeDeclaration
			ASTNode mdNode = Utility.getEnclosingNode(node, MethodDeclaration.class);
			ASTNode tdNode = Utility.getEnclosingNode(node, TypeDeclaration.class);
			if (mdNode != null && tdNode != null) {
				AST ast = node.getAST();
				ASTRewrite rewrite = ASTRewrite.create(ast);

				SecureCodingNodeVisitor visitor = new SecureCodingNodeVisitor();
				mdNode.accept(visitor);

				// get all the methodDeclaration in the class
				MethodDeclaration[] mds = ((TypeDeclaration) tdNode).getMethods();

				// find whether the method has been invoked in the clone method
				for (MethodDeclaration md : mds) {
					if ((md.getModifiers() & Modifier.FINAL) == 0 && (md.getModifiers() & Modifier.PRIVATE) == 0) {
						ArrayList<String> arguType = new ArrayList<>();
						for (Object obj : md.parameters()) {
							if (obj instanceof SingleVariableDeclaration) {
								SingleVariableDeclaration svd = (SingleVariableDeclaration) obj;
								arguType.add(svd.getType().toString());
							}
						}
						ArrayList<MethodInvocation> mis = visitor.getMethodInvocations(md.getName().getIdentifier(),
								md.resolveBinding().getDeclaringClass().getBinaryName(), md.parameters().size(),
								arguType);
						if (!mis.isEmpty()) {
							ListRewrite listRewrite = rewrite.getListRewrite(md, MethodDeclaration.MODIFIERS2_PROPERTY);
							listRewrite.insertLast(ast.newModifier(ModifierKeyword.FINAL_KEYWORD), null);
							list.put("Add final to the invoked method", rewrite);
						}
					}
				}

			}
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}

		return list;
	}
}
