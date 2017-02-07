package edu.csus.plugin.securecodingassistant.rules;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.TreeMap;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTMatcher;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Assignment.Operator;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;

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
class IDS00J_PreventSQLInjection extends SecureCodingRule {

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
			if (ruleViolated)
				ruleViolated = super.violated(node);
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
		return Globals.RuleNames.IDS00_J;
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

	@Override
	public String getRuleID() {
		return Globals.RuleID.IDS00_J;
	}
	
	@SuppressWarnings({ "unchecked" })
	@Override
	public TreeMap<String, ASTRewrite> getSolutions(ASTNode node) {
		
		TreeMap<String, ASTRewrite> list = new TreeMap<String, ASTRewrite>();
		list.putAll(super.getSolutions(node));
		
		try {
		//stmt.executeQuery(sqlString or null)
		MethodInvocation method = (MethodInvocation)node;
		AST ast = node.getAST();
			
		// if methodInvocation is stmt.executeQuery(sqlString) get variable
		// name and argument that called the executeQuery
		if ("executeQuery".equals(method.getName().getIdentifier()) && Statement.class.getCanonicalName()
				.equals(method.getExpression().resolveTypeBinding().getQualifiedName())) {

			ASTRewrite rewrite1 = ASTRewrite.create(ast);

			// get stmt: stmt.executeQuery(sqlString)
			String stmt = method.getExpression().toString();

			// set the arguments of stmt.executeQuery(sqlString) to null;
			MethodInvocation newStmtMethodInvocation = (MethodInvocation) ast.newMethodInvocation();
			newStmtMethodInvocation.setName(ast.newSimpleName("executeQuery"));
			newStmtMethodInvocation.setExpression(ast.newSimpleName(stmt));
			rewrite1.replace(method, newStmtMethodInvocation, null);

			// get sqlString: stmt.executeQuery(sqlString)
			String sqlString = method.arguments().get(0).toString();
			
			ASTNode astBlock = Utility.getEnclosingNode(method, Block.class);
			if (astBlock != null && astBlock instanceof Block) {
				Block block = (Block)astBlock;
				SecureCodingNodeVisitor visitor = new SecureCodingNodeVisitor();
				block.accept(visitor);
				
				// Change Statement stmt = connection.createStatement() to
				// PreparedStatement stmt = connection.prepareStatement(sqlString);
				Type oldType = visitor.getType(stmt);
				Type newType = ast.newSimpleType(ast.newName("PreparedStatement"));
				if (oldType != null)
					rewrite1.replace(oldType, newType, null);
				
				VariableDeclarationFragment stmtADF = visitor.getVariableDeclarationFragment(stmt);
				if (stmtADF != null && stmtADF.getInitializer() != null) {
					if (stmtADF.getInitializer() instanceof MethodInvocation) {
						MethodInvocation mi = (MethodInvocation)stmtADF.getInitializer();
						MethodInvocation stmtMI = ast.newMethodInvocation();
						stmtMI.setName(ast.newSimpleName("prepareStatement"));
						stmtMI.setExpression(ast.newSimpleName(mi.getExpression().toString()));
						stmtMI.arguments().add(ast.newSimpleName(sqlString));
						rewrite1.replace(mi, stmtMI, null);
					}
				} else {
					ArrayList<MethodInvocation> stmtMIs = visitor.getMethodInvocations("createStatement",
							Connection.class.getCanonicalName(), 0, new ArrayList<>());
					MethodInvocation stmtMI = ast.newMethodInvocation();
					stmtMI.setName(ast.newSimpleName("prepareStatement"));
					stmtMI.arguments().add(ast.newSimpleName(sqlString));
					for (MethodInvocation mi : stmtMIs) {
						if (stmt.equals(visitor.getVariable(mi))) {
							stmtMI.setExpression(ast.newSimpleName(mi.getExpression().toString()));
							rewrite1.replace(mi, stmtMI, null);
							break;
						}
					}
				}
				
			}
			list.put("Using PreparedStatement instead of Statement", rewrite1);

		} else {
			ASTRewrite rewrite2 = ASTRewrite.create(ast);

			// methodInvocation: stmt.executeQuery();
			org.eclipse.jdt.core.dom.Statement oldStmt = SecureCodingNodeVisitor.getStatement(method);
			String stmt = method.getExpression().toString();

			// insert setString(1, username); setString(2, password) before
			// stmt.executeQuery();
			MethodInvocation setString1 = ast.newMethodInvocation();
			setString1.arguments().add(ast.newNumberLiteral("1"));
			setString1.arguments().add(ast.newSimpleName("username"));
			setString1.setName(ast.newSimpleName("setString"));
			setString1.setExpression(ast.newSimpleName(stmt));
			ExpressionStatement expstmt1 = ast.newExpressionStatement(setString1);

			MethodInvocation setString2 = ast.newMethodInvocation();
			setString2.arguments().add(ast.newNumberLiteral("2"));
			setString2.arguments().add(ast.newSimpleName("password"));
			setString2.setName(ast.newSimpleName("setString"));
			setString2.setExpression(ast.newSimpleName(stmt));
			ExpressionStatement expstmt2 = ast.newExpressionStatement(setString2);

			ASTNode astBlock = Utility.getEnclosingNode(method, Block.class);
			if (astBlock != null && astBlock instanceof Block) {
				Block block = (Block) astBlock;
				ListRewrite listRewrite = rewrite2.getListRewrite(block, Block.STATEMENTS_PROPERTY);
				listRewrite.insertBefore(expstmt1, oldStmt, null);
				listRewrite.insertBefore(expstmt2, oldStmt, null);
				
				
				// Redefine String sqlString = "select * from db_user where
				// username=? and password=?";
				SecureCodingNodeVisitor visitor = new SecureCodingNodeVisitor();
				block.accept(visitor);
				
				// stmt = connection.prepareStatement(sqlString);
				ArrayList<String> arguTypes = new ArrayList<>();
				arguTypes.add(String.class.getCanonicalName());
				ArrayList<MethodInvocation> MIs = visitor.getMethodInvocations("prepareStatement",
						Connection.class.getCanonicalName(), 1, arguTypes);
				for (MethodInvocation mi: MIs) {
					if (stmt.equals(visitor.getVariable(mi))) {
						String sqlString = mi.arguments().get(0).toString();
						
						VariableDeclarationFragment sqlStringVDF = visitor.getVariableDeclarationFragment(sqlString);
						StringLiteral sl = ast.newStringLiteral();
							sl.setLiteralValue("select * from db_user where username=? and password=?");

							if (sqlStringVDF != null && sqlStringVDF.getInitializer() != null) {
								rewrite2.replace(sqlStringVDF.getInitializer(), sl, null);
							} else {
								Assignment sqlStringAssign = ast.newAssignment();
								sqlStringAssign.setLeftHandSide(ast.newSimpleName(sqlString));
								sqlStringAssign.setOperator(Operator.ASSIGN);
								sqlStringAssign.setRightHandSide(sl);
								ExpressionStatement expStmt = ast.newExpressionStatement(sqlStringAssign);
								ListRewrite listRewrite2 = rewrite2.getListRewrite(block, Block.STATEMENTS_PROPERTY);
								listRewrite2.insertBefore(expStmt, SecureCodingNodeVisitor.getStatement(mi), null);
							}
							break;
						}
					}
				}
				list.put("Using setString() to pass in user input", rewrite2);

			}
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}

		return list;
	}
}
