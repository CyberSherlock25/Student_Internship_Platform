<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.mitwpu.lca.model.User" %>
<%@ page import="com.mitwpu.lca.dao.StudentDAO" %>
<%@ page import="com.mitwpu.lca.model.Student" %>

<%
    User user = (User) session.getAttribute("user");
    if (user == null || !"STUDENT".equals(user.getRole())) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    StudentDAO dao = new StudentDAO();
    Student student = dao.getStudentByUserId(user.getUserId());
%>

<!DOCTYPE html>
<html>
<head>
    <title>Profile</title>

    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/student/dashboard.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

<style>
/* GLASS CARD */
.glass {
    background: rgba(255,255,255,0.06);
    backdrop-filter: blur(12px);
    border-radius: 16px;
    padding: 25px;
    margin-bottom: 20px;
    color: #e2e8f0;
    box-shadow: 0 8px 25px rgba(0,0,0,0.3);
}

/* HEADER */
.profile-header {
    display:flex;
    justify-content:space-between;
    align-items:center;
}

.profile-left {
    display:flex;
    gap:20px;
    align-items:center;
}

.avatar {
    width:75px;
    height:75px;
    border-radius:50%;
    background:white;
    color: black;
    display:flex;
    align-items:center;
    justify-content:center;
    font-size:28px;
}

/* BUTTON */
.edit-btn {
    background:#6366f1;
    padding:8px 16px;
    border-radius:20px;
    cursor:pointer;
    color:white;
    transition:0.3s;
}
.edit-btn:hover { transform:scale(1.05); }

/* STATS */
.stats-grid {
    display:grid;
    grid-template-columns:repeat(4,1fr);
    gap:15px;
}

.stat-card {
    text-align:center;
    padding:15px;
    border-radius:12px;
    background: rgba(0,0,0,0.3);
}

/* DETAILS */
.details-grid {
    display:grid;
    grid-template-columns:1fr 1fr;
    gap:12px;
}

.details-grid div {
    background: rgba(0,0,0,0.25);
    padding:12px;
    border-radius:8px;
}

/* MODAL */
.modal {
    display:none;
    position:fixed;
    top:0;left:0;
    width:100%;height:100%;
    background: rgba(0,0,0,0.6);
}

.modal-box {
    background: rgba(30,41,59,0.95);
    backdrop-filter: blur(15px);
    border-radius:16px;
    padding:25px;
    width:500px;
    margin:50px auto;
    color:white;
}

/* FORM GRID */
.form-grid {
    display:grid;
    grid-template-columns:1fr 1fr;
    gap:10px;
}

.modal-box label {
    font-size:12px;
    color:#94a3b8;
}

.modal-box input {
    width:100%;
    padding:8px;
    border-radius:8px;
    border:none;
    background:#1e293b;
    color:white;
}

.modal-box button {
    width:100%;
    padding:10px;
    margin-top:10px;
    border-radius:8px;
    background:#6366f1;
}
</style>

<script>
function openModal(){ document.getElementById("modal").style.display="block"; }
function closeModal(){ document.getElementById("modal").style.display="none"; }
</script>

</head>

<body class="app-layout" data-page="profile">

<%@ include file="../components/navbar.jsp" %>

<main class="main-content">

<div style="max-width:1100px; margin:30px auto;">

<!-- HEADER -->
<section class="glass profile-header">
    <div class="profile-left">
        <div class="avatar"><i class="fas fa-user"></i></div>
        <div>
            <h2><%= user.getFullName() %></h2>
            <p><i class="fas fa-envelope"></i> <%= user.getEmail() %></p>
            <p>
                <i class="fas fa-graduation-cap"></i> 
                <%= student!=null && student.getDepartmentName()!=null ? student.getDepartmentName() : "-" %> 
                | Sem 
                <%= student!=null ? student.getSemester() : "-" %>
            </p>
        </div>
    </div>

    <div class="edit-btn" onclick="openModal()">✏️ Edit</div>
</section>

<!-- STATS -->
<section class="glass">
<h3>📊 Your Progress</h3>

<div class="stats-grid">
    <div class="stat-card"><i class="fas fa-file-alt"></i><h3 id="totalApps">0</h3><p>Applications</p></div>
    <div class="stat-card"><i class="fas fa-star"></i><h3 id="shortlisted">0</h3><p>Shortlisted</p></div>
    <div class="stat-card"><i class="fas fa-check-circle"></i><h3 id="selected">0</h3><p>Selected</p></div>
    <div class="stat-card"><i class="fas fa-chart-line"></i>
        <h3><%= student!=null ? student.getCgpa() : "-" %></h3><p>CGPA</p>
    </div>
</div>
</section>

<!-- DETAILS -->
<section class="glass">
<h3>📋 Profile Details</h3>

<div class="details-grid">
    <div>Roll: <%= student!=null && student.getRollNumber()!=null ? student.getRollNumber() : "-" %></div>
    <div>Dept Code: <%= student!=null && student.getDepartmentCode()!=null ? student.getDepartmentCode() : "-" %></div>
    <div>Department: <%= student!=null && student.getDepartmentName()!=null ? student.getDepartmentName() : "-" %></div>
    <div>Semester: <%= student!=null ? student.getSemester() : "-" %></div>

    <div>DOB: <%= student!=null && student.getDateOfBirth()!=null ? student.getDateOfBirth() : "-" %></div>
    <div>City: <%= student!=null && student.getCity()!=null ? student.getCity() : "-" %></div>
    <div>State: <%= student!=null && student.getState()!=null ? student.getState() : "-" %></div>
    <div>Address: <%= student!=null && student.getAddress()!=null ? student.getAddress() : "-" %></div>
</div>
</section>

</div>
</main>

<!-- MODAL -->
<div class="modal" id="modal">
<div class="modal-box">

<h3>✏️ Edit Profile</h3>

<form method="post" action="<%= request.getContextPath() %>/student/save-profile">

<div class="form-grid">

<div>
<label>Roll</label>
<input type="text" name="roll" value="<%= student!=null?student.getRollNumber():"" %>">
</div>

<div>
<label>Dept Code</label>
<input type="text" name="deptCode" value="<%= student!=null?student.getDepartmentCode():"" %>">
</div>

<div>
<label>Dept Name</label>
<input type="text" name="deptName" value="<%= student!=null?student.getDepartmentName():"" %>">
</div>

<div>
<label>CGPA</label>
<input type="number" step="0.01" name="cgpa" value="<%= student!=null?student.getCgpa():"" %>">
</div>

<div>
<label>Semester</label>
<input type="number" name="semester" value="<%= student!=null?student.getSemester():"" %>">
</div>

<div>
<label>DOB</label>
<input type="date" name="dob" value="<%= student!=null?student.getDateOfBirth():"" %>">
</div>

<div>
<label>City</label>
<input type="text" name="city" value="<%= student!=null?student.getCity():"" %>">
</div>

<div>
<label>State</label>
<input type="text" name="state" value="<%= student!=null?student.getState():"" %>">
</div>

<div>
<label>Pincode</label>
<input type="text" name="pincode" value="<%= student!=null?student.getPincode():"" %>">
</div>

<div>
<label>Address</label>
<input type="text" name="address" value="<%= student!=null?student.getAddress():"" %>">
</div>

</div>

<button type="submit">Save Changes</button>
</form>

<button onclick="closeModal()">Close</button>

</div>
</div>

<script>
const contextPath = '<%= request.getContextPath() %>';

fetch(contextPath + '/student/profile-stats')
.then(res => res.json())
.then(data => {
    if(data.success){
        totalApps.innerText = data.total;
        shortlisted.innerText = data.shortlisted;
        selected.innerText = data.selected;
    }
});
</script>

<script src="<%= request.getContextPath() %>/js/navbar.js"></script>

</body>
</html>