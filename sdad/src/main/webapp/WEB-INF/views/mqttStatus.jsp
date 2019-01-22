<%--
  Created by IntelliJ IDEA.
  User: vigal
  Date: 16/12/2018
  Time: 17:15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

    <title>MQTT - Status</title>
</head>
<body>
    <h1>Status MQTT</h1>
    <p>Conectado: <%=request.getAttribute("status")%></p>
</body>
</html>
