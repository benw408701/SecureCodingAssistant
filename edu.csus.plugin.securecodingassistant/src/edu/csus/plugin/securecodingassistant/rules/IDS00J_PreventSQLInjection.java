package edu.csus.plugin.securecodingassistant.rules;

import java.sql.Statement;
import java.sql.PreparedStatement;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodInvocation;
import edu.csus.plugin.securecodingassistant.Globals;

/**
 * Java Secure Coding Rule: IDS00-J. Prevent SQL injection
 * <p>
 * CERT Website: SQL injection vulnerabilities arise in applications where elements of a SQL query originate
 * from an untrusted source. Without precautions, the untrusted data may maliciously alter
 * the query, resulting in information leaks or data modification. The primary means of preventing
 * SQL injection are sanitization and validation, which are typically implemented as parameterized
 * queries and stored procedures.
 * </p>
 * @author Ben White
 * @see <a target="_blank" href="https://www.securecoding.cert.org/confluence/display/java/IDS00-J.+Prevent+SQL+injection">Java Secure Coding Rule: IDS00-J</a>
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
			// and then always return true. PreparedStatement should always be used
			// instead of Statement
			else
				ruleViolated = Utility.calledMethod(method, Statement.class.getCanonicalName(), "executeQuery");
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
