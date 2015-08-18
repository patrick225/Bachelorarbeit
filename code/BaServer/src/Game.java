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
	
	Channel client1;
	Channel robot1;
	
	Channel client2;
	Channel robot2;
	
	
	
	
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
					} else {
						client1.unregisterMessageListener();
						robot1.unregisterMessageListener();
					}
					break;
				case 2:

				}
			}
		});
		
		gd = new GoalDetector(goalListener);

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
			forwardPlayer1(data);
		}
	};
	
	OnMessageReceived messageListenerRobot1 = new OnMessageReceived() {
		
		@Override
		public void messageReceived(byte[] data) {
			statusPlayer1(new RobotStatus(data));
		}
	};
	

}
