package testProject;


public class Test06MET extends Super implements Cloneable {
	// Test MET04J
	// Make this method protected to avoid error
	public void doLogic() {
		
	}
	
	// Test MET06J
	// Make the doLogic() method private or final to make the error
	// go away
	public Object clone() throws CloneNotSupportedException {
		final Test06MET clone = (Test06MET) super.clone();
		clone.doLogic();
		return clone;
	}
}
