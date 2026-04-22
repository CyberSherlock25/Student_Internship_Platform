package com.mitwpu.lca.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Internship POJO class representing an internship posting.
 */
public class Internship implements Serializable {

    private static final long serialVersionUID = 1L;

    private int internshipId;
    private int companyId;
    private String jobTitle;
    private String jobDescription;
    private String jobLocation;
    private double stipendAmount;
    private String stipendType; // "Monthly", "One-time"
    private int durationMonths;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate applicationDeadline;
    private double minimumCgpa;
    private String requiredSkills;
    private int totalPositions;
    private int filledPositions;
    private String status; // "OPEN", "CLOSED", "FILLED"
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Internship() {
    }

    public Internship(int companyId, String jobTitle, String jobDescription,
                      String jobLocation, double stipendAmount, int durationMonths,
                      LocalDate startDate, LocalDate applicationDeadline, double minimumCgpa) {
        this.companyId = companyId;
        this.jobTitle = jobTitle;
        this.jobDescription = jobDescription;
        this.jobLocation = jobLocation;
        this.stipendAmount = stipendAmount;
        this.durationMonths = durationMonths;
        this.startDate = startDate;
        this.applicationDeadline = applicationDeadline;
        this.minimumCgpa = minimumCgpa;
        this.status = "OPEN";
        this.filledPositions = 0;
    }

    public Internship(int internshipId, int companyId, String jobTitle, String jobDescription,
                      String jobLocation, double stipendAmount, String stipendType,
                      int durationMonths, LocalDate startDate, LocalDate endDate,
                      LocalDate applicationDeadline, double minimumCgpa, String requiredSkills,
                      int totalPositions, int filledPositions, String status,
                      LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.internshipId = internshipId;
        this.companyId = companyId;
        this.jobTitle = jobTitle;
        this.jobDescription = jobDescription;
        this.jobLocation = jobLocation;
        this.stipendAmount = stipendAmount;
        this.stipendType = stipendType;
        this.durationMonths = durationMonths;
        this.startDate = startDate;
        this.endDate = endDate;
        this.applicationDeadline = applicationDeadline;
        this.minimumCgpa = minimumCgpa;
        this.requiredSkills = requiredSkills;
        this.totalPositions = totalPositions;
        this.filledPositions = filledPositions;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getInternshipId() {
        return internshipId;
    }

    public void setInternshipId(int internshipId) {
        this.internshipId = internshipId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getJobLocation() {
        return jobLocation;
    }

    public void setJobLocation(String jobLocation) {
        this.jobLocation = jobLocation;
    }

    public double getStipendAmount() {
        return stipendAmount;
    }

    public void setStipendAmount(double stipendAmount) {
        this.stipendAmount = stipendAmount;
    }

    public String getStipendType() {
        return stipendType;
    }

    public void setStipendType(String stipendType) {
        this.stipendType = stipendType;
    }

    public int getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(int durationMonths) {
        this.durationMonths = durationMonths;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(LocalDate applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }

    public double getMinimumCgpa() {
        return minimumCgpa;
    }

    public void setMinimumCgpa(double minimumCgpa) {
        this.minimumCgpa = minimumCgpa;
    }

    public String getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(String requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public int getTotalPositions() {
        return totalPositions;
    }

    public void setTotalPositions(int totalPositions) {
        this.totalPositions = totalPositions;
    }

    public int getFilledPositions() {
        return filledPositions;
    }

    public void setFilledPositions(int filledPositions) {
        this.filledPositions = filledPositions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
    public boolean isOpen() {
        return "OPEN".equals(this.status);
    }

    public boolean hasAvailablePositions() {
        return filledPositions < totalPositions;
    }

    public boolean isDeadlinePassed(LocalDate currentDate) {
        return currentDate.isAfter(applicationDeadline);
    }

    public int getAvailablePositions() {
        return totalPositions - filledPositions;
    }

    @Override
    public String toString() {
        return "Internship{" +
                "internshipId=" + internshipId +
                ", jobTitle='" + jobTitle + '\'' +
                ", jobLocation='" + jobLocation + '\'' +
                ", minimumCgpa=" + minimumCgpa +
                ", status='" + status + '\'' +
                '}';
    }
}
