package com.examsystem.onlineexam.controller;

import com.examsystem.onlineexam.dto.ExamSubmissionForm;
import com.examsystem.onlineexam.model.ExamResult;
import com.examsystem.onlineexam.model.Question;
import com.examsystem.onlineexam.service.ExamService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ExamApiController {

    private final ExamService examService;

    public ExamApiController(ExamService examService) {
        this.examService = examService;
    }

    @GetMapping("/questions")
    public ResponseEntity<List<Question>> getAllQuestions() {
        return ResponseEntity.ok(examService.getAllQuestions());
    }

    @GetMapping("/questions/{id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable Long id) {
        Question question = examService.getQuestionById(id);
        if (question == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(question);
    }

    @PostMapping("/exams/submit")
    public ResponseEntity<ExamResult> submitExam(@Valid @RequestBody ExamSubmissionForm form) {
        ExamResult result = examService.evaluateAndSaveExam(form);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/results")
    public ResponseEntity<List<ExamResult>> getAllResults() {
        return ResponseEntity.ok(examService.getAllExamResults());
    }

    @GetMapping("/results/{id}")
    public ResponseEntity<ExamResult> getResultById(@PathVariable Long id) {
        ExamResult result = examService.getExamResult(id);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }
}
