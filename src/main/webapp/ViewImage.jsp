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
                    <div class="comment">
                        <b>Image by: <a href="/InstagrimPJP/Profile/<%=user%>"><%=user%></a></b> <br/>
                        <span class="commentTime"><%=date%></span>
                        <br/><br/><br/>
                        <form method="POST" name="commentForm">
                            <textarea id="commentBox" class="text" name="commentBox" placeholder="Write your comment here:" autofocus></textarea>

                            <input name="posttype" type="hidden" value="comment">
                            <input name="picID" type="hidden" value="<%=p.getSUUID()%>">

                            <button class="btn btn-lg btn-primary btn-block" type="submit">Submit Comment</button>
                        </form>
                    </div>
                    <br/><br/><br/>
                    <%
                        java.util.LinkedList<Comment> commentList = (java.util.LinkedList<Comment>) request.getAttribute("comments");
                        if (commentList == null) {
                    %>
                    <div class="comment">
                        <p>No comments, yet!</p>
                    </div>
                    <%
                        } else {
                            Iterator<Comment> iterator;
                            iterator = commentList.iterator();
                            while (iterator.hasNext()) {
                                Comment c = (Comment) iterator.next();
                    %>
                    <div class="comment">
                        <%
                                if( c.getUser().equals("Anonymous") ) {
                        %>
                        <b><i>Anonymous</i></b>
                        <%
                                }
                                else {
                        %>
                        <b><a href="/InstagrimPJP/Profile/<%=c.getUser()%>"><%=c.getUser()%></a></b>
                        <%
                                }
                        %>
                        <br /><span class="commentTime"><%=c.getDate().toString()%><br /></span>
                        <%=c.getCommentText()%>
                        <br /><br />
                    </div>
                    <%
                            }
                        }
                %>
                </td>
            </tr>
        </table>
    </body>
</html>
