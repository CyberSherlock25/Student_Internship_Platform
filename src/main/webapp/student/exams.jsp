<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.mitwpu.lca.model.User" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null || user.getRole() == null || !"STUDENT".equals(user.getRole())) {
        response.sendRedirect(request.getContextPath() + "/login.jsp?error=Unauthorized");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Exams - InternshipHub</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <style>
        .exam-placeholder {
            max-width: 900px;
            margin: 24px auto;
            background: rgba(255, 255, 255, 0.9);
            border: 1px solid rgba(15, 23, 42, 0.08);
            border-radius: 16px;
            padding: 28px;
            box-shadow: 0 14px 30px rgba(15, 23, 42, 0.12);
        }

        .exam-placeholder h1 {
            margin-top: 0;
            margin-bottom: 8px;
        }

        .exam-placeholder p {
            color: #4b5563;
            line-height: 1.6;
        }
    </style>
</head>
<body class="app-layout" data-page="exams">
    <%@ include file="../components/navbar.jsp" %>

    <section class="exam-placeholder">
        <h1>Exams Module Coming Soon</h1>
        <p>The examination dashboard is under development. You can continue browsing internships and managing applications while this module is being finalized.</p>
    </section>

    <script src="<%= request.getContextPath() %>/js/navbar.js"></script>
</body>
</html>
