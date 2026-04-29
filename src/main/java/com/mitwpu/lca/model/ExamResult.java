package com.mitwpu.lca.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ExamResult model class
 * Represents the result of an exam attempt with scoring and violation data
 */
public class ExamResult implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private int resultId;
    private int attemptId;
    private int studentId;
    private int examId;
    private int totalMarks;
    private int obtainedMarks;
    private double percentage;
    private boolean passed;
    private int totalViolations;
    private String violationSeverity; // LOW, MEDIUM, HIGH
    private String resultStatus; // PENDING, EVALUATED, PUBLISHED
    private int reviewedBy; // User ID who reviewed
    private LocalDateTime reviewedAt;
    
    // For convenience
    private String studentName;
    private String examTitle;
    private String reviewerName;
    
    // Constructors
    public ExamResult() {
    }
    
    public ExamResult(int attemptId, int studentId, int examId, int totalMarks, 
                     int obtainedMarks, int totalViolations) {
        this.attemptId = attemptId;
        this.studentId = studentId;
        this.examId = examId;
        this.totalMarks = totalMarks;
        this.obtainedMarks = obtainedMarks;
        this.percentage = (obtainedMarks * 100.0) / totalMarks;
        this.totalViolations = totalViolations;
        this.resultStatus = "PENDING";
    }
    
    public ExamResult(int resultId, int attemptId, int studentId, int examId,
                     int totalMarks, int obtainedMarks, double percentage, boolean passed,
                     int totalViolations, String violationSeverity, String resultStatus,
                     int reviewedBy, LocalDateTime reviewedAt) {
        this.resultId = resultId;
        this.attemptId = attemptId;
        this.studentId = studentId;
        this.examId = examId;
        this.totalMarks = totalMarks;
        this.obtainedMarks = obtainedMarks;
        this.percentage = percentage;
        this.passed = passed;
        this.totalViolations = totalViolations;
        this.violationSeverity = violationSeverity;
        this.resultStatus = resultStatus;
        this.reviewedBy = reviewedBy;
        this.reviewedAt = reviewedAt;
    }
    
    // Getters and Setters
    public int getResultId() {
        return resultId;
    }
    
    public void setResultId(int resultId) {
        this.resultId = resultId;
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
    
    public int getExamId() {
        return examId;
    }
    
    public void setExamId(int examId) {
        this.examId = examId;
    }
    
    public int getTotalMarks() {
        return totalMarks;
    }
    
    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }
    
    public int getObtainedMarks() {
        return obtainedMarks;
    }
    
    public void setObtainedMarks(int obtainedMarks) {
        this.obtainedMarks = obtainedMarks;
    }
    
    public double getPercentage() {
        return percentage;
    }
    
    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
    
    public boolean isPassed() {
        return passed;
    }
    
    public void setPassed(boolean passed) {
        this.passed = passed;
    }
    
    public int getTotalViolations() {
        return totalViolations;
    }
    
    public void setTotalViolations(int totalViolations) {
        this.totalViolations = totalViolations;
    }
    
    public String getViolationSeverity() {
        return violationSeverity;
    }
    
    public void setViolationSeverity(String violationSeverity) {
        this.violationSeverity = violationSeverity;
    }
    
    public String getResultStatus() {
        return resultStatus;
    }
    
    public void setResultStatus(String resultStatus) {
        this.resultStatus = resultStatus;
    }
    
    public int getReviewedBy() {
        return reviewedBy;
    }
    
    public void setReviewedBy(int reviewedBy) {
        this.reviewedBy = reviewedBy;
    }
    
    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }
    
    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
    
    public String getStudentName() {
        return studentName;
    }
    
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
    
    public String getExamTitle() {
        return examTitle;
    }
    
    public void setExamTitle(String examTitle) {
        this.examTitle = examTitle;
    }
    
    public String getReviewerName() {
        return reviewerName;
    }
    
    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }
    
    @Override
    public String toString() {
        return "ExamResult{" +
                "resultId=" + resultId +
                ", studentId=" + studentId +
                ", examId=" + examId +
                ", obtainedMarks=" + obtainedMarks +
                ", totalMarks=" + totalMarks +
                ", percentage=" + percentage +
                ", totalViolations=" + totalViolations +
                ", resultStatus='" + resultStatus + '\'' +
                '}';
    }
}
