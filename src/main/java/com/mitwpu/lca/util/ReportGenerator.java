package com.mitwpu.lca.util;

import com.mitwpu.lca.dao.ViolationDAO;
import com.mitwpu.lca.model.Violation;

/**
 * Utility class for generating comprehensive reports
 * Supports violations, exam results, application tracking, etc.
 */
public class ReportGenerator {
    
    /**
     * Generate violation report for an exam attempt
     */
    public static String generateViolationReport(int attemptId, String studentName) {
        StringBuilder report = new StringBuilder();
        
        report.append("===========================================\n");
        report.append("EXAM PROCTORING VIOLATION REPORT\n");
        report.append("===========================================\n\n");
        report.append("Student: ").append(studentName).append("\n");
        report.append("Attempt ID: ").append(attemptId).append("\n");
        report.append("Generated: ").append(new java.util.Date()).append("\n\n");
        
        ViolationDAO dao = new ViolationDAO();
        ViolationDAO.ViolationSummary summary = dao.getViolationSummary(attemptId);
        String typeSummary = dao.getViolationTypeSummary(attemptId);
        
        report.append("VIOLATION SUMMARY\n");
        report.append("------------------------------------------\n");
        report.append("Total Violations: ").append(summary.getTotalViolations()).append("\n");
        report.append("  - High Severity: ").append(summary.highViolations).append("\n");
        report.append("  - Medium Severity: ").append(summary.mediumViolations).append("\n");
        report.append("  - Low Severity: ").append(summary.lowViolations).append("\n\n");
        
        report.append("VIOLATION BREAKDOWN BY TYPE\n");
        report.append("------------------------------------------\n");
        report.append(typeSummary).append("\n\n");
        
        report.append("SEVERITY ASSESSMENT\n");
        report.append("------------------------------------------\n");
        if (summary.highViolations >= 5) {
            report.append("STATUS: CRITICAL - Multiple high severity violations detected\n");
            report.append("RECOMMENDATION: Review exam attempt for potential cheating\n");
        } else if (summary.mediumViolations >= 5 || summary.highViolations >= 2) {
            report.append("STATUS: SUSPICIOUS - Several violations noted\n");
            report.append("RECOMMENDATION: Further investigation recommended\n");
        } else {
            report.append("STATUS: ACCEPTABLE - Within normal parameters\n");
            report.append("RECOMMENDATION: Approve exam result\n");
        }
        
        report.append("\n===========================================\n");
        
        return report.toString();
    }
    
    /**
     * Generate exam result report
     */
    public static String generateExamResultReport(String studentName, String examTitle,
                                                  int obtainedMarks, int totalMarks,
                                                  double percentage, int violationCount) {
        StringBuilder report = new StringBuilder();
        
        report.append("===========================================\n");
        report.append("EXAM RESULT REPORT\n");
        report.append("===========================================\n\n");
        report.append("Student: ").append(studentName).append("\n");
        report.append("Exam: ").append(examTitle).append("\n");
        report.append("Date: ").append(new java.util.Date()).append("\n\n");
        
        report.append("SCORES\n");
        report.append("------------------------------------------\n");
        report.append("Obtained Marks: ").append(obtainedMarks).append("\n");
        report.append("Total Marks: ").append(totalMarks).append("\n");
        report.append("Percentage: ").append(String.format("%.2f%%", percentage)).append("\n");
        report.append("Grade: ").append(getGrade(percentage)).append("\n\n");
        
        report.append("PROCTORING VIOLATIONS\n");
        report.append("------------------------------------------\n");
        report.append("Total Violations: ").append(violationCount).append("\n\n");
        
        report.append("STATUS\n");
        report.append("------------------------------------------\n");
        report.append("Result: ").append(percentage >= 40 ? "PASSED" : "FAILED").append("\n");
        
        report.append("\n===========================================\n");
        
        return report.toString();
    }
    
    /**
     * Generate application tracking report
     */
    public static String generateApplicationReport(String studentName, String internshipTitle,
                                                   String companyName, String applicationStatus,
                                                   String appliedDate) {
        StringBuilder report = new StringBuilder();
        
        report.append("===========================================\n");
        report.append("APPLICATION TRACKING REPORT\n");
        report.append("===========================================\n\n");
        report.append("Student: ").append(studentName).append("\n");
        report.append("Company: ").append(companyName).append("\n");
        report.append("Position: ").append(internshipTitle).append("\n");
        report.append("Applied Date: ").append(appliedDate).append("\n\n");
        
        report.append("CURRENT STATUS\n");
        report.append("------------------------------------------\n");
        report.append("Application Status: ").append(applicationStatus).append("\n");
        
        switch (applicationStatus.toUpperCase()) {
            case "PENDING":
                report.append("Awaiting company review\n");
                break;
            case "SHORTLISTED":
                report.append("You have been shortlisted! Exam will be scheduled soon.\n");
                break;
            case "ACCEPTED":
                report.append("Congratulations! You have been selected.\n");
                break;
            case "REJECTED":
                report.append("Your application was not selected. Try other opportunities.\n");
                break;
        }
        
        report.append("\n===========================================\n");
        
        return report.toString();
    }
    
    /**
     * Helper method to determine grade based on percentage
     */
    private static String getGrade(double percentage) {
        if (percentage >= 90) return "A";
        if (percentage >= 80) return "B";
        if (percentage >= 70) return "C";
        if (percentage >= 60) return "D";
        if (percentage >= 40) return "E";
        return "F";
    }
    
    /**
     * Generate CSV format for bulk export
     */
    public static String generateCSVExport(String[][] data, String[] headers) {
        StringBuilder csv = new StringBuilder();
        
        // Add headers
        for (int i = 0; i < headers.length; i++) {
            csv.append("\"").append(headers[i]).append("\"");
            if (i < headers.length - 1) {
                csv.append(",");
            }
        }
        csv.append("\n");
        
        // Add data
        for (String[] row : data) {
            for (int i = 0; i < row.length; i++) {
                csv.append("\"").append(row[i]).append("\"");
                if (i < row.length - 1) {
                    csv.append(",");
                }
            }
            csv.append("\n");
        }
        
        return csv.toString();
    }
}
