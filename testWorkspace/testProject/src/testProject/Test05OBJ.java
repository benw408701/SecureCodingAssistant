package testProject;

public class Test05OBJ {

	@SuppressWarnings({ "unused", "null" })
	public void test() {
		// Test OBJ09J
		Integer x = 1, y = 2;
		Object auth = null;
		// The following will generate a warning
		boolean r1 = x.getClass().getName().equals(y.getClass().getName());
		// The following will NOT generate a warning
		boolean r2 = x.getClass() == y.getClass();
		// The following will generate a warning
		boolean r3 = auth.getClass().getName().equals("com.application.auth.DefaultAuthenticationHandler");
		// The following will NOT generate a warning
		boolean r4 = auth.getClass() == Object.class; 
}
	
}
