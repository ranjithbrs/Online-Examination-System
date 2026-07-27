# Online Examination & Proctoring System

A modern, robust, and full-featured **Spring Boot Web Application** designed for conducting secure online examinations with real-time proctoring security, candidate analytics, automated grading, and teacher/admin audit dashboards.

---

## 🌟 Key Features

* **Candidate Portal & Registration**: Quick candidate onboarding with session tracking (Name, Email, Roll Number).
* **Live Proctored Exam Interface**:
  * Interactive 10-minute countdown timer with automatic answer submission at `0:00`.
  * Dynamic question rendering with clear choice & question palette grid (tracking answered/unanswered status).
* **Proctoring Security Engine**:
  * **Full-Screen Locking**: Enforces full-screen mode during the exam session.
  * **Tab Switch & Focus Loss Monitoring**: Tracks when candidates switch browser tabs or minimize the window.
  * **Copy / Paste / Cut Prevention**: Blocks keyboard shortcuts (`Ctrl+C`, `Ctrl+V`, `Ctrl+X`) and DevTools inspection (`F12`, `Ctrl+Shift+I`).
  * **Right-Click / Context Menu Blocking**: Prevents context menu shortcuts.
  * **Real-time Alert Toasts**: Notifies candidates instantly upon security policy violations.
* **Academic Performance & Integrity Report**:
  * Comprehensive score breakdown, percentage, grade, and pass/fail banner.
  * **Trust Index Gauge (% Score)**: Calculates weighted proctoring risk score based on recorded security incidents.
  * **Proctoring Log Timeline**: Logs exact violation incidents with timestamps.
  * **Detailed Question Review**: Explanations provided for every question for candidate feedback.
* **Teacher / Admin Audit Dashboard (`/history`)**:
  * Browse past candidate attempts with score records, percentage, integrity status badges, and full report inspection.
* **Database & Persistence**:
  * Built with Spring Data JPA (`Question`, `ExamResult`, `ViolationLog` entities).
  * Auto-configures for both MySQL and H2 database fallback for out-of-the-box local execution.

---

## 🛠️ Technology Stack

* **Backend**: Java 21, Spring Boot 3.5, Spring Data JPA, Spring MVC, Thymeleaf.
* **Frontend**: HTML5, Vanilla JavaScript, Custom CSS Design System (Inter Font, Dark Mode Glassmorphism, Responsive Grid).
* **Database**: MySQL / H2 In-Memory Database.
* **Build System & Testing**: Maven (`mvnw`), JUnit 5, Mockito.

---

## 🚀 Getting Started

### Prerequisites

* Java 21 or higher installed.
* Maven wrapper included (`./mvnw` or `mvnw.cmd`).

### Running the Application

1. **Clone the repository**:
   ```bash
   git clone https://github.com/ranjithbrs/Online-Examination-System.git
   cd Online-Examination-System
   ```

2. **Run via Maven**:
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Access the Portal**:
   * Candidate Registration Portal: [http://localhost:8080/](http://localhost:8080/)
   * Admin / Teacher Audit Dashboard: [http://localhost:8080/history](http://localhost:8080/history)

---

## 🧪 Running Automated Tests

Run the full automated test suite (includes context loading, repository testing, grading logic, clean exam evaluation, and suspicious cheating violation risk scoring):

```bash
./mvnw test
```

---

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.
