package connection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;


public class UDPSocketProvider implements Runnable {

	private final static int ROBOT_PORT = 44044;
	private final static String IP_ROBOT1 = "10.38.5.229";
	private final static String IP_ROBOT2 = "134.60.145.165";
	private final static String IP_DEBUG = "134.60.156.40";
	
	private final static int PACKETSIZE_INCOMING = 12;
	
	private DatagramSocket socket;
	
	private UDPConnectionHandler[] robots = new UDPConnectionHandler[2];
	
	private BiMap<SocketAddress, UDPConnectionHandler> mapping;
	
	public UDPSocketProvider() {

		mapping = HashBiMap.create();
		try {
			socket = new DatagramSocket(ROBOT_PORT);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void run() {

		while (true) {
			byte[] data = new byte[PACKETSIZE_INCOMING];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);
				
				// discard if ip no robot
				if (!isRobot(packet)) continue;
				
				handleMessage(packet);
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}	
	
	
	public void send(UDPConnectionHandler who, byte[] data) {
		
		for (int i = 0; i < robots.length; i++) {
			if (who == robots[i]) {
				SocketAddress addr = mapping.inverse().get(who);
				try {
					DatagramPacket packet = new DatagramPacket(data, 0, data.length, addr);
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public void unsetRobot(UDPConnectionHandler who) {
		
		for (int i = 0; i < robots.length; i++) {
			if (who == robots[i]) {
				robots[i] = null;
				mapping.remove(mapping.inverse().get(who));
			}
		}
	}
	
	
	public SocketAddress getAddressByHandler(UDPConnectionHandler handler) {
		return mapping.inverse().get(handler);
	}
	
	
	private void handleMessage(DatagramPacket packet) {
		
		int nr = getRobotNr(packet) - 1;
		if (robots[nr] == null) {
			robots[nr] = new UDPConnectionHandler(this);
			mapping.put(packet.getSocketAddress(), robots[nr]);
		}
		
		robots[nr].incomingMessage(packet);
	}
	
	
	private int getRobotNr(DatagramPacket packet) {
		
		return 1;
//		try {
//			if (packet.getAddress().equals(InetAddress.getByName(IP_ROBOT1))) 
//				return 1;
//			if (packet.getAddress().equals(InetAddress.getByName(IP_ROBOT2)))
//				return 2;
//			if (packet.getAddress().equals(InetAddress.getByName(IP_DEBUG)))
//				return 1;
//			//debug
//			if (packet.getAddress().equals(InetAddress.getByName("127.0.0.1")))
//				return 1;
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		}
//		
//		return -1;
	}
	
	
	private boolean isRobot(DatagramPacket packet) {
		
		if (getRobotNr(packet) != -1) {
			return true;
		}
		
		return false;
	}
	
}
