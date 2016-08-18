package edu.upenn.cis455.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.sleepycat.persist.EntityStore;

import edu.upenn.cis455.storage.DatabaseWrapper;
import edu.upenn.cis455.storage.User;
import edu.upenn.cis455.storage.UserDA;

/**
 * Servlet implementation class LoginServlet
 */
//@WebServlet(name="LoginServlet", urlPatterns={"/loginServlet"})
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");		
//		System.out.println("********" + username + " " + password);
		
		try{
			if(username==null || password == null || username.length() ==0 || password.length() ==0){
				response.sendRedirect("xpath");
				System.out.println("******** Invalid username or password! - retry");
			}
			new DatabaseWrapper(getServletContext().getInitParameter("BDBstore"));
			EntityStore store = DatabaseWrapper.getStore();
			if(!isLoginValid(username, password,store)){				
				response.sendRedirect("xpath#error");
			}else{
				UserDA accessor = new UserDA(store);
				HttpSession session = request.getSession();
				session.setAttribute("userid", accessor.fetchEntityFromSecondaryKey(username).getUserId());
				response.sendRedirect("homeServlet");
			}
		}catch(IOException ioe){
		}	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		doGet(request, response);
	}
	
	private boolean isLoginValid(String username, String password,EntityStore store){
		UserDA user = new UserDA(store);
		User entity = user.fetchEntityFromSecondaryKey(username);
		
		if(entity != null){
			if (entity.getPassword().equals(password))
				return true;
		}		
		return false;
	}

}
