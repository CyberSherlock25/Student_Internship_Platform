package com.mitwpu.lca.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Option POJO representing an MCQ answer option.
 */
public class Option implements Serializable {

    private static final long serialVersionUID = 1L;

    private int optionId;
    private int questionId;
    private String optionText;
    private int optionNumber;
    private boolean isCorrect; // true for correct answer
    private LocalDateTime createdAt;

    public Option() {}

    public Option(int questionId, String optionText, int optionNumber, boolean isCorrect) {
        this.questionId = questionId;
        this.optionText = optionText;
        this.optionNumber = optionNumber;
        this.isCorrect = isCorrect;
    }

    // Getters and Setters
    public int getOptionId() { return optionId; }
    public void setOptionId(int optionId) { this.optionId = optionId; }

    public int getQuestionId() { return questionId; }
    public void setQuestionId(int questionId) { this.questionId = questionId; }

    public String getOptionText() { return optionText; }
    public void setOptionText(String optionText) { this.optionText = optionText; }

    public int getOptionNumber() { return optionNumber; }
    public void setOptionNumber(int optionNumber) { this.optionNumber = optionNumber; }

    public boolean getIsCorrect() { return isCorrect; }
    public void setIsCorrect(boolean isCorrect) { this.isCorrect = isCorrect; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Option{" +
                "optionId=" + optionId +
                ", questionId=" + questionId +
                ", optionNumber=" + optionNumber +
                ", isCorrect=" + isCorrect +
                '}';
    }
}

