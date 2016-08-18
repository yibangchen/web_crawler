package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sleepycat.persist.EntityStore;

import edu.upenn.cis455.storage.DatabaseWrapper;
import edu.upenn.cis455.crawler.HelperFunctions;

/**
 * Servlet implementation class ChannelServlet
 */
public class ChannelServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChannelServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		EntityStore store = new DatabaseWrapper(getServletContext().getInitParameter("BDBstore")).getStore();
		InputStream  stream = getServletContext().getResourceAsStream("/ChannelList.html");
		String content = HelperFunctions.buildString(stream);		
		content = content.replace("%%%Insert Table here%%%", HelperFunctions.getAllChannels(store));
		try{
			response.getWriter().write(content);
		}catch(IOException ioe){
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
