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
	    for (float x = 0.1f; x <= 1.0f; x += 0.1f) {
	    	  System.out.println(x);
	    	}
	}
	
}
