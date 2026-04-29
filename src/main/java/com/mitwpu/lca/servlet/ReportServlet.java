package com.mitwpu.lca.servlet;

import com.mitwpu.lca.dao.*;

import com.mitwpu.lca.model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Servlet for generating various reports.
 * Admin-only access for analytics and monitoring.
 */
@WebServlet("/report")
public class ReportServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ExamAttemptDAO attemptDAO;
    private AuditLogDAO auditLogDAO;
    private ApplicationDAO applicationDAO;
    private CompanyDAO companyDAO;
    private ExamDAO examDAO;

    @Override
    public void init() throws ServletException {
        attemptDAO = new ExamAttemptDAO();
        auditLogDAO = new AuditLogDAO();
        applicationDAO = new ApplicationDAO();
        companyDAO = new CompanyDAO();
        examDAO = new ExamDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        String contextPath = request.getContextPath();

        if (user == null || !user.isAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin access required");
            return;
        }

        String type = request.getParameter("type");

        switch (type) {
            case "rankList":
                int examId = Integer.parseInt(request.getParameter("examId"));
                List<ExamAttempt> rankList = attemptDAO.getRankListByExam(examId);
                request.setAttribute("rankList", rankList);
                request.setAttribute("exam", examDAO.getExamById(examId));
                request.getRequestDispatcher("/admin/reports.jsp?sub=rankList").forward(request, response);
                break;

            case "violations":
                List<AuditLog> violations = auditLogDAO.getViolationLogs();
                request.setAttribute("violations", violations);
                request.getRequestDispatcher("/admin/reports.jsp?sub=violations").forward(request, response);
                break;

            case "examAttempts":
                List<Exam> exams = examDAO.getAllExams();
                request.setAttribute("exams", exams);
                request.getRequestDispatcher("/admin/reports.jsp?sub=examAttempts").forward(request, response);
                break;

            default:
                response.sendRedirect(contextPath + "/admin/dashboard.jsp");
        }
    }
}

