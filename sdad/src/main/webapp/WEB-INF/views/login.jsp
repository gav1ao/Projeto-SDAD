<%--
  Created by IntelliJ IDEA.
  User: vigal
  Date: 12/12/2018
  Time: 23:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>SDAD - Login</title>

    <!-- Bootstrap core CSS-->
    <link href="<c:url value="/resources/vendor/bootstrap/css/bootstrap.min.css" />" rel="stylesheet">

    <!-- Custom fonts for this template-->
    <link href="<c:url value="/resources/vendor/fontawesome-free/css/all.min.css" />" rel="stylesheet" type="text/css">

    <!-- Custom styles for this template-->
    <link href="<c:url value="/resources/css/sb-admin.css" />" rel="stylesheet">

</head>

<body class="bg-dark">

<div class="container">
    <div class="card card-login mx-auto mt-5">
        <div class="card-header">Login</div>
        <div class="card-body">

            <form:form servletRelativeAction="login" name="f">
                <div class="form-group">
                    <div class="form-label-group">
                        <input type="email" id="inputEmail" name="username" class="form-control" placeholder="Endereço de email" required="required" autofocus="autofocus">
                        <label for="inputEmail">Endereço de email</label>
                    </div>
                </div>
                <div class="form-group">
                    <div class="form-label-group">
                        <input type="password" id="inputPassword" name="password" class="form-control" placeholder="Senha" required="required">
                        <label for="inputPassword">Senha</label>
                    </div>
                </div>
                <div class="form-group">
                    <div class="checkbox">
                        <label>
                            <input type="checkbox" value="remember-me">
                            Lembrar senha
                        </label>
                    </div>
                </div>
                <button type="submit" class="btn btn-primary btn-block">Login</button>

            </form:form>
            <!--
            <div class="text-center">
                <a class="d-block small mt-3" href="register.html">Registre-se</a>
                <a class="d-block small" href="forgot-password.html">Esqueceu a senha?</a>
            </div>
            -->
        </div>
    </div>
</div>

<!-- Bootstrap core JavaScript-->
<script src="<c:url value="/resources/vendor/jquery/jquery.min.js"/>"></script>
<script src="<c:url value="/resources/vendor/bootstrap/js/bootstrap.bundle.min.js"/>"></script>

<!-- Core plugin JavaScript-->
<script src="<c:url value="/resources/vendor/jquery-easing/jquery.easing.min.js"/>"></script>

</body>

</html>
