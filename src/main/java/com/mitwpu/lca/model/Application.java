package com.mitwpu.lca.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Application POJO class
 * Represents a student's application for an internship position
 */
public class Application implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private int applicationId;
    private int studentId;
    private int internshipId;
    private LocalDate appliedDate;
    private String status; // PENDING, SHORTLISTED, REJECTED, ACCEPTED
    private String coverLetter;
    private Double rating; // Rating given by company (1-5)
    private String feedback;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public Application() {
        this.status = "PENDING";
    }
    
    public Application(int studentId, int internshipId, LocalDate appliedDate, String coverLetter) {
        this.studentId = studentId;
        this.internshipId = internshipId;
        this.appliedDate = appliedDate;
        this.coverLetter = coverLetter;
        this.status = "PENDING";
    }
    
    public Application(int applicationId, int studentId, int internshipId, LocalDate appliedDate,
                      String status, String coverLetter, Double rating, String feedback) {
        this.applicationId = applicationId;
        this.studentId = studentId;
        this.internshipId = internshipId;
        this.appliedDate = appliedDate;
        this.status = status;
        this.coverLetter = coverLetter;
        this.rating = rating;
        this.feedback = feedback;
    }
    
    // Getters and Setters
    public int getApplicationId() {
        return applicationId;
    }
    
    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }
    
    public int getStudentId() {
        return studentId;
    }
    
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }
    
    public int getInternshipId() {
        return internshipId;
    }
    
    public void setInternshipId(int internshipId) {
        this.internshipId = internshipId;
    }
    
    public LocalDate getAppliedDate() {
        return appliedDate;
    }
    
    public void setAppliedDate(LocalDate appliedDate) {
        this.appliedDate = appliedDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getCoverLetter() {
        return coverLetter;
    }
    
    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }
    
    public Double getRating() {
        return rating;
    }
    
    public void setRating(Double rating) {
        this.rating = rating;
    }
    
    public String getFeedback() {
        return feedback;
    }
    
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Helper methods
    public boolean isPending() {
        return "PENDING".equals(this.status);
    }
    
    public boolean isShortlisted() {
        return "SHORTLISTED".equals(this.status);
    }
    
    public boolean isRejected() {
        return "REJECTED".equals(this.status);
    }
    
    public boolean isAccepted() {
        return "ACCEPTED".equals(this.status);
    }
    
    public boolean hasRating() {
        return rating != null && rating > 0;
    }
    
    @Override
    public String toString() {
        return "Application{" +
                "applicationId=" + applicationId +
                ", studentId=" + studentId +
                ", internshipId=" + internshipId +
                ", appliedDate=" + appliedDate +
                ", status='" + status + '\'' +
                ", rating=" + rating +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
