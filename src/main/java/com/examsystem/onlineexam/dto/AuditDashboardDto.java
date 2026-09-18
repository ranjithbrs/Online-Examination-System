package com.examsystem.onlineexam.dto;

public class AuditDashboardDto {

    private int totalExams;
    private long passedCount;
    private long failedCount;
    private double passRate;
    private double averageScorePercentage;
    private double averageTrustScore;
    private long flaggedCount;
    private long disqualifiedCount;

    public AuditDashboardDto() {
    }

    public AuditDashboardDto(int totalExams, long passedCount, long failedCount, double passRate,
                             double averageScorePercentage, double averageTrustScore,
                             long flaggedCount, long disqualifiedCount) {
        this.totalExams = totalExams;
        this.passedCount = passedCount;
        this.failedCount = failedCount;
        this.passRate = passRate;
        this.averageScorePercentage = averageScorePercentage;
        this.averageTrustScore = averageTrustScore;
        this.flaggedCount = flaggedCount;
        this.disqualifiedCount = disqualifiedCount;
    }

    public int getTotalExams() {
        return totalExams;
    }

    public void setTotalExams(int totalExams) {
        this.totalExams = totalExams;
    }

    public long getPassedCount() {
        return passedCount;
    }

    public void setPassedCount(long passedCount) {
        this.passedCount = passedCount;
    }

    public long getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(long failedCount) {
        this.failedCount = failedCount;
    }

    public double getPassRate() {
        return passRate;
    }

    public void setPassRate(double passRate) {
        this.passRate = passRate;
    }

    public double getAverageScorePercentage() {
        return averageScorePercentage;
    }

    public void setAverageScorePercentage(double averageScorePercentage) {
        this.averageScorePercentage = averageScorePercentage;
    }

    public double getAverageTrustScore() {
        return averageTrustScore;
    }

    public void setAverageTrustScore(double averageTrustScore) {
        this.averageTrustScore = averageTrustScore;
    }

    public long getFlaggedCount() {
        return flaggedCount;
    }

    public void setFlaggedCount(long flaggedCount) {
        this.flaggedCount = flaggedCount;
    }

    public long getDisqualifiedCount() {
        return disqualifiedCount;
    }

    public void setDisqualifiedCount(long disqualifiedCount) {
        this.disqualifiedCount = disqualifiedCount;
    }
}
