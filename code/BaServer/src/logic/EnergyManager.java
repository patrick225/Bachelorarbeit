package logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import message.RobotCommand;
import message.RobotStatus;
import connection.OnRobotMessageReceived;
import connection.UDPConnectionHandler;

public class EnergyManager {

	// threshold on 15 %
	private static final byte POWER_THRESHOLD = (byte) 0x0F;
	
	private static final byte PULSELENGTH_GOAL1 = (byte) 0x00;
	private static final byte PULSELENGTH_GOAL2 = (byte) 0xFF;
	
	private static final byte TOLERANCE = (byte) 0x0A;
	
	private byte myPulslength;
	
	private UDPConnectionHandler robot;
	private BlinkTask blink;
	
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
		if (rs.getPulseLength() <= myPulslength + TOLERANCE && 
			rs.getPulseLength() >= myPulslength - TOLERANCE) {
			
			rc = new RobotCommand(false, false, 10, 10);
			
		} else {
			
			rc = new RobotCommand(false, false, -5, 5);
			
		}
		
		robot.send(rc.getBytes());
	}
	
	
	private void startBlink() {
		
		// No Blinktask existing, or one existing but not alive
		if (blink == null ||
			(blink != null && !blink.isAlive())) {
			
			blink = new BlinkTask();
			blink.start();
		}
	}
	
	private void stopBlink() {

		if (blink != null && blink.isAlive()) {
			blink.stopBlink();
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

			try {
			    process = new ProcessBuilder("./test").start();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		public void stopBlink() {
			process.destroyForcibly();
		}
	}
}
