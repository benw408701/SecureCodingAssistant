package testProject;

import java.io.Console;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Test09LCK {

	public synchronized void test(Socket socket, long time) throws IOException, InterruptedException {
		// Test LCK09J
		// Make method synchronized to generate warning
		@SuppressWarnings("unused")
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		
		Thread.sleep(time);
		
		Console c = System.console();
		c.readLine();
	}
	
}
