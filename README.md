# 🎓 SecureExam — Smart Online Examination & Proctoring System

[![Live Demo](https://img.shields.io/badge/Live%20Demo-Render-46E3B7?style=for-the-badge&logo=render&logoColor=white)](https://online-examination-system-dcoc.onrender.com)
[![Backend: Java 21](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Framework: Spring Boot 3.5](https://img.shields.io/badge/Spring%20Boot-3.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Database: JPA / H2 / MySQL](https://img.shields.io/badge/Database-JPA%20%7C%20H2%20%7C%20MySQL-003B57?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Container: Docker](https://img.shields.io/badge/Container-Docker%20Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white)](Dockerfile)
[![Tests: 34 JUnit Tests](https://img.shields.io/badge/Tests-34%20Passing-brightgreen?style=for-the-badge&logo=junit5&logoColor=white)](src/test/)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)

> 🌟 **A complete online exam portal that automatically monitors fairness and integrity — just like having an in-person supervisor in the exam hall, but running right inside your web browser!**

---

## 🔗 Quick Links & Live Demos

- 🌐 **Student Exam Portal:** [online-examination-system-dcoc.onrender.com](https://online-examination-system-dcoc.onrender.com)
- 📊 **Teacher & Admin Audit Dashboard:** [online-examination-system-dcoc.onrender.com/history](https://online-examination-system-dcoc.onrender.com/history)
- 📝 **Question Bank Management:** [online-examination-system-dcoc.onrender.com/admin/questions](https://online-examination-system-dcoc.onrender.com/admin/questions)
- 🐙 **GitHub Repository:** [github.com/ranjithbrs/Online-Examination-System](https://github.com/ranjithbrs/Online-Examination-System)

---

## 💡 What is this Project? (In Plain English)

Imagine taking an exam from home on your laptop. Normally, a school or company has to hire teachers to stand in the room and make sure students don't search for answers on Google or talk to someone next to them.

**SecureExam** solves this problem automatically with code. It is an intelligent web application that:
1. **Presents an interactive test**: Students register, pick their subject, and answer questions one-by-one with an easy-to-use countdown timer.
2. **Watches for dishonest behavior in real time**:
   - 🚫 **No Tab Switching**: If a candidate opens a new browser tab or minimizes the exam, the system catches it immediately.
   - 🚫 **No Copy-Pasting**: Right-clicking, copying questions, and pasting answers are strictly blocked.
   - 📷 **Webcam Verification**: Automatically takes photos at the start of the exam, during suspicious moments, and when submitting.
   - 🎙️ **Room Noise Detection**: Listens for loud talking, whispering, or background chatter using the computer microphone.
3. **Grades Instantly & Generates Reports**: As soon as the student finishes, the system calculates their score, grades their answers, gives helpful explanations, and produces an **official PDF report with verifiable photo evidence**.

---

## 👥 Who is it Built For?

| User | How SecureExam Helps Them |
| :--- | :--- |
| 🎓 **Students** | Take exams in a clean, distraction-free screen with auto-saving so you never lose your answers, plus instant feedback and explanations. |
| 🏫 **Schools & Colleges** | Conduct semester quizzes, entrance exams, and mock tests without renting physical exam centers or worrying about cheating. |
| 💼 **Recruiters & Companies** | Screen candidates for job interviews remotely and review automated trust ratings to ensure candidates solved questions on their own. |

---

## 🚀 How It Works (Step-by-Step Walkthrough)

```text
  [1. Register & Pick Subject]
               │
               ▼
  [2. Enter Full-Screen Mode] ──▶ Webcam & Microphone Activated
               │
               ▼
  [3. Step-by-Step Questions] ──▶ Save & Next ➡️, Mark for Review ⭐, Auto-Save Draft
               │
               ▼
  [4. Active Cheating Guard]  ──▶ Tab Switches, Fullscreen Exits, Copy Attempts & Noise Tracked
               │
               ▼
  [5. Review & Confirm]       ──▶ Summary Modal checks unanswered questions before final submission
               │
               ▼
  [6. Instant Scorecard & PDF]──▶ Final Grade + 100% Trust Score + Official PDF Certificate
```

1. **Candidate Registration**: The student enters their Name, Email, and Roll Number, then chooses a subject (e.g. *Java Fundamentals*, *Spring Boot*, *Database Systems*, or *All Topics*).
2. **Locked Full-Screen Exam**: The test launches in full-screen. A 10-minute timer starts ticking down.
3. **Smooth Question Stepper**: Candidates see one question at a time. They can click **`Save & Next ➡️`**, jump between questions using the visual palette, or mark difficult questions for review. If their internet hiccups, our background **auto-save draft** keeps their work safe!
4. **Behavioral Integrity Monitor**:
   - If the student leaves full-screen or switches windows ➡️ Logged with an instant alert!
   - If loud talking is detected in the room ➡️ Flagged with a noise spike!
   - Reaching 3 severe security strikes ➡️ Exam automatically locks and terminates!
5. **Official Verification**: Upon completion, the student receives their score (e.g. 4 / 5 Marks) and can download a beautiful **Official PDF Certificate & Report** complete with security hashes and photo audit frames.

---

## ✨ Key Features Breakdown

### 🎯 1. For Students (Smooth Test Taking Experience)
- **Subject-Wise Modules**: Pick a targeted subject (Java, Spring, SQL, Web) or take the full comprehensive test.
- **Easy Question Navigation**: `Save & Next ➡️`, `⬅️ Previous`, and visual question numbers that change colors as you answer.
- **Mark for Review ⭐**: Bookmark questions you want to double-check before submitting.
- **Pre-Submission Checklist**: A clear modal shows how many questions you answered, marked, or left blank before you finalize.
- **15-Second Auto-Save**: Answers are continually backed up in the background to protect against accidental browser refreshes.

### 🛡️ 2. For Evaluators (Real-Time Proctoring Security)
- **Full-Screen Enforcement**: Keeps the candidate inside the testing environment. Exiting triggers a security infraction.
- **Tab Switch & Focus Detection**: Detects whenever the browser window loses focus or another application is opened.
- **Clipboard & DevTools Blocker**: Prohibits `Ctrl+C`, `Ctrl+V`, `F12` developer tools, and right-click context menus.
- **📷 Automated Photo Audit**: Captures webcam snapshots at start, on security strikes, and upon submission.
- **🎙️ Ambient Audio Proctoring**: Measures room noise level in real-time. Detects speech or disruptions above safe levels.
- **3-Strike Disqualification**: Automatically terminates the exam if a candidate repeatedly violates safety policies.

### 📊 3. For Teachers & Administrators
- **Teacher Audit Dashboard (`/history`)**: Search and filter past candidate records by name, email, roll number, or pass/fail status.
- **Trust Index Score**: Calculates an overall honesty rating (e.g. `100% High Integrity` vs `High Risk / Flagged`).
- **Question Bank Manager (`/admin/questions`)**: Add, edit, or delete questions directly from the browser without touching code.
- **CSV & PDF Export**: Download bulk candidate results or individual official scorecards with one click.

---

## 🛠️ Technology Stack (Behind the Scenes)

For developers and technical reviewers, here is how the system is engineered under the hood:

| Layer | Technologies Used | What It Does |
| :--- | :--- | :--- |
| **Backend** | **Java 21**, **Spring Boot 3.5** | Core server logic, REST endpoints, and security session management |
| **Data & ORM** | **Spring Data JPA**, **Hibernate**, **H2 / MySQL** | Database storage with instant local in-memory H2 and production MySQL |
| **Frontend** | **HTML5**, **Vanilla JavaScript**, **Thymeleaf**, **CSS3** | Fast, zero-dependency modern UI with dark glassmorphism styling |
| **Audio Proctoring** | **Web Audio API** (`AudioContext`, `AnalyserNode`) | Real-time browser microphone frequency & volume analysis |
| **Photo Proctoring** | **HTML5 MediaDevices** (`getUserMedia`, `<canvas>`) | Client-side snapshot capture with base64 serialization |
| **PDF Generation** | **OpenPDF / iText** | High-fidelity, multi-page vector PDF report generation with security seals |
| **Testing** | **JUnit 5**, **Spring Boot Test**, **MockMvc** | 34 automated unit, integration, and security test cases |
| **Deployment** | **Docker**, **Render Cloud** | Containerized cloud deployment ready for high availability |

---

## 🏃 How to Run It on Your Computer (Quick Setup)

You don't need complicated database setups—the app comes with an automatic built-in database that starts instantly!

### What You Need:
- **Java 21** or newer installed ([Download free from Oracle or Adoptium](https://adoptium.net/))

### Step 1: Download or Clone the Code
```bash
git clone https://github.com/ranjithbrs/Online-Examination-System.git
cd Online-Examination-System
```

### Step 2: Start the Application

- **On Windows:**
  ```powershell
  .\mvnw.cmd spring-boot:run
  ```

- **On Mac or Linux:**
  ```bash
  ./mvnw spring-boot:run
  ```

### Step 3: Open in Your Browser
Once you see `Started OnlineexamApplication` in your terminal, open:
- 👉 **Candidate Exam Portal:** [http://localhost:8080/](http://localhost:8080/)
- 👉 **Teacher Audit Dashboard:** [http://localhost:8080/history](http://localhost:8080/history)
- 👉 **Question Bank Manager:** [http://localhost:8080/admin/questions](http://localhost:8080/admin/questions)

---

## 🐳 Running with Docker

If you prefer using Docker:

```bash
# 1. Build the lightweight container
docker build -t secure-exam-system .

# 2. Run on port 8080
docker run -p 8080:8080 secure-exam-system
```
Then visit `http://localhost:8080` in your browser.

---

## 🧪 Automated Quality & Test Suite

The project includes **34 automated test cases** covering every part of the system:
- Question bank seeding and subject filtering
- Accurate score calculation (e.g., 5 questions scored strictly out of 5)
- Proctoring violation counts and strike rules
- Disqualification triggers
- Real-time auto-save draft endpoints
- PDF report generation
- Teacher audit search and CSV exports

Run the test suite anytime:
```bash
# Windows
.\mvnw.cmd test

# Mac / Linux
./mvnw test
```

Expected result:
```text
[INFO] Tests run: 34, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## 📁 Project Structure

```text
Online-Examination-System/
├── src/
│   ├── main/
│   │   ├── java/com/examsystem/onlineexam/
│   │   │   ├── OnlineexamApplication.java   # Main application starter
│   │   │   ├── HomeController.java          # Handles web page routes & submissions
│   │   │   ├── config/                      # Database question seeder (35 sample questions)
│   │   │   ├── controller/                  # REST API controllers
│   │   │   ├── dto/                         # Safe data objects for forms & drafts
│   │   │   ├── model/                       # Database tables (ExamResult, Question, etc.)
│   │   │   ├── repository/                  # Database communication interfaces
│   │   │   └── service/                     # Grading, Trust score math, & PDF generator
│   │   └── resources/
│   │       ├── application.properties       # App settings & database config
│   │       ├── static/css/styles.css        # Clean dark-mode stylesheet
│   │       └── templates/                   # Web pages (exam, result, start, history)
│   └── test/                                # 34 automated JUnit 5 tests
├── Dockerfile                               # Multi-stage production container build
├── render.yaml                              # Cloud deployment configuration
├── pom.xml                                  # Project dependencies
└── README.md                                # Project documentation
```

---

## 👨‍💻 Created By

**Ranjith B**  
🎓 *B.Tech Computer Science & Business Systems (CSBS)*  
🏛️ *Nehru Institute of Engineering and Technology, Coimbatore*  

- 💼 **LinkedIn**: [linkedin.com/in/ranjith-b-csbs](https://linkedin.com/in/ranjith-b-csbs)  
- 🐙 **GitHub**: [github.com/ranjithbrs](https://github.com/ranjithbrs)  
- 🌐 **Portfolio**: [ranjithbrs.github.io/portfolio](https://ranjithbrs.github.io/portfolio/)  
- 📧 **Email**: ranjithb2k06@gmail.com  

---

## 📄 License

Distributed under the [MIT License](LICENSE). Free for academic, personal, and educational use.
