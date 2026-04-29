package com.mitwpu.lca.servlet;

import com.mitwpu.lca.dao.ExamResultDAO;
import com.mitwpu.lca.dao.ViolationDAO;
import com.mitwpu.lca.model.ExamResult;
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
 * Servlet for handling exam results and report generation
 * Provides result data to company admins and students
 */
@WebServlet("/exam-results")
public class ExamResultServlet extends HttpServlet {
    
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
            
            if ("getByStudent".equals(action)) {
                getResultsByStudent(request, response, user);
            } else if ("getByExam".equals(action)) {
                getResultsByExam(request, response, user);
            } else if ("getById".equals(action)) {
                getResultById(request, response);
            } else if ("updateStatus".equals(action)) {
                updateResultStatus(request, response, user);
            } else {
                sendJsonResponse(response, false, "Invalid action", null);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(response, false, "Server error: " + e.getMessage(), null);
        }
    }
    
    private void getResultsByStudent(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {
        
        try {
            int studentId = Integer.parseInt(request.getParameter("studentId"));
            
            // Students can only view their own results
            if ("STUDENT".equals(user.getRole()) && studentId != Integer.parseInt(
                    request.getSession().getAttribute("studentId").toString())) {
                sendJsonResponse(response, false, "Unauthorized access", null);
                return;
            }
            
            ExamResultDAO dao = new ExamResultDAO();
            List<ExamResult> results = dao.getExamResultsByStudent(studentId);
            
            JSONArray resultArray = new JSONArray();
            for (ExamResult r : results) {
                JSONObject obj = buildResultJSON(r);
                resultArray.add(obj);
            }
            
            sendJsonResponse(response, true, "Results retrieved", resultArray);
            
        } catch (NumberFormatException e) {
            sendJsonResponse(response, false, "Invalid student ID", null);
        }
    }
    
    private void getResultsByExam(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {
        
        try {
            int examId = Integer.parseInt(request.getParameter("examId"));
            
            // Only ADMIN and COMPANY_ADMIN can view exam results
            if (!("ADMIN".equals(user.getRole()) || "COMPANY_ADMIN".equals(user.getRole()))) {
                sendJsonResponse(response, false, "Unauthorized access", null);
                return;
            }
            
            ExamResultDAO dao = new ExamResultDAO();
            List<ExamResult> results = dao.getExamResultsByExam(examId);
            
            JSONArray resultArray = new JSONArray();
            for (ExamResult r : results) {
                JSONObject obj = buildResultJSON(r);
                resultArray.add(obj);
            }
            
            sendJsonResponse(response, true, "Results retrieved", resultArray);
            
        } catch (NumberFormatException e) {
            sendJsonResponse(response, false, "Invalid exam ID", null);
        }
    }
    
    private void getResultById(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        try {
            int resultId = Integer.parseInt(request.getParameter("resultId"));
            
            ExamResultDAO dao = new ExamResultDAO();
            ExamResult result = dao.getExamResultById(resultId);
            
            if (result != null) {
                JSONObject resultObj = buildResultJSON(result);
                
                // Get violation details
                ViolationDAO violationDAO = new ViolationDAO();
                ViolationDAO.ViolationSummary summary = violationDAO.getViolationSummary(result.getAttemptId());
                resultObj.put("violationDetails", summary.getSummaryText());
                
                sendJsonResponse(response, true, "Result retrieved", resultObj);
            } else {
                sendJsonResponse(response, false, "Result not found", null);
            }
            
        } catch (NumberFormatException e) {
            sendJsonResponse(response, false, "Invalid result ID", null);
        }
    }
    
    private void updateResultStatus(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {
        
        try {
            // Only admin can update result status
            if (!("ADMIN".equals(user.getRole()) || "COMPANY_ADMIN".equals(user.getRole()))) {
                sendJsonResponse(response, false, "Unauthorized access", null);
                return;
            }
            
            int resultId = Integer.parseInt(request.getParameter("resultId"));
            String status = request.getParameter("status");
            
            ExamResultDAO dao = new ExamResultDAO();
            boolean success = dao.updateResultStatus(resultId, status, user.getUserId());
            
            if (success) {
                sendJsonResponse(response, true, "Result status updated", null);
            } else {
                sendJsonResponse(response, false, "Failed to update result status", null);
            }
            
        } catch (NumberFormatException e) {
            sendJsonResponse(response, false, "Invalid parameters", null);
        }
    }
    
    private JSONObject buildResultJSON(ExamResult result) {
        JSONObject obj = new JSONObject();
        obj.put("resultId", result.getResultId());
        obj.put("studentId", result.getStudentId());
        obj.put("studentName", result.getStudentName());
        obj.put("examId", result.getExamId());
        obj.put("examTitle", result.getExamTitle());
        obj.put("totalMarks", result.getTotalMarks());
        obj.put("obtainedMarks", result.getObtainedMarks());
        obj.put("percentage", String.format("%.2f", result.getPercentage()));
        obj.put("passed", result.isPassed());
        obj.put("totalViolations", result.getTotalViolations());
        obj.put("violationSeverity", result.getViolationSeverity());
        obj.put("resultStatus", result.getResultStatus());
        if (result.getReviewedAt() != null) {
            obj.put("reviewedAt", result.getReviewedAt().toString());
        }
        return obj;
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
