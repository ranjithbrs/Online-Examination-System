package com.examsystem.onlineexam.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "exam_results")
public class ExamResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String studentName;

    @Column(nullable = false)
    private String studentEmail;

    @Column(nullable = false)
    private String rollNumber;

    private int score;
    private int totalMarks;
    private double percentage;
    private boolean passed;

    // Proctoring Metrics
    private int tabSwitchCount;
    private int copyCount;
    private int rightClickCount;
    private int fullscreenExitCount;
    private int windowBlurCount;
    private int totalViolations;

    // Integrity Analytics
    private int riskScore;
    private int trustScore; // 0 - 100%
    private String integrityStatus; // "High Integrity (Normal)", "Moderate Warning", "High Risk / Flagged"

    private LocalDateTime submittedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "exam_result_answers", joinColumns = @JoinColumn(name = "exam_result_id"))
    @MapKeyColumn(name = "question_id")
    @Column(name = "selected_option")
    private Map<Long, String> selectedAnswers = new HashMap<>();

    public ExamResult() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public int getTabSwitchCount() {
        return tabSwitchCount;
    }

    public void setTabSwitchCount(int tabSwitchCount) {
        this.tabSwitchCount = tabSwitchCount;
    }

    public int getCopyCount() {
        return copyCount;
    }

    public void setCopyCount(int copyCount) {
        this.copyCount = copyCount;
    }

    public int getRightClickCount() {
        return rightClickCount;
    }

    public void setRightClickCount(int rightClickCount) {
        this.rightClickCount = rightClickCount;
    }

    public int getFullscreenExitCount() {
        return fullscreenExitCount;
    }

    public void setFullscreenExitCount(int fullscreenExitCount) {
        this.fullscreenExitCount = fullscreenExitCount;
    }

    public int getWindowBlurCount() {
        return windowBlurCount;
    }

    public void setWindowBlurCount(int windowBlurCount) {
        this.windowBlurCount = windowBlurCount;
    }

    public int getTotalViolations() {
        return totalViolations;
    }

    public void setTotalViolations(int totalViolations) {
        this.totalViolations = totalViolations;
    }

    public int getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(int riskScore) {
        this.riskScore = riskScore;
    }

    public int getTrustScore() {
        return trustScore;
    }

    public void setTrustScore(int trustScore) {
        this.trustScore = trustScore;
    }

    public String getIntegrityStatus() {
        return integrityStatus;
    }

    public void setIntegrityStatus(String integrityStatus) {
        this.integrityStatus = integrityStatus;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Map<Long, String> getSelectedAnswers() {
        return selectedAnswers;
    }

    public void setSelectedAnswers(Map<Long, String> selectedAnswers) {
        this.selectedAnswers = selectedAnswers;
    }
}
