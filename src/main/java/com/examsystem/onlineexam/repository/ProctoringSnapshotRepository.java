package com.examsystem.onlineexam.repository;

import com.examsystem.onlineexam.model.ProctoringSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProctoringSnapshotRepository extends JpaRepository<ProctoringSnapshot, Long> {
    List<ProctoringSnapshot> findByExamResultIdOrderByCapturedAtAsc(Long examResultId);
}
