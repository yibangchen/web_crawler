package edu.upenn.cis455.servlet;

import java.io.IOException;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sleepycat.persist.EntityStore;

import edu.upenn.cis455.storage.DatabaseWrapper;
import edu.upenn.cis455.storage.ChannelDA;
import edu.upenn.cis455.storage.UserChannelDA;
import edu.upenn.cis455.storage.Channel;
import edu.upenn.cis455.storage.UserChannelMap;
import edu.upenn.cis455.crawler.HelperFunctions;

/**
 * This servlet creates a new channel
 * @author cis455
 *
 */

public class ChannelCreator extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChannelCreator() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		HttpSession session = request.getSession(false);
		if(session==null || !HelperFunctions.isValidSession(session)){
			response.sendRedirect("xpath");
		}
		else{			
			String channelName = request.getParameter("channelname");
			String channelXPaths = request.getParameter("channelpaths");
			String channelstylesheet = request.getParameter("channelstylesheet");
			
			if(channelName==null|| channelXPaths==null || channelstylesheet ==null){
				response.sendRedirect("homeServlet#createerror");					
			}
			else{
				String userId = session.getAttribute("userid").toString();
				new DatabaseWrapper(getServletContext().getInitParameter("BDBstore"));
				EntityStore store = DatabaseWrapper.getStore();
				ChannelDA accessor = new ChannelDA(store);
				UserChannelDA userChannelDA = new UserChannelDA(store);
				UserChannelMap userChannel = userChannelDA.fetchEntityFromPrimaryKey(userId);
				
				if(userChannel==null){
					Channel entity = new Channel();
					entity.setChannelId(HelperFunctions.getUniqueId());
					entity.setChannelName(channelName);
					entity.setXPath(channelXPaths);
					entity.setXls(channelstylesheet);
					entity.setCreator(userId);
					entity.setLastModified(new Date().getTime());
					accessor.putEntity(entity);
					
					userChannel = new UserChannelMap();
					userChannel.setUserId(userId);
					Set<String> ids = new HashSet<String>();
					ids.add(entity.getChannelId());
					userChannel.setChannel(ids);
					userChannel.setMappingId(HelperFunctions.getUniqueId());
					userChannelDA.putEntity(userChannel);
										
				}else{
					
					Set<String> channels = userChannel.getChannelSet();
					for(String id:channels){
						Channel channelEnt = accessor.fetchEntityFromPrimaryKey(id);
						if(channels.contains(channelName)){
							response.sendRedirect("homeServlet#uniqueerror");					
						}
					}
					Channel entity = new Channel();
					entity.setChannelId(HelperFunctions.getUniqueId());
					entity.setChannelName(channelName);
					entity.setXPath(channelXPaths);
					entity.setXls(channelstylesheet);
					entity.setCreator(userId);
					entity.setLastModified(new Date().getTime());
					accessor.putEntity(entity);
					
					channels.add(entity.getChannelId());
					userChannel.setChannel(channels);
					userChannelDA.putEntity(userChannel);
				}
				response.sendRedirect("homeServlet#createsuccess");
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		doGet(request, response);
	}
}
