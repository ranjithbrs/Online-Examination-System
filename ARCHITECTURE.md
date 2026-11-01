# 🏛️ SecureExam — Comprehensive System Architecture & Engineering Documentation

This document provides complete architectural, database schema, security, role separation, and proctoring flow specifications for the **SecureExam (Online Examination & Real-Time Proctoring System)**.

---

## 📑 Table of Contents
1. [High-Level System Architecture](#1-high-level-system-architecture)
2. [Role-Based Access Control (RBAC) & Security Flow](#2-role-based-access-control-rbac--security-flow)
3. [Database Entity-Relationship (ER) Schema & Indexes](#3-database-entity-relationship-er-schema--indexes)
4. [Proctoring Pipeline & Trust Score Engine](#4-proctoring-pipeline--trust-score-engine)
5. [Candidate Exam Lifecycle & Scoring Logic](#5-candidate-exam-lifecycle--scoring-logic)
6. [Visual Interface Gallery & Screen Guide](#6-visual-interface-gallery--screen-guide)
7. [Automated Test Suite Verification Matrix (43/43 Passing)](#7-automated-test-suite-verification-matrix-4343-passing)

---

## 1. High-Level System Architecture

SecureExam is built on a clean three-tier architecture utilizing **Spring Boot 3.5**, **Java 21**, **Spring Data JPA**, **Thymeleaf**, and **Vanilla HTML5/JS Web APIs**.

```mermaid
graph TD
    subgraph Client ["Client Layer (Web Browser)"]
        UI["Thymeleaf Dark Glassmorphism UI"]
        Cam["HTML5 MediaDevices (Webcam)"]
        Mic["Web Audio API (AnalyserNode)"]
        Guard["DOM Event Listener (Tab/Key/Fullscreen)"]
    end

    subgraph Controller ["Spring MVC Controller Layer"]
        HC["HomeController (Web Navigation & Auth)"]
        TC["TeacherAuthInterceptor (Protected Route Guard)"]
        REST["ExamApiController (Auto-Save & Proctoring Sync)"]
    end

    subgraph Service ["Business Logic Service Layer"]
        ES["ExamService (Scoring, Risk & Trust Math)"]
        PS["PdfExportService (OpenPDF Vector Report Engine)"]
    end

    subgraph Data ["Data Access & Storage Layer"]
        QR["QuestionRepository"]
        ER["ExamResultRepository"]
        VR["ViolationLogRepository"]
        SR["ProctoringSnapshotRepository"]
        DB[("MySQL / In-Memory H2 Database")]
    end

    UI --> HC
    UI --> REST
    Mic --> REST
    Cam --> HC
    Guard --> HC

    HC --> TC
    TC --> HC
    HC --> ES
    HC --> PS
    REST --> ES

    ES --> QR
    ES --> ER
    ES --> VR
    ES --> SR

    QR --> DB
    ER --> DB
    VR --> DB
    SR --> DB
```

---

## 2. Role-Based Access Control (RBAC) & Security Flow

The system strictly enforces dual-domain separation between **Candidates** and **Teachers / Administrators**:

```mermaid
sequenceDiagram
    autonumber
    actor User as User (Candidate or Teacher)
    participant Browser as Browser Client
    participant Interceptor as TeacherAuthInterceptor
    participant Controller as HomeController
    participant Session as HTTP Session
    participant DB as Database

    User->>Browser: Enters URL (e.g. /history or /admin/questions)
    Browser->>Interceptor: GET /admin/questions
    Interceptor->>Session: Check teacherLoggedIn == true?
    alt Not Logged In as Teacher
        Interceptor-->>Browser: Redirect to /teacher/login?unauthorized=true
        User->>Browser: Submits Teacher Credentials (admin / admin123)
        Browser->>Controller: POST /teacher/login
        Controller->>Session: Set teacherLoggedIn=true, userRole='TEACHER'
        Controller-->>Browser: Redirect to /history (Authorized)
    else Authenticated Teacher
        Interceptor-->>Controller: Proceed to protected route
        Controller->>DB: Query audit history / questions
        Controller-->>Browser: Render Management Views
    end
```

### RBAC Privilege Matrix

| Route / Capability | Candidate Role | Teacher / Admin Role | Unauthenticated Guest |
| :--- | :---: | :---: | :---: |
| `/` (Registration & Topic Select) | ✅ Yes | ✅ Yes | ✅ Yes |
| `/start-exam` (Validation & Init) | ✅ Yes | ❌ No | ✅ Yes (Becomes Candidate) |
| `/exam` (Take Exam & Auto-Save) | ✅ Yes | ❌ No | ❌ Redirects to `/` |
| `/submit` (Submit Answers) | ✅ Yes | ❌ No | ❌ Redirects to `/` |
| `/result/{id}` (View Own Result) | ✅ Yes | ✅ Yes | ❌ Redirects to `/` |
| `/result/{id}/pdf` (Download Report)| ✅ Yes | ✅ Yes | ❌ Redirects to `/` |
| `/history` (All Candidates Audit) | 🚫 Blocked | ✅ Full Access | 🚫 Redirect to `/teacher/login` |
| `/admin/questions` (Question Bank)| 🚫 Blocked | ✅ Full Access | 🚫 Redirect to `/teacher/login` |
| `/admin/questions/save` (Add/Edit)| 🚫 Blocked | ✅ Full Access | 🚫 Redirect to `/teacher/login` |
| `/admin/questions/delete/{id}` | 🚫 Blocked | ✅ Full Access | 🚫 Redirect to `/teacher/login` |
| `/admin/questions/import` (CSV/JSON)| 🚫 Blocked | ✅ Full Access | 🚫 Redirect to `/teacher/login` |
| `/admin/history/export/csv` | 🚫 Blocked | ✅ Full Access | 🚫 Redirect to `/teacher/login` |

---

## 3. Database Entity-Relationship (ER) Schema & Indexes

```mermaid
erDiagram
    QUESTIONS ||--o{ EXAM_RESULT_QUESTION_IDS : "included in"
    EXAM_RESULTS ||--o{ EXAM_RESULT_ANSWERS : "contains candidate choices"
    EXAM_RESULTS ||--o{ EXAM_RESULT_QUESTION_IDS : "tracks test question scope"
    EXAM_RESULTS ||--o{ VIOLATION_LOGS : "logs security infractions"
    EXAM_RESULTS ||--o{ PROCTORING_SNAPSHOTS : "stores audit photos"

    QUESTIONS {
        bigint id PK
        varchar question_text
        varchar option_a
        varchar option_b
        varchar option_c
        varchar option_d
        varchar correct_option
        varchar category
        int marks
        text explanation
    }

    EXAM_RESULTS {
        bigint id PK
        varchar student_name
        varchar student_email
        varchar roll_number
        int score
        int total_marks
        double percentage
        boolean passed
        int tab_switch_count
        int copy_count
        int right_click_count
        int fullscreen_exit_count
        int window_blur_count
        int audio_spike_count
        int total_violations
        int risk_score
        int trust_score
        varchar integrity_status
        int time_taken_seconds
        boolean overtime
        boolean disqualified
        varchar disqualification_reason
        varchar selected_topic
        datetime submitted_at
    }

    EXAM_RESULT_ANSWERS {
        bigint exam_result_id FK
        bigint question_id
        varchar selected_option
    }

    EXAM_RESULT_QUESTION_IDS {
        bigint exam_result_id FK
        bigint question_id
    }

    VIOLATION_LOGS {
        bigint id PK
        bigint exam_result_id FK "Index: idx_viol_exam_id"
        varchar violation_type
        varchar details
        datetime timestamp
    }

    PROCTORING_SNAPSHOTS {
        bigint id PK
        bigint exam_result_id FK "Index: idx_snap_exam_id"
        varchar snapshot_type
        longtext image_base64
        varchar note
        datetime captured_at
    }
```

### Relational Database Optimizations
- **Index `idx_viol_exam_id`**: Added to `violation_logs (exam_result_id)` to ensure fast lookup of infractions for scorecards and audit reports.
- **Index `idx_snap_exam_id`**: Added to `proctoring_snapshots (exam_result_id)` preventing full-table scans during snapshot retrieval.
- **Foreign Keys**: Managed cleanly with cascading lifecycle and `@ElementCollection` eager associations for question IDs and user answers.

---

## 4. Proctoring Pipeline & Trust Score Engine

```mermaid
flowchart TD
    A[Candidate Starts Exam] --> B[Request Camera & Microphone Permissions]
    B --> C[Capture Initial Identity Photo]
    C --> D[Enter Enforced Full-Screen Mode]
    
    D --> E{Real-Time Surveillance Loop}
    
    E -- Tab Switch or Window Blur --> F1[Increment Tab/Blur Counter + Instant Warning Alert]
    E -- Copy / Paste Attempt --> F2[Prevent Default + Increment Copy Counter]
    E -- Context Menu / Right Click --> F3[Block Context Menu + Increment Right-Click Counter]
    E -- Fullscreen Escape --> F4[Trigger Strike + Capture Security Snapshot Frame]
    E -- Web Audio Volume > Threshold --> F5[Log Ambient Noise Spike + Audio Warning]
    E -- Timer Exceeds 10m + 30s Buffer --> F6[Flag Overtime Submission + Apply Penalty]
    
    F4 --> G{Strikes >= 3?}
    G -- Yes --> H[Immediate Disqualification: Lock Exam & Auto-Submit]
    G -- No --> E

    E -- Candidate Clicks Submit --> I[Capture Final Submission Snapshot]
    I --> J[Evaluate Score & Compute Trust Index]
    H --> J
```

### Mathematical Trust Formula
$$\text{Risk Score} = (5 \times \text{Tabs}) + (8 \times \text{Copies}) + (3 \times \text{RightClicks}) + (10 \times \text{FullscreenExits}) + (4 \times \text{Blurs}) + (6 \times \text{AudioSpikes}) + \text{OvertimePenalty}(25)$$

$$\text{Trust Score} = \max(0, 100 - \text{Risk Score})$$

### Integrity Status Tiers
- **$90\% - 100\%$**: `High Integrity (Normal)` 🟢
- **$70\% - 89\%$**: `Moderate Warning` 🟡
- **$< 70\%$**: `High Risk / Flagged` 🔴
- **Strikes $\ge 3$ or Disqualified**: `DISQUALIFIED` ⛔ (Score set to $0\%$, Trust set to $0\%$)

---

## 5. Candidate Exam Lifecycle & Scoring Logic

1. **Scoped Subject Filtering**:
   - When a candidate selects **Java Fundamentals** (5 questions), exactly those 5 questions are served.
   - The score is strictly evaluated against those 5 questions ($1/5$, $4/5$, $5/5$).
   - The question reviews list only contains questions from that candidate's active exam session.
2. **Draft Auto-Save & Recovery**:
   - Every 15 seconds or upon answer selection, background asynchronous POST to `/api/exam/draft/save` backs up the candidate's active progress.
   - If the candidate accidentally reloads, the draft automatically restores checked options and marked questions.
3. **Pre-Submission Review Modal**:
   - Displays a breakdown of Answered, Marked for Review, and Unanswered questions before final submission.

---

## 6. Visual Interface Gallery & Screen Guide

### Screen 1: Candidate Registration Portal (`/`)
- Clean glassmorphism layout with candidate input fields (Name, Email, Roll Number).
- Dynamic subject selector showing live question counts for each category.
- Role-aware navigation bar dynamically providing `Teacher Sign In 🔒` or teacher management shortcuts.

### Screen 2: Interactive Proctoring Examination (`/exam`)
- **Top Navigation Bar**: Live candidate identity, 10-minute visual countdown timer, draft save status indicator dot.
- **Audio Monitor Widget**: Real-time microphone input volume visualizer detecting talking and background noise.
- **Question Card**: Single-question stepper with `Save & Next ➡️`, `⬅️ Previous`, `⭐ Mark for Review`, and `Clear Selection`.
- **Question Palette**: Grid of clickable question numbers color-coded by state (Answered: Green, Marked: Purple/Star, Unvisited: Gray, Current: Blue).

### Screen 3: Assessment & Integrity Scorecard (`/result/{id}`)
- Official candidate metadata and time taken summary.
- Big scorecard banner showing accurate category-scoped score (e.g. `4 / 5 Marks`, `80%`).
- Proctoring & Trust Audit Analytics displaying tabular counter breakdown and incident timestamps.
- Photo Audit Gallery showing candidate webcam frames captured at start, during security events, and at final submission.
- Academic breakdown with correct answer badges and comprehensive explanations.
- One-click **"Download Official PDF Certificate & Report"** button.

### Screen 4: Teacher & Administrator Sign-In (`/teacher/login`)
- Dedicated secure login portal with validation alerts.
- Protects administrative audit histories and question authoring tools.

### Screen 5: Audit History Dashboard (`/history`)
- Real-time candidate performance analytics (Average Score, Pass Rate, Total Candidates, Flagged Candidates).
- Live search bar and status filters.
- Direct CSV export (`/admin/history/export/csv`) for academic record archiving.

### Screen 6: Question Bank Management (`/admin/questions`)
- Full CRUD interface to add, edit, or delete questions.
- Subject categorization with marks and explanations.
- JSON and CSV bulk question import/export tools.

---

## 7. Automated Test Suite Verification Matrix (43/43 Passing)

All 43 unit, integration, and security tests execute cleanly in under 18 seconds:

| Category | Test Name | Target Verification | Status |
| :--- | :--- | :--- | :---: |
| **Candidate Auth & Registration** | `testCandidateRegistrationValidation` | Verifies validation rules (min 2 chars, valid email) | ✅ PASS |
| **Candidate Auth & Registration** | `testTopicFilteredExamStartAndSessionIsolation` | Validates session isolation per candidate | ✅ PASS |
| **Role Separation & RBAC** | `testUnauthorizedAccessToAdminQuestionsRedirectsToLogin` | Interceptor redirects unauthenticated requests | ✅ PASS |
| **Role Separation & RBAC** | `testUnauthorizedAccessToHistoryRedirectsToLogin` | Interceptor protects candidate history | ✅ PASS |
| **Role Separation & RBAC** | `testTeacherLoginSuccess` | Validates teacher authentication | ✅ PASS |
| **Role Separation & RBAC** | `testTeacherLoginFailure` | Rejects invalid teacher credentials | ✅ PASS |
| **Role Separation & RBAC** | `testTeacherLogout` | Clears teacher session attributes | ✅ PASS |
| **Question Management (Teacher)** | `testQuestionCrudLifecycleByTeacher` | Complete Create-Read-Delete lifecycle | ✅ PASS |
| **Question Management (Teacher)** | `testQuestionBankSeeded` | Checks startup question seeding | ✅ PASS |
| **Question Management (Teacher)** | `testImportQuestionsJson` / `Csv` | Bulk question import functionality | ✅ PASS |
| **Question Management (Teacher)** | `testExportQuestionsJson` / `Csv` | Bulk question export functionality | ✅ PASS |
| **Answer Submission & Scoring** | `testCleanExamEvaluation` | 100% score for correct answers | ✅ PASS |
| **Answer Submission & Scoring** | `testTopicBasedExamEvaluationAndScoring` | Accurate score calculation per topic scope | ✅ PASS |
| **Answer Submission & Scoring** | `testAnswerPersistenceInDatabase` | Stores selected answers in database | ✅ PASS |
| **Timing & Submission Rules** | `testExamTimingOvertimePenaltyAndViolations` | Flags overtime submissions (>630s) | ✅ PASS |
| **Timing & Submission Rules** | `testServerSideTimerEnforcementAndOvertime` | Server-side time tracking verification | ✅ PASS |
| **Proctoring & Disqualification** | `testAutomaticDisqualificationStrikeThresholds` | 3 strikes trigger automatic disqualification | ✅ PASS |
| **Proctoring & Disqualification** | `testExamEvaluationWithAudioSpikesRiskPenalty` | Deducts trust score on audio spikes | ✅ PASS |
| **Proctoring & Disqualification** | `testProctoringSnapshotPersistenceAndRetrieval`| Verifies webcam snapshots persistence | ✅ PASS |
| **Draft Auto-Save** | `testExamDraftAutoSaveAndRetrieval` | Real-time draft persistence | ✅ PASS |
| **Draft Auto-Save** | `testExamDraftWithMarkedQuestions` | Preserves bookmarked questions | ✅ PASS |
| **PDF Reporting** | `testPdfReportGeneration` | Generates official PDF report | ✅ PASS |
| **PDF Reporting** | `testPdfDownloadEndpoint` | Validates PDF download HTTP response | ✅ PASS |
| **Audit Dashboard & Export** | `testAuditDashboardMetricsCalculation` | Calculates class statistics | ✅ PASS |
| **Audit Dashboard & Export** | `testExportAuditHistoryCsvEndpoint` | Generates audit CSV download | ✅ PASS |

---

*SecureExam System Architecture Documentation — Built & Maintained by Ranjith B*
