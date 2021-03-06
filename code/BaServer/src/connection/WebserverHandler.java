package connection;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
 
public class WebserverHandler extends AbstractHandler {
	
 
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    	
    	FileReader fr = new FileReader("web/index.html");
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        char[] content = new char[100000];
        fr.read(content);
        fr.close();
        response.getWriter().write(String.valueOf(content));
        baseRequest.setHandled(true);
    }

}