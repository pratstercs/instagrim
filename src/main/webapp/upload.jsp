<%-- 
    Document   : upload
    Created on : Sep 22, 2014, 6:31:50 PM
    Author     : Administrator
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Instagrim</title>
        <link rel="shortcut icon" href="assets/favicon.ico" />
        <link rel="stylesheet" type="text/css" href="assets/Styles.css" />
    </head>
    <body>
        <h1>InstaGrim ! </h1>
        <h2>Your world in Black and White</h2>
        <nav>
            <ul>
                <li class="nav"><a href="upload.jsp">Upload</a></li>
                <!-- <li class="nav"><a href="/InstagrimPJP/Images/majed">Sample Images</a></li> -->
            </ul>
        </nav>
 
        <article>
            <h3>File Upload</h3>
            <form method="POST" enctype="multipart/form-data" action="Image">
                File to upload: <input type="file" name="upfile"><br/>

                <br/>
                <input type="submit" value="Press"> to upload the file!
                <input name="posttype" type="hidden" value="upload">
            </form>

        </article>
        <footer>
            <ul>
                <li class="footer"><a href="/InstagrimPJP">Home</a></li>
            </ul>
        </footer>
    </body>
</html>
