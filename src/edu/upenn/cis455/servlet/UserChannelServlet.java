package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.sleepycat.persist.EntityStore;

import edu.upenn.cis455.storage.Channel;
import edu.upenn.cis455.storage.ChannelDA;
import edu.upenn.cis455.storage.DatabaseWrapper;
import edu.upenn.cis455.storage.UserChannelDA;
import edu.upenn.cis455.storage.UserChannelMap;
import edu.upenn.cis455.crawler.HelperFunctions;

/**
 * Servlet implementation class HomeServlet
 */
public class UserChannelServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserChannelServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);

		if(session==null || !isValidSession(session)){
			response.sendRedirect("xpath");
		}
		else{
			new DatabaseWrapper(getServletContext().getInitParameter("BDBstore"));
			EntityStore store = DatabaseWrapper.getStore();
			InputStream stream = getServletContext().getResourceAsStream("/MyChannel.html");
			String content = HelperFunctions.buildString(stream);
			content = content.replace("%%REPLACE TABLE HERE%%", 
					getUserChannelContent(session.getAttribute("userid").toString(), store));
			response.getWriter().write(content);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		doGet(request, response);
	}
		
	private boolean isValidSession(HttpSession session){
		Date curr = new Date();
		try{
			if(!((session.getLastAccessedTime()-curr.getTime())>session.getMaxInactiveInterval()))
				return true;
		}catch(Exception e){
			System.err.println("Session error: " + 
					e.toString());
		}		
		return false;
	}
	
	private String getUserChannelContent(String userId,EntityStore store){	
		StringBuilder sb = new StringBuilder();
		sb.append("<table class=\"channelTable\"><tr><th>S.No.</th><th>Channel Name</th><th>Action</th></tr>");
		int ctr=1;
		
		UserChannelDA accessor = new UserChannelDA(store);
		ChannelDA channelAccessor = new ChannelDA(store);
		UserChannelMap userChannelEnt = accessor.fetchEntityFromSecondaryKey(userId);
		
		if(userChannelEnt!=null){
			Set<String> channelIds = userChannelEnt.getChannelSet();		
			for(String id: channelIds){
					Channel currChannel = channelAccessor.fetchEntityFromPrimaryKey(id);
					if(currChannel!=null){	
						sb.append("<tr><td>");
						sb.append(ctr++);
						sb.append("</td><td>");
						sb.append("<a href=\"getchannel?id=");
						sb.append(currChannel.getChannelId());
						sb.append("\">");
						sb.append(currChannel.getChannelName());
						sb.append("</a>");
						sb.append("</td><td>");
						sb.append("<a href=\"deletechannel?id=");
						sb.append(currChannel.getChannelId());
						sb.append("\">");
						sb.append("Delete");
						sb.append("</td></tr>");
					}	
			}
		}
		sb.append("</table>");
		return sb.toString();
	}
}
