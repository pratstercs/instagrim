<%-- 
    Document   : register.jsp
    Created on : Sep 28, 2014, 6:29:51 PM
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
        <title>Instagrim :: Register</title>
    </head>
    <body>
        <a href="index.jsp"><h1>InstaGrim!</h1></a>
                
        <form method="POST" action="Register" class="form-login">
            <script>
                //function modified from http://keithscode.com/tutorials/javascript/3-a-simple-javascript-password-validator.html
                function checkPass()
                {
                    //Setting which input fields to read
                    var pass1 = document.getElementById('inputPassword');
                    var pass2 = document.getElementById('confirmPassword');
                    
                    var badColor = "#ff6666";
                    var white = "#ffffff";
                    
                    //Message to display in case of non-match
                    var message = document.getElementById('confirmMessage');
                    message.style.color = badColor;
                    
                    //If passwords do not match
                    if(pass1.value !== pass2.value){
                        //notify user by changing background colour and displaying message
                            pass2.style.backgroundColor = badColor;
                            message.innerHTML = "Passwords do not match!"
                    }
                    else {
                        //otherwise reset
                        pass2.style.backgroundColor = white;
                        message.innerHTML = "";
                    }
                }
            </script>
            
            <h2 class="form-login-header">Register:</h2>
            <div id="pwdReqs">Password must be at least 6 characters</div>
                <label for="inputEmail" class="sr-only">Username</label>
                <input type="text" id="inputUser" name="username" class="form-control" placeholder="Username" required autofocus>
                
                <label for="inputEmail" class="sr-only">Email</label>
                <input type="email" id="inputEmail" name="email" class="form-control" placeholder="Email Address" required>
                
                <label for="inputPassword" class="sr-only">Password</label>
                <input type="password" id="inputPassword" name="confPass" class="form-control" placeholder="Password" required>
                
                <label for="inputPassword" class="sr-only">Confirm Password</label>
                <input type="password" id="confirmPassword" name="password" class="form-control" placeholder="Confirm Password" onkeyup="checkPass(); return false;" required>
            <br/>
            <div id="confirmMessage"><!--Password match text is inserted here--></div>
            <button class="btn btn-lg btn-primary btn-block" type="submit">Register</button>
            <div class="loginRegister"><a href="login.jsp">Already have an account?</a></div>
        </form>
    </body>
</html>
