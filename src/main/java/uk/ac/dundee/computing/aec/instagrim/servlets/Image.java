package uk.ac.dundee.computing.aec.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
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
import javax.servlet.http.Part;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.lib.Convertors;
import uk.ac.dundee.computing.aec.instagrim.models.PicModel;
import uk.ac.dundee.computing.aec.instagrim.models.User;
import uk.ac.dundee.computing.aec.instagrim.stores.*;

/**
 * Servlet implementation class Image
 */
@WebServlet(urlPatterns = {
    "/Image",
    "/Image/*",
    "/Thumb/*",
    "/Images",
    "/Images/*",
    "/ViewImage",
    "/ViewImage/*",
})
@MultipartConfig

public class Image extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Cluster cluster;
    private HashMap CommandsMap = new HashMap();
    
    

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Image() {
        super();
        // TODO Auto-generated constructor stub
        CommandsMap.put("Image", 1);
        CommandsMap.put("Images", 2);
        CommandsMap.put("Thumb", 3);
        CommandsMap.put("ViewImage", 4);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
        cluster = CassandraHosts.getCluster();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        String args[] = Convertors.SplitRequestPath(request);
        int command;
        try {
            command = (Integer) CommandsMap.get(args[1]);
        } catch (Exception et) {
            error("Bad Operator", response);
            return;
        }
        switch (command) {
            case 1:
                DisplayImage(Convertors.DISPLAY_PROCESSED, args[2], response);
                break;
            case 2:
                try {
                    DisplayImageList(args[2], request, response);
                }
                catch (NullPointerException npe) {
                    DisplayImageList("a",request,response);
                }
                break;
            case 3:
                DisplayImage(Convertors.DISPLAY_THUMB,args[2],  response);
                break;
            case 4:
                ImagePage(Convertors.DISPLAY_PROCESSED, args[2], request, response);
                break;
            default:
                error("Bad Operator", response);
        }
    }
    
    private void ImagePage(int type, String pic, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PicModel tm = new PicModel();
        tm.setCluster(cluster);
        
        java.util.UUID picid = java.util.UUID.fromString(pic);
        
        Pic p = tm.getPic(type,picid);
        java.util.LinkedList<Comment> commentList = tm.getComments(picid);
        
        RequestDispatcher rd = request.getRequestDispatcher("/ViewImage.jsp");
        
        request.setAttribute("picture", p);
        request.setAttribute("comments", commentList);
        
        rd.forward(request, response);
    }
    
    
    private void DisplayImageList(String User, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PicModel tm = new PicModel();
        tm.setCluster(cluster);
        java.util.LinkedList<Pic> lsPics = tm.getPicsForUser(User);
        RequestDispatcher rd = request.getRequestDispatcher("/UsersPics.jsp");
        request.setAttribute("Pics", lsPics);
        rd.forward(request, response);
    }

    private void DisplayImage(int type,String Image, HttpServletResponse response) throws ServletException, IOException {
        PicModel tm = new PicModel();
        tm.setCluster(cluster);
  
        
        Pic p = tm.getPic(type,java.util.UUID.fromString(Image));
        
        OutputStream out = response.getOutputStream();

        response.setContentType(p.getType());
        response.setContentLength(p.getLength());
        //out.write(Image);
        InputStream is = new ByteArrayInputStream(p.getBytes());
        BufferedInputStream input = new BufferedInputStream(is);
        byte[] buffer = new byte[8192];
        for (int length = 0; (length = input.read(buffer)) > 0;) {
            out.write(buffer, 0, length);
        }
        out.close();
    }
    
    private void upload(HttpServletRequest request, HttpServletResponse response, String username) throws ServletException, IOException {
        for (Part part : request.getParts() ) {
            System.out.println("Part Name " + part.getName());

            String type = part.getContentType();
            String filename = part.getSubmittedFileName();

            InputStream is = request.getPart(part.getName()).getInputStream();
            int i = is.available();
            int mode = PicModel.NORMAL;


            if (i > 0) {
                byte[] b = new byte[i + 1];
                is.read(b);
                System.out.println("Length : " + b.length);
                PicModel tm = new PicModel();
                tm.setCluster(cluster);
                tm.insertPic(b, type, filename, username, mode);

                is.close();
            }
        }
        response.sendRedirect("/InstagrimPJP/Images/"+username);
    }

    private void setProfilePic(HttpServletRequest request, HttpServletResponse response, String username, LoggedIn lg) throws IOException {
        User us = new User();
        String id;
        id = request.getParameter("picID");
        lg.setProfilePic(id);
        us.updateUser(lg);

        response.sendRedirect("/InstagrimPJP/Profile/"+username);
    }
    
    private void filter(HttpServletRequest request, HttpServletResponse response, String username) throws IOException {
        String filterMode;
        filterMode = request.getParameter("filterMode");
        int mode = Integer.parseInt(filterMode);
        String picID = request.getParameter("picID");

        PicModel tm = new PicModel();
        Pic pic = tm.getPic(Convertors.DISPLAY_IMAGE, java.util.UUID.fromString(picID));

        tm.setCluster(cluster);
        tm.insertPic(pic.getBytes(), pic.getType(), pic.getSUUID(), username, mode);


        response.sendRedirect("/InstagrimPJP/Images/"+username);
    }
    
    private void comment(HttpServletRequest request, HttpServletResponse response, String username) throws IOException {
        System.out.println("Comment recieved");

        String picID = request.getParameter("picID");
        String comment = request.getParameter("commentBox");
        java.util.UUID uuid = java.util.UUID.fromString(picID);

        PicModel tm = new PicModel();
        tm.setCluster(cluster);
        tm.postComment(uuid, comment, username);

        response.sendRedirect("/InstagrimPJP/ViewImage/" + picID);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session=request.getSession();
        LoggedIn lg = (LoggedIn)session.getAttribute("LoggedIn");
        String username="majed";
        String posttype;
        posttype = request.getParameter("posttype");
        
        if (lg == null) {
            username = "Anonymous";
        }
        else if (lg.getlogedin()){
            username=lg.getUsername();
        }
        
        switch(posttype) {
            case "upload":
                upload(request, response, username);
                break;
            case "profilePic":
                setProfilePic(request, response, username, lg);
                break;
            case "filter":
                filter(request, response, username);
                break;
            case "comment":
                comment(request, response, username);
                break;
        }
    }

    private void error(String mess, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = null;
        out = new PrintWriter(response.getOutputStream());
        out.println("<h1>You have a an error in your input</h1>");
        out.println("<h2>" + mess + "</h2>");
        out.close();
    }
}
