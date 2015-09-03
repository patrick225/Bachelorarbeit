package connection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Stack;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class UDPSocketProvider {

	private static DatagramSocket socketClient;
	private static DatagramSocket socketRobot;

	private static DatagramSocket sendSocket;
	
	private static UDPSocketProvider instance;
	
	
	private static HashMap<Device, UDPConnectionHandler> mapIncoming;
	private static HashMap<UDPConnectionHandler, Device> mapOutgoing;
	
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
		
		Device device = mapOutgoing.get(handler);
		DatagramPacket packet = new DatagramPacket(data, data.length, device.ip, device.port);
		System.out.println("address:" + packet.getSocketAddress());
		
		try {
			sendSocket.send(packet);
		} catch (Exception e)  {
			e.printStackTrace();
		}
		
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
				byte[] data = new byte[1000];
				DatagramPacket packet = new DatagramPacket(data, data.length);
				try {
					
					socketClient.receive(packet);
					Device device = new Device(socketClient.getLocalPort(), packet.getAddress());					
					if (mapIncoming.containsKey(device)) {
						mapIncoming.get(device).incomingMessage(packet);
						
					}
					else if (!stackClient.empty()) {
						UDPConnectionHandler handler = newDevice(packet);
						handler.incomingMessage(packet);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}
	};
	
	
	private UDPConnectionHandler newDevice(DatagramPacket packet) {
		
		byte[] data = packet.getData();
		Device device;
		JSONParser parser = new JSONParser();
		try {
			JSONObject obj = (JSONObject) parser.parse(new String(data).trim());

			device = new Device(packet.getPort(), obj.get("ip").toString());
			
		} catch (Exception e) {
			
			device = new Device(packet.getPort(), packet.getAddress());
		}
		
		
		UDPConnectionHandler handler = stackClient.pop();
		mapIncoming.put(device, handler);
		mapOutgoing.put(handler, device);
		
		return handler;
	}
	
	private Runnable readRobot = new Runnable() {
		
		@Override
		public void run() {

			while (true) {
				byte[] data = new byte[Channel.PACKETSIZE_ROBOT];
				DatagramPacket packet = new DatagramPacket(data, data.length);
				try {
					socketRobot.receive(packet);
					Device device = new Device(packet.getPort(), packet.getAddress());
					
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
	
	
	public Device getDevice(UDPConnectionHandler handler) {
		return mapOutgoing.get(handler);
	}

	
	public class Device {
		
		public int port;
		public InetAddress ip;
		
		public Device(int port, InetAddress ip) {
			this.port = port;
			this.ip = ip;
		}
		
		public Device(int port, String ip) {
			this.port = port;
			try {
				this.ip = InetAddress.getByName(ip);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		
		@Override
	    public int hashCode() {
			
			//quersumme
			String values = ip.toString().replaceAll( "[^\\d]", "" );
			int sum = 0;
			for (int i = 0; i < values.length(); i++) {
				sum += Integer.valueOf(values.charAt(i));
			}
					
			return sum;
		}
		
		 
		@Override
		public boolean equals(Object device) {
			
			if (!(device instanceof Device) || device == null) {
				return false;
			}
			Device dev = (Device) device;
			
			if (dev.ip.equals(this.ip)) 
				return true;
			
			return false;
			
		}
			
		
		
	}
	
	
}
