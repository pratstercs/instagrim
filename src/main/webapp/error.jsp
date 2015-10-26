<%@ page isErrorPage="true" import="java.io.*" contentType="text/plain"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Instagrim :: Error</title>
        <link rel="stylesheet" type="text/css" href="assets/bootstrap.min.css" />
        <link rel="stylesheet" type="text/css" href="assets/Styles.css" />
        <link rel="shortcut icon" href="assets/favicon.ico" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        
        <!-- Include meta tag to ensure proper rendering and touch zooming -->
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <!-- Include jQuery Mobile stylesheets -->
        <link rel="stylesheet" href="http://code.jquery.com/mobile/1.4.5/jquery.mobile-1.4.5.min.css">
        <!-- Include the jQuery library -->
        <script src="http://code.jquery.com/jquery-1.11.3.min.js"></script>
        <!-- Include the jQuery Mobile library -->
        <script src="http://code.jquery.com/mobile/1.4.5/jquery.mobile-1.4.5.min.js"></script>
    </head>
    <body>
        <%
            try {
                String errorMessage = exception.getMessage();
            }
            catch (Exception e) {
                
            }
        %>
        <h1><a href="/InstagrimPJP/">Instagrim</a></h1>
         <div data-role="collapsible">
             <h2>Exception details</h2>
             <p><%=errorMessage%></p>
             <p><%exception.printStackTrace();%></p>
        </div>
    </body>
</html>
