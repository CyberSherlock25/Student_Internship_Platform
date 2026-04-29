<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.mitwpu.lca.model.*, com.mitwpu.lca.dao.*" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Exam Management - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <%@ include file="../components/navbar.jsp" %>

    <div class="container-fluid mt-4">
        <div class="row">
            <!-- Sidebar -->
            <div class="col-md-3">
                <div class="list-group">
                    <a href="dashboard.jsp" class="list-group-item list-group-item-action">Dashboard</a>
                    <a href="companies.jsp" class="list-group-item list-group-item-action">Companies</a>
                    <a href="internships.jsp" class="list-group-item list-group-item-action">Internships</a>
                    <a href="applications.jsp" class="list-group-item list-group-item-action">Applications</a>
                    <a href="students.jsp" class="list-group-item list-group-item-action">Students</a>
                    <a href="exams.jsp" class="list-group-item list-group-item-action active">Exams</a>
                    <a href="${pageContext.request.contextPath}/report?type=violations" class="list-group-item list-group-item-action">Audit Logs</a>
                </div>
            </div>

            <!-- Main Content -->
            <div class="col-md-9">
                <h2>Exam Management</h2>

                <% if (request.getParameter("msg") != null) { %>
                    <div class="alert alert-info alert-dismissible fade show" role="alert">
                        <%= request.getParameter("msg") %>
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                <% } %>

                <!-- Create Exam Button -->
                <button class="btn btn-primary mb-3" data-bs-toggle="modal" data-bs-target="#createExamModal">
                    <i class="bi bi-plus-circle"></i> Create New Exam
                </button>

                <!-- Exams Table -->
                <table class="table table-striped table-hover">
                    <thead class="table-dark">
                        <tr>
                            <th>ID</th>
                            <th>Title</th>
                            <th>Date</th>
                            <th>Duration</th>
                            <th>Total Marks</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% List<Exam> exams = (List<Exam>) request.getAttribute("exams");
                           if (exams != null) {
                               for (Exam exam : exams) { %>
                        <tr>
                            <td><%= exam.getExamId() %></td>
                            <td><%= exam.getExamTitle() %></td>
                            <td><%= exam.getExamDate() %></td>
                            <td><%= exam.getDurationMinutes() %> min</td>
                            <td><%= exam.getTotalMarks() %></td>
                            <td>
                                <span class="badge bg-<%= "COMPLETED".equals(exam.getStatus()) ? "success" : "SCHEDULED".equals(exam.getStatus()) ? "info" : "ONGOING".equals(exam.getStatus()) ? "warning" : "secondary" %>">
                                    <%= exam.getStatus() %>
                                </span>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/exam?action=edit&id=<%= exam.getExamId() %>" class="btn btn-sm btn-outline-primary">Edit</a>
                                <form action="${pageContext.request.contextPath}/exam" method="post" class="d-inline">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="examId" value="<%= exam.getExamId() %>">
                                    <button type="submit" class="btn btn-sm btn-outline-danger" onclick="return confirm('Delete this exam?')">Delete</button>
                                </form>
                            </td>
                        </tr>
                        <% }} %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Create Exam Modal -->
    <div class="modal fade" id="createExamModal" tabindex="-1">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Create New Exam</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <form action="${pageContext.request.contextPath}/exam" method="post">
                    <div class="modal-body">
                        <input type="hidden" name="action" value="create">
                        <div class="mb-3">
                            <label class="form-label">Exam Title</label>
                            <input type="text" class="form-control" name="examTitle" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Description</label>
                            <textarea class="form-control" name="examDescription" rows="3"></textarea>
                        </div>
                        <div class="row">
                            <div class="col-md-4 mb-3">
                                <label class="form-label">Total Marks</label>
                                <input type="number" class="form-control" name="totalMarks" value="100" required>
                            </div>
                            <div class="col-md-4 mb-3">
                                <label class="form-label">Duration (minutes)</label>
                                <input type="number" class="form-control" name="durationMinutes" value="60" required>
                            </div>
                            <div class="col-md-4 mb-3">
                                <label class="form-label">Passing Marks</label>
                                <input type="number" class="form-control" name="passingMarks" value="40" required>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-4 mb-3">
                                <label class="form-label">Exam Date</label>
                                <input type="date" class="form-control" name="examDate" required>
                            </div>
                            <div class="col-md-4 mb-3">
                                <label class="form-label">Start Time</label>
                                <input type="time" class="form-control" name="examStartTime" required>
                            </div>
                            <div class="col-md-4 mb-3">
                                <label class="form-label">End Time</label>
                                <input type="time" class="form-control" name="examEndTime" required>
                            </div>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Status</label>
                            <select class="form-select" name="status">
                                <option value="DRAFT">Draft</option>
                                <option value="SCHEDULED">Scheduled</option>
                            </select>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn btn-primary">Create Exam</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

