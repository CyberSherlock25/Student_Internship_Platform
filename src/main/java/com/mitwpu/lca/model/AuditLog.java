package com.mitwpu.lca.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AuditLog POJO for tracking system events and user actions.
 * Used for security monitoring and compliance.
 */
public class AuditLog implements Serializable {

    private static final long serialVersionUID = 1L;

    private int logId;
    private Integer userId; // nullable
    private String action;
    private String entityType; // e.g., EXAM, APPLICATION, USER
    private Integer entityId;
    private String details;
    private String ipAddress;
    private LocalDateTime loggedAt;

    // Transient
    private String userName;

    public AuditLog() {}

    public AuditLog(Integer userId, String action, String entityType,
                    Integer entityId, String details, String ipAddress) {
        this.userId = userId;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.details = details;
        this.ipAddress = ipAddress;
    }

    // Getters and Setters
    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public Integer getEntityId() { return entityId; }
    public void setEntityId(Integer entityId) { this.entityId = entityId; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public LocalDateTime getLoggedAt() { return loggedAt; }
    public void setLoggedAt(LocalDateTime loggedAt) { this.loggedAt = loggedAt; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    @Override
    public String toString() {
        return "AuditLog{" +
                "logId=" + logId +
                ", userId=" + userId +
                ", action='" + action + '\'' +
                ", entityType='" + entityType + '\'' +
                ", loggedAt=" + loggedAt +
                '}';
    }
}

