package com.mitwpu.lca.dao;

import com.mitwpu.lca.model.AuditLog;
import com.mitwpu.lca.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for AuditLog entity.
 * Records security events, violations, and system actions.
 */
public class AuditLogDAO {

    public boolean logEvent(AuditLog log) {
        String sql = "INSERT INTO audit_logs (user_id, action, entity_type, entity_id, details, ip_address) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (log.getUserId() != null) stmt.setInt(1, log.getUserId());
            else stmt.setNull(1, Types.INTEGER);
            stmt.setString(2, log.getAction());
            stmt.setString(3, log.getEntityType());
            if (log.getEntityId() != null) stmt.setInt(4, log.getEntityId());
            else stmt.setNull(4, Types.INTEGER);
            stmt.setString(5, log.getDetails());
            stmt.setString(6, log.getIpAddress());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public List<AuditLog> getAllLogs() {
        String sql = "SELECT a.*, u.full_name as user_name FROM audit_logs a " +
                     "LEFT JOIN users u ON a.user_id = u.user_id ORDER BY a.logged_at DESC LIMIT 1000";
        List<AuditLog> logs = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) logs.add(mapResultSetToLog(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return logs;
    }

    public List<AuditLog> getLogsByUser(int userId) {
        String sql = "SELECT a.*, u.full_name as user_name FROM audit_logs a " +
                     "LEFT JOIN users u ON a.user_id = u.user_id WHERE a.user_id = ? ORDER BY a.logged_at DESC";
        List<AuditLog> logs = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) logs.add(mapResultSetToLog(rs));
            rs.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return logs;
    }

    public List<AuditLog> getViolationLogs() {
        String sql = "SELECT a.*, u.full_name as user_name FROM audit_logs a " +
                     "LEFT JOIN users u ON a.user_id = u.user_id " +
                     "WHERE a.action LIKE '%VIOLATION%' OR a.action LIKE '%TAB_SWITCH%' OR a.action LIKE '%MULTIPLE_LOGIN%' " +
                     "OR a.action LIKE '%IP_MISMATCH%' ORDER BY a.logged_at DESC";
        List<AuditLog> logs = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) logs.add(mapResultSetToLog(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return logs;
    }

    public List<AuditLog> getLogsByAction(String action) {
        String sql = "SELECT a.*, u.full_name as user_name FROM audit_logs a " +
                     "LEFT JOIN users u ON a.user_id = u.user_id WHERE a.action = ? ORDER BY a.logged_at DESC";
        List<AuditLog> logs = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, action);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) logs.add(mapResultSetToLog(rs));
            rs.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return logs;
    }

    private AuditLog mapResultSetToLog(ResultSet rs) throws SQLException {
        AuditLog log = new AuditLog();
        log.setLogId(rs.getInt("log_id"));
        int userId = rs.getInt("user_id");
        if (!rs.wasNull()) log.setUserId(userId);
        log.setAction(rs.getString("action"));
        log.setEntityType(rs.getString("entity_type"));
        int entityId = rs.getInt("entity_id");
        if (!rs.wasNull()) log.setEntityId(entityId);
        log.setDetails(rs.getString("details"));
        log.setIpAddress(rs.getString("ip_address"));
        Timestamp ts = rs.getTimestamp("logged_at");
        if (ts != null) log.setLoggedAt(ts.toLocalDateTime());
        log.setUserName(rs.getString("user_name"));
        return log;
    }
}

