package connection;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Stack;


public class TCPConnector extends Thread {	
		
	private int port;
	
	private ServerSocket serverSocket;
	
	private Stack<TCPConnectionHandler> connectionQueue = new Stack<TCPConnectionHandler>();
		
	
	public TCPConnector(int port) {
		this.port = port;		
				
		start();
	}
	
	public void connectHandler(TCPConnectionHandler handler) {
		connectionQueue.add(handler);		
	}
	
	public void close () {
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		super.run();
				
		 try {			 		
			 serverSocket = new ServerSocket(port);
			
			 while (true) {
				 
				 if (connectionQueue.size() != 0) {
					 Socket client = serverSocket.accept();					 
					 connectionQueue.pop().setSocket(client);
				 }
			 }
			 			 			 
		 } 
		 catch (Exception e) {
			 e.printStackTrace();
			 System.out.println("Socket was closed!");
		 } 
		
		
	}
			
}

