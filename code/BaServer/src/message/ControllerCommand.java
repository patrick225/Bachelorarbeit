package message;

import logic.JSONUtil;

import org.json.simple.JSONObject;

import com.google.common.primitives.Longs;


public class ControllerCommand {

	private byte[] command = new byte[3];
	
	public ControllerCommand() {
		
	}
	
	public ControllerCommand(JSONObject json) {
		
		command[0] = JSONUtil.jsonObjToByte(json.get("motorLeft"));
		command[1] = JSONUtil.jsonObjToByte(json.get("motorRight"));
		command[2] = JSONUtil.jsonObjToByte(json.get("shot"));
		
	}
	
	public byte[] getBytes() {
		return command;
	}
	
	@Override
	public String toString() {
		
		return "left: " + command[0] + 
				" right: " + command[1] + 
				" shot: " + command[2];
	}

}
