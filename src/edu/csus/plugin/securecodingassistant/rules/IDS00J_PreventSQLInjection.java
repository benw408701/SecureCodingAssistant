package edu.csus.plugin.securecodingassistant.rules;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodInvocation;

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
		NodeType nType;
		boolean ruleViolated;
		MethodInvocation method;
		
		ruleViolated = false;
		
		// Detect use of Statement.ExecuteQuery or PreparedStatement.ExecuteQuery
		nType = NodeType.OTHER;
		if (node instanceof MethodInvocation) {
			method = (MethodInvocation) node;
			if (Utility.calledMethod(method, "Statement", "executeQuery"))
				nType = NodeType.STATEMENT;
			else if (Utility.calledMethod(method, "PreparedStatement", "executeQuery"))
				nType = NodeType.PREPARED_STATEMENT;
		}
		

		switch (nType) {
			// If Statement was used then always warn since they should have used a
			// PreparedStatement
			case STATEMENT:
				ruleViolated = true;
				break;
				
			// If PreparedStatement was used then make sure that setString() was
			// called at least once
			case PREPARED_STATEMENT:
				method = (MethodInvocation) node; // legal since tested in if statement above
				
				break;
				
			case OTHER:
			default:
				break;
		}

		return ruleViolated;
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
	
	private enum NodeType {
		STATEMENT, PREPARED_STATEMENT, OTHER
	}
}
