package com.mitwpu.lca.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Exam POJO representing an online examination.
 * Managed by Admin, taken by shortlisted Students.
 */
public class Exam implements Serializable {

    private static final long serialVersionUID = 1L;

    private int examId;
    private String examTitle;
    private String examDescription;
    private int totalMarks;
    private int durationMinutes;
    private int passingMarks;
    private LocalDate examDate;
    private LocalTime examStartTime;
    private LocalTime examEndTime;
    private String status; // DRAFT, SCHEDULED, ONGOING, COMPLETED, ARCHIVED
    private int createdBy; // user_id of admin
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Exam() {}

    public Exam(String examTitle, String examDescription, int totalMarks,
                int durationMinutes, int passingMarks, LocalDate examDate,
                LocalTime examStartTime, LocalTime examEndTime, int createdBy) {
        this.examTitle = examTitle;
        this.examDescription = examDescription;
        this.totalMarks = totalMarks;
        this.durationMinutes = durationMinutes;
        this.passingMarks = passingMarks;
        this.examDate = examDate;
        this.examStartTime = examStartTime;
        this.examEndTime = examEndTime;
        this.createdBy = createdBy;
        this.status = "DRAFT";
    }

    // Getters and Setters
    public int getExamId() { return examId; }
    public void setExamId(int examId) { this.examId = examId; }

    public String getExamTitle() { return examTitle; }
    public void setExamTitle(String examTitle) { this.examTitle = examTitle; }

    public String getExamDescription() { return examDescription; }
    public void setExamDescription(String examDescription) { this.examDescription = examDescription; }

    public int getTotalMarks() { return totalMarks; }
    public void setTotalMarks(int totalMarks) { this.totalMarks = totalMarks; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public int getPassingMarks() { return passingMarks; }
    public void setPassingMarks(int passingMarks) { this.passingMarks = passingMarks; }

    public LocalDate getExamDate() { return examDate; }
    public void setExamDate(LocalDate examDate) { this.examDate = examDate; }

    public LocalTime getExamStartTime() { return examStartTime; }
    public void setExamStartTime(LocalTime examStartTime) { this.examStartTime = examStartTime; }

    public LocalTime getExamEndTime() { return examEndTime; }
    public void setExamEndTime(LocalTime examEndTime) { this.examEndTime = examEndTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}


