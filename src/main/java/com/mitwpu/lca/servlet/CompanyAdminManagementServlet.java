package com.mitwpu.lca.servlet;

import com.mitwpu.lca.dao.CompanyAdminDAO;
import com.mitwpu.lca.dao.CompanyDAO;
import com.mitwpu.lca.dao.UserDAO;
import com.mitwpu.lca.model.CompanyAdmin;
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
 * Servlet for managing company admins
 * Only super admin can assign/remove company admins
 */
@WebServlet("/admin/company-admins")
public class CompanyAdminManagementServlet extends HttpServlet {
    
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
            if (user == null || !"ADMIN".equals(user.getRole())) {
                sendJsonResponse(response, false, "Unauthorized access", null);
                return;
            }
            
            String action = request.getParameter("action");
            
            if ("assign".equals(action)) {
                assignCompanyAdmin(request, response);
            } else if ("getByCompany".equals(action)) {
                getAdminsByCompany(request, response);
            } else if ("remove".equals(action)) {
                removeCompanyAdmin(request, response);
            } else {
                sendJsonResponse(response, false, "Invalid action", null);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(response, false, "Server error: " + e.getMessage(), null);
        }
    }
    
    private void assignCompanyAdmin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        try {
            int userId = Integer.parseInt(request.getParameter("userId"));
            int companyId = Integer.parseInt(request.getParameter("companyId"));
            String designation = request.getParameter("designation");
            
            if (userId <= 0 || companyId <= 0 || designation == null || designation.isEmpty()) {
                sendJsonResponse(response, false, "Invalid parameters", null);
                return;
            }
            
            // Check if user exists and is not already assigned
            UserDAO userDAO = new UserDAO();
            User targetUser = userDAO.getUserById(userId);
            
            if (targetUser == null) {
                sendJsonResponse(response, false, "User not found", null);
                return;
            }
            
            // Update user role to COMPANY_ADMIN
            targetUser.setRole("COMPANY_ADMIN");
            userDAO.updateUser(targetUser);
            
            // Assign company admin
            CompanyAdminDAO dao = new CompanyAdminDAO();
            boolean success = dao.assignCompanyAdmin(userId, companyId, designation);
            
            if (success) {
                sendJsonResponse(response, true, "Company admin assigned successfully", null);
            } else {
                sendJsonResponse(response, false, "Failed to assign company admin", null);
            }
            
        } catch (NumberFormatException e) {
            sendJsonResponse(response, false, "Invalid number format", null);
        }
    }
    
    private void getAdminsByCompany(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        try {
            int companyId = Integer.parseInt(request.getParameter("companyId"));
            
            CompanyAdminDAO dao = new CompanyAdminDAO();
            List<CompanyAdmin> admins = dao.getAdminsByCompany(companyId);
            
            JSONArray adminArray = new JSONArray();
            for (CompanyAdmin admin : admins) {
                JSONObject obj = new JSONObject();
                obj.put("adminId", admin.getAdminId());
                obj.put("userId", admin.getUserId());
                obj.put("companyId", admin.getCompanyId());
                obj.put("designation", admin.getDesignation());
                if (admin.getUser() != null) {
                    obj.put("userName", admin.getUser().getFullName());
                    obj.put("userEmail", admin.getUser().getEmail());
                }
                obj.put("assignedAt", admin.getAssignedAt().toString());
                adminArray.add(obj);
            }
            
            sendJsonResponse(response, true, "Admins retrieved", adminArray);
            
        } catch (NumberFormatException e) {
            sendJsonResponse(response, false, "Invalid company ID", null);
        }
    }
    
    private void removeCompanyAdmin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        try {
            int adminId = Integer.parseInt(request.getParameter("adminId"));
            
            CompanyAdminDAO dao = new CompanyAdminDAO();
            CompanyAdmin admin = dao.getCompanyAdminById(adminId);
            
            if (admin != null) {
                boolean success = dao.removeCompanyAdmin(adminId);
                
                if (success) {
                    // Update user role back to STUDENT or default
                    UserDAO userDAO = new UserDAO();
                    User user = userDAO.getUserById(admin.getUserId());
                    if (user != null) {
                        user.setRole("STUDENT");
                        userDAO.updateUser(user);
                    }
                    
                    sendJsonResponse(response, true, "Company admin removed successfully", null);
                } else {
                    sendJsonResponse(response, false, "Failed to remove company admin", null);
                }
            } else {
                sendJsonResponse(response, false, "Admin not found", null);
            }
            
        } catch (NumberFormatException e) {
            sendJsonResponse(response, false, "Invalid admin ID", null);
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
