<!--<%@page import="java.util.*"%>-->
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="uk.ac.dundee.computing.aec.instagrim.stores.*" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Instagrim :: Image</title>
        <link rel="shortcut icon" href="/InstagrimPJP/assets/favicon.ico" />
        <link rel="stylesheet" type="text/css" href="/InstagrimPJP/assets/Styles.css" />
        <link rel="stylesheet" type="text/css" href="/InstagrimPJP/assets/viewImage.css" />
    </head>
    <body>
        <%
            //String p = (String) request.getAttribute("pic");
            Pic p = (Pic) request.getAttribute("picture");
            
            String user = p.getUser();
            Date date = p.getDate();
            
            LoggedIn lg = (LoggedIn) request.getAttribute("user");
            if (lg == null) {
                lg = (LoggedIn) session.getAttribute("LoggedIn");
            }
            if (lg == null) {
                lg = new LoggedIn();
                lg.clearData();
                lg.setUsername("Anonymous");
            }
        %>
        
        <a href="/InstagrimPJP/index.jsp"><h1>Instagrim</h1></a>
        <br /><br />
        <table>
            <tr>
                <td>
                    <a href="/InstagrimPJP/Image/<%=p.getSUUID()%>" ><img src="/InstagrimPJP/Image/<%=p.getSUUID()%>"></a>
                </td>
                <td>
                    Posted by: <%=user%> <br/>
                    At: <%=date%>
                </td>
            </tr>
        </table>
    </body>
</html>
