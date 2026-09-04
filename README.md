# Online Examination & Proctoring System

<div align="center">

[![Live Demo](https://img.shields.io/badge/Live%20Demo-Render-46E3B7?style=for-the-badge&logo=render&logoColor=white)](https://online-examination-system-dcoc.onrender.com)
[![Java 21](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)

**🚀 Live Application**: **[https://online-examination-system-dcoc.onrender.com](https://online-examination-system-dcoc.onrender.com)**

</div>

A modern, robust, and full-featured **Spring Boot Web Application** designed for conducting secure online examinations with real-time proctoring security, candidate analytics, automated grading, teacher question management, and a full RESTful API.

---

## 🌟 Key Features

### 👨‍🎓 Candidate Portal
* **Quick Registration**: Onboarding with Name, Email, and Roll Number.
* **Live Proctored Exam Interface**:
  * Interactive 10-minute countdown timer with automatic submission at `0:00`.
  * Dynamic question rendering with answer palette grid (answered/unanswered tracking).
  * **Real-time exam progress bar** — fills as questions are answered.

### 🔒 Proctoring Security Engine
* **Full-Screen Locking**: Enforces full-screen mode throughout the exam session.
* **Tab Switch & Focus Loss Monitoring**: Tracks browser tab switches and window minimization.
* **Copy / Paste / Cut Prevention**: Blocks `Ctrl+C`, `Ctrl+V`, `Ctrl+X` and DevTools shortcuts (`F12`, `Ctrl+Shift+I`).
* **Right-Click / Context Menu Blocking**: Prevents context menu access.
* **Real-time Alert Toasts**: Notifies candidates instantly on policy violations.

### 📊 Academic Performance & Integrity Report
* Comprehensive score breakdown, percentage, grade, and pass/fail banner.
* **Trust Index Score**: Weighted proctoring risk score based on recorded security incidents.
* **Proctoring Log Timeline**: Logs each violation type with timestamps.
* **Detailed Question Review**: Per-question correct/incorrect breakdown with explanations — **persisted in the database**, available even after session expiry.
* **🖨️ Print / Export PDF Report** button for audit records.

### 🏫 Teacher / Admin Portal
* **Audit Dashboard (`/history`)**: Browse all candidate submissions with score, integrity badge, and report inspection. Includes **live search filter** by name, email, or roll number.
* **Question Bank Management (`/admin/questions`)**: Full **Create / Edit / Delete** interface for exam questions — no restart required.

### 🌐 REST API
Full JSON API for integrations and automated testing:

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/v1/questions` | `GET` | List all questions |
| `/api/v1/questions/{id}` | `GET` | Get a single question |
| `/api/v1/exams/submit` | `POST` | Submit an exam (JSON body) |
| `/api/v1/results` | `GET` | List all exam results |
| `/api/v1/results/{id}` | `GET` | Get a single result |

### 🗄️ Database & Persistence
* Built with Spring Data JPA (`Question`, `ExamResult`, `ViolationLog`, `exam_result_answers` entities).
* **Candidate answers are persisted to the database** — historical reports are always accurate, never session-dependent.
* **Defaults to H2 in-memory DB** for zero-configuration local startup.
* Optional **MySQL profile** for production environments.

---

## 🛠️ Technology Stack

* **Backend**: Java 21, Spring Boot 3.5, Spring Data JPA, Spring MVC, Spring Validation, Thymeleaf.
* **Frontend**: HTML5, Vanilla JavaScript, Custom CSS Design System (Inter Font, Dark Mode Glassmorphism, Responsive Grid).
* **Database**: H2 In-Memory (default) / MySQL (production profile).
* **API**: RESTful JSON API via `@RestController`.
* **Build & Testing**: Maven (`mvnw`), JUnit 5, MockMvc — **7 automated tests**.
* **Containerization**: Docker (multi-stage build).

---

## 🚀 Getting Started

### Prerequisites

* Java 21 or higher installed.
* Maven wrapper included (`./mvnw` or `mvnw.cmd`) — no Maven install needed.

### Running the Application

1. **Clone the repository**:
   ```bash
   git clone https://github.com/ranjithbrs/Online-Examination-System.git
   cd Online-Examination-System
   ```

2. **Run via Maven** *(H2 in-memory DB — no database setup needed)*:
   ```bash
   # Linux / macOS
   ./mvnw spring-boot:run

   # Windows
   mvnw.cmd spring-boot:run
   ```

3. **Run with MySQL** *(production)*:
   ```bash
   # Update credentials in src/main/resources/application-mysql.properties first, then:
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql
   ```

### 🔗 Access the Application

| Portal / Feature | Local URL | Live Cloud URL (Render) |
|---|---|---|
| **Candidate Registration & Exam Portal** | `http://localhost:8080/` | [https://online-examination-system-dcoc.onrender.com/](https://online-examination-system-dcoc.onrender.com/) |
| **Admin / Teacher Audit Dashboard** | `http://localhost:8080/history` | [https://online-examination-system-dcoc.onrender.com/history](https://online-examination-system-dcoc.onrender.com/history) |
| **Teacher Question Bank Management** | `http://localhost:8080/admin/questions` | [https://online-examination-system-dcoc.onrender.com/admin/questions](https://online-examination-system-dcoc.onrender.com/admin/questions) |
| **REST API — Question List** | `http://localhost:8080/api/v1/questions` | [https://online-examination-system-dcoc.onrender.com/api/v1/questions](https://online-examination-system-dcoc.onrender.com/api/v1/questions) |
| **System Health Check** | `http://localhost:8080/health` | [https://online-examination-system-dcoc.onrender.com/health](https://online-examination-system-dcoc.onrender.com/health) |
| **H2 Database Web Console** | `http://localhost:8080/h2-console` | *Internal only* |

---

## 🐳 Running with Docker

```bash
docker build -t online-exam-system .
docker run -p 8080:8080 online-exam-system
```

---

## ☁️ Cloud Deployment (Render / Railway / Docker)

The application is pre-configured for one-click or containerized deployment on cloud platforms like **Render**:

1. **Zero-Config Deployment (H2 In-Memory)**:
   - Deploy directly using the provided `Dockerfile` or `render.yaml`.
   - The app automatically binds to the cloud platform's dynamic `$PORT` and starts immediately with seeded questions in H2.
   - Health check path is available at `/health` (or `/api/v1/health`).

2. **Persistent Cloud Database (MySQL)**:
   - If connecting to an external database (e.g. Aiven MySQL, AWS RDS, Railway), provide the following environment variables in your cloud dashboard:
     - `DB_URL`: `jdbc:mysql://<host>:<port>/<dbname>?useSSL=true`
     - `DB_USERNAME`: `<your_db_username>`
     - `DB_PASSWORD`: `<your_db_password>`

---

## 🧪 Running Automated Tests

Runs 7 tests: context loading, question bank seeding, clean exam grading, suspicious exam & violation logging, **answer persistence across sessions**, question CRUD operations, and REST API endpoints.

```bash
# Linux / macOS
./mvnw test

# Windows
mvnw.cmd test
```

Expected output:
```
Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

## 📁 Project Structure

```
src/main/java/com/examsystem/onlineexam/
├── OnlineexamApplication.java          # Spring Boot entry point
├── HomeController.java                 # MVC routes (exam, result, history, admin)
├── config/
│   └── DataInitializer.java            # Seeds question bank on startup
├── controller/
│   └── ExamApiController.java          # REST API endpoints
├── dto/
│   ├── ExamSubmissionForm.java         # Exam submission DTO (validated)
│   ├── QuestionFormDto.java            # Question create/edit DTO (validated)
│   └── QuestionReviewDto.java          # Result review projection
├── exception/
│   └── GlobalExceptionHandler.java     # Structured JSON error responses
├── model/
│   ├── ExamResult.java                 # Exam result entity (with persisted answers)
│   ├── Question.java                   # Question entity
│   └── ViolationLog.java              # Proctoring violation log entity
├── repository/
│   ├── ExamResultRepository.java
│   ├── QuestionRepository.java
│   └── ViolationLogRepository.java
└── service/
    └── ExamService.java                # Business logic & grading engine

src/main/resources/
├── application.properties              # H2 default config
├── application-mysql.properties        # MySQL production profile
├── static/css/styles.css              # Dark mode design system
└── templates/
    ├── start.html                      # Candidate registration page
    ├── exam.html                       # Live proctored exam
    ├── result.html                     # Score & integrity report
    ├── history.html                    # Audit dashboard
    └── question-manage.html           # Teacher question management
```

---

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.
