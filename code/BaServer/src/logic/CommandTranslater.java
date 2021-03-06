package logic;
import message.RobotCommand;

import org.json.simple.JSONObject;


public class CommandTranslater {

	private static final int CONTROLLER_WEBAPP = 1;
	private static final int CONTROLLER_DIFFERENTIAL = 2;
	private static final int CONTROLLER_RCREMOTE = 3;
	
	
	public static RobotCommand translateCommand(JSONObject command) {
		
		RobotCommand result = null;
		
		switch (JSONUtil.jsonObjToInt(command.get("controllerType"))) {
		
		case CONTROLLER_WEBAPP:
			result = translateWASD(command);
			break;
		case CONTROLLER_DIFFERENTIAL:
			result = translateDifferential(command);
			break;
		case CONTROLLER_RCREMOTE:
			result = translateRCRemote(command);
			
		}
		
		return result;
	}
	
	
	private static RobotCommand translateWASD(JSONObject command) {
		
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
		if (JSONUtil.jsonObjToBoolean(command.get("left"))) {
			motorLeft *= 0.75;
		}
		
		if (motorRight == 0 && motorLeft == 0 &&
				JSONUtil.jsonObjToBoolean(command.get("right"))) {
			
			motorLeft = 20;
			motorRight = -20;
		}
		
		if (motorRight == 0 && motorLeft == 0 &&
				JSONUtil.jsonObjToBoolean(command.get("left"))) {
			
			motorLeft = -20;
			motorRight = 20;
		}
		
		RobotCommand robot = new RobotCommand(
				false, 
				shot,
				motorLeft,
				motorRight);
		
		return robot;
	}
	
	
	private static RobotCommand translateDifferential(JSONObject command) {
		
		RobotCommand robot = new RobotCommand(
				false, 
				JSONUtil.jsonObjToBoolean(command.get("shot")), 
				JSONUtil.jsonObjToInt(command.get("motorLeft")), 
				JSONUtil.jsonObjToInt(command.get("motorRight")));
		
		return robot;
	}
	
	
	private static RobotCommand translateRCRemote(JSONObject command) {
		
		
		RobotCommand robot = new RobotCommand();
		robot.setKick(JSONUtil.jsonObjToBoolean(command.get("shot")));
		
		robot.setMotorLeft(JSONUtil.jsonObjToInt(command.get("velocity")));
		robot.setMotorRight(JSONUtil.jsonObjToInt(command.get("velocity")));
		
		int dir = JSONUtil.jsonObjToInt(command.get("direction"));
		
		
		double ratioLR = Math.pow(2,((double) dir/50));
		
		if (ratioLR > 1) {
			robot.setMotorRight((int) ((double) robot.getMotorLeft() / ratioLR)); 
		}
		if (ratioLR < 1) {
			robot.setMotorLeft((int) ((double) robot.getMotorRight() * ratioLR));
		}
		
		return robot;
	}
	
	
	public static RobotCommand filterInvalidMotorvalues (RobotCommand newOne, RobotCommand oldOne) {
		
		RobotCommand valid = newOne;
		
		int motorLeftNew = newOne.getMotorLeft();
		int motorRightNew = newOne.getMotorRight();
		
		int motorLeftOld = oldOne.getMotorLeft();
		int motorRightOld = oldOne.getMotorRight();
		
		
		
		// if passing 0, break to 0
		if (motorLeftNew > 0 && motorLeftOld < 0 ||
			motorLeftNew < 0 && motorLeftOld > 0) {
			
			valid.setMotorLeft(0);
		} else if (Math.abs(motorLeftNew)  > Math.abs(motorLeftOld)){
			valid.setMotorLeft(getNewValue(motorLeftNew, motorLeftOld));
		}
		
		
		// if passing 0, break to 0
		if (motorRightNew > 0 && motorRightOld < 0 ||
			motorRightNew < 0 && motorRightOld > 0) {
			
			valid.setMotorRight(0);
		} else if (Math.abs(motorRightNew)  > Math.abs(motorRightOld)){
			valid.setMotorRight(getNewValue(motorRightNew, motorRightOld));
		}
		
		double ratioBigSmall = 1.0;
		if (Math.abs(motorLeftNew) > Math.abs(motorRightNew) && motorRightNew != 0) {
			ratioBigSmall = (double) motorLeftNew / (double) motorRightNew;
			valid.setMotorRight((int) ((double) valid.getMotorLeft() / ratioBigSmall));
		}
		else if (Math.abs(motorLeftNew) < Math.abs(motorRightNew) && motorLeftNew != 0){
			ratioBigSmall = (double) motorRightNew / (double) motorLeftNew;
			valid.setMotorLeft((int) ((double) valid.getMotorRight() / ratioBigSmall));
		}
		
		return valid;
	}
	
	private static int getMaxValue(int old) {
		
		old = Math.abs(old);
		double newVal =  1.225 * old + 10.0;
		
		return (int) newVal;
	}
	
	private static int getNewValue(int motorNew, int motorOld) {
		int newVal = Math.min(Math.abs(motorNew), getMaxValue(motorOld));
		
		if (motorNew > 0) {
			return newVal;
		} else if (motorNew < 0) {
			return -newVal;
		} else {
			return 0;
		}
	}
	
}
