<%@ page import="java.util.List" %>
<%@ page import="com.mitwpu.lca.model.Application" %>
<%@ page import="com.mitwpu.lca.model.Student" %>
<%@ page import="com.mitwpu.lca.model.Internship" %>
<%@ page import="com.mitwpu.lca.model.User" %>
<%@ page import="com.mitwpu.lca.dao.ApplicationDAO" %>
<%@ page import="com.mitwpu.lca.dao.StudentDAO" %>
<%@ page import="com.mitwpu.lca.dao.InternshipDAO" %>
<%@ page import="com.mitwpu.lca.dao.UserDAO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    User user = (User) session.getAttribute("user");
    if (user == null || !user.getRole().equals("ADMIN")) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    ApplicationDAO applicationDAO = new ApplicationDAO();
    StudentDAO studentDAO = new StudentDAO();
    InternshipDAO internshipDAO = new InternshipDAO();
    UserDAO userDAO = new UserDAO();
    
    List<Application> applications = applicationDAO.getApplicationsByStatus("PENDING");
    List<Application> allApplications = null;
    
    String statusFilter = request.getParameter("status");
    if (statusFilter != null && !statusFilter.isEmpty() && !statusFilter.equals("all")) {
        applications = applicationDAO.getApplicationsByStatus(statusFilter);
    } else if (statusFilter == null || statusFilter.equals("all")) {
        // Get all applications by fetching and combining
        applications = applicationDAO.getApplicationsByStatus("PENDING");
        applications.addAll(applicationDAO.getApplicationsByStatus("SHORTLISTED"));
        applications.addAll(applicationDAO.getApplicationsByStatus("REJECTED"));
        applications.addAll(applicationDAO.getApplicationsByStatus("ACCEPTED"));
    }
    
    // Count statistics
    int pendingCount = applicationDAO.getApplicationsByStatus("PENDING").size();
    int shortlistedCount = applicationDAO.getApplicationsByStatus("SHORTLISTED").size();
    int rejectedCount = applicationDAO.getApplicationsByStatus("REJECTED").size();
    int acceptedCount = applicationDAO.getApplicationsByStatus("ACCEPTED").size();
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Application Review - Admin Dashboard</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
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
            padding: 20px;
        }

        .container {
            max-width: 1400px;
            margin: 0 auto;
            background: white;
            border-radius: 15px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
            overflow: hidden;
        }

        .header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px;
        }

        .header h1 {
            font-size: 28px;
            font-weight: 600;
            margin-bottom: 20px;
        }

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
            gap: 15px;
        }

        .stat-card {
            background: rgba(255, 255, 255, 0.1);
            padding: 15px;
            border-radius: 10px;
            text-align: center;
            border: 2px solid rgba(255, 255, 255, 0.2);
        }

        .stat-card .number {
            font-size: 28px;
            font-weight: 700;
            margin-bottom: 5px;
        }

        .stat-card .label {
            font-size: 12px;
            opacity: 0.9;
        }

        .content {
            padding: 30px;
        }

        .filter-bar {
            display: flex;
            gap: 10px;
            margin-bottom: 20px;
            flex-wrap: wrap;
        }

        .filter-btn {
            padding: 10px 20px;
            border: 2px solid #eee;
            background: white;
            border-radius: 8px;
            cursor: pointer;
            font-weight: 600;
            transition: all 0.3s ease;
        }

        .filter-btn:hover, .filter-btn.active {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-color: #667eea;
        }

        .table-wrapper {
            overflow-x: auto;
            margin-bottom: 30px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        thead {
            background: #f5f5f5;
            border-bottom: 3px solid #667eea;
        }

        th {
            padding: 15px;
            text-align: left;
            font-weight: 600;
            color: #333;
        }

        td {
            padding: 15px;
            border-bottom: 1px solid #eee;
        }

        tr:hover {
            background: #f9f9f9;
        }

        .status-badge {
            display: inline-block;
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
        }

        .badge-pending {
            background: #fff3cd;
            color: #856404;
        }

        .badge-shortlisted {
            background: #cfe2ff;
            color: #084298;
        }

        .badge-rejected {
            background: #f8d7da;
            color: #721c24;
        }

        .badge-accepted {
            background: #d1e7dd;
            color: #0f5132;
        }

        .action-btn {
            background: none;
            border: none;
            color: #667eea;
            cursor: pointer;
            font-size: 18px;
            padding: 5px 10px;
            border-radius: 5px;
            transition: all 0.3s ease;
        }

        .action-btn:hover {
            background: rgba(102, 126, 234, 0.1);
            transform: scale(1.1);
        }

        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #999;
        }

        .empty-state h2 {
            font-size: 24px;
            margin-bottom: 10px;
            color: #666;
        }

        /* Modal Styles */
        .modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.5);
            animation: fadeIn 0.3s ease;
            overflow-y: auto;
        }

        .modal.show {
            display: flex;
            align-items: flex-start;
            justify-content: center;
        }

        @keyframes fadeIn {
            from { opacity: 0; }
            to { opacity: 1; }
        }

        .modal-content {
            background: white;
            padding: 30px;
            border-radius: 15px;
            width: 90%;
            max-width: 700px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
            animation: slideIn 0.3s ease;
            margin-top: 30px;
            margin-bottom: 30px;
        }

        @keyframes slideIn {
            from {
                transform: translateY(-50px);
                opacity: 0;
            }
            to {
                transform: translateY(0);
                opacity: 1;
            }
        }

        .modal-header {
            font-size: 24px;
            font-weight: 600;
            margin-bottom: 20px;
            color: #333;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .close {
            background: none;
            border: none;
            font-size: 28px;
            cursor: pointer;
            color: #aaa;
        }

        .close:hover {
            color: #000;
        }

        .application-info {
            background: #f9f9f9;
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 20px;
        }

        .info-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 15px;
            margin-bottom: 10px;
        }

        .info-item label {
            font-weight: 600;
            color: #666;
            font-size: 12px;
        }

        .info-item .value {
            color: #333;
            margin-top: 3px;
        }

        .cover-letter-section {
            margin-bottom: 20px;
        }

        .cover-letter-section h3 {
            font-size: 16px;
            font-weight: 600;
            margin-bottom: 10px;
            color: #333;
        }

        .cover-letter-box {
            background: #f9f9f9;
            padding: 15px;
            border-radius: 8px;
            border-left: 4px solid #667eea;
            max-height: 200px;
            overflow-y: auto;
            color: #555;
            line-height: 1.6;
        }

        .form-group {
            margin-bottom: 20px;
        }

        label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            color: #333;
        }

        select, textarea {
            width: 100%;
            padding: 12px;
            border: 2px solid #eee;
            border-radius: 8px;
            font-size: 14px;
            font-family: inherit;
            transition: border-color 0.3s ease;
        }

        select:focus, textarea:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }

        textarea {
            resize: vertical;
            min-height: 120px;
        }

        .rating-section {
            margin-bottom: 20px;
        }

        .rating-stars {
            display: flex;
            gap: 10px;
            margin-top: 10px;
        }

        .star {
            font-size: 30px;
            cursor: pointer;
            color: #ddd;
            transition: color 0.2s ease;
        }

        .star:hover, .star.active {
            color: #ffc107;
        }

        .modal-footer {
            display: flex;
            gap: 10px;
            justify-content: flex-end;
            margin-top: 30px;
            border-top: 1px solid #eee;
            padding-top: 20px;
        }

        .btn-cancel {
            background: #eee;
            color: #333;
            border: none;
            padding: 12px 24px;
            border-radius: 8px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
        }

        .btn-cancel:hover {
            background: #ddd;
        }

        .btn-submit {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            padding: 12px 24px;
            border-radius: 8px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
        }

        .btn-submit:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 20px rgba(102, 126, 234, 0.3);
        }

        .alert {
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 8px;
            display: none;
        }

        .alert.show {
            display: block;
        }

        .alert-success {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }

        .alert-error {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
    </style>
</head>
<body class="app-layout" data-page="applications">
    <%@ include file="../components/navbar.jsp" %>
    <div class="container">
        <div class="header">
            <h1>📋 Application Review</h1>
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="number"><%= pendingCount %></div>
                    <div class="label">Pending</div>
                </div>
                <div class="stat-card">
                    <div class="number"><%= shortlistedCount %></div>
                    <div class="label">Shortlisted</div>
                </div>
                <div class="stat-card">
                    <div class="number"><%= acceptedCount %></div>
                    <div class="label">Accepted</div>
                </div>
                <div class="stat-card">
                    <div class="number"><%= rejectedCount %></div>
                    <div class="label">Rejected</div>
                </div>
            </div>
        </div>

        <div class="content">
            <div id="alert" class="alert"></div>

            <div class="filter-bar">
                <button class="filter-btn <%= statusFilter == null || statusFilter.equals("all") ? "active" : "" %>" 
                    onclick="filterApplications('all')">All Applications</button>
                <button class="filter-btn <%= "PENDING".equals(statusFilter) ? "active" : "" %>" 
                    onclick="filterApplications('PENDING')">⏳ Pending</button>
                <button class="filter-btn <%= "SHORTLISTED".equals(statusFilter) ? "active" : "" %>" 
                    onclick="filterApplications('SHORTLISTED')">⭐ Shortlisted</button>
                <button class="filter-btn <%= "ACCEPTED".equals(statusFilter) ? "active" : "" %>" 
                    onclick="filterApplications('ACCEPTED')">✅ Accepted</button>
                <button class="filter-btn <%= "REJECTED".equals(statusFilter) ? "active" : "" %>" 
                    onclick="filterApplications('REJECTED')">❌ Rejected</button>
            </div>

            <% if (applications != null && !applications.isEmpty()) { %>
                <div class="table-wrapper">
                    <table>
                        <thead>
                            <tr>
                                <th>Student Name</th>
                                <th>Position</th>
                                <th>Applied Date</th>
                                <th>Status</th>
                                <th>Rating</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Application app : applications) {
                                Student student = studentDAO.getStudentById(app.getStudentId());
                                User studentUser = null;
                                if (student != null) {
                                    studentUser = userDAO.getUserById(student.getUserId());
                                }
                                Internship internship = internshipDAO.getInternshipById(app.getInternshipId());
                            %>
                                <tr>
                                    <td><strong><%= studentUser != null ? studentUser.getFullName() : "Unknown" %></strong></td>
                                    <td><%= internship != null ? internship.getJobTitle() : "Unknown" %></td>
                                    <td><%= app.getAppliedDate() != null ? app.getAppliedDate() : "N/A" %></td>
                                    <td>
                                        <span class="status-badge badge-<%= app.getStatus().toLowerCase() %>">
                                            <%= app.getStatus() %>
                                        </span>
                                    </td>
                                    <td><%= app.getRating() != null ? "⭐ " + app.getRating() + "/5" : "-" %></td>
                                    <td>
                                        <button class="action-btn" onclick="openReviewModal(<%= app.getApplicationId() %>)" title="Review">👁️</button>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            <% } else { %>
                <div class="empty-state">
                    <h2>No applications found</h2>
                    <p>There are no applications in this status category</p>
                </div>
            <% } %>
        </div>
    </div>

    <!-- Review Modal -->
    <div id="reviewModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <span>Review Application</span>
                <button class="close" onclick="closeModal()">&times;</button>
            </div>

            <div id="applicationDetails"></div>

            <form id="reviewForm" onsubmit="submitReview(event)">
                <div class="form-group">
                    <label for="status">Application Status *</label>
                    <select name="status" id="status" required>
                        <option value="">Select Status</option>
                        <option value="PENDING">Pending</option>
                        <option value="SHORTLISTED">Shortlisted</option>
                        <option value="REJECTED">Rejected</option>
                        <option value="ACCEPTED">Accepted</option>
                    </select>
                </div>

                <div class="rating-section">
                    <label>Rating</label>
                    <div class="rating-stars">
                        <span class="star" onclick="setRating(1)">★</span>
                        <span class="star" onclick="setRating(2)">★</span>
                        <span class="star" onclick="setRating(3)">★</span>
                        <span class="star" onclick="setRating(4)">★</span>
                        <span class="star" onclick="setRating(5)">★</span>
                    </div>
                    <input type="hidden" name="rating" id="ratingInput" value="0">
                </div>

                <div class="form-group">
                    <label for="feedback">Feedback</label>
                    <textarea name="feedback" id="feedback" placeholder="Provide feedback to the student..."></textarea>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn-cancel" onclick="closeModal()">Cancel</button>
                    <button type="submit" class="btn-submit">Save Review</button>
                </div>
            </form>
        </div>
    </div>

    <script>
        const contextPath = '<%= request.getContextPath() %>';
        let currentApplicationId = 0;

        function filterApplications(status) {
            const url = status === 'all' 
                ? `${contextPath}/admin/applications.jsp`
                : `${contextPath}/admin/applications.jsp?status=${status}`;
            window.location.href = url;
        }

        function openReviewModal(applicationId) {
            currentApplicationId = applicationId;
            fetch(`${contextPath}/admin/application-review?action=get&applicationId=${applicationId}`)
                .then(response => response.json())
                .then(data => {
                    const studentUser = `<%= studentDAO.getStudentById(0) != null ? userDAO.getUserById(studentDAO.getStudentById(0).getUserId()).getFullName() : "" %>`;
                    
                    const details = `
                        <div class="application-info">
                            <div class="info-row">
                                <div class="info-item">
                                    <label>Student ID</label>
                                    <div class="value">${data.studentId}</div>
                                </div>
                                <div class="info-item">
                                    <label>Internship ID</label>
                                    <div class="value">${data.internshipId}</div>
                                </div>
                            </div>
                            <div class="info-row">
                                <div class="info-item">
                                    <label>Current Status</label>
                                    <div class="value"><strong>${data.status}</strong></div>
                                </div>
                                <div class="info-item">
                                    <label>Current Rating</label>
                                    <div class="value">${data.rating ? '⭐ ' + data.rating + '/5' : 'Not rated'}</div>
                                </div>
                            </div>
                        </div>
                        <div class="cover-letter-section">
                            <h3>Cover Letter</h3>
                            <div class="cover-letter-box">` + escapeHtml(data.coverLetter || 'No cover letter provided') + `</div>
                        </div>
                    `;
                    
                    document.getElementById('applicationDetails').innerHTML = details;
                    document.getElementById('status').value = data.status;
                    document.getElementById('feedback').value = data.feedback || '';
                    setRatingVisual(data.rating || 0);
                    document.getElementById('reviewModal').classList.add('show');
                })
                .catch(error => {
                    showAlert('Error loading application details', 'error');
                    console.error(error);
                });
        }

        function closeModal() {
            document.getElementById('reviewModal').classList.remove('show');
        }

        function setRating(rating) {
            document.getElementById('ratingInput').value = rating;
            setRatingVisual(rating);
        }

        function setRatingVisual(rating) {
            const stars = document.querySelectorAll('.star');
            stars.forEach((star, index) => {
                if (index < rating) {
                    star.classList.add('active');
                } else {
                    star.classList.remove('active');
                }
            });
        }

        function submitReview(event) {
            event.preventDefault();
            
            const status = document.getElementById('status').value;
            const rating = document.getElementById('ratingInput').value;
            const feedback = document.getElementById('feedback').value;

            if (!status) {
                showAlert('Please select a status', 'error');
                return;
            }

            // Update status
            const statusFormData = new FormData();
            statusFormData.append('action', 'update-status');
            statusFormData.append('applicationId', currentApplicationId);
            statusFormData.append('status', status);

            fetch(`${contextPath}/admin/application-review`, {
                method: 'POST',
                body: statusFormData
            })
            .then(response => response.json())
            .then(data => {
                if (!data.success) throw new Error(data.message);
                
                // Update feedback
                if (feedback || rating > 0) {
                    const feedbackFormData = new FormData();
                    feedbackFormData.append('action', 'set-feedback');
                    feedbackFormData.append('applicationId', currentApplicationId);
                    feedbackFormData.append('feedback', feedback);
                    feedbackFormData.append('rating', rating);

                    return fetch(`${contextPath}/admin/application-review`, {
                        method: 'POST',
                        body: feedbackFormData
                    }).then(response => response.json());
                }
                return data;
            })
            .then(data => {
                if (data.success) {
                    showAlert('Application updated successfully', 'success');
                    closeModal();
                    setTimeout(() => {
                        location.reload();
                    }, 1000);
                } else {
                    showAlert(data.message, 'error');
                }
            })
            .catch(error => {
                showAlert('Error updating application', 'error');
                console.error(error);
            });
        }

        function showAlert(message, type) {
            const alert = document.getElementById('alert');
            alert.textContent = message;
            alert.className = `alert show alert-${type}`;
            setTimeout(() => {
                alert.classList.remove('show');
            }, 4000);
        }

        function escapeHtml(text) {
            const div = document.createElement('div');
            div.textContent = text;
            return div.innerHTML;
        }

        window.onclick = function(event) {
            const modal = document.getElementById('reviewModal');
            if (event.target === modal) {
                closeModal();
            }
        };
    </script>
    <script src="<%= request.getContextPath() %>/js/navbar.js"></script>
</body>
</html>
