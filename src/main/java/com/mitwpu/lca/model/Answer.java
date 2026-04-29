package com.mitwpu.lca.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Answer POJO representing a student's answer to a question.
 */
public class Answer implements Serializable {

    private static final long serialVersionUID = 1L;

    private int answerId;
    private int attemptId;
    private int questionId;
    private Integer selectedOptionId; // For MCQ
    private String subjectiveAnswer; // For subjective questions
    private int marksObtained;
    private boolean isMarked; // Whether evaluated by admin
    private LocalDateTime markedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Transient fields for display
    private String questionText;
    private String selectedOptionText;
    private boolean isCorrect;
    private int correctOptionId;

    public Answer() {}

    public Answer(int attemptId, int questionId, Integer selectedOptionId, String subjectiveAnswer) {
        this.attemptId = attemptId;
        this.questionId = questionId;
        this.selectedOptionId = selectedOptionId;
        this.subjectiveAnswer = subjectiveAnswer;
        this.marksObtained = 0;
        this.isMarked = false;
    }

    // Getters and Setters
    public int getAnswerId() { return answerId; }
    public void setAnswerId(int answerId) { this.answerId = answerId; }

    public int getAttemptId() { return attemptId; }
    public void setAttemptId(int attemptId) { this.attemptId = attemptId; }

    public int getQuestionId() { return questionId; }
    public void setQuestionId(int questionId) { this.questionId = questionId; }

    public Integer getSelectedOptionId() { return selectedOptionId; }
    public void setSelectedOptionId(Integer selectedOptionId) { this.selectedOptionId = selectedOptionId; }

    public String getSubjectiveAnswer() { return subjectiveAnswer; }
    public void setSubjectiveAnswer(String subjectiveAnswer) { this.subjectiveAnswer = subjectiveAnswer; }

    public int getMarksObtained() { return marksObtained; }
    public void setMarksObtained(int marksObtained) { this.marksObtained = marksObtained; }

    public boolean getIsMarked() { return isMarked; }
    public void setIsMarked(boolean isMarked) { this.isMarked = isMarked; }

    public LocalDateTime getMarkedAt() { return markedAt; }
    public void setMarkedAt(LocalDateTime markedAt) { this.markedAt = markedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getSelectedOptionText() { return selectedOptionText; }
    public void setSelectedOptionText(String selectedOptionText) { this.selectedOptionText = selectedOptionText; }

    public boolean getIsCorrect() { return isCorrect; }
    public void setIsCorrect(boolean isCorrect) { this.isCorrect = isCorrect; }

    public int getCorrectOptionId() { return correctOptionId; }
    public void setCorrectOptionId(int correctOptionId) { this.correctOptionId = correctOptionId; }

    @Override
    public String toString() {
        return "Answer{" +
                "answerId=" + answerId +
                ", attemptId=" + attemptId +
                ", questionId=" + questionId +
                ", marksObtained=" + marksObtained +
                '}';
    }
}

