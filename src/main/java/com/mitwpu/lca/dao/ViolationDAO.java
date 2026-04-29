package com.mitwpu.lca.dao;

import com.mitwpu.lca.model.Violation;
import com.mitwpu.lca.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Violation operations
 * Handles CRUD operations for exam proctoring violations
 */
public class ViolationDAO {
    
    /**
     * Record a new violation
     * @param violation Violation object to record
     * @return true if successfully recorded
     */
    public boolean recordViolation(Violation violation) {
        String sql = "INSERT INTO violation_logs (attempt_id, student_id, violation_type, " +
                     "violation_description, severity, ip_address) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, violation.getAttemptId());
            stmt.setInt(2, violation.getStudentId());
            stmt.setString(3, violation.getViolationType());
            stmt.setString(4, violation.getViolationDescription());
            stmt.setString(5, violation.getSeverity());
            stmt.setString(6, violation.getIpAddress());
            
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error recording violation: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Get all violations for an exam attempt
     * @param attemptId Exam attempt ID
     * @return List of violations for the attempt
     */
    public List<Violation> getViolationsByAttempt(int attemptId) {
        String sql = "SELECT violation_id, attempt_id, student_id, violation_type, " +
                     "violation_description, severity, violation_time, ip_address " +
                     "FROM violation_logs WHERE attempt_id = ? ORDER BY violation_time ASC";
        
        List<Violation> violations = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, attemptId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                violations.add(mapResultSetToViolation(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting violations by attempt: " + e.getMessage());
            e.printStackTrace();
        }
        return violations;
    }
    
    /**
     * Get all violations for a student
     * @param studentId Student ID
     * @return List of violations for the student
     */
    public List<Violation> getViolationsByStudent(int studentId) {
        String sql = "SELECT violation_id, attempt_id, student_id, violation_type, " +
                     "violation_description, severity, violation_time, ip_address " +
                     "FROM violation_logs WHERE student_id = ? ORDER BY violation_time DESC";
        
        List<Violation> violations = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                violations.add(mapResultSetToViolation(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting violations by student: " + e.getMessage());
            e.printStackTrace();
        }
        return violations;
    }
    
    /**
     * Get violation statistics for an attempt
     * @param attemptId Exam attempt ID
     * @return Violation count summary
     */
    public ViolationSummary getViolationSummary(int attemptId) {
        String sql = "SELECT COUNT(*) as total, severity FROM violation_logs " +
                     "WHERE attempt_id = ? GROUP BY severity";
        
        ViolationSummary summary = new ViolationSummary();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, attemptId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                int count = rs.getInt("total");
                String severity = rs.getString("severity");
                
                if ("HIGH".equals(severity)) summary.highViolations = count;
                else if ("MEDIUM".equals(severity)) summary.mediumViolations = count;
                else if ("LOW".equals(severity)) summary.lowViolations = count;
            }
        } catch (SQLException e) {
            System.err.println("Error getting violation summary: " + e.getMessage());
            e.printStackTrace();
        }
        
        return summary;
    }
    
    /**
     * Get count of violations by type
     * @param attemptId Exam attempt ID
     * @return Violation type count summary
     */
    public String getViolationTypeSummary(int attemptId) {
        String sql = "SELECT violation_type, COUNT(*) as count FROM violation_logs " +
                     "WHERE attempt_id = ? GROUP BY violation_type";
        
        StringBuilder summary = new StringBuilder();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, attemptId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String type = rs.getString("violation_type");
                int count = rs.getInt("count");
                summary.append(type).append(": ").append(count).append(", ");
            }
        } catch (SQLException e) {
            System.err.println("Error getting violation type summary: " + e.getMessage());
            e.printStackTrace();
        }
        
        return summary.toString().replaceAll(", $", "");
    }
    
    /**
     * Map ResultSet to Violation object
     */
    private Violation mapResultSetToViolation(ResultSet rs) throws SQLException {
        Violation violation = new Violation();
        violation.setViolationId(rs.getInt("violation_id"));
        violation.setAttemptId(rs.getInt("attempt_id"));
        violation.setStudentId(rs.getInt("student_id"));
        violation.setViolationType(rs.getString("violation_type"));
        violation.setViolationDescription(rs.getString("violation_description"));
        violation.setSeverity(rs.getString("severity"));
        violation.setViolationTime(rs.getTimestamp("violation_time").toLocalDateTime());
        violation.setIpAddress(rs.getString("ip_address"));
        return violation;
    }
    
    /**
     * Inner class for violation summary statistics
     */
    public static class ViolationSummary {
        public int highViolations = 0;
        public int mediumViolations = 0;
        public int lowViolations = 0;
        
        public int getTotalViolations() {
            return highViolations + mediumViolations + lowViolations;
        }
        
        public String getSummaryText() {
            return String.format("High: %d, Medium: %d, Low: %d", 
                    highViolations, mediumViolations, lowViolations);
        }
    }
}
