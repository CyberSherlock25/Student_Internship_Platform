package com.mitwpu.lca.servlet;

import com.mitwpu.lca.dao.InternshipDAO;
import com.mitwpu.lca.model.Internship;
import com.mitwpu.lca.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet for managing internships (Admin only)
 * Handles: Create, read, update, delete, and list internships
 */
@WebServlet("/admin/internship")
public class InternshipManagementServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(InternshipManagementServlet.class.getName());

    private InternshipDAO internshipDAO = new InternshipDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            if (!isAuthorizedAdmin(request.getSession(false))) {
                sendErrorResponse(response, "Unauthorized", 403);
                return;
            }

            String action = request.getParameter("action") != null ? 
                request.getParameter("action") : "list";

            switch (action) {
                case "get":
                    handleGetInternship(request, response);
                    break;
                case "list":
                    response.sendRedirect(request.getContextPath() + "/admin/internships.jsp");
                    break;
                default:
                    sendErrorResponse(response, "Invalid action", 400);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in GET request", e);
            sendErrorResponse(response, "Server error", 500);
        }
    }

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
                case "create":
                    handleCreateInternship(request, response);
                    break;
                case "update":
                    handleUpdateInternship(request, response);
                    break;
                case "delete":
                    handleDeleteInternship(request, response);
                    break;
                default:
                    sendErrorResponse(response, "Invalid action", 400);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in POST request", e);
            sendErrorResponse(response, "Server error", 500);
        }
    }

    /**
     * Handle internship creation
     */
    private void handleCreateInternship(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int companyId = parseIntParameter(request, "companyId", -1);
            String jobTitle = request.getParameter("jobTitle");
            String jobDescription = request.getParameter("jobDescription");
            String jobLocation = request.getParameter("jobLocation");
            int durationMonths = parseIntParameter(request, "durationMonths", 0);
            double stipendAmount = parseDoubleParameter(request, "stipendAmount", 0);
            double minimumCgpa = parseDoubleParameter(request, "minimumCgpa", 0);
            String deadlineStr = request.getParameter("applicationDeadline");

            // Validation
            if (companyId <= 0) {
                sendErrorResponse(response, "Invalid company ID", 400);
                return;
            }
            if (jobTitle == null || jobTitle.trim().isEmpty()) {
                sendErrorResponse(response, "Job title is required", 400);
                return;
            }
            if (durationMonths <= 0) {
                sendErrorResponse(response, "Duration must be greater than 0", 400);
                return;
            }
            if (minimumCgpa < 0 || minimumCgpa > 10) {
                sendErrorResponse(response, "CGPA must be between 0 and 10", 400);
                return;
            }

            Internship internship = new Internship();
            internship.setCompanyId(companyId);
            internship.setJobTitle(jobTitle.trim());
            internship.setJobDescription(jobDescription != null ? jobDescription.trim() : "");
            internship.setJobLocation(jobLocation != null ? jobLocation.trim() : "");
            internship.setDurationMonths(durationMonths);
            internship.setStipendAmount(stipendAmount);
            internship.setMinimumCgpa(minimumCgpa);
            internship.setApplicationDeadline(deadlineStr != null && !deadlineStr.isEmpty() ? 
                LocalDate.parse(deadlineStr) : LocalDate.now().plusMonths(1));
            internship.setStatus("OPEN");
            internship.setFilledPositions(0);
            internship.setTotalPositions(parseIntParameter(request, "totalPositions", 1));

            internshipDAO.createInternship(internship);

            LOGGER.log(Level.INFO, "Internship created: " + jobTitle);
            sendSuccessResponse(response, "Internship created successfully", 201);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating internship", e);
            sendErrorResponse(response, "Error: " + e.getMessage(), 500);
        }
    }

    /**
     * Handle internship update
     */
    private void handleUpdateInternship(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int internshipId = parseIntParameter(request, "internshipId", -1);
            if (internshipId <= 0) {
                sendErrorResponse(response, "Invalid internship ID", 400);
                return;
            }

            Internship internship = internshipDAO.getInternshipById(internshipId);
            if (internship == null) {
                sendErrorResponse(response, "Internship not found", 404);
                return;
            }

            // Update fields
            String jobTitle = request.getParameter("jobTitle");
            if (jobTitle != null && !jobTitle.trim().isEmpty()) {
                internship.setJobTitle(jobTitle.trim());
            }

            String jobDescription = request.getParameter("jobDescription");
            if (jobDescription != null) {
                internship.setJobDescription(jobDescription.trim());
            }

            String jobLocation = request.getParameter("jobLocation");
            if (jobLocation != null) {
                internship.setJobLocation(jobLocation.trim());
            }

            String durationStr = request.getParameter("durationMonths");
            if (durationStr != null && !durationStr.isEmpty()) {
                int duration = Integer.parseInt(durationStr);
                if (duration > 0) {
                    internship.setDurationMonths(duration);
                }
            }

            String stipendStr = request.getParameter("stipendAmount");
            if (stipendStr != null && !stipendStr.isEmpty()) {
                internship.setStipendAmount(Double.parseDouble(stipendStr));
            }

            String cgpaStr = request.getParameter("minimumCgpa");
            if (cgpaStr != null && !cgpaStr.isEmpty()) {
                double cgpa = Double.parseDouble(cgpaStr);
                if (cgpa >= 0 && cgpa <= 10) {
                    internship.setMinimumCgpa(cgpa);
                }
            }

            String status = request.getParameter("status");
            if (status != null && (status.equals("OPEN") || status.equals("CLOSED"))) {
                internship.setStatus(status);
            }

            internshipDAO.updateInternship(internship);

            LOGGER.log(Level.INFO, "Internship updated: " + internship.getJobTitle());
            sendSuccessResponse(response, "Internship updated successfully", 200);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating internship", e);
            sendErrorResponse(response, "Error: " + e.getMessage(), 500);
        }
    }

    /**
     * Handle internship deletion
     */
    private void handleDeleteInternship(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int internshipId = parseIntParameter(request, "internshipId", -1);
            if (internshipId <= 0) {
                sendErrorResponse(response, "Invalid internship ID", 400);
                return;
            }

            Internship internship = internshipDAO.getInternshipById(internshipId);
            if (internship == null) {
                sendErrorResponse(response, "Internship not found", 404);
                return;
            }

            internshipDAO.deleteInternship(internshipId);

            LOGGER.log(Level.INFO, "Internship deleted: " + internship.getJobTitle());
            sendSuccessResponse(response, "Internship deleted successfully", 200);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting internship", e);
            sendErrorResponse(response, "Error deleting internship", 500);
        }
    }

    /**
     * Handle single internship retrieval
     */
    private void handleGetInternship(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int internshipId = parseIntParameter(request, "internshipId", -1);
            if (internshipId <= 0) {
                sendErrorResponse(response, "Invalid internship ID", 400);
                return;
            }

            Internship internship = internshipDAO.getInternshipById(internshipId);
            if (internship == null) {
                sendErrorResponse(response, "Internship not found", 404);
                return;
            }

            response.setStatus(200);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(internshipToJson(internship));

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving internship", e);
            sendErrorResponse(response, "Error retrieving internship", 500);
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
     * Parse double parameter safely
     */
    private double parseDoubleParameter(HttpServletRequest request, String paramName, double defaultValue) {
        try {
            String value = request.getParameter(paramName);
            if (value == null || value.trim().isEmpty()) {
                return defaultValue;
            }
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Convert internship to JSON
     */
    private String internshipToJson(Internship internship) {
        return String.format(
            "{\"internshipId\": %d, \"companyId\": %d, \"jobTitle\": \"%s\", \"jobLocation\": \"%s\", " +
            "\"durationMonths\": %d, \"stipendAmount\": %.2f, \"minimumCgpa\": %.2f, \"status\": \"%s\", " +
            "\"totalPositions\": %d, \"filledPositions\": %d}",
            internship.getInternshipId(),
            internship.getCompanyId(),
            escapeJson(internship.getJobTitle()),
            escapeJson(internship.getJobLocation()),
            internship.getDurationMonths(),
            internship.getStipendAmount(),
            internship.getMinimumCgpa(),
            internship.getStatus(),
            internship.getTotalPositions(),
            internship.getFilledPositions()
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
