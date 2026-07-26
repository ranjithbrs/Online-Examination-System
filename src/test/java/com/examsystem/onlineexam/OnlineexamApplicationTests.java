package com.examsystem.onlineexam;

import com.examsystem.onlineexam.dto.ExamSubmissionForm;
import com.examsystem.onlineexam.model.ExamResult;
import com.examsystem.onlineexam.model.Question;
import com.examsystem.onlineexam.model.ViolationLog;
import com.examsystem.onlineexam.repository.ExamResultRepository;
import com.examsystem.onlineexam.repository.QuestionRepository;
import com.examsystem.onlineexam.service.ExamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OnlineexamApplicationTests {

    @Autowired
    private ExamService examService;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ExamResultRepository examResultRepository;

    @Test
    void contextLoads() {
        assertNotNull(examService);
        assertNotNull(questionRepository);
    }

    @Test
    void testQuestionBankSeeded() {
        List<Question> questions = examService.getAllQuestions();
        assertFalse(questions.isEmpty(), "Question bank should be seeded upon app startup");
    }

    @Test
    void testCleanExamEvaluation() {
        List<Question> questions = examService.getAllQuestions();
        ExamSubmissionForm form = new ExamSubmissionForm();
        form.setStudentName("Alice Tester");
        form.setStudentEmail("alice@test.com");
        form.setRollNumber("TEST-001");

        // Submit correct options for all questions
        Map<Long, String> answers = new HashMap<>();
        for (Question q : questions) {
            answers.put(q.getId(), q.getCorrectOption());
        }
        form.setAnswers(answers);

        // Zero security violations
        form.setTabSwitch(0);
        form.setCopyCount(0);
        form.setRightClick(0);
        form.setFullscreenExit(0);
        form.setWindowBlur(0);

        ExamResult result = examService.evaluateAndSaveExam(form);

        assertNotNull(result.getId());
        assertEquals(questions.size(), result.getScore());
        assertEquals(100.0, result.getPercentage());
        assertTrue(result.isPassed());
        assertEquals(100, result.getTrustScore());
        assertEquals("High Integrity", result.getIntegrityStatus());
    }

    @Test
    void testSuspiciousExamEvaluation() {
        List<Question> questions = examService.getAllQuestions();
        ExamSubmissionForm form = new ExamSubmissionForm();
        form.setStudentName("Bob Suspicious");
        form.setStudentEmail("bob@test.com");
        form.setRollNumber("TEST-002");

        Map<Long, String> answers = new HashMap<>();
        if (!questions.isEmpty()) {
            answers.put(questions.get(0).getId(), questions.get(0).getCorrectOption());
        }
        form.setAnswers(answers);

        // Simulate heavy cheating violations
        form.setTabSwitch(4);        // 4 * 5 = 20 pts
        form.setCopyCount(3);        // 3 * 8 = 24 pts
        form.setRightClick(2);       // 2 * 3 = 6 pts
        form.setFullscreenExit(2);   // 2 * 10 = 20 pts
        form.setWindowBlur(3);       // 3 * 4 = 12 pts
        // Total Risk Score = 82 pts -> Trust Score = 18% -> High Risk / Flagged

        ExamResult result = examService.evaluateAndSaveExam(form);

        assertNotNull(result.getId());
        assertEquals(18, result.getTrustScore());
        assertEquals("High Risk / Flagged", result.getIntegrityStatus());

        List<ViolationLog> logs = examService.getViolationLogs(result.getId());
        assertFalse(logs.isEmpty(), "Violation logs should be persisted");
    }
}
