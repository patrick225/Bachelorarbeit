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
	
	public byte getPulseLength() {
		return status[2];
	}

}
