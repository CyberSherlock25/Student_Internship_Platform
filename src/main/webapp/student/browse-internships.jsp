<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.mitwpu.lca.model.User" %>
<%@ page import="com.mitwpu.lca.dao.InternshipDAO" %>
<%@ page import="com.mitwpu.lca.dao.CompanyDAO" %>
<%@ page import="com.mitwpu.lca.dao.StudentDAO" %>
<%@ page import="com.mitwpu.lca.model.Internship" %>
<%@ page import="com.mitwpu.lca.model.Company" %>
<%@ page import="com.mitwpu.lca.model.Student" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

<%
    // Check authentication
    User user = (User) session.getAttribute("user");
    if (user == null || !user.getRole().equals("STUDENT")) {
        response.sendRedirect(request.getContextPath() + "/login.jsp?error=Unauthorized");
        return;
    }

    // Get student profile
    StudentDAO studentDAO = new StudentDAO();
    Student student = studentDAO.getStudentByUserId(user.getUserId());
    if (student == null) {
        out.println("<div class='alert alert-warning'>Please complete your profile first. <a href='profile.jsp'>Complete Profile</a></div>");
        return;
    }

    // Get all internships
    InternshipDAO internshipDAO = new InternshipDAO();
    CompanyDAO companyDAO = new CompanyDAO();
    List<Internship> allInternships = internshipDAO.getOpenInternships();
    
    // Get companies for filter dropdown
    List<Company> companies = companyDAO.getActiveCompanies();
    
    // Parse filter parameters
    String searchQuery = request.getParameter("search") != null ? request.getParameter("search").trim() : "";
    String companyFilter = request.getParameter("company") != null ? request.getParameter("company").trim() : "";
    String locationFilter = request.getParameter("location") != null ? request.getParameter("location").trim() : "";
    String minStipendStr = request.getParameter("minStipend") != null ? request.getParameter("minStipend").trim() : "0";
    String maxStipendStr = request.getParameter("maxStipend") != null ? request.getParameter("maxStipend").trim() : "1000000";
    
    double minStipend = 0, maxStipend = 1000000;
    try {
        minStipend = Double.parseDouble(minStipendStr);
        maxStipend = Double.parseDouble(maxStipendStr);
    } catch (NumberFormatException e) {
        minStipend = 0;
        maxStipend = 1000000;
    }
    
    // Filter internships
    List<Internship> filteredInternships = new java.util.ArrayList<>();
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
    
    for (Internship internship : allInternships) {
        // Apply filters
        if (!companyFilter.isEmpty() && internship.getCompanyId() != Integer.parseInt(companyFilter)) {
            continue;
        }
        
        if (!locationFilter.isEmpty() && !internship.getJobLocation().equalsIgnoreCase(locationFilter)) {
            continue;
        }
        
        if (internship.getStipendAmount() < minStipend || internship.getStipendAmount() > maxStipend) {
            continue;
        }
        
        if (!searchQuery.isEmpty()) {
            String query = searchQuery.toLowerCase();
            if (!internship.getJobTitle().toLowerCase().contains(query) && 
                !internship.getJobDescription().toLowerCase().contains(query)) {
                continue;
            }
        }
        
        filteredInternships.add(internship);
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Browse Internships - InternshipHub</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px 0;
        }

        .navbar {
            background: white;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            padding: 15px 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
        }

        .navbar-brand {
            font-size: 24px;
            font-weight: bold;
            color: #667eea;
        }

        .navbar-menu {
            display: flex;
            gap: 25px;
            list-style: none;
        }

        .navbar-menu a {
            text-decoration: none;
            color: #333;
            font-weight: 500;
            transition: color 0.3s;
        }

        .navbar-menu a:hover {
            color: #667eea;
        }

        .navbar-right {
            display: flex;
            gap: 15px;
            align-items: center;
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 0 20px;
        }

        .page-header {
            text-align: center;
            color: white;
            margin-bottom: 40px;
        }

        .page-header h1 {
            font-size: 2.5em;
            margin-bottom: 10px;
        }

        .page-header p {
            font-size: 1.1em;
            opacity: 0.9;
        }

        .main-content {
            display: grid;
            grid-template-columns: 250px 1fr;
            gap: 30px;
            margin-bottom: 40px;
        }

        .filter-panel {
            background: white;
            padding: 25px;
            border-radius: 12px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
            height: fit-content;
            position: sticky;
            top: 20px;
        }

        .filter-panel h3 {
            font-size: 1.2em;
            margin-bottom: 20px;
            color: #333;
            border-bottom: 3px solid #667eea;
            padding-bottom: 10px;
        }

        .filter-group {
            margin-bottom: 20px;
        }

        .filter-group label {
            display: block;
            font-weight: 600;
            margin-bottom: 8px;
            color: #555;
            font-size: 0.9em;
        }

        .filter-group input,
        .filter-group select {
            width: 100%;
            padding: 10px;
            border: 2px solid #e0e0e0;
            border-radius: 6px;
            font-size: 0.9em;
            transition: border-color 0.3s;
        }

        .filter-group input:focus,
        .filter-group select:focus {
            outline: none;
            border-color: #667eea;
        }

        .filter-buttons {
            display: flex;
            gap: 10px;
            margin-top: 20px;
        }

        .btn {
            padding: 10px 16px;
            border: none;
            border-radius: 6px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s;
            font-size: 0.9em;
        }

        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            flex: 1;
        }

        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
        }

        .btn-secondary {
            background: #f0f0f0;
            color: #333;
            flex: 1;
        }

        .btn-secondary:hover {
            background: #e0e0e0;
        }

        .internships-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
            gap: 25px;
        }

        .internship-card {
            background: white;
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
            transition: all 0.3s;
            display: flex;
            flex-direction: column;
        }

        .internship-card:hover {
            transform: translateY(-8px);
            box-shadow: 0 8px 25px rgba(0,0,0,0.15);
        }

        .card-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px;
        }

        .company-name {
            font-size: 1.3em;
            font-weight: bold;
            margin-bottom: 5px;
        }

        .position-title {
            font-size: 1.1em;
            font-weight: 600;
        }

        .card-body {
            padding: 20px;
            flex-grow: 1;
        }

        .internship-meta {
            display: flex;
            flex-direction: column;
            gap: 12px;
            margin-bottom: 15px;
            font-size: 0.9em;
        }

        .meta-item {
            display: flex;
            align-items: center;
            gap: 8px;
            color: #666;
        }

        .meta-label {
            font-weight: 600;
            color: #333;
            min-width: 80px;
        }

        .stipend-badge {
            display: inline-block;
            background: #e8f5e9;
            color: #2e7d32;
            padding: 6px 12px;
            border-radius: 20px;
            font-weight: 600;
            font-size: 0.9em;
        }

        .location-badge {
            display: inline-block;
            background: #e3f2fd;
            color: #1565c0;
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 0.9em;
        }

        .description {
            color: #666;
            font-size: 0.9em;
            line-height: 1.5;
            margin: 15px 0;
            min-height: 80px;
        }

        .card-footer {
            padding: 15px 20px;
            border-top: 1px solid #eee;
            display: flex;
            gap: 10px;
        }

        .btn-apply {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 6px;
            cursor: pointer;
            font-weight: 600;
            flex: 1;
            transition: all 0.3s;
        }

        .btn-apply:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
        }

        .btn-details {
            background: #f0f0f0;
            color: #333;
            border: none;
            padding: 10px 20px;
            border-radius: 6px;
            cursor: pointer;
            font-weight: 600;
            transition: all 0.3s;
        }

        .btn-details:hover {
            background: #e0e0e0;
        }

        .empty-state {
            grid-column: 1 / -1;
            text-align: center;
            padding: 60px 20px;
            background: white;
            border-radius: 12px;
        }

        .empty-state h2 {
            color: #999;
            margin-bottom: 10px;
        }

        .empty-state p {
            color: #bbb;
            font-size: 1.1em;
        }

        .results-info {
            color: white;
            margin-bottom: 20px;
            font-size: 1.1em;
        }

        .modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0,0,0,0.5);
            animation: fadeIn 0.3s;
        }

        .modal-content {
            background-color: white;
            margin: 10% auto;
            padding: 30px;
            border-radius: 12px;
            width: 90%;
            max-width: 600px;
            box-shadow: 0 5px 30px rgba(0,0,0,0.3);
            animation: slideIn 0.3s;
        }

        .modal-header {
            font-size: 1.5em;
            margin-bottom: 20px;
            color: #333;
            border-bottom: 2px solid #667eea;
            padding-bottom: 15px;
        }

        .modal-body {
            margin-bottom: 20px;
        }

        .form-group {
            margin-bottom: 15px;
        }

        .form-group label {
            display: block;
            font-weight: 600;
            margin-bottom: 8px;
            color: #333;
        }

        .form-group textarea {
            width: 100%;
            min-height: 120px;
            padding: 12px;
            border: 2px solid #e0e0e0;
            border-radius: 6px;
            font-family: 'Segoe UI', sans-serif;
            resize: vertical;
        }

        .form-group textarea:focus {
            outline: none;
            border-color: #667eea;
        }

        .char-count {
            font-size: 0.8em;
            color: #999;
            margin-top: 5px;
        }

        .modal-footer {
            display: flex;
            gap: 10px;
            justify-content: flex-end;
        }

        .close {
            color: #999;
            float: right;
            font-size: 1.5em;
            cursor: pointer;
            transition: color 0.3s;
        }

        .close:hover {
            color: #333;
        }

        @keyframes fadeIn {
            from { opacity: 0; }
            to { opacity: 1; }
        }

        @keyframes slideIn {
            from { 
                opacity: 0;
                transform: translateY(-50px);
            }
            to { 
                opacity: 1;
                transform: translateY(0);
            }
        }

        .alert {
            padding: 15px 20px;
            margin-bottom: 20px;
            border-radius: 6px;
            border-left: 4px solid;
        }

        .alert-success {
            background: #d4edda;
            color: #155724;
            border-color: #28a745;
        }

        .alert-error {
            background: #f8d7da;
            color: #721c24;
            border-color: #f5c6cb;
        }

        @media (max-width: 768px) {
            .main-content {
                grid-template-columns: 1fr;
            }

            .filter-panel {
                position: static;
            }

            .internships-grid {
                grid-template-columns: 1fr;
            }

            .page-header h1 {
                font-size: 1.8em;
            }

            .navbar-menu {
                gap: 15px;
            }
        }
    </style>
</head>
<body class="app-layout" data-page="internships">
    <%@ include file="../components/navbar.jsp" %>

    <div class="container">
        <!-- Page Header -->
        <div class="page-header">
            <h1>Discover Internship Opportunities</h1>
            <p>Find the perfect internship to launch your career</p>
        </div>

        <div class="main-content">
            <!-- Filter Sidebar -->
            <div class="filter-panel">
                <h3>🔍 Filters</h3>
                <form id="filterForm" method="GET">
                    <!-- Search -->
                    <div class="filter-group">
                        <label for="search">Search Position</label>
                        <input type="text" id="search" name="search" placeholder="e.g., Developer, Analyst" value="<%= searchQuery %>">
                    </div>

                    <!-- Company Filter -->
                    <div class="filter-group">
                        <label for="company">Company</label>
                        <select id="company" name="company">
                            <option value="">All Companies</option>
                            <% for (Company company : companies) { %>
                                <option value="<%= company.getCompanyId() %>" <%= companyFilter.equals(String.valueOf(company.getCompanyId())) ? "selected" : "" %>>
                                    <%= company.getCompanyName() %>
                                </option>
                            <% } %>
                        </select>
                    </div>

                    <!-- Location Filter -->
                    <div class="filter-group">
                        <label for="location">Location</label>
                        <input type="text" id="location" name="location" placeholder="e.g., Bangalore, Pune" value="<%= locationFilter %>">
                    </div>

                    <!-- Stipend Range -->
                    <div class="filter-group">
                        <label>Stipend Range</label>
                        <input type="number" name="minStipend" placeholder="Min" value="<%= minStipendStr %>">
                        <input type="number" name="maxStipend" placeholder="Max" value="<%= maxStipendStr %>" style="margin-top: 10px;">
                    </div>

                    <!-- Buttons -->
                    <div class="filter-buttons">
                        <button type="submit" class="btn btn-primary">Apply Filters</button>
                        <a href="<%= request.getContextPath() %>/student/browse-internships.jsp" class="btn btn-secondary">Clear</a>
                    </div>
                </form>
            </div>

            <!-- Internships List -->
            <div>
                <div class="results-info">
                    Found <strong><%= filteredInternships.size() %></strong> internship<%= filteredInternships.size() != 1 ? "s" : "" %>
                </div>

                <% if (filteredInternships.isEmpty()) { %>
                    <div class="empty-state">
                        <h2>No internships found</h2>
                        <p>Try adjusting your filters or search criteria</p>
                    </div>
                <% } else { %>
                    <div class="internships-grid">
                        <% for (Internship internship : filteredInternships) {
                            Company company = companyDAO.getCompanyById(internship.getCompanyId());
                            String companyName = company != null ? company.getCompanyName() : "Unknown Company";
                        %>
                            <div class="internship-card">
                                <div class="card-header">
                                    <div class="company-name"><%= companyName %></div>
                                    <div class="position-title"><%= internship.getJobTitle() %></div>
                                </div>
                                <div class="card-body">
                                    <div class="internship-meta">
                                        <div class="meta-item">
                                            <span class="meta-label">📍 Location:</span>
                                            <span class="location-badge"><%= internship.getJobLocation() %></span>
                                        </div>
                                        <div class="meta-item">
                                            <span class="meta-label">💰 Stipend:</span>
                                            <span class="stipend-badge">₹<%= String.format("%.0f", internship.getStipendAmount()) %>/month</span>
                                        </div>
                                        <div class="meta-item">
                                            <span class="meta-label">⏱️ Duration:</span>
                                            <span><%= internship.getDurationMonths() %> months</span>
                                        </div>
                                        <div class="meta-item">
                                            <span class="meta-label">📚 CGPA Required:</span>
                                            <span><%= String.format("%.2f", internship.getMinimumCgpa()) %>+</span>
                                        </div>
                                    </div>
                                    <div class="description">
                                        <%= internship.getJobDescription().length() > 150 ? 
                                            internship.getJobDescription().substring(0, 150) + "..." : 
                                            internship.getJobDescription() %>
                                    </div>
                                </div>
                                <div class="card-footer">
                                    <button class="btn-apply" onclick="openApplicationModal(<%= internship.getInternshipId() %>, '<%= companyName %>', '<%= internship.getJobTitle() %>')" >Apply Now</button>
                                </div>
                            </div>
                        <% } %>
                    </div>
                <% } %>
            </div>
        </div>
    </div>

    <!-- Application Modal -->
    <div id="applicationModal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="closeApplicationModal()">&times;</span>
            <div class="modal-header">
                <div id="modalTitle"></div>
            </div>
            <form id="applicationForm" method="POST" action="<%= request.getContextPath() %>/student/apply" onsubmit="submitApplication(event)">
                <input type="hidden" name="action" value="apply">
                <input type="hidden" id="internshipId" name="internshipId" value="">

                <div class="modal-body">
                    <div class="form-group">
                        <label for="coverLetter">Cover Letter <span style="color: red;">*</span></label>
                        <textarea id="coverLetter" name="coverLetter" placeholder="Tell us why you're interested in this internship (minimum 50 characters)" required></textarea>
                        <div class="char-count">
                            <span id="charCount">0</span> / 2000 characters
                        </div>
                    </div>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" onclick="closeApplicationModal()">Cancel</button>
                    <button type="submit" class="btn btn-primary">Submit Application</button>
                </div>
            </form>
        </div>
    </div>

    <script>
    function openApplicationModal(internshipId, company, position) {
        document.getElementById('applicationModal').style.display = 'block';

        // 🔥 SET INTERNSHIP ID (VERY IMPORTANT)
        document.getElementById('internshipId').value = internshipId;

        document.getElementById('modalTitle').textContent = 
            "Apply for " + position + " at " + company;

        document.getElementById('coverLetter').value = '';
        updateCharCount();
    }

    function closeApplicationModal() {
        document.getElementById('applicationModal').style.display = 'none';
    }

    function updateCharCount() {
        const textarea = document.getElementById('coverLetter');
        document.getElementById('charCount').textContent = textarea.value.length;
    }

    function submitApplication(event) {
        event.preventDefault();

        const internshipId = document.getElementById('internshipId').value;
        const coverLetter = document.getElementById('coverLetter').value.trim();

        // 🔥 VALIDATION
        if (!internshipId) {
            alert("Internship ID missing!");
            return;
        }

        if (coverLetter.length < 50) {
            alert('Cover letter must be at least 50 characters.');
            return;
        }

        // 🔥 SEND DATA (FIXED)
        fetch('<%= request.getContextPath() %>/student/apply', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: "internshipId=" + internshipId + 
                  "&coverLetter=" + encodeURIComponent(coverLetter)
        })
        .then(response => response.json())
        .then(data => {
            closeApplicationModal();

            if (data.success) {
                showAlert('success', data.message);

                setTimeout(() => {
                    window.location.href = 
                        '<%= request.getContextPath() %>/student/my-applications.jsp';
                }, 2000);
            } else {
                showAlert('error', data.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showAlert('error', 'Something went wrong!');
        });
    }

    function showAlert(type, message) {
        const alertDiv = document.createElement('div');
        alertDiv.className = "alert alert-" + type;
        alertDiv.textContent = message;

        document.querySelector('.container')
            .insertBefore(alertDiv, document.querySelector('.page-header'));

        setTimeout(() => {
            alertDiv.remove();
        }, 5000);
    }

    // Live character count
    document.getElementById('coverLetter')?.addEventListener('input', updateCharCount);

    // Close modal outside click
    window.onclick = function(event) {
        const modal = document.getElementById('applicationModal');
        if (event.target == modal) {
            modal.style.display = 'none';
        }
    }
</script>
    <script src="<%= request.getContextPath() %>/js/navbar.js"></script>
</body>
</html>
