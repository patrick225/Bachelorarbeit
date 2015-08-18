package connection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class UDPConnectionHandler extends Channel {

	private UDPSocketProvider socketProv;

	public UDPConnectionHandler(StateListener stateListener) {
		super(stateListener);

		socketProv = UDPSocketProvider.getInstance();

	}

	@Override
	public void run() {

	}

	@Override
	public void sendMessage(byte[] data) {

		try {
			socketProv.send(this, data);
		} catch (IOException e) {
			System.out.println("Error in UDPconnectionHandler:sendMessage \n"
					+ e.getMessage());
			close();
		}
	}

	@Override
	public synchronized void close() {
		cc.stopControl();
		stateListener.stateChanged(this, ConnectionManager.STATE_DISCONNECTED);
		first = true;
	}

	static boolean first = true;
	public void incomingMessage(DatagramPacket packet) {

		if (first) {
			cc = new ConnectionControl(this);
			cc.startControl();
			stateListener.stateChanged(this, ConnectionManager.STATE_CONNECTED);
			first = false;
		}
		cc.update();
		if (messageListener != null) {
			messageListener.messageReceived(packet.getData());
		}

	}

}
