package com.mitwpu.lca.servlet;

import com.mitwpu.lca.dao.AuditLogDAO;
import com.mitwpu.lca.dao.SessionTrackingDAO;
import com.mitwpu.lca.model.AuditLog;
import com.mitwpu.lca.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Enhanced Logout Servlet with session tracking cleanup.
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private SessionTrackingDAO sessionTrackingDAO;
    private AuditLogDAO auditLogDAO;

    @Override
    public void init() throws ServletException {
        sessionTrackingDAO = new SessionTrackingDAO();
        auditLogDAO = new AuditLogDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String contextPath = request.getContextPath();

        if (session != null) {
            User user = (User) session.getAttribute("user");
            String sessionId = session.getId();

            // Invalidate session in database
            sessionTrackingDAO.invalidateSession(sessionId);

            // Audit log
            if (user != null) {
                auditLogDAO.logEvent(new AuditLog(user.getUserId(), "LOGOUT",
                        "USER", user.getUserId(), "User logged out", request.getRemoteAddr()));
            }

            // Invalidate HTTP session
            session.invalidate();
        }

        response.sendRedirect(contextPath + "/login.jsp?msg=Logged+out+successfully");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}

