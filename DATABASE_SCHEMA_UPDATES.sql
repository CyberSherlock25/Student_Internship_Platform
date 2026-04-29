-- ============================================
-- DATABASE UPDATES: session_tracking table + constraints
-- ============================================

USE internship_exam_system;

-- ============================================
-- SESSION_TRACKING TABLE (Anti-cheating + single session enforcement)
-- ============================================
CREATE TABLE IF NOT EXISTS session_tracking (
    session_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    session_token VARCHAR(255) NOT NULL UNIQUE,
    ip_address VARCHAR(45) NOT NULL,
    user_agent VARCHAR(255),
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_activity TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    logout_time TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_session_token (session_token),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- ADD CHECK CONSTRAINTS (MySQL 8.0.16+)
-- ============================================
ALTER TABLE students
    ADD CONSTRAINT chk_cgpa CHECK (cgpa >= 0.00 AND cgpa <= 10.00),
    ADD CONSTRAINT chk_semester CHECK (semester >= 1 AND semester <= 12);

ALTER TABLE internships
    ADD CONSTRAINT chk_min_cgpa CHECK (minimum_cgpa >= 0.00 AND minimum_cgpa <= 10.00),
    ADD CONSTRAINT chk_duration CHECK (duration_months > 0),
    ADD CONSTRAINT chk_positions CHECK (total_positions > 0),
    ADD CONSTRAINT chk_filled_positions CHECK (filled_positions >= 0);

ALTER TABLE exams
    ADD CONSTRAINT chk_duration CHECK (duration_minutes > 0),
    ADD CONSTRAINT chk_passing_marks CHECK (passing_marks >= 0),
    ADD CONSTRAINT chk_total_marks CHECK (total_marks > 0);

ALTER TABLE questions
    ADD CONSTRAINT chk_marks CHECK (marks > 0);

ALTER TABLE exam_attempts
    ADD CONSTRAINT chk_attempt_status CHECK (status IN ('STARTED','IN_PROGRESS','SUBMITTED','EVALUATED'));

ALTER TABLE answers
    ADD CONSTRAINT chk_marks_obtained CHECK (marks_obtained >= 0);

-- ============================================
-- ADD TRIGGER for application_logs (auto-log on status change)
-- ============================================
DELIMITER //
CREATE TRIGGER IF NOT EXISTS trg_application_log 
AFTER UPDATE ON applications
FOR EACH ROW
BEGIN
    IF OLD.status != NEW.status THEN
        INSERT INTO application_logs (application_id, action, previous_status, new_status, logged_at)
        VALUES (NEW.application_id, 'STATUS_CHANGED', OLD.status, NEW.status, CURRENT_TIMESTAMP);
    END IF;
END//
DELIMITER ;

-- ============================================
-- SEED DATA FOR EXAM MODULE
-- ============================================

-- Sample Questions for Data Structures Test (exam_id = 1)
INSERT INTO questions (exam_id, question_number, question_text, question_type, marks, difficulty_level)
VALUES (1, 1, 'What is the time complexity of binary search?', 'MCQ', 2, 'EASY');

INSERT INTO options (question_id, option_text, option_number, is_correct)
VALUES 
    (1, 'O(n)', 1, FALSE),
    (1, 'O(log n)', 2, TRUE),
    (1, 'O(n log n)', 3, FALSE),
    (1, 'O(1)', 4, FALSE);

INSERT INTO questions (exam_id, question_number, question_text, question_type, marks, difficulty_level)
VALUES (1, 2, 'Explain the difference between Stack and Queue with real-world examples.', 'SUBJECTIVE', 5, 'MEDIUM');

INSERT INTO questions (exam_id, question_number, question_text, question_type, marks, difficulty_level)
VALUES (1, 3, 'Which data structure uses LIFO principle?', 'MCQ', 1, 'EASY');

INSERT INTO options (question_id, option_text, option_number, is_correct)
VALUES 
    (3, 'Queue', 1, FALSE),
    (3, 'Stack', 2, TRUE),
    (3, 'Linked List', 3, FALSE),
    (3, 'Tree', 4, FALSE);

