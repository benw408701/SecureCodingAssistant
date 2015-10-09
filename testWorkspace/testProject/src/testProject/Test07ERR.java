package testProject;

public class Test07ERR {

	public void test() {
		// Test ERR08J
		String s = "test";
		try {
			String names[] = s.split(" ");
			
			if (names.length != 2) {
				;
			}
		} catch (NullPointerException e) {
			
		}
	}
	
}
