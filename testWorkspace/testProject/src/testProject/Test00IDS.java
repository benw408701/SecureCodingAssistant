package testProject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test00IDS {
	@SuppressWarnings("unused")
	public void test(String username) throws Throwable {
		// Test IDS00J
		Connection connection = DriverManager.getConnection("testing");
		Statement stmt = connection.createStatement();
		PreparedStatement pStmt = connection.prepareStatement(null);
		pStmt.setString(1, "");
		String queryString = "SELECT * FROM tbl WHERE usr = " + username;
		
		// Uncomment rs2 or comment pstmt to test rule
		ResultSet rs = pStmt.executeQuery("testing");
		ResultSet rs2 = stmt.executeQuery("testing2");
		ResultSet rs3 = stmt.executeQuery(queryString);


		
		// Test IDS01J
		String s = "\uFE64" + "script" + "\uFE65";

		// Validate
		Pattern pattern = Pattern.compile("[<>]");
		Matcher matcher = pattern.matcher(s);
		

		// Comment out to remove error
		s = Normalizer.normalize(s, Form.NFKD);
		
		
		
		


		// Test IDS07J
		String dir = System.getProperty("dir");
		Runtime rt = Runtime.getRuntime();

		// Comment out to remove warning
		Process proc = rt.exec("cmd.exe /C dir " + dir);
		

		
		

		// Test IDS11J
		String s2 = "<scr!ipt>";
		s2 = Normalizer.normalize(s2, Form.NFKC);

		// Look for script tag
		pattern = Pattern.compile("<script>");
		matcher = pattern.matcher(s2);
		if (matcher.find())
			throw new IllegalArgumentException("Invalid Input");
		
		// Delete non-character code points  (move to end to generate warning)
		s2 = s2.replaceAll("[\\p{Cn}]","");
	}
}
