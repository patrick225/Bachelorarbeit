package message;


public class RobotCommand {

	
	private final int size = 6;
	private byte[] command = new byte[size];
	
	public RobotCommand() {

		command[0] = (byte) 0xFF;
		command[1] = (byte) 0x00;
		command[2] = (byte) 0x00;
		command[3] = (byte) 0x00;
		command[4] = (byte) 0x00;
		command[5] = (byte) 0x00;
	}
	
	public RobotCommand(boolean camera, boolean kick, int motorLeft, int motorRight) {

		command[0] = (byte) 0xFF;
		
		if (camera) {
			command[1] = (byte) 0x01;
		} else {
			command[1] = (byte) 0x00;
		}
		
		if (kick) {
			command[2] = (byte) 0x01;
		} else {
			command[2] = (byte) 0x00;
		}
		
		command[3] = (byte) motorLeft;
		command[4] = (byte) motorRight;
		
		command[5] = (byte) 0x00;
	}
	

	public RobotCommand(byte[] data) {
		
		command[2] = data[2];
		command[3] = data[0];
		command[4] = data[1];
		
	}
	
	public byte[] getBytes() {
		setChecksum();
		return command;
	}

	public void setCamera(boolean value) {
		if (value) {
			command[1] = (byte) 0x01;
		} else {
			command[1] = (byte) 0x00;
		}
	}
	
	public void setKick(boolean value) {
		if (value) {
			command[2] = (byte) 0x01;
		} else {
			command[2] = (byte) 0x00;
		}
	}
	
	public void setMotorLeft(int percentage) {
		
		command[3] = (byte) percentage;
	}
	
	public void setMotorRight(int percentage) {
		
		command[4] = (byte) percentage;
	}
	
	private void setChecksum() {
		command[5] = (byte) ((byte) 0xFF - 
				(command[1] + command[2] + command[3] + command[4]) % (byte) 0xFF);
	}

}
