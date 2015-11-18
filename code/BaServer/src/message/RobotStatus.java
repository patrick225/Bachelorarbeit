package message;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;

public class RobotStatus {

	private final int size = 12;
	private byte[] status = new byte[size];
	private byte[] picture;
		
	public RobotStatus(byte[] status) {
		
		ByteArrayInputStream is = new ByteArrayInputStream(status);
		is.read(this.status, 0, size);
		
		picture = new byte[getPictureSize()];
		
		is.read(picture, 0, picture.length);
		
	}
	
	private int getPictureSize() {
		
		byte[] buf = {(byte) 0x00, (byte) 0x00, status[10], status[11]};
		
		ByteBuffer wrapped = ByteBuffer.wrap(buf); // big-endian by default
		
		return wrapped.getInt();
	}
	
	public byte[] getBytes() {
		return status;
	}
	
	
	public byte getAkku() {
		return status[1];
	}
	
	public boolean seeStation() {
		
		if (status[2] == (byte) 0x01)
			return true;
		else 
			return false;
	}
	
	public byte[] getPictureData() {
		return picture;
	}
	
	@Override
	public String toString() {
		
		return "Akku: " + status[1] + "\n"
				+ "PulsL�nge: " + seeStation();
	}
}
	
