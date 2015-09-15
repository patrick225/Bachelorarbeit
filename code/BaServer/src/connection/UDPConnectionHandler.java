package connection;

import java.net.DatagramPacket;
import java.net.SocketAddress;

public class UDPConnectionHandler {

	private UDPSocketProvider socketProv;
	private ConnectionManager cm;
	
	private SocketAddress address;
	private ConnectionControl cc;
	
	OnRobotMessageReceived messageListener;

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
		if (messageListener != null) {
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
