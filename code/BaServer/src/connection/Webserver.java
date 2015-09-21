package connection;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Webserver implements Runnable {

	@Override
	public void run() {
		
		Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
        server.addConnector(connector);

        // Setup the basic application "context" for this application at "/"
        // This is also known as the handler tree (in jetty speak)
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/control/");
               
        // Add a websocket to a specific path spec
        ServletHolder holderEvents = new ServletHolder("ws-events", WebsocketServlet.class);
        context.addServlet(holderEvents, "/");
        
        ContextHandler webserverContext = new ContextHandler();
        webserverContext.setContextPath("/");
        Handler webserverHandler = new WebserverHandler();
        webserverContext.setHandler(webserverHandler);
        
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[]{context, webserverContext});
        
        HandlerCollection handlers = new HandlerCollection();
        handlers.setHandlers(new Handler[]{contexts, new DefaultHandler()});
        
        server.setHandler(handlers);

        try
        {
            server.start();
            server.dump(System.err);
            server.join();
        }
        catch (Throwable t)
        {
            t.printStackTrace(System.err);
        }
		
	}

}
