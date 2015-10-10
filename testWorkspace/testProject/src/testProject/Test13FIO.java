package testProject;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

public class Test13FIO {
	
	@SuppressWarnings({ "null", "unused" })
	public void test() throws IOException {
		
		// Test FIO08J
		FileInputStream in = null;
		// Initialize stream
		byte data;
		while ((data = (byte) in.read()) != -1) {
		  // ...
		}
		
		FileReader in2 = null;
		// Initialize stream
		char data2;
		while ((data2 = (char) in2.read()) != -1) {
		  // ...
		}
	}

}
