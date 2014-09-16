<%@page contentType="text/html" pageEncoding="UTF-8"%>
        <meta name="viewport" content="width=device-width,initial-scale=1.0"/>
        <link rel="stylesheet" href="<%= request.getContextPath() %>/webjars/bootstrap/3.2.0/css/bootstrap.min.css"/>
        <link rel="stylesheet" href="<%= request.getContextPath() %>/css/feed.css"/>
        <script type="text/javascript" src="<%= request.getContextPath() %>/webjars/jquery/2.1.1/jquery.min.js"></script>
        <script type="text/javascript" src="<%= request.getContextPath() %>/webjars/angularjs/1.2.19/angular.min.js"></script>
        <script type="text/javascript" src="<%= request.getContextPath() %>/webjars/angularjs/1.2.19/angular-sanitize.min.js"></script>
        <script type="text/javascript" src="<%= request.getContextPath() %>/js/feed.js"></script>
        <script>
            var webservicesUrl = {
                "feed": "/feedreader/ws/feed",
                "feedItem": "/feedreader/ws/feedItem",
            }
        </script>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
