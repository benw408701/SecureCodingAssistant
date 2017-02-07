package edu.csus.plugin.securecodingassistant.rules;

import java.security.SecureRandom;
import java.util.Random;
import java.util.TreeMap;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * <b><i>The text and/or code below is from the CERT website: <a target="_blank" href="https://www.securecoding.cert.org">https://www.securecoding.cert.org</a></i></b>
 * <p>
 * Java Secure Coding Rule: MSC02-J. Generate strong random numbers
 * </p>
 * <p>
 * CERT Website: Pseudorandom number generators (PRNGs) use deterministic mathematical algorithms 
 * to produce a sequence of numbers with good statistical properties. However, the 
 * sequences of numbers produced fail to achieve true randomness. PRNGs usually 
 * start with an arithmetic seed value. The algorithm uses this seed to generate 
 * an output value and a new seed, which is used to generate the next value, and so on.
 * </p>
 * <p>
 * The Java API provides a PRNG, the java.util.Random class. This PRNG is portable and 
 * repeatable. Consequently, two instances of the java.util.Random class that are 
 * created using the same seed will generate identical sequences of numbers in all 
 * Java implementations. Seed values are often reused on application initialization 
 * or after every system reboot. In other cases, the seed is derived from the current 
 * time obtained from the system clock. An attacker can learn the value of the seed 
 * by performing some reconnaissance on the vulnerable target and can then build a 
 * lookup table for estimating future seed values.
 * </p>
 * @author Ben White (Plugin Logic), CERT (Rule Definition)
 * @see Java Secure Coding Rule defined by CERT: <a target="_blank" href="https://www.securecoding.cert.org/confluence/display/java/MSC02-J.+Generate+strong+random+numbers">MSC02-J</a>
 */
class MSC02J_GenerateStrongRandomNumbers extends SecureCodingRule {

	@Override
	public boolean violated(ASTNode node) {
		boolean ruleViolated = false;
		// Check to see if a Random object is being created
		if (node instanceof ClassInstanceCreation) {
			ClassInstanceCreation instance = (ClassInstanceCreation) node;
			ruleViolated = instance.getType() != null &&
					instance.getType().resolveBinding() != null &&
					instance.getType().resolveBinding().getQualifiedName().equals(Random.class.getCanonicalName());
			if (ruleViolated)
				ruleViolated = super.violated(node);
		}
		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		return "CERT Website-The Java API provides a PRNG, the java.util.Random class. This PRNG is "
				+ "portable and repeatable. Consequently, two instances of the java.util."
				+ "Random class that are created using the same seed will generate "
				+ "identical sequences of numbers in all Java implementations. Seed "
				+ "values are often reused on application initialization or after every system "
				+ "reboot. In other cases, the seed is derived from the current time obtained"
				+ " from the system clock. An attacker can learn the value of the seed by "
				+ "performing some reconnaissance on the vulnerable target and can then "
				+ "build a lookup table for estimating future seed values.";
	}

	@Override
	public String getRuleName() {
		return Globals.RuleNames.MSC02_J;
	}

	@Override
	public String getRuleRecommendation() {
		return "java.util.Random is not a secure random number generator, use "
				+ "java.security.SecureRandom instead";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_HIGH;
	}

	@Override
	public String getRuleID() {
		return Globals.RuleID.MSC02_J;
	}
	
	@Override
	public TreeMap<String, ASTRewrite> getSolutions(ASTNode node) {

		TreeMap<String, ASTRewrite> list = new TreeMap<>();
		list.putAll(super.getSolutions(node));
		try {
			AST ast = node.getAST();
			ASTRewrite rewrite = ASTRewrite.create(ast);

			ClassInstanceCreation cic = (ClassInstanceCreation) node;
			ClassInstanceCreation newCic = ast.newClassInstanceCreation();
			newCic.setType(ast.newSimpleType(ast.newName(SecureRandom.class.getSimpleName())));

			rewrite.replace(cic, newCic, null);
			list.put("Use SecureRandom instead of Random", rewrite);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}

		return list;
	}

}
