package connection;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

@SuppressWarnings("serial")
public class WebsocketServlet extends WebSocketServlet {
	
	
    @Override
    public void configure(WebSocketServletFactory factory) {
    	factory.getPolicy().setIdleTimeout(0);
        factory.register(WebsocketSocket.class);
    }

}
