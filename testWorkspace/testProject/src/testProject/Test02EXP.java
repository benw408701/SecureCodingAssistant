package testProject;

import java.util.Arrays;

public class Test02EXP {

	@SuppressWarnings("unused")
	public void test() {
		
		// Test EXP00J
		String s;
		int i = 3;
		
		// Remove assignment to generate warning
		String.valueOf(i);
		
		
		
		// Test EXP02J
		int[] test = new int[20];
		int[] test2 = new int[30];
		
		Arrays.equals(test, test2);
		
		test.equals(test2);
		
	}
	
}
