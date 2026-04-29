package com.mitwpu.lca.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * CompanyAdmin model class
 * Links a user to a company with admin privileges for that company
 */
public class CompanyAdmin implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private int adminId;
    private int userId;
    private int companyId;
    private String designation;
    private LocalDateTime assignedAt;
    
    // For convenience: store company and user details
    private Company company;
    private User user;
    
    // Constructors
    public CompanyAdmin() {
    }
    
    public CompanyAdmin(int userId, int companyId, String designation) {
        this.userId = userId;
        this.companyId = companyId;
        this.designation = designation;
    }
    
    public CompanyAdmin(int adminId, int userId, int companyId, String designation, LocalDateTime assignedAt) {
        this.adminId = adminId;
        this.userId = userId;
        this.companyId = companyId;
        this.designation = designation;
        this.assignedAt = assignedAt;
    }
    
    // Getters and Setters
    public int getAdminId() {
        return adminId;
    }
    
    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public int getCompanyId() {
        return companyId;
    }
    
    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }
    
    public String getDesignation() {
        return designation;
    }
    
    public void setDesignation(String designation) {
        this.designation = designation;
    }
    
    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }
    
    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }
    
    public Company getCompany() {
        return company;
    }
    
    public void setCompany(Company company) {
        this.company = company;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    @Override
    public String toString() {
        return "CompanyAdmin{" +
                "adminId=" + adminId +
                ", userId=" + userId +
                ", companyId=" + companyId +
                ", designation='" + designation + '\'' +
                ", assignedAt=" + assignedAt +
                '}';
    }
}
