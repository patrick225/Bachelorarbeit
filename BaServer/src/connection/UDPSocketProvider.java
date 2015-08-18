package connection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Stack;

public class UDPSocketProvider {

	private static DatagramSocket socketClient;
	private static DatagramSocket socketRobot;

	private static DatagramSocket sendSocket;
	
	private static UDPSocketProvider instance;
	
	
	private static HashMap<SocketAddress, UDPConnectionHandler> mapIncoming;
	private static HashMap<UDPConnectionHandler, SocketAddress> mapOutgoing;
	
	private static Stack<UDPConnectionHandler> stackClient;
	private static Stack<UDPConnectionHandler> stackRobot;
	
	public static UDPSocketProvider getInstance() {
		
		if (instance == null) {
			instance = new UDPSocketProvider();
		}
		
		return instance;
	}
	
	private UDPSocketProvider() {

		mapIncoming = new HashMap<>();
		mapOutgoing = new HashMap<>();
		stackClient = new Stack<>();
		stackRobot = new Stack<>();
		try {
			socketClient = new DatagramSocket(Channel.PORT_CLIENT);
			socketRobot = new DatagramSocket(Channel.PORT_ROBOT);
			sendSocket = new DatagramSocket();
			
			new Thread(readClient).start();
			new Thread(readRobot).start();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		
	}
	
	public void send(UDPConnectionHandler handler, byte[] data) throws IOException  {
		
		SocketAddress device = mapOutgoing.get(handler);
		System.out.println(device);
		DatagramPacket packet = new DatagramPacket(data, data.length, device);
		
		sendSocket.send(packet);
		
	}
	
	public void connectHandler (UDPConnectionHandler handler, int port) {
		if (port == Channel.PORT_CLIENT)
			stackClient.add(handler);
		if (port == Channel.PORT_ROBOT)
			stackRobot.add(handler);
	}
	
	private Runnable readClient = new Runnable() {
		
		@Override
		public void run() {

			while(true) {
				byte[] data = new byte[Channel.PACKETSIZE_CLIENT];
				DatagramPacket packet = new DatagramPacket(data, data.length);
				try {
					
					socketClient.receive(packet);
					SocketAddress device = packet.getSocketAddress();
					
					if (mapIncoming.containsKey(device)) {
						mapIncoming.get(device).incomingMessage(packet);
					}
					else if (!stackClient.empty()) {
						UDPConnectionHandler handler = stackClient.pop();
						mapIncoming.put(device, handler);
						mapOutgoing.put(handler, device);
						handler.incomingMessage(packet);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}
	};
	
	private Runnable readRobot = new Runnable() {
		
		@Override
		public void run() {

			while (true) {
				byte[] data = new byte[Channel.PACKETSIZE_CLIENT];
				DatagramPacket packet = new DatagramPacket(data, data.length);
				try {
					socketRobot.receive(packet);
					SocketAddress device = packet.getSocketAddress();
					
					if (mapIncoming.containsKey(device)) {
						mapIncoming.get(device).incomingMessage(packet);
					}
					else if (!stackRobot.empty()) {
						UDPConnectionHandler handler = stackRobot.pop();
						mapIncoming.put(device, handler);
						mapOutgoing.put(handler, device);
						handler.incomingMessage(packet);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};	

}
