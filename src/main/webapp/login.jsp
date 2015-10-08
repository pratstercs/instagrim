<%-- 
    Document   : login.jsp
    Created on : Sep 28, 2014, 12:04:14 PM
    Author     : Administrator
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="shortcut icon" href="favicon.ico" />
        <link rel="stylesheet" type="text/css" href="assets/Styles.css" />
        <link rel="stylesheet" type="text/css" href="assets/bootstrap.min.css">
        <link rel="stylesheet" type="text/css" href="assets/login.css" />
        <title>Instagrim :: Login</title>
    </head>
    <body>
        <a href="index.jsp"><h1>InstaGrim!</h1></a>
                
        <form method="POST" action="Login" class="form-login">
            <h2 class="form-login-header">Log in:</h2>
            
                <label for="inputEmail" class="sr-only">Username</label>
                <input type="text" id="top" name="username" class="form-control" placeholder="Username" required autofocus>
                
                <label for="inputPassword" class="sr-only">Password</label>
                <input type="password" id="bottom" name="password" class="form-control" placeholder="Password" required>
                
            <br/>

            <button class="btn btn-lg btn-primary btn-block" type="submit">Login</button>
            <div class="loginRegister"><a href="register.jsp">Need to register for an account?</a></div>
        </form>
    </body>
</html>
