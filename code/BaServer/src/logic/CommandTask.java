package logic;
import java.util.Timer;
import java.util.TimerTask;

import message.ControllerStatus;
import message.RobotCommand;
import connection.UDPConnectionHandler;
import connection.WebsocketSocket;


public class CommandTask extends TimerTask {
	
	private static final int VALIDITY = 500;
	
	private int sendRate = 100;
	
	private volatile RobotCommand bufferedCommandRobot;
	private volatile ControllerStatus bufferedStatusController;
	
	private Timer timer = new Timer(true);
	
	private int stopCount = 0;
	
	private UDPConnectionHandler robot;
	private WebsocketSocket controller;
	
	public CommandTask(UDPConnectionHandler robot) {
		bufferedCommandRobot = new RobotCommand();
		this.robot = robot;
	}
	
	public CommandTask(WebsocketSocket controller) {
		bufferedStatusController = new ControllerStatus();
		this.controller = controller;
	}
	
	
	public void setCommand(RobotCommand command) {
		bufferedCommandRobot = command;
		stopCount = 0;
	}
	
	public void setCommand(ControllerStatus command) {
		bufferedStatusController = command;
		stopCount = 0;
	}


	@Override
	public void run() {
		
		if (robot != null) {
			robot.send(bufferedCommandRobot.getBytes());
			
			if (stopCount > (VALIDITY / sendRate)) {
				bufferedCommandRobot = new RobotCommand();
			}
		}
		
		if (controller != null) {
			controller.send(bufferedStatusController.getJSON());
			
			if (stopCount > (VALIDITY / sendRate)) {
				bufferedStatusController = new ControllerStatus();
			}
		}
		
		stopCount++;
		
	}
	
	public void start() {
		timer.scheduleAtFixedRate(this, 0, sendRate);
	}
}
