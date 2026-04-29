package com.mitwpu.lca.dao;

import com.mitwpu.lca.model.ExamAttempt;
import com.mitwpu.lca.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for ExamAttempt entity.
 * Tracks student exam sessions with transaction-safe updates.
 */
public class ExamAttemptDAO {

    public boolean startAttempt(ExamAttempt attempt) {
        String sql = "INSERT INTO exam_attempts (student_id, exam_id, status, ip_address) VALUES (?, ?, 'STARTED', ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, attempt.getStudentId());
            stmt.setInt(2, attempt.getExamId());
            stmt.setString(3, attempt.getIpAddress());
            if (stmt.executeUpdate() > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) attempt.setAttemptId(rs.getInt(1));
                rs.close();
                return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public ExamAttempt getAttemptByStudentAndExam(int studentId, int examId) {
        String sql = "SELECT * FROM exam_attempts WHERE student_id = ? AND exam_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, examId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) { ExamAttempt a = mapResultSetToAttempt(rs); rs.close(); return a; }
            rs.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public ExamAttempt getAttemptById(int attemptId) {
        String sql = "SELECT * FROM exam_attempts WHERE attempt_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, attemptId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) { ExamAttempt a = mapResultSetToAttempt(rs); rs.close(); return a; }
            rs.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean updateAttemptStatus(int attemptId, String status) {
        String sql = "UPDATE exam_attempts SET status = ? WHERE attempt_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, attemptId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /**
     * Submit exam attempt with total marks - TRANSACTION SAFE
     */
    public boolean submitAttempt(int attemptId, int totalMarksObtained) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection(false); // manual commit
            String sql = "UPDATE exam_attempts SET status = 'SUBMITTED', total_marks_obtained = ?, " +
                         "submitted_at = CURRENT_TIMESTAMP, end_time = CURRENT_TIMESTAMP WHERE attempt_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, totalMarksObtained);
            stmt.setInt(2, attemptId);
            int updated = stmt.executeUpdate();
            stmt.close();
            conn.commit();
            return updated > 0;
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (SQLException e) { e.printStackTrace(); }
        }
        return false;
    }

    public boolean evaluateAttempt(int attemptId, int totalMarksObtained) {
        String sql = "UPDATE exam_attempts SET status = 'EVALUATED', total_marks_obtained = ? WHERE attempt_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, totalMarksObtained);
            stmt.setInt(2, attemptId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public List<ExamAttempt> getAttemptsByStudent(int studentId) {
        String sql = "SELECT a.*, e.exam_title, u.full_name as student_name FROM exam_attempts a " +
                     "JOIN exams e ON a.exam_id = e.exam_id " +
                     "JOIN students s ON a.student_id = s.student_id " +
                     "JOIN users u ON s.user_id = u.user_id " +
                     "WHERE a.student_id = ? ORDER BY a.start_time DESC";
        List<ExamAttempt> attempts = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) { attempts.add(mapResultSetToAttemptWithJoin(rs)); }
            rs.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return attempts;
    }

    public List<ExamAttempt> getAttemptsByExam(int examId) {
        String sql = "SELECT a.*, e.exam_title, u.full_name as student_name FROM exam_attempts a " +
                     "JOIN exams e ON a.exam_id = e.exam_id " +
                     "JOIN students s ON a.student_id = s.student_id " +
                     "JOIN users u ON s.user_id = u.user_id " +
                     "WHERE a.exam_id = ? ORDER BY a.total_marks_obtained DESC";
        List<ExamAttempt> attempts = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, examId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) { attempts.add(mapResultSetToAttemptWithJoin(rs)); }
            rs.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return attempts;
    }

    public List<ExamAttempt> getRankListByExam(int examId) {
        String sql = "SELECT a.*, e.exam_title, u.full_name as student_name, s.cgpa FROM exam_attempts a " +
                     "JOIN exams e ON a.exam_id = e.exam_id " +
                     "JOIN students s ON a.student_id = s.student_id " +
                     "JOIN users u ON s.user_id = u.user_id " +
                     "WHERE a.exam_id = ? AND a.status IN ('SUBMITTED', 'EVALUATED') " +
                     "ORDER BY a.total_marks_obtained DESC, a.submitted_at ASC";
        List<ExamAttempt> attempts = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, examId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) { attempts.add(mapResultSetToAttemptWithJoin(rs)); }
            rs.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return attempts;
    }

    private ExamAttempt mapResultSetToAttempt(ResultSet rs) throws SQLException {
        ExamAttempt a = new ExamAttempt();
        a.setAttemptId(rs.getInt("attempt_id"));
        a.setStudentId(rs.getInt("student_id"));
        a.setExamId(rs.getInt("exam_id"));
        Timestamp start = rs.getTimestamp("start_time");
        if (start != null) a.setStartTime(start.toLocalDateTime());
        Timestamp end = rs.getTimestamp("end_time");
        if (end != null) a.setEndTime(end.toLocalDateTime());
        Timestamp submitted = rs.getTimestamp("submitted_at");
        if (submitted != null) a.setSubmittedAt(submitted.toLocalDateTime());
        a.setTotalMarksObtained(rs.getInt("total_marks_obtained"));
        a.setStatus(rs.getString("status"));
        a.setIpAddress(rs.getString("ip_address"));
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) a.setCreatedAt(created.toLocalDateTime());
        Timestamp updated = rs.getTimestamp("updated_at");
        if (updated != null) a.setUpdatedAt(updated.toLocalDateTime());
        return a;
    }

    private ExamAttempt mapResultSetToAttemptWithJoin(ResultSet rs) throws SQLException {
        ExamAttempt a = mapResultSetToAttempt(rs);
        a.setExamTitle(rs.getString("exam_title"));
        a.setStudentName(rs.getString("student_name"));
        return a;
    }
}

