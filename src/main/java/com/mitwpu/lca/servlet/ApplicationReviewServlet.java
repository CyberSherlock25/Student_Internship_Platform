package com.mitwpu.lca.servlet;

import com.mitwpu.lca.dao.ApplicationDAO;
import com.mitwpu.lca.model.Application;
import com.mitwpu.lca.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet for reviewing student internship applications (Admin only)
 * Handles: Update application status, provide feedback, set rating
 */
@WebServlet("/admin/application-review")
public class ApplicationReviewServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ApplicationReviewServlet.class.getName());

    private final ApplicationDAO applicationDAO = new ApplicationDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            if (!isAuthorizedAdmin(request.getSession(false))) {
                sendErrorResponse(response, "Unauthorized", 403);
                return;
            }

            String action = request.getParameter("action");
            if (action == null || action.trim().isEmpty()) {
                sendErrorResponse(response, "Invalid request", 400);
                return;
            }

            switch (action) {
                case "update-status":
                    handleUpdateStatus(request, response);
                    break;
                case "set-feedback":
                    handleSetFeedback(request, response);
                    break;
                default:
                    sendErrorResponse(response, "Invalid action", 400);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in POST request", e);
            sendErrorResponse(response, "Server error", 500);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            if (!isAuthorizedAdmin(request.getSession(false))) {
                sendErrorResponse(response, "Unauthorized", 403);
                return;
            }

            String action = request.getParameter("action");
            if ("get".equals(action)) {
                handleGetApplication(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/applications.jsp");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in GET request", e);
            sendErrorResponse(response, "Server error", 500);
        }
    }

    /**
     * Handle application status update
     */
    private void handleUpdateStatus(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int applicationId = parseIntParameter(request, "applicationId", -1);
            String newStatus = request.getParameter("status");

            if (applicationId <= 0 || newStatus == null || newStatus.trim().isEmpty()) {
                sendErrorResponse(response, "Invalid parameters", 400);
                return;
            }

            // Validate status
            if (!isValidStatus(newStatus)) {
                sendErrorResponse(response, "Invalid status value", 400);
                return;
            }

            Application application = applicationDAO.getApplicationById(applicationId);
            if (application == null) {
                sendErrorResponse(response, "Application not found", 404);
                return;
            }

            applicationDAO.updateApplicationStatus(applicationId, newStatus);

            LOGGER.log(Level.INFO, "Application status updated: " + applicationId + " -> " + newStatus);
            sendSuccessResponse(response, "Status updated successfully", 200);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating application status", e);
            sendErrorResponse(response, "Error: " + e.getMessage(), 500);
        }
    }

    /**
     * Handle feedback and rating submission
     */
    private void handleSetFeedback(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int applicationId = parseIntParameter(request, "applicationId", -1);
            String feedback = request.getParameter("feedback");
            String ratingStr = request.getParameter("rating");

            if (applicationId <= 0) {
                sendErrorResponse(response, "Invalid application ID", 400);
                return;
            }

            Application application = applicationDAO.getApplicationById(applicationId);
            if (application == null) {
                sendErrorResponse(response, "Application not found", 404);
                return;
            }

            // Update feedback
            if (feedback != null && !feedback.trim().isEmpty()) {
                application.setFeedback(feedback.trim());
            }

            // Update rating
            if (ratingStr != null && !ratingStr.trim().isEmpty()) {
                try {
                    double rating = Double.parseDouble(ratingStr.trim());
                    if (rating >= 0 && rating <= 5) {
                        application.setRating(rating);
                    } else {
                        sendErrorResponse(response, "Rating must be between 0 and 5", 400);
                        return;
                    }
                } catch (NumberFormatException e) {
                    sendErrorResponse(response, "Invalid rating format", 400);
                    return;
                }
            }

            applicationDAO.updateApplication(application);

            LOGGER.log(Level.INFO, "Application feedback updated: " + applicationId);
            sendSuccessResponse(response, "Feedback saved successfully", 200);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error setting feedback", e);
            sendErrorResponse(response, "Error: " + e.getMessage(), 500);
        }
    }

    /**
     * Handle application retrieval for editing
     */
    private void handleGetApplication(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int applicationId = parseIntParameter(request, "applicationId", -1);
            if (applicationId <= 0) {
                sendErrorResponse(response, "Invalid application ID", 400);
                return;
            }

            Application application = applicationDAO.getApplicationById(applicationId);
            if (application == null) {
                sendErrorResponse(response, "Application not found", 404);
                return;
            }

            response.setStatus(200);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(applicationToJson(application));

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving application", e);
            sendErrorResponse(response, "Error retrieving application", 500);
        }
    }

    /**
     * Check if user is authenticated admin
     */
    private boolean isAuthorizedAdmin(HttpSession session) {
        if (session == null) {
            return false;
        }
        User user = (User) session.getAttribute("user");
        return user != null && user.getRole().equals("ADMIN");
    }

    /**
     * Validate application status
     */
    private boolean isValidStatus(String status) {
        return status.equals("PENDING") || 
               status.equals("SHORTLISTED") || 
               status.equals("REJECTED") || 
               status.equals("ACCEPTED");
    }

    /**
     * Parse integer parameter safely
     */
    private int parseIntParameter(HttpServletRequest request, String paramName, int defaultValue) {
        try {
            String value = request.getParameter(paramName);
            if (value == null || value.trim().isEmpty()) {
                return defaultValue;
            }
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Convert application to JSON
     */
    private String applicationToJson(Application application) {
        return String.format(
            "{\"applicationId\": %d, \"studentId\": %d, \"internshipId\": %d, \"status\": \"%s\", " +
            "\"coverLetter\": \"%s\", \"rating\": %s, \"feedback\": \"%s\"}",
            application.getApplicationId(),
            application.getStudentId(),
            application.getInternshipId(),
            application.getStatus(),
            escapeJson(application.getCoverLetter() != null ? application.getCoverLetter() : ""),
            application.getRating() != null ? application.getRating() : "null",
            escapeJson(application.getFeedback() != null ? application.getFeedback() : "")
        );
    }

    /**
     * Escape special characters for JSON
     */
    private String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r");
    }

    /**
     * Send error response as JSON
     */
    private void sendErrorResponse(HttpServletResponse response, String message, int statusCode)
            throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format(
            "{\"success\": false, \"message\": \"%s\"}", 
            escapeJson(message)
        ));
    }

    /**
     * Send success response as JSON
     */
    private void sendSuccessResponse(HttpServletResponse response, String message, int statusCode)
            throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format(
            "{\"success\": true, \"message\": \"%s\"}", 
            escapeJson(message)
        ));
    }
}
