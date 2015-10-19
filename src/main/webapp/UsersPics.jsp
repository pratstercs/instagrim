<%-- 
    Document   : UsersPics
    Created on : Sep 24, 2014, 2:52:48 PM
    Author     : Administrator
--%>

<%@page import="java.util.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="uk.ac.dundee.computing.aec.instagrim.stores.*" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Instagrim</title>
        <link rel="stylesheet" type="text/css" href="/Instagrim/assets/Styles.css" />
        <script>
            function submitForm(picID) {
                var box = document.getElementById('profilePicID');
                box.value = picID;
                document.getElementById('form').submit();
            }
        </script>
    </head>
    <body>
        <header>
            <h1>InstaGrim!</h1>
            <h2>Your world in Black and White</h2>
        </header>
        
        <nav>
            <ul>
                <li class="nav"><a href="/Instagrim/upload.jsp">Upload</a></li>
                <li class="nav"><ul><a href="/Instagrim">Home</a></ul></li>
                <!-- <li class="nav"><a href="/Instagrim/Images/majed">Sample Images</a></li> -->
            </ul>
        </nav>
 
        <article>
            <h2>Your Pics</h2>
        <%
            java.util.LinkedList<Pic> lsPics = (java.util.LinkedList<Pic>) request.getAttribute("Pics");
            if (lsPics == null) {
        %>
        <p>No Pictures found</p>
        <%
        } else {
            Iterator<Pic> iterator;
            iterator = lsPics.iterator();
            while (iterator.hasNext()) {
                Pic p = (Pic) iterator.next();

        %>
        <a href="/Instagrim/Image/<%=p.getSUUID()%>" ><img src="/Instagrim/Thumb/<%=p.getSUUID()%>"></a><a href="#" onclick='submitForm("<%=p.getSUUID()%>")'>Use as profile picture</a><br/><%

            }
            }
        %>
        </article>
        <form id="form" action="Image" method="POST" class="form-horizontal" role="form">
            <input name="posttype" id="posttype" value="profilePic" type="text">
            <input name="profilePicID" id="profilePicID" value="" type="hidden">
        </form>
    </body>
</html>
