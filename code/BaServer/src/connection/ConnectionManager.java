package connection;

import java.util.HashMap;



public class ConnectionManager {
	
	private static ConnectionManager instance = null;

	public static final int STATE_CONNECTED = 100;
	public static final int STATE_DISCONNECTED = 200;
	
	private static final int PROT_TCP = 10;
	private static final int PROT_UDP = 11;
	
	private static final int CHANNEL_ROBOT1 = 0;
	private static final int CHANNEL_ROBOT2 = 1;
	private static final int CHANNEL_CLIENT1 = 2;
	private static final int CHANNEL_CLIENT2 = 3;

	private TCPConnector tcpConnectClient;
	private TCPConnector tcpConnectRobot;
	
	private UDPSocketProvider udpConnector;
	
	private Channel[] channels = new Channel[4];
	
	private OnPlayerReady playerReadyListener;
	private boolean player1Ready = false;
	private boolean player2Ready = false;
	
	

	private ConnectionManager() {
		
		initDevice(CHANNEL_CLIENT2, PROT_TCP);
		initDevice(CHANNEL_CLIENT1, PROT_TCP);
		initDevice(CHANNEL_ROBOT2, PROT_UDP);
		initDevice(CHANNEL_ROBOT1, PROT_UDP);
	}
	
	public static ConnectionManager getInstance() {
		if (instance == null) {
			instance = new ConnectionManager();
		}
		return instance;
	}
	
	
	private void initDevice(final int device, int protocol) {
		
		if (protocol == PROT_TCP) {
			
			if (tcpConnectClient == null) 
				tcpConnectClient = new TCPConnector(Channel.PORT_CLIENT);
			if (tcpConnectRobot == null)
				tcpConnectRobot = new TCPConnector(Channel.PORT_ROBOT);
			
			TCPConnectionHandler tcp = new TCPConnectionHandler(new StateListener() {
				
				@Override
				public void stateChanged(Channel who, int state) {

					if (state == STATE_CONNECTED) {
						setChannel(device, who);
					}
					if (state == STATE_DISCONNECTED) {
						setChannel(device, null);
						initDevice(device, PROT_TCP);
					}
					
					printStatus();
					checkForReadyPlayers();
				}
			});
			if (device == CHANNEL_CLIENT1 || device == CHANNEL_CLIENT2)
				tcpConnectClient.connectHandler(tcp);
			if (device == CHANNEL_ROBOT1 || device == CHANNEL_ROBOT2)
				tcpConnectRobot.connectHandler(tcp);
		}
		
		if (protocol == PROT_UDP) {
			
			udpConnector = UDPSocketProvider.getInstance();
			
			
			UDPConnectionHandler udp = new UDPConnectionHandler(new StateListener() {
				
				@Override
				public void stateChanged(Channel who, int state) {
					
					if (state == STATE_CONNECTED) {
						setChannel(device, who);
					}
					if (state == STATE_DISCONNECTED) {
						setChannel(device, null);
						initDevice(device, PROT_UDP);
					}
					
					printStatus();
					checkForReadyPlayers();
					
				}
			});
			if (device == CHANNEL_CLIENT1 || device == CHANNEL_CLIENT2)
				udpConnector.connectHandler(udp, Channel.PORT_CLIENT);
			if (device == CHANNEL_ROBOT1 || device == CHANNEL_ROBOT2) {
				udpConnector.connectHandler(udp, Channel.PORT_ROBOT);
			}
			
		}
		
	}
	
	
	private void setChannel(int device, Channel channel) {
				
		if (channel != null && channel.getIp().equals(Channel.IP_ROBOT1)) {
			channels[CHANNEL_ROBOT1] = channel;
		}
		else if (channel != null && channel.getIp().equals(Channel.IP_ROBOT2)) {
			channels[CHANNEL_ROBOT2] = channel;
		}else {
			channels[device] = channel;
		}
	}
	
	
	public void registerPlayerReadyListener(OnPlayerReady playerListener) {
		playerReadyListener = playerListener;
	}
	
	
	private void checkForReadyPlayers() {
				
		// player1 was not ready, but is now
		if (!player1Ready && getChannelClient1() != null && getChannelRobot1() != null) {
			playerReadyListener.playerIsReady(1, true);
			player1Ready = true;
		}
		
		// player1 was ready, but is no more
		if (player1Ready && !(getChannelClient1() != null && getChannelRobot1() != null)) {
			playerReadyListener.playerIsReady(1, false);
			player1Ready = false;
		}
		
		// player2 was not ready, but is now
		if (!player2Ready && getChannelClient2() != null && getChannelRobot2() != null) {
			playerReadyListener.playerIsReady(2, true);
			player2Ready = true;
		}
		
		// player2 was ready, but is no more
		if (player2Ready && !(getChannelClient2() != null && getChannelRobot2() != null)) {
			playerReadyListener.playerIsReady(2, false);
			player2Ready = false;
		}
		
	}

	private synchronized void printStatus() {

		System.out.println("------------------");
		System.out.println("Connections:");
		System.out.println("Controller1: "
				+ String.valueOf(getChannelClient1() != null));
		System.out.println("Controller2: "
				+ String.valueOf(getChannelClient2() != null));
		System.out.println("Robot1: " 
				+ String.valueOf(getChannelRobot1() != null));
		System.out.println("Robot2: " 
				+ String.valueOf(getChannelRobot2() != null));
		System.out.println("------------------");

	}
	
	
	public Channel getChannelClient1() {
		return channels[CHANNEL_CLIENT1];
	}
	
	public Channel getChannelClient2() {
		return channels[CHANNEL_CLIENT2];
	}
	
	public Channel getChannelRobot1() {
		return channels[CHANNEL_ROBOT1];
	}
	
	public Channel getChannelRobot2() {
		return channels[CHANNEL_ROBOT2];
	}
	
}
