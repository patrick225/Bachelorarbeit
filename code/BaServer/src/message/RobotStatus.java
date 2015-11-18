package message;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;

public class RobotStatus {

	private final int size = 12;
	private byte[] status = new byte[size];
	private byte[] picture;
		
	public static final int SEE_LEFT = 2;
	public static final int SEE_RIGHT = 3;
	public static final int SEE_STRAIGHT = 1;
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
	
	public int seeStation() {
		
		switch (status[2]) {
		
		case (byte) 0x01:
			return 1;
		case (byte) 0x02:
			return 2;
		case (byte) 0x03:
			return 3;
		case (byte) 0x00:
			return 0;
		}
		return 0;
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
	
