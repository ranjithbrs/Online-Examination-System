package com.examsystem.onlineexam.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

public class ExamSubmissionForm {

    @NotBlank(message = "Student name is required")
    private String studentName;

    @NotBlank(message = "Student email is required")
    @Email(message = "Please provide a valid email address")
    private String studentEmail;

    @NotBlank(message = "Roll number is required")
    private String rollNumber;

    // Map of questionId -> selectedOption ("A", "B", "C", "D")
    private Map<Long, String> answers = new HashMap<>();

    // Proctoring Metrics
    @Min(value = 0, message = "Tab switch count must be positive")
    private int tabSwitch = 0;

    @Min(value = 0, message = "Copy count must be positive")
    private int copyCount = 0;

    @Min(value = 0, message = "Right click count must be positive")
    private int rightClick = 0;

    @Min(value = 0, message = "Fullscreen exit count must be positive")
    private int fullscreenExit = 0;

    @Min(value = 0, message = "Window blur count must be positive")
    private int windowBlur = 0;

    // JSON string or comma-separated log of proctoring events
    private String violationLogsJson;

    public ExamSubmissionForm() {
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public Map<Long, String> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Long, String> answers) {
        this.answers = answers;
    }

    public int getTabSwitch() {
        return tabSwitch;
    }

    public void setTabSwitch(int tabSwitch) {
        this.tabSwitch = tabSwitch;
    }

    public int getCopyCount() {
        return copyCount;
    }

    public void setCopyCount(int copyCount) {
        this.copyCount = copyCount;
    }

    public int getRightClick() {
        return rightClick;
    }

    public void setRightClick(int rightClick) {
        this.rightClick = rightClick;
    }

    public int getFullscreenExit() {
        return fullscreenExit;
    }

    public void setFullscreenExit(int fullscreenExit) {
        this.fullscreenExit = fullscreenExit;
    }

    public int getWindowBlur() {
        return windowBlur;
    }

    public void setWindowBlur(int windowBlur) {
        this.windowBlur = windowBlur;
    }

    public String getViolationLogsJson() {
        return violationLogsJson;
    }

    public void setViolationLogsJson(String violationLogsJson) {
        this.violationLogsJson = violationLogsJson;
    }
}
