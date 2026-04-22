<%@ page import="java.util.List" %>
<%@ page import="com.mitwpu.lca.model.Internship" %>
<%@ page import="com.mitwpu.lca.model.Company" %>
<%@ page import="com.mitwpu.lca.model.User" %>
<%@ page import="com.mitwpu.lca.dao.InternshipDAO" %>
<%@ page import="com.mitwpu.lca.dao.CompanyDAO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    User user = (User) session.getAttribute("user");
    if (user == null || !user.getRole().equals("ADMIN")) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    InternshipDAO internshipDAO = new InternshipDAO();
    CompanyDAO companyDAO = new CompanyDAO();
    List<Internship> internships = internshipDAO.getAllInternships();
    List<Company> companies = companyDAO.getAllCompanies();
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Internship Management - Admin Dashboard</title>
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
            max-width: 1200px;
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
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .header h1 {
            font-size: 28px;
            font-weight: 600;
        }

        .btn-primary {
            background: white;
            color: #667eea;
            border: none;
            padding: 12px 24px;
            border-radius: 8px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
        }

        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 20px rgba(0, 0, 0, 0.2);
        }

        .content {
            padding: 30px;
        }

        .table-wrapper {
            overflow-x: auto;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
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

        .badge-open {
            background: #d4edda;
            color: #155724;
        }

        .badge-closed {
            background: #f8d7da;
            color: #721c24;
        }

        .action-btn {
            background: none;
            border: none;
            color: #667eea;
            cursor: pointer;
            font-size: 18px;
            margin: 0 5px;
            transition: all 0.3s ease;
        }

        .action-btn:hover {
            transform: scale(1.2);
            color: #764ba2;
        }

        .btn-delete {
            color: #dc3545;
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
        }

        .modal.show {
            display: flex;
            align-items: center;
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
            max-width: 600px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
            animation: slideIn 0.3s ease;
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

        input, textarea, select {
            width: 100%;
            padding: 12px;
            border: 2px solid #eee;
            border-radius: 8px;
            font-size: 14px;
            font-family: inherit;
            transition: border-color 0.3s ease;
        }

        input:focus, textarea:focus, select:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }

        textarea {
            resize: vertical;
            min-height: 100px;
        }

        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 15px;
        }

        .modal-footer {
            display: flex;
            gap: 10px;
            justify-content: flex-end;
            margin-top: 30px;
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

        .close {
            position: absolute;
            right: 20px;
            top: 20px;
            font-size: 28px;
            font-weight: bold;
            color: #aaa;
            cursor: pointer;
            background: none;
            border: none;
        }

        .close:hover {
            color: #000;
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
<body>
    <div class="container">
        <div class="header">
            <h1>🎯 Internship Management</h1>
            <button class="btn-primary" onclick="openAddModal()">+ Add New Internship</button>
        </div>

        <div class="content">
            <div id="alert" class="alert"></div>

            <% if (internships != null && !internships.isEmpty()) { %>
                <div class="table-wrapper">
                    <table>
                        <thead>
                            <tr>
                                <th>Position Title</th>
                                <th>Company</th>
                                <th>Location</th>
                                <th>Duration</th>
                                <th>Stipend</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Internship internship : internships) {
                                Company company = null;
                                for (Company c : companies) {
                                    if (c.getCompanyId() == internship.getCompanyId()) {
                                        company = c;
                                        break;
                                    }
                                }
                            %>
                                <tr>
                                    <td><strong><%= internship.getJobTitle() %></strong></td>
                                    <td><%= company != null ? company.getCompanyName() : "Unknown" %></td>
                                    <td><%= internship.getJobLocation() %></td>
                                    <td><%= internship.getDurationMonths() %> months</td>
                                    <td>₹<%= String.format("%.0f", internship.getStipendAmount()) %></td>
                                    <td>
                                        <span class="status-badge <%= internship.getStatus().equals("OPEN") ? "badge-open" : "badge-closed" %>">
                                            <%= internship.getStatus() %>
                                        </span>
                                    </td>
                                    <td>
                                        <button class="action-btn" onclick="openEditModal(<%= internship.getInternshipId() %>)" title="Edit">✏️</button>
                                        <button class="action-btn btn-delete" onclick="deleteInternship(<%= internship.getInternshipId() %>)" title="Delete">🗑️</button>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            <% } else { %>
                <div class="empty-state">
                    <h2>No internships added yet</h2>
                    <p>Click "Add New Internship" to create your first internship posting</p>
                    <button class="btn-primary" onclick="openAddModal()" style="margin-top: 20px;">+ Add Internship</button>
                </div>
            <% } %>
        </div>
    </div>

    <!-- Add/Edit Modal -->
    <div id="internshipModal" class="modal">
        <div class="modal-content">
            <button class="close" onclick="closeModal()">&times;</button>
            <div class="modal-header" id="modalTitle">Add New Internship</div>
            <form id="internshipForm" onsubmit="submitForm(event)">
                <input type="hidden" name="action" id="actionInput" value="create">
                <input type="hidden" name="internshipId" id="internshipIdInput">

                <div class="form-group">
                    <label for="companyId">Company *</label>
                    <select name="companyId" id="companyId" required>
                        <option value="">Select a company</option>
                        <% if (companies != null) {
                            for (Company c : companies) { %>
                                <option value="<%= c.getCompanyId() %>"><%= c.getCompanyName() %></option>
                        <%  }
                        } %>
                    </select>
                </div>

                <div class="form-group">
                    <label for="jobTitle">Position Title *</label>
                    <input type="text" name="jobTitle" id="jobTitle" placeholder="e.g., Software Engineer, Data Analyst" required>
                </div>

                <div class="form-group">
                    <label for="jobLocation">Location *</label>
                    <input type="text" name="jobLocation" id="jobLocation" placeholder="e.g., Bangalore, Mumbai" required>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="durationMonths">Duration (Months) *</label>
                        <input type="number" name="durationMonths" id="durationMonths" min="1" max="12" placeholder="e.g., 6" required>
                    </div>
                    <div class="form-group">
                        <label for="stipendAmount">Stipend Amount (₹) *</label>
                        <input type="number" name="stipendAmount" id="stipendAmount" min="0" step="100" placeholder="e.g., 15000" required>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="minimumCgpa">Minimum CGPA *</label>
                        <input type="number" name="minimumCgpa" id="minimumCgpa" min="0" max="10" step="0.1" placeholder="e.g., 7.0" required>
                    </div>
                    <div class="form-group">
                        <label for="status">Status *</label>
                        <select name="status" id="status" required>
                            <option value="OPEN">OPEN</option>
                            <option value="CLOSED">CLOSED</option>
                        </select>
                    </div>
                </div>

                <div class="form-group">
                    <label for="jobDescription">Description</label>
                    <textarea name="jobDescription" id="jobDescription" placeholder="Describe the role, responsibilities, and requirements..."></textarea>
                </div>

                <div class="form-group">
                    <label for="totalPositions">Total Positions</label>
                    <input type="number" name="totalPositions" id="totalPositions" min="1" placeholder="e.g., 5" value="1">
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn-cancel" onclick="closeModal()">Cancel</button>
                    <button type="submit" class="btn-submit">Save Internship</button>
                </div>
            </form>
        </div>
    </div>

    <script>
        const contextPath = '<%= request.getContextPath() %>';

        function openAddModal() {
            document.getElementById('actionInput').value = 'create';
            document.getElementById('modalTitle').textContent = 'Add New Internship';
            document.getElementById('internshipForm').reset();
            document.getElementById('internshipIdInput').value = '';
            document.getElementById('internshipModal').classList.add('show');
        }

        function openEditModal(internshipId) {
            fetch(`${contextPath}/admin/internship?action=get&internshipId=${internshipId}`)
                .then(response => response.json())
                .then(data => {
                    document.getElementById('actionInput').value = 'update';
                    document.getElementById('modalTitle').textContent = 'Edit Internship';
                    document.getElementById('internshipIdInput').value = data.internshipId;
                    document.getElementById('companyId').value = data.companyId;
                    document.getElementById('jobTitle').value = data.jobTitle;
                    document.getElementById('jobLocation').value = data.jobLocation;
                    document.getElementById('durationMonths').value = data.durationMonths;
                    document.getElementById('stipendAmount').value = data.stipendAmount;
                    document.getElementById('minimumCgpa').value = data.minimumCgpa;
                    document.getElementById('status').value = data.status;
                    document.getElementById('totalPositions').value = data.totalPositions;
                    document.getElementById('internshipModal').classList.add('show');
                })
                .catch(error => {
                    showAlert('Error loading internship details', 'error');
                    console.error(error);
                });
        }

        function closeModal() {
            document.getElementById('internshipModal').classList.remove('show');
        }

        function submitForm(event) {
            event.preventDefault();
            const formData = new FormData(document.getElementById('internshipForm'));
            const action = document.getElementById('actionInput').value;

            fetch(`${contextPath}/admin/internship`, {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    showAlert(data.message, 'success');
                    closeModal();
                    setTimeout(() => {
                        location.reload();
                    }, 1000);
                } else {
                    showAlert(data.message, 'error');
                }
            })
            .catch(error => {
                showAlert('Error processing request', 'error');
                console.error(error);
            });
        }

        function deleteInternship(internshipId) {
            if (confirm('Are you sure you want to delete this internship?')) {
                const formData = new FormData();
                formData.append('action', 'delete');
                formData.append('internshipId', internshipId);

                fetch(`${contextPath}/admin/internship`, {
                    method: 'POST',
                    body: formData
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        showAlert(data.message, 'success');
                        setTimeout(() => {
                            location.reload();
                        }, 1000);
                    } else {
                        showAlert(data.message, 'error');
                    }
                })
                .catch(error => {
                    showAlert('Error deleting internship', 'error');
                    console.error(error);
                });
            }
        }

        function showAlert(message, type) {
            const alert = document.getElementById('alert');
            alert.textContent = message;
            alert.className = `alert show alert-${type}`;
            setTimeout(() => {
                alert.classList.remove('show');
            }, 4000);
        }

        // Close modal when clicking outside
        window.onclick = function(event) {
            const modal = document.getElementById('internshipModal');
            if (event.target === modal) {
                closeModal();
            }
        };
    </script>
</body>
</html>
