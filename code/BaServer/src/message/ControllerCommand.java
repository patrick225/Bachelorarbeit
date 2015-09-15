package message;

import org.json.simple.JSONObject;

public class ControllerCommand {

	private byte[] command = new byte[3];
	
	public ControllerCommand() {
		
	}
	
	public ControllerCommand(JSONObject json) {
		
		command[0] = (byte) json.get("motorLeft");
		command[1] = (byte) json.get("motorRight");
		command[2] = (byte) json.get("shot");
	}
	
	public byte[] getBytes() {
		return command;
	}

}
