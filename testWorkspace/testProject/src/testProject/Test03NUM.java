package testProject;

public class Test03NUM {

	public void test() {
		// Test NUM07J
	    double x = 0.0;
	    double result = Math.cos(1/x); // Returns NaN if input is infinity
	    if (result == Double.NaN) { // Comparison is always false!
	      System.out.println("result is NaN");
	    }
	    
	    // Test NUM09J
	    for (float x2 = 0.1f; x2 <= 1.0f; x2 += 0.1f) {
	    	  System.out.println(x2);
	    	}
	}
	
}
