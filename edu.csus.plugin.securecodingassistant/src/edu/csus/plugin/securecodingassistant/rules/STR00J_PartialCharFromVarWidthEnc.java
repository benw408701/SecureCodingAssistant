package edu.csus.plugin.securecodingassistant.rules;

import java.io.InputStream;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.WhileStatement;

/**
 * Java Secure Coding Rule: STR00-J. Don't form strings containing partial characters from
 * variable-width encodings.
 * <p>
 * Programmers must not form strings containing partial characters, for example, when converting
 * variable-width encoded character data to strings. Avoid this by not building a text string until
 * confirmed that all data has been read from the buffer.
 * </p>
 * @author Ben White
 * @see <a target="_blank" href="https://www.securecoding.cert.org/confluence/display/java/STR00-J.+Don%27t+form+strings+containing+partial+characters+from+variable-width+encodings">Java Secure Coding Rule: STR00-J</a>
 */
public class STR00J_PartialCharFromVarWidthEnc implements IRule {

	@Override
	public boolean violated(ASTNode node) {
		boolean ruleViolated = false;
		
		// Is data being read from an input stream? Class = InputStream, method = read.
		if (node instanceof MethodInvocation
				&& Utility.calledMethod((MethodInvocation)node, InputStream.class.getCanonicalName(), "read")) {
			
			// check to see if in while loop
			Statement stmt = Utility.getEnclosingStatement(node, WhileStatement.class);
			if (stmt instanceof WhileStatement) {
				WhileStatement wStmt = (WhileStatement)stmt;
				MethodInvocation method = (MethodInvocation)node;
				
				// Capture identifier of byte buffer
				SimpleName idBuffer = method.arguments().size() < 1 ? null : (SimpleName) method.arguments().get(0);
				
				// If string is being constructed in the while loop then rule violated. Calling string constructor
				// with byte buffer as the parameter
				ruleViolated = Utility.containsInstanceCreation(wStmt.getBody(), String.class.getCanonicalName(), idBuffer);
			}		
		}

		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		return "Programmers must not form strings containing partial characters, for example, when "
				+ "converting variable-width encoded character data to strings.";
	}

	@Override
	public String getRuleName() {
		return "STR00-J. Don't form strings containing partial characters"
				+ " from variable-width encodings";
	}

	@Override
	public String getRuleRecommendation() {
		return "Defer building text string until all data has been read by the buffer";
	}

}
