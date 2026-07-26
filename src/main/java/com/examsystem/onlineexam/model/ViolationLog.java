package com.examsystem.onlineexam.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "violation_logs")
public class ViolationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long examResultId;
    private String violationType; // TAB_SWITCH, COPY_PASTE, RIGHT_CLICK, FULLSCREEN_EXIT, WINDOW_BLUR, DEVTOOLS_ATTEMPT
    
    @Column(length = 500)
    private String description;
    
    private LocalDateTime timestamp;

    public ViolationLog() {
    }

    public ViolationLog(Long examResultId, String violationType, String description, LocalDateTime timestamp) {
        this.examResultId = examResultId;
        this.violationType = violationType;
        this.description = description;
        this.timestamp = timestamp;
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

    public String getViolationType() {
        return violationType;
    }

    public void setViolationType(String violationType) {
        this.violationType = violationType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
