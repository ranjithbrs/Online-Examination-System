package com.examsystem.onlineexam.dto;

import java.io.Serializable;
import java.util.List;

public class QuestionDisplayDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String questionText;
    private String category;
    private int marks;
    private List<OptionDisplay> options;

    public QuestionDisplayDto() {
    }

    public QuestionDisplayDto(Long id, String questionText, String category, int marks, List<OptionDisplay> options) {
        this.id = id;
        this.questionText = questionText;
        this.category = category;
        this.marks = marks;
        this.options = options;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }

    public List<OptionDisplay> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDisplay> options) {
        this.options = options;
    }

    public static class OptionDisplay implements Serializable {
        private static final long serialVersionUID = 1L;

        private String label; // "A", "B", "C", "D" displayed on screen
        private String value; // original option key ("A", "B", "C", "D") for grading
        private String text;  // option text

        public OptionDisplay() {
        }

        public OptionDisplay(String label, String value, String text) {
            this.label = label;
            this.value = value;
            this.text = text;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
