package testProject;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class Test04STR {
	@SuppressWarnings("unused")
	public void test() throws Throwable {
		
		
		
		
		
		
		// Test STR00J
		final int MAX_SIZE = 1024;
		@SuppressWarnings("resource")
		Socket socket = new Socket();
		InputStream in = socket.getInputStream();
		byte[] data = new byte[MAX_SIZE + 1];
		int offset = 0; 
		int bytesRead = 0;
		String str = new String();
		while ((bytesRead = in.read(data, offset, data.length - offset)) != -1) {
			offset += bytesRead;
			// Uncomment this line to generate warning
			str += new String(data, offset, data.length - offset, "UTF-8");
			if (offset >= data.length) {
				throw new IOException("Too much input");
			}
		}
		// This is the proper way to do it
		String str2 = new String(data, "UTF-8");
		in.close();
		
		
	}
}
