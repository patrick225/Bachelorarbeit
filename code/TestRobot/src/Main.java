import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

	static byte[] puffer;
	static byte[] data;
	
	static int pointer = 0;
	public static void main(String[] args) {

		
		
		try {
			
			Path path = Paths.get("/home/patrick/Pictures/test1.jpg");
			byte[] puffer1 = Files.readAllBytes(path);
			path = Paths.get("/home/patrick/Pictures/test2.jpg");
			byte[] puffer2 = Files.readAllBytes(path);
			
			puffer = new byte[puffer1.length + puffer2.length];
			System.arraycopy(puffer1, 0, puffer, 0, puffer1.length);
			System.arraycopy(puffer2, 0, puffer, puffer1.length, puffer2.length);
			
			System.out.println(puffer.length);
			
			
			
			data = new byte[12+1400];
			data[10] = (byte) 0x05;
			data[11] = (byte) 0x78; 	
			
			DatagramPacket packet = new DatagramPacket(data, data.length);
			packet.setAddress(InetAddress.getByName("127.0.01"));
			packet.setPort(44044);
			
			DatagramSocket socket = new DatagramSocket();
			
			while (true) {
				fillBuffer();
				socket.send(packet);
				Thread.sleep(1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void fillBuffer() {
		
		for (int i = 0; i < 1400; i++) {
			data[i + 12] = puffer[pointer];
			pointer++;
			if (pointer == puffer.length) {
				pointer = 0;
			}
		}
		
	}

}
