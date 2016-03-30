<%@page contentType="text/html" pageEncoding="UTF-8"%><!DOCTYPE html>
<html>
    <head>
        <title>Visualisation des flux rss/atom</title>
        <jsp:include page="/WEB-INF/head.jsp"/>
        <script type="text/javascript" src="<%= request.getContextPath() %>/js/route.js?v=<%= System.nanoTime() %>"></script>
        <script type="text/javascript" src="<%= request.getContextPath() %>/js/routeHandle.js?v=<%= System.nanoTime() %>"></script>
    </head>
    <body>
        <div id="main-container">
            
        </div>
    </body>
</html>
