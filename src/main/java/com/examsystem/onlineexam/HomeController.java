package com.examsystem.onlineexam;

import com.examsystem.onlineexam.dto.ExamSubmissionForm;
import com.examsystem.onlineexam.dto.QuestionReviewDto;
import com.examsystem.onlineexam.dto.SnapshotItemDto;
import com.examsystem.onlineexam.model.ExamResult;
import com.examsystem.onlineexam.model.ProctoringSnapshot;
import com.examsystem.onlineexam.model.Question;
import com.examsystem.onlineexam.model.ViolationLog;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.examsystem.onlineexam.service.ExamService;
import com.examsystem.onlineexam.service.PdfExportService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final ExamService examService;
    private final PdfExportService pdfExportService;
    private final ObjectMapper objectMapper;

    public HomeController(ExamService examService, PdfExportService pdfExportService, ObjectMapper objectMapper) {
        this.examService = examService;
        this.pdfExportService = pdfExportService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/")
    public String startPage(Model model, HttpSession session) {
        String studentName = (String) session.getAttribute("studentName");
        String studentEmail = (String) session.getAttribute("studentEmail");
        String rollNumber = (String) session.getAttribute("rollNumber");

        model.addAttribute("studentName", studentName != null ? studentName : "");
        model.addAttribute("studentEmail", studentEmail != null ? studentEmail : "");
        model.addAttribute("rollNumber", rollNumber != null ? rollNumber : "");

        List<Question> questions = examService.getAllQuestions();
        List<String> topics = examService.getDistinctTopics();
        Map<String, Long> topicCounts = questions.stream()
                .collect(Collectors.groupingBy(
                        q -> (q.getCategory() != null && !q.getCategory().isBlank()) ? q.getCategory().trim() : "General",
                        Collectors.counting()
                ));
        model.addAttribute("totalQuestions", questions.size());
        model.addAttribute("topics", topics);
        model.addAttribute("topicCounts", topicCounts);
        model.addAttribute("examDurationMinutes", 10);

        return "start";
    }

    @GetMapping("/health")
    @ResponseBody
    public Map<String, String> healthCheck() {
        return Map.of("status", "UP");
    }

    @PostMapping("/start-exam")
    public String registerAndStart(
            @RequestParam String studentName,
            @RequestParam String studentEmail,
            @RequestParam String rollNumber,
            @RequestParam(required = false, defaultValue = "ALL") String selectedTopic,
            HttpSession session) {

        session.setAttribute("studentName", studentName);
        session.setAttribute("studentEmail", studentEmail);
        session.setAttribute("rollNumber", rollNumber);
        session.setAttribute("selectedTopic", selectedTopic);
        session.removeAttribute("sessionQuestions");
        session.removeAttribute("examStartTime");
        session.removeAttribute("examDraft");

        return "redirect:/exam";
    }

    @GetMapping("/exam")
    public String examPage(Model model, HttpSession session) {
        String studentName = (String) session.getAttribute("studentName");
        String studentEmail = (String) session.getAttribute("studentEmail");
        String rollNumber = (String) session.getAttribute("rollNumber");
        String selectedTopic = (String) session.getAttribute("selectedTopic");
        if (selectedTopic == null || selectedTopic.isBlank()) {
            selectedTopic = "ALL";
        }

        if (studentName == null || studentName.isBlank()) {
            studentName = "Candidate User";
            studentEmail = "candidate@example.com";
            rollNumber = "REG-" + (System.currentTimeMillis() % 10000);
        }

        @SuppressWarnings("unchecked")
        List<com.examsystem.onlineexam.dto.QuestionDisplayDto> questions = 
                (List<com.examsystem.onlineexam.dto.QuestionDisplayDto>) session.getAttribute("sessionQuestions");
        if (questions == null || questions.isEmpty()) {
            questions = examService.getRandomizedQuestionsByTopic(selectedTopic);
            session.setAttribute("sessionQuestions", questions);
        }

        // Server-Side Timer Start Initialization
        if (session.getAttribute("examStartTime") == null) {
            session.setAttribute("examStartTime", System.currentTimeMillis());
        }

        // Draft Auto-Save Recovery
        com.examsystem.onlineexam.dto.ExamDraftDto draft = 
                (com.examsystem.onlineexam.dto.ExamDraftDto) session.getAttribute("examDraft");
        model.addAttribute("draftAnswers", draft != null ? draft.getAnswers() : new HashMap<>());
        model.addAttribute("draftMarkedQuestions", draft != null ? draft.getMarkedQuestions() : new ArrayList<>());
        model.addAttribute("draft", draft != null ? draft : new com.examsystem.onlineexam.dto.ExamDraftDto());

        String topicDisplayName = "ALL".equalsIgnoreCase(selectedTopic) 
                ? "All Topics (Comprehensive)" 
                : selectedTopic;

        model.addAttribute("studentName", studentName);
        model.addAttribute("studentEmail", studentEmail);
        model.addAttribute("rollNumber", rollNumber);
        model.addAttribute("questions", questions);
        model.addAttribute("selectedTopic", selectedTopic);
        model.addAttribute("topicDisplayName", topicDisplayName);
        model.addAttribute("durationMinutes", 10);

        return "exam";
    }

    @PostMapping("/submit")
    public String submitExam(
            @RequestParam(required = false, defaultValue = "Anonymous Candidate") String studentName,
            @RequestParam(required = false, defaultValue = "candidate@example.com") String studentEmail,
            @RequestParam(required = false, defaultValue = "REG-0000") String rollNumber,
            @RequestParam(required = false) String selectedTopic,
            @RequestParam(name = "examQuestionIds", required = false) List<Long> examQuestionIds,
            @RequestParam(defaultValue = "0") int tabSwitch,
            @RequestParam(defaultValue = "0") int copyCount,
            @RequestParam(defaultValue = "0") int rightClick,
            @RequestParam(defaultValue = "0") int fullscreenExit,
            @RequestParam(defaultValue = "0") int windowBlur,
            @RequestParam(defaultValue = "0") int audioSpikes,
            @RequestParam(defaultValue = "0") int timeTakenSeconds,
            @RequestParam Map<String, String> allParams,
            HttpSession session) {

        ExamSubmissionForm form = new ExamSubmissionForm();
        form.setStudentName(studentName);
        form.setStudentEmail(studentEmail);
        form.setRollNumber(rollNumber);

        if (selectedTopic == null || selectedTopic.isBlank()) {
            selectedTopic = allParams.get("selectedTopic");
        }
        if (selectedTopic == null || selectedTopic.isBlank()) {
            selectedTopic = (String) session.getAttribute("selectedTopic");
        }
        form.setSelectedTopic(selectedTopic != null && !selectedTopic.isBlank() ? selectedTopic : "ALL");

        if (examQuestionIds == null || examQuestionIds.isEmpty()) {
            @SuppressWarnings("unchecked")
            List<com.examsystem.onlineexam.dto.QuestionDisplayDto> sessQ = 
                    (List<com.examsystem.onlineexam.dto.QuestionDisplayDto>) session.getAttribute("sessionQuestions");
            if (sessQ != null && !sessQ.isEmpty()) {
                examQuestionIds = sessQ.stream().map(com.examsystem.onlineexam.dto.QuestionDisplayDto::getId).toList();
            }
        }
        form.setExamQuestionIds(examQuestionIds != null ? examQuestionIds : new ArrayList<>());

        form.setTabSwitch(tabSwitch);
        form.setCopyCount(copyCount);
        form.setRightClick(rightClick);
        form.setFullscreenExit(fullscreenExit);
        form.setWindowBlur(windowBlur);
        form.setAudioSpikes(audioSpikes);

        if (form.getAudioSpikes() == 0 && allParams.containsKey("audioSpikes")) {
            try {
                form.setAudioSpikes(Integer.parseInt(allParams.get("audioSpikes")));
            } catch (Exception ignored) {}
        }

        // Calculate time taken from server session or fallback to request param
        Long examStartTime = (Long) session.getAttribute("examStartTime");
        int elapsedSeconds = timeTakenSeconds;
        if (examStartTime != null) {
            elapsedSeconds = (int) ((System.currentTimeMillis() - examStartTime) / 1000);
            session.removeAttribute("examStartTime");
        }
        form.setTimeTakenSeconds(elapsedSeconds);

        if ("true".equalsIgnoreCase(allParams.get("disqualified"))) {
            form.setDisqualified(true);
            form.setDisqualificationReason(allParams.getOrDefault("disqualificationReason", "Exceeded security strike threshold"));
            if (form.getDisqualificationReason() != null 
                    && form.getDisqualificationReason().toLowerCase().contains("audio") 
                    && form.getAudioSpikes() == 0) {
                form.setAudioSpikes(1);
            }
        }

        Map<Long, String> answers = new HashMap<>();
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            if (entry.getKey().startsWith("q_")) {
                try {
                    Long questionId = Long.parseLong(entry.getKey().substring(2));
                    answers.put(questionId, entry.getValue());
                } catch (NumberFormatException ignored) {
                }
            }
        }
        form.setAnswers(answers);

        // Parse proctoring webcam snapshots if provided
        String snapshotPayload = allParams.get("snapshotPayload");
        if (snapshotPayload != null && !snapshotPayload.isBlank()) {
            try {
                List<SnapshotItemDto> snapshots =
                        objectMapper.readValue(snapshotPayload, new TypeReference<List<SnapshotItemDto>>() {});
                form.setSnapshots(snapshots);
            } catch (Exception ignored) {
            }
        }

        ExamResult savedResult = examService.evaluateAndSaveExam(form);

        // Store answers in session for rendering detail review
        session.setAttribute("userAnswers_" + savedResult.getId(), answers);
        session.removeAttribute("sessionQuestions");
        session.removeAttribute("examDraft");

        return "redirect:/result/" + savedResult.getId();
    }

    @GetMapping("/result/{id}")
    public String resultPage(@PathVariable Long id, Model model, HttpSession session) {
        ExamResult result = examService.getExamResult(id);
        if (result == null) {
            return "redirect:/";
        }

        // Load question reviews directly from DB-persisted ExamResult
        List<QuestionReviewDto> questionReviews = examService.getQuestionReviews(result);
        List<ViolationLog> violationLogs = examService.getViolationLogs(id);
        List<ProctoringSnapshot> snapshots = examService.getProctoringSnapshots(id);

        model.addAttribute("result", result);
        model.addAttribute("questionReviews", questionReviews);
        model.addAttribute("violationLogs", violationLogs);
        model.addAttribute("snapshots", snapshots);

        return "result";
    }

    @GetMapping("/result/{id}/pdf")
    public ResponseEntity<byte[]> downloadResultPdf(@PathVariable Long id) {
        ExamResult result = examService.getExamResult(id);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] pdfBytes = pdfExportService.generateExamReportPdf(result);
        String cleanRoll = result.getRollNumber() != null ? result.getRollNumber().replaceAll("[^a-zA-Z0-9_-]", "") : "CANDIDATE";
        String filename = "Exam_Report_" + cleanRoll + "_" + id + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfBytes.length)
                .body(pdfBytes);
    }

    @GetMapping("/history")
    public String historyPage(Model model) {
        List<ExamResult> results = examService.getAllExamResults();
        com.examsystem.onlineexam.dto.AuditDashboardDto metrics = examService.getAuditDashboardMetrics();
        model.addAttribute("results", results);
        model.addAttribute("metrics", metrics);
        return "history";
    }

    @GetMapping("/admin/history/export/csv")
    public ResponseEntity<byte[]> exportAuditHistoryCsv() {
        String csv = examService.exportExamResultsToCsv();
        byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);
        String filename = "Exam_Audit_Report_" + java.time.LocalDate.now() + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .contentLength(bytes.length)
                .body(bytes);
    }

    // Teacher Question Management Routes
    @GetMapping("/admin/questions")
    public String manageQuestions(Model model) {
        List<Question> questions = examService.getAllQuestions();
        int totalQuestions = questions.size();
        int totalMarks = questions.stream().mapToInt(Question::getMarks).sum();

        Map<String, Long> topicCounts = questions.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        q -> (q.getCategory() != null && !q.getCategory().isBlank()) ? q.getCategory() : "General",
                        java.util.stream.Collectors.counting()
                ));
        List<String> topics = new java.util.ArrayList<>(topicCounts.keySet());
        java.util.Collections.sort(topics);

        model.addAttribute("questions", questions);
        model.addAttribute("totalQuestions", totalQuestions);
        model.addAttribute("totalMarks", totalMarks);
        model.addAttribute("topicCounts", topicCounts);
        model.addAttribute("topics", topics);
        model.addAttribute("newQuestion", new com.examsystem.onlineexam.dto.QuestionFormDto());
        return "question-manage";
    }

    @PostMapping("/admin/questions/save")
    public String saveQuestion(@ModelAttribute com.examsystem.onlineexam.dto.QuestionFormDto dto) {
        Question q = dto.getId() != null ? examService.getQuestionById(dto.getId()) : new Question();
        if (q == null) {
            q = new Question();
        }
        q.setQuestionText(dto.getQuestionText());
        q.setOptionA(dto.getOptionA());
        q.setOptionB(dto.getOptionB());
        q.setOptionC(dto.getOptionC());
        q.setOptionD(dto.getOptionD());
        q.setCorrectOption(dto.getCorrectOption() != null ? dto.getCorrectOption().trim().toUpperCase() : "A");
        q.setExplanation(dto.getExplanation());
        q.setCategory(dto.getCategory() != null && !dto.getCategory().isBlank() ? dto.getCategory() : "General");
        q.setMarks(dto.getMarks() > 0 ? dto.getMarks() : 1);

        examService.saveQuestion(q);
        return "redirect:/admin/questions";
    }

    @GetMapping("/admin/questions/delete/{id}")
    public String deleteQuestion(@PathVariable Long id) {
        examService.deleteQuestion(id);
        return "redirect:/admin/questions";
    }

    @GetMapping("/admin/questions/export/json")
    public ResponseEntity<byte[]> exportQuestionsJson() throws Exception {
        List<Question> questions = examService.getAllQuestions();
        byte[] jsonBytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(questions);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Question_Bank_Export.json\"")
                .contentType(MediaType.APPLICATION_JSON)
                .contentLength(jsonBytes.length)
                .body(jsonBytes);
    }

    @GetMapping("/admin/questions/export/csv")
    public ResponseEntity<byte[]> exportQuestionsCsv() {
        List<Question> questions = examService.getAllQuestions();
        StringBuilder csv = new StringBuilder();
        csv.append("\"ID\",\"Category\",\"Marks\",\"Question\",\"Option A\",\"Option B\",\"Option C\",\"Option D\",\"Correct Option\",\"Explanation\"\n");

        for (Question q : questions) {
            csv.append("\"").append(q.getId()).append("\",");
            csv.append("\"").append(escapeCsv(q.getCategory())).append("\",");
            csv.append("\"").append(q.getMarks()).append("\",");
            csv.append("\"").append(escapeCsv(q.getQuestionText())).append("\",");
            csv.append("\"").append(escapeCsv(q.getOptionA())).append("\",");
            csv.append("\"").append(escapeCsv(q.getOptionB())).append("\",");
            csv.append("\"").append(escapeCsv(q.getOptionC())).append("\",");
            csv.append("\"").append(escapeCsv(q.getOptionD())).append("\",");
            csv.append("\"").append(escapeCsv(q.getCorrectOption())).append("\",");
            csv.append("\"").append(escapeCsv(q.getExplanation())).append("\"\n");
        }

        byte[] csvBytes = csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Question_Bank_Export.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .contentLength(csvBytes.length)
                .body(csvBytes);
    }

    private String escapeCsv(String input) {
        if (input == null) return "";
        return input.replace("\"", "\"\"");
    }

    @PostMapping("/admin/questions/import")
    public String importQuestions(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file == null || file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a valid CSV or JSON file to import.");
            return "redirect:/admin/questions";
        }

        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";
        int importedCount = 0;

        try {
            if (originalFilename.endsWith(".json")) {
                List<Question> questions = objectMapper.readValue(file.getInputStream(), new TypeReference<List<Question>>() {});
                if (questions != null) {
                    for (Question q : questions) {
                        if (q.getQuestionText() != null && !q.getQuestionText().isBlank()) {
                            q.setId(null);
                            if (q.getMarks() <= 0) q.setMarks(1);
                            if (q.getCategory() == null || q.getCategory().isBlank()) q.setCategory("General");
                            if (q.getCorrectOption() == null || q.getCorrectOption().isBlank()) q.setCorrectOption("A");
                            examService.saveQuestion(q);
                            importedCount++;
                        }
                    }
                }
            } else if (originalFilename.endsWith(".csv")) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    boolean isFirstLine = true;
                    while ((line = reader.readLine()) != null) {
                        if (line.isBlank()) continue;
                        if (isFirstLine) {
                            isFirstLine = false;
                            if (line.toLowerCase().contains("question") || line.toLowerCase().contains("category")) {
                                continue;
                            }
                        }

                        List<String> tokens = parseCsvLine(line);
                        if (tokens.size() >= 9) {
                            String category = tokens.size() > 1 && !tokens.get(1).isBlank() ? tokens.get(1) : "General";
                            int marks = 1;
                            try {
                                marks = Integer.parseInt(tokens.get(2).trim());
                            } catch (NumberFormatException ignored) {}
                            String questionText = tokens.get(3);
                            String optA = tokens.get(4);
                            String optB = tokens.get(5);
                            String optC = tokens.get(6);
                            String optD = tokens.get(7);
                            String correctOption = tokens.get(8).trim().toUpperCase();
                            String explanation = tokens.size() > 9 ? tokens.get(9) : "";

                            if (!questionText.isBlank()) {
                                Question q = new Question();
                                q.setCategory(category);
                                q.setMarks(marks > 0 ? marks : 1);
                                q.setQuestionText(questionText);
                                q.setOptionA(optA);
                                q.setOptionB(optB);
                                q.setOptionC(optC);
                                q.setOptionD(optD);
                                q.setCorrectOption(correctOption.matches("[A-D]") ? correctOption : "A");
                                q.setExplanation(explanation);

                                examService.saveQuestion(q);
                                importedCount++;
                            }
                        }
                    }
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Unsupported file type. Please upload a .csv or .json file.");
                return "redirect:/admin/questions";
            }

            if (importedCount > 0) {
                redirectAttributes.addFlashAttribute("successMessage", "Successfully imported " + importedCount + " question(s) into the bank!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "No valid questions were found in the uploaded file.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to import questions: " + e.getMessage());
        }

        return "redirect:/admin/questions";
    }

    private List<String> parseCsvLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                    sb.append('\"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString().trim());
        return tokens;
    }
}