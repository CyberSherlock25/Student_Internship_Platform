# Implementation Guide for Internship Portal Enhancements

## Overview
This guide provides step-by-step instructions to implement the enhanced internship portal with comprehensive logging, violation tracking, and company admin functionality.

---

## PHASE 1: Database Setup (First Steps)

### Step 1: Execute Database Schema Updates
```bash
# Run the SQL update file
mysql -u root -p internship_exam_system < DATABASE_SCHEMA_UPDATES.sql
```

This will:
- Add `COMPANY_ADMIN` role to users table
- Create `company_admins` table
- Create `violation_logs` table  
- Create `exam_results` table
- Create `action_logs` table
- Create views for easy querying
- Create stored procedures for common operations

### Step 2: Verify New Tables
```sql
-- Check if tables were created successfully
SHOW TABLES LIKE '%admin%';
SHOW TABLES LIKE '%violation%';
SHOW TABLES LIKE '%result%';
SHOW TABLES LIKE '%action%';
```

---

## PHASE 2: Java Code Implementation

### Already Provided Files:
1. ✅ **Models** (in `/src/main/java/com/mitwpu/lca/model/`)
   - `Violation.java` - Tracks exam proctoring violations
   - `CompanyAdmin.java` - Links user to company
   - `ExamResult.java` - Stores exam results with violation data

2. ✅ **DAOs** (in `/src/main/java/com/mitwpu/lca/dao/`)
   - `ViolationDAO.java` - CRUD for violations
   - `CompanyAdminDAO.java` - CRUD for company admins
   - `ExamResultDAO.java` - CRUD for exam results

3. ✅ **Servlets** (in `/src/main/java/com/mitwpu/lca/servlet/`)
   - `ViolationServlet.java` - Record and retrieve violations
   - `ExamResultServlet.java` - Manage exam results
   - `CompanyAdminManagementServlet.java` - Assign/manage company admins

4. ✅ **Utilities** (in `/src/main/java/com/mitwpu/lca/util/`)
   - `AuditLogger.java` - Comprehensive action logging
   - `ReportGenerator.java` - Generate various reports

### Step 3: Update web.xml (if not already done)
Add servlet mappings to `/src/main/webapp/WEB-INF/web.xml`:

```xml
<!-- Violation Management -->
<servlet>
    <servlet-name>ViolationServlet</servlet-name>
    <servlet-class>com.mitwpu.lca.servlet.ViolationServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>ViolationServlet</servlet-name>
    <url-pattern>/student/violations</url-pattern>
</servlet-mapping>

<!-- Exam Results -->
<servlet>
    <servlet-name>ExamResultServlet</servlet-name>
    <servlet-class>com.mitwpu.lca.servlet.ExamResultServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>ExamResultServlet</servlet-name>
    <url-pattern>/exam-results</url-pattern>
</servlet-mapping>

<!-- Company Admin Management -->
<servlet>
    <servlet-name>CompanyAdminManagementServlet</servlet-name>
    <servlet-class>com.mitwpu.lca.servlet.CompanyAdminManagementServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>CompanyAdminManagementServlet</servlet-name>
    <url-pattern>/admin/company-admins</url-pattern>
</servlet-mapping>
```

### Step 4: Add JSON Library to pom.xml
Add to your `pom.xml` if not already present:

```xml
<!-- JSON Simple -->
<dependency>
    <groupId>com.googlecode.json-simple</groupId>
    <artifactId>json-simple</artifactId>
    <version>1.1.1</version>
</dependency>
```

---

## PHASE 3: Integration with Existing Code

### Step 5: Update LoginServlet to Log Actions
Add this after successful login in your `LoginServlet.java`:

```java
import com.mitwpu.lca.util.AuditLogger;

// After successful login validation
AuditLogger.logAction(
    user.getUserId(),
    "USER_LOGIN",
    "USER",
    user.getUserId(),
    "User logged in successfully",
    request.getRemoteAddr(),
    request.getHeader("User-Agent")
);
```

### Step 6: Update ApplyInternshipServlet to Log Application
Add this when application is created:

```java
import com.mitwpu.lca.util.AuditLogger;

// After successful application creation
AuditLogger.logApplicationCreation(
    studentId,
    applicationId,
    internshipId
);
```

### Step 7: Record Violations During Exam
In your exam taking JSP or servlet, add violation recording:

```javascript
// JavaScript for proctoring (exam-security.js or similar)
function recordViolation(violationType, severity, description) {
    fetch('/student/violations', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: `action=record&attemptId=${attemptId}&studentId=${studentId}` +
              `&violationType=${violationType}&severity=${severity}` +
              `&violationDescription=${description}`
    })
    .then(response => response.json())
    .then(data => console.log('Violation recorded:', data))
    .catch(error => console.error('Error:', error));
}

// Example usage:
// Tab switch detected
recordViolation('TAB_SWITCH', 'HIGH', 'Student switched to another tab');

// Camera off detected
recordViolation('CAMERA_OFF', 'HIGH', 'Camera stopped detecting face');
```

---

## PHASE 4: Create JSP Pages for Company Admin Dashboard

### Step 8: Create Company Admin Dashboard
Create `/src/main/webapp/company-admin/dashboard.jsp`:

```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.mitwpu.lca.model.User" %>

<%
    User user = (User) session.getAttribute("user");
    if (user == null || !("COMPANY_ADMIN".equals(user.getRole()))) {
        response.sendRedirect("/index.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <title>Company Admin Dashboard</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
    <%@ include file="/components/navbar.jsp" %>
    
    <div class="container">
        <h1>Company Admin Dashboard</h1>
        
        <div class="dashboard-grid">
            <!-- Applications Section -->
            <div class="card">
                <h3>Applications</h3>
                <div id="applicationStats">
                    <p>Total: <span id="totalApps">-</span></p>
                    <p>Pending: <span id="pendingApps">-</span></p>
                    <p>Shortlisted: <span id="shortlistedApps">-</span></p>
                    <p>Accepted: <span id="acceptedApps">-</span></p>
                </div>
                <a href="/company-admin/applications.jsp" class="btn">View Applications</a>
            </div>
            
            <!-- Exam Results Section -->
            <div class="card">
                <h3>Exam Results</h3>
                <div id="resultStats">
                    <p>Total Exams: <span id="totalExams">-</span></p>
                    <p>Pending: <span id="pendingResults">-</span></p>
                    <p>Published: <span id="publishedResults">-</span></p>
                </div>
                <a href="/company-admin/exam-results.jsp" class="btn">View Results</a>
            </div>
            
            <!-- Reports Section -->
            <div class="card">
                <h3>Reports</h3>
                <p>Generate comprehensive reports on violations, results, and selections</p>
                <a href="/company-admin/reports.jsp" class="btn">Generate Reports</a>
            </div>
        </div>
    </div>
    
    <script>
        // Load dashboard statistics
        loadDashboardStats();
        
        function loadDashboardStats() {
            // Will be implemented with AJAX calls to fetch data
        }
    </script>
</body>
</html>
```

### Step 9: Create Application Review Page
Create `/src/main/webapp/company-admin/applications.jsp`:

```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
    <title>Application Review</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
    <%@ include file="/components/navbar.jsp" %>
    
    <div class="container">
        <h1>Shortlisted Applications</h1>
        
        <table id="applicationsTable">
            <thead>
                <tr>
                    <th>Student Name</th>
                    <th>Email</th>
                    <th>Roll Number</th>
                    <th>CGPA</th>
                    <th>Position Applied</th>
                    <th>Applied Date</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody id="applicationsList">
                <tr><td colspan="8">Loading...</td></tr>
            </tbody>
        </table>
    </div>
    
    <script>
        loadApplications();
        
        function loadApplications() {
            // AJAX to load shortlisted applications
        }
        
        function updateApplicationStatus(appId, status) {
            // AJAX to update application status
        }
    </script>
</body>
</html>
```

### Step 10: Create Exam Results Page
Create `/src/main/webapp/company-admin/exam-results.jsp`:

```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
    <title>Exam Results with Violation Report</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
    <%@ include file="/components/navbar.jsp" %>
    
    <div class="container">
        <h1>Exam Results & Violation Analysis</h1>
        
        <div class="filter-section">
            <label>Select Exam: 
                <select id="examSelect" onchange="loadResults()">
                    <option value="">All Exams</option>
                </select>
            </label>
        </div>
        
        <table id="resultsTable">
            <thead>
                <tr>
                    <th>Student Name</th>
                    <th>Email</th>
                    <th>Obtained Marks</th>
                    <th>Total Marks</th>
                    <th>Percentage</th>
                    <th>Passed</th>
                    <th>Violations</th>
                    <th>Violation Severity</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody id="resultsList">
                <tr><td colspan="9">Loading...</td></tr>
            </tbody>
        </table>
    </div>
    
    <script>
        loadResults();
        
        function loadResults() {
            // AJAX to load exam results with violation data
        }
        
        function viewViolationDetails(resultId) {
            // Show detailed violation breakdown
        }
    </script>
</body>
</html>
```

### Step 11: Create Student Results Page
Create `/src/main/webapp/student/exam-results.jsp`:

```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
    <title>My Exam Results</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
    <%@ include file="/components/navbar.jsp" %>
    
    <div class="container">
        <h1>My Exam Results</h1>
        
        <div id="resultsList">
            <!-- Results will be loaded via AJAX -->
        </div>
    </div>
    
    <script>
        loadMyResults();
        
        function loadMyResults() {
            fetch('/exam-results?action=getByStudent&studentId=' + getStudentId())
                .then(response => response.json())
                .then(data => displayResults(data.data))
                .catch(error => console.error('Error:', error));
        }
        
        function displayResults(results) {
            let html = '';
            results.forEach(result => {
                html += `
                    <div class="result-card">
                        <h3>${result.examTitle}</h3>
                        <p>Score: ${result.obtainedMarks}/${result.totalMarks} (${result.percentage}%)</p>
                        <p>Status: ${result.passed ? '✅ PASSED' : '❌ FAILED'}</p>
                        <p>Violations: ${result.totalViolations}</p>
                        <a href="/student/violation-report.jsp?resultId=${result.resultId}" 
                           class="btn">View Violation Report</a>
                    </div>
                `;
            });
            document.getElementById('resultsList').innerHTML = html;
        }
    </script>
</body>
</html>
```

---

## PHASE 5: Enhanced Existing Servlets

### Step 12: Update ApplyInternshipServlet to Add Logging
In your existing `ApplyInternshipServlet.java`, add logging:

```java
// At the end of successful application
AuditLogger.logApplicationCreation(studentId, applicationId, internshipId);
```

### Step 13: Create Exam Submission Handler
Update your exam submission logic to:
1. Calculate obtained marks
2. Count violations for the attempt
3. Create ExamResult record
4. Log the submission

```java
// In your exam submission servlet
ViolationDAO violationDAO = new ViolationDAO();
ViolationDAO.ViolationSummary summary = violationDAO.getViolationSummary(attemptId);

ExamResult result = new ExamResult(
    attemptId, studentId, examId, 
    totalMarks, obtainedMarks, 
    summary.getTotalViolations()
);

ExamResultDAO resultDAO = new ExamResultDAO();
resultDAO.createExamResult(result);

AuditLogger.logExamAttemptSubmission(attemptId, studentId, examId, 
                                    obtainedMarks, request.getRemoteAddr());
```

---

## PHASE 6: Testing the Implementation

### Test Case 1: Admin Workflow
1. ✅ Admin logs in
2. ✅ Admin adds company
3. ✅ Admin assigns company admin user
4. ✅ Check `company_admins` table - record created
5. ✅ Check `action_logs` table - actions recorded

### Test Case 2: Student Application & Exam
1. ✅ Student logs in
2. ✅ Student browses internships
3. ✅ Student applies to internship
4. ✅ Check `applications` table - record created
5. ✅ Check `application_logs` table - log entry created
6. ✅ Check `action_logs` table - action recorded

### Test Case 3: Exam with Violations
1. ✅ Student takes exam
2. ✅ Simulate violations (tab switch, camera off, etc.)
3. ✅ Check `violation_logs` table - violations recorded
4. ✅ Student submits exam
5. ✅ Check `exam_results` table - result created with violation count

### Test Case 4: Company Admin Review
1. ✅ Company admin logs in
2. ✅ Company admin views applications
3. ✅ Company admin shortlists candidates
4. ✅ Check `applications` table - status updated
5. ✅ Check `application_logs` table - status change logged
6. ✅ Company admin views exam results with violations
7. ✅ Company admin makes final selection

### Test Case 5: Student Views Results
1. ✅ Student logs in
2. ✅ Student views exam results
3. ✅ Student views violation report
4. ✅ Student sees company decision

---

## PHASE 7: Adding pom.xml Dependencies

Ensure your `pom.xml` has all necessary dependencies:

```xml
<!-- JSON Simple for API responses -->
<dependency>
    <groupId>com.googlecode.json-simple</groupId>
    <artifactId>json-simple</artifactId>
    <version>1.1.1</version>
</dependency>

<!-- MySQL Connector -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>

<!-- Jakarta Servlet API -->
<dependency>
    <groupId>jakarta.servlet</groupId>
    <artifactId>jakarta.servlet-api</artifactId>
    <version>5.0.0</version>
    <scope>provided</scope>
</dependency>

<!-- JUnit for Testing -->
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.13.2</version>
    <scope>test</scope>
</dependency>
```

---

## Key Features Implemented

### ✅ Violation Logging
- Tab switches, camera off, microphone off, screen sharing detected
- Severity levels: LOW, MEDIUM, HIGH
- Timestamp and IP address recorded
- Summary statistics available

### ✅ Test Registration Logs
- Who gave exam (student ID, name, email)
- When exam was taken (timestamp)
- Exam duration and IP address
- Violation details
- Final score and result

### ✅ Report Generation
- Violation reports
- Exam result reports
- Application tracking reports
- CSV export capability

### ✅ Audit Trail
- All user actions logged
- Entity changes tracked
- IP addresses recorded
- Comprehensive timestamp data

### ✅ Company Admin Role
- Separate admin role for company representatives
- Company-specific application viewing
- Result reviewing with violation details
- Student selection capability

---

## Files Summary

| File | Purpose | Status |
|------|---------|--------|
| Violation.java | Model for violations | ✅ Created |
| CompanyAdmin.java | Model for company admins | ✅ Created |
| ExamResult.java | Model for results | ✅ Created |
| ViolationDAO.java | Data access for violations | ✅ Created |
| CompanyAdminDAO.java | Data access for company admins | ✅ Created |
| ExamResultDAO.java | Data access for results | ✅ Created |
| ViolationServlet.java | API for violations | ✅ Created |
| ExamResultServlet.java | API for results | ✅ Created |
| CompanyAdminManagementServlet.java | API for company admin mgmt | ✅ Created |
| AuditLogger.java | Centralized logging | ✅ Created |
| ReportGenerator.java | Report generation utilities | ✅ Created |
| DATABASE_SCHEMA_UPDATES.sql | Database modifications | ✅ Created |
| JSP Pages | UI for dashboards | 📋 To Be Created (Template Provided) |

---

## Next Steps

1. Execute database schema updates
2. Compile and build the project
3. Create JSP pages using provided templates
4. Integrate violation recording in exam proctoring JS
5. Test all workflows
6. Deploy to application server

For any issues or questions, refer to the original DATABASE_SCHEMA.sql and existing codebase structure.

