import java.util.Timer;
import java.util.TimerTask;

import message.RobotCommand;
import connection.UDPConnectionHandler;


public class CommandTask extends TimerTask {
	
	private static final int VALIDITY = 500;
	
	private int sendRate = 100;
	
	private volatile RobotCommand bufferedCommand;
	
	private Timer timer = new Timer(true);
	
	private int stopCount = 0;
	
	private UDPConnectionHandler robot;
	
	public CommandTask(UDPConnectionHandler robot) {
		bufferedCommand = new RobotCommand();
		this.robot = robot;
	}
	
	
	public void setCommand(RobotCommand command) {
		bufferedCommand = command;
		stopCount = 0;
	}


	@Override
	public void run() {
		
		robot.send(bufferedCommand.getBytes());
		
		stopCount++;
		if (stopCount > (VALIDITY / sendRate)) {
			bufferedCommand = new RobotCommand();
		}
	}
	
	public void start() {
		timer.scheduleAtFixedRate(this, 0, sendRate);
	}
}
