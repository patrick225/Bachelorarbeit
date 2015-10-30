import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Main {

	public static void main(String[] args) {

		
		
		try {
			
			byte[] data = new byte[12];
			
			DatagramPacket packet = new DatagramPacket(data, 12);
			packet.setAddress(InetAddress.getByName("127.0.01"));
			packet.setPort(44044);
			
			DatagramSocket socket = new DatagramSocket();
			
			while (true) {
				socket.send(packet);
				Thread.sleep(100);
			}
			
		} catch (Exception e) {
			
		}
	}

}
