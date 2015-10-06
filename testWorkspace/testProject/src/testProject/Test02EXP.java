package testProject;

public class Test02EXP {

	@SuppressWarnings("unused")
	public void test() {
		
		// Test EXP00J
		String s;
		int i = 3;
		
		// Remove assignment to generate warning
		s = String.valueOf(i);
	}
	
}
