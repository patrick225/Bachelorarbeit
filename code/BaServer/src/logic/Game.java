package logic;
import connection.ConnectionManager;
import connection.OnPlayerReady;


public class Game {
	
	ConnectionManager cm;
	GoalDetector gd;
	
	
	private Player player1;
	private Player player2;
	
	private static String score = "0:0";
	
	public Game() {
		
		player1 = new Player();
		player2 = new Player();
		
		cm = ConnectionManager.getInstance();
		cm.registerPlayerReadyListener(playerReadyListener);
		
		gd = new GoalDetector(goalListener);

	}
	
	public void resetScore() {
		player1.setScore(0);
		player2.setScore(0);
		
		
	}
	
	
	OnPlayerReady playerReadyListener = new OnPlayerReady() {
		
		@Override
		public void playerIsReady(int player, boolean state) {

			switch (player) {
			case 1:
				if (state) {
					player1.setDevices(cm.getRobot1(), cm.getController1());
					
				} else {
					player1.unsetDevices();
				}
				break;
			case 2:
				if (state) {
					player2.setDevices(cm.getRobot2(), cm.getController2());

				} else {
					player2.unsetDevices();

				}
			}
		}
	};
	
	
	public static String getScore() {
		return score;
	}
	
	
	OnGoalDetected goalListener = new OnGoalDetected() {
		
		@Override
		public void goalDetected(int player) {
			
			if (player == 1) player1.goalDetected();
			if (player == 2) player2.goalDetected();
			
			score = player1.getScore() + " : " + player2.getScore();
			
			System.out.println(score);			
		}
	};
	
	
}