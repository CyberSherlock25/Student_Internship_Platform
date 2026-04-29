package com.mitwpu.lca.dao;

import com.mitwpu.lca.model.Option;
import com.mitwpu.lca.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Option/MCQ entity.
 */
public class OptionDAO {

    public boolean createOption(Option option) {
        String sql = "INSERT INTO options (question_id, option_text, option_number, is_correct) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, option.getQuestionId());
            stmt.setString(2, option.getOptionText());
            stmt.setInt(3, option.getOptionNumber());
            stmt.setBoolean(4, option.getIsCorrect());
            if (stmt.executeUpdate() > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) option.setOptionId(rs.getInt(1));
                rs.close();
                return true;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public List<Option> getOptionsByQuestion(int questionId) {
        String sql = "SELECT * FROM options WHERE question_id = ? ORDER BY option_number ASC";
        List<Option> options = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, questionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) options.add(mapResultSetToOption(rs));
            rs.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return options;
    }

    public Option getCorrectOption(int questionId) {
        String sql = "SELECT * FROM options WHERE question_id = ? AND is_correct = true LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, questionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) { Option o = mapResultSetToOption(rs); rs.close(); return o; }
            rs.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean updateOption(Option option) {
        String sql = "UPDATE options SET option_text = ?, option_number = ?, is_correct = ? WHERE option_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, option.getOptionText());
            stmt.setInt(2, option.getOptionNumber());
            stmt.setBoolean(3, option.getIsCorrect());
            stmt.setInt(4, option.getOptionId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean deleteOptionsByQuestion(int questionId) {
        String sql = "DELETE FROM options WHERE question_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, questionId);
            return stmt.executeUpdate() >= 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    private Option mapResultSetToOption(ResultSet rs) throws SQLException {
        Option o = new Option();
        o.setOptionId(rs.getInt("option_id"));
        o.setQuestionId(rs.getInt("question_id"));
        o.setOptionText(rs.getString("option_text"));
        o.setOptionNumber(rs.getInt("option_number"));
        o.setIsCorrect(rs.getBoolean("is_correct"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) o.setCreatedAt(ts.toLocalDateTime());
        return o;
    }
}

