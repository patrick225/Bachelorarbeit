package logic;
import message.RobotCommand;

import org.json.simple.JSONObject;


public class CommandTranslater {

	private static final int CONTROLLER_WEBAPP = 1;
	private static final int CONTROLLER_ANDROIDAPP = 2;
	
	public CommandTranslater() {
		
	}
	
	
	public static RobotCommand translateCommand(JSONObject command) {
		
		RobotCommand result = null;
		
		switch (JSONUtil.jsonObjToInt(command.get("controllerType"))) {
		
		case CONTROLLER_WEBAPP:
			result = translateWebapplication(command);
			break;
		case CONTROLLER_ANDROIDAPP:
			result = translateAndroidapplication(command);
			break;
			
		}
		
		return result;
	}
	
	
	private static RobotCommand translateWebapplication(JSONObject command) {
		
		int motorLeft = 0, motorRight = 0;
		
		boolean shot = JSONUtil.jsonObjToBoolean(command.get("shot"));
		
		if (JSONUtil.jsonObjToBoolean(command.get("up"))) {
			motorLeft += 100;
			motorRight += 100;
		}
		if (JSONUtil.jsonObjToBoolean(command.get("down"))) {
			motorLeft -= 100;
			motorRight -= 100;
		}
		if (JSONUtil.jsonObjToBoolean(command.get("right"))) {
			motorRight *= 0.75;
		}
		if (JSONUtil.jsonObjToBoolean(command.get("up"))) {
			motorLeft *= 0.75;
		}
		
		RobotCommand robot = new RobotCommand(
				false, 
				shot,
				motorLeft,
				motorRight);
		
		return robot;
	}
	
	
	private static RobotCommand translateAndroidapplication(JSONObject command) {
		
		RobotCommand robot = new RobotCommand(
				false, 
				JSONUtil.jsonObjToBoolean(command.get("shot")), 
				JSONUtil.jsonObjToInt(command.get("motorLeft")), 
				JSONUtil.jsonObjToInt(command.get("motorRight")));
		
		return robot;
	}
}
