package com.examsystem.onlineexam.repository;

import com.examsystem.onlineexam.model.ExamResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {
    List<ExamResult> findAllByOrderBySubmittedAtDesc();
    List<ExamResult> findByIntegrityStatusContainingIgnoreCase(String status);
}
