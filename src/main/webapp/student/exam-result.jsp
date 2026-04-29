<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.mitwpu.lca.model.*" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Exam Result</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <%@ include file="../components/navbar.jsp" %>

    <div class="container mt-5">
        <% ExamAttempt attempt = (ExamAttempt) request.getAttribute("attempt");
           Exam exam = (Exam) request.getAttribute("exam");
           List<Answer> answers = (List<Answer>) request.getAttribute("answers");
           if (attempt != null && exam != null) {
               boolean passed = attempt.getTotalMarksObtained() >= exam.getPassingMarks();
        %>
        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="card shadow-lg">
                    <div class="card-header text-center <%= passed ? "bg-success" : "bg-danger" %> text-white">
                        <h3 class="mb-0">Exam Result</h3>
                        <h5><%= exam.getExamTitle() %></h5>
                    </div>
                    <div class="card-body text-center">
                        <div class="display-1 fw-bold <%= passed ? "text-success" : "text-danger" %>">
                            <%= attempt.getTotalMarksObtained() %><small class="fs-3 text-muted">/<%= exam.getTotalMarks() %></small>
                        </div>
                        <h4 class="mt-3">
                            <span class="badge <%= passed ? "bg-success" : "bg-danger" %>">
                                <%= passed ? "PASSED" : "FAILED" %>
                            </span>
                        </h4>
                        <p class="text-muted">Passing Marks: <%= exam.getPassingMarks() %></p>

                        <hr>

                        <h5 class="text-start">Question-wise Breakdown</h5>
                        <table class="table table-sm table-striped text-start">
                            <thead>
                                <tr>
                                    <th>#</th>
                                    <th>Question</th>
                                    <th>Your Answer</th>
                                    <th>Marks</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (answers != null) {
                                    int qNo = 1;
                                    for (Answer ans : answers) { %>
                                <tr>
                                    <td><%= qNo++ %></td>
                                    <td><%= ans.getQuestionText() != null ? ans.getQuestionText().substring(0, Math.min(50, ans.getQuestionText().length())) + "..." : "-" %></td>
                                    <td>
                                        <% if (ans.getSelectedOptionId() != null) { %>
                                            Option #<%= ans.getSelectedOptionId() %>
                                        <% } else if (ans.getSubjectiveAnswer() != null) { %>
                                            <%= ans.getSubjectiveAnswer().substring(0, Math.min(30, ans.getSubjectiveAnswer().length())) + "..." %>
                                        <% } else { %>
                                            <span class="text-muted">Not answered</span>
                                        <% } %>
                                    </td>
                                    <td><%= ans.getMarksObtained() %></td>
                                    <td>
                                        <% if (ans.getMarksObtained() > 0) { %>
                                            <span class="badge bg-success">Correct</span>
                                        <% } else if (ans.getSelectedOptionId() != null || ans.getSubjectiveAnswer() != null) { %>
                                            <span class="badge bg-danger">Wrong</span>
                                        <% } else { %>
                                            <span class="badge bg-secondary">Unattempted</span>
                                        <% } %>
                                    </td>
                                </tr>
                                <% }} %>
                            </tbody>
                        </table>

                        <div class="mt-4">
                            <a href="${pageContext.request.contextPath}/student/dashboard.jsp" class="btn btn-primary">Back to Dashboard</a>
                            <a href="${pageContext.request.contextPath}/student/exams.jsp" class="btn btn-outline-secondary">View All Exams</a>
                        </div>
                    </div>
                    <div class="card-footer text-muted text-center">
                        Submitted at: <%= attempt.getSubmittedAt() %>
                    </div>
                </div>
            </div>
        </div>
        <% } else { %>
        <div class="alert alert-warning">Result not found.</div>
        <% } %>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

