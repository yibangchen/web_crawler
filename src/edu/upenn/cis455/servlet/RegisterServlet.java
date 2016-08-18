package edu.upenn.cis455.servlet;

import java.io.*;
import java.rmi.server.UID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sleepycat.persist.EntityStore;

import edu.upenn.cis455.storage.DatabaseWrapper;
import edu.upenn.cis455.storage.UserDA;
import edu.upenn.cis455.storage.User;

/**
 * Servlet implementation class RegisterServlet
 */
//@WebServlet(name="registerServlet", urlPatterns={"/register"})
public class RegisterServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res) 
			throws ServletException, IOException {

		res.setContentType("text/html");  
		PrintWriter pw=res.getWriter();
		res.sendRedirect("register.html");
		pw.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		EntityStore store = new DatabaseWrapper(getServletContext().getInitParameter("BDBstore")).getStore();
		String userName = request.getParameter("username");
		String password = request.getParameter("password");
		
		if(userName ==null || password == null){
			response.sendRedirect("register.html#error");
		}
		else if(isUserRegisterd(userName, store)){
			response.sendRedirect("register.html#error");
		}
		else{
			User user = new User();
			UID id = new UID();
			user.setUserId(id.toString());
			user.setName(userName);
			user.setPassword(password);
			user.setUserType("General");
			user.setLastLogin(0);
			UserDA accessor = new UserDA(store);
			accessor.putEntity(user);
			response.sendRedirect("register.html#success");
		}
	}
	
	private boolean isUserRegisterd(String userName, EntityStore store) {
		UserDA accessor = new UserDA(store);
		User entity =accessor.fetchEntityFromSecondaryKey(userName);
		if(entity==null)	return false;
		return true;
	}

}
