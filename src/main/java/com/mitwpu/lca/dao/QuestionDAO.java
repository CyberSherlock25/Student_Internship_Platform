package com.mitwpu.lca.dao;

import com.mitwpu.lca.model.Question;
import com.mitwpu.lca.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Question entity.
 */
public class QuestionDAO {

    /**
     * Create a new question
     */
    public boolean createQuestion(Question question) {
        String sql = "INSERT INTO questions (exam_id, question_number, question_text, question_type, marks, difficulty_level) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, question.getExamId());
            stmt.setInt(2, question.getQuestionNumber());
            stmt.setString(3, question.getQuestionText());
            stmt.setString(4, question.getQuestionType());
            stmt.setInt(5, question.getMarks());
            stmt.setString(6, question.getDifficultyLevel());

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    question.setQuestionId(rs.getInt(1));
                }
                rs.close();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creating question: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get questions by exam ID
     */
    public List<Question> getQuestionsByExam(int examId) {
        String sql = "SELECT * FROM questions WHERE exam_id = ? ORDER BY question_number ASC";
        List<Question> questions = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, examId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                questions.add(mapResultSetToQuestion(rs));
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error getting questions by exam: " + e.getMessage());
            e.printStackTrace();
        }
        return questions;
    }

    /**
     * Get question by ID
     */
    public Question getQuestionById(int questionId) {
        String sql = "SELECT * FROM questions WHERE question_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, questionId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToQuestion(rs);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error getting question by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Update question
     */
    public boolean updateQuestion(Question question) {
        String sql = "UPDATE questions SET question_text = ?, question_type = ?, marks = ?, " +
                     "difficulty_level = ? WHERE question_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, question.getQuestionText());
            stmt.setString(2, question.getQuestionType());
            stmt.setInt(3, question.getMarks());
            stmt.setString(4, question.getDifficultyLevel());
            stmt.setInt(5, question.getQuestionId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating question: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Delete question
     */
    public boolean deleteQuestion(int questionId) {
        String sql = "DELETE FROM questions WHERE question_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, questionId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting question: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private Question mapResultSetToQuestion(ResultSet rs) throws SQLException {
        Question q = new Question();
        q.setQuestionId(rs.getInt("question_id"));
        q.setExamId(rs.getInt("exam_id"));
        q.setQuestionNumber(rs.getInt("question_number"));
        q.setQuestionText(rs.getString("question_text"));
        q.setQuestionType(rs.getString("question_type"));
        q.setMarks(rs.getInt("marks"));
        q.setDifficultyLevel(rs.getString("difficulty_level"));

        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) q.setCreatedAt(createdTs.toLocalDateTime());

        Timestamp updatedTs = rs.getTimestamp("updated_at");
        if (updatedTs != null) q.setUpdatedAt(updatedTs.toLocalDateTime());

        return q;
    }
}

