package com.examsystem.onlineexam.dto;

import com.examsystem.onlineexam.model.Question;

public class QuestionReviewDto {
    private Question question;
    private String selectedOption;
    private boolean correct;

    public QuestionReviewDto(Question question, String selectedOption, boolean correct) {
        this.question = question;
        this.selectedOption = selectedOption;
        this.correct = correct;
    }

    public Question getQuestion() {
        return question;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public boolean isCorrect() {
        return correct;
    }
}
