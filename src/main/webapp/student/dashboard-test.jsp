<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.mitwpu.lca.model.User" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard Test</title>
</head>
<body>
    <h1>Dashboard Test for <%= user.getName() %></h1>
    <button onclick="testFetch()">Load Data</button>
    <div id="result"></div>
    
    <script>
        function testFetch() {
            const url = '/InternshipExamSystem/student/dashboard-stats';
            fetch(url)
                .then(r => r.json())
                .then(data => {
                    document.getElementById('result').innerText = JSON.stringify(data, null, 2);
                })
                .catch(e => {
                    document.getElementById('result').innerText = 'Error: ' + e.message;
                });
        }
    </script>
</body>
</html>
