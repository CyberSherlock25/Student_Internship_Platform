package com.mitwpu.lca.dao;

import com.mitwpu.lca.model.CompanyAdmin;
import com.mitwpu.lca.model.Company;
import com.mitwpu.lca.model.User;
import com.mitwpu.lca.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Company Admin operations
 * Handles CRUD operations for company admin assignments
 */
public class CompanyAdminDAO {
    
    /**
     * Assign a user as company admin
     * @param companyAdminId Admin ID
     * @param userId User ID
     * @param companyId Company ID
     * @param designation Admin designation
     * @return true if successfully assigned
     */
    public boolean assignCompanyAdmin(int userId, int companyId, String designation) {
        String sql = "INSERT INTO company_admins (user_id, company_id, designation) " +
                     "VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, companyId);
            stmt.setString(3, designation);
            
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error assigning company admin: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Get company admin by ID
     * @param adminId Admin ID
     * @return CompanyAdmin object or null
     */
    public CompanyAdmin getCompanyAdminById(int adminId) {
        String sql = "SELECT admin_id, user_id, company_id, designation, assigned_at " +
                     "FROM company_admins WHERE admin_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, adminId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToCompanyAdmin(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting company admin by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get company admin for a user
     * @param userId User ID
     * @return CompanyAdmin object or null
     */
    public CompanyAdmin getCompanyAdminByUserId(int userId) {
        String sql = "SELECT admin_id, user_id, company_id, designation, assigned_at " +
                     "FROM company_admins WHERE user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToCompanyAdmin(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting company admin by user ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get all admins for a company
     * @param companyId Company ID
     * @return List of company admins
     */
    public List<CompanyAdmin> getAdminsByCompany(int companyId) {
        String sql = "SELECT ca.admin_id, ca.user_id, ca.company_id, ca.designation, ca.assigned_at, " +
                     "u.full_name, u.email FROM company_admins ca " +
                     "JOIN users u ON ca.user_id = u.user_id WHERE ca.company_id = ?";
        
        List<CompanyAdmin> admins = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, companyId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                CompanyAdmin admin = mapResultSetToCompanyAdmin(rs);
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                admin.setUser(user);
                admins.add(admin);
            }
        } catch (SQLException e) {
            System.err.println("Error getting admins by company: " + e.getMessage());
            e.printStackTrace();
        }
        return admins;
    }
    
    /**
     * Remove admin assignment
     * @param adminId Admin ID
     * @return true if successfully removed
     */
    public boolean removeCompanyAdmin(int adminId) {
        String sql = "DELETE FROM company_admins WHERE admin_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, adminId);
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error removing company admin: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Check if user is admin of company
     * @param userId User ID
     * @param companyId Company ID
     * @return true if user is admin of company
     */
    public boolean isCompanyAdmin(int userId, int companyId) {
        String sql = "SELECT COUNT(*) FROM company_admins WHERE user_id = ? AND company_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, companyId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking company admin: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Map ResultSet to CompanyAdmin object
     */
    private CompanyAdmin mapResultSetToCompanyAdmin(ResultSet rs) throws SQLException {
        CompanyAdmin admin = new CompanyAdmin();
        admin.setAdminId(rs.getInt("admin_id"));
        admin.setUserId(rs.getInt("user_id"));
        admin.setCompanyId(rs.getInt("company_id"));
        admin.setDesignation(rs.getString("designation"));
        admin.setAssignedAt(rs.getTimestamp("assigned_at").toLocalDateTime());
        return admin;
    }
}
