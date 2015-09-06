import java.util.Timer;
import java.util.TimerTask;

import message.ControllerStatus;
import message.RobotCommand;
import message.RobotStatus;
import connection.Channel;
import connection.ConnectionManager;
import connection.OnMessageReceived;
import connection.OnPlayerReady;


public class Game {
	
	int scorePlayer1;
	int scorePlayer2;

	ConnectionManager cm;
	GoalDetector gd;
	
	volatile Channel client1;
	volatile Channel robot1;
	
	volatile Channel client2;
	volatile Channel robot2;
	
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
						client1 = cm.getChannelClient1();
						robot1 = cm.getChannelRobot1();
						client1.registerMessageListener(messageListenerClient1);
						robot1.registerMessageListener(messageListenerRobot1);
						commandP1 = new RobotCommand();
						forwardP1 = new ForwardTask(1);
						forwardP1.start();
						
					} else {
						client1.unregisterMessageListener();
						robot1.unregisterMessageListener();
						forwardP1.cancel();

					}
					break;
				case 2:
					if (state) {
						client2 = cm.getChannelClient2();
						robot2 = cm.getChannelRobot2();
						client2.registerMessageListener(messageListenerClient2);
						robot2.registerMessageListener(messageListenerRobot2);
						commandP2 = new RobotCommand();
						forwardP2 = new ForwardTask(2);
						forwardP2.start();

					} else {
						client2.unregisterMessageListener();
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
		robot1.sendMessage(new RobotCommand(data).getBytes());
	}
	
	private void statusPlayer1(RobotStatus status) {
		ControllerStatus cstat = new ControllerStatus();
		cstat.setAkku(status.getAkku());
		cstat.setCountP1(scorePlayer1);
		cstat.setCountP2(scorePlayer2);
		client1.sendMessage(cstat.getBytes());
	}
	
	private void forwardPlayer2(byte[] data) {
		robot2.sendMessage(new RobotCommand(data).getBytes());
	}
	
	private void statusPlayer2(RobotStatus status) {
		ControllerStatus cstat = new ControllerStatus();
		cstat.setAkku(status.getAkku());
		cstat.setCountP1(scorePlayer1);
		cstat.setCountP2(scorePlayer2);
		client2.sendMessage(cstat.getBytes());
	}
	
	
	OnGoalDetected goalListener = new OnGoalDetected() {
		
		@Override
		public void goalDetected(int player) {
			
			if (player == 1) scorePlayer1++;
			if (player == 2) scorePlayer2++;
			
			System.out.println(scorePlayer1 + " : " + scorePlayer2);			
		}
	};
	
	OnMessageReceived messageListenerClient1 = new OnMessageReceived() {
		
		@Override
		public void messageReceived(byte[] data) {
			commandP1 = new RobotCommand(data);
		}
	};
	
	OnMessageReceived messageListenerRobot1 = new OnMessageReceived() {
		
		@Override
		public void messageReceived(byte[] data) {
			statusPlayer1(new RobotStatus(data));
		}
	};
	
OnMessageReceived messageListenerClient2 = new OnMessageReceived() {
		
		@Override
		public void messageReceived(byte[] data) {
			commandP2 = new RobotCommand(data);
		}
	};
	
	OnMessageReceived messageListenerRobot2 = new OnMessageReceived() {
		
		@Override
		public void messageReceived(byte[] data) {
			statusPlayer2(new RobotStatus(data));
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
				robot1.sendMessage(commandP1.getBytes());
			}
			if (player == 2) {
				robot2.sendMessage(commandP2.getBytes());
			}
			
			System.out.println("forward player" + player);
		}
		
		public void start() {
			timer.scheduleAtFixedRate(this, 0, sendRate);
		}
		
	}

}
