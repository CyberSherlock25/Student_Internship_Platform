package com.mitwpu.lca.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Student POJO class representing a student profile.
 * Extends user information with academic details.
 */
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    private int studentId;
    private int userId; // Foreign key to User table
    private String rollNumber;
    private String departmentCode;
    private String departmentName;
    private double cgpa;
    private int semester;
    private LocalDate dateOfBirth;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private LocalDateTime enrolledAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Student() {
    }

    public Student(int userId, String rollNumber, String departmentCode,
                   String departmentName, double cgpa, int semester) {
        this.userId = userId;
        this.rollNumber = rollNumber;
        this.departmentCode = departmentCode;
        this.departmentName = departmentName;
        this.cgpa = cgpa;
        this.semester = semester;
    }

    public Student(int studentId, int userId, String rollNumber, String departmentCode,
                   String departmentName, double cgpa, int semester, LocalDate dateOfBirth,
                   String address, String city, String state, String pincode,
                   LocalDateTime enrolledAt, LocalDateTime updatedAt) {
        this.studentId = studentId;
        this.userId = userId;
        this.rollNumber = rollNumber;
        this.departmentCode = departmentCode;
        this.departmentName = departmentName;
        this.cgpa = cgpa;
        this.semester = semester;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.enrolledAt = enrolledAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public double getCgpa() {
        return cgpa;
    }

    public void setCgpa(double cgpa) {
        this.cgpa = cgpa;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(LocalDateTime enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Helper methods
    public boolean isEligible(double requiredCgpa) {
        return this.cgpa >= requiredCgpa;
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId=" + studentId +
                ", rollNumber='" + rollNumber + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", cgpa=" + cgpa +
                ", semester=" + semester +
                '}';
    }
}
