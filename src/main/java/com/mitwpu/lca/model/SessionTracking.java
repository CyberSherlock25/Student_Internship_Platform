package com.mitwpu.lca.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * SessionTracking POJO for monitoring active user sessions.
 * Prevents multiple concurrent logins and tracks session security.
 */
public class SessionTracking implements Serializable {

    private static final long serialVersionUID = 1L;

    private int trackId;
    private int userId;
    private String sessionId;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime loginTime;
    private LocalDateTime lastActivity;
    private LocalDateTime logoutTime;
    private String status; // ACTIVE, EXPIRED, FORCE_LOGOUT

    // Transient
    private String userName;
    private String userEmail;

    public SessionTracking() {}

    public SessionTracking(int userId, String sessionId, String ipAddress, String userAgent) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.status = "ACTIVE";
    }

    // Getters and Setters
    public int getTrackId() { return trackId; }
    public void setTrackId(int trackId) { this.trackId = trackId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public LocalDateTime getLoginTime() { return loginTime; }
    public void setLoginTime(LocalDateTime loginTime) { this.loginTime = loginTime; }

    public LocalDateTime getLastActivity() { return lastActivity; }
    public void setLastActivity(LocalDateTime lastActivity) { this.lastActivity = lastActivity; }

    public LocalDateTime getLogoutTime() { return logoutTime; }
    public void setLogoutTime(LocalDateTime logoutTime) { this.logoutTime = logoutTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public boolean isActive() {
        return "ACTIVE".equals(this.status);
    }

    @Override
    public String toString() {
        return "SessionTracking{" +
                "trackId=" + trackId +
                ", userId=" + userId +
                ", sessionId='" + sessionId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

