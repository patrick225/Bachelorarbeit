package message;

public class ControllerStatus {

	private final int size = 3;
	private byte[] status = new byte[size];
	
	public ControllerStatus() {
		
		status[0] = (byte) 0x64;
		status[1] = (byte) 0x00;
		status[2] = (byte) 0x00;
	}
	
	public ControllerStatus(int akku, int countP1, int countP2) {
		
		status[0] = (byte) akku;
		status[1] = (byte) countP1;
		status[2] = (byte) countP2;
	}
	
	public void setAkku(int akku) {
		status[0] = (byte) akku;
	}
	
	public void setCountP1(int count) {
		status[1] = (byte) count;
	}
	
	public void setCountP2(int count) {
		status[2] = (byte) count;
	}
	
	public byte[] getBytes() {
		return status;
	}

}
