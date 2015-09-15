package message;

import org.json.simple.JSONObject;

import com.google.common.primitives.Longs;

public class ControllerCommand {

	private byte[] command = new byte[3];
	
	public ControllerCommand() {
		
	}
	
	public ControllerCommand(JSONObject json) {
		
		
		command[0] = jsonObjToByte(json.get("motorLeft"));
		command[1] = jsonObjToByte(json.get("motorRight"));
		command[2] = jsonObjToByte(json.get("shot"));
	}
	
	public byte[] getBytes() {
		return command;
	}
	
	
	private byte jsonObjToByte(Object obj) {
		
		byte[] bytes = Longs.toByteArray((long) obj);
		return bytes[(Long.SIZE / 8) - 1];
	}
	
	@Override
	public String toString() {
		
		return "left: " + command[0] + 
				" right: " + command[1] + 
				" shot: " + command[2];
	}

}
