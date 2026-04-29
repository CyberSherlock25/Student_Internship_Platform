# Implementation TODO Tracker

## Phase 1: Database Schema Updates
- [x] Add `session_tracking` table to DATABASE_SCHEMA_UPDATES.sql

## Phase 2: Model Beans (JavaBeans)
- [x] Exam.java
- [x] Question.java
- [x] Option.java
- [x] ExamAttempt.java
- [x] Answer.java
- [x] AuditLog.java
- [x] SessionTracking.java

## Phase 3: DAO Classes
- [x] ExamDAO.java
- [x] QuestionDAO.java
- [x] OptionDAO.java
- [x] ExamAttemptDAO.java
- [x] AnswerDAO.java
- [x] AuditLogDAO.java
- [x] SessionTrackingDAO.java

## Phase 4: Servlets (Controllers)
- [x] ExamServlet.java (CRUD + schedule)
- [x] ExamAttemptServlet.java (start/submit exam)
- [x] AnswerServlet.java (auto-save + submit answers)
- [x] ReportServlet.java (generate reports)

## Phase 5: JSP Views
- [x] admin/exams.jsp
- [x] admin/reports.jsp
- [x] student/exam-result.jsp
- [x] student/take-exam.jsp

## Phase 6: Security & Anti-Cheating Integration
- [x] Update LoginServlet with session tracking + IP logging + single-session enforcement
- [x] Update LogoutServlet with session tracking cleanup
- [x] Integrate violation tracking into exam flow (take-exam.jsp)

## Phase 7: web.xml Updates
- [x] Add servlet mappings for new servlets

## Phase 8: Final Integration & Testing
- [ ] Verify end-to-end flow
- [ ] Build project with Maven

