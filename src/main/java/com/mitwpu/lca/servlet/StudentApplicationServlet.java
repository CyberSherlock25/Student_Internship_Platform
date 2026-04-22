package com.mitwpu.lca.servlet;

import com.mitwpu.lca.dao.ApplicationDAO;
import com.mitwpu.lca.dao.InternshipDAO;
import com.mitwpu.lca.dao.StudentDAO;
import com.mitwpu.lca.model.Application;
import com.mitwpu.lca.model.Internship;
import com.mitwpu.lca.model.Student;
import com.mitwpu.lca.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.time.LocalDate;

@WebServlet("/student/apply")
public class StudentApplicationServlet extends HttpServlet {
    private final ApplicationDAO applicationDAO = new ApplicationDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final InternshipDAO internshipDAO = new InternshipDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        HttpSession session = request.getSession(false);
        User user = session != null ? (User) session.getAttribute("user") : null;

        if (user == null || !user.getRole().equals("STUDENT")) {
            sendJsonResponse(response, false, "Unauthorized access", 403);
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            sendJsonResponse(response, false, "Action parameter required", 400);
            return;
        }

        try {
            switch (action) {
                case "apply":
                    handleApply(request, response, user);
                    break;
                case "withdraw":
                    handleWithdraw(request, response, user);
                    break;
                case "getApplications":
                    handleGetApplications(request, response, user);
                    break;
                case "getApplicationDetails":
                    handleGetApplicationDetails(request, response, user);
                    break;
                default:
                    sendJsonResponse(response, false, "Invalid action", 400);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(response, false, "Server error: " + e.getMessage(), 500);
        }
    }

    private void handleApply(HttpServletRequest request, HttpServletResponse response, User user) {
        String internshipIdStr = request.getParameter("internshipId");
        String coverLetter = request.getParameter("coverLetter");

        if (internshipIdStr == null || internshipIdStr.isEmpty()) {
            sendJsonResponse(response, false, "Internship ID required", 400);
            return;
        }

        try {
            int internshipId = Integer.parseInt(internshipIdStr);

            // Get student
            Student student = studentDAO.getStudentByUserId(user.getUserId());
            if (student == null) {
                sendJsonResponse(response, false, "Student profile not found. Please complete your profile first.", 400);
                return;
            }

            // Check if internship exists and is open
            Internship internship = internshipDAO.getInternshipById(internshipId);
            if (internship == null) {
                sendJsonResponse(response, false, "Internship not found", 404);
                return;
            }

            if (!internship.getStatus().equals("OPEN")) {
                sendJsonResponse(response, false, "This internship is no longer accepting applications", 400);
                return;
            }

            // Check if deadline has passed
            if (internship.getApplicationDeadline() != null && 
                internship.getApplicationDeadline().isBefore(LocalDate.now())) {
                sendJsonResponse(response, false, "Application deadline has passed", 400);
                return;
            }

            // Check eligibility (CGPA)
            if (student.getCgpa() < internship.getMinimumCgpa()) {
                sendJsonResponse(response, false, 
                    String.format("Your CGPA (%.2f) is below the minimum required (%.2f)", 
                        student.getCgpa(), internship.getMinimumCgpa()), 400);
                return;
            }

            // Check if already applied
            java.util.List<Application> existingApps = applicationDAO.getApplicationsByStudent(student.getStudentId());
            for (Application app : existingApps) {
                if (app.getInternshipId() == internshipId) {
                    sendJsonResponse(response, false, "You have already applied to this internship", 400);
                    return;
                }
            }

            // Create application
            Application application = new Application();
            application.setStudentId(student.getStudentId());
            application.setInternshipId(internshipId);
            application.setAppliedDate(LocalDate.now());
            application.setStatus("PENDING");
            application.setCoverLetter(coverLetter != null ? coverLetter : "");

            boolean created = applicationDAO.createApplication(application);
            if (created) {
                sendJsonResponse(response, true, "Application submitted successfully!");
            } else {
                sendJsonResponse(response, false, "Failed to submit application", 500);
            }
        } catch (NumberFormatException e) {
            sendJsonResponse(response, false, "Invalid internship ID", 400);
        } catch (Exception e) {
            sendJsonResponse(response, false, "Error: " + e.getMessage(), 500);
        }
    }

    private void handleWithdraw(HttpServletRequest request, HttpServletResponse response, User user) {
        String applicationIdStr = request.getParameter("applicationId");

        if (applicationIdStr == null || applicationIdStr.isEmpty()) {
            sendJsonResponse(response, false, "Application ID required", 400);
            return;
        }

        try {
            int applicationId = Integer.parseInt(applicationIdStr);
            Student student = studentDAO.getStudentByUserId(user.getUserId());

            if (student == null) {
                sendJsonResponse(response, false, "Student not found", 404);
                return;
            }

            // Verify ownership
            Application app = applicationDAO.getApplicationById(applicationId);
            if (app == null || app.getStudentId() != student.getStudentId()) {
                sendJsonResponse(response, false, "Unauthorized", 403);
                return;
            }

            // Only allow withdrawal if status is PENDING
            if (!app.getStatus().equals("PENDING")) {
                sendJsonResponse(response, false, "Can only withdraw pending applications", 400);
                return;
            }

            applicationDAO.deleteApplication(applicationId);
            sendJsonResponse(response, true, "Application withdrawn successfully");
        } catch (NumberFormatException e) {
            sendJsonResponse(response, false, "Invalid application ID", 400);
        } catch (Exception e) {
            sendJsonResponse(response, false, "Error: " + e.getMessage(), 500);
        }
    }

    private void handleGetApplications(HttpServletRequest request, HttpServletResponse response, User user) {
        try {
            Student student = studentDAO.getStudentByUserId(user.getUserId());
            if (student == null) {
                sendJsonResponse(response, false, "Student not found", 404);
                return;
            }

            java.util.List<Application> applications = applicationDAO.getApplicationsByStudent(student.getStudentId());
            
            // Build JSON response
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < applications.size(); i++) {
                Application app = applications.get(i);
                Internship internship = internshipDAO.getInternshipById(app.getInternshipId());
                
                if (i > 0) json.append(",");
                json.append("{")
                    .append("\"applicationId\":").append(app.getApplicationId()).append(",")
                    .append("\"internshipTitle\":\"").append(escapeJson(internship.getJobTitle())).append("\",")
                    .append("\"companyName\":\"").append(escapeJson(internship.getJobTitle())).append("\",")
                    .append("\"appliedDate\":\"").append(app.getAppliedDate()).append("\",")
                    .append("\"status\":\"").append(app.getStatus()).append("\",")
                    .append("\"rating\":").append(app.getRating() != null ? app.getRating() : "null").append(",")
                    .append("\"feedback\":\"").append(escapeJson(app.getFeedback() != null ? app.getFeedback() : "")).append("\"")
                    .append("}");
            }
            json.append("]");

            response.setContentType("application/json");
            response.getWriter().write(json.toString());
        } catch (Exception e) {
            sendJsonResponse(response, false, "Error: " + e.getMessage(), 500);
        }
    }

    private void handleGetApplicationDetails(HttpServletRequest request, HttpServletResponse response, User user) {
        String applicationIdStr = request.getParameter("applicationId");

        if (applicationIdStr == null || applicationIdStr.isEmpty()) {
            sendJsonResponse(response, false, "Application ID required", 400);
            return;
        }

        try {
            int applicationId = Integer.parseInt(applicationIdStr);
            Student student = studentDAO.getStudentByUserId(user.getUserId());

            if (student == null) {
                sendJsonResponse(response, false, "Student not found", 404);
                return;
            }

            Application app = applicationDAO.getApplicationById(applicationId);
            if (app == null || app.getStudentId() != student.getStudentId()) {
                sendJsonResponse(response, false, "Unauthorized", 403);
                return;
            }

            Internship internship = internshipDAO.getInternshipById(app.getInternshipId());

            // Build JSON response
            StringBuilder json = new StringBuilder("{")
                .append("\"applicationId\":").append(app.getApplicationId()).append(",")
                .append("\"internshipId\":").append(app.getInternshipId()).append(",")
                .append("\"jobTitle\":\"").append(escapeJson(internship.getJobTitle())).append("\",")
                .append("\"location\":\"").append(escapeJson(internship.getJobLocation())).append("\",")
                .append("\"stipend\":").append(internship.getStipendAmount()).append(",")
                .append("\"duration\":").append(internship.getDurationMonths()).append(",")
                .append("\"appliedDate\":\"").append(app.getAppliedDate()).append("\",")
                .append("\"status\":\"").append(app.getStatus()).append("\",")
                .append("\"coverLetter\":\"").append(escapeJson(app.getCoverLetter() != null ? app.getCoverLetter() : "")).append("\",")
                .append("\"rating\":").append(app.getRating() != null ? app.getRating() : "null").append(",")
                .append("\"feedback\":\"").append(escapeJson(app.getFeedback() != null ? app.getFeedback() : "")).append("\"")
                .append("}");

            response.setContentType("application/json");
            response.getWriter().write(json.toString());
        } catch (NumberFormatException e) {
            sendJsonResponse(response, false, "Invalid application ID", 400);
        } catch (Exception e) {
            sendJsonResponse(response, false, "Error: " + e.getMessage(), 500);
        }
    }

    private void sendJsonResponse(HttpServletResponse response, boolean success, String message, int status) {
        response.setStatus(status);
        sendJsonResponse(response, success, message);
    }

    private void sendJsonResponse(HttpServletResponse response, boolean success, String message) {
        response.setContentType("application/json");
        try {
            PrintWriter out = response.getWriter();
            out.write("{\"success\":" + success + ",\"message\":\"" + escapeJson(message) + "\"}");
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
