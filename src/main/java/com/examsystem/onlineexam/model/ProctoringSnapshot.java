package com.examsystem.onlineexam.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "proctoring_snapshots")
public class ProctoringSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long examResultId;

    private String snapshotType; // SESSION_START, SECURITY_EVENT, PERIODIC_CHECK, EXAM_SUBMISSION

    @Lob
    @Column(columnDefinition = "CLOB")
    private String imageBase64;

    @Column(length = 500)
    private String note;

    private LocalDateTime capturedAt;

    public ProctoringSnapshot() {
    }

    public ProctoringSnapshot(Long examResultId, String snapshotType, String imageBase64, String note, LocalDateTime capturedAt) {
        this.examResultId = examResultId;
        this.snapshotType = snapshotType;
        this.imageBase64 = imageBase64;
        this.note = note;
        this.capturedAt = capturedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getExamResultId() {
        return examResultId;
    }

    public void setExamResultId(Long examResultId) {
        this.examResultId = examResultId;
    }

    public String getSnapshotType() {
        return snapshotType;
    }

    public void setSnapshotType(String snapshotType) {
        this.snapshotType = snapshotType;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCapturedAt() {
        return capturedAt;
    }

    public void setCapturedAt(LocalDateTime capturedAt) {
        this.capturedAt = capturedAt;
    }
}
