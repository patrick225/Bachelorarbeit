package message;

public class RobotStatus {

	private final int size = 12;
	private byte[] status = new byte[size];
		
	public RobotStatus(byte[] status) {
		this.status = status;
	}
	
	
	public byte[] getBytes() {
		return status;
	}
	
	
	public byte getAkku() {
		return status[1];
	}
	
//	public byte getPulseLength() {
//		return status[2];
//	}
	
	public int getPulseLength() {
		
		byte[] bytes = {0, 0, 0, status[2]};
		
		int x = java.nio.ByteBuffer.wrap(bytes).getInt();
		
		return x;
	}

	
	@Override
	public String toString() {
		
		return "Akku: " + status[1] + "\n"
				+ "PulsLänge: " + getPulseLength();
	}
}
