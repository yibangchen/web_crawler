package edu.upenn.cis455.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.upenn.cis455.storage.DatabaseWrapper;
import edu.upenn.cis455.storage.UserDA;
import edu.upenn.cis455.storage.User;
import edu.upenn.cis455.crawler.UserObject;
import edu.upenn.cis455.crawler.HelperFunctions;


/**
 * This Servlet deletes a user channel
 * @author Yibang
 *
 */
public class ChannelModifier extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChannelModifier() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		handleRequest(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		handleRequest(request, response);
	}
	
	private void handleRequest(HttpServletRequest request, HttpServletResponse response){
		
		
		HttpSession session = request.getSession(false);
		try{
			
			if(session==null || !HelperFunctions.isValidSession(session)){
				response.sendRedirect("xpath");
			}else{
			
				String userId = (String)session.getAttribute("userid");
				String channelId = request.getParameter("id");
				if(userId==null || channelId==null){
					session.invalidate();
					response.sendRedirect("xpath");
				}else{
						
					UserDA accessor = new UserDA(DatabaseWrapper.getStore());
					User ent = accessor.fetchEntityFromPrimaryKey(userId);
					
					if(ent==null){
						session.invalidate();
						response.sendRedirect("xpath");
					}else{
						
						UserObject userInf = new UserObject(ent);
						if(!userInf.isUserChannelPresent(channelId)){
							session.invalidate();
							response.sendRedirect("xpath");
						}else{
							
							if(userInf.deleteChannel(channelId)){
								response.sendRedirect("homeServlet#deletesuccess");
							}else{
								response.sendRedirect("homeServlet#deleteerror");
							}
						}
					}
				}
			}
			
			
		}catch(IOException ioe){
		}catch(Exception e){
			session.invalidate();
			try{
				response.sendRedirect("xpath");
			}catch(IOException ioe){
			}
		}
	}
}
