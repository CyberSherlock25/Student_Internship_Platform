package com.mitwpu.lca.dao;

import com.mitwpu.lca.model.Student;
import com.mitwpu.lca.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Student operations
 * Handles CRUD operations and student profile management
 */
public class StudentDAO {
    
    /**
     * Get student by user ID
     * @param userId User ID
     * @return Student object or null if not found
     */
    public Student getStudentByUserId(int userId) {
        String sql = "SELECT student_id, user_id, roll_number, department_code, " +
                     "department_name, cgpa, semester, date_of_birth, address, city, state, " +
                     "pincode, enrolled_at, updated_at FROM students WHERE user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToStudent(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting student by user ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get student by student ID
     * @param studentId Student ID
     * @return Student object or null if not found
     */
    public Student getStudentById(int studentId) {
        String sql = "SELECT student_id, user_id, roll_number, department_code, " +
                     "department_name, cgpa, semester, date_of_birth, address, city, state, " +
                     "pincode, enrolled_at, updated_at FROM students WHERE student_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToStudent(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting student by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get all active students
     * @return List of active students
     */
    public List<Student> getAllActiveStudents() {
        String sql = "SELECT student_id, user_id, roll_number, department_code, " +
                     "department_name, cgpa, semester, date_of_birth, address, city, state, " +
                     "pincode, enrolled_at, updated_at FROM students ORDER BY enrolled_at DESC";
        
        List<Student> students = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all active students: " + e.getMessage());
            e.printStackTrace();
        }
        return students;
    }
    
    /**
     * Get all students by department
     * @param departmentCode Department code
     * @return List of students from specified department
     */
    public List<Student> getStudentsByDepartment(String departmentCode) {
        String sql = "SELECT student_id, user_id, roll_number, department_code, " +
                     "department_name, cgpa, semester, date_of_birth, address, city, state, " +
                     "pincode, enrolled_at, updated_at FROM students WHERE department_code = ? " +
                     "ORDER BY cgpa DESC";
        
        List<Student> students = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, departmentCode);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting students by department: " + e.getMessage());
            e.printStackTrace();
        }
        return students;
    }
    
    /**
     * Create new student profile
     * @param student Student object to create
     * @return true if student created successfully, false otherwise
     */
    public boolean createStudent(Student student) {
        String sql = "INSERT INTO students (user_id, roll_number, department_code, " +
                     "department_name, cgpa, semester, date_of_birth, address, city, state, pincode) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, student.getUserId());
            stmt.setString(2, student.getRollNumber());
            stmt.setString(3, student.getDepartmentCode());
            stmt.setString(4, student.getDepartmentName());
            stmt.setDouble(5, student.getCgpa());
            stmt.setInt(6, student.getSemester());
            stmt.setDate(7, java.sql.Date.valueOf(student.getDateOfBirth()));
            stmt.setString(8, student.getAddress());
            stmt.setString(9, student.getCity());
            stmt.setString(10, student.getState());
            stmt.setString(11, student.getPincode());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error creating student: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Update student profile
     * @param student Student object with updated values
     * @return true if student updated successfully, false otherwise
     */
    public boolean updateStudent(Student student) {
        String sql = "UPDATE students SET roll_number = ?, department_code = ?, " +
                     "department_name = ?, cgpa = ?, semester = ?, date_of_birth = ?, " +
                     "address = ?, city = ?, state = ?, pincode = ?, updated_at = CURRENT_TIMESTAMP " +
                     "WHERE student_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, student.getRollNumber());
            stmt.setString(2, student.getDepartmentCode());
            stmt.setString(3, student.getDepartmentName());
            stmt.setDouble(4, student.getCgpa());
            stmt.setInt(5, student.getSemester());
            stmt.setDate(6, java.sql.Date.valueOf(student.getDateOfBirth()));
            stmt.setString(7, student.getAddress());
            stmt.setString(8, student.getCity());
            stmt.setString(9, student.getState());
            stmt.setString(10, student.getPincode());
            stmt.setInt(11, student.getStudentId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating student: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Check if student is eligible for internship
     * @param studentId Student ID
     * @param minimumCgpa Minimum CGPA required
     * @return true if student meets eligibility criteria
     */
    public boolean isEligibleForInternship(int studentId, double minimumCgpa) {
        String sql = "SELECT cgpa FROM students WHERE student_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                double studentCgpa = rs.getDouble("cgpa");
                return studentCgpa >= minimumCgpa;
            }
        } catch (SQLException e) {
            System.err.println("Error checking student eligibility: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Get students by CGPA range
     * @param minCgpa Minimum CGPA
     * @param maxCgpa Maximum CGPA
     * @return List of students within CGPA range
     */
    public List<Student> getStudentsByCgpaRange(double minCgpa, double maxCgpa) {
        String sql = "SELECT student_id, user_id, roll_number, department_code, " +
                     "department_name, cgpa, semester, date_of_birth, address, city, state, " +
                     "pincode, enrolled_at, updated_at FROM students WHERE cgpa >= ? AND cgpa <= ? " +
                     "ORDER BY cgpa DESC";
        
        List<Student> students = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, minCgpa);
            stmt.setDouble(2, maxCgpa);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting students by CGPA range: " + e.getMessage());
            e.printStackTrace();
        }
        return students;
    }
    
    /**
     * Delete student profile
     * @param studentId Student ID
     * @return true if student deleted successfully, false otherwise
     */
    public boolean deleteStudent(int studentId) {
        String sql = "DELETE FROM students WHERE student_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, studentId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting student: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Helper method to map ResultSet to Student object
     */
    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setStudentId(rs.getInt("student_id"));
        student.setUserId(rs.getInt("user_id"));
        student.setRollNumber(rs.getString("roll_number"));
        student.setDepartmentCode(rs.getString("department_code"));
        student.setDepartmentName(rs.getString("department_name"));
        student.setCgpa(rs.getDouble("cgpa"));
        student.setSemester(rs.getInt("semester"));
        
        java.sql.Date dobDate = rs.getDate("date_of_birth");
        if (dobDate != null) student.setDateOfBirth(dobDate.toLocalDate());
        
        student.setAddress(rs.getString("address"));
        student.setCity(rs.getString("city"));
        student.setState(rs.getString("state"));
        student.setPincode(rs.getString("pincode"));
        
        java.sql.Timestamp enrolledTs = rs.getTimestamp("enrolled_at");
        if (enrolledTs != null) student.setEnrolledAt(enrolledTs.toLocalDateTime());
        java.sql.Timestamp updatedTs = rs.getTimestamp("updated_at");
        if (updatedTs != null) student.setUpdatedAt(updatedTs.toLocalDateTime());
        
        return student;
    }
}
