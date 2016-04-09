package edu.csus.plugin.securecodingassistant.rules;

import java.sql.Statement;
import java.sql.PreparedStatement;

import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import edu.csus.plugin.securecodingassistant.Globals;

/**
 * <b><i>The text and/or code below is from the CERT website: <a target="_blank" href="https://www.securecoding.cert.org">https://www.securecoding.cert.org</a></i></b>
 * <p>
 * Java Secure Coding Rule: IDS00-J. Prevent SQL injection
 * </p>
 * <p>
 * CERT Website: SQL injection vulnerabilities arise in applications where elements of a SQL query originate
 * from an untrusted source. Without precautions, the untrusted data may maliciously alter
 * the query, resulting in information leaks or data modification. The primary means of preventing
 * SQL injection are sanitization and validation, which are typically implemented as parameterized
 * queries and stored procedures.
 * </p>
 * @author Ben White (Plugin Logic), CERT (Rule Definition)
 * @see Java Secure Coding Rule defined by CERT: <a target="_blank" href="https://www.securecoding.cert.org/confluence/display/java/IDS00-J.+Prevent+SQL+injection">IDS00-J</a>
 */
class IDS00J_PreventSQLInjection implements IRule {

	@Override
	public boolean violated(ASTNode node) {
		boolean ruleViolated = false;
		MethodInvocation method;
		
		// Detect use of Statement.ExecuteQuery or PreparedStatement.ExecuteQuery
		if (node instanceof MethodInvocation) {
			method = (MethodInvocation) node;
			// If PreparedStatement was used then make sure that setString() was
			// called at least once, if not then the rule is violated
			if (Utility.calledMethod(method, PreparedStatement.class.getCanonicalName(), "executeQuery"))
				ruleViolated = !Utility.calledPrior(method, PreparedStatement.class.getCanonicalName(), "setString");
			// If PreparedStatement was not used then see if Statement was used
			// and then return true if the string being used wasn't a literal string
			else if (Utility.calledMethod(method, Statement.class.getCanonicalName(), "executeQuery")) {
				// Rule is violated if the argument was created using a compound expression
				// (anything except for a literal string)
				// In this first case there is a variable being passed to executeQuery()
				if (method.arguments().size() > 0
						&& method.arguments().get(0) instanceof SimpleName) {
					// The argument in this case would be  the query string being passed to the database
					SimpleName argument = (SimpleName) method.arguments().get(0);
					ASTNode parent = node.getParent();

					// Look for assignments to the query string in parent nodes
					while (parent != null && !ruleViolated) {
						ASTNodeProcessor processor = new ASTNodeProcessor();
						parent.accept(processor);
						// Check all of the assignments
						for(NodeNumPair assignmentPair : processor.getAssignments()) {
							Assignment assignment = (Assignment)assignmentPair.getNode();
							// If the assignment is for the query string and the right hand side
							// is not a literal string then the rule has been violated
							if (assignment.getLeftHandSide().subtreeMatch(new ASTMatcher(), argument)
									&& !(assignment.getRightHandSide() instanceof StringLiteral)) {
								ruleViolated = true;
								break;
							}
						}
						// If the rule still isn't violated, check all of the declarations
						if (!ruleViolated) {
							for(NodeNumPair declarationPair : processor.getVariableDeclarationFragments()) {
								VariableDeclarationFragment declaration = (VariableDeclarationFragment) declarationPair.getNode();
								// If the variable being declared is the query string and the right hand
								// side is not a literal string then the rule has been violated
								if (declaration.getName().subtreeMatch(new ASTMatcher(), argument)
										&& !(declaration.getInitializer() instanceof StringLiteral)) {
									ruleViolated = true;
									break;
								}
							}
						}
						parent = parent.getParent();
					}
				}
				// In this case there is another type of argument being sent, the rule
				// is violated if that argument is anything except a string literal
				else
					// In this case the argument could have been a simple name
					ruleViolated = !(method.arguments().size() > 0
							&& method.arguments().get(0) instanceof StringLiteral);
			}
		}

		return ruleViolated;
	}

	@Override
	public String getRuleText() {
		return "CERT Website-SQL injection vulnerabilities arise in applications where elements of a SQL query "
				+ "originate from an untrusted source. Without precautions, the untrusted data may "
				+ "maliciously alter the query, resulting in information leaks or data modification. "
				+ "The primary means of preventing SQL injection are sanitization and validation, "
				+ "which are typically implemented as parameterized queries and stored procedures.";
	}

	@Override
	public String getRuleName() {
		return "IDS00-J. Prevent SQL injection";
	}

	@Override
	public String getRuleRecommendation() {
		return "Do not execute SQL queries with parameters directly, use a PreparedStatement and the "
				+ "setString() method to insert the parameters";
	}

	@Override
	public int securityLevel() {
		return Globals.Markers.SECURITY_LEVEL_HIGH;
	}
}
