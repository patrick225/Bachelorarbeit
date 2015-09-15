import java.util.Timer;
import java.util.TimerTask;

import message.RobotCommand;

import org.json.simple.JSONObject;

import connection.Channel;
import connection.ConnectionManager;
import connection.OnControllerMessageReceived;
import connection.OnPlayerReady;
import connection.OnRobotMessageReceived;
import connection.UDPConnectionHandler;
import connection.WebsocketSocket;


public class Game {
	
	int scorePlayer1;
	int scorePlayer2;

	ConnectionManager cm;
	GoalDetector gd;
	
	volatile WebsocketSocket controller1;
	volatile UDPConnectionHandler robot1;
	
	volatile WebsocketSocket controller2;
	volatile UDPConnectionHandler robot2;
	
	volatile RobotCommand commandP1;
	volatile RobotCommand commandP2;
	
	ForwardTask forwardP1;
	ForwardTask forwardP2;
	
	
	
	public Game() {
				
		scorePlayer1 = 0;
		scorePlayer2 = 0;
		
		cm = ConnectionManager.getInstance();
		cm.registerPlayerReadyListener(new OnPlayerReady() {
			
			@Override
			public void playerIsReady(int player, boolean state) {

				switch (player) {
				case 1:
					if (state) {
						controller1 = cm.getController1();
						robot1 = cm.getRobot2();
						controller1.registerMessageListener(messageListenerController);						
						robot1.registerMessageListener(messageListenerRobot);
						commandP1 = new RobotCommand();
						forwardP1 = new ForwardTask(1);
						forwardP1.start();
						
					} else {
						controller1.unregisterMessageListener();
						robot1.unregisterMessageListener();
						forwardP1.cancel();

					}
					break;
				case 2:
					if (state) {
						controller2 = cm.getController2();
						robot2 = cm.getRobot2();
						controller2.registerMessageListener(messageListenerController);
						robot2.registerMessageListener(messageListenerRobot);
						commandP2 = new RobotCommand();
						forwardP2 = new ForwardTask(2);
						forwardP2.start();

					} else {
						controller2.unregisterMessageListener();
						robot2.unregisterMessageListener();
						forwardP2.start();

					}
				}
			}
		});
		
//		gd = new GoalDetector(goalListener);

	}
	
	public void resetScore() {
		scorePlayer1 = 0;
		scorePlayer2 = 0;
	}
	
	private void forwardPlayer1(byte[] data) {
		robot1.send(new RobotCommand(data).getBytes());
	}
	
//	private void statusPlayer1(RobotStatus status) {
//		ControllerStatus cstat = new ControllerStatus();
//		cstat.setAkku(status.getAkku());
//		cstat.setCountP1(scorePlayer1);
//		cstat.setCountP2(scorePlayer2);
//		controller1.sendMessage(cstat.getBytes());
//	}
	
	private void forwardPlayer2(byte[] data) {
		robot2.send(new RobotCommand(data).getBytes());
	}
	
//	private void statusPlayer2(RobotStatus status) {
//		ControllerStatus cstat = new ControllerStatus();
//		cstat.setAkku(status.getAkku());
//		cstat.setCountP1(scorePlayer1);
//		cstat.setCountP2(scorePlayer2);
//		controller2.sendMessage(cstat.getBytes());
//	}
	
	
	OnGoalDetected goalListener = new OnGoalDetected() {
		
		@Override
		public void goalDetected(int player) {
			
			if (player == 1) scorePlayer1++;
			if (player == 2) scorePlayer2++;
			
			System.out.println(scorePlayer1 + " : " + scorePlayer2);			
		}
	};
	
	
	OnControllerMessageReceived messageListenerController = new OnControllerMessageReceived() {
		
		@Override
		public void messageReceived(WebsocketSocket controller, JSONObject data) {

			
		}
	};
	
	OnRobotMessageReceived messageListenerRobot = new OnRobotMessageReceived() {
		
		@Override
		public void messageReceived(UDPConnectionHandler robot, byte[] data) {

		
		}
	};
	
	
	
	private class ForwardTask extends TimerTask {

		private int sendRate = 100;
		private int player;
		
		Timer timer = new Timer(true);
		
		public ForwardTask(int player) {
			
			this.player = player;
		}
		
		
		@Override
		public void run() {
			if (player == 1) {
				robot1.send(commandP1.getBytes());
			}
			if (player == 2) {
				robot2.send(commandP2.getBytes());
			}
			
			System.out.println("forward player" + player);
		}
		
		public void start() {
			timer.scheduleAtFixedRate(this, 0, sendRate);
		}
		
	}

}
