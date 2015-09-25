package logic;
import message.ControllerStatus;
import message.RobotStatus;

import org.json.simple.JSONObject;

import connection.OnControllerMessageReceived;
import connection.OnRobotMessageReceived;
import connection.UDPConnectionHandler;
import connection.WebsocketSocket;


public class Player {

	
	private WebsocketSocket controller;
	
	private UDPConnectionHandler robot;
	
	private int score = 0;
	
	private CommandTask commandTaskToRobot;
	private CommandTask commandTaskToController;
	
	
	
	public void setDevices(UDPConnectionHandler robot, WebsocketSocket controller) {
		this.controller = controller;
		this.robot = robot;
		
		commandTaskToRobot = new CommandTask(robot);
		commandTaskToController = new CommandTask(controller, 500);
		
		commandTaskToRobot.start();
		commandTaskToController.start();
		
		controller.registerMessageListener(messageListenerController);						
		robot.registerMessageListener(messageListenerRobot);
	}
	
	
	public void unsetDevices() {
		
		controller.unregisterMessageListener();
		robot.unregisterMessageListener();
		
		controller = null;
		robot = null;
		
		commandTaskToRobot.cancel();
		commandTaskToController.cancel();
	}

	
	public void goalDetected() {
		score++;
	}
	
	
	public void setScore(int score) {
		this.score = score;
	}
	
	
	public int getScore() {
		return score;
	}
	
	
	OnControllerMessageReceived messageListenerController = new OnControllerMessageReceived() {
		
		@Override
		public void messageReceived(WebsocketSocket controller, JSONObject command) {

			commandTaskToRobot.setCommand(CommandTranslater.translateCommand(command));
		}
	};
	
	
	OnRobotMessageReceived messageListenerRobot = new OnRobotMessageReceived() {
		
		@Override
		public void messageReceived(UDPConnectionHandler robot, byte[] data) {
			
			String score = Game.getScore();
			String[] scores = score.split(":");
			
			RobotStatus rs = new RobotStatus(data);
			ControllerStatus cs = new ControllerStatus();
			cs.setAkku(rs.getAkku());
			cs.setCountP1(Integer.valueOf(scores[0]));
			cs.setCountP2(Integer.valueOf(scores[1]));
			commandTaskToController.setCommand(cs);
		
		}
	};
}
