package testProject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;

public class FalsePositiveTest {

	@SuppressWarnings({ "null", "unused" })
	public void testSQL() throws SQLException {

		Connection conn;
		DataSource pool = null;
		Statement stmt;
		ResultSet rs;
		boolean loggedIn;
		String username = null, password = null;
		
		conn = pool.getConnection( );
		String sql = "select * from user where username='" + username +"' and password='" + password + "'";
		stmt = conn.createStatement();
		rs = stmt.executeQuery(sql);
		if (rs.next()) {
		loggedIn = true;
			System.out.println("Successfully logged in");
		} else {
			System.out.println("Username and/or password not recognized");
		}
		
		
		// From cwe
		/*
		String path = getInputPath();
		if (path.startsWith("/safe_dir/"))
		{
		File f = new File(path);
		return f.getCanonicalPath();
		}
		
		String str1 = new String("Hello");
		String str2 = new String("Hello");
		if (str1 == str2) {
		System.out.println("str1 == str2");
		}
		
		public boolean equals(Object obj) {
			boolean isEquals = false;

			// first check to see if the object is of the same class
			if (obj.getClass().getName().equals(this.getClass().getName())) {
			*/


	}
}

