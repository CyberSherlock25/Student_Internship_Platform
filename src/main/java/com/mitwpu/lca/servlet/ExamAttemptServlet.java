package com.mitwpu.lca.servlet;

import com.mitwpu.lca.dao.*;
import com.mitwpu.lca.model.*;
import com.mitwpu.lca.util.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

/**
 * Servlet for managing exam attempts.
 * Handles: start exam, submit exam, auto-evaluation.
 */
@WebServlet("/attempt")
public class ExamAttemptServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ExamAttemptDAO attemptDAO;
    private ExamDAO examDAO;
    private QuestionDAO questionDAO;
    private OptionDAO optionDAO;
    private AnswerDAO answerDAO;
    private AuditLogDAO auditLogDAO;
    private StudentDAO studentDAO;

    @Override
    public void init() throws ServletException {
        attemptDAO = new ExamAttemptDAO();
        examDAO = new ExamDAO();
        questionDAO = new QuestionDAO();
        optionDAO = new OptionDAO();
        answerDAO = new AnswerDAO();
        auditLogDAO = new AuditLogDAO();
        studentDAO = new StudentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        String contextPath = request.getContextPath();

        if (user == null || !user.isStudent()) {
            response.sendRedirect(contextPath + "/login.jsp");
            return;
        }

        Student student = studentDAO.getStudentByUserId(user.getUserId());
        if (student == null) {
            response.sendRedirect(contextPath + "/student/profile.jsp?error=Complete+your+profile+first");
            return;
        }

        if ("start".equals(action)) {
            int examId = Integer.parseInt(request.getParameter("examId"));
            Exam exam = examDAO.getExamById(examId);

            if (exam == null) {
                response.sendRedirect(contextPath + "/student/exams.jsp?error=Exam+not+found");
                return;
            }

            // Check if already attempted
            ExamAttempt existing = attemptDAO.getAttemptByStudentAndExam(student.getStudentId(), examId);
            if (existing != null && existing.isSubmitted()) {
                response.sendRedirect(contextPath + "/student/exams.jsp?error=You+have+already+taken+this+exam");
                return;
            }

            // Create new attempt or reuse existing
            ExamAttempt attempt;
            if (existing != null) {
                attempt = existing;
                attemptDAO.updateAttemptStatus(attempt.getAttemptId(), "IN_PROGRESS");
            } else {
                attempt = new ExamAttempt(student.getStudentId(), examId, getClientIp(request));
                attemptDAO.startAttempt(attempt);
            }

            // Load questions with options
            List<Question> questions = questionDAO.getQuestionsByExam(examId);
            for (Question q : questions) {
                if (q.isMCQ()) {
                    q.setOptions(optionDAO.getOptionsByQuestion(q.getQuestionId()));
                }
            }

            // Load saved answers
            List<Answer> savedAnswers = answerDAO.getAnswersByAttempt(attempt.getAttemptId());

            // Log exam start
            auditLogDAO.logEvent(new AuditLog(user.getUserId(), "EXAM_STARTED", "EXAM", examId,
                    "Student started exam: " + exam.getExamTitle(), getClientIp(request)));

            request.setAttribute("exam", exam);
            request.setAttribute("questions", questions);
            request.setAttribute("attempt", attempt);
            request.setAttribute("savedAnswers", savedAnswers);
            request.getRequestDispatcher("/student/take-exam.jsp").forward(request, response);
        }
        else if ("result".equals(action)) {
            int attemptId = Integer.parseInt(request.getParameter("attemptId"));
            ExamAttempt attempt = attemptDAO.getAttemptById(attemptId);
            List<Answer> answers = answerDAO.getAnswersByAttempt(attemptId);
            Exam exam = examDAO.getExamById(attempt.getExamId());

            request.setAttribute("attempt", attempt);
            request.setAttribute("answers", answers);
            request.setAttribute("exam", exam);
            request.getRequestDispatcher("/student/exam-result.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        String contextPath = request.getContextPath();

        if (user == null || !user.isStudent()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if ("submit".equals(action)) {
            int attemptId = Integer.parseInt(request.getParameter("attemptId"));
            int examId = Integer.parseInt(request.getParameter("examId"));

            // Mark unattempted questions with 0 marks
            answerDAO.markUnattemptedQuestions(attemptId, examId);

            // Auto-evaluate MCQ answers
            answerDAO.evaluateMCQAnswers(attemptId);

            // Calculate total marks
            int totalMarks = answerDAO.calculateTotalMarks(attemptId);

            // Submit attempt
            attemptDAO.submitAttempt(attemptId, totalMarks);

            // Log submission
            auditLogDAO.logEvent(new AuditLog(user.getUserId(), "EXAM_SUBMITTED", "EXAM", examId,
                    "Exam submitted. Marks obtained: " + totalMarks, getClientIp(request)));

            response.sendRedirect(contextPath + "/attempt?action=result&attemptId=" + attemptId);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) ip = request.getRemoteAddr();
        return ip;
    }
}

