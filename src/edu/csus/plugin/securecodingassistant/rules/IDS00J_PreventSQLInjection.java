package edu.csus.plugin.securecodingassistant.rules;

import org.eclipse.jdt.core.dom.ASTNode;

/**
 * Java Secure Coding Rule: IDS00-J. Prevent SQL Injection
 * <p>
 * SQL injection vulnerabilities arise in applications where elements of a SQL query originate
 * from an untrusted source. Without precautions, the untrusted data may maliciously alter
 * the query, resulting in information leaks or data modification. The primary means of preventing
 * SQL injection are sanitization and validation, which are typically implemented as parameterized
 * queries and stored procedures.
 * </p>
 * @author Ben White
 * @see <a href="https://www.securecoding.cert.org/confluence/display/java/IDS00-J.+Prevent+SQL+injection">Java Secure Coding Rule: IDS00-J</a>
 */
public class IDS00J_PreventSQLInjection implements IRule {

	@Override
	public boolean violated(ASTNode node) {
		// TODO Detect use of Statement.ExecuteQuery or PreparedStatement.ExecuteQuery
		
		
		// TODO If Statement was used then always warn since they should have used a PreparedStatement
		
		
		// TODO If PreparedStatement was used then make sure that setString() was called at least once
		return false;
	}

	@Override
	public String getRuleText() {
		return "SQL injection vulnerabilities arise in applications where elements of a SQL query "
				+ "originate from an untrusted source. Without precautions, the untrusted data may "
				+ "maliciously alter the query, resulting in information leaks or data modification. "
				+ "The primary means of preventing SQL injection are sanitization and validation, "
				+ "which are typically implemented as parameterized queries and stored procedures.";
	}

	@Override
	public String getRuleName() {
		return "IDS00-J. Prevent SQL Injection";
	}

	@Override
	public String getRuleRecommendation() {
		// TODO Auto-generated method stub
		return "Do not execute SQL queries with parameters directly, use a PreparedStatement and the "
				+ "setString() method to insert the parameters";
	}

}
