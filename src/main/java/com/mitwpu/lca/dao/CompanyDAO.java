package com.mitwpu.lca.dao;

import com.mitwpu.lca.model.Company;
import com.mitwpu.lca.util.DBConnection;
import java.sql.*;
import java.util.*;

/**
 * Data Access Object for Company entity
 * Handles CRUD operations for companies
 */
public class CompanyDAO {
    
    /**
     * Get all companies
     * @return List of all companies
     */
    public List<Company> getAllCompanies() {
        String sql = "SELECT company_id, company_name, company_email, company_phone, website, industry, " +
                     "headquarters, city, state, country, company_size, description, status, registered_at, updated_at " +
                     "FROM companies ORDER BY registered_at DESC";
        
        List<Company> companies = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Company company = mapResultSetToCompany(rs);
                companies.add(company);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all companies: " + e.getMessage());
            e.printStackTrace();
        }
        return companies;
    }
    
    /**
     * Get all active companies
     * @return List of active companies
     */
    public List<Company> getActiveCompanies() {
        String sql = "SELECT company_id, company_name, company_email, company_phone, website, industry, " +
                     "headquarters, city, state, country, company_size, description, status, registered_at, updated_at " +
                     "FROM companies WHERE status = 'ACTIVE' ORDER BY company_name ASC";
        
        List<Company> companies = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Company company = mapResultSetToCompany(rs);
                companies.add(company);
            }
        } catch (SQLException e) {
            System.err.println("Error getting active companies: " + e.getMessage());
            e.printStackTrace();
        }
        return companies;
    }
    
    /**
     * Get company by ID
     * @param companyId Company ID
     * @return Company object or null if not found
     */
    public Company getCompanyById(int companyId) {
        String sql = "SELECT company_id, company_name, company_email, company_phone, website, industry, " +
                     "headquarters, city, state, country, company_size, description, status, registered_at, updated_at " +
                     "FROM companies WHERE company_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, companyId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToCompany(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting company by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Create new company
     * @param company Company object to create
     * @return true if company created successfully, false otherwise
     */
    public boolean createCompany(Company company) {
        String sql = "INSERT INTO companies (company_name, company_email, company_phone, website, industry, " +
                     "headquarters, city, state, country, company_size, description, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, company.getCompanyName());
            stmt.setString(2, company.getCompanyEmail());
            stmt.setString(3, company.getCompanyPhone());
            stmt.setString(4, company.getWebsite());
            stmt.setString(5, company.getIndustry());
            stmt.setString(6, company.getHeadquarters());
            stmt.setString(7, company.getCity());
            stmt.setString(8, company.getState());
            stmt.setString(9, company.getCountry());
            stmt.setString(10, company.getCompanySize());
            stmt.setString(11, company.getDescription());
            stmt.setString(12, company.getStatus());
            
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.err.println("Error creating company: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Update company
     * @param company Company object with updated values
     * @return true if company updated successfully, false otherwise
     */
    public boolean updateCompany(Company company) {
        String sql = "UPDATE companies SET company_name = ?, company_email = ?, company_phone = ?, website = ?, " +
                     "industry = ?, headquarters = ?, city = ?, state = ?, country = ?, company_size = ?, " +
                     "description = ?, status = ?, updated_at = CURRENT_TIMESTAMP WHERE company_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, company.getCompanyName());
            stmt.setString(2, company.getCompanyEmail());
            stmt.setString(3, company.getCompanyPhone());
            stmt.setString(4, company.getWebsite());
            stmt.setString(5, company.getIndustry());
            stmt.setString(6, company.getHeadquarters());
            stmt.setString(7, company.getCity());
            stmt.setString(8, company.getState());
            stmt.setString(9, company.getCountry());
            stmt.setString(10, company.getCompanySize());
            stmt.setString(11, company.getDescription());
            stmt.setString(12, company.getStatus());
            stmt.setInt(13, company.getCompanyId());
            
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Error updating company: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Delete company by ID
     * @param companyId Company ID
     * @return true if company deleted successfully, false otherwise
     */
    public boolean deleteCompany(int companyId) {
        String sql = "DELETE FROM companies WHERE company_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, companyId);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting company: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Helper method to map ResultSet to Company object
     */
    private Company mapResultSetToCompany(ResultSet rs) throws SQLException {
        Company company = new Company();
        company.setCompanyId(rs.getInt("company_id"));
        company.setCompanyName(rs.getString("company_name"));
        company.setCompanyEmail(rs.getString("company_email"));
        company.setCompanyPhone(rs.getString("company_phone"));
        company.setWebsite(rs.getString("website"));
        company.setIndustry(rs.getString("industry"));
        company.setHeadquarters(rs.getString("headquarters"));
        company.setCity(rs.getString("city"));
        company.setState(rs.getString("state"));
        company.setCountry(rs.getString("country"));
        company.setCompanySize(rs.getString("company_size"));
        company.setDescription(rs.getString("description"));
        company.setStatus(rs.getString("status"));
        java.sql.Timestamp registeredTs = rs.getTimestamp("registered_at");
        if (registeredTs != null) company.setRegisteredAt(registeredTs.toLocalDateTime());
        java.sql.Timestamp updatedTs = rs.getTimestamp("updated_at");
        if (updatedTs != null) company.setUpdatedAt(updatedTs.toLocalDateTime());
        return company;
    }
}
