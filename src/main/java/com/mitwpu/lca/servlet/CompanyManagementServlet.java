package com.mitwpu.lca.servlet;

import com.mitwpu.lca.dao.CompanyDAO;
import com.mitwpu.lca.model.Company;
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
 * Servlet for managing companies (Admin only)
 * Handles: Create, read, update, delete, and list companies
 */
@WebServlet("/admin/company")
public class CompanyManagementServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(CompanyManagementServlet.class.getName());

    private CompanyDAO companyDAO = new CompanyDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Check authentication
            HttpSession session = request.getSession(false);
            if (!isAuthorizedAdmin(session)) {
                sendErrorResponse(response, "Unauthorized", 403);
                return;
            }

            String action = request.getParameter("action");
            if (action == null || action.trim().isEmpty()) {
                action = "list";
            }

            switch (action) {
                case "get":
                    handleGetCompany(request, response);
                    break;
                case "list":
                    handleListCompanies(request, response);
                    break;
                default:
                    sendErrorResponse(response, "Invalid action", 400);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in GET request", e);
            sendErrorResponse(response, "Server error occurred", 500);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Check authentication
            HttpSession session = request.getSession(false);
            if (!isAuthorizedAdmin(session)) {
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
                    handleCreateCompany(request, response);
                    break;
                case "update":
                    handleUpdateCompany(request, response);
                    break;
                case "delete":
                    handleDeleteCompany(request, response);
                    break;
                default:
                    sendErrorResponse(response, "Invalid action", 400);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in POST request", e);
            sendErrorResponse(response, "Server error occurred", 500);
        }
    }

    /**
     * Handle company creation
     */
    private void handleCreateCompany(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String companyName = request.getParameter("companyName");
            String companyEmail = request.getParameter("companyEmail");
            String companyPhone = request.getParameter("companyPhone");
            String headquarters = request.getParameter("headquarters");
            String industry = request.getParameter("industry");
            String description = request.getParameter("description");

            // Validation
            if (companyName == null || companyName.trim().isEmpty()) {
                sendErrorResponse(response, "Company name is required", 400);
                return;
            }
            if (companyEmail == null || companyEmail.trim().isEmpty() || !isValidEmail(companyEmail)) {
                sendErrorResponse(response, "Valid email is required", 400);
                return;
            }
            if (companyPhone == null || companyPhone.trim().isEmpty()) {
                sendErrorResponse(response, "Phone number is required", 400);
                return;
            }

            // Create company
            Company company = new Company();
            company.setCompanyName(companyName.trim());
            company.setCompanyEmail(companyEmail.trim());
            company.setCompanyPhone(companyPhone.trim());
            company.setHeadquarters(headquarters != null ? headquarters.trim() : "");
            company.setIndustry(industry != null ? industry.trim() : "");
            company.setDescription(description != null ? description.trim() : "");
            company.setStatus("ACTIVE");

            companyDAO.createCompany(company);

            LOGGER.log(Level.INFO, "Company created: " + companyName);
            sendSuccessResponse(response, "Company created successfully", 201);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error creating company", e);
            sendErrorResponse(response, "Error creating company: " + e.getMessage(), 500);
        }
    }

    /**
     * Handle company update
     */
    private void handleUpdateCompany(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int companyId = parseIntParameter(request, "companyId", -1);
            if (companyId <= 0) {
                sendErrorResponse(response, "Invalid company ID", 400);
                return;
            }

            Company company = companyDAO.getCompanyById(companyId);
            if (company == null) {
                sendErrorResponse(response, "Company not found", 404);
                return;
            }

            // Update fields
            String companyName = request.getParameter("companyName");
            if (companyName != null && !companyName.trim().isEmpty()) {
                company.setCompanyName(companyName.trim());
            }

            String companyEmail = request.getParameter("companyEmail");
            if (companyEmail != null && !companyEmail.trim().isEmpty() && isValidEmail(companyEmail)) {
                company.setCompanyEmail(companyEmail.trim());
            }

            String companyPhone = request.getParameter("companyPhone");
            if (companyPhone != null && !companyPhone.trim().isEmpty()) {
                company.setCompanyPhone(companyPhone.trim());
            }

            String location = request.getParameter("headquarters");
            if (location != null) {
                company.setHeadquarters(location.trim());
            }

            String industry = request.getParameter("industry");
            if (industry != null) {
                company.setIndustry(industry.trim());
            }

            String description = request.getParameter("description");
            if (description != null) {
                company.setDescription(description.trim());
            }

            String status = request.getParameter("status");
            if (status != null && (status.equals("ACTIVE") || status.equals("INACTIVE"))) {
                company.setStatus(status);
            }

            companyDAO.updateCompany(company);

            LOGGER.log(Level.INFO, "Company updated: " + company.getCompanyName());
            sendSuccessResponse(response, "Company updated successfully", 200);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating company", e);
            sendErrorResponse(response, "Error updating company", 500);
        }
    }

    /**
     * Handle company deletion
     */
    private void handleDeleteCompany(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int companyId = parseIntParameter(request, "companyId", -1);
            if (companyId <= 0) {
                sendErrorResponse(response, "Invalid company ID", 400);
                return;
            }

            Company company = companyDAO.getCompanyById(companyId);
            if (company == null) {
                sendErrorResponse(response, "Company not found", 404);
                return;
            }

            companyDAO.deleteCompany(companyId);

            LOGGER.log(Level.INFO, "Company deleted: " + company.getCompanyName());
            sendSuccessResponse(response, "Company deleted successfully", 200);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting company", e);
            sendErrorResponse(response, "Error deleting company", 500);
        }
    }

    /**
     * Handle single company retrieval
     */
    private void handleGetCompany(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int companyId = parseIntParameter(request, "companyId", -1);
            if (companyId <= 0) {
                sendErrorResponse(response, "Invalid company ID", 400);
                return;
            }

            Company company = companyDAO.getCompanyById(companyId);
            if (company == null) {
                sendErrorResponse(response, "Company not found", 404);
                return;
            }

            response.setStatus(200);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(companyToJson(company));

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error retrieving company", e);
            sendErrorResponse(response, "Error retrieving company", 500);
        }
    }

    /**
     * Handle listing all companies
     */
    private void handleListCompanies(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            response.sendRedirect(request.getContextPath() + "/admin/companies.jsp");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error listing companies", e);
            sendErrorResponse(response, "Error retrieving companies", 500);
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
     * Validate email format
     */
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
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
     * Convert company to JSON
     */
    private String companyToJson(Company company) {
        return String.format(
            "{\"companyId\": %d, \"companyName\": \"%s\", \"companyEmail\": \"%s\", \"companyPhone\": \"%s\", \"headquarters\": \"%s\", \"industry\": \"%s\", \"status\": \"%s\"}",
            company.getCompanyId(),
            escapeJson(company.getCompanyName()),
            escapeJson(company.getCompanyEmail()),
            escapeJson(company.getCompanyPhone()),
            escapeJson(company.getHeadquarters() != null ? company.getHeadquarters() : ""),
            escapeJson(company.getIndustry() != null ? company.getIndustry() : ""),
            company.getStatus()
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
