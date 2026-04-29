package com.mitwpu.lca.util;

import java.sql.*;

/**
 * Centralized logging utility for comprehensive audit trail
 * Logs all system actions, user activities, and data changes
 */
public class AuditLogger {
    
    /**
     * Log a user action
     */
    public static void logAction(int userId, String actionType, String entityType, 
                                 int entityId, String description, String ipAddress, String userAgent) {
        String sql = "INSERT INTO action_logs (user_id, action_type, entity_type, entity_id, " +
                     "description, ip_address, user_agent) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setString(2, actionType);
            stmt.setString(3, entityType);
            stmt.setInt(4, entityId);
            stmt.setString(5, description);
            stmt.setString(6, ipAddress);
            stmt.setString(7, userAgent);
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error logging action: " + e.getMessage());
        }
    }
    
    /**
     * Log application status change
     */
    public static void logApplicationStatusChange(int applicationId, String oldStatus, String newStatus,
                                                   String notes, int companyAdminId) {
        String sql = "INSERT INTO application_logs (application_id, action, previous_status, " +
                     "new_status, notes, company_admin_id) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, applicationId);
            stmt.setString(2, "STATUS_CHANGE");
            stmt.setString(3, oldStatus);
            stmt.setString(4, newStatus);
            stmt.setString(5, notes);
            stmt.setInt(6, companyAdminId);
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error logging application status: " + e.getMessage());
        }
    }
    
    /**
     * Log exam attempt start
     */
    public static void logExamAttemptStart(int attemptId, int studentId, int examId, String ipAddress) {
        logAction(studentId, "EXAM_ATTEMPT_START", "EXAM", examId,
                 String.format("Student %d started exam attempt %d", studentId, attemptId),
                 ipAddress, "");
    }
    
    /**
     * Log exam attempt submission
     */
    public static void logExamAttemptSubmission(int attemptId, int studentId, int examId, 
                                                int marksObtained, String ipAddress) {
        logAction(studentId, "EXAM_ATTEMPT_SUBMIT", "EXAM", examId,
                 String.format("Student %d submitted exam with score %d", studentId, marksObtained),
                 ipAddress, "");
    }
    
    /**
     * Log violation recording
     */
    public static void logViolation(int studentId, int attemptId, String violationType, 
                                    String severity, String ipAddress) {
        logAction(studentId, "VIOLATION_DETECTED", "EXAM_ATTEMPT", attemptId,
                 String.format("Violation type: %s, Severity: %s", violationType, severity),
                 ipAddress, "");
    }
    
    /**
     * Log application creation
     */
    public static void logApplicationCreation(int studentId, int applicationId, int internshipId) {
        logAction(studentId, "APPLICATION_CREATED", "APPLICATION", applicationId,
                 String.format("Student applied for internship %d", internshipId),
                 "", "");
    }
    
    /**
     * Get audit trail for a specific user
     */
    public static ResultSet getAuditTrailForUser(int userId) throws SQLException {
        String sql = "SELECT * FROM action_logs WHERE user_id = ? ORDER BY logged_at DESC LIMIT 100";
        Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, userId);
        return stmt.executeQuery();
    }
    
    /**
     * Get audit trail for a specific entity
     */
    public static ResultSet getAuditTrailForEntity(String entityType, int entityId) throws SQLException {
        String sql = "SELECT * FROM action_logs WHERE entity_type = ? AND entity_id = ? " +
                     "ORDER BY logged_at DESC LIMIT 100";
        Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, entityType);
        stmt.setInt(2, entityId);
        return stmt.executeQuery();
    }
    
    /**
     * Get recent actions count
     */
    public static int getRecentActionsCount(String actionType, int minutes) throws SQLException {
        String sql = "SELECT COUNT(*) FROM action_logs WHERE action_type = ? " +
                     "AND logged_at >= DATE_SUB(NOW(), INTERVAL ? MINUTE)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, actionType);
            stmt.setInt(2, minutes);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}
