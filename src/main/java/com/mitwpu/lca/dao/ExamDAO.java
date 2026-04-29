package com.mitwpu.lca.dao;

import com.mitwpu.lca.model.Exam;
import com.mitwpu.lca.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Exam entity.
 * Handles CRUD operations for exam scheduling and management.
 */
public class ExamDAO {

    /**
     * Create a new exam
     */
    public boolean createExam(Exam exam) {
        String sql = "INSERT INTO exams (exam_title, exam_description, total_marks, duration_minutes, " +
                     "passing_marks, exam_date, exam_start_time, exam_end_time, status, created_by) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, exam.getExamTitle());
            stmt.setString(2, exam.getExamDescription());
            stmt.setInt(3, exam.getTotalMarks());
            stmt.setInt(4, exam.getDurationMinutes());
            stmt.setInt(5, exam.getPassingMarks());
            stmt.setDate(6, Date.valueOf(exam.getExamDate()));
            stmt.setTime(7, Time.valueOf(exam.getExamStartTime()));
            stmt.setTime(8, Time.valueOf(exam.getExamEndTime()));
            stmt.setString(9, exam.getStatus());
            stmt.setInt(10, exam.getCreatedBy());

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    exam.setExamId(rs.getInt(1));
                }
                rs.close();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating exam: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get exam by ID
     */
    public Exam getExamById(int examId) {
        String sql = "SELECT * FROM exams WHERE exam_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, examId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToExam(rs);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error getting exam by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all exams
     */
    public List<Exam> getAllExams() {
        String sql = "SELECT * FROM exams ORDER BY exam_date DESC, exam_start_time ASC";
        List<Exam> exams = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                exams.add(mapResultSetToExam(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all exams: " + e.getMessage());
            e.printStackTrace();
        }
        return exams;
    }

    /**
     * Get exams by status
     */
    public List<Exam> getExamsByStatus(String status) {
        String sql = "SELECT * FROM exams WHERE status = ? ORDER BY exam_date DESC";
        List<Exam> exams = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                exams.add(mapResultSetToExam(rs));
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error getting exams by status: " + e.getMessage());
            e.printStackTrace();
        }
        return exams;
    }

    /**
     * Get scheduled/ongoing exams available for a student
     */
    public List<Exam> getAvailableExamsForStudent() {
        String sql = "SELECT * FROM exams WHERE status IN ('SCHEDULED', 'ONGOING') " +
                     "AND exam_date >= CURDATE() ORDER BY exam_date ASC";
        List<Exam> exams = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                exams.add(mapResultSetToExam(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting available exams: " + e.getMessage());
            e.printStackTrace();
        }
        return exams;
    }

    /**
     * Update exam
     */
    public boolean updateExam(Exam exam) {
        String sql = "UPDATE exams SET exam_title = ?, exam_description = ?, total_marks = ?, " +
                     "duration_minutes = ?, passing_marks = ?, exam_date = ?, exam_start_time = ?, " +
                     "exam_end_time = ?, status = ? WHERE exam_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, exam.getExamTitle());
            stmt.setString(2, exam.getExamDescription());
            stmt.setInt(3, exam.getTotalMarks());
            stmt.setInt(4, exam.getDurationMinutes());
            stmt.setInt(5, exam.getPassingMarks());
            stmt.setDate(6, Date.valueOf(exam.getExamDate()));
            stmt.setTime(7, Time.valueOf(exam.getExamStartTime()));
            stmt.setTime(8, Time.valueOf(exam.getExamEndTime()));
            stmt.setString(9, exam.getStatus());
            stmt.setInt(10, exam.getExamId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating exam: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update exam status
     */
    public boolean updateExamStatus(int examId, String newStatus) {
        String sql = "UPDATE exams SET status = ? WHERE exam_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, examId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating exam status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Delete exam
     */
    public boolean deleteExam(int examId) {
        String sql = "DELETE FROM exams WHERE exam_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, examId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting exam: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Map ResultSet row to Exam object
     */
    private Exam mapResultSetToExam(ResultSet rs) throws SQLException {
        Exam exam = new Exam();
        exam.setExamId(rs.getInt("exam_id"));
        exam.setExamTitle(rs.getString("exam_title"));
        exam.setExamDescription(rs.getString("exam_description"));
        exam.setTotalMarks(rs.getInt("total_marks"));
        exam.setDurationMinutes(rs.getInt("duration_minutes"));
        exam.setPassingMarks(rs.getInt("passing_marks"));

        Date examDate = rs.getDate("exam_date");
        if (examDate != null) exam.setExamDate(examDate.toLocalDate());

        Time startTime = rs.getTime("exam_start_time");
        if (startTime != null) exam.setExamStartTime(startTime.toLocalTime());

        Time endTime = rs.getTime("exam_end_time");
        if (endTime != null) exam.setExamEndTime(endTime.toLocalTime());

        exam.setStatus(rs.getString("status"));
        exam.setCreatedBy(rs.getInt("created_by"));

        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) exam.setCreatedAt(createdTs.toLocalDateTime());

        Timestamp updatedTs = rs.getTimestamp("updated_at");
        if (updatedTs != null) exam.setUpdatedAt(updatedTs.toLocalDateTime());

        return exam;
    }
}

