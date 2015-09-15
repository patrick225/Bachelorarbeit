package connection;

import org.json.simple.JSONObject;

public interface OnControllerMessageReceived {

	public void messageReceived(WebsocketSocket controller, JSONObject data);
}
