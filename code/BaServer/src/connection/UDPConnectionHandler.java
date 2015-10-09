package connection;

import java.net.DatagramPacket;
import java.net.SocketAddress;

import message.RobotStatus;

public class UDPConnectionHandler {

	private UDPSocketProvider socketProv;
	private ConnectionManager cm;
	
	private SocketAddress address;
	private ConnectionControl cc;
	
	OnRobotMessageReceived messageListener;
	OnRobotMessageReceived energyListener;
	
	private boolean blockControllerCommands = false;

	public UDPConnectionHandler(UDPSocketProvider socketProv) {

		this.socketProv = socketProv;
		address = socketProv.getAddressByHandler(this);
		cm = ConnectionManager.getInstance();
		cm.registerRobot(this);
		
		
		cc = new ConnectionControl(this);
		cc.startControl();

	}


	public void incomingMessage(DatagramPacket packet) {

		cc.update();
		System.out.println(new RobotStatus(packet.getData()));
		energyListener.messageReceived(this, packet.getData());
		if (messageListener != null && !blockControllerCommands) {
			messageListener.messageReceived(this, packet.getData());
		}
	}
	
	
	public void send(byte[] data) {
		socketProv.send(this, data);
	}
	
	
    public void registerMessageListener (OnRobotMessageReceived messageListener) {
    	this.messageListener = messageListener;
    }
    
    
    public void unregisterMessageListener () {
    	this.messageListener = null;
    }
    
    
    public void registerEnergyListener (OnRobotMessageReceived energyListener) {
    	this.energyListener = energyListener;
    }
    
    public void blockControllerCommands (boolean block) {
    	blockControllerCommands = block;
    }
    
    public void onTimeout() {
    	cm.unregisterRobot(this);
    	socketProv.unsetRobot(this);
    }
    
    
	@Override
	public boolean equals(Object obj) {
		try {
			UDPConnectionHandler other = (UDPConnectionHandler) obj;
			
			if (other.address.equals(this.address))
				return true;
			return false;
		} catch (NullPointerException e) {
			return super.equals(obj);
		}
	}
	
	
	@Override
	public int hashCode() {
		
		try {
			int sum = 0;
			
			String addrStr = address.toString();
			for (int i = 0; i < addrStr.length(); i++) {
				sum += Integer.valueOf(addrStr.charAt(i));
			}
		
			return sum;
		} catch (NullPointerException e) {
			return super.hashCode();
		}
	}
	
	

}
