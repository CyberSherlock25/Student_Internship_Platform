package com.mitwpu.lca.servlet;

import com.mitwpu.lca.dao.AuditLogDAO;
import com.mitwpu.lca.dao.SessionTrackingDAO;
import com.mitwpu.lca.dao.UserDAO;
import com.mitwpu.lca.model.AuditLog;
import com.mitwpu.lca.model.SessionTracking;
import com.mitwpu.lca.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Locale;

/**
 * Enhanced Login Servlet with:
 * - Session tracking (IP, user agent)
 * - Single active session enforcement
 * - Audit logging
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;
    private SessionTrackingDAO sessionTrackingDAO;
    private AuditLogDAO auditLogDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        sessionTrackingDAO = new SessionTrackingDAO();
        auditLogDAO = new AuditLogDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String contextPath = request.getContextPath();
        String ipAddress = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        // Validation
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            response.sendRedirect(contextPath + "/login.jsp?error=Email+and+password+are+required");
            return;
        }

        email = email.trim().toLowerCase(Locale.ROOT);

        // Authenticate user
        User user = userDAO.authenticate(email, password);

        if (user != null) {
            // SINGLE SESSION ENFORCEMENT
            // Check if user already has an active session
            SessionTracking existingSession = sessionTrackingDAO.getActiveSessionByUser(user.getUserId());
            if (existingSession != null) {
                // Log multiple login attempt
                auditLogDAO.logEvent(new AuditLog(user.getUserId(), "MULTIPLE_LOGIN_ATTEMPT",
                        "USER", user.getUserId(),
                        "Blocked login - already active session from IP: " + existingSession.getIpAddress(),
                        ipAddress));
                response.sendRedirect(contextPath + "/login.jsp?error=You+already+have+an+active+session.+Please+logout+first.");
                return;
            }

            // Create new session
            HttpSession session = request.getSession(true);
            // Invalidate old session if exists to prevent fixation
            if (!session.isNew()) {
                session.invalidate();
                session = request.getSession(true);
            }

            session.setAttribute("user", user);
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("userEmail", user.getEmail());
            session.setAttribute("userRole", user.getRole());
            session.setAttribute("sessionId", session.getId());

            // Track session in database
            SessionTracking tracking = new SessionTracking(
                    user.getUserId(), session.getId(), ipAddress, userAgent);
            sessionTrackingDAO.createSession(tracking);

            // Audit log successful login
            auditLogDAO.logEvent(new AuditLog(user.getUserId(), "LOGIN_SUCCESS",
                    "USER", user.getUserId(), "User logged in", ipAddress));

            // Redirect based on role
            if (user.isAdmin()) {
                response.sendRedirect(contextPath + "/admin/dashboard.jsp");
            } else if (user.isStudent()) {
                response.sendRedirect(contextPath + "/student/dashboard.jsp");
            } else {
                response.sendRedirect(contextPath + "/index.jsp?error=Invalid+user+role");
            }
        } else {
            // Audit log failed login
            auditLogDAO.logEvent(new AuditLog(null, "LOGIN_FAILED",
                    "USER", null, "Failed login attempt for email: " + email, ipAddress));
            response.sendRedirect(contextPath + "/login.jsp?error=Invalid+email+or+password");
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // In case of multiple IPs, take the first one
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}

