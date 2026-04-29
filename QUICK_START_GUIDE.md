# Quick Start Guide - Portal Enhancements

## What Was Added?

You now have complete violation logging, exam result tracking, and company admin functionality for your internship portal.

---

## Quick Overview - What Each File Does

### Models (Represent Data)
- `Violation.java` → Stores violation details (tab switch, camera off, etc.)
- `CompanyAdmin.java` → Links a user to a company as admin
- `ExamResult.java` → Stores exam scores and violation counts

### DAOs (Access Database)
- `ViolationDAO.java` → Save/retrieve violations from database
- `CompanyAdminDAO.java` → Manage company admin assignments
- `ExamResultDAO.java` → Save/retrieve exam results

### Servlets (Handle Requests)
- `ViolationServlet.java` → API for recording violations
- `ExamResultServlet.java` → API for managing results
- `CompanyAdminManagementServlet.java` → API for admin assignment

### Utilities (Helper Functions)
- `AuditLogger.java` → Log every action (login, apply, submit, etc.)
- `ReportGenerator.java` → Generate violation and result reports

### Database
- `DATABASE_SCHEMA_UPDATES.sql` → Run this to add new tables

### Documentation
- `PROJECT_ANALYSIS.md` → Complete analysis of what's needed
- `IMPLEMENTATION_GUIDE.md` → Step-by-step how to implement
- `PROFESSORS_SUMMARY.md` → What was delivered (for your professor)

---

## 5-Minute Setup

### Step 1: Run Database Updates
```bash
mysql -u root -p internship_exam_system < DATABASE_SCHEMA_UPDATES.sql
```

### Step 2: Copy Java Files
Copy all `.java` files from the descriptions above into your project:
- Models → `src/main/java/com/mitwpu/lca/model/`
- DAOs → `src/main/java/com/mitwpu/lca/dao/`
- Servlets → `src/main/java/com/mitwpu/lca/servlet/`
- Utilities → `src/main/java/com/mitwpu/lca/util/`

### Step 3: Add to pom.xml
```xml
<dependency>
    <groupId>com.googlecode.json-simple</groupId>
    <artifactId>json-simple</artifactId>
    <version>1.1.1</version>
</dependency>
```

### Step 4: Update web.xml
Add these servlet mappings to your `web.xml`:

```xml
<!-- Violations -->
<servlet>
    <servlet-name>ViolationServlet</servlet-name>
    <servlet-class>com.mitwpu.lca.servlet.ViolationServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>ViolationServlet</servlet-name>
    <url-pattern>/student/violations</url-pattern>
</servlet-mapping>

<!-- Results -->
<servlet>
    <servlet-name>ExamResultServlet</servlet-name>
    <servlet-class>com.mitwpu.lca.servlet.ExamResultServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>ExamResultServlet</servlet-name>
    <url-pattern>/exam-results</url-pattern>
</servlet-mapping>

<!-- Company Admin -->
<servlet>
    <servlet-name>CompanyAdminManagementServlet</servlet-name>
    <servlet-class>com.mitwpu.lca.servlet.CompanyAdminManagementServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>CompanyAdminManagementServlet</servlet-name>
    <url-pattern>/admin/company-admins</url-pattern>
</servlet-mapping>
```

### Step 5: Build
```bash
mvn clean install
```

---

## How to Use Each Feature

### Recording a Violation During Exam
```javascript
// In your proctoring JavaScript (exam-security.js)
fetch('/student/violations', {
    method: 'POST',
    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
    body: 'action=record&attemptId=1&studentId=5' +
          '&violationType=TAB_SWITCH&severity=HIGH' +
          '&violationDescription=Student switched to another tab'
})
.then(r => r.json())
.then(data => console.log(data.message));
```

### Getting Violation Summary
```java
// In any servlet
ViolationDAO dao = new ViolationDAO();
ViolationDAO.ViolationSummary summary = dao.getViolationSummary(attemptId);
System.out.println("Total: " + summary.getTotalViolations());
System.out.println("High: " + summary.highViolations);
System.out.println("Medium: " + summary.mediumViolations);
System.out.println("Low: " + summary.lowViolations);
```

### Logging a User Action
```java
// In ApplyInternshipServlet or anywhere
import com.mitwpu.lca.util.AuditLogger;

// After student applies for internship
AuditLogger.logApplicationCreation(studentId, applicationId, internshipId);

// After exam submission
AuditLogger.logExamAttemptSubmission(attemptId, studentId, examId, 
                                    obtainedMarks, ipAddress);
```

### Creating Exam Result
```java
// After exam submission
ViolationDAO violationDAO = new ViolationDAO();
ViolationDAO.ViolationSummary summary = violationDAO.getViolationSummary(attemptId);

ExamResult result = new ExamResult(attemptId, studentId, examId, 
                                  totalMarks, obtainedMarks, 
                                  summary.getTotalViolations());

ExamResultDAO resultDAO = new ExamResultDAO();
resultDAO.createExamResult(result);
```

### Assigning Company Admin
```java
// In admin servlet
CompanyAdminDAO dao = new CompanyAdminDAO();
boolean success = dao.assignCompanyAdmin(userId, companyId, "HR Manager");
```

### Getting Exam Results
```javascript
// Get results for a student
fetch('/exam-results?action=getByStudent&studentId=5')
    .then(r => r.json())
    .then(data => {
        data.data.forEach(result => {
            console.log(`${result.examTitle}: ${result.obtainedMarks}/${result.totalMarks}`);
        });
    });

// Get results for an exam (admin only)
fetch('/exam-results?action=getByExam&examId=10')
    .then(r => r.json())
    .then(data => {
        data.data.forEach(result => {
            console.log(`${result.studentName}: ${result.percentage}%`);
        });
    });
```

---

## Database Tables Added

| Table | Purpose | Key Columns |
|-------|---------|------------|
| `violation_logs` | Track proctoring violations | violation_type, severity, attempt_id |
| `company_admins` | Link users to companies | user_id, company_id, designation |
| `exam_results` | Store exam scores | obtained_marks, total_marks, violation_count |
| `action_logs` | Audit trail | user_id, action_type, entity_type, logged_at |

---

## API Endpoints Now Available

### Violations
```
POST /student/violations?action=record
  - Record a violation
  
POST /student/violations?action=getByAttempt&attemptId=1
  - Get all violations for an attempt
  
POST /student/violations?action=getSummary&attemptId=1
  - Get violation statistics
```

### Results
```
POST /exam-results?action=getByStudent&studentId=5
  - Get all results for a student
  
POST /exam-results?action=getByExam&examId=10
  - Get all results for an exam (admin only)
  
POST /exam-results?action=getById&resultId=1
  - Get specific result details
  
POST /exam-results?action=updateStatus&resultId=1&status=PUBLISHED
  - Update result status (admin only)
```

### Company Admins
```
POST /admin/company-admins?action=assign&userId=5&companyId=1&designation=HR Manager
  - Assign user as company admin
  
POST /admin/company-admins?action=getByCompany&companyId=1
  - Get all admins for a company
  
POST /admin/company-admins?action=remove&adminId=1
  - Remove admin assignment
```

---

## Common Integration Points

### In LoginServlet
```java
// After successful login
AuditLogger.logAction(user.getUserId(), "USER_LOGIN", "USER", 
                     user.getUserId(), "User logged in", 
                     request.getRemoteAddr(), 
                     request.getHeader("User-Agent"));
```

### In ApplyInternshipServlet
```java
// After successful application
AuditLogger.logApplicationCreation(studentId, applicationId, internshipId);
```

### During Exam (JavaScript)
```javascript
// When tab switch detected
recordViolation('TAB_SWITCH', 'HIGH', 'Student switched tabs');

// When camera off detected
recordViolation('CAMERA_OFF', 'HIGH', 'Camera feed disconnected');
```

### On Exam Submission (Servlet)
```java
// Calculate result and log
ViolationDAO vDao = new ViolationDAO();
ExamResultDAO rDao = new ExamResultDAO();
ViolationDAO.ViolationSummary summary = vDao.getViolationSummary(attemptId);

ExamResult result = new ExamResult(attemptId, studentId, examId, 
                                  totalMarks, obtainedMarks, 
                                  summary.getTotalViolations());
rDao.createExamResult(result);
AuditLogger.logExamAttemptSubmission(attemptId, studentId, examId, 
                                    obtainedMarks, request.getRemoteAddr());
```

---

## JSP Pages to Create (Templates in IMPLEMENTATION_GUIDE.md)

- `company-admin/dashboard.jsp` - Admin dashboard
- `company-admin/applications.jsp` - Application review
- `company-admin/exam-results.jsp` - Results with violations
- `student/exam-results.jsp` - Student results view
- `student/violation-report.jsp` - Violation details

---

## Testing the Implementation

### Test 1: Record Violation
```bash
curl -X POST "http://localhost:8080/student/violations" \
  -d "action=record&attemptId=1&studentId=5&violationType=TAB_SWITCH&severity=HIGH&violationDescription=Tab switch"
```

### Test 2: Get Violations
```bash
curl -X POST "http://localhost:8080/student/violations?action=getByAttempt&attemptId=1"
```

### Test 3: Assign Company Admin
```bash
curl -X POST "http://localhost:8080/admin/company-admins" \
  -d "action=assign&userId=100&companyId=1&designation=HR Manager"
```

### Test 4: Get Results
```bash
curl -X POST "http://localhost:8080/exam-results?action=getByStudent&studentId=5"
```

---

## Key Points to Remember

✅ **Always use AuditLogger** - Log every important action  
✅ **Check permissions** - Verify user role before accessing data  
✅ **Use prepared statements** - All DAOs already do this (SQL injection safe)  
✅ **Handle exceptions** - All DAOs already do this  
✅ **Return JSON** - Servlets return JSON responses  
✅ **Timestamp everything** - Database automatically timestamps records  

---

## Still Need Help?

1. **Implementation details?** → Read `IMPLEMENTATION_GUIDE.md`
2. **What was added?** → Read `PROJECT_ANALYSIS.md`
3. **For your professor?** → Share `PROFESSORS_SUMMARY.md`
4. **Database schema?** → See `DATABASE_SCHEMA_UPDATES.sql`

---

## Files Checklist

- [ ] Copy `Violation.java` to model/
- [ ] Copy `CompanyAdmin.java` to model/
- [ ] Copy `ExamResult.java` to model/
- [ ] Copy `ViolationDAO.java` to dao/
- [ ] Copy `CompanyAdminDAO.java` to dao/
- [ ] Copy `ExamResultDAO.java` to dao/
- [ ] Copy `ViolationServlet.java` to servlet/
- [ ] Copy `ExamResultServlet.java` to servlet/
- [ ] Copy `CompanyAdminManagementServlet.java` to servlet/
- [ ] Copy `AuditLogger.java` to util/
- [ ] Copy `ReportGenerator.java` to util/
- [ ] Update `pom.xml` with json-simple dependency
- [ ] Update `web.xml` with servlet mappings
- [ ] Run `DATABASE_SCHEMA_UPDATES.sql`
- [ ] Build project: `mvn clean install`
- [ ] Create JSP pages from templates
- [ ] Integrate logging into existing servlets
- [ ] Test each feature
- [ ] Deploy to server

---

**Everything is ready to go! Start with Step 1 and follow the checklist.**

