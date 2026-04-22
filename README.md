# Integrated Internship &amp; Online Examination Management System

A comprehensive web application for managing internships, applications, and online examinations for academic institutions.

## Project Overview

This system enables:
- **Companies**: Post internship openings
- **Students**: Browse and apply for internships based on eligibility (CGPA criteria)
- **Admins**: Manage companies, applications, and generate reports
- **Online Exams**: Conduct secure online examinations with auto-save and auto-submission

## Technology Stack

- **Backend**: Java 11, Jakarta Servlets, JSP
- **Database**: MySQL 8.0
- **Build Tool**: Maven 3.8+
- **Frontend**: HTML5, CSS3, Vanilla JavaScript

## Project Structure

```
InternshipExamSystem/
├── src/main/java/
│   └── com/mitwpu/lca/
│       ├── model/           (POJO Classes)
│       ├── dao/             (Data Access Objects)
│       ├── controller/      (Servlets)
│       ├── util/            (Utilities & DB Connection)
│       └── filter/          (Authentication Filters)
├── src/main/webapp/
│   ├── WEB-INF/
│   ├── admin/               (Admin Pages)
│   ├── student/             (Student Pages)
│   ├── css/
│   ├── js/
│   └── images/
└── pom.xml
```

## Team Assignments

| Developer | Module | Responsibilities |
|-----------|--------|------------------|
| Dev 1 | Database &amp; Auth | MySQL setup, JDBC connection, User POJOs, Login, Authentication Filter |
| Dev 2 | Admin &amp; Company | Company management, internship posting, application processing, reports |
| Dev 3 | Student Module | Student registration, internship browsing, application transactions |
| Dev 4 | Exam Engine | Online exams, timer, MCQ/Subjective questions, AJAX auto-save, evaluation |

## Git Workflow

### Initial Setup
```bash
git init
git add .
git commit -m "Initial project structure"
git branch dev
git push -u origin dev
```

### Per Developer
```bash
# Create feature branch off dev
git checkout dev
git pull origin dev
git checkout -b feature/<module-name>

# Work on feature
git add .
git commit -m "description of changes"
git push origin feature/<module-name>

# Create Pull Request on GitHub to merge into dev
# After review and approval, merge to dev
# Then later, merge dev into main for production
```

### Branch Strategy
- **main**: Production-ready code only
- **dev**: Integration branch for team
- **feature/***: Individual developer branches

## Prerequisites

1. **Java Development Kit (JDK)**: Version 11 or higher
2. **Maven**: Version 3.8.1 or higher
3. **MySQL Server**: Version 8.0 or higher
4. **Apache Tomcat**: Version 10.0 or higher (for WAR deployment)
5. **Git**: For version control

## Setup Instructions

### 1. Database Setup

Execute the SQL script to create the database schema:

```sql
CREATE DATABASE internship_exam_system;
USE internship_exam_system;

-- Tables will be created based on the modular development phase
```

### 2. Database Connection Configuration

Create `src/main/resources/database.properties`:

```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/internship_exam_system
db.username=root
db.password=your_password
```

### 3. Build the Project

```bash
cd InternshipExamSystem
mvn clean install
```

### 4. Deploy on Tomcat

- Copy `target/InternshipExamSystem-1.0.0.war` to `CATALINA_HOME/webapps/`
- Restart Tomcat
- Access at: `http://localhost:8080/InternshipExamSystem/`

## Development Phases

### Phase 1: Database &amp; Authentication (Dev 1)
- Setup `DBConnection.java` utility
- Create User, Student, Company, Internship POJOs
- Implement `UserDAO` with authentication logic
- Create `LoginServlet` and `AuthFilter`

### Phase 2: Admin Module (Dev 2)
- Implement `CompanyDAO` and `InternshipDAO`
- Create `AdminInternshipServlet` for posting internships
- Build Admin Dashboard JSP pages

### Phase 3: Student Module (Dev 3)
- Implement `StudentDAO` and `ApplicationDAO`
- Create `ApplyInternshipServlet` with transaction management
- Build Student Pages for browsing and applying

### Phase 4: Exam Engine (Dev 4)
- Implement `ExamDAO` and `QuestionDAO`
- Create `ExamServlet` and `ExamSubmissionServlet`
- Build Online Exam UI with JavaScript timer
- Implement `AutoSaveServlet` for AJAX functionality

## API Endpoints

(To be documented after implementation)

## Testing

### Unit Tests
```bash
mvn test
```

### Integration Testing
Use Postman or Insomnia for API testing. HTML forms in JSP pages for manual testing.

## Logging &amp; Monitoring

- Log files: Check Tomcat logs in `CATALINA_HOME/logs/`
- Application logging: To be configured with SLF4J (optional enhancement)

## Troubleshooting

### Common Issues

1. **MySQL Connection Error**
   - Verify MySQL is running
   - Check database.properties credentials
   - Ensure MySQL JDBC driver is in classpath

2. **Servlet Not Found (404)**
   - Check web.xml mappings
   - Verify servlet annotation or filter configuration
   - Clear Tomcat cache: `CATALINA_HOME/work/`

3. **Session Issues**
   - Ensure cookies are enabled in browser
   - Check session timeout in web.xml (currently 30 minutes)
   - Verify `AuthFilter` is properly configured

## Performance Optimization

- Use connection pooling (HikariCP) for production
- Implement caching for frequently accessed data
- Optimize database queries with proper indexing
- Use AJAX for auto-save to reduce page reloads

## Security Considerations

- Use prepared statements (JDBC) to prevent SQL injection
- Implement HTTPS for production
- Hash passwords using bcrypt or similar
- Validate all user inputs server-side
- Implement CSRF tokens for form submissions
- Use secure HTTP-only cookies for sessions

## Future Enhancements

- Email notifications for applications and exam results
- Real-time dashboard analytics
- Automated bulk email for shortlisting
- PDF report generation
- Mobile application support
- Two-factor authentication

## Support &amp; Contribution

For issues, questions, or contributions:
1. Open an issue on GitHub
2. Create a pull request with detailed description
3. Follow the commit message conventions

## License

[To be specified by institution]

## Author

Internship &amp; Examination Management System Team
MIT World Peace University (MITWPU)
