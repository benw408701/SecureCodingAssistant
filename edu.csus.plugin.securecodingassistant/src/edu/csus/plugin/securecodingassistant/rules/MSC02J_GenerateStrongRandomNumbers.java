package edu.csus.plugin.securecodingassistant.rules;

import java.util.Random;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * Java Secure Coding Rule: MSC02-J. Generate strong random numbers
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
 * @author Ben White
 * @see <a target="_blank" href="https://www.securecoding.cert.org/confluence/display/java/MSC02-J.+Generate+strong+random+numbers">MSC02-J. Generate strong random numbers</a>
 */
public class MSC02J_GenerateStrongRandomNumbers implements IRule {

	@Override
	public boolean violated(ASTNode node) {
		return node instanceof ClassInstanceCreation &&
				((ClassInstanceCreation)node).resolveTypeBinding().getQualifiedName().equals(Random.class.getCanonicalName());
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
		return "MSC02-J. Generate strong random numbers";
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

}
