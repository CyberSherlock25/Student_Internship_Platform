package com.mitwpu.lca.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Violation model class representing exam proctoring violations
 * Tracks security breaches during exam attempts
 */
public class Violation implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private int violationId;
    private int attemptId;
    private int studentId;
    private String violationType; // TAB_SWITCH, CAMERA_OFF, MIC_OFF, SCREEN_SHARE, FACE_NOT_DETECTED, MULTIPLE_FACES
    private String violationDescription;
    private String severity; // LOW, MEDIUM, HIGH
    private LocalDateTime violationTime;
    private String ipAddress;
    
    // Constructors
    public Violation() {
    }
    
    public Violation(int attemptId, int studentId, String violationType, 
                    String violationDescription, String severity, String ipAddress) {
        this.attemptId = attemptId;
        this.studentId = studentId;
        this.violationType = violationType;
        this.violationDescription = violationDescription;
        this.severity = severity;
        this.ipAddress = ipAddress;
        this.violationTime = LocalDateTime.now();
    }
    
    public Violation(int violationId, int attemptId, int studentId, String violationType,
                    String violationDescription, String severity, LocalDateTime violationTime, String ipAddress) {
        this.violationId = violationId;
        this.attemptId = attemptId;
        this.studentId = studentId;
        this.violationType = violationType;
        this.violationDescription = violationDescription;
        this.severity = severity;
        this.violationTime = violationTime;
        this.ipAddress = ipAddress;
    }
    
    // Getters and Setters
    public int getViolationId() {
        return violationId;
    }
    
    public void setViolationId(int violationId) {
        this.violationId = violationId;
    }
    
    public int getAttemptId() {
        return attemptId;
    }
    
    public void setAttemptId(int attemptId) {
        this.attemptId = attemptId;
    }
    
    public int getStudentId() {
        return studentId;
    }
    
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }
    
    public String getViolationType() {
        return violationType;
    }
    
    public void setViolationType(String violationType) {
        this.violationType = violationType;
    }
    
    public String getViolationDescription() {
        return violationDescription;
    }
    
    public void setViolationDescription(String violationDescription) {
        this.violationDescription = violationDescription;
    }
    
    public String getSeverity() {
        return severity;
    }
    
    public void setSeverity(String severity) {
        this.severity = severity;
    }
    
    public LocalDateTime getViolationTime() {
        return violationTime;
    }
    
    public void setViolationTime(LocalDateTime violationTime) {
        this.violationTime = violationTime;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    @Override
    public String toString() {
        return "Violation{" +
                "violationId=" + violationId +
                ", attemptId=" + attemptId +
                ", studentId=" + studentId +
                ", violationType='" + violationType + '\'' +
                ", severity='" + severity + '\'' +
                ", violationTime=" + violationTime +
                ", ipAddress='" + ipAddress + '\'' +
                '}';
    }
}
