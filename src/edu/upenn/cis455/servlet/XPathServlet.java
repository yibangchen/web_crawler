package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.*;

/**
 * Main servlet which shows the query screen
 * @author cis455
 *
 */

@SuppressWarnings("serial")
public class XPathServlet extends HttpServlet {	
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws IOException {
		response.setContentType("text/html");  
		PrintWriter pw=response.getWriter();
		response.sendRedirect("interface.html");
		pw.close();
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws IOException {		
		doGet(request, response);
	}
}


