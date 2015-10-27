<%-- 
    Document   : index
    Created on : Sep 28, 2014, 7:01:44 PM
    Author     : Administrator
--%>


<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="uk.ac.dundee.computing.aec.instagrim.stores.*" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Instagrim</title>
        <link rel="stylesheet" type="text/css" href="assets/bootstrap.min.css" />
        <link rel="stylesheet" type="text/css" href="assets/Styles.css" />
        <link rel="stylesheet" type="text/css" href="assets/index.css" />
        <link rel="shortcut icon" href="assets/favicon.ico" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    </head>
    <body>
        
        <div id="cardBox" class="col-lg-6 col-sm-6">
            <div class="card hovercard">
                <div class="card-background">
                    <img class="card-bkimg" alt="" src="/InstagrimPJP/assets/bg.png">
                </div>
                <div class="useravatar">
                    <img alt="" src="/InstagrimPJP/assets/instagrim.png">
                </div>
                <div class="card-info">
                    <span class="card-title">Your world in many colours</span>
                </div>
            </div>
            <div class="btn-pref btn-group btn-group-justified btn-group-lg" role="group" aria-label="...">
                <div class="btn-group" role="group">
                    <a href="upload.jsp">
                        <button type="button" id="upload" class="btn btn-default" /><span class=" glyphicon glyphicon-upload" aria-hidden="true"></span>
                            <div class="hidden-xs">Upload</div>
                        </button>
                    </a>
                </div>
                <%
                        
                    LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
                    if (lg != null) {
                        String UserName = lg.getUsername();
                        if (lg.getlogedin()) {
                %>
                <div class="btn-group" role="group">
                    <a href="/InstagrimPJP/Images/<%=lg.getUsername()%>">
                        <button type="button" id="images" class="btn btn-default"><span class="glyphicon glyphicon-camera" aria-hidden="true"></span>
                            <div class="hidden-xs">Images</div>
                        </button>
                    </a>
                </div>
                <div class="btn-group" role="group">
                    <a href="/InstagrimPJP/Profile/<%=lg.getUsername()%>">
                        <button type="button" id="profile" class="btn btn-default"><span class="glyphicon glyphicon-user" aria-hidden="true"></span>
                            <div class="hidden-xs">Profile</div>
                        </button>
                    </a>
                </div>
                <div class="btn-group" role="group">
                    <a href="logout.jsp">
                        <button type="button" id="logout" class="btn btn-primary"><span class="glyphicon glyphicon-remove-circle" aria-hidden="true"></span>
                            <div class="hidden-xs">Logout</div>
                        </button>
                    </a>
                </div>
                
                <%}
                            } else {
                                %>
                <div class="btn-group" role="group">
                    <a href="register.jsp">
                        <button type="button" id="register" class="btn btn-primary"><span class="glyphicon glyphicon-pencil" aria-hidden="true"></span>
                            <div class="hidden-xs">Register</div>
                        </button>
                    </a>
                </div>
                <div class="btn-group" role="group">
                    <a href="login.jsp">
                        <button type="button" id="login" class="btn btn-primary"><span class="glyphicon glyphicon-user" aria-hidden="true"></span>
                            <div class="hidden-xs">Login</div>
                        </button>
                    </a>
                </div>
                <%}%>
            </div>
        </div>
    </body>
</html>
