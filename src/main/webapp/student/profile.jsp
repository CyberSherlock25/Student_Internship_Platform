<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.mitwpu.lca.model.User" %>
<%@ page import="com.mitwpu.lca.dao.StudentDAO, com.mitwpu.lca.dao.UserDAO" %>
<%@ page import="com.mitwpu.lca.model.Student, com.mitwpu.lca.util.DBConnection" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.sql.Connection, java.sql.PreparedStatement, java.sql.ResultSet" %>

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

    // Handle form submission
    String message = "";
    String messageType = "";

    if ("POST".equalsIgnoreCase(request.getMethod())) {
        try {
            String action = request.getParameter("action");
            
            if ("update".equals(action)) {
                String rollNumber = request.getParameter("rollNumber");
                String departmentCode = request.getParameter("departmentCode");
                String departmentName = request.getParameter("departmentName");
                String dobStr = request.getParameter("dateOfBirth");
                String address = request.getParameter("address");
                String city = request.getParameter("city");
                String state = request.getParameter("state");
                String pincode = request.getParameter("pincode");
                String cgpaStr = request.getParameter("cgpa");
                String semesterStr = request.getParameter("semester");

                // Validation
                if (rollNumber == null || rollNumber.trim().isEmpty()) {
                    throw new Exception("Roll number is required");
                }
                if (departmentCode == null || departmentCode.trim().isEmpty()) {
                    throw new Exception("Department code is required");
                }
                if (cgpaStr == null || cgpaStr.trim().isEmpty()) {
                    throw new Exception("CGPA is required");
                }
                if (semesterStr == null || semesterStr.trim().isEmpty()) {
                    throw new Exception("Semester is required");
                }

                double cgpa = Double.parseDouble(cgpaStr);
                int semester = Integer.parseInt(semesterStr);

                if (cgpa < 0 || cgpa > 10) {
                    throw new Exception("CGPA must be between 0 and 10");
                }
                if (semester < 1 || semester > 8) {
                    throw new Exception("Semester must be between 1 and 8");
                }

                // Update student profile
                if (student == null) {
                    // Create new student profile
                    student = new Student();
                    student.setUserId(user.getUserId());
                    student.setRollNumber(rollNumber);
                    student.setDepartmentCode(departmentCode);
                    student.setDepartmentName(departmentName);
                    student.setDateOfBirth(dobStr != null && !dobStr.isEmpty() ? LocalDate.parse(dobStr) : null);
                    student.setAddress(address);
                    student.setCity(city);
                    student.setState(state);
                    student.setPincode(pincode);
                    student.setCgpa(cgpa);
                    student.setSemester(semester);
                    studentDAO.createStudent(student);
                } else {
                    // Update existing student
                    student.setRollNumber(rollNumber);
                    student.setDepartmentCode(departmentCode);
                    student.setDepartmentName(departmentName);
                    student.setDateOfBirth(dobStr != null && !dobStr.isEmpty() ? LocalDate.parse(dobStr) : null);
                    student.setAddress(address);
                    student.setCity(city);
                    student.setState(state);
                    student.setPincode(pincode);
                    student.setCgpa(cgpa);
                    student.setSemester(semester);
                    studentDAO.updateStudent(student);
                }

                // Update user profile
                user.setFirstName(request.getParameter("firstName") != null ? request.getParameter("firstName") : user.getFirstName());
                user.setLastName(request.getParameter("lastName") != null ? request.getParameter("lastName") : user.getLastName());
                user.setPhoneNumber(request.getParameter("phoneNumber") != null ? request.getParameter("phoneNumber") : user.getPhoneNumber());

                UserDAO userDAO = new UserDAO();
                userDAO.updateUser(user);

                message = "Profile updated successfully!";
                messageType = "success";
                
                // Refresh student object
                student = studentDAO.getStudentByUserId(user.getUserId());
            }
        } catch (NumberFormatException e) {
            message = "Invalid number format: " + e.getMessage();
            messageType = "error";
        } catch (Exception e) {
            message = "Error: " + e.getMessage();
            messageType = "error";
        }
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Profile - InternshipHub</title>
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
            max-width: 800px;
            margin: 0 auto;
            padding: 0 20px;
        }

        .profile-container {
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
            overflow: hidden;
        }

        .profile-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px;
            text-align: center;
        }

        .profile-header h1 {
            font-size: 2em;
            margin-bottom: 10px;
        }

        .profile-header p {
            opacity: 0.9;
            font-size: 1em;
        }

        .profile-content {
            padding: 40px;
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

        .form-section {
            margin-bottom: 30px;
            padding-bottom: 30px;
            border-bottom: 2px solid #eee;
        }

        .form-section:last-child {
            border-bottom: none;
        }

        .section-title {
            font-size: 1.3em;
            font-weight: 600;
            color: #333;
            margin-bottom: 20px;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .form-grid {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 20px;
        }

        .form-group {
            display: flex;
            flex-direction: column;
        }

        .form-group label {
            font-weight: 600;
            margin-bottom: 8px;
            color: #333;
        }

        .form-group input,
        .form-group select,
        .form-group textarea {
            padding: 12px;
            border: 2px solid #e0e0e0;
            border-radius: 6px;
            font-size: 1em;
            font-family: 'Segoe UI', sans-serif;
            transition: border-color 0.3s;
        }

        .form-group input:focus,
        .form-group select:focus,
        .form-group textarea:focus {
            outline: none;
            border-color: #667eea;
            background: #f9f9f9;
        }

        .form-group textarea {
            min-height: 100px;
            resize: vertical;
        }

        .form-grid.full {
            grid-template-columns: 1fr;
        }

        .form-group.required label::after {
            content: " *";
            color: red;
        }

        .button-group {
            display: flex;
            gap: 15px;
            margin-top: 30px;
        }

        .btn {
            padding: 12px 30px;
            border: none;
            border-radius: 6px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s;
            font-size: 1em;
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
        }

        .btn-secondary:hover {
            background: #e0e0e0;
        }

        .profile-completion {
            display: flex;
            align-items: center;
            gap: 15px;
            background: #e3f2fd;
            padding: 15px;
            border-radius: 6px;
            margin-bottom: 20px;
        }

        .progress-bar {
            flex: 1;
            height: 8px;
            background: #ccc;
            border-radius: 4px;
            overflow: hidden;
        }

        .progress-fill {
            height: 100%;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            width: <%= student != null && student.getRollNumber() != null ? "80%" : "40%" %>;
            transition: width 0.3s;
        }

        .progress-text {
            font-weight: 600;
            color: #1565c0;
            min-width: 50px;
        }

        @media (max-width: 768px) {
            .form-grid {
                grid-template-columns: 1fr;
            }

            .profile-header h1 {
                font-size: 1.5em;
            }

            .button-group {
                flex-direction: column;
            }
        }
    </style>
</head>
<body>
    <!-- Navigation -->
    <nav class="navbar">
        <div class="navbar-brand">🎓 InternshipHub</div>
        <ul class="navbar-menu">
            <li><a href="<%= request.getContextPath() %>/student/dashboard.jsp">Dashboard</a></li>
            <li><a href="<%= request.getContextPath() %>/student/browse-internships.jsp">Browse</a></li>
            <li><a href="<%= request.getContextPath() %>/student/my-applications.jsp">My Applications</a></li>
            <li><a href="<%= request.getContextPath() %>/student/profile.jsp">Profile</a></li>
        </ul>
        <div class="navbar-right">
            <span>Welcome, <%= user.getFirstName() %></span>
            <form action="<%= request.getContextPath() %>/logout" method="POST" style="margin: 0;">
                <button type="submit" class="btn btn-primary" style="margin: 0;">Logout</button>
            </form>
        </div>
    </nav>

    <div class="container">
        <div class="profile-container">
            <!-- Header -->
            <div class="profile-header">
                <h1>👤 My Profile</h1>
                <p>Complete your profile to unlock more opportunities</p>
            </div>

            <!-- Content -->
            <div class="profile-content">
                <!-- Alerts -->
                <% if (!message.isEmpty()) { %>
                    <div class="alert alert-<%= messageType %>">
                        <%= message %>
                    </div>
                <% } %>

                <!-- Profile Completion Progress -->
                <div class="profile-completion">
                    <span>Profile Completion:</span>
                    <div class="progress-bar">
                        <div class="progress-fill"></div>
                    </div>
                    <span class="progress-text"><%= student != null && student.getRollNumber() != null ? "80%" : "40%" %></span>
                </div>

                <!-- Profile Form -->
                <form method="POST" action="<%= request.getContextPath() %>/student/profile.jsp">
                    <input type="hidden" name="action" value="update">

                    <!-- Personal Information -->
                    <div class="form-section">
                        <h2 class="section-title">👤 Personal Information</h2>
                        <div class="form-grid">
                            <div class="form-group required">
                                <label for="firstName">First Name</label>
                                <input type="text" id="firstName" name="firstName" value="<%= user.getFirstName() != null ? user.getFirstName() : "" %>" required>
                            </div>
                            <div class="form-group required">
                                <label for="lastName">Last Name</label>
                                <input type="text" id="lastName" name="lastName" value="<%= user.getLastName() != null ? user.getLastName() : "" %>" required>
                            </div>
                        </div>
                        <div class="form-grid">
                            <div class="form-group required">
                                <label for="email">Email Address</label>
                                <input type="email" id="email" value="<%= user.getEmail() %>" disabled>
                                <small style="color: #999; margin-top: 5px;">Email cannot be changed</small>
                            </div>
                            <div class="form-group">
                                <label for="phoneNumber">Phone Number</label>
                                <input type="tel" id="phoneNumber" name="phoneNumber" value="<%= user.getPhoneNumber() != null ? user.getPhoneNumber() : "" %>" placeholder="10-digit number">
                            </div>
                        </div>
                    </div>

                    <!-- Academic Information -->
                    <div class="form-section">
                        <h2 class="section-title">🎓 Academic Information</h2>
                        <div class="form-grid">
                            <div class="form-group required">
                                <label for="rollNumber">Roll Number</label>
                                <input type="text" id="rollNumber" name="rollNumber" value="<%= student != null && student.getRollNumber() != null ? student.getRollNumber() : "" %>" required>
                            </div>
                            <div class="form-group required">
                                <label for="departmentCode">Department Code</label>
                                <input type="text" id="departmentCode" name="departmentCode" value="<%= student != null && student.getDepartmentCode() != null ? student.getDepartmentCode() : "" %>" required placeholder="e.g., CS, IT, EC">
                            </div>
                        </div>
                        <div class="form-grid">
                            <div class="form-group required">
                                <label for="departmentName">Department Name</label>
                                <input type="text" id="departmentName" name="departmentName" value="<%= student != null && student.getDepartmentName() != null ? student.getDepartmentName() : "" %>" required placeholder="e.g., Computer Science">
                            </div>
                            <div class="form-group required">
                                <label for="semester">Current Semester</label>
                                <select id="semester" name="semester" required>
                                    <option value="">Select Semester</option>
                                    <% for (int i = 1; i <= 8; i++) { %>
                                        <option value="<%= i %>" <%= student != null && student.getSemester() == i ? "selected" : "" %>>
                                            Semester <%= i %>
                                        </option>
                                    <% } %>
                                </select>
                            </div>
                        </div>
                        <div class="form-grid">
                            <div class="form-group required">
                                <label for="cgpa">CGPA (0.00 - 10.00)</label>
                                <input type="number" id="cgpa" name="cgpa" min="0" max="10" step="0.01" value="<%= student != null ? String.format("%.2f", student.getCgpa()) : "" %>" required>
                            </div>
                            <div class="form-group">
                                <label for="dateOfBirth">Date of Birth</label>
                                <input type="date" id="dateOfBirth" name="dateOfBirth" value="<%= student != null && student.getDateOfBirth() != null ? student.getDateOfBirth() : "" %>">
                            </div>
                        </div>
                    </div>

                    <!-- Contact Information -->
                    <div class="form-section">
                        <h2 class="section-title">📍 Contact Information</h2>
                        <div class="form-grid full">
                            <div class="form-group">
                                <label for="address">Address</label>
                                <textarea id="address" name="address" placeholder="Street address"><%= student != null && student.getAddress() != null ? student.getAddress() : "" %></textarea>
                            </div>
                        </div>
                        <div class="form-grid">
                            <div class="form-group">
                                <label for="city">City</label>
                                <input type="text" id="city" name="city" value="<%= student != null && student.getCity() != null ? student.getCity() : "" %>" placeholder="e.g., Pune">
                            </div>
                            <div class="form-group">
                                <label for="state">State</label>
                                <input type="text" id="state" name="state" value="<%= student != null && student.getState() != null ? student.getState() : "" %>" placeholder="e.g., Maharashtra">
                            </div>
                            <div class="form-group">
                                <label for="pincode">Pincode</label>
                                <input type="text" id="pincode" name="pincode" value="<%= student != null && student.getPincode() != null ? student.getPincode() : "" %>" placeholder="e.g., 411042">
                            </div>
                        </div>
                    </div>

                    <!-- Buttons -->
                    <div class="button-group">
                        <button type="submit" class="btn btn-primary">💾 Save Profile</button>
                        <a href="<%= request.getContextPath() %>/student/dashboard.jsp" class="btn btn-secondary">Cancel</a>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script>
        // Form validation
        document.querySelector('form').addEventListener('submit', function(e) {
            const cgpa = parseFloat(document.getElementById('cgpa').value);
            
            if (isNaN(cgpa) || cgpa < 0 || cgpa > 10) {
                e.preventDefault();
                alert('CGPA must be between 0 and 10');
                return false;
            }
        });

        // Real-time validation for phone number
        document.getElementById('phoneNumber').addEventListener('input', function() {
            this.value = this.value.replace(/[^0-9]/g, '').substring(0, 10);
        });

        // Pincode validation
        document.getElementById('pincode').addEventListener('input', function() {
            this.value = this.value.replace(/[^0-9]/g, '').substring(0, 6);
        });
    </script>
</body>
</html>
