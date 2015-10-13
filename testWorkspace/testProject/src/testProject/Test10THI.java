package testProject;

public class Test10THI implements Runnable {

	@SuppressWarnings("deprecation")
	public void test() throws InterruptedException {
		// Test THI05J
	    Thread thread = new Thread(new Test10THI());
	    thread.start();
	    Thread.sleep(5000);
	    thread.stop(); // This generates warning
	}

	@Override
	public synchronized void run() {
		// TODO Auto-generated method stub
		
	}
	
}
