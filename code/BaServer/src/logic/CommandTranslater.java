package logic;
import org.json.simple.JSONObject;


public class CommandTranslater {

	private static final int CONTROLLER_WEBAPP = 1;
	private static final int CONTROLLER_ANDROIDAPP = 2;
	
	public CommandTranslater() {
		
	}
	
	
	public static byte[] translateCommand(JSONObject command) {
		
		switch (JSONUtil.jsonObjToInt(command.get("controllerType"))) {
		case CONTROLLER_WEBAPP:
			translateWebapplication(command);
			break;
		case CONTROLLER_ANDROIDAPP:
			
			break;
			
		}
		
		return null;
	}
	
	
	private static byte[] translateWebapplication(JSONObject command) {
		
		
		
		return null;
	}
}
