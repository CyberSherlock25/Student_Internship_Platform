package com.mitwpu.lca.dao;

import com.mitwpu.lca.model.Answer;
import com.mitwpu.lca.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Answer entity.
 * Handles auto-save during exam and final submission.
 */
public class AnswerDAO {

    /**
     * Save or update an answer (auto-save / upsert)
     */
    public boolean saveOrUpdateAnswer(Answer answer) {
        // First check if answer already exists for this attempt+question
        Answer existing = getAnswerByAttemptAndQuestion(answer.getAttemptId(), answer.getQuestionId());

        if (existing != null) {
            // Update existing
            String sql = "UPDATE answers SET selected_option_id = ?, subjective_answer = ?, updated_at = CURRENT_TIMESTAMP " +
                         "WHERE answer_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                if (answer.getSelectedOptionId() != null)
                    stmt.setInt(1, answer.getSelectedOptionId());
                else
                    stmt.setNull(1, Types.INTEGER);
                stmt.setString(2, answer.getSubjectiveAnswer());
                stmt.setInt(3, existing.getAnswerId());
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) { e.printStackTrace(); }
        } else {
            // Insert new
            String sql = "INSERT INTO answers (attempt_id, question_id, selected_option_id, subjective_answer, marks_obtained) " +
                         "VALUES (?, ?, ?, ?, 0)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, answer.getAttemptId());
                stmt.setInt(2, answer.getQuestionId());
                if (answer.getSelectedOptionId() != null)
                    stmt.setInt(3, answer.getSelectedOptionId());
                else
                    stmt.setNull(3, Types.INTEGER);
                stmt.setString(4, answer.getSubjectiveAnswer());
                return stmt.executeUpdate() > 0;
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return false;
    }

    public Answer getAnswerByAttemptAndQuestion(int attemptId, int questionId) {
        String sql = "SELECT * FROM answers WHERE attempt_id = ? AND question_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, attemptId);
            stmt.setInt(2, questionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) { Answer a = mapResultSetToAnswer(rs); rs.close(); return a; }
            rs.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Answer> getAnswersByAttempt(int attemptId) {
        String sql = "SELECT a.*, q.question_text, q.question_type, q.marks as question_marks FROM answers a " +
                     "JOIN questions q ON a.question_id = q.question_id WHERE a.attempt_id = ?";
        List<Answer> answers = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, attemptId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) { answers.add(mapResultSetToAnswerWithQuestion(rs)); }
            rs.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return answers;
    }

    /**
     * Evaluate MCQ answers auto-magically using SQL JOIN with options table
     */
    public boolean evaluateMCQAnswers(int attemptId) {
        String sql = "UPDATE answers a " +
                     "JOIN questions q ON a.question_id = q.question_id " +
                     "JOIN options o ON a.selected_option_id = o.option_id " +
                     "SET a.marks_obtained = CASE WHEN o.is_correct = true THEN q.marks ELSE 0 END, " +
                     "    a.is_marked = true, a.marked_at = CURRENT_TIMESTAMP " +
                     "WHERE a.attempt_id = ? AND q.question_type = 'MCQ'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, attemptId);
            return stmt.executeUpdate() >= 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /**
     * Calculate total marks obtained in an attempt
     */
    public int calculateTotalMarks(int attemptId) {
        String sql = "SELECT COALESCE(SUM(marks_obtained), 0) as total FROM answers WHERE attempt_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, attemptId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) { int total = rs.getInt("total"); rs.close(); return total; }
            rs.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /**
     * Admin evaluates subjective answer
     */
    public boolean evaluateSubjectiveAnswer(int answerId, int marksObtained) {
        String sql = "UPDATE answers SET marks_obtained = ?, is_marked = true, marked_at = CURRENT_TIMESTAMP " +
                     "WHERE answer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, marksObtained);
            stmt.setInt(2, answerId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean markUnattemptedQuestions(int attemptId, int examId) {
        String sql = "INSERT INTO answers (attempt_id, question_id, marks_obtained) " +
                     "SELECT ?, q.question_id, 0 FROM questions q " +
                     "WHERE q.exam_id = ? AND q.question_id NOT IN " +
                     "(SELECT question_id FROM answers WHERE attempt_id = ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, attemptId);
            stmt.setInt(2, examId);
            stmt.setInt(3, attemptId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    private Answer mapResultSetToAnswer(ResultSet rs) throws SQLException {
        Answer a = new Answer();
        a.setAnswerId(rs.getInt("answer_id"));
        a.setAttemptId(rs.getInt("attempt_id"));
        a.setQuestionId(rs.getInt("question_id"));
        int optId = rs.getInt("selected_option_id");
        if (!rs.wasNull()) a.setSelectedOptionId(optId);
        a.setSubjectiveAnswer(rs.getString("subjective_answer"));
        a.setMarksObtained(rs.getInt("marks_obtained"));
        a.setIsMarked(rs.getBoolean("is_marked"));
        Timestamp marked = rs.getTimestamp("marked_at");
        if (marked != null) a.setMarkedAt(marked.toLocalDateTime());
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) a.setCreatedAt(created.toLocalDateTime());
        Timestamp updated = rs.getTimestamp("updated_at");
        if (updated != null) a.setUpdatedAt(updated.toLocalDateTime());
        return a;
    }

    private Answer mapResultSetToAnswerWithQuestion(ResultSet rs) throws SQLException {
        Answer a = mapResultSetToAnswer(rs);
        a.setQuestionText(rs.getString("question_text"));
        return a;
    }
}

