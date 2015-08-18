package connection;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class TCPConnectionHandler extends Channel {

	private OutputStream out;
	private Socket socket;

	public TCPConnectionHandler(StateListener stateListener) {
		super(stateListener);
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
		new Thread(this).start();
	}

	@Override
	public void sendMessage(byte[] data) {

		if (out != null && !socket.isClosed()) {

			try {
				out.write(data);
				out.flush();
			} catch (IOException e) {
				System.out
						.println("Error in TCPConnectionHandler:sendMessage \n"
								+ e.getMessage());
				close();
			}
		}
	}

	@Override
	public void close() {
		running = false;
		cc.stopControl();

		if (!socket.isClosed()) {
			try {
				socket.close();
			} catch (IOException e) {
			}
			stateListener.stateChanged(this,
					ConnectionManager.STATE_DISCONNECTED);
		}
	}

	@Override
	public void run() {

		running = true;

		cc = new ConnectionControl(this);
		cc.startControl();

		try {
			DataInputStream in = new DataInputStream(socket.getInputStream());

			out = socket.getOutputStream();

			stateListener.stateChanged(this, ConnectionManager.STATE_CONNECTED);

			byte[] pack = null;
			if (socket.getLocalPort() == Channel.PORT_ROBOT) {
				pack = new byte[PACKETSIZE_ROBOT];
			}
			if (socket.getLocalPort() == Channel.PORT_CLIENT) {
				pack = new byte[PACKETSIZE_CLIENT];
			}

			while (running && in.read(pack, 0, pack.length) != -1) {

				cc.update();

				if (pack.length != 0 && messageListener != null) {
					messageListener.messageReceived(pack);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Socket closed unexpectedly!");
		} finally {
			close();
		}
	}

}
