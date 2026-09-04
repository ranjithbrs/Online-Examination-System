package com.examsystem.onlineexam;

import com.examsystem.onlineexam.dto.ExamSubmissionForm;
import com.examsystem.onlineexam.dto.QuestionReviewDto;
import com.examsystem.onlineexam.model.ExamResult;
import com.examsystem.onlineexam.model.Question;
import com.examsystem.onlineexam.model.ViolationLog;
import com.examsystem.onlineexam.service.ExamService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    private final ExamService examService;

    public HomeController(ExamService examService) {
        this.examService = examService;
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
        model.addAttribute("totalQuestions", questions.size());
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
            HttpSession session) {

        session.setAttribute("studentName", studentName);
        session.setAttribute("studentEmail", studentEmail);
        session.setAttribute("rollNumber", rollNumber);

        return "redirect:/exam";
    }

    @GetMapping("/exam")
    public String examPage(Model model, HttpSession session) {
        String studentName = (String) session.getAttribute("studentName");
        String studentEmail = (String) session.getAttribute("studentEmail");
        String rollNumber = (String) session.getAttribute("rollNumber");

        if (studentName == null || studentName.isBlank()) {
            studentName = "Candidate User";
            studentEmail = "candidate@example.com";
            rollNumber = "REG-" + (System.currentTimeMillis() % 10000);
        }

        List<Question> questions = examService.getAllQuestions();

        model.addAttribute("studentName", studentName);
        model.addAttribute("studentEmail", studentEmail);
        model.addAttribute("rollNumber", rollNumber);
        model.addAttribute("questions", questions);
        model.addAttribute("durationMinutes", 10);

        return "exam";
    }

    @PostMapping("/submit")
    public String submitExam(
            @RequestParam(required = false, defaultValue = "Anonymous Candidate") String studentName,
            @RequestParam(required = false, defaultValue = "candidate@example.com") String studentEmail,
            @RequestParam(required = false, defaultValue = "REG-0000") String rollNumber,
            @RequestParam(defaultValue = "0") int tabSwitch,
            @RequestParam(defaultValue = "0") int copyCount,
            @RequestParam(defaultValue = "0") int rightClick,
            @RequestParam(defaultValue = "0") int fullscreenExit,
            @RequestParam(defaultValue = "0") int windowBlur,
            @RequestParam Map<String, String> allParams,
            HttpSession session) {

        ExamSubmissionForm form = new ExamSubmissionForm();
        form.setStudentName(studentName);
        form.setStudentEmail(studentEmail);
        form.setRollNumber(rollNumber);
        form.setTabSwitch(tabSwitch);
        form.setCopyCount(copyCount);
        form.setRightClick(rightClick);
        form.setFullscreenExit(fullscreenExit);
        form.setWindowBlur(windowBlur);

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

        ExamResult savedResult = examService.evaluateAndSaveExam(form);

        // Store answers in session for rendering detail review
        session.setAttribute("userAnswers_" + savedResult.getId(), answers);

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

        model.addAttribute("result", result);
        model.addAttribute("questionReviews", questionReviews);
        model.addAttribute("violationLogs", violationLogs);

        return "result";
    }

    @GetMapping("/history")
    public String historyPage(Model model) {
        List<ExamResult> results = examService.getAllExamResults();
        model.addAttribute("results", results);
        return "history";
    }

    // Teacher Question Management Routes
    @GetMapping("/admin/questions")
    public String manageQuestions(Model model) {
        model.addAttribute("questions", examService.getAllQuestions());
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
}