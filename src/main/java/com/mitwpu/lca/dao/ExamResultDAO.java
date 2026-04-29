package com.mitwpu.lca.dao;

import com.mitwpu.lca.model.ExamResult;
import com.mitwpu.lca.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Exam Result operations
 * Handles CRUD operations for exam results and scoring
 */
public class ExamResultDAO {
    
    /**
     * Create exam result
     * @param result ExamResult object
     * @return true if successfully created
     */
    public boolean createExamResult(ExamResult result) {
        String sql = "INSERT INTO exam_results (attempt_id, student_id, exam_id, total_marks, " +
                     "obtained_marks, percentage, passed, total_violations, violation_severity, result_status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, result.getAttemptId());
            stmt.setInt(2, result.getStudentId());
            stmt.setInt(3, result.getExamId());
            stmt.setInt(4, result.getTotalMarks());
            stmt.setInt(5, result.getObtainedMarks());
            stmt.setDouble(6, result.getPercentage());
            stmt.setBoolean(7, result.isPassed());
            stmt.setInt(8, result.getTotalViolations());
            stmt.setString(9, result.getViolationSeverity());
            stmt.setString(10, result.getResultStatus());
            
            int resultRows = stmt.executeUpdate();
            return resultRows > 0;
        } catch (SQLException e) {
            System.err.println("Error creating exam result: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Get exam result by ID
     * @param resultId Result ID
     * @return ExamResult object or null
     */
    public ExamResult getExamResultById(int resultId) {
        String sql = "SELECT result_id, attempt_id, student_id, exam_id, total_marks, " +
                     "obtained_marks, percentage, passed, total_violations, violation_severity, " +
                     "result_status, reviewed_by, reviewed_at FROM exam_results WHERE result_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, resultId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToExamResult(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting exam result by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get exam result by attempt ID
     * @param attemptId Exam attempt ID
     * @return ExamResult object or null
     */
    public ExamResult getExamResultByAttempt(int attemptId) {
        String sql = "SELECT result_id, attempt_id, student_id, exam_id, total_marks, " +
                     "obtained_marks, percentage, passed, total_violations, violation_severity, " +
                     "result_status, reviewed_by, reviewed_at FROM exam_results WHERE attempt_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, attemptId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToExamResult(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting exam result by attempt: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get exam results for a student
     * @param studentId Student ID
     * @return List of exam results
     */
    public List<ExamResult> getExamResultsByStudent(int studentId) {
        String sql = "SELECT er.result_id, er.attempt_id, er.student_id, er.exam_id, er.total_marks, " +
                     "er.obtained_marks, er.percentage, er.passed, er.total_violations, er.violation_severity, " +
                     "er.result_status, er.reviewed_by, er.reviewed_at, e.exam_title, u.full_name as reviewer_name " +
                     "FROM exam_results er " +
                     "JOIN exams e ON er.exam_id = e.exam_id " +
                     "LEFT JOIN users u ON er.reviewed_by = u.user_id " +
                     "WHERE er.student_id = ? ORDER BY er.result_id DESC";
        
        List<ExamResult> results = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                ExamResult result = mapResultSetToExamResult(rs);
                result.setExamTitle(rs.getString("exam_title"));
                result.setReviewerName(rs.getString("reviewer_name"));
                results.add(result);
            }
        } catch (SQLException e) {
            System.err.println("Error getting exam results by student: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }
    
    /**
     * Get exam results for an exam
     * @param examId Exam ID
     * @return List of exam results
     */
    public List<ExamResult> getExamResultsByExam(int examId) {
        String sql = "SELECT er.result_id, er.attempt_id, er.student_id, er.exam_id, er.total_marks, " +
                     "er.obtained_marks, er.percentage, er.passed, er.total_violations, er.violation_severity, " +
                     "er.result_status, er.reviewed_by, er.reviewed_at, u.full_name as student_name " +
                     "FROM exam_results er " +
                     "JOIN students s ON er.student_id = s.student_id " +
                     "JOIN users u ON s.user_id = u.user_id " +
                     "WHERE er.exam_id = ? ORDER BY er.result_id DESC";
        
        List<ExamResult> results = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, examId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                ExamResult result = mapResultSetToExamResult(rs);
                result.setStudentName(rs.getString("student_name"));
                results.add(result);
            }
        } catch (SQLException e) {
            System.err.println("Error getting exam results by exam: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }
    
    /**
     * Update result status
     * @param resultId Result ID
     * @param status New status (PENDING, EVALUATED, PUBLISHED)
     * @param reviewedBy User ID of reviewer
     * @return true if successfully updated
     */
    public boolean updateResultStatus(int resultId, String status, int reviewedBy) {
        String sql = "UPDATE exam_results SET result_status = ?, reviewed_by = ?, reviewed_at = ? " +
                     "WHERE result_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, reviewedBy);
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(4, resultId);
            
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error updating result status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Map ResultSet to ExamResult object
     */
    private ExamResult mapResultSetToExamResult(ResultSet rs) throws SQLException {
        ExamResult result = new ExamResult();
        result.setResultId(rs.getInt("result_id"));
        result.setAttemptId(rs.getInt("attempt_id"));
        result.setStudentId(rs.getInt("student_id"));
        result.setExamId(rs.getInt("exam_id"));
        result.setTotalMarks(rs.getInt("total_marks"));
        result.setObtainedMarks(rs.getInt("obtained_marks"));
        result.setPercentage(rs.getDouble("percentage"));
        result.setPassed(rs.getBoolean("passed"));
        result.setTotalViolations(rs.getInt("total_violations"));
        result.setViolationSeverity(rs.getString("violation_severity"));
        result.setResultStatus(rs.getString("result_status"));
        result.setReviewedBy(rs.getInt("reviewed_by"));
        Timestamp reviewedAt = rs.getTimestamp("reviewed_at");
        if (reviewedAt != null) {
            result.setReviewedAt(reviewedAt.toLocalDateTime());
        }
        return result;
    }
}
