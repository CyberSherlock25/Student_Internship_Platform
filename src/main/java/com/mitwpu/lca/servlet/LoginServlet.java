package com.mitwpu.lca.servlet;

import com.mitwpu.lca.dao.UserDAO;
import com.mitwpu.lca.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet for handling user login
 * Authenticates users and creates session
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Redirect to login.jsp page
        response.sendRedirect("login.jsp");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("rememberMe");
        
        // Validation
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            response.sendRedirect("login.jsp?error=Email and password are required");
            return;
        }
        
        // Authenticate user
        User user = userDAO.authenticate(email, password);
        
        if (user != null) {
            // Create session
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("userEmail", user.getEmail());
            session.setAttribute("userRole", user.getRole());
            
            // Handle "Remember Me" functionality (optional - can store in cookie)
            if ("on".equals(rememberMe) || "true".equals(rememberMe)) {
                // In production, create persistent token and store in database
                System.out.println("Remember Me enabled for user: " + email);
            }
            
            // Redirect based on role
            if (user.isAdmin()) {
                response.sendRedirect("admin/dashboard.jsp");
            } else if (user.isStudent()) {
                response.sendRedirect("student/dashboard.jsp");
            } else {
                response.sendRedirect("index.jsp?error=Invalid user role");
            }
        } else {
            // Authentication failed
            response.sendRedirect("login.jsp?error=Invalid email or password");
        }
    }
}
