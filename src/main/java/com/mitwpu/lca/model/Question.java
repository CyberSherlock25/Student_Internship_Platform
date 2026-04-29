package com.mitwpu.lca.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Question POJO representing an exam question.
 * Can be MCQ or SUBJECTIVE type.
 */
public class Question implements Serializable {

    private static final long serialVersionUID = 1L;

    private int questionId;
    private int examId;
    private int questionNumber;
    private String questionText;
    private String questionType; // MCQ, SUBJECTIVE
    private int marks;
    private String difficultyLevel; // EASY, MEDIUM, HARD
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Transient field for options
    private List<Option> options;

    public Question() {}

    public Question(int examId, int questionNumber, String questionText,
                    String questionType, int marks, String difficultyLevel) {
        this.examId = examId;
        this.questionNumber = questionNumber;
        this.questionText = questionText;
        this.questionType = questionType;
        this.marks = marks;
        this.difficultyLevel = difficultyLevel;
    }

    // Getters and Setters
    public int getQuestionId() { return questionId; }
    public void setQuestionId(int questionId) { this.questionId = questionId; }

    public int getExamId() { return examId; }
    public void setExamId(int examId) { this.examId = examId; }

    public int getQuestionNumber() { return questionNumber; }
    public void setQuestionNumber(int questionNumber) { this.questionNumber = questionNumber; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getQuestionType() { return questionType; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }

    public int getMarks() { return marks; }
    public void setMarks(int marks) { this.marks = marks; }

    public String getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(String difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<Option> getOptions() { return options; }
    public void setOptions(List<Option> options) { this.options = options; }

    public boolean isMCQ() {
        return "MCQ".equalsIgnoreCase(this.questionType);
    }

    public boolean isSubjective() {
        return "SUBJECTIVE".equalsIgnoreCase(this.questionType);
    }

    @Override
    public String toString() {
        return "Question{" +
                "questionId=" + questionId +
                ", examId=" + examId +
                ", questionNumber=" + questionNumber +
                ", questionType='" + questionType + '\'' +
                ", marks=" + marks +
                '}';
    }
}

