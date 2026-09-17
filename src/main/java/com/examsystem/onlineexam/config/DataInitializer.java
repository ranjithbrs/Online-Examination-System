package com.examsystem.onlineexam.config;

import com.examsystem.onlineexam.model.Question;
import com.examsystem.onlineexam.repository.QuestionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final QuestionRepository questionRepository;

    public DataInitializer(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public void run(String... args) {
        if (questionRepository.count() == 0) {
            List<Question> defaultQuestions = Arrays.asList(
                new Question(
                    "What is Java?",
                    "A high-level, class-based, object-oriented programming language",
                    "An operating system for supercomputers",
                    "A web browser rendering engine",
                    "A relational database management system",
                    "A",
                    "Java is a popular object-oriented programming language developed by Sun Microsystems (now Oracle).",
                    "Java Fundamentals",
                    1
                ),
                new Question(
                    "What primary role does Spring Boot serve in software development?",
                    "Rapid creation of stand-alone, production-grade Spring-based applications",
                    "Creating 3D video game graphics and physics shaders",
                    "Configuring network routers and hardware firewalls",
                    "Designing 2D vector animations for browsers",
                    "A",
                    "Spring Boot makes it easy to create stand-alone, production-grade Spring based Applications that you can 'just run'.",
                    "Spring Boot Framework",
                    1
                ),
                new Question(
                    "What is MySQL?",
                    "An open-source relational database management system (RDBMS)",
                    "A front-end JavaScript CSS library",
                    "A compiled binary operating system kernel",
                    "A cloud DNS domain name server",
                    "A",
                    "MySQL is an open-source Relational Database Management System based on Structured Query Language (SQL).",
                    "Database Systems",
                    1
                ),
                new Question(
                    "Which Spring Boot annotation marks a class as a web controller capable of handling HTTP requests?",
                    "@Service",
                    "@Controller or @RestController",
                    "@Repository",
                    "@Entity",
                    "B",
                    "@Controller and @RestController are used to define Spring MVC web request controllers.",
                    "Spring MVC",
                    1
                ),
                new Question(
                    "Which JPA annotation specifies the primary key of an entity?",
                    "@Column",
                    "@Table",
                    "@Id",
                    "@GeneratedValue",
                    "C",
                    "The @Id annotation specifies the primary key field of an entity in JPA.",
                    "Java Persistence API (JPA)",
                    1
                ),
                new Question(
                    "What does Thymeleaf do in a Spring Boot application?",
                    "It is a server-side Java template engine for rendering HTML",
                    "It manages database connection pooling",
                    "It compiles Java code to native machine instructions",
                    "It encrypts user passwords in transit",
                    "A",
                    "Thymeleaf is a modern server-side Java template engine for web and standalone environments.",
                    "Web Architecture",
                    1
                ),
                new Question(
                    "Which HTTP method is idempotent and primarily used to update an existing resource completely?",
                    "POST",
                    "PUT",
                    "PATCH",
                    "CONNECT",
                    "B",
                    "PUT is idempotent, meaning multiple identical requests produce the same result as a single request.",
                    "REST Architecture",
                    1
                ),
                new Question(
                    "What is the average time complexity of searching an element in a balanced Binary Search Tree (BST)?",
                    "O(1)",
                    "O(n)",
                    "O(log n)",
                    "O(n^2)",
                    "C",
                    "In a balanced Binary Search Tree, searching takes O(log n) time as each comparison eliminates half the remaining elements.",
                    "Data Structures",
                    1
                )
            );

            questionRepository.saveAll(defaultQuestions);
        }
    }
}
