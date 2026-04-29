package com.mitwpu.lca.dao;

import com.mitwpu.lca.model.SessionTracking;
import com.mitwpu.lca.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for SessionTracking entity.
 * Prevents multiple concurrent logins and tracks session security.
 */
public class SessionTrackingDAO {

    public boolean createSession(SessionTracking session) {
        String sql = "INSERT INTO session_tracking (user_id, session_id, ip_address, user_agent, status) " +
                     "VALUES (?, ?, ?, ?, 'ACTIVE')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, session.getUserId());
            stmt.setString(2, session.getSessionId());
            stmt.setString(3, session.getIpAddress());
            stmt.setString(4, session.getUserAgent());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public SessionTracking getActiveSessionByUser(int userId) {
        String sql = "SELECT st.*, u.full_name as user_name, u.email as user_email FROM session_tracking st " +
                     "JOIN users u ON st.user_id = u.user_id WHERE st.user_id = ? AND st.status = 'ACTIVE' " +
                     "ORDER BY st.login_time DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) { SessionTracking s = mapResultSetToSession(rs); rs.close(); return s; }
            rs.close();
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean hasActiveSession(int userId) {
        return getActiveSessionByUser(userId) != null;
    }

    public boolean invalidateUserSessions(int userId) {
        String sql = "UPDATE session_tracking SET status = 'FORCE_LOGOUT', logout_time = CURRENT_TIMESTAMP WHERE user_id = ? AND status = 'ACTIVE'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() >= 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean invalidateSession(String sessionId) {
        String sql = "UPDATE session_tracking SET status = 'EXPIRED', logout_time = CURRENT_TIMESTAMP WHERE session_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean updateLastActivity(String sessionId) {
        String sql = "UPDATE session_tracking SET last_activity = CURRENT_TIMESTAMP WHERE session_id = ? AND status = 'ACTIVE'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public List<SessionTracking> getAllActiveSessions() {
        String sql = "SELECT st.*, u.full_name as user_name, u.email as user_email FROM session_tracking st " +
                     "JOIN users u ON st.user_id = u.user_id WHERE st.status = 'ACTIVE' ORDER BY st.login_time DESC";
        List<SessionTracking> sessions = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) sessions.add(mapResultSetToSession(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return sessions;
    }

    private SessionTracking mapResultSetToSession(ResultSet rs) throws SQLException {
        SessionTracking s = new SessionTracking();
        s.setTrackId(rs.getInt("track_id"));
        s.setUserId(rs.getInt("user_id"));
        s.setSessionId(rs.getString("session_id"));
        s.setIpAddress(rs.getString("ip_address"));
        s.setUserAgent(rs.getString("user_agent"));
        Timestamp login = rs.getTimestamp("login_time");
        if (login != null) s.setLoginTime(login.toLocalDateTime());
        Timestamp last = rs.getTimestamp("last_activity");
        if (last != null) s.setLastActivity(last.toLocalDateTime());
        Timestamp logout = rs.getTimestamp("logout_time");
        if (logout != null) s.setLogoutTime(logout.toLocalDateTime());
        s.setStatus(rs.getString("status"));
        s.setUserName(rs.getString("user_name"));
        s.setUserEmail(rs.getString("user_email"));
        return s;
    }
}

