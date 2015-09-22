package logic;
import org.json.simple.JSONObject;

import connection.OnControllerMessageReceived;
import connection.OnRobotMessageReceived;
import connection.UDPConnectionHandler;
import connection.WebsocketSocket;


public class Player {

	
	private WebsocketSocket controller;
	
	private UDPConnectionHandler robot;
	
	private int score = 0;
	
	private CommandTask commandTask;
	
	
	public Player() {
	}
	
	
	public void setDevices(UDPConnectionHandler robot, WebsocketSocket controller) {
		this.controller = controller;
		this.robot = robot;
		
		controller.registerMessageListener(messageListenerController);						
		robot.registerMessageListener(messageListenerRobot);
		
		commandTask = new CommandTask(robot);
	}
	
	
	public void unsetDevices() {
		
		controller.unregisterMessageListener();
		robot.unregisterMessageListener();
		
		controller = null;
		robot = null;
		
		commandTask.cancel();
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

			commandTask.setCommand(CommandTranslater.translateCommand(command));
		}
	};
	
	OnRobotMessageReceived messageListenerRobot = new OnRobotMessageReceived() {
		
		@Override
		public void messageReceived(UDPConnectionHandler robot, byte[] data) {

		
		}
	};
}
