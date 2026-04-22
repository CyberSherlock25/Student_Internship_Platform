-- Internship & Examination Management System Database Schema
-- Execute this script to create the complete database structure

-- Create the database
CREATE DATABASE IF NOT EXISTS internship_exam_system;
USE internship_exam_system;

-- ============================================
-- USERS TABLE (Base user information)
-- ============================================
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'STUDENT') NOT NULL DEFAULT 'STUDENT',
    phone_number VARCHAR(15),
    status ENUM('ACTIVE', 'INACTIVE', 'BLOCKED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- STUDENTS TABLE (Student-specific details)
-- ============================================
CREATE TABLE students (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    roll_number VARCHAR(50) NOT NULL UNIQUE,
    department_code VARCHAR(10) NOT NULL,
    department_name VARCHAR(100) NOT NULL,
    cgpa DECIMAL(3,2) NOT NULL,
    semester INT NOT NULL,
    date_of_birth DATE,
    address VARCHAR(255),
    city VARCHAR(50),
    state VARCHAR(50),
    pincode VARCHAR(10),
    enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_cgpa (cgpa),
    INDEX idx_department (department_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- COMPANIES TABLE
-- ============================================
CREATE TABLE companies (
    company_id INT AUTO_INCREMENT PRIMARY KEY,
    company_name VARCHAR(150) NOT NULL UNIQUE,
    company_email VARCHAR(100) NOT NULL UNIQUE,
    company_phone VARCHAR(15) NOT NULL,
    website VARCHAR(150),
    industry VARCHAR(100),
    headquarters VARCHAR(150),
    city VARCHAR(50),
    state VARCHAR(50),
    country VARCHAR(50),
    company_size ENUM('Startup', 'SME', 'Enterprise') DEFAULT 'SME',
    description TEXT,
    status ENUM('ACTIVE', 'INACTIVE', 'BLOCKED') DEFAULT 'ACTIVE',
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status),
    INDEX idx_city (city)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- INTERNSHIPS TABLE
-- ============================================
CREATE TABLE internships (
    internship_id INT AUTO_INCREMENT PRIMARY KEY,
    company_id INT NOT NULL,
    job_title VARCHAR(150) NOT NULL,
    job_description TEXT NOT NULL,
    job_location VARCHAR(150),
    stipend_amount DECIMAL(10,2),
    stipend_type VARCHAR(50),
    duration_months INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    application_deadline DATE NOT NULL,
    minimum_cgpa DECIMAL(3,2) NOT NULL DEFAULT 3.0,
    required_skills TEXT,
    total_positions INT NOT NULL,
    filled_positions INT DEFAULT 0,
    status ENUM('OPEN', 'CLOSED', 'FILLED') DEFAULT 'OPEN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (company_id) REFERENCES companies(company_id) ON DELETE CASCADE,
    INDEX idx_status (status),
    INDEX idx_deadline (application_deadline),
    INDEX idx_minimum_cgpa (minimum_cgpa)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- APPLICATIONS TABLE (Student internship applications)
-- ============================================
CREATE TABLE applications (
    application_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    internship_id INT NOT NULL,
    company_id INT NOT NULL,
    status ENUM('PENDING', 'SHORTLISTED', 'REJECTED', 'ACCEPTED', 'WITHDRAWN') DEFAULT 'PENDING',
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (internship_id) REFERENCES internships(internship_id) ON DELETE CASCADE,
    FOREIGN KEY (company_id) REFERENCES companies(company_id) ON DELETE CASCADE,
    UNIQUE KEY unique_application (student_id, internship_id),
    INDEX idx_status (status),
    INDEX idx_student_id (student_id),
    INDEX idx_internship_id (internship_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- APPLICATION_LOGS TABLE (Audit trail for applications)
-- ============================================
CREATE TABLE application_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    application_id INT NOT NULL,
    action VARCHAR(100) NOT NULL,
    previous_status VARCHAR(50),
    new_status VARCHAR(50),
    notes TEXT,
    logged_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (application_id) REFERENCES applications(application_id) ON DELETE CASCADE,
    INDEX idx_application_id (application_id),
    INDEX idx_logged_at (logged_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- EXAMS TABLE
-- ============================================
CREATE TABLE exams (
    exam_id INT AUTO_INCREMENT PRIMARY KEY,
    exam_title VARCHAR(150) NOT NULL,
    exam_description TEXT,
    total_marks INT NOT NULL,
    duration_minutes INT NOT NULL,
    passing_marks INT NOT NULL,
    exam_date DATE NOT NULL,
    exam_start_time TIME NOT NULL,
    exam_end_time TIME NOT NULL,
    status ENUM('DRAFT', 'SCHEDULED', 'ONGOING', 'COMPLETED', 'ARCHIVED') DEFAULT 'DRAFT',
    created_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(user_id),
    INDEX idx_exam_date (exam_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- QUESTIONS TABLE
-- ============================================
CREATE TABLE questions (
    question_id INT AUTO_INCREMENT PRIMARY KEY,
    exam_id INT NOT NULL,
    question_number INT NOT NULL,
    question_text TEXT NOT NULL,
    question_type ENUM('MCQ', 'SUBJECTIVE') NOT NULL,
    marks INT NOT NULL DEFAULT 1,
    difficulty_level ENUM('EASY', 'MEDIUM', 'HARD') DEFAULT 'MEDIUM',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (exam_id) REFERENCES exams(exam_id) ON DELETE CASCADE,
    INDEX idx_exam_id (exam_id),
    INDEX idx_question_type (question_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- OPTIONS TABLE (For MCQ questions)
-- ============================================
CREATE TABLE options (
    option_id INT AUTO_INCREMENT PRIMARY KEY,
    question_id INT NOT NULL,
    option_text TEXT NOT NULL,
    option_number INT NOT NULL,
    is_correct BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (question_id) REFERENCES questions(question_id) ON DELETE CASCADE,
    INDEX idx_question_id (question_id),
    INDEX idx_is_correct (is_correct),
    UNIQUE KEY unique_option (question_id, option_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- EXAM_ATTEMPTS TABLE
-- ============================================
CREATE TABLE exam_attempts (
    attempt_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    exam_id INT NOT NULL,
    start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP NULL,
    submitted_at TIMESTAMP NULL,
    total_marks_obtained INT DEFAULT 0,
    status ENUM('STARTED', 'IN_PROGRESS', 'SUBMITTED', 'EVALUATED') DEFAULT 'STARTED',
    ip_address VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (exam_id) REFERENCES exams(exam_id) ON DELETE CASCADE,
    UNIQUE KEY unique_attempt (student_id, exam_id),
    INDEX idx_status (status),
    INDEX idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- ANSWERS TABLE (Student answers)
-- ============================================
CREATE TABLE answers (
    answer_id INT AUTO_INCREMENT PRIMARY KEY,
    attempt_id INT NOT NULL,
    question_id INT NOT NULL,
    selected_option_id INT NULL,
    subjective_answer TEXT,
    marks_obtained INT DEFAULT 0,
    is_marked BOOLEAN DEFAULT FALSE,
    marked_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (attempt_id) REFERENCES exam_attempts(attempt_id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(question_id) ON DELETE CASCADE,
    FOREIGN KEY (selected_option_id) REFERENCES options(option_id) ON DELETE SET NULL,
    UNIQUE KEY unique_answer (attempt_id, question_id),
    INDEX idx_attempt_id (attempt_id),
    INDEX idx_is_marked (is_marked)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- AUDIT_LOGS TABLE (System audit trail)
-- ============================================
CREATE TABLE audit_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50),
    entity_id INT,
    details TEXT,
    ip_address VARCHAR(45),
    logged_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_logged_at (logged_at),
    INDEX idx_action (action),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- INSERT SAMPLE DATA (Optional)
-- ============================================

-- Sample Admin User
INSERT INTO users (email, password, full_name, role, phone_number, status)
VALUES ('admin@mitwpu.edu.in', 'admin123', 'Admin User', 'ADMIN', '9999999999', 'ACTIVE');

-- Sample Student User
INSERT INTO users (email, password, full_name, role, phone_number, status)
VALUES ('student@student.com', 'student123', 'John Doe', 'STUDENT', '9876543210', 'ACTIVE');

-- Sample Student Profile
INSERT INTO students (user_id, roll_number, department_code, department_name, cgpa, semester, date_of_birth, city)
VALUES (2, 'CSE2021001', 'CSE', 'Computer Science and Engineering', 3.8, 6, '2003-05-15', 'Pune');

-- Sample Company
INSERT INTO companies (company_name, company_email, company_phone, website, industry, city, state, country, company_size, status)
VALUES ('TechCorp Inc', 'hr@techcorp.com', '9111111111', 'www.techcorp.com', 'Information Technology', 'Bangalore', 'Karnataka', 'India', 'Enterprise', 'ACTIVE');

-- Sample Internship
INSERT INTO internships (company_id, job_title, job_description, job_location, stipend_amount, stipend_type, 
                        duration_months, start_date, application_deadline, minimum_cgpa, total_positions, status)
VALUES (1, 'Java Developer Internship', 'Full stack Java development role', 'Bangalore', 50000, 'Monthly', 
        3, '2026-05-01', '2026-04-30', 3.0, 5, 'OPEN');

-- Sample Exam
INSERT INTO exams (exam_title, exam_description, total_marks, duration_minutes, passing_marks, exam_date, exam_start_time, exam_end_time, status, created_by)
VALUES ('Data Structures Test', 'Online test for Data Structures knowledge', 100, 120, 40, '2026-05-10', '14:00:00', '16:00:00', 'SCHEDULED', 1);

-- Create Index for better query performance
CREATE INDEX idx_users_email_status ON users(email, status);
CREATE INDEX idx_internships_deadline_cgpa ON internships(application_deadline, minimum_cgpa);
CREATE INDEX idx_answers_attempt_question ON answers(attempt_id, question_id);
