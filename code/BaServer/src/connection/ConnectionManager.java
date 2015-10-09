package connection;

import logic.EnergyManager;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;




public class ConnectionManager {
	
	private static volatile ConnectionManager instance = null;

	private OnPlayerReady playerReadyListener;
	private boolean player1Ready = false;
	private boolean player2Ready = false;
	
	
	BiMap<WebsocketSocket, UDPConnectionHandler> mapping;
	
	private WebsocketSocket controller1;
	private WebsocketSocket controller2;
	
	private UDPConnectionHandler robot1;
	private UDPConnectionHandler robot2;
	
	private UDPConnectionHandler robot1Static;
	private UDPConnectionHandler robot2Static;
	
	

	private ConnectionManager() {
		
		// map robots to controllers and reverse
		mapping = HashBiMap.create();
	
		// server for controllers
		new Thread(new Webserver()).start();		
		
		// server for robots
		new Thread(new UDPSocketProvider()).start();
		
		// webserver for webcontroller
	}
	
	public static synchronized ConnectionManager getInstance() {
		
		if (instance == null) {
			instance = new ConnectionManager();
		}
		return instance;
	}
	
	public void registerController(WebsocketSocket socket) {
		
		if (controller1 == null) {
			controller1 = socket;
		} else {
			controller2 = socket;
		}
		
		printStatus();
		checkForReadyPlayers();
	}
	
	public void unregisterController(WebsocketSocket socket) {
		
		if (socket.equals(controller1)) {
			controller1 = null;
		}
		if (socket.equals(controller2)) {
			controller2 = null;
		}
		mapping.remove(socket);
		printStatus();
		checkForReadyPlayers();
	}
	
	
	public void registerRobot(UDPConnectionHandler robot) {
		//TODO
		if (robot.equals(robot1Static)) {
			robot1 = robot;
			new EnergyManager(robot, 1);
		}
		if (robot.equals(robot2Static)) {
			robot2 = robot;
//			new EnergyManager(robot, 2);
		}
		
		if (robot1 == null) { 
			robot1 = robot;
			new EnergyManager(robot, 1);
		}
		else if (robot2 == null) {
			robot2 = robot;
//			new EnergyManager(robot, 2);
		}
		
		printStatus();
		checkForReadyPlayers();
	}
	
	
	public void unregisterRobot(UDPConnectionHandler robot) {
		
		if (robot.equals(robot1)) {
			robot1Static = robot1;
			robot1 = null;
		}
		if (robot.equals(robot2)) {
			robot2Static = robot2;
			robot2 = null;
		}
		
		printStatus();
		checkForReadyPlayers();
	}
	
	
	public void registerPlayerReadyListener(OnPlayerReady playerListener) {
		playerReadyListener = playerListener;
	}
	
	
	private void checkForReadyPlayers() {
				
		// player1 was not ready, but is now
		if (!player1Ready && getController1() != null && getRobot1() != null) {
			playerReadyListener.playerIsReady(1, true);
			player1Ready = true;
			mapping.put(getController1(), getRobot1());
		}
		
		// player1 was ready, but is no more
		if (player1Ready && !(getController1() != null && getRobot1() != null)) {
			playerReadyListener.playerIsReady(1, false);
			player1Ready = false;
		}
		
		// player2 was not ready, but is now
		if (!player2Ready && getController2() != null && getRobot2() != null) {
			playerReadyListener.playerIsReady(2, true);
			player2Ready = true;
			mapping.put(getController2(), getRobot2());
		}
		
		// player2 was ready, but is no more
		if (player2Ready && !(getController2() != null && getRobot2() != null)) {
			playerReadyListener.playerIsReady(2, false);
			player2Ready = false;
		}
		
	}

	private synchronized void printStatus() {

		System.out.println("------------------");
		System.out.println("Connections:");
		System.out.println("Controller1: "
				+ String.valueOf(controller1 != null));
		System.out.println("Controller2: "
				+ String.valueOf(controller2 != null));
		System.out.println("Robot1: " 
				+ String.valueOf(robot1 != null));
		System.out.println("Robot2: " 
				+ String.valueOf(robot2 != null));
		System.out.println("------------------");

	}
	
	
	public WebsocketSocket getController1() {
		return controller1;
	}
	
	public WebsocketSocket getController2() {
		return controller2;
	}
	
	public UDPConnectionHandler getRobot1() {
		return robot1;
	}
	
	public UDPConnectionHandler getRobot2() {
		return robot2;
	}
	
	public UDPConnectionHandler getRobotByController (WebsocketSocket controller) {
		return mapping.get(controller);
	}
	
	public WebsocketSocket getControllerByRobot (UDPConnectionHandler robot) {
		return mapping.inverse().get(robot);
	}
	
}
