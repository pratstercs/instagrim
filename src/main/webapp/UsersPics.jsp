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
        <link rel="shortcut icon" href="/InstagrimPJP/assets/favicon.ico" />
        <link rel="stylesheet" type="text/css" href="/InstagrimPJP/assets/Styles.css" />
        <script>
            function submitForm(picID) {
                var box = document.getElementById('picID');
                var type = document.getElementById('posttype');
                
                box.value = picID;
                type.value = "profilePic"
                
                document.getElementById('form').submit();
            }
            function applyFilter(picID) {
                var box = document.getElementById('picID');
                var type = document.getElementById('posttype');
                
                box.value = picID;
                type.value = "filter"
                
                document.getElementById('form').submit();
            }
        </script>
    </head>
    <body>
        <header>
            <a href="/InstagrimPJP"><h1>InstaGrim!</h1></a>
        </header>
        
        <nav>
            <ul>
                <li class="nav"><a href="/InstagrimPJP/upload.jsp">Upload</a></li>
                <li class="nav"><ul><a href="/InstagrimPJP">Home</a></ul></li>
            </ul>
        </nav>
 
        <article>
            <h2>Your Pics</h2>
            <form id="form" action="Image" method="POST" class="form-horizontal" role="form">
                <%
                    java.util.LinkedList<Pic> lsPics = (java.util.LinkedList<Pic>) request.getAttribute("Pics");
                    if (lsPics == null) {
                %>
                <p>No Pictures found</p>
                <%
                    } else {
                %>
                <table border="0">
                    <%
                        Iterator<Pic> iterator;
                        iterator = lsPics.iterator();
                        while (iterator.hasNext()) {
                    %>
                            <tr>
                                <td>
                    <%
                            Pic p = (Pic) iterator.next();
                    %>
                            <a href="/InstagrimPJP/Image/<%=p.getSUUID()%>" ><img src="/InstagrimPJP/Thumb/<%=p.getSUUID()%>"></a>
                        </td>
                        <td>
                            <a href="#" onclick='submitForm("<%=p.getSUUID()%>")'>Use as profile picture</a><br/><br/>
                            Select filter to apply:
                            <select name="filterMode" id="filterMode" onchange='applyFilter("<%=p.getSUUID()%>")'>
                                <option selected="selected" value="0"></option>
                                <option value="1">Greyscale</option>
                                <option value="2">Sickeningly Pink</option>
                                <option value="3">Sepia</option>
                                <option value="4">Invert</option>
                                <option value="5">Lighten</option>
                                <option value="6">Darken</option>
                            </select><br /><br />
                        </td>
                    </tr> 
            <%
                        }
                    }
            %>
        </table>
        </article>
            <input name="posttype" id="posttype" value="profilePic" type="hidden">
            <input name="picID" id="picID" value="" type="hidden">
        </form>
    </body>
</html>
