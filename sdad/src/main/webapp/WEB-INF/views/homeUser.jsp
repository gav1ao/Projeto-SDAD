<%-- 
    Document   : usuario
    Created on : 15/12/2018, 14:06:00
    Author     : Dougl
--%>
<!teste>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">

    <head>
        <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <meta name="description" content="">
        <meta name="author" content="">

        <title>SDAD - Usuario</title>


        <!-- Bootstrap core CSS-->

        <link href="<c:url value="/resources/vendor/bootstrap/css/bootstrap.min.css"/>" rel="stylesheet">

        <!-- Custom fonts for this template-->
        <link href="<c:url value="/resources/vendor/fontawesome-free/css/all.min.css"/>" rel="stylesheet" type="text/css">

        <!-- Page level plugin CSS-->
        <link href="<c:url value="/resources/vendor/datatables/dataTables.bootstrap4.css"/>" rel="stylesheet">

        <!-- Custom styles for this template-->
        <link href="<c:url value="/resources/css/sb-admin.css"/>" rel="stylesheet">

    </head>

    <body id="page-top" class="bg-dark">


        <div id="content-wrapper">

            <div class="container-fluid ">

                <!-- Breadcrumbs-->
                <ol class="breadcrumb bg-blue">
                    <li class="breadcrumb-item">
                        <a href="#login">SDAD</a>
                    </li>

                </ol>

                <!-- DataTables Example -->
                <div class="card mb-3">
                    <div class="card-header">
                        <i class="fas fa-table"></i>
                        Tabela de Informações do Usuário <b>${userLogin}</b></div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-bordered" id="dataTable" width="100%" cellspacing="0">
                                <thead>
                                    <tr>
                                        <th>Informação</th>
                                        <th>Valor</th>
                                        <th>Data</th>
                                        <th>Hora</th>

                                    </tr>
                                </thead>

                                <tbody>
                                    <c:forEach items="${batimentos}" var="medicao">
                                        <tr>
                                            <td>Batimentos Cardíacos</td>
                                            <td>${medicao.batimentos} BPM</td>
                                            <td>${medicao.dataHora.toLocalDateTime().toLocalDate().toString()}</td>
                                            <td>${medicao.dataHora.toLocalDateTime().toLocalTime().toString()}</td>
                                        </tr>
                                    </c:forEach>


                                    <c:forEach items="${quedas}" var="medicao">
                                        <tr>
                                            <td>Queda</td>
                                            <td>Queda detectada</td>
                                            <td>${medicao.dataHora.toLocalDateTime().toLocalDate().toString()}</td>
                                            <td>${medicao.dataHora.toLocalDateTime().toLocalTime().toString()}</td>
                                        </tr>
                                    </c:forEach>

                                    <c:forEach items="${temperaturas}" var="medicao">
                                        <tr>
                                            <td>Temperatura</td>
                                            <td>${medicao.temperatura} °C</td>
                                            <td>${medicao.dataHora.toLocalDateTime().toLocalDate().toString()}</td>
                                            <td>${medicao.dataHora.toLocalDateTime().toLocalTime().toString()}</td>
                                        </tr>
                                    </c:forEach>
                                    <tr>
                                        <td>Batimentos Cardiaco</td>
                                        <td>80</td>
                                        <td>15/12/2018</td>
                                        <td>19:10</td>

                                    </tr>
                                    <tr>
                                        <td>Batimentos Cardiaco</td>
                                        <td>92</td>
                                        <td>15/12/2018</td>
                                        <td>19:50</td>

                                    </tr>

                                </tbody>
                            </table>
                        </div>
                    </div>
                    <div class="card-footer small text-muted"></div>
                </div>

                <p class="small text-center text-muted my-5">
                    <em>SDAD v:1.0</em>
                </p>

            </div>
            <!-- /.container-fluid -->

            <!-- Sticky Footer -->
            <!--
            <footer class="sticky-footer">
                <div class="container my-auto">
                    <div class="copyright text-center my-auto">
                        <span>Copyright © Your Website 2018</span>
                    </div>
                </div>
            </footer>
            -->

        </div>
        <!-- /.content-wrapper -->


        <!-- Scroll to Top Button-->
        <a class="scroll-to-top rounded" href="#page-top">
            <i class="fas fa-angle-up"></i>
        </a>

        <!-- Logout Modal-->
        <div class="modal fade" id="logoutModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="exampleModalLabel">Ready to Leave?</h5>
                        <button class="close" type="button" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">×</span>
                        </button>
                    </div>
                    <div class="modal-body">Select "Logout" below if you are ready to end your current session.</div>
                    <div class="modal-footer">
                        <button class="btn btn-secondary" type="button" data-dismiss="modal">Cancel</button>
                        <a class="btn btn-primary" href="login.html">Logout</a>
                    </div>
                </div>
            </div>
        </div>
        <!-- Jquery-->
        <script src="<c:url value="/resources/js/jquery-3.3.1.js"/>"></script>


        <!-- Custom scripts for all pages-->
        <script src="<c:url value="/resources/js/sb-admin.min.js"/>"></script>

        <!-- Demo scripts for this page-->
        <script src="<c:url value="/resources/js/demo/datatables-demo.js"/>"></script>



        <!-- Core plugin JavaScript-->
        <script src="<c:url value="/resources/vendor/jquery-easing/jquery.easing.min.js"/>"></script>


        <!-- Page level plugin JavaScript-->
        <script src="<c:url value="/resources/vendor/datatables/jquery.dataTables.js"/>"></script>
        <script src="<c:url value="/resources/vendor/datatables/dataTables.bootstrap4.js"/>"></script>



        <!-- Bootstrap core JavaScript-->
        <script src="<c:url value="/resources/vendor/bootstrap/js/bootstrap.bundle.min.js"/>"></script>


    </body>

</html>
