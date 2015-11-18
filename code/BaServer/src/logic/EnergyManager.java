package logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import message.RobotCommand;
import message.RobotStatus;
import connection.OnRobotMessageReceived;
import connection.UDPConnectionHandler;

public class EnergyManager {

	// threshold on 15 %
	private static final int POWER_THRESHOLD = 100;
	
	private static final int PULSELENGTH_GOAL1 = 10000;
	private static final int PULSELENGTH_GOAL1_DEVIATION = 2000;
	private static final int PULSELENGTH_GOAL2 = 5000;
	
	
	private static final int FINDSTATE_SEESTRAIGHT = RobotStatus.SEE_STRAIGHT;
	private static final int FINDSTATE_SEELEFT= RobotStatus.SEE_LEFT;
	private static final int FINDSTATE_SEERIGHT = RobotStatus.SEE_RIGHT;
	private static final int FINDSTATE_TURNAROUND= 100;
	private static final int FINDSTATE_DRIVERANDOM = 101;
	private static final int FINDSTATE_SEEREALLY = 102;
	
	
	private int myPulslength;
	private int seeTemp;
	private int seeTempCounter;
	
	private int lastPower = POWER_THRESHOLD;
	
	private UDPConnectionHandler robot;	
	private BlinkTask blink, blinkLeft, blinkRight;
	
	
	private int findState = 0;
	
	public EnergyManager (UDPConnectionHandler robot, int chargeStation) {
		
		this.robot = robot;
		robot.registerEnergyListener(energyListener);
		
		if (chargeStation == 1) {
			myPulslength = PULSELENGTH_GOAL1;
		}
		if (chargeStation == 2) {
			myPulslength = PULSELENGTH_GOAL2;
		}
		
	}
	
	OnRobotMessageReceived energyListener = new OnRobotMessageReceived() {
		
		@Override
		public void messageReceived(UDPConnectionHandler robot, byte[] data) {
			
			RobotStatus rs = new RobotStatus(data);
			
			if (hasLowBattery(rs) && !isCharging(rs)) {
				startBlink();
				robot.blockControllerCommands(true);
				findChargeStation(rs);
			} else if (hasLowBattery(rs) && isCharging(rs)) {
				stopBlink();
			}
		}

	};
	
	private boolean hasLowBattery(RobotStatus rs) {
		
		if (rs.getAkku() <= POWER_THRESHOLD) {
			return true;
		}
		return false;
	}
	
	
	
	private void findChargeStation (RobotStatus rs) {
		
		RobotCommand rc = new RobotCommand(false, false, 0, 0);
		int seeStation = rs.seeStation();
		
		switch (findState) {
		
		case FINDSTATE_TURNAROUND:
			
			// seen something? get sure
			if (seeStation != 0)
				findState = FINDSTATE_SEEREALLY;
			
			// turn around
			else {
				rc = new RobotCommand(false, false, -10, 10);
				seeTempCounter++;
				
			}
			
			// turned many times around, drive random			
			if (seeTemp == 0 && seeTempCounter > 75) {
				findState = FINDSTATE_DRIVERANDOM;
				seeTempCounter = 0;
			}
			
			break;
			
		case FINDSTATE_DRIVERANDOM:
			
			// drive some loops forward
			if (seeTempCounter < 25) {
				rc = new RobotCommand(false,  false, 20,  20);
				seeTempCounter++;
			} else {
				
				findState = FINDSTATE_SEEREALLY;
			}
			
			break;
			
		case FINDSTATE_SEELEFT:
			
			// first turn around a little bit
			if (seeTempCounter < 5) {
				rc = new RobotCommand(false,  false,  10, -10);
				seeTempCounter++;
			} 
			
			// then drive forward
			else if (seeTempCounter < 25){
				rc = new RobotCommand(false,  false,  -10,  -10);
				seeTempCounter++;
			} else {
				findState = FINDSTATE_SEEREALLY;
				seeTempCounter = 0;
				seeTemp = 0;
			}
			
			
			break;
			
		case FINDSTATE_SEERIGHT:
			
			if (seeTempCounter < 5) {
				rc = new RobotCommand(false,  false,  -10, 10);
				seeTempCounter++;
			} else if (seeTempCounter < 25){
				rc = new RobotCommand(false,  false,  -10,  -10);
				seeTempCounter++;
			} else {
				findState = FINDSTATE_SEEREALLY;
				seeTempCounter = 0;
				seeTemp = 0;
			}
			break;
			
		case FINDSTATE_SEESTRAIGHT:
			
			if (seeStation == RobotStatus.SEE_STRAIGHT)
				rc = new RobotCommand(false,  false, -20,  -20);
			else {
				findState = FINDSTATE_SEEREALLY;
			}
			break;
			
		case FINDSTATE_SEEREALLY:
			
			// see repeatedly, count
			if (seeStation == seeTemp) {
				seeTempCounter++;
			} 
			// see for the first time, begin count
			else {
				seeTemp = seeStation;
				seeTempCounter = 0;
			}
			
			// seen the same light for more than 2 times
			if (seeTemp != 0 && seeTempCounter > 2) {
				//drive straight, right or left
				
				seeTemp = 0;
				seeTempCounter = 0;
				findState = seeTemp;
			}
			
			// seen nothing for more than 5 times
			if (seeTemp == 0 && seeTempCounter > 5) {
				findState = FINDSTATE_TURNAROUND;
			}
			
			break;
		}
		
		
		robot.send(rc.getBytes());
		
	}
	
	
	private void startBlink() {
		
		// No Blinktask existing, or one existing but not alive
		if (blink == null) {
			
			blink = new BlinkTask(myPulslength);
			blink.start();
		}
		if (blinkLeft == null) {
			
			blinkLeft = new BlinkTask(myPulslength-PULSELENGTH_GOAL1_DEVIATION);
			blinkLeft.start();
		}
		if (blinkRight == null) {
			
			blinkRight = new BlinkTask(myPulslength + PULSELENGTH_GOAL1_DEVIATION);
			blinkRight.start();
		}
	}
	
	private void stopBlink() {

		if (blink != null) {
			blink.stopBlink();
			blink = null;
		}
		if (blinkRight != null) {
			blinkRight.stopBlink();
			blinkRight = null;
		}
		if (blinkLeft != null) {
			blinkLeft.stopBlink();
			blinkLeft = null;
		}
	}
	
	//TODO
	private boolean isCharging(RobotStatus rs) {
		
		if (lastPower < rs.getAkku())
			return true;
		
		lastPower = rs.getAkku();
		return false;
	}
	
	
	
	private class BlinkTask extends Thread {

		Process process;
		int length;
		
		public BlinkTask(int pulslength) {
			length = pulslength;
		}
		
		@Override
		public void run() {

			System.out.println("Start Blink:");
			try {
				
				
			    process = new ProcessBuilder("./blinkIR", String.valueOf(length)).start();
			    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			    StringBuilder builder = new StringBuilder();
			    String line = null;
			    
			    while ( (line = reader.readLine()) != null) {
			    	builder.append(line);
			    	builder.append(System.getProperty("line.seperator"));
			    }
			    System.out.println(builder.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		public void stopBlink() {
			process.destroyForcibly();
			System.out.println("Blink stopped.");
		}
	}
}
