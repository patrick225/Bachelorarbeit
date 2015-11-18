package logic;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Base64;

import org.json.simple.JSONObject;

import com.sun.xml.internal.ws.util.ByteArrayBuffer;

import connection.OnControllerMessageReceived;
import connection.OnRobotMessageReceived;
import connection.UDPConnectionHandler;
import connection.WebsocketSocket;
import message.ControllerStatus;
import message.RobotStatus;


public class Player {

	
	private WebsocketSocket controller;
	
	private UDPConnectionHandler robot;
	
	private int score = 0;
	
	private CommandTask commandTaskToRobot;
	private CommandTask commandTaskToController;
	
	private ByteArrayBuffer pictureBuf = new ByteArrayBuffer();
	
	
	public void setDevices(UDPConnectionHandler robot, WebsocketSocket controller) {
		
		this.controller = controller;
		this.robot = robot;
		
		commandTaskToRobot = new CommandTask(robot);
		commandTaskToController = new CommandTask(controller, 500);
		
		commandTaskToRobot.start();
		commandTaskToController.start();
		
		controller.registerMessageListener(messageListenerController);						
		robot.registerMessageListener(messageListenerRobot);
		
	}
	
	
	public void unsetDevices() {
		
		controller.unregisterMessageListener();
		robot.unregisterMessageListener();
		
		controller = null;
		robot = null;
		
		commandTaskToRobot.cancel();
		commandTaskToController.cancel();
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
	
	private void handlePictureData(byte[] data) {
		
		// fall vernachlässigt, dass FF im alten paket steht und D8 im nächsten
		for (int i = 0; i < data.length; i++) {
			
			if (data[i] == (byte) 0xFF && i < data.length -1 && data[i+1] == (byte) 0xD8 && pictureBuf.size() != 0) {

				sendPicture();
				pictureBuf.reset();
				
			}
			pictureBuf.write(data[i]);
		}
	}
	
	private void sendPicture() {
		
		//send as base64 String
		String base64 = Base64.getEncoder().encodeToString(pictureBuf.getRawData());
		controller.send(base64);
		
	}
	
	
	OnControllerMessageReceived messageListenerController = new OnControllerMessageReceived() {
		
		@Override
		public void messageReceived(WebsocketSocket controller, JSONObject command) {

			commandTaskToRobot.setCommand(CommandTranslater.translateCommand(command));
		}
	};
	
	
	OnRobotMessageReceived messageListenerRobot = new OnRobotMessageReceived() {
		
		@Override
		public void messageReceived(UDPConnectionHandler robot, byte[] data) {
			
			String score = Game.getScore();
			String[] scores = score.split(":");
			
			RobotStatus rs = new RobotStatus(data);
			ControllerStatus cs = new ControllerStatus();
			cs.setAkku(rs.getAkku());
			cs.setCountP1(Integer.valueOf(scores[0]));
			cs.setCountP2(Integer.valueOf(scores[1]));
			commandTaskToController.setCommand(cs);
			
			handlePictureData(rs.getPictureData());
		}
	};
}
