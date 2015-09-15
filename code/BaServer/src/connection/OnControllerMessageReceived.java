package connection;

import message.ControllerCommand;

public interface OnControllerMessageReceived {

	public void messageReceived(WebsocketSocket controller, ControllerCommand command);
}
