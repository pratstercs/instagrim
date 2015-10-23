<!--<%@page import="java.util.*"%>-->
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="uk.ac.dundee.computing.aec.instagrim.stores.*" %>

<!doctype html>
<html>
    <head>
        <title>Instagrim :: Profile</title>
        <link rel="stylesheet" type="text/css" href="/InstagrimPJP/assets/bootstrap.min.css">
        <link rel="stylesheet" type="text/css" href="http://snipplicious.com/css/font-awesome-4.1.0.min.css">
        <link rel="stylesheet" type="text/css" href="/InstagrimPJP/assets/viewProfile.css" />
        <link rel="stylesheet" type="text/css" href="/InstagrimPJP/assets/Styles.css" />
        <link rel="shortcut icon" href="/InstagrimPJP/assets/favicon.ico" />
    </head>
    <body>  
        <%
            String username = "";
            String name = "";
            String email = "";
            String encodedAddress = "";
            String[] address = new String[3];
            java.util.UUID profilePic = null;
            String picLocation = "";
            String thumbLocation = "";

            LoggedIn lg = (LoggedIn) request.getAttribute("user");
            if (lg == null) {
                lg = (LoggedIn) session.getAttribute("LoggedIn");
            }
            if (lg == null) {
                lg = new LoggedIn();
                lg.clearData();
                lg.setUsername("NOT LOGGED IN");
            }
            
            username = lg.getUsername();
            name = lg.getFirstName() + " " + lg.getLastName();
            email = lg.getEmail();
            encodedAddress = lg.getEncodedAddress();
            address = lg.getAddress();
            profilePic = lg.getProfilePic();

            if(profilePic == null){
                thumbLocation = "/InstagrimPJP/assets/blank.png";
                picLocation = "/InstagrimPJP/assets/blank.png";
            }
            else {
                thumbLocation = "/InstagrimPJP/Thumb/" + profilePic;
                picLocation = "/InstagrimPJP/Image/" + profilePic;
            }


        %>
        <a href="/InstagrimPJP/"><h1>Instagrim</h1></a>
        <table>
            <tr>
                <td>
                    <div class="container">
                        <div class="row">
                            <div class="col-xs-12 col-sm-12 col-md-6 col-lg-6 col-xs-offset-0 col-sm-offset-0 col-md-offset-3 col-lg-offset-3 toppad" >
                                <div class="panel panel-info">
                                    <div class="panel-heading">
                                        <h3 class="panel-title"><%=username%></h3>
                                    </div>
                                    <div class="panel-body">
                                        <div class="row">
                                            <div class="col-md-3 col-lg-3 " align="center">
                                                <a href="<%=picLocation%>"><img alt="Profile Picture" src="<%=thumbLocation%>" class="img-circle img-responsive"></a>
                                            </div>
                                            <div class=" col-md-9 col-lg-9 "> 
                                                <table class="table table-user-information">
                                                    <tbody>
                                                        <tr>
                                                            <td>Name:</td>
                                                                <td><%=name%></td>
                                                        </tr>
                                                        <tr>
                                                            <tr>
                                                                <td>Address</td>
                                                                <td>
                                                                    <%=address[0]%><br />
                                                                    <%=address[1]%><br />
                                                                    <%=address[2]%><br />
                                                                </td>
                                                            </tr>
                                                            <tr>
                                                                <td>Email</td>
                                                                <td><a href="mailto:<%=email%>"><%=email%></a></td>
                                                    </tbody>
                                                </table>
                                                    <span class="pull-right"><a href="/InstagrimPJP/Images/<%=username%>/" class="btn btn-primary">My Images</a></span>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="panel-footer">
                                        <a href="/InstagrimPJP/editProfile" data-original-title="Edit profile" data-toggle="tooltip" type="button" class="btn btn-sm btn-warning"><!-- @TODO edit profile link here --><i class="glyphicon glyphicon-edit"></i></a>
                                        <span class="pull-right">
                                            <a data-original-title="Logout" data-toggle="tooltip" type="button" class="btn btn-sm btn-danger" href="/InstagrimPJP/logout.jsp"><i class="glyphicon glyphicon-remove"></i></a>
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </td>
                <td>
                    <iframe width="600" height="450" frameborder="0" style="border:0" src="<%=encodedAddress%>" allowfullscreen></iframe>
                </td>
            </tr>
        </table>
        <br /><br /><br />
        <table>
            <tr>
                <%
                    java.util.LinkedList<Pic> lsPics = (java.util.LinkedList<Pic>) request.getAttribute("Pics");
                    if (lsPics == null) {
                        //no pictures found
                    } else {
                        int count = -1;
                        Iterator<Pic> iterator;
                        iterator = lsPics.iterator();
                        while (iterator.hasNext()) {
                            count++;
                            
                            if( ((count % 4) == 0) && (count != 0)) {
                                %>
                                </tr>
                                <tr>
                                <%
                            }
                            
                            Pic p = (Pic) iterator.next();
                            %>
                            <a href="/InstagrimPJP/Image/<%=p.getSUUID()%>" ><img src="/InstagrimPJP/Thumb/<%=p.getSUUID()%>"></a>
                            <%
                        }
                    }
                %>
            </tr>
        </table>
        
    </body>
</html>
