package com.mitwpu.lca.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ExamAttempt POJO tracking a student's exam session.
 */
public class ExamAttempt implements Serializable {

    private static final long serialVersionUID = 1L;

    private int attemptId;
    private int studentId;
    private int examId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime submittedAt;
    private int totalMarksObtained;
    private String status; // STARTED, IN_PROGRESS, SUBMITTED, EVALUATED
    private String ipAddress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Transient fields for UI (not stored in DB)
    private String examTitle;
    private String studentName;

    public ExamAttempt() {}

    public ExamAttempt(int studentId, int examId, String ipAddress) {
        this.studentId = studentId;
        this.examId = examId;
        this.ipAddress = ipAddress;
        this.status = "STARTED";
        this.totalMarksObtained = 0;
    }

    // Getters and Setters
    public int getAttemptId() { return attemptId; }
    public void setAttemptId(int attemptId) { this.attemptId = attemptId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getExamId() { return examId; }
    public void setExamId(int examId) { this.examId = examId; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public int getTotalMarksObtained() { return totalMarksObtained; }
    public void setTotalMarksObtained(int totalMarksObtained) { this.totalMarksObtained = totalMarksObtained; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getExamTitle() { return examTitle; }
    public void setExamTitle(String examTitle) { this.examTitle = examTitle; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public boolean isSubmitted() {
        return "SUBMITTED".equals(this.status) || "EVALUATED".equals(this.status);
    }

    @Override
    public String toString() {
        return "ExamAttempt{" +
                "attemptId=" + attemptId +
                ", studentId=" + studentId +
                ", examId=" + examId +
                ", status='" + status + '\'' +
                ", totalMarksObtained=" + totalMarksObtained +
                '}';
    }
}

