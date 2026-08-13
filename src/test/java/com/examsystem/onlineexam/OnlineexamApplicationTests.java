package com.examsystem.onlineexam;

import com.examsystem.onlineexam.dto.ExamSubmissionForm;
import com.examsystem.onlineexam.dto.QuestionReviewDto;
import com.examsystem.onlineexam.model.ExamResult;
import com.examsystem.onlineexam.model.Question;
import com.examsystem.onlineexam.model.ViolationLog;
import com.examsystem.onlineexam.repository.ExamResultRepository;
import com.examsystem.onlineexam.repository.QuestionRepository;
import com.examsystem.onlineexam.service.ExamService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OnlineexamApplicationTests {

    @Autowired
    private ExamService examService;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ExamResultRepository examResultRepository;

    @Autowired
    private MockMvc mockMvc;

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

        Map<Long, String> answers = new HashMap<>();
        for (Question q : questions) {
            answers.put(q.getId(), q.getCorrectOption());
        }
        form.setAnswers(answers);

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

        form.setTabSwitch(4);        // 4 * 5 = 20 pts
        form.setCopyCount(3);        // 3 * 8 = 24 pts
        form.setRightClick(2);       // 2 * 3 = 6 pts
        form.setFullscreenExit(2);   // 2 * 10 = 20 pts
        form.setWindowBlur(3);       // 3 * 4 = 12 pts

        ExamResult result = examService.evaluateAndSaveExam(form);

        assertNotNull(result.getId());
        assertEquals(18, result.getTrustScore());
        assertEquals("High Risk / Flagged", result.getIntegrityStatus());

        List<ViolationLog> logs = examService.getViolationLogs(result.getId());
        assertFalse(logs.isEmpty(), "Violation logs should be persisted");
    }

    @Test
    void testAnswerPersistenceInDatabase() {
        List<Question> questions = examService.getAllQuestions();
        if (questions.isEmpty()) return;

        Question firstQ = questions.get(0);
        ExamSubmissionForm form = new ExamSubmissionForm();
        form.setStudentName("Charlie Persistent");
        form.setStudentEmail("charlie@test.com");
        form.setRollNumber("TEST-003");

        Map<Long, String> answers = new HashMap<>();
        answers.put(firstQ.getId(), firstQ.getCorrectOption());
        form.setAnswers(answers);

        ExamResult savedResult = examService.evaluateAndSaveExam(form);

        // Fetch from DB using repository
        ExamResult fetchedResult = examService.getExamResult(savedResult.getId());
        assertNotNull(fetchedResult);
        assertNotNull(fetchedResult.getSelectedAnswers());
        assertEquals(firstQ.getCorrectOption(), fetchedResult.getSelectedAnswers().get(firstQ.getId()));

        // Test review generation directly from entity
        List<QuestionReviewDto> reviews = examService.getQuestionReviews(fetchedResult);
        assertFalse(reviews.isEmpty());
        QuestionReviewDto firstReview = reviews.stream()
                .filter(r -> r.getQuestion().getId().equals(firstQ.getId()))
                .findFirst()
                .orElse(null);
        assertNotNull(firstReview);
        assertTrue(firstReview.isCorrect());
    }

    @Test
    void testQuestionCrudOperations() {
        int initialCount = examService.getAllQuestions().size();

        Question newQ = new Question(
            "What is Spring Data JPA?",
            "An abstraction layer on top of JPA to reduce boilerplate DAO code",
            "A web browser extension",
            "A JavaScript framework",
            "A hardware component",
            "A",
            "Spring Data JPA simplifies data access for relational databases.",
            "Spring Data",
            1
        );

        Question saved = examService.saveQuestion(newQ);
        assertNotNull(saved.getId());
        assertEquals(initialCount + 1, examService.getAllQuestions().size());

        // Update
        saved.setCategory("Updated Category");
        examService.saveQuestion(saved);
        assertEquals("Updated Category", examService.getQuestionById(saved.getId()).getCategory());

        // Delete
        examService.deleteQuestion(saved.getId());
        assertEquals(initialCount, examService.getAllQuestions().size());
    }

    @Test
    void testRestApiEndpoints() throws Exception {
        mockMvc.perform(get("/api/v1/questions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        mockMvc.perform(get("/api/v1/results")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
