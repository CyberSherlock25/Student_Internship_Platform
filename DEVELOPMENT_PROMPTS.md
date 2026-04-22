# Modular Development Prompts & Assignment Guide

This document contains the 6 modular LLM prompts to be executed in sequence by your team. Each prompt focuses on a specific module and should be completed before moving to the next one.

---

## Overview

- **Total Prompts**: 6
- **Development Duration**: 2-3 weeks (estimated)
- **Testing Period**: 1 week
- **Deployment**: Final week

---

## Prompt Assignment Matrix

| Dev | Prompt | Module | Duration |
|-----|--------|--------|----------|
| Dev 1 | 1 | Database Setup & Core Models | 3-4 days |
| Dev 1 | 2 | Authentication & Security Filter | 3-4 days |
| Dev 2 | 3 | Admin Company & Internship Module | 4-5 days |
| Dev 3 | 4 | Student Application Module with Transactions | 4-5 days |
| Dev 4 | 5 | Online Examination Engine & AJAX | 5-6 days |
| Dev 4 | 6 | Submission & Evaluation Engine | 3-4 days |

---

## PROMPT 1: Database Setup & Core Models (Dev 1)

**Duration**: 3-4 days | **Complexity**: Medium

Copy and paste this prompt to your LLM:

```
Act as an expert Java backend developer. I am building an Integrated Internship & Online Examination System 
using Java, JSP, Servlets, and JDBC. Based on the following database tables: `users` (Admin/Student), 
`students` (profile, CGPA), `companies`, `internships`, `applications`, `exams`, `questions`, `options`, 
`exam_attempts`, `answers`, and `audit_logs`.

Task 1: Write a robust `DBConnection.java` utility class using JDBC to connect to MySQL. Include methods for:
- Getting a connection with auto-commit enabled/disabled for transactions
- Closing connections safely
- Testing the connection

Task 2: Create the Java POJO (Model) classes for the following entities with appropriate fields, 
getters, setters, constructors, and toString() methods based on standard data types:
- User (userId, email, password, fullName, role, phoneNumber, status, createdAt, updatedAt)
- Student (studentId, userId, rollNumber, departmentCode, departmentName, cgpa, semester, dateOfBirth, 
  address, city, state, pincode, enrolledAt, updatedAt)
- Company (companyId, companyName, companyEmail, companyPhone, website, industry, headquarters, 
  city, state, country, companySize, description, status, registeredAt, updatedAt)
- Internship (internshipId, companyId, jobTitle, jobDescription, jobLocation, stipendAmount, 
  stipendType, durationMonths, startDate, endDate, applicationDeadline, minimumCgpa, requiredSkills, 
  totalPositions, filledPositions, status, createdAt, updatedAt)

Include helper methods like isAdmin(), isStudent(), isEligible(), isOpen(), hasAvailablePositions(), 
isDeadlinePassed() where applicable.

Do not write the UI yet. Focus strictly on clean, optimized Java code.
Make all classes implement Serializable for session storage.
```

### Deliverables:
- ✅ `DBConnection.java` in `src/main/java/com/mitwpu/lca/util/`
- ✅ `User.java` in `src/main/java/com/mitwpu/lca/model/`
- ✅ `Student.java` in `src/main/java/com/mitwpu/lca/model/`
- ✅ `Company.java` in `src/main/java/com/mitwpu/lca/model/`
- ✅ `Internship.java` in `src/main/java/com/mitwpu/lca/model/`

### Testing:
```bash
# Unit test DBConnection
mvn clean compile
# Manually test connection by running a simple Java program
```

---

## PROMPT 2: Authentication & Security Filter (Dev 1)

**Duration**: 3-4 days | **Complexity**: Medium-High

Depends on: PROMPT 1

Copy and paste this prompt to your LLM:

```
Continuing from the previous Java JDBC web project. I have already created DBConnection.java and 
all POJO classes (User, Student, Company, Internship).

Task 1: Create a `UserDAO.java` (Data Access Object) in com.mitwpu.lca.dao package with the following methods:
- authenticateUser(String email, String password): Returns a User object if credentials are valid, 
  otherwise returns null. Use SELECT query on the users table. DO NOT use plain text password comparison 
  in production; for now, use simple comparison but add a TODO comment.
- getUserById(int userId): Returns User object by ID
- getAllUsers(): Returns List of all User objects
- createUser(User user): Inserts a new user and returns the generated userId
- updateUser(User user): Updates user details
- Include proper exception handling and resource management (close Statement and ResultSet)

Task 2: Create a `LoginServlet.java` in com.mitwpu.lca.controller package that handles POST requests 
at /login. It should:
- Extract email and password parameters from the request
- Use UserDAO.authenticateUser() to validate credentials
- If authentication successful: Create an HttpSession, store the User object and role in session attributes, 
  and redirect based on role (admin -> /admin/dashboard.jsp, student -> /student/dashboard.jsp)
- If authentication fails: Redirect back to login.jsp with an error parameter
- Include session timeout configuration (30 minutes suggested)
- Prevent multiple active sessions for the same user (optional: advanced feature)
- Log authentication attempts for audit trail

Task 3: Implement a Java Servlet Filter named `AuthFilter.java` in com.mitwpu.lca.filter package that:
- Intercepts all requests to /admin/* and /student/* paths
- Checks if a valid HttpSession exists
- Verifies the user object exists in the session
- Validates that the user's role matches the requested path (ADMIN for /admin/*, STUDENT for /student/*)
- If unauthorized: Redirects to login.jsp with an error message
- If authorized: Allows the request to proceed using filterChain.doFilter()
- Includes exception handling for null sessions

All code should follow best practices with proper exception handling and logging.
```

### Deliverables:
- ✅ `UserDAO.java` in `src/main/java/com/mitwpu/lca/dao/`
- ✅ `LoginServlet.java` in `src/main/java/com/mitwpu/lca/controller/`
- ✅ `AuthFilter.java` in `src/main/java/com/mitwpu/lca/filter/`

### Testing:
```bash
# 1. Add servlet annotation to LoginServlet: @WebServlet("/LoginServlet")
# 2. Add filter annotation to AuthFilter: @WebFilter(urlPatterns = {"/admin/*", "/student/*"})
# 3. Test with Postman:
#    POST http://localhost:8080/InternshipExamSystem/LoginServlet
#    Body: email=student@student.com&password=student123
# 4. Verify session is created and user is redirected to dashboard
```

---

## PROMPT 3: Admin Company & Internship Module (Dev 2)

**Duration**: 4-5 days | **Complexity**: Medium

Depends on: PROMPT 1, PROMPT 2

Copy and paste this prompt to your LLM:

```
Continuing the project. I have Database setup, Core Models, and Authentication system in place.

Task 1: Create `CompanyDAO.java` in com.mitwpu.lca.dao with CRUD methods using JDBC:
- createCompany(Company company): Inserts a new company and returns generated companyId
- getCompanyById(int companyId): Returns Company object
- getAllCompanies(): Returns List of all companies filtered by status = 'ACTIVE'
- updateCompany(Company company): Updates company details
- deleteCompany(int companyId): Soft delete by setting status = 'INACTIVE'
- searchCompanies(String keyword): Searches by company name or industry
- Include proper exception handling and resource management

Task 2: Create `InternshipDAO.java` in com.mitwpu.lca.dao with methods:
- createInternship(Internship internship): Inserts a new internship posting
- getInternshipById(int internshipId): Returns Internship object
- getAllInternships(): Returns List of all OPEN internships
- getInternshipsByCompany(int companyId): Returns internships posted by a specific company
- updateInternship(Internship internship): Updates internship details
- updateInternshipStatus(int internshipId, String status): Updates status (OPEN/CLOSED/FILLED)
- getExpiredInternships(): Returns internships where deadline has passed
- Include pagination support (limit, offset parameters)

Task 3: Create `AdminInternshipServlet.java` in com.mitwpu.lca.controller to handle internship posting:
- Should be accessible only to ADMIN role (Auth filter will enforce this)
- Handles both GET (show form) and POST (process form) requests
- Extracts parameters: companyId, jobTitle, jobDescription, jobLocation, stipendAmount, 
  durationMonths, startDate, applicationDeadline, minimumCgpa, totalPositions
- Uses InternshipDAO.createInternship() to save to database
- Redirects to admin dashboard with success message on successful creation
- Handles validation (deadline must be future date, positions must be > 0, etc.)
- Logs the action to audit_logs table

Task 4: Write the HTML/JSP structure for `admin_dashboard.jsp` in src/main/webapp/admin/:
- Display a list of all posted internships with details (title, company, deadline, positions, status)
- Include a form to post new internship with all necessary fields
- Include filters to view internships by company, status, or deadline
- Add Edit/Delete buttons for existing internships
- Add styling for a clean, professional look
- Include logout functionality

Focus on backend logic first; UI can be enhanced later.
```

### Deliverables:
- ✅ `CompanyDAO.java` in `src/main/java/com/mitwpu/lca/dao/`
- ✅ `InternshipDAO.java` in `src/main/java/com/mitwpu/lca/dao/`
- ✅ `AdminInternshipServlet.java` in `src/main/java/com/mitwpu/lca/controller/`
- ✅ `admin_dashboard.jsp` in `src/main/webapp/admin/`

### Testing:
```bash
# 1. Create a test company first
# 2. Use AdminInternshipServlet to post an internship
# 3. Verify data appears in admin_dashboard.jsp
# 4. Test validation: Try invalid dates, negative positions, etc.
```

---

## PROMPT 4: Student Application Module with Transactions (Dev 3)

**Duration**: 4-5 days | **Complexity**: High (Transaction Management)

Depends on: PROMPT 1, PROMPT 2, PROMPT 3

Copy and paste this prompt to your LLM:

```
Continuing the project. Database, Authentication, and Admin modules are complete.

Task 1: Create `ApplicationDAO.java` and `ApplicationLogDAO.java` in com.mitwpu.lca.dao:

For ApplicationDAO:
- createApplication(int studentId, int internshipId): Inserts an application record
- getApplicationsByStudent(int studentId): Returns all applications by a student
- getApplicationsByInternship(int internshipId): Returns all applications for an internship
- getApplicationStatus(int applicationId): Returns current status
- updateApplicationStatus(int applicationId, String status): Updates status 
  (PENDING/SHORTLISTED/REJECTED/ACCEPTED/WITHDRAWN)
- checkDuplicateApplication(int studentId, int internshipId): Returns true if already applied
- getAllPendingApplications(): For admin to review

For ApplicationLogDAO:
- createApplicationLog(int applicationId, String action, String previousStatus, 
  String newStatus, String notes): Inserts audit log entry
- getApplicationHistory(int applicationId): Returns all logs for an application

Task 2: Create `ApplyInternshipServlet.java` in com.mitwpu.lca.controller. **THIS IS CRITICAL - 
USE JDBC TRANSACTION MANAGEMENT**:
- Accessible only to STUDENT role
- Receives parameters: studentId, internshipId
- CRITICAL: When a student applies, use JDBC Transaction Management:
  * conn.setAutoCommit(false) at the start
  * Insert record into applications table using ApplicationDAO
  * Immediately insert a log entry into application_logs table using ApplicationLogDAO
  * conn.commit() only if both inserts succeed
  * conn.rollback() if ANY exception occurs
  * This ensures data consistency: an application cannot exist without its audit log
- Include eligibility constraints checking:
  * Get student's CGPA from students table
  * Get internship's minimum_cgpa requirement
  * Check: student CGPA >= internship.minimumCgpa
  * Check: current date is before application_deadline
  * Check: student hasn't already applied using checkDuplicateApplication()
  * If ineligible, return error message with reason
- After successful application: redirect to student dashboard with success message
- Log all actions to audit_logs table
- Handle exceptions and provide meaningful error messages

Task 3: Ensure the servlet validates all constraints before applying.

The transaction management is crucial for data integrity. Do not skip the rollback mechanism.
```

### Deliverables:
- ✅ `ApplicationDAO.java` in `src/main/java/com/mitwpu/lca/dao/`
- ✅ `ApplicationLogDAO.java` in `src/main/java/com/mitwpu/lca/dao/`
- ✅ `ApplyInternshipServlet.java` in `src/main/java/com/mitwpu/lca/controller/`

### Testing:
```bash
# 1. Test with eligible student (CGPA meets requirement, before deadline)
# 2. Test with ineligible student (CGPA too low)
# 3. Test with deadline passed
# 4. Test duplicate application (should be rejected)
# 5. **Simulate transaction failure**: Disable application_logs insert to verify rollback
# 6. Verify data consistency: Every application has at least one log entry
```

---

## PROMPT 5: Online Examination Engine & AJAX (Dev 4)

**Duration**: 5-6 days | **Complexity**: High (JavaScript/AJAX + Complex SQL)

Depends on: PROMPT 1, PROMPT 2

Copy and paste this prompt to your LLM:

```
Continuing the project. Core database, authentication, and business logic are in place.

Task 1: Create `ExamDAO.java` and `QuestionDAO.java` in com.mitwpu.lca.dao:

For ExamDAO:
- createExam(Exam exam): Creates a new exam (Admin only)
- getExamById(int examId): Returns complete exam details
- getAllExams(): Returns all SCHEDULED/ONGOING exams
- getExamsByStatus(String status): Returns exams filtered by status
- updateExamStatus(int examId, String status): Updates exam status
- getExamDuration(int examId): Returns duration in minutes

For QuestionDAO:
- getQuestionsByExam(int examId): Returns all questions for an exam
- getQuestionWithOptions(int questionId): Returns question with all options for MCQ
- getCorrectOption(int questionId): Returns the correct option_id for MCQ (ADMIN only)
- Include question difficulty levels and marks per question

Task 2: Create `ExamServlet.java` in com.mitwpu.lca.controller:
- Handles exam initialization when a student starts the test
- Receives examId as parameter
- Validates student eligibility (role = STUDENT)
- Creates a new row in exam_attempts table with:
  * student_id (from session)
  * exam_id
  * start_time = CURRENT_TIMESTAMP
  * status = 'STARTED'
  * ip_address = request.getRemoteAddr()
- Returns the attempt_id to the student
- Loads all questions and options for the exam
- Passes data to take_exam.jsp for rendering
- Include error handling for already attempted exams (prevent multiple submissions)

Task 3: Create JSP view `take_exam.jsp` in src/main/webapp/student/:
- Display exam title and instructions
- Show question counter (e.g., "Question 3 of 25")
- **MUST INCLUDE A JAVASCRIPT COUNTDOWN TIMER** based on exam duration from database
  * Calculate end_time = start_time + duration_minutes
  * Update timer every 1 second
  * Display remaining time in HH:MM:SS format
  * Show warning when time is less than 5 minutes
  * Auto-submit when time reaches 0:00
- Render questions based on type:
  * MCQ: Display all options as radio buttons, allow selection
  * SUBJECTIVE: Display textarea for answer
- Include question navigation buttons (Previous, Next, Jump to Question)
- Include a "Save Answer" button that triggers AJAX
- Display previously saved answers when navigating between questions

Task 4: Create `AutoSaveServlet.java` in com.mitwpu.lca.controller and provide AJAX code:
- Endpoint: /AutoSaveServlet (POST)
- Receives parameters: attemptId, questionId, selectedOptionId (for MCQ), 
  subjectiveAnswer (for subjective)
- Updates answers table with the response
- Returns JSON response: {"status": "success", "message": "Answer saved"}
- Include AJAX code (vanilla JavaScript, no jQuery) inside take_exam.jsp:
  * Trigger on option selection (MCQ)
  * Trigger on textarea blur or every 30 seconds (subjective)
  * Send asynchronous POST to AutoSaveServlet
  * Show visual feedback (e.g., "Saving...", "Saved ✓")
  * Handle network errors gracefully

Example AJAX snippet structure:
```javascript
function saveAnswer(attemptId, questionId, answer) {
    const data = new FormData();
    data.append('attemptId', attemptId);
    data.append('questionId', questionId);
    data.append('answer', answer);
    
    fetch('/InternshipExamSystem/AutoSaveServlet', {
        method: 'POST',
        body: data
    })
    .then(response => response.json())
    .then(json => {
        console.log('Answer saved:', json);
        // Update UI to show saved status
    })
    .catch(error => console.error('Error:', error));
}
```

All code should prevent cheating (no copy-paste, tab switching detection optional).
```

### Deliverables:
- ✅ POJO: `Exam.java` and `Question.java` in `src/main/java/com/mitwpu/lca/model/`
- ✅ POJO: `Answer.java` and `ExamAttempt.java` in `src/main/java/com/mitwpu/lca/model/`
- ✅ `ExamDAO.java` and `QuestionDAO.java` in `src/main/java/com/mitwpu/lca/dao/`
- ✅ `ExamServlet.java` in `src/main/java/com/mitwpu/lca/controller/`
- ✅ `AutoSaveServlet.java` in `src/main/java/com/mitwpu/lca/controller/`
- ✅ `take_exam.jsp` in `src/main/webapp/student/` (with countdown timer & AJAX)

### Testing:
```bash
# 1. Create an exam with questions using SQL or admin interface
# 2. Start exam as student, verify timer starts correctly
# 3. Select MCQ option, verify auto-save triggers
# 4. Type subjective answer, verify auto-save on blur
# 5. Navigate between questions, verify answers are preserved
# 6. Test timer countdown, especially warnings at <5 min
# 7. Let timer expire, verify auto-submission
```

---

## PROMPT 6: Submission & Evaluation Engine (Dev 4)

**Duration**: 3-4 days | **Complexity**: High (Scoring Logic)

Depends on: All previous prompts, especially PROMPT 5

Copy and paste this prompt to your LLM:

```
Final module. The online exam system is functional. Now implement exam submission and auto-evaluation.

Task 1: Create `ExamSubmissionServlet.java` in com.mitwpu.lca.controller:
- Endpoint: /ExamSubmissionServlet (POST)
- Receives attemptId as parameter
- Validates the attempt exists and belongs to the logged-in student
- Calls evaluation engine (Task 2)
- Updates exam_attempts table:
  * submitted_at = CURRENT_TIMESTAMP
  * status = 'SUBMITTED'
  * total_marks_obtained = calculated score
- Returns JSON with final score, pass/fail status, and detailed results
- Include transaction management to ensure atomicity

Task 2: Create Evaluation Engine (can be in ExamSubmissionServlet or separate `EvaluationService.java`):
- Takes attemptId as input
- Iterates through all questions in the exam_attempts -> answers join
- For MCQ questions:
  * Get student's selected_option from answers table
  * Compare against options table where is_correct = TRUE
  * If match: award full marks for that question
  * If no match: award 0 marks
- For SUBJECTIVE questions:
  * Mark as is_marked = FALSE
  * Total marks for subjective = 0 (Admin will evaluate later)
- Calculate total score: SUM of all marks_obtained
- Determine pass/fail: total_score >= passing_marks (from exams table)
- Update answers table: marks_obtained and is_marked flags
- Update exam_attempts: status = 'EVALUATED', total_marks_obtained = calculated score
- Return evaluation report with:
  * studentName
  * examName
  * totalScore
  * passingScore
  * result (PASS/FAIL)
  * breakdownByQuestion (question_id, marks_obtained, marks_possible)

Task 3: Provide the SQL query to generate an 'Exam Rank List' report:
- Join users (student names)
- Join students (roll numbers, departments)
- Join exam_attempts (scores, submission dates)
- Group by student and exam
- Calculate rank based on total_marks_obtained in descending order
- Include fields: Rank, Student Name, Roll Number, Department, Exam Name, Score, Percentage, Result
- Order by rank

Example SQL structure:
```sql
SELECT 
    RANK() OVER (PARTITION BY ea.exam_id ORDER BY ea.total_marks_obtained DESC) as rank,
    u.full_name,
    s.roll_number,
    s.department_name,
    e.exam_title,
    ea.total_marks_obtained,
    (ea.total_marks_obtained / e.total_marks * 100) as percentage,
    CASE WHEN ea.total_marks_obtained >= e.passing_marks THEN 'PASS' ELSE 'FAIL' END as result
FROM exam_attempts ea
JOIN students s ON ea.student_id = s.student_id
JOIN users u ON s.user_id = u.user_id
JOIN exams e ON ea.exam_id = e.exam_id
WHERE ea.status = 'EVALUATED'
ORDER BY ea.exam_id, rank;
```

Ensure all updates use transaction management with rollback on failure.
```

### Deliverables:
- ✅ `ExamSubmissionServlet.java` in `src/main/java/com/mitwpu/lca/controller/`
- ✅ `EvaluationService.java` (or evaluation logic in servlet) in `src/main/java/com/mitwpu/lca/util/`
- ✅ SQL query for exam rank list report (include in database script)
- ✅ JSP page for displaying results: `exam_result.jsp` in `src/main/webapp/student/`

### Testing:
```bash
# 1. Complete an exam and submit
# 2. Verify evaluation calculates MCQ scores correctly
# 3. Verify subjective questions are marked as not evaluated
# 4. Generate exam rank list report and verify rankings
# 5. Test pass/fail logic with different passing marks
# 6. Verify transaction rollback on database error
# 7. Test report generation with multiple exams and students
```

---

## Integration Checklist

Before moving to Phase 5 (System Testing), verify:

- [ ] All DAO classes have proper exception handling
- [ ] All Servlets use @WebServlet or web.xml mappings
- [ ] AuthFilter is properly configured and working
- [ ] Transaction management is implemented in ApplyInternshipServlet and ExamSubmissionServlet
- [ ] AJAX auto-save is functioning without page refresh
- [ ] Timer countdown works correctly
- [ ] Session management prevents multiple logins
- [ ] Validation prevents invalid data entry
- [ ] All database queries are parameterized (prevent SQL injection)
- [ ] Error messages are user-friendly
- [ ] Audit logs record all important actions
- [ ] All JSP pages are responsive and functional

---

## Common Mistakes to Avoid

1. **Forgetting to close DB resources**: Always use try-catch-finally or try-with-resources
2. **Skipping transaction rollback**: Always implement rollback for multi-step operations
3. **Not validating input**: Validate all parameters on server-side
4. **Hardcoding database credentials**: Use properties file
5. **Storing passwords in plain text**: Use proper hashing (bcrypt, scrypt)
6. **Missing null checks**: Check for null values before using objects
7. **Not implementing proper error handling**: Catch and log exceptions appropriately
8. **UI tested before backend**: Implement backend first, test with Postman, then build UI

---

## Tools & Commands

### Build Project
```bash
mvn clean install
```

### Run Tests
```bash
mvn test
```

### Package for Deployment
```bash
mvn clean package
```

### Deploy to Tomcat
1. Copy `target/InternshipExamSystem-1.0.0.war` to `CATALINA_HOME/webapps/`
2. Start Tomcat: `catalina.sh run` (Linux) or `catalina.bat run` (Windows)
3. Access: `http://localhost:8080/InternshipExamSystem/`

### Database Setup
```bash
mysql -u root -p < DATABASE_SCHEMA.sql
```

---

## Questions or Issues?

- Review the README.md for project overview
- Check GIT_WORKFLOW.md for version control guidance
- Refer to database schema in DATABASE_SCHEMA.sql for table structure
- Use this document to stay aligned on implementation details

Good luck with development! 🚀
