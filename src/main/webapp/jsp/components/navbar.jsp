<%@ page import="com.mitwpu.lca.model.User" %>
<%
    User navSessionUser = (User) session.getAttribute("user");
    if (navSessionUser == null) {
        return;
    }

    String navRole = navSessionUser.getRole() != null ? navSessionUser.getRole().toUpperCase() : "";
    boolean navIsStudent = "STUDENT".equals(navRole);
    boolean navIsAdmin = "ADMIN".equals(navRole);

    String navDisplayName = navSessionUser.getFullName();
    if (navDisplayName == null || navDisplayName.trim().isEmpty()) {
        navDisplayName = navSessionUser.getEmail() != null ? navSessionUser.getEmail() : "User";
    }

    String navContext = request.getContextPath();
%>

<div class="app-topbar">
    <button type="button" class="app-icon-btn" id="navToggleBtn" aria-label="Toggle navigation">
        <i class="fa-solid fa-bars"></i>
    </button>
    <a class="app-brand" href="<%= navContext %>/<%= navIsAdmin ? "admin/dashboard.jsp" : "student/dashboard.jsp" %>">
        <i class="fa-solid fa-layer-group"></i>
        <span>Internship Hub</span>
    </a>
    <div class="app-topbar-actions">
        <button type="button" class="app-icon-btn" id="themeToggleBtn" aria-label="Toggle dark mode">
            <i class="fa-regular fa-moon"></i>
        </button>
        <span class="app-user-chip" title="Logged in user">
            <i class="fa-regular fa-circle-user"></i>
            <span><%= navDisplayName %></span>
        </span>
    </div>
</div>

<aside class="app-sidebar" id="appSidebar" aria-label="Sidebar">
    <div class="app-sidebar-header">
        <div>
            <p class="app-role-label"><%= navRole %> PORTAL</p>
            <h2>Navigation</h2>
        </div>
    </div>

    <nav class="app-nav">
        <% if (navIsStudent) { %>
            <a href="<%= navContext %>/student/dashboard.jsp" class="app-nav-link" data-page="dashboard">
                <i class="fa-solid fa-gauge"></i><span>Dashboard</span>
            </a>
            <a href="<%= navContext %>/student/browse-internships.jsp" class="app-nav-link" data-page="internships">
                <i class="fa-solid fa-briefcase"></i><span>Internships</span>
            </a>
            <a href="<%= navContext %>/student/my-applications.jsp" class="app-nav-link" data-page="applications">
                <i class="fa-solid fa-file-circle-check"></i><span>Applications</span>
            </a>
            <a href="<%= navContext %>/student/exams.jsp" class="app-nav-link" data-page="exams">
                <i class="fa-solid fa-pen-ruler"></i><span>Exams</span>
            </a>
            <a href="<%= navContext %>/student/profile.jsp" class="app-nav-link" data-page="profile">
                <i class="fa-solid fa-id-card"></i><span>Profile</span>
            </a>
        <% } %>

        <% if (navIsAdmin) { %>
            <a href="<%= navContext %>/admin/dashboard.jsp" class="app-nav-link" data-page="dashboard">
                <i class="fa-solid fa-gauge"></i><span>Dashboard</span>
            </a>
            <a href="<%= navContext %>/admin/internships.jsp" class="app-nav-link" data-page="internships">
                <i class="fa-solid fa-briefcase"></i><span>Internships</span>
            </a>
            <a href="<%= navContext %>/admin/applications.jsp" class="app-nav-link" data-page="applications">
                <i class="fa-solid fa-file-circle-check"></i><span>Applications</span>
            </a>
            <a href="<%= navContext %>/admin/companies.jsp" class="app-nav-link" data-page="companies">
                <i class="fa-solid fa-building"></i><span>Companies</span>
            </a>
        <% } %>
    </nav>

    <div class="app-sidebar-footer">
        <form action="<%= navContext %>/logout" method="POST">
            <button type="submit" class="app-logout-btn">
                <i class="fa-solid fa-right-from-bracket"></i>
                <span>Logout</span>
            </button>
        </form>
    </div>
</aside>

<div class="app-sidebar-backdrop" id="appSidebarBackdrop"></div>
