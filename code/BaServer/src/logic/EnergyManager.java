package logic;

import java.io.IOException;

import message.RobotCommand;
import message.RobotStatus;
import connection.OnRobotMessageReceived;
import connection.UDPConnectionHandler;

public class EnergyManager {

	// threshold on 15 %
	private static final byte POWER_THRESHOLD = (byte) 0x64;
	
	private static final byte PULSELENGTH_GOAL1 = (byte) 0x64;
	private static final byte PULSELENGTH_GOAL2 = (byte) 0xFF;
	
	private static final byte TOLERANCE = (byte) 0x50;
	
	private static final long TIMEOUT = 5000;
	
	private byte myPulslength;
	
	private UDPConnectionHandler robot;
	private BlinkTask blink;
	private long lastSignal = System.currentTimeMillis();
	private int sequenceCounter = 0;
	
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
		
		
		RobotCommand rc = null;
		// got signal
		if (rs.getPulseLength() <= myPulslength + TOLERANCE && 
			rs.getPulseLength() >= myPulslength - TOLERANCE) {
			
			rc = new RobotCommand(false, false, 15, 15);
			lastSignal = System.currentTimeMillis();
			sequenceCounter = 0;
		} 
		// no signal for too long time, drive random for 5 sequences
		else if(lastSignal < System.currentTimeMillis() - TIMEOUT  && sequenceCounter < 5) {
			rc = new RobotCommand(false, false, 15, 15);
			sequenceCounter++;
		} else {
			
			rc = new RobotCommand(false, false, -10, 10);
			sequenceCounter = 0;
		}
		
		robot.send(rc.getBytes());
	}
	
	
	private void startBlink() {
		
		// No Blinktask existing, or one existing but not alive
		if (blink == null) {
			
			blink = new BlinkTask();
			blink.start();
		}
	}
	
	private void stopBlink() {

		if (blink != null) {
			blink.stopBlink();
			blink = null;
		}
	}
	
	//TODO
	private boolean isCharging(RobotStatus rs) {
		
		if (rs.getAkku() == (byte) 0x64) {
			return false;
		}
		
		return false;
	}
	
	
	
	private class BlinkTask extends Thread {

		Process process;
		
		@Override
		public void run() {

			System.out.println("Start Blink:");
			try {
			    process = new ProcessBuilder("./test").start();
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
