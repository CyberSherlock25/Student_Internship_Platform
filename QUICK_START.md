# 📋 Quick Start Guide - Project Setup Summary

Welcome to the **Integrated Internship & Online Examination Management System** development team! This document provides a quick overview of what's been set up and how to get started.

---

## What's Been Set Up

Your project is now ready for team development with:

✅ **Maven Project Structure** - Professional Java web project layout  
✅ **Database Schema** - Complete SQL schema with all required tables and sample data  
✅ **Core POJOs** - User, Student, Company, Internship model classes  
✅ **DB Connection Utility** - JDBC connection management with resource cleanup  
✅ **Web Configuration** - web.xml with servlet and filter mappings  
✅ **Starter JSP Pages** - Login, Homepage, Error pages with styling  
✅ **Git Setup Guide** - Complete version control workflow for 4-person team  
✅ **Modular Development Prompts** - 6 organized prompts for LLM assistance  

---

## Project Directory Structure

```
InternshipExamSystem/
├── src/main/java/com/mitwpu/lca/
│   ├── model/              <- POJO Classes (User, Student, Company, Internship)
│   ├── dao/                <- Database Access Objects (UserDAO, CompanyDAO, etc.)
│   ├── controller/         <- Servlets (LoginServlet, AdminInternshipServlet, etc.)
│   ├── util/               <- Utilities (DBConnection, Constants, etc.)
│   └── filter/             <- Filters (AuthFilter for role-based access)
├── src/main/webapp/
│   ├── WEB-INF/web.xml    <- Servlet & Filter Configuration
│   ├── admin/              <- Admin pages (admin_dashboard.jsp, etc.)
│   ├── student/            <- Student pages (take_exam.jsp, etc.)
│   ├── css/                <- Stylesheets
│   ├── js/                 <- JavaScript files
│   ├── images/             <- Image assets
│   ├── index.jsp           <- Home page
│   ├── login.jsp           <- Login page
│   └── error.jsp           <- Error page
├── pom.xml                 <- Maven configuration with dependencies
├── README.md               <- Detailed project documentation
├── DATABASE_SCHEMA.sql     <- Complete database schema with sample data
├── GIT_WORKFLOW.md         <- Git commands and team workflow
├── DEVELOPMENT_PROMPTS.md  <- LLM prompts for each development phase
└── .gitignore              <- Git ignore rules
```

---

## Prerequisites Before Starting

### System Requirements
- **Java Development Kit (JDK)**: Version 11 or higher
- **Maven**: Version 3.8.1 or higher
- **MySQL Server**: Version 8.0 or higher
- **Apache Tomcat**: Version 10.0 or higher (for running the app)
- **Git**: Latest version
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code with Java extensions

### Check Installations
```bash
java -version              # Should show Java 11+
mvn -v                     # Should show Maven 3.8+
mysql --version            # Should show MySQL 8.0+
git --version              # Should show latest Git version
```

---

## 🚀 Getting Started (First Time Setup)

### Step 1: Clone the Repository
```bash
git clone https://github.com/your-org/InternshipExamSystem.git
cd InternshipExamSystem
```

### Step 2: Create Database
```bash
# Start MySQL
mysql -u root -p

# Execute in MySQL prompt
CREATE DATABASE internship_exam_system;
USE internship_exam_system;
source DATABASE_SCHEMA.sql;    # Load all tables and sample data
```

### Step 3: Configure Database Connection
Create `src/main/resources/database.properties`:
```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/internship_exam_system
db.username=root
db.password=your_mysql_password
```

### Step 4: Build Project
```bash
mvn clean install
```

### Step 5: Deploy to Tomcat
```bash
# Copy WAR file to Tomcat
cp target/InternshipExamSystem-1.0.0.war /path/to/tomcat/webapps/

# Start Tomcat
cd /path/to/tomcat/bin
./catalina.sh run                 # Linux/Mac
catalina.bat run                  # Windows
```

### Step 6: Access Application
Open browser and navigate to:
```
http://localhost:8080/InternshipExamSystem/
```

**Sample Login Credentials:**
- **Admin**: email: `admin@mitwpu.edu.in` | password: `admin123`
- **Student**: email: `student@student.com` | password: `student123`

---

## 👥 Team Assignments

### Developer 1: Database & Authentication (Dev1_name)
**Duration**: 1-2 weeks  
**Prompts**: 1, 2

1. **PROMPT 1** (3-4 days): Database Setup & Core Models
   - Create `DBConnection.java` utility
   - Create POJO classes: User, Student, Company, Internship

2. **PROMPT 2** (3-4 days): Authentication & Security Filter
   - Create `UserDAO.java`
   - Create `LoginServlet.java`
   - Create `AuthFilter.java`

**Deliverables**: 5 Java classes + working login system

---

### Developer 2: Admin & Company Module (Dev2_name)
**Duration**: 1 week  
**Prompt**: 3

**PROMPT 3**: Admin Company & Internship Module
- Create `CompanyDAO.java` and `InternshipDAO.java`
- Create `AdminInternshipServlet.java`
- Create `admin_dashboard.jsp`

**Deliverables**: 3 Java classes + 1 JSP page with admin UI

---

### Developer 3: Student Application Module (Dev3_name)
**Duration**: 1 week  
**Prompt**: 4

**PROMPT 4**: Student Application Module with Transactions
- Create `ApplicationDAO.java` and `ApplicationLogDAO.java`
- Create `ApplyInternshipServlet.java` with transaction management
- Create `student_dashboard.jsp` for browsing internships

**Deliverables**: 3 Java classes + 1 JSP page + transaction-based application logic

---

### Developer 4: Exam Engine (Dev4_name)
**Duration**: 2 weeks  
**Prompts**: 5, 6

1. **PROMPT 5** (5-6 days): Online Examination Engine & AJAX
   - Create `ExamDAO.java` and `QuestionDAO.java`
   - Create `ExamServlet.java` and `AutoSaveServlet.java`
   - Create `take_exam.jsp` with countdown timer and AJAX auto-save

2. **PROMPT 6** (3-4 days): Submission & Evaluation Engine
   - Create `ExamSubmissionServlet.java`
   - Create `EvaluationService.java`
   - Create exam rank list report query

**Deliverables**: 6 Java classes + 2 JSP pages + ranking report

---

## 📝 Development Workflow

### Each Developer Should Follow

1. **Create Feature Branch**
   ```bash
   git checkout dev
   git pull origin dev
   git checkout -b feature/<your-module>
   ```

2. **Work on Your Assignment**
   - Use the corresponding LLM prompt
   - Test locally with Maven
   - Commit frequently with clear messages

3. **Push Changes**
   ```bash
   git add .
   git commit -m "Descriptive commit message"
   git push origin feature/<your-module>
   ```

4. **Create Pull Request**
   - Go to GitHub repository
   - Create PR from your feature branch to `dev`
   - Request review from team members
   - Address feedback and merge

5. **Keep Updated**
   ```bash
   git checkout dev
   git pull origin dev
   git checkout feature/<your-module>
   git merge dev    # Stay updated with latest changes
   ```

---

## 🧪 Testing Your Code

### Local Testing
```bash
# Clean build
mvn clean compile

# Run tests
mvn test

# Package for deployment
mvn package
```

### Using Postman (for Servlets/APIs)
1. Download Postman: https://www.postman.com/downloads/
2. Create requests for each Servlet endpoint
3. Test with different parameters
4. Verify response codes and data

### Manual Testing in Browser
1. Login with sample credentials
2. Navigate through pages
3. Test form submissions
4. Verify database updates

---

## 🔍 Useful SQL Queries for Testing

```sql
-- Check all users
SELECT * FROM users;

-- Check applications submitted
SELECT * FROM applications;

-- Check exam attempts
SELECT * FROM exam_attempts;

-- Check audit logs
SELECT * FROM audit_logs ORDER BY logged_at DESC;

-- Student's CGPA (for internship eligibility)
SELECT s.roll_number, s.cgpa, i.job_title, i.minimum_cgpa
FROM students s
JOIN applications a ON s.student_id = a.student_id
JOIN internships i ON a.internship_id = i.internship_id;
```

---

## 🐛 Troubleshooting

### Maven Issues
```bash
# Clean Maven cache
mvn clean
rm -rf ~/.m2/repository

# Rebuild
mvn install
```

### Database Connection Errors
```
Error: Can't connect to MySQL server

Solution:
1. Verify MySQL is running
2. Check database.properties credentials
3. Ensure database exists: USE internship_exam_system;
4. Verify mysql-connector-java is in Maven dependencies
```

### Servlet Not Found (404)
```
Error: Requested resource not found

Solution:
1. Check @WebServlet annotations
2. Verify web.xml filter mappings
3. Clear Tomcat cache: CATALINA_HOME/work/
4. Rebuild WAR file: mvn clean package
```

### Session/Authentication Issues
```
Solution:
1. Check browser cookies are enabled
2. Verify AuthFilter is configured
3. Check session timeout settings
4. Clear browser cache and cookies
```

---

## 📚 Important Documentation Files

| File | Purpose |
|------|---------|
| `README.md` | Complete project documentation |
| `DATABASE_SCHEMA.sql` | Database structure and sample data |
| `GIT_WORKFLOW.md` | Git commands and team collaboration guide |
| `DEVELOPMENT_PROMPTS.md` | **Start here!** - 6 modular LLM prompts |
| `pom.xml` | Maven dependencies and build configuration |
| `src/main/webapp/WEB-INF/web.xml` | Servlet and filter mappings |

---

## 💡 Pro Tips

1. **Code First, UI Later**: Implement backend logic thoroughly, then build UI
2. **Test with Postman**: Don't wait for JSP pages, test APIs immediately
3. **Commit Frequently**: Small commits are easier to review and revert
4. **Keep It DRY**: Don't repeat code, create utility methods
5. **Security First**: Always validate input, use prepared statements, hash passwords
6. **Documentation**: Add JavaDoc comments to your code
7. **Error Handling**: Catch and log all exceptions

---

## 📞 Support Resources

- **Maven Docs**: https://maven.apache.org/
- **JDBC Tutorial**: https://docs.oracle.com/javase/tutorial/jdbc/
- **Servlet & JSP**: https://jakarta.ee/specifications/servlet/
- **MySQL Docs**: https://dev.mysql.com/doc/

---

## 🎯 Key Milestones

- **Week 1**: Dev 1 completes authentication
- **Week 2**: Dev 2 & 3 implement business logic
- **Week 2-3**: Dev 4 implements exam engine
- **Week 3**: Integration testing across all modules
- **Week 4**: Final refinements and deployment

---

## Ready to Start? 

1. ✅ Read `DEVELOPMENT_PROMPTS.md` for your assigned prompts
2. ✅ Review `GIT_WORKFLOW.md` for version control instructions
3. ✅ Create your feature branch
4. ✅ Use the LLM prompts to generate your code
5. ✅ Test locally with Maven and Postman
6. ✅ Create pull requests for code review

**Let's build this together! 🚀**

---

**Last Updated**: April 20, 2026  
**Project Version**: 1.0.0  
**Team**: 4 Developers  
**Status**: Ready for Development
