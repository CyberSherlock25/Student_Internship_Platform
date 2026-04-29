package com.mitwpu.lca.servlet;

import com.mitwpu.lca.dao.ViolationDAO;
import com.mitwpu.lca.model.Violation;
import com.mitwpu.lca.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Servlet for handling exam proctoring violations
 * Records and retrieves violation logs
 */
@WebServlet("/student/violations")
public class ViolationServlet extends HttpServlet {
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        
        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
                sendJsonResponse(response, false, "Session expired", null);
                return;
            }
            
            User user = (User) session.getAttribute("user");
            if (user == null) {
                sendJsonResponse(response, false, "Unauthorized access", null);
                return;
            }
            
            String action = request.getParameter("action");
            
            if ("record".equals(action)) {
                recordViolation(request, response);
            } else if ("getByAttempt".equals(action)) {
                getViolationsByAttempt(request, response);
            } else if ("getSummary".equals(action)) {
                getViolationSummary(request, response);
            } else {
                sendJsonResponse(response, false, "Invalid action", null);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(response, false, "Server error: " + e.getMessage(), null);
        }
    }
    
    private void recordViolation(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        try {
            int attemptId = Integer.parseInt(request.getParameter("attemptId"));
            int studentId = Integer.parseInt(request.getParameter("studentId"));
            String violationType = request.getParameter("violationType");
            String violationDescription = request.getParameter("violationDescription");
            String severity = request.getParameter("severity");
            String ipAddress = request.getRemoteAddr();
            
            // Validate inputs
            if (attemptId <= 0 || studentId <= 0 || violationType == null || violationType.isEmpty()) {
                sendJsonResponse(response, false, "Invalid parameters", null);
                return;
            }
            
            Violation violation = new Violation(attemptId, studentId, violationType, 
                                               violationDescription, severity, ipAddress);
            
            ViolationDAO dao = new ViolationDAO();
            boolean success = dao.recordViolation(violation);
            
            if (success) {
                sendJsonResponse(response, true, "Violation recorded successfully", null);
            } else {
                sendJsonResponse(response, false, "Failed to record violation", null);
            }
            
        } catch (NumberFormatException e) {
            sendJsonResponse(response, false, "Invalid number format", null);
        }
    }
    
    private void getViolationsByAttempt(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        try {
            int attemptId = Integer.parseInt(request.getParameter("attemptId"));
            
            ViolationDAO dao = new ViolationDAO();
            List<Violation> violations = dao.getViolationsByAttempt(attemptId);
            
            JSONArray violationArray = new JSONArray();
            for (Violation v : violations) {
                JSONObject obj = new JSONObject();
                obj.put("violationId", v.getViolationId());
                obj.put("violationType", v.getViolationType());
                obj.put("violationDescription", v.getViolationDescription());
                obj.put("severity", v.getSeverity());
                obj.put("violationTime", v.getViolationTime().toString());
                violationArray.add(obj);
            }
            
            sendJsonResponse(response, true, "Violations retrieved", violationArray);
            
        } catch (NumberFormatException e) {
            sendJsonResponse(response, false, "Invalid attempt ID", null);
        }
    }
    
    private void getViolationSummary(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        try {
            int attemptId = Integer.parseInt(request.getParameter("attemptId"));
            
            ViolationDAO dao = new ViolationDAO();
            ViolationDAO.ViolationSummary summary = dao.getViolationSummary(attemptId);
            String typeSummary = dao.getViolationTypeSummary(attemptId);
            
            JSONObject result = new JSONObject();
            result.put("totalViolations", summary.getTotalViolations());
            result.put("highViolations", summary.highViolations);
            result.put("mediumViolations", summary.mediumViolations);
            result.put("lowViolations", summary.lowViolations);
            result.put("violationTypeSummary", typeSummary);
            
            sendJsonResponse(response, true, "Summary retrieved", result);
            
        } catch (NumberFormatException e) {
            sendJsonResponse(response, false, "Invalid attempt ID", null);
        }
    }
    
    private void sendJsonResponse(HttpServletResponse response, boolean success, 
                                 String message, Object data) throws IOException {
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("success", success);
        jsonResponse.put("message", message);
        if (data != null) {
            jsonResponse.put("data", data);
        }
        response.getWriter().write(jsonResponse.toJSONString());
    }
}
