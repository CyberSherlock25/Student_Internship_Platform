package com.mitwpu.lca.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.mitwpu.lca.model.User;
import java.io.IOException;

/**
 * Authentication Filter for role-based access control
 * Intercepts requests to /admin/* and /student/* URLs
 */
@WebFilter(urlPatterns = {"/admin/*", "/student/*"})
public class AuthFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("AuthFilter initialized");
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String relativePath = requestURI.substring(contextPath.length());
        
        // Check if user is logged in
        User user = null;
        if (session != null) {
            user = (User) session.getAttribute("user");
        }
        
        // If not logged in, redirect to login
        if (user == null) {
            httpResponse.sendRedirect(contextPath + "/login.jsp?error=Session expired. Please login again.");
            return;
        }
        
        // Check role-based access
        if (relativePath.startsWith("/admin/")) {
            if (!user.isAdmin()) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, 
                    "Access Denied: Admin privileges required");
                return;
            }
        } else if (relativePath.startsWith("/student/")) {
            if (!user.isStudent()) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, 
                    "Access Denied: Student privileges required");
                return;
            }
        }
        
        // User is authenticated and authorized, proceed with request
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        System.out.println("AuthFilter destroyed");
    }
}
