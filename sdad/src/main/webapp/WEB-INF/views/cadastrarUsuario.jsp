<%--
  Created by IntelliJ IDEA.
  User: vigal
  Date: 10/11/2018
  Time: 17:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>
<head>
    <title>Cadastro de usuário</title>

    <meta charset="UTF-8"/>

    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">



    <!-- Bootstrap core CSS-->

    <link href="<c:url value="/resources/vendor/bootstrap/css/bootstrap.min.css"/>" rel="stylesheet">

    <!-- Custom fonts for this template-->
    <link href="<c:url value="/resources/vendor/fontawesome-free/css/all.min.css"/>" rel="stylesheet" type="text/css">

    <!-- Page level plugin CSS-->
    <link href="<c:url value="/resources/vendor/datatables/dataTables.bootstrap4.css"/>" rel="stylesheet">

    <!-- Custom styles for this template-->
    <link href="<c:url value="/resources/css/sb-admin.css"/>" rel="stylesheet">
</head>
<body class="bg-dark">

<div class="container">
    <div class="card card-register mx-auto mt-5">
        <div class="card-header">Cadastrar Usuário</div>
        <div class="card-body">
            <form method="post" action="/usuario/cadastrar">


                <div class="form-group">
                    <div class="form-row">
                        <div class="col-md-6">
                            <div class="form-label-group">
                                <input type="text" id="nome" name="nome" class="form-control" placeholder="First name" required="required" autofocus="autofocus">
                                <label for="nome">Nome</label>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-label-group">

                                <input type="text" id="sobrenome" name="sobrenome" class="form-control" placeholder="Last name" required="required">
                                <label for="sobrenome">Sobrenome</label>
                            </div>
                        </div>
                    </div>
                </div>


                <div class="form-group">
                    <div class="form-label-group">
                        <input type="text" id="email" name="email" class="form-control" placeholder="Email address" required="required">
                        <label for="email">Email</label>
                    </div>
                </div>

                <div class="form-group">
                    <div class="form-label-group">
                        <input type="text" id="idDispositvo" name="idDispositivo" class="form-control" placeholder="Id do dispositivo" required="required">
                        <label for="idDispositvo">ID do dispositivo</label>
                    </div>
                </div>


                <div class="form-group">
                    <div class="form-label-group">
                        <input type="text" id="login" name="login" class="form-control" placeholder="Id do dispositivo" required="required">
                        <label for="login">Login</label>
                    </div>
                </div>

                <div class="form-group">
                    <div class="form-label-group">
                        <input type="password" id="senha" name="senha" class="form-control" placeholder="Password" required="required">
                        <label for="senha">Senha</label>
                    </div>
                </div>


                <div class="form-group">
                    <div class="form-row">
                        <div class="col-md-6">
                            <div class="form-label-group">
                                <input type="text" id="tempMin" name="tempMin" class="form-control" placeholder="First name" required="required" autofocus="autofocus">
                                <label for="tempMin">Temperatura mínima em °C</label>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-label-group">

                                <input type="text" id="tempMax" name="tempMax" class="form-control" placeholder="Last name" required="required">
                                <label for="tempMax">Temperatura máxima em °C</label>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="form-group">
                    <div class="form-row">
                        <div class="col-md-6">
                            <div class="form-label-group">
                                <input type="text" id="bpmMin" name="bpmMin" class="form-control" placeholder="First name" required="required" autofocus="autofocus">
                                <label for="bpmMin">Batimentos cardíacos minimo</label>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-label-group">

                                <input type="text" id="bpmMax" name="bpmMax" class="form-control" placeholder="Last name" required="required">
                                <label for="bpmMax">Batimentos cardíacos máximo</label>
                            </div>
                        </div>
                    </div>
                </div>

                <div>
                    <input type="submit" value="Cadastrar"  class="btn btn-primary btn-block">
                </div>

                <input type="hidden"
                       name="${_csrf.parameterName}"
                       value="${_csrf.token}"/>
            </form>

            <div class="text-center">
                <!--<a class="d-block small mt-3" href="sdad/login">Página de login</a>-->
                <!--<a class="d-block small" href="forgot-password.html">Forgot Password?</a>-->
            </div>
        </div>
    </div>
</div>



</body>
</html>