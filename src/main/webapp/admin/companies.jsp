<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.mitwpu.lca.model.User" %>
<%@ page import="com.mitwpu.lca.dao.CompanyDAO" %>
<%@ page import="com.mitwpu.lca.model.Company" %>
<%@ page import="java.util.List" %>

<%
    // Check authentication
    User user = (User) session.getAttribute("user");
    if (user == null || !user.getRole().equals("ADMIN")) {
        response.sendRedirect(request.getContextPath() + "/login.jsp?error=Unauthorized");
        return;
    }

    // Get all companies
    CompanyDAO companyDAO = new CompanyDAO();
    List<Company> companies = companyDAO.getAllCompanies();
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Company Management - InternshipHub Admin</title>
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
            padding: 0 20px 30px;
        }

        .page-header {
            background: white;
            padding: 30px;
            border-radius: 12px;
            margin-bottom: 30px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .page-header h1 {
            color: #333;
            font-size: 1.8em;
        }

        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 6px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 8px;
        }

        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }

        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
        }

        .btn-secondary {
            background: #f0f0f0;
            color: #333;
        }

        .btn-secondary:hover {
            background: #e0e0e0;
        }

        .btn-danger {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
            padding: 6px 12px;
            font-size: 0.9em;
        }

        .btn-danger:hover {
            background: #f5c6cb;
        }

        .btn-edit {
            background: #e3f2fd;
            color: #1565c0;
            border: 1px solid #bbdefb;
            padding: 6px 12px;
            font-size: 0.9em;
        }

        .btn-edit:hover {
            background: #bbdefb;
        }

        .table-container {
            background: white;
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }

        .table-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px;
            font-weight: 600;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        table thead th {
            background: #f5f5f5;
            padding: 15px;
            text-align: left;
            font-weight: 600;
            color: #333;
            border-bottom: 2px solid #ddd;
        }

        table tbody td {
            padding: 15px;
            border-bottom: 1px solid #eee;
            color: #555;
        }

        table tbody tr:hover {
            background: #f9f9f9;
        }

        .status-badge {
            display: inline-block;
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 0.85em;
            font-weight: 600;
        }

        .status-active {
            background: #d4edda;
            color: #155724;
        }

        .status-inactive {
            background: #f8d7da;
            color: #721c24;
        }

        .action-buttons {
            display: flex;
            gap: 8px;
        }

        .empty-state {
            text-align: center;
            padding: 60px 20px;
        }

        .empty-state h2 {
            color: #999;
            margin-bottom: 10px;
        }

        .empty-state p {
            color: #bbb;
            margin-bottom: 20px;
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
            margin: 5% auto;
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
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .close {
            color: #999;
            font-size: 1.5em;
            cursor: pointer;
            transition: color 0.3s;
        }

        .close:hover {
            color: #333;
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

        .form-group input,
        .form-group textarea,
        .form-group select {
            width: 100%;
            padding: 10px;
            border: 2px solid #e0e0e0;
            border-radius: 6px;
            font-size: 0.95em;
            font-family: 'Segoe UI', sans-serif;
        }

        .form-group input:focus,
        .form-group textarea:focus,
        .form-group select:focus {
            outline: none;
            border-color: #667eea;
            background: #f9f9f9;
        }

        .form-group textarea {
            min-height: 80px;
            resize: vertical;
        }

        .modal-footer {
            display: flex;
            gap: 10px;
            justify-content: flex-end;
            margin-top: 20px;
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

        @media (max-width: 768px) {
            .page-header {
                flex-direction: column;
                gap: 15px;
            }

            table {
                font-size: 0.9em;
            }

            table th, table td {
                padding: 10px;
            }

            .action-buttons {
                flex-direction: column;
            }
        }
    </style>
</head>
<body class="app-layout" data-page="companies">
    <%@ include file="../components/navbar.jsp" %>

    <div class="container">
        <!-- Page Header -->
        <div class="page-header">
            <h1>🏢 Company Management</h1>
            <button class="btn btn-primary" onclick="openAddModal()">➕ Add New Company</button>
        </div>

        <!-- Companies Table -->
        <div class="table-container">
            <% if (companies.isEmpty()) { %>
                <div class="empty-state">
                    <h2>No companies added yet</h2>
                    <p>Start by adding companies to the platform</p>
                    <button class="btn btn-primary" onclick="openAddModal()">Add First Company</button>
                </div>
            <% } else { %>
                <table>
                    <thead>
                        <tr>
                            <th>Company Name</th>
                            <th>Email</th>
                            <th>Phone</th>
                            <th>Location</th>
                            <th>Industry</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Company company : companies) { %>
                            <tr>
                                <td><strong><%= company.getCompanyName() %></strong></td>
                                <td><%= company.getCompanyEmail() %></td>
                                <td><%= company.getCompanyPhone() %></td>
                                <td><%= company.getLocation() != null ? company.getLocation() : "N/A" %></td>
                                <td><%= company.getIndustry() != null ? company.getIndustry() : "N/A" %></td>
                                <td>
                                    <span class="status-badge status-<%= company.getStatus().toLowerCase() %>">
                                        <%= company.getStatus() %>
                                    </span>
                                </td>
                                <td>
                                    <div class="action-buttons">
                                        <button class="btn btn-edit" onclick="openEditModal(<%= company.getCompanyId() %>)">✏️ Edit</button>
                                        <button class="btn btn-danger" onclick="deleteCompany(<%= company.getCompanyId() %>, '<%= company.getCompanyName() %>')">🗑️ Delete</button>
                                    </div>
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            <% } %>
        </div>
    </div>

    <!-- Add/Edit Modal -->
    <div id="companyModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <span id="modalTitle">Add New Company</span>
                <span class="close" onclick="closeModal()">&times;</span>
            </div>
            <form id="companyForm" onsubmit="submitForm(event)">
                <input type="hidden" name="action" id="actionInput" value="create">
                <input type="hidden" name="companyId" id="companyId" value="">

                <div class="form-group">
                    <label for="companyName">Company Name <span style="color: red;">*</span></label>
                    <input type="text" id="companyName" name="companyName" required>
                </div>

                <div class="form-group">
                    <label for="companyEmail">Email <span style="color: red;">*</span></label>
                    <input type="email" id="companyEmail" name="companyEmail" required>
                </div>

                <div class="form-group">
                    <label for="companyPhone">Phone <span style="color: red;">*</span></label>
                    <input type="tel" id="companyPhone" name="companyPhone" required>
                </div>

                <div class="form-group">
                    <label for="location">Location</label>
                    <input type="text" id="location" name="location" placeholder="e.g., Bangalore, Pune">
                </div>

                <div class="form-group">
                    <label for="industry">Industry</label>
                    <input type="text" id="industry" name="industry" placeholder="e.g., Technology, Finance">
                </div>

                <div class="form-group">
                    <label for="description">Description</label>
                    <textarea id="description" name="description" placeholder="Company description..."></textarea>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" onclick="closeModal()">Cancel</button>
                    <button type="submit" class="btn btn-primary">Save Company</button>
                </div>
            </form>
        </div>
    </div>

    <script>
        function openAddModal() {
            document.getElementById('modalTitle').textContent = 'Add New Company';
            document.getElementById('actionInput').value = 'create';
            document.getElementById('companyId').value = '';
            document.getElementById('companyForm').reset();
            document.getElementById('companyModal').style.display = 'block';
        }

        function openEditModal(companyId) {
            document.getElementById('actionInput').value = 'update';
            document.getElementById('companyId').value = companyId;
            document.getElementById('modalTitle').textContent = 'Edit Company';
            
            // Fetch company data
            fetch('<%= request.getContextPath() %>/admin/company?action=get&companyId=' + companyId)
                .then(response => response.json())
                .then(data => {
                    document.getElementById('companyName').value = data.companyName;
                    document.getElementById('companyEmail').value = data.companyEmail;
                    document.getElementById('companyPhone').value = data.companyPhone;
                    document.getElementById('location').value = data.location;
                    document.getElementById('industry').value = data.industry;
                    document.getElementById('companyModal').style.display = 'block';
                })
                .catch(error => {
                    console.error('Error:', error);
                    showAlert('error', 'Failed to load company data');
                });
        }

        function closeModal() {
            document.getElementById('companyModal').style.display = 'none';
        }

        function submitForm(event) {
            event.preventDefault();
            
            const formData = new FormData(document.getElementById('companyForm'));

            fetch('<%= request.getContextPath() %>/admin/company', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                closeModal();
                if (data.success) {
                    showAlert('success', data.message);
                    setTimeout(() => {
                        location.reload();
                    }, 2000);
                } else {
                    showAlert('error', data.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showAlert('error', 'An error occurred');
            });
        }

        function deleteCompany(companyId, companyName) {
            if (confirm('Are you sure you want to delete ' + companyName + '?')) {
                const formData = new FormData();
                formData.append('action', 'delete');
                formData.append('companyId', companyId);

                fetch('<%= request.getContextPath() %>/admin/company', {
                    method: 'POST',
                    body: formData
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        showAlert('success', data.message);
                        setTimeout(() => {
                            location.reload();
                        }, 2000);
                    } else {
                        showAlert('error', data.message);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    showAlert('error', 'An error occurred');
                });
            }
        }

        function showAlert(type, message) {
            const alertDiv = document.createElement('div');
            alertDiv.className = `alert alert-${type}`;
            alertDiv.textContent = message;
            document.querySelector('.container').insertBefore(alertDiv, document.querySelector('.page-header'));
            
            setTimeout(() => {
                alertDiv.remove();
            }, 5000);
        }

        // Close modal when clicking outside
        window.onclick = function(event) {
            const modal = document.getElementById('companyModal');
            if (event.target == modal) {
                modal.style.display = 'none';
            }
        }
    </script>
    <script src="<%= request.getContextPath() %>/js/navbar.js"></script>
</body>
</html>
