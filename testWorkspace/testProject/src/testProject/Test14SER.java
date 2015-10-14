package testProject;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

public class Test14SER implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Test SER01J
	// Make the following three methods anything except private
	// to generate an error
	private void writeObject(java.io.ObjectOutputStream out)
		    throws IOException {
		
	}
	private void readObject(java.io.ObjectInputStream in)
	    throws IOException, ClassNotFoundException {
		
	}
	@SuppressWarnings("unused")
	private void readObjectNoData()
	    throws ObjectStreamException {
		
	}
	
	// Make the following two methods static or anything
	// except protected to generate an error
	protected Object readResolve() {
		return null;
	}
	
	protected Object writeReplace() {
		return null;
	}
	
}
