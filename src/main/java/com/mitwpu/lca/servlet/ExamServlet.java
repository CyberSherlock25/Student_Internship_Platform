package com.mitwpu.lca.servlet;

import com.mitwpu.lca.dao.ExamDAO;
import com.mitwpu.lca.model.Exam;
import com.mitwpu.lca.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Servlet for Exam CRUD operations.
 * Admin can create, update, delete exams.
 * Students can view available exams.
 */
@WebServlet("/exam")
public class ExamServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ExamDAO examDAO;

    @Override
    public void init() throws ServletException {
        examDAO = new ExamDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        String contextPath = request.getContextPath();

        if (user == null) {
            response.sendRedirect(contextPath + "/login.jsp");
            return;
        }

        // Admin views all exams
        if (user.isAdmin()) {
            if ("edit".equals(action)) {
                int examId = Integer.parseInt(request.getParameter("id"));
                Exam exam = examDAO.getExamById(examId);
                request.setAttribute("exam", exam);
                request.getRequestDispatcher("/admin/exams.jsp").forward(request, response);
                return;
            }
            List<Exam> exams = examDAO.getAllExams();
            request.setAttribute("exams", exams);
            request.getRequestDispatcher("/admin/exams.jsp").forward(request, response);
        }
        // Student views available exams
        else if (user.isStudent()) {
            List<Exam> exams = examDAO.getAvailableExamsForStudent();
            request.setAttribute("exams", exams);
            request.getRequestDispatcher("/student/exams.jsp").forward(request, response);
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

        if (user == null || !user.isAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin access required");
            return;
        }

        if ("create".equals(action)) {
            Exam exam = new Exam();
            exam.setExamTitle(request.getParameter("examTitle"));
            exam.setExamDescription(request.getParameter("examDescription"));
            exam.setTotalMarks(Integer.parseInt(request.getParameter("totalMarks")));
            exam.setDurationMinutes(Integer.parseInt(request.getParameter("durationMinutes")));
            exam.setPassingMarks(Integer.parseInt(request.getParameter("passingMarks")));
            exam.setExamDate(LocalDate.parse(request.getParameter("examDate")));
            exam.setExamStartTime(LocalTime.parse(request.getParameter("examStartTime")));
            exam.setExamEndTime(LocalTime.parse(request.getParameter("examEndTime")));
            exam.setStatus(request.getParameter("status"));
            exam.setCreatedBy(user.getUserId());

            boolean success = examDAO.createExam(exam);
            response.sendRedirect(contextPath + "/exam?msg=" + (success ? "Exam+created+successfully" : "Failed+to+create+exam"));
        }
        else if ("update".equals(action)) {
            int examId = Integer.parseInt(request.getParameter("examId"));
            Exam exam = examDAO.getExamById(examId);
            if (exam != null) {
                exam.setExamTitle(request.getParameter("examTitle"));
                exam.setExamDescription(request.getParameter("examDescription"));
                exam.setTotalMarks(Integer.parseInt(request.getParameter("totalMarks")));
                exam.setDurationMinutes(Integer.parseInt(request.getParameter("durationMinutes")));
                exam.setPassingMarks(Integer.parseInt(request.getParameter("passingMarks")));
                exam.setExamDate(LocalDate.parse(request.getParameter("examDate")));
                exam.setExamStartTime(LocalTime.parse(request.getParameter("examStartTime")));
                exam.setExamEndTime(LocalTime.parse(request.getParameter("examEndTime")));
                exam.setStatus(request.getParameter("status"));

                boolean success = examDAO.updateExam(exam);
                response.sendRedirect(contextPath + "/exam?msg=" + (success ? "Exam+updated+successfully" : "Failed+to+update+exam"));
            }
        }
        else if ("delete".equals(action)) {
            int examId = Integer.parseInt(request.getParameter("examId"));
            boolean success = examDAO.deleteExam(examId);
            response.sendRedirect(contextPath + "/exam?msg=" + (success ? "Exam+deleted+successfully" : "Failed+to+delete+exam"));
        }
        else if ("changeStatus".equals(action)) {
            int examId = Integer.parseInt(request.getParameter("examId"));
            String newStatus = request.getParameter("newStatus");
            boolean success = examDAO.updateExamStatus(examId, newStatus);
            response.sendRedirect(contextPath + "/exam?msg=" + (success ? "Status+updated" : "Failed+to+update+status"));
        }
    }
}

