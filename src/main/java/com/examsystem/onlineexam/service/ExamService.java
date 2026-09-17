package com.examsystem.onlineexam.service;

import com.examsystem.onlineexam.dto.ExamSubmissionForm;
import com.examsystem.onlineexam.dto.QuestionReviewDto;
import com.examsystem.onlineexam.model.ExamResult;
import com.examsystem.onlineexam.model.Question;
import com.examsystem.onlineexam.model.ViolationLog;
import com.examsystem.onlineexam.repository.ExamResultRepository;
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

    public ExamService(QuestionRepository questionRepository,
                       ExamResultRepository examResultRepository,
                       ViolationLogRepository violationLogRepository) {
        this.questionRepository = questionRepository;
        this.examResultRepository = examResultRepository;
        this.violationLogRepository = violationLogRepository;
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

    public List<com.examsystem.onlineexam.dto.QuestionDisplayDto> getRandomizedQuestions() {
        List<Question> questions = new ArrayList<>(questionRepository.findAll());
        Collections.shuffle(questions);

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
