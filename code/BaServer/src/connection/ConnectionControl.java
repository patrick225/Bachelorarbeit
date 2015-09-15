package connection;

public class ConnectionControl extends Thread {
	
	private volatile boolean running = true;
	private volatile long lastStatus;
	public static final long TIMEOUT = 5000;
	private UDPConnectionHandler handler;
	
	public ConnectionControl(UDPConnectionHandler handler) {
		this.handler = handler;
	}
	
	@Override
	public void run() {
		while(running) {
			long curTime = System.currentTimeMillis();
			long lastTime = lastStatus;
			if ((curTime - lastTime) > TIMEOUT) {
				handler.onTimeout();
				break;
			}
			
		}
	}
	
	public void stopControl() {
		running = false;
	}
	
	public void startControl() {
		
		lastStatus = System.currentTimeMillis();
		start();
	}
	
	public void update() {
		lastStatus = System.currentTimeMillis();
	}
}
