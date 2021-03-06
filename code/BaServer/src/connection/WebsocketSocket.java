package connection;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class WebsocketSocket extends WebSocketAdapter {
	
	private static int playerCount = 0;
	
	private OnControllerMessageReceived messageListener;
	ConnectionManager cm;
	
	public WebsocketSocket() {
		super();
		cm = ConnectionManager.getInstance();
	}

    @Override
    public void onWebSocketConnect(Session sess) {
    	
    	
    	if (playerCount < 2) {  
	        super.onWebSocketConnect(sess);
	        System.out.println("Socket Connected: " + sess.getRemoteAddress());

	        playerCount++;
	        cm.registerController(this);
	        
    	} else {
    		System.out.println("Already two Controllers!");
    	}
    }
    
    @Override
    public void onWebSocketText(String message)
    {
        super.onWebSocketText(message);
   
        if (messageListener != null) {
        	notifyListener(message);
        }
        
    }
    
    
    
    @Override
    public void onWebSocketClose(int statusCode, String reason)
    {
        super.onWebSocketClose(statusCode,reason);
        System.out.println("Socket Closed: [" + statusCode + "] " + reason);
        
        cm.unregisterController(this);
        playerCount--;
    }
    
    @Override
    public void onWebSocketError(Throwable cause)
    {
        super.onWebSocketError(cause);
        cause.printStackTrace(System.err);
        
        cm.unregisterController(this);
        playerCount--;
    }
    
    
    public void registerMessageListener (OnControllerMessageReceived messageListener) {
    	this.messageListener = messageListener;
    }
    
    public void unregisterMessageListener () {
    	this.messageListener = null;
    }
    
    private void notifyListener(String message) {
    	
    	JSONParser parser = new JSONParser();
		try {
			JSONObject obj = (JSONObject) parser.parse(message);
			messageListener.messageReceived(this, obj);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	public void send(String message) {

		try {
			getRemote().sendString(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void send(byte[] data) {
		ByteBuffer buffer = ByteBuffer.wrap(data);
		try {
			getRemote().sendBytes(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
