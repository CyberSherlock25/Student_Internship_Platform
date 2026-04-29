<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.mitwpu.lca.model.*, com.mitwpu.lca.dao.*" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reports - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <%@ include file="../components/navbar.jsp" %>

    <div class="container-fluid mt-4">
        <div class="row">
            <div class="col-md-3">
                <div class="list-group">
                    <a href="dashboard.jsp" class="list-group-item list-group-item-action">Dashboard</a>
                    <a href="companies.jsp" class="list-group-item list-group-item-action">Companies</a>
                    <a href="internships.jsp" class="list-group-item list-group-item-action">Internships</a>
                    <a href="applications.jsp" class="list-group-item list-group-item-action">Applications</a>
                    <a href="students.jsp" class="list-group-item list-group-item-action">Students</a>
                    <a href="exams.jsp" class="list-group-item list-group-item-action">Exams</a>
                    <a href="${pageContext.request.contextPath}/report?type=violations" class="list-group-item list-group-item-action active">Reports & Logs</a>
                </div>
            </div>

            <div class="col-md-9">
                <h2>Reports & Audit Logs</h2>

                <!-- Report Type Tabs -->
                <ul class="nav nav-tabs mb-3">
                    <li class="nav-item">
                        <a class="nav-link <%= "violations".equals(request.getParameter("sub")) ? "active" : "" %>"
                           href="${pageContext.request.contextPath}/report?type=violations">Violation Logs</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link <%= "rankList".equals(request.getParameter("sub")) ? "active" : "" %>"
                           href="${pageContext.request.contextPath}/report?type=examAttempts">Exam Rank Lists</a>
                    </li>
                </ul>

                <!-- Violation Logs -->
                <% if ("violations".equals(request.getParameter("sub")) || request.getParameter("sub") == null) {
                    List<AuditLog> violations = (List<AuditLog>) request.getAttribute("violations");
                %>
                <div class="card">
                    <div class="card-header bg-danger text-white">
                        <h5 class="mb-0">Suspicious Activity / Violation Logs</h5>
                    </div>
                    <div class="card-body">
                        <table class="table table-sm table-striped">
                            <thead>
                                <tr>
                                    <th>Time</th>
                                    <th>User</th>
                                    <th>Action</th>
                                    <th>Details</th>
                                    <th>IP Address</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (violations != null) {
                                    for (AuditLog log : violations) { %>
                                <tr class="<%= log.getAction().contains("MULTIPLE_LOGIN") ? "table-warning" : log.getAction().contains("TAB_SWITCH") ? "table-danger" : "" %>">
                                    <td><%= log.getLoggedAt() %></td>
                                    <td><%= log.getUserName() != null ? log.getUserName() : "N/A" %></td>
                                    <td><span class="badge bg-dark"><%= log.getAction() %></span></td>
                                    <td><%= log.getDetails() %></td>
                                    <td><code><%= log.getIpAddress() %></code></td>
                                </tr>
                                <% }} %>
                            </tbody>
                        </table>
                    </div>
                </div>
                <% } %>

                <!-- Rank List -->
                <% if ("rankList".equals(request.getParameter("sub"))) {
                    List<ExamAttempt> rankList = (List<ExamAttempt>) request.getAttribute("rankList");
                    Exam exam = (Exam) request.getAttribute("exam");
                %>
                <div class="card">
                    <div class="card-header bg-success text-white">
                        <h5 class="mb-0">Rank List: <%= exam != null ? exam.getExamTitle() : "" %></h5>
                    </div>
                    <div class="card-body">
                        <table class="table table-striped">
                            <thead>
                                <tr>
                                    <th>Rank</th>
                                    <th>Student</th>
                                    <th>Marks Obtained</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (rankList != null) {
                                    int rank = 1;
                                    for (ExamAttempt attempt : rankList) { %>
                                <tr class="<%= rank == 1 ? "table-warning" : rank == 2 ? "table-light" : rank == 3 ? "table-info" : "" %>">
                                    <td><strong>#<%= rank++ %></strong></td>
                                    <td><%= attempt.getStudentName() %></td>
                                    <td><%= attempt.getTotalMarksObtained() %> / <%= exam != null ? exam.getTotalMarks() : "-" %></td>
                                    <td><span class="badge bg-success"><%= attempt.getStatus() %></span></td>
                                </tr>
                                <% }} %>
                            </tbody>
                        </table>
                    </div>
                </div>
                <% } %>

                <!-- Exam Selection for Rank List -->
                <% if ("examAttempts".equals(request.getParameter("sub"))) {
                    List<Exam> exams = (List<Exam>) request.getAttribute("exams");
                %>
                <div class="card">
                    <div class="card-header bg-primary text-white">
                        <h5 class="mb-0">Select Exam for Rank List</h5>
                    </div>
                    <div class="card-body">
                        <div class="list-group">
                            <% if (exams != null) {
                                for (Exam e : exams) { %>
                            <a href="${pageContext.request.contextPath}/report?type=rankList&examId=<%= e.getExamId() %>"
                               class="list-group-item list-group-item-action d-flex justify-content-between align-items-center">
                                <%= e.getExamTitle() %>
                                <span class="badge bg-primary rounded-pill"><%= e.getStatus() %></span>
                            </a>
                            <% }} %>
                        </div>
                    </div>
                </div>
                <% } %>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

