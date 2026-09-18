package com.examsystem.onlineexam.service;

import com.examsystem.onlineexam.dto.AuditDashboardDto;
import com.examsystem.onlineexam.dto.ExamSubmissionForm;
import com.examsystem.onlineexam.dto.QuestionReviewDto;
import com.examsystem.onlineexam.dto.SnapshotItemDto;
import com.examsystem.onlineexam.model.ExamResult;
import com.examsystem.onlineexam.model.ProctoringSnapshot;
import com.examsystem.onlineexam.model.Question;
import com.examsystem.onlineexam.model.ViolationLog;
import com.examsystem.onlineexam.repository.ExamResultRepository;
import com.examsystem.onlineexam.repository.ProctoringSnapshotRepository;
import com.examsystem.onlineexam.repository.QuestionRepository;
import com.examsystem.onlineexam.repository.ViolationLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Collections;

@Service
public class ExamService {

    private final QuestionRepository questionRepository;
    private final ExamResultRepository examResultRepository;
    private final ViolationLogRepository violationLogRepository;
    private final ProctoringSnapshotRepository proctoringSnapshotRepository;

    public ExamService(QuestionRepository questionRepository,
                       ExamResultRepository examResultRepository,
                       ViolationLogRepository violationLogRepository,
                       ProctoringSnapshotRepository proctoringSnapshotRepository) {
        this.questionRepository = questionRepository;
        this.examResultRepository = examResultRepository;
        this.violationLogRepository = violationLogRepository;
        this.proctoringSnapshotRepository = proctoringSnapshotRepository;
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public ExamResult getExamResult(Long id) {
        return examResultRepository.findById(id).orElse(null);
    }

    public List<ExamResult> getAllExamResults() {
        return examResultRepository.findAllByOrderBySubmittedAtDesc();
    }

    public List<ViolationLog> getViolationLogs(Long examResultId) {
        return violationLogRepository.findByExamResultIdOrderByTimestampAsc(examResultId);
    }

    public List<ProctoringSnapshot> getProctoringSnapshots(Long examResultId) {
        return proctoringSnapshotRepository.findByExamResultIdOrderByCapturedAtAsc(examResultId);
    }

    public AuditDashboardDto getAuditDashboardMetrics() {
        List<ExamResult> results = getAllExamResults();
        int total = results.size();
        if (total == 0) {
            return new AuditDashboardDto(0, 0, 0, 0.0, 0.0, 100.0, 0, 0);
        }

        long passed = results.stream().filter(ExamResult::isPassed).count();
        long failed = total - passed;
        double passRate = Math.round((passed * 100.0 / total) * 10.0) / 10.0;
        double avgScore = Math.round(results.stream().mapToDouble(ExamResult::getPercentage).average().orElse(0.0) * 10.0) / 10.0;
        double avgTrust = Math.round(results.stream().mapToInt(ExamResult::getTrustScore).average().orElse(100.0) * 10.0) / 10.0;
        long flagged = results.stream().filter(r -> r.isDisqualified() || r.getTrustScore() < 60).count();
        long disqualified = results.stream().filter(ExamResult::isDisqualified).count();

        return new AuditDashboardDto(total, passed, failed, passRate, avgScore, avgTrust, flagged, disqualified);
    }

    public String exportExamResultsToCsv() {
        List<ExamResult> results = getAllExamResults();
        StringBuilder sb = new StringBuilder();
        sb.append("Result ID,Student Name,Email,Roll Number,Score,Total Marks,Percentage,Passed,Time Taken (s),Overtime,Tab Switches,Copy Attempts,Right Clicks,Fullscreen Exits,Window Blurs,Total Violations,Disqualified,Disqualification Reason,Trust Score,Integrity Status,Submitted At\n");

        for (ExamResult r : results) {
            sb.append(escapeCsv(String.valueOf(r.getId()))).append(",");
            sb.append(escapeCsv(r.getStudentName())).append(",");
            sb.append(escapeCsv(r.getStudentEmail())).append(",");
            sb.append(escapeCsv(r.getRollNumber())).append(",");
            sb.append(r.getScore()).append(",");
            sb.append(r.getTotalMarks()).append(",");
            sb.append(r.getPercentage()).append(",");
            sb.append(r.isPassed()).append(",");
            sb.append(r.getTimeTakenSeconds()).append(",");
            sb.append(r.isOvertime()).append(",");
            sb.append(r.getTabSwitchCount()).append(",");
            sb.append(r.getCopyCount()).append(",");
            sb.append(r.getRightClickCount()).append(",");
            sb.append(r.getFullscreenExitCount()).append(",");
            sb.append(r.getWindowBlurCount()).append(",");
            sb.append(r.getTotalViolations()).append(",");
            sb.append(r.isDisqualified()).append(",");
            sb.append(escapeCsv(r.getDisqualificationReason())).append(",");
            sb.append(r.getTrustScore()).append(",");
            sb.append(escapeCsv(r.getIntegrityStatus())).append(",");
            sb.append(escapeCsv(r.getSubmittedAt() != null ? r.getSubmittedAt().toString() : "")).append("\n");
        }
        return sb.toString();
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "\"\"";
        }
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    @Transactional
    public ExamResult evaluateAndSaveExam(ExamSubmissionForm form) {
        List<Question> questions = questionRepository.findAll();
        int score = 0;
        int totalMarks = 0;

        Map<Long, String> submittedAnswers = form.getAnswers();

        for (Question q : questions) {
            totalMarks += q.getMarks();
            String userAns = submittedAnswers.get(q.getId());
            if (userAns != null && userAns.trim().equalsIgnoreCase(q.getCorrectOption().trim())) {
                score += q.getMarks();
            }
        }

        double percentage = totalMarks > 0 ? (score * 100.0) / totalMarks : 0.0;
        boolean passed = percentage >= 50.0;

        // Proctoring Risk & Trust Score calculation
        int tabSwitch = Math.max(0, form.getTabSwitch());
        int copyCount = Math.max(0, form.getCopyCount());
        int rightClick = Math.max(0, form.getRightClick());
        int fullscreenExit = Math.max(0, form.getFullscreenExit());
        int windowBlur = Math.max(0, form.getWindowBlur());

        int totalViolations = tabSwitch + copyCount + rightClick + fullscreenExit + windowBlur;

        // Server-Side Timer & Overtime Validation (10 mins = 600s, 30s grace buffer)
        int allowedSeconds = 600;
        int timeTaken = form.getTimeTakenSeconds();
        boolean isOvertime = timeTaken > (allowedSeconds + 30);

        int riskScore = (tabSwitch * 5) + (copyCount * 8) + (rightClick * 3) + (fullscreenExit * 10) + (windowBlur * 4);
        if (isOvertime) {
            riskScore += 25; // Overtime penalty
        }
        int trustScore = Math.max(0, 100 - riskScore);

        // Disqualification Strike Rule: 3+ Fullscreen Exits OR 4+ Tab Switches OR explicit form disqualification
        boolean isDisqualified = form.isDisqualified() 
                || fullscreenExit >= 3 
                || tabSwitch >= 4 
                || (fullscreenExit >= 2 && tabSwitch >= 2);

        String disqualificationReason = form.getDisqualificationReason();
        if (isDisqualified && (disqualificationReason == null || disqualificationReason.isBlank())) {
            if (fullscreenExit >= 3) {
                disqualificationReason = "Exceeded maximum allowed full-screen exit strikes (3+ strikes)";
            } else if (tabSwitch >= 4) {
                disqualificationReason = "Exceeded maximum allowed tab/window switch strikes (4+ strikes)";
            } else {
                disqualificationReason = "Automatic disqualification due to excessive security violations";
            }
        }

        String integrityStatus;
        if (isDisqualified) {
            trustScore = 0;
            integrityStatus = "DISQUALIFIED";
            passed = false;
        } else if (trustScore >= 85) {
            integrityStatus = "High Integrity";
        } else if (trustScore >= 60) {
            integrityStatus = "Moderate Warning";
        } else {
            integrityStatus = "High Risk / Flagged";
        }

        ExamResult result = new ExamResult();
        result.setStudentName(form.getStudentName() != null && !form.getStudentName().isBlank() ? form.getStudentName() : "Anonymous Candidate");
        result.setStudentEmail(form.getStudentEmail() != null && !form.getStudentEmail().isBlank() ? form.getStudentEmail() : "candidate@example.com");
        result.setRollNumber(form.getRollNumber() != null && !form.getRollNumber().isBlank() ? form.getRollNumber() : "EXAM-" + System.currentTimeMillis() % 10000);
        
        result.setScore(score);
        result.setTotalMarks(totalMarks);
        result.setPercentage(Math.round(percentage * 10.0) / 10.0);
        result.setPassed(passed);

        result.setTabSwitchCount(tabSwitch);
        result.setCopyCount(copyCount);
        result.setRightClickCount(rightClick);
        result.setFullscreenExitCount(fullscreenExit);
        result.setWindowBlurCount(windowBlur);
        result.setTotalViolations(totalViolations);

        result.setTimeTakenSeconds(timeTaken);
        result.setOvertime(isOvertime);

        result.setDisqualified(isDisqualified);
        result.setDisqualificationReason(disqualificationReason);

        result.setRiskScore(riskScore);
        result.setTrustScore(trustScore);
        result.setIntegrityStatus(integrityStatus);
        result.setSubmittedAt(LocalDateTime.now());
        result.setSelectedAnswers(submittedAnswers != null ? new HashMap<>(submittedAnswers) : new HashMap<>());

        ExamResult savedResult = examResultRepository.save(result);

        // Record individual proctoring violation logs
        createViolationLogs(savedResult.getId(), form, isDisqualified, disqualificationReason);

        // Save proctoring webcam snapshots
        if (form.getSnapshots() != null && !form.getSnapshots().isEmpty()) {
            for (SnapshotItemDto s : form.getSnapshots()) {
                if (s.getImageBase64() != null && !s.getImageBase64().isBlank()) {
                    ProctoringSnapshot snapshot = new ProctoringSnapshot(
                            savedResult.getId(),
                            s.getType() != null && !s.getType().isBlank() ? s.getType() : "AUDIT_SNAPSHOT",
                            s.getImageBase64(),
                            s.getNote(),
                            LocalDateTime.now()
                    );
                    proctoringSnapshotRepository.save(snapshot);
                }
            }
        }

        return savedResult;
    }

    private void createViolationLogs(Long resultId, ExamSubmissionForm form, boolean isDisqualified, String disqualificationReason) {
        LocalDateTime now = LocalDateTime.now();
        if (form.getTabSwitch() > 0) {
            violationLogRepository.save(new ViolationLog(resultId, "TAB_SWITCH", "Detected " + form.getTabSwitch() + " tab/browser switch event(s)", now));
        }
        if (form.getCopyCount() > 0) {
            violationLogRepository.save(new ViolationLog(resultId, "COPY_PASTE", "Detected " + form.getCopyCount() + " copy or paste attempt(s)", now));
        }
        if (form.getRightClick() > 0) {
            violationLogRepository.save(new ViolationLog(resultId, "RIGHT_CLICK", "Detected " + form.getRightClick() + " context menu / right click attempt(s)", now));
        }
        if (form.getFullscreenExit() > 0) {
            violationLogRepository.save(new ViolationLog(resultId, "FULLSCREEN_EXIT", "Exited secure full-screen mode " + form.getFullscreenExit() + " time(s)", now));
        }
        if (form.getWindowBlur() > 0) {
            violationLogRepository.save(new ViolationLog(resultId, "WINDOW_BLUR", "Browser window lost focus " + form.getWindowBlur() + " time(s)", now));
        }
        if (form.getTimeTakenSeconds() > 630) {
            int over = form.getTimeTakenSeconds() - 600;
            violationLogRepository.save(new ViolationLog(resultId, "OVERTIME_SUBMISSION", "Exam submitted overtime by " + (over / 60) + "m " + (over % 60) + "s", now));
        }
        if (isDisqualified) {
            violationLogRepository.save(new ViolationLog(resultId, "SECURITY_DISQUALIFICATION", "Candidate disqualified: " + disqualificationReason, now));
        }
    }

    public List<QuestionReviewDto> getQuestionReviews(ExamResult result) {
        Map<Long, String> answers = (result != null && result.getSelectedAnswers() != null) 
            ? result.getSelectedAnswers() 
            : new HashMap<>();
        return getQuestionReviews(answers);
    }

    public List<QuestionReviewDto> getQuestionReviews(Map<Long, String> userAnswers) {
        List<Question> questions = getAllQuestions();
        List<QuestionReviewDto> reviews = new ArrayList<>();
        for (Question q : questions) {
            String ans = userAnswers != null ? userAnswers.get(q.getId()) : null;
            boolean isCorrect = ans != null && ans.trim().equalsIgnoreCase(q.getCorrectOption().trim());
            reviews.add(new QuestionReviewDto(q, ans != null ? ans : "Not Answered", isCorrect));
        }
        return reviews;
    }

    public List<String> getDistinctTopics() {
        return questionRepository.findAll().stream()
                .map(q -> q.getCategory() != null && !q.getCategory().isBlank() ? q.getCategory().trim() : "General")
                .distinct()
                .sorted()
                .toList();
    }

    public List<com.examsystem.onlineexam.dto.QuestionDisplayDto> getRandomizedQuestions() {
        List<Question> questions = new ArrayList<>(questionRepository.findAll());
        Collections.shuffle(questions);
        return formatQuestionsForDisplay(questions);
    }

    public List<com.examsystem.onlineexam.dto.QuestionDisplayDto> getRandomizedQuestionsByTopic(String topic) {
        if (topic == null || topic.isBlank() || "ALL".equalsIgnoreCase(topic.trim())) {
            return getRandomizedQuestions();
        }

        List<Question> questions = new ArrayList<>(questionRepository.findAll().stream()
                .filter(q -> q.getCategory() != null && q.getCategory().trim().equalsIgnoreCase(topic.trim()))
                .toList());

        if (questions.isEmpty()) {
            return getRandomizedQuestions();
        }

        Collections.shuffle(questions);
        return formatQuestionsForDisplay(questions);
    }

    private List<com.examsystem.onlineexam.dto.QuestionDisplayDto> formatQuestionsForDisplay(List<Question> questions) {
        List<com.examsystem.onlineexam.dto.QuestionDisplayDto> displayList = new ArrayList<>();
        String[] labels = {"A", "B", "C", "D"};

        for (Question q : questions) {
            List<Map.Entry<String, String>> rawOptions = new ArrayList<>();
            rawOptions.add(Map.entry("A", q.getOptionA()));
            rawOptions.add(Map.entry("B", q.getOptionB()));
            rawOptions.add(Map.entry("C", q.getOptionC()));
            rawOptions.add(Map.entry("D", q.getOptionD()));
            Collections.shuffle(rawOptions);

            List<com.examsystem.onlineexam.dto.QuestionDisplayDto.OptionDisplay> options = new ArrayList<>();
            for (int i = 0; i < rawOptions.size(); i++) {
                Map.Entry<String, String> entry = rawOptions.get(i);
                options.add(new com.examsystem.onlineexam.dto.QuestionDisplayDto.OptionDisplay(labels[i], entry.getKey(), entry.getValue()));
            }

            displayList.add(new com.examsystem.onlineexam.dto.QuestionDisplayDto(q.getId(), q.getQuestionText(), q.getCategory(), q.getMarks(), options));
        }

        return displayList;
    }

    public Question getQuestionById(Long id) {
        return questionRepository.findById(id).orElse(null);
    }

    @Transactional
    public Question saveQuestion(Question question) {
        return questionRepository.save(question);
    }

    @Transactional
    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }
}
