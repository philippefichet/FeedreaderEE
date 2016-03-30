<%-- 
    Document   : index
    Created on : Sep 10, 2014, 12:56:21 PM
    Author     : philippe
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html ng-app="FeedReader">
    <head>
        <title>Edition des flux rss/atom</title>
        <jsp:include page="/WEB-INF/head.jsp"/>
        <script type="text/javascript">
            require(["Feed/Edit"], function(FeedEdit) {
                FeedEdit.build("FeedEdit", "<%= request.getContextPath() %>/ws/feed")
            });
        </script>
    </head>
    <body>
        <div id="FeedEdit"></div>
    </body>
</html>
