<%@page contentType="text/html" pageEncoding="UTF-8"%>
        <meta name="viewport" content="width=device-width,initial-scale=1.0"/>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <script type="text/javascript" src="<%= request.getContextPath() %>/webjars/requirejs/2.1.22/require.js"></script>
        <script type="text/javascript" src="<%= request.getContextPath() %>/require-config?v=1"></script>
        <script type="text/javascript" src="<%= request.getContextPath() %>/webjars/jquery/2.2.1/dist/jquery.min.js"></script>
        <script type="text/javascript" src="<%= request.getContextPath() %>/webjars/bootstrap/3.3.6/dist/js/bootstrap.min.js"></script>
        <link rel="stylesheet" href="<%= request.getContextPath() %>/webjars/bootstrap/3.3.6/dist/css/bootstrap.min.css"/>

        <!-- material design -->
        <link rel="stylesheet" href="<%= request.getContextPath() %>/webjars/bootstrap-material-design/0.5.8/dist/css/bootstrap-material-design.min.css"/>
        <link rel="stylesheet" href="<%= request.getContextPath() %>/webjars/bootstrap-material-design/0.5.8/dist/css/ripples.min.css"/>
        <script type="text/javascript" src="<%= request.getContextPath() %>/webjars/bootstrap-material-design/0.5.8/dist/js/material.min.js"></script>
        <script type="text/javascript" src="<%= request.getContextPath() %>/webjars/bootstrap-material-design/0.5.8/dist/js/ripples.min.js"></script>
        <!-- Material Design fonts -->
        <link rel="stylesheet" type="text/css" href="//fonts.googleapis.com/css?family=Roboto:300,400,500,700">
        <link rel="stylesheet" type="text/css" href="//fonts.googleapis.com/icon?family=Material+Icons">

        <link rel="stylesheet" href="<%= request.getContextPath() %>/webjars/font-awesome/4.5.0/css/font-awesome.min.css"/>
        <link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css"/>
        <link rel="stylesheet" href="<%= request.getContextPath() %>/css/loader.css"/>
        <script type="text/javascript">
            window.baseUrl = "<%= request.getContextPath() %>";
        </script>
