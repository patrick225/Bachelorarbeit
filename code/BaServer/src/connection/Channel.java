package connection;

import java.net.InetAddress;

public abstract class Channel implements Runnable {

	protected static final int PACKETSIZE_CLIENT = 3;
	protected static final int PACKETSIZE_ROBOT = 12;
	
	public static final int PORT_CLIENT = 55055;
	public static final int PORT_ROBOT = 44044;
	
	public static String IP_ROBOT1 = "134.60.168.114";
	public static String IP_ROBOT2 = "134.60.168.115";
	
	protected boolean running = false;
	
	protected OnMessageReceived messageListener;
	protected StateListener stateListener;
	
	protected ConnectionControl cc;
	
	protected Channel(StateListener stateListener) {
		this.stateListener = stateListener;
	}
	
	public void registerMessageListener(OnMessageReceived messageListener) {
		this.messageListener = messageListener;
	}
	
	public void unregisterMessageListener() {
		this.messageListener = null;
	}
	
	public abstract void sendMessage(byte[] data);
	public abstract void close();
}