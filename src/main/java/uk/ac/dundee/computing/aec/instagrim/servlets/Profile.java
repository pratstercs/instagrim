/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.lib.Convertors;
import uk.ac.dundee.computing.aec.instagrim.models.User;
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;


/**
 *
 * @author Phil
 */
@WebServlet(urlPatterns = {
    "/Profile",
    "/Profile/*",
    "/editProfile",
    "/editProfile/*"
})
@MultipartConfig
public class Profile extends HttpServlet{
    Cluster cluster=null;
    private HashMap CommandsMap = new HashMap();
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Profile() {
        super();
        // TODO Auto-generated constructor stub
        CommandsMap.put("Profile", 1);
        CommandsMap.put("editProfile", 2);
    }
    
    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
        cluster = CassandraHosts.getCluster();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session=request.getSession();
        
        LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
        boolean checkPass = false;
        
        String username = request.getParameter("username");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        String[] address = { request.getParameter("street"), request.getParameter("city"), request.getParameter("postcode") };
        
        try {
            String encodedPass = User.encodePass(password);
            checkPass = lg.comparePass(encodedPass);
        }
        catch (UnsupportedEncodingException | NoSuchAlgorithmException et) {
            System.out.println( "Could not encode password " + et);
        }
        catch (NullPointerException npe) {
            System.out.println("No password inputted " + npe);
            checkPass = false;
        }
        
        User us = new User();
        
        if(checkPass) {    
            us.updateUser(username, firstName, lastName, email, address, lg.getProfilePic());
        }
        
        LoggedIn newlg = new LoggedIn();
        lg.setUsername(username);
        
        newlg = us.getUserData(lg);
        session.setAttribute("LoggedIn", newlg);
        
	response.sendRedirect("/Instagrim/Profile");
        
    }
    
//    /**
//     * Method to test if string is empty
//     */
//    public static boolean checkEmpty(String text) {
//        if( text != null && !text.isEmpty() ) {
//            return false;
//        }
//        else {
//            return true;
//        }
//    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String args[] = Convertors.SplitRequestPath(request);
        int command;
        RequestDispatcher rd;
        
        try {
            command = (Integer) CommandsMap.get(args[1]);
        } catch (Exception et) {
            error("Bad Operator", response);
            return;
        }
        switch (command) {
            case 2:
                rd = request.getRequestDispatcher("/EditProfile.jsp");
                break;
            case 1:
                rd = request.getRequestDispatcher("/UserProfile.jsp");
                break;
            default:
                rd = request.getRequestDispatcher("/UserProfile.jsp");
                error("Bad Operator", response);
        }
        
        //RequestDispatcher rd = request.getRequestDispatcher("/UserProfile.jsp");
        rd.forward(request, response);
    }
    
        private void error(String mess, HttpServletResponse response) throws ServletException, IOException {

        PrintWriter out = null;
        out = new PrintWriter(response.getOutputStream());
        out.println("<h1>You have a an error in your input</h1>");
        out.println("<h2>" + mess + "</h2>");
        out.close();
        return;
    }
}
