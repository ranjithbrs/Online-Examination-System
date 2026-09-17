package com.examsystem.onlineexam;

import com.examsystem.onlineexam.dto.ExamSubmissionForm;
import com.examsystem.onlineexam.dto.QuestionReviewDto;
import com.examsystem.onlineexam.model.ExamResult;
import com.examsystem.onlineexam.model.Question;
import com.examsystem.onlineexam.model.ViolationLog;
import com.examsystem.onlineexam.repository.ExamResultRepository;
import com.examsystem.onlineexam.repository.QuestionRepository;
import com.examsystem.onlineexam.service.ExamService;
import com.examsystem.onlineexam.service.PdfExportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OnlineexamApplicationTests {

    @Autowired
    private ExamService examService;

    @Autowired
    private PdfExportService pdfExportService;

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

        form.setTabSwitch(2);        // 2 * 5 = 10 pts
        form.setCopyCount(3);        // 3 * 8 = 24 pts
        form.setRightClick(3);       // 3 * 3 = 9 pts
        form.setFullscreenExit(1);   // 1 * 10 = 10 pts
        form.setWindowBlur(3);       // 3 * 4 = 12 pts

        ExamResult result = examService.evaluateAndSaveExam(form);

        assertNotNull(result.getId());
        assertEquals(35, result.getTrustScore());
        assertEquals("High Risk / Flagged", result.getIntegrityStatus());

        List<ViolationLog> logs = examService.getViolationLogs(result.getId());
        assertFalse(logs.isEmpty(), "Violation logs should be persisted");
    }

    @Test
    void testRandomizedQuestionsGeneration() {
        List<com.examsystem.onlineexam.dto.QuestionDisplayDto> randomized = examService.getRandomizedQuestions();
        assertFalse(randomized.isEmpty());
        assertEquals(examService.getAllQuestions().size(), randomized.size());

        for (com.examsystem.onlineexam.dto.QuestionDisplayDto q : randomized) {
            assertEquals(4, q.getOptions().size());
            List<String> labels = q.getOptions().stream().map(com.examsystem.onlineexam.dto.QuestionDisplayDto.OptionDisplay::getLabel).toList();
            List<String> values = q.getOptions().stream().map(com.examsystem.onlineexam.dto.QuestionDisplayDto.OptionDisplay::getValue).sorted().toList();
            assertEquals(List.of("A", "B", "C", "D"), labels);
            assertEquals(List.of("A", "B", "C", "D"), values);
        }
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

    @Test
    void testServerSideTimerEnforcementAndOvertime() {
        // Test Normal On-Time Exam (300 seconds)
        ExamSubmissionForm normalForm = new ExamSubmissionForm();
        normalForm.setStudentName("OnTime Student");
        normalForm.setStudentEmail("ontime@test.com");
        normalForm.setRollNumber("TIME-001");
        normalForm.setTimeTakenSeconds(300);

        ExamResult normalResult = examService.evaluateAndSaveExam(normalForm);
        assertFalse(normalResult.isOvertime());
        assertEquals("5m 00s", normalResult.getFormattedTimeTaken());

        // Test Overtime Exam (750 seconds > 630s threshold)
        ExamSubmissionForm overtimeForm = new ExamSubmissionForm();
        overtimeForm.setStudentName("Overtime Student");
        overtimeForm.setStudentEmail("overtime@test.com");
        overtimeForm.setRollNumber("TIME-002");
        overtimeForm.setTimeTakenSeconds(750);

        ExamResult overtimeResult = examService.evaluateAndSaveExam(overtimeForm);
        assertTrue(overtimeResult.isOvertime());
        assertEquals("12m 30s", overtimeResult.getFormattedTimeTaken());
        assertTrue(overtimeResult.getRiskScore() >= 25, "Should include overtime penalty");

        List<ViolationLog> logs = examService.getViolationLogs(overtimeResult.getId());
        boolean hasOvertimeLog = logs.stream().anyMatch(l -> "OVERTIME_SUBMISSION".equals(l.getViolationType()));
        assertTrue(hasOvertimeLog, "Violation log must record OVERTIME_SUBMISSION");
    }

    @Test
    void testExamDraftAutoSaveAndRetrieval() throws Exception {
        org.springframework.mock.web.MockHttpSession session = new org.springframework.mock.web.MockHttpSession();

        String draftJson = """
            {
                "answers": { "1": "A", "2": "B" },
                "tabSwitch": 1,
                "copyCount": 0,
                "rightClick": 1,
                "fullscreenExit": 0,
                "windowBlur": 0
            }
            """;

        mockMvc.perform(post("/api/v1/exam/draft")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(draftJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SAVED"))
                .andExpect(jsonPath("$.savedAnswersCount").value(2));

        mockMvc.perform(get("/api/v1/exam/draft")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tabSwitch").value(1))
                .andExpect(jsonPath("$.rightClick").value(1))
                .andExpect(jsonPath("$.answers.1").value("A"))
                .andExpect(jsonPath("$.answers.2").value("B"));
    }

    @Test
    void testDisqualificationAndStrikeSystem() {
        // Case 1: Disqualified by 3 fullscreen exits strike threshold
        ExamSubmissionForm strikeForm = new ExamSubmissionForm();
        strikeForm.setStudentName("Strike Candidate");
        strikeForm.setStudentEmail("strike@test.com");
        strikeForm.setRollNumber("STRIKE-001");
        strikeForm.setFullscreenExit(3);
        strikeForm.setTabSwitch(0);

        // Give correct answers, candidate should still fail due to disqualification
        List<Question> questions = examService.getAllQuestions();
        Map<Long, String> answers = new HashMap<>();
        for (Question q : questions) {
            answers.put(q.getId(), q.getCorrectOption());
        }
        strikeForm.setAnswers(answers);

        ExamResult strikeResult = examService.evaluateAndSaveExam(strikeForm);
        assertTrue(strikeResult.isDisqualified(), "Should be disqualified after 3 fullscreen exits");
        assertFalse(strikeResult.isPassed(), "Disqualified candidate must fail even with 100% correct answers");
        assertEquals(0, strikeResult.getTrustScore(), "Disqualified candidate must have 0% trust score");
        assertEquals("DISQUALIFIED", strikeResult.getIntegrityStatus());
        assertNotNull(strikeResult.getDisqualificationReason());
        assertTrue(strikeResult.getDisqualificationReason().contains("3+ strikes"));

        List<ViolationLog> strikeLogs = examService.getViolationLogs(strikeResult.getId());
        assertTrue(strikeLogs.stream().anyMatch(l -> "SECURITY_DISQUALIFICATION".equals(l.getViolationType())),
                "Violation logs should contain SECURITY_DISQUALIFICATION");

        // Case 2: Explicit client lockout disqualification (e.g. devtools opened)
        ExamSubmissionForm lockoutForm = new ExamSubmissionForm();
        lockoutForm.setStudentName("DevTools Cheater");
        lockoutForm.setStudentEmail("cheater@test.com");
        lockoutForm.setRollNumber("DEV-999");
        lockoutForm.setDisqualified(true);
        lockoutForm.setDisqualificationReason("Unauthorized Developer Tools / Console opened during exam session");

        ExamResult lockoutResult = examService.evaluateAndSaveExam(lockoutForm);
        assertTrue(lockoutResult.isDisqualified());
        assertFalse(lockoutResult.isPassed());
        assertEquals(0, lockoutResult.getTrustScore());
        assertEquals("DISQUALIFIED", lockoutResult.getIntegrityStatus());
        assertEquals("Unauthorized Developer Tools / Console opened during exam session", lockoutResult.getDisqualificationReason());
    }

    @Test
    void testPdfReportGeneration() {
        ExamSubmissionForm form = new ExamSubmissionForm();
        form.setStudentName("PDF Candidate");
        form.setStudentEmail("pdf@example.com");
        form.setRollNumber("PDF-101");
        form.setTimeTakenSeconds(250);

        ExamResult result = examService.evaluateAndSaveExam(form);
        assertNotNull(result.getId());

        byte[] pdfBytes = pdfExportService.generateExamReportPdf(result);
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 500, "PDF document should contain bytes");

        // Verify PDF Header Magic Number (%PDF-)
        String pdfHeader = new String(pdfBytes, 0, 5);
        assertEquals("%PDF-", pdfHeader, "Generated file must be a valid PDF format");
    }

    @Test
    void testPdfDownloadEndpoint() throws Exception {
        ExamSubmissionForm form = new ExamSubmissionForm();
        form.setStudentName("Endpoint Tester");
        form.setStudentEmail("endpoint@example.com");
        form.setRollNumber("END-202");

        ExamResult result = examService.evaluateAndSaveExam(form);

        mockMvc.perform(get("/result/" + result.getId() + "/pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE))
                .andExpect(header().exists(HttpHeaders.CONTENT_DISPOSITION))
                .andExpect(resultMatcher -> {
                    byte[] content = resultMatcher.getResponse().getContentAsByteArray();
                    assertTrue(content.length > 500);
                    assertEquals("%PDF-", new String(content, 0, 5));
                });

        // 404 for non-existent result ID
        mockMvc.perform(get("/result/999999/pdf"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAdminQuestionBankMetricsAndTopicAnalytics() throws Exception {
        mockMvc.perform(get("/admin/questions"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("questions"))
                .andExpect(model().attributeExists("totalQuestions"))
                .andExpect(model().attributeExists("totalMarks"))
                .andExpect(model().attributeExists("topics"))
                .andExpect(model().attributeExists("topicCounts"))
                .andExpect(model().attribute("totalQuestions", org.hamcrest.Matchers.greaterThanOrEqualTo(6)))
                .andExpect(model().attribute("totalMarks", org.hamcrest.Matchers.greaterThanOrEqualTo(6)));
    }

    @Test
    void testExportQuestionsJson() throws Exception {
        mockMvc.perform(get("/admin/questions/export/json"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Question_Bank_Export.json\""))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()", org.hamcrest.Matchers.greaterThanOrEqualTo(6)))
                .andExpect(jsonPath("$[0].questionText").exists())
                .andExpect(jsonPath("$[0].category").exists());
    }

    @Test
    void testExportQuestionsCsv() throws Exception {
        mockMvc.perform(get("/admin/questions/export/csv"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Question_Bank_Export.csv\""))
                .andExpect(resultMatcher -> {
                    String content = resultMatcher.getResponse().getContentAsString();
                    assertTrue(content.startsWith("\"ID\",\"Category\",\"Marks\",\"Question\""));
                    assertTrue(content.contains("Java Fundamentals"));
                });
    }

    @Test
    void testImportQuestionsJson() throws Exception {
        int initialCount = examService.getAllQuestions().size();
        String jsonPayload = """
            [
                {
                    "questionText": "What is Docker?",
                    "optionA": "A containerization platform",
                    "optionB": "A relational database",
                    "optionC": "A text editor",
                    "optionD": "A compiler",
                    "correctOption": "A",
                    "category": "DevOps",
                    "marks": 2,
                    "explanation": "Docker automates application deployment in lightweight containers."
                }
            ]
            """;

        MockMultipartFile file = new MockMultipartFile(
                "file", "questions.json", "application/json", jsonPayload.getBytes(java.nio.charset.StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/admin/questions/import").file(file))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/questions"))
                .andExpect(flash().attributeExists("successMessage"));

        assertEquals(initialCount + 1, examService.getAllQuestions().size());
    }

    @Test
    void testImportQuestionsCsv() throws Exception {
        int initialCount = examService.getAllQuestions().size();
        String csvPayload = "\"ID\",\"Category\",\"Marks\",\"Question\",\"Option A\",\"Option B\",\"Option C\",\"Option D\",\"Correct Option\",\"Explanation\"\n"
                + "\"\",\"Cloud Computing\",\"2\",\"What is AWS S3?\",\"Simple Storage Service\",\"A relational DB\",\"A load balancer\",\"An OS\",\"A\",\"AWS S3 provides object storage.\"\n";

        MockMultipartFile file = new MockMultipartFile(
                "file", "questions.csv", "text/csv", csvPayload.getBytes(java.nio.charset.StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/admin/questions/import").file(file))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/questions"))
                .andExpect(flash().attributeExists("successMessage"));

        assertEquals(initialCount + 1, examService.getAllQuestions().size());
    }

    @Test
    void testImportQuestionsInvalidFormat() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "invalid.txt", "text/plain", "Random text".getBytes(java.nio.charset.StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/admin/questions/import").file(file))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/questions"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    void testTopicFilteredQuestionGeneration() {
        List<com.examsystem.onlineexam.dto.QuestionDisplayDto> dbQuestions = 
                examService.getRandomizedQuestionsByTopic("Database Systems");
        assertFalse(dbQuestions.isEmpty(), "Database Systems topic should contain questions");
        for (com.examsystem.onlineexam.dto.QuestionDisplayDto q : dbQuestions) {
            assertEquals("Database Systems", q.getCategory());
        }

        List<String> distinctTopics = examService.getDistinctTopics();
        assertFalse(distinctTopics.isEmpty());
        assertTrue(distinctTopics.contains("Database Systems"));
    }

    @Test
    void testTopicFilteredExamStartAndSessionIsolation() throws Exception {
        org.springframework.mock.web.MockHttpSession session = new org.springframework.mock.web.MockHttpSession();

        // 1. Register with topic selection
        mockMvc.perform(post("/start-exam")
                .session(session)
                .param("studentName", "Topic Candidate")
                .param("studentEmail", "topic@test.com")
                .param("rollNumber", "TOPIC-001")
                .param("selectedTopic", "Database Systems"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/exam"));

        // 2. Open exam page and verify questions are filtered
        mockMvc.perform(get("/exam").session(session))
                .andExpect(status().isOk())
                .andExpect(model().attribute("selectedTopic", "Database Systems"))
                .andExpect(model().attribute("topicDisplayName", "Database Systems"))
                .andExpect(model().attributeExists("questions"));

        @SuppressWarnings("unchecked")
        List<com.examsystem.onlineexam.dto.QuestionDisplayDto> sessionQuestions = 
                (List<com.examsystem.onlineexam.dto.QuestionDisplayDto>) session.getAttribute("sessionQuestions");
        assertNotNull(sessionQuestions);
        assertFalse(sessionQuestions.isEmpty());
        for (com.examsystem.onlineexam.dto.QuestionDisplayDto q : sessionQuestions) {
            assertEquals("Database Systems", q.getCategory());
        }
    }
}






