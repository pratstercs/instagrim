<!--<%@page import="java.util.*"%>-->
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="uk.ac.dundee.computing.aec.instagrim.stores.*" %>

<!doctype html>
<html>
    <head>
        <title>Instagrim :: Profile</title>
        <link rel="stylesheet" type="text/css" href="http://snipplicious.com/css/bootstrap-3.2.0.min.css">

        <link rel="stylesheet" type="text/css" href="http://snipplicious.com/css/font-awesome-4.1.0.min.css">

        <script src="http://snipplicious.com/js/jquery.js"></script>
        <script src="http://snipplicious.com/js/bootstrap.min.js"></script>
    </head>
    <body>
        <%
                String username = "";
                String firstName = "";
                String lastName = "";
                String email = "";
                
                LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
                if (lg != null) {
                    username = lg.getUsername();
                    firstName = lg.getFirstName();
                    lastName = lg.getLastName();
                    email = lg.getEmail();
                }
                else {
                    username = "NOT LOGGED IN";
                }
        %>
        <div class="container" style="padding-top: 60px;">
          <h1 class="page-header">Edit Profile</h1>
          <div class="row">
            <!-- left column -->
            <div class="col-md-4 col-sm-6 col-xs-12">
              <div class="text-center">
                <img src="http://lorempixel.com/200/200/people/9/" class="avatar img-circle img-thumbnail" alt="avatar">
                <h6>Upload a different photo...</h6>
                <input type="file" class="text-center center-block well well-sm">
              </div>
            </div>
            <!-- edit form column -->
            <div class="col-md-8 col-sm-6 col-xs-12 personal-info">
              <h3>Personal info</h3>
              <form class="form-horizontal" role="form">
                <div class="form-group">
                  <label class="col-lg-3 control-label">First name:</label>
                  <div class="col-lg-8">
                    <input class="form-control" value="<%=firstName%>" type="text">
                  </div>
                </div>
                <div class="form-group">
                  <label class="col-lg-3 control-label">Last name:</label>
                  <div class="col-lg-8">
                    <input class="form-control" value="<%=lastName%>" type="text">
                  </div>
                </div>
                <div class="form-group">
                  <label class="col-lg-3 control-label">Email:</label>
                  <div class="col-lg-8">
                    <input class="form-control" value="<%=email%>" type="text">
                  </div>
                </div>
                <div class="form-group">
                  <label class="col-md-3 control-label">Username:</label>
                  <div class="col-md-8">
                    <input class="form-control" value="<%=username%>" type="text">
                  </div>
                </div>
                <div class="form-group">
                  <label class="col-md-3 control-label">Password:</label>
                  <div class="col-md-8">
                    <input class="form-control" value="" type="password">
                  </div>
                </div>
                <div class="form-group">
                  <label class="col-md-3 control-label">Confirm password:</label>
                  <div class="col-md-8">
                    <input class="form-control" value="" type="password">
                  </div>
                </div>
                <div class="form-group">
                  <label class="col-md-3 control-label"></label>
                  <div class="col-md-8">
                    <input class="btn btn-primary" value="Save Changes" type="button">
                    <span></span>
                    <input class="btn btn-default" value="Cancel" type="reset">
                  </div>
                </div>
              </form>
            </div>
          </div>
        </div>
    </body>
</html>