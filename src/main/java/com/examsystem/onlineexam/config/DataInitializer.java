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
                // ======================== 1. Java Fundamentals (5 Questions) ========================
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
                    "Which JVM memory region is shared among all running threads and stores instantiated objects and arrays?",
                    "Program Counter (PC) Register",
                    "Java Thread Stack",
                    "Heap Memory",
                    "Native Method Stack",
                    "C",
                    "In the JVM, Heap memory is the runtime data area from which memory for all class instances and arrays is allocated, shared across all threads.",
                    "Java Fundamentals",
                    1
                ),
                new Question(
                    "Which of the following is an unchecked (runtime) exception in Java?",
                    "IOException",
                    "SQLException",
                    "NullPointerException",
                    "ClassNotFoundException",
                    "C",
                    "NullPointerException extends RuntimeException, making it an unchecked exception that does not require mandatory try-catch or throws declaration.",
                    "Java Fundamentals",
                    1
                ),
                new Question(
                    "What key feature was introduced in Java 8 to enable functional programming styles and passing behavior as parameters?",
                    "Generics",
                    "Lambda Expressions",
                    "Annotations",
                    "Enums",
                    "B",
                    "Java 8 introduced Lambda Expressions, enabling functional interfaces and concise functional programming constructs.",
                    "Java Fundamentals",
                    1
                ),
                new Question(
                    "What is the difference between '==' and '.equals()' when comparing two String objects in Java?",
                    "'==' compares memory references, whereas '.equals()' compares character contents",
                    "'==' compares character contents, whereas '.equals()' compares memory references",
                    "Both operators perform identical reference comparisons",
                    "'.equals()' is only applicable to primitive data types",
                    "A",
                    "'==' checks reference equality (pointing to the same memory object), while '.equals()' in String checks value equality (identical character sequence).",
                    "Java Fundamentals",
                    1
                ),

                // ======================== 2. Spring Boot Framework (5 Questions) ========================
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
                    "Which Spring Boot starter dependency is primarily required to build web and RESTful applications with embedded Tomcat?",
                    "spring-boot-starter-data-jpa",
                    "spring-boot-starter-web",
                    "spring-boot-starter-security",
                    "spring-boot-starter-test",
                    "B",
                    "spring-boot-starter-web includes Spring MVC, REST support, and an embedded Apache Tomcat web server.",
                    "Spring Boot Framework",
                    1
                ),
                new Question(
                    "What three core annotations are bundled within the meta-annotation @SpringBootApplication?",
                    "@Configuration, @EnableAutoConfiguration, and @ComponentScan",
                    "@Controller, @Service, and @Repository",
                    "@Entity, @Table, and @Id",
                    "@Component, @Bean, and @Autowired",
                    "A",
                    "@SpringBootApplication is a convenience annotation that wraps @Configuration, @EnableAutoConfiguration, and @ComponentScan.",
                    "Spring Boot Framework",
                    1
                ),
                new Question(
                    "Where is standard external application configuration typically maintained in a Spring Boot project?",
                    "src/main/resources/application.properties or application.yml",
                    "src/main/resources/web.xml",
                    "pom.xml dependencies block",
                    "System32 Windows registry",
                    "A",
                    "Spring Boot automatically loads external configuration properties from application.properties or application.yml located in classpath resources.",
                    "Spring Boot Framework",
                    1
                ),
                new Question(
                    "Which Spring Boot Actuator endpoint exposes the overall health and readiness of the running service?",
                    "/actuator/info",
                    "/actuator/metrics",
                    "/actuator/health",
                    "/actuator/beans",
                    "C",
                    "The /actuator/health endpoint provides basic or detailed status information indicating whether the application is UP or DOWN.",
                    "Spring Boot Framework",
                    1
                ),

                // ======================== 3. Database Systems (5 Questions) ========================
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
                    "Which SQL clause is used to filter records returned by a SELECT query according to specified conditions?",
                    "GROUP BY",
                    "ORDER BY",
                    "WHERE",
                    "HAVING",
                    "C",
                    "The WHERE clause filters rows before any grouping occurs, returning only records that meet the boolean condition.",
                    "Database Systems",
                    1
                ),
                new Question(
                    "What is the primary benefit of creating a B-Tree index on a frequently queried database column?",
                    "It significantly speeds up record search and retrieval operations",
                    "It compresses the entire database table into a smaller ZIP file",
                    "It automatically encrypts user passwords in plaintext columns",
                    "It prevents any new INSERT statements from executing",
                    "A",
                    "Indexes allow the database engine to find associated rows much faster than scanning the entire table sequentially.",
                    "Database Systems",
                    1
                ),
                new Question(
                    "Which ACID property ensures that all operations within a database transaction either fully complete or completely rollback?",
                    "Atomicity",
                    "Consistency",
                    "Isolation",
                    "Durability",
                    "A",
                    "Atomicity ensures 'all-or-nothing' execution — if any step in the transaction fails, all changes are rolled back.",
                    "Database Systems",
                    1
                ),
                new Question(
                    "Which SQL constraint guarantees that all values in a designated column are distinct and cannot be duplicated?",
                    "FOREIGN KEY",
                    "UNIQUE",
                    "DEFAULT",
                    "CHECK",
                    "B",
                    "The UNIQUE constraint ensures that all values in a column are distinct, rejecting duplicate entries.",
                    "Database Systems",
                    1
                ),

                // ======================== 4. Spring MVC (4 Questions) ========================
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
                    "Which annotation binds an incoming HTTP request query parameter or form field to a controller method parameter?",
                    "@RequestParam",
                    "@PathVariable",
                    "@RequestBody",
                    "@ModelAttribute",
                    "A",
                    "@RequestParam binds query parameters or submitted form data fields to handler method arguments.",
                    "Spring MVC",
                    1
                ),
                new Question(
                    "What is the key difference between @Controller and @RestController in Spring?",
                    "@RestController implicitly adds @ResponseBody to every handler method, returning serialized data instead of HTML view names",
                    "@RestController can only connect to MySQL databases",
                    "@Controller does not support HTTP GET requests",
                    "@RestController is deprecated in Spring Boot 3",
                    "A",
                    "@RestController is a convenience meta-annotation combining @Controller and @ResponseBody.",
                    "Spring MVC",
                    1
                ),
                new Question(
                    "Which Spring MVC annotation maps an HTTP POST request to a specific handler method?",
                    "@GetMapping",
                    "@PostMapping",
                    "@PutMapping",
                    "@DeleteMapping",
                    "B",
                    "@PostMapping is a specialized shortcut for @RequestMapping(method = RequestMethod.POST).",
                    "Spring MVC",
                    1
                ),

                // ======================== 5. Java Persistence API (JPA) (4 Questions) ========================
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
                    "Which annotation is placed on an entity field to specify that its primary key value should be generated automatically by the database identity column?",
                    "@GeneratedValue(strategy = GenerationType.IDENTITY)",
                    "@Transient",
                    "@Enumerated",
                    "@Version",
                    "A",
                    "GenerationType.IDENTITY relies on an auto-increment or identity column in the database to generate unique primary keys.",
                    "Java Persistence API (JPA)",
                    1
                ),
                new Question(
                    "Which Spring Data interface provides standard pagination, sorting, and CRUD methods out of the box?",
                    "JpaRepository",
                    "EntityManagerFactory",
                    "JdbcTemplate",
                    "SessionManager",
                    "A",
                    "JpaRepository extends PagingAndSortingRepository and CrudRepository, providing complete data access operations.",
                    "Java Persistence API (JPA)",
                    1
                ),
                new Question(
                    "What does the @Transient annotation indicate on an entity attribute?",
                    "The field should not be persisted or mapped to any column in the database",
                    "The field must be encrypted before writing to disk",
                    "The field is a foreign key to another table",
                    "The field can only hold positive numbers",
                    "A",
                    "@Transient denotes that a property is not persistent and will not be mapped to any database table column.",
                    "Java Persistence API (JPA)",
                    1
                ),

                // ======================== 6. Web Architecture (4 Questions) ========================
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
                    "Which HTTP status code signifies that a requested resource was not found on the server?",
                    "200 OK",
                    "400 Bad Request",
                    "404 Not Found",
                    "500 Internal Server Error",
                    "C",
                    "HTTP 404 indicates the origin server could not find a current representation for the target resource.",
                    "Web Architecture",
                    1
                ),
                new Question(
                    "What security mechanism in web browsers prevents a script on one origin from accessing sensitive data on another origin without explicit permission?",
                    "Same-Origin Policy (SOP)",
                    "Domain Name Resolution (DNS)",
                    "Address Resolution Protocol (ARP)",
                    "Dynamic Host Configuration (DHCP)",
                    "A",
                    "The Same-Origin Policy is a fundamental web application security model that restricts how documents loaded from one origin can interact with resources from another.",
                    "Web Architecture",
                    1
                ),
                new Question(
                    "Which cookie attribute prevents client-side scripts (such as JavaScript) from accessing the cookie, mitigating XSS token theft?",
                    "HttpOnly",
                    "Secure",
                    "SameSite",
                    "Domain",
                    "A",
                    "The HttpOnly flag directs browsers that the cookie should not be accessible via client-side scripts (e.g. document.cookie).",
                    "Web Architecture",
                    1
                ),

                // ======================== 7. REST Architecture (4 Questions) ========================
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
                    "Which HTTP status code represents successful creation of a new resource via a POST request?",
                    "200 OK",
                    "201 Created",
                    "204 No Content",
                    "301 Moved Permanently",
                    "B",
                    "HTTP 201 Created indicates that the request has succeeded and led to the creation of a new resource.",
                    "REST Architecture",
                    1
                ),
                new Question(
                    "What architectural constraint in REST requires each client request to contain all necessary context for the server to process it without stored session state?",
                    "Statelessness",
                    "Cacheability",
                    "Layered System",
                    "Code on Demand",
                    "A",
                    "Statelessness ensures each client request must contain all information needed to understand and complete the request.",
                    "REST Architecture",
                    1
                ),
                new Question(
                    "Which standard data serialization format is most widely used for request and response payloads in modern REST APIs?",
                    "JSON (JavaScript Object Notation)",
                    "Binary PostScript",
                    "Raw Bitmap (BMP)",
                    "Compiled Bytecode",
                    "A",
                    "JSON is the lightweight, human-readable data interchange format ubiquitous in modern RESTful web services.",
                    "REST Architecture",
                    1
                ),

                // ======================== 8. Data Structures (4 Questions) ========================
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
                ),
                new Question(
                    "Which data structure operates strictly on a Last In First Out (LIFO) access sequence?",
                    "Queue",
                    "Stack",
                    "Linked List",
                    "Min Heap",
                    "B",
                    "A Stack is a linear data structure that follows the Last In, First Out (LIFO) principle.",
                    "Data Structures",
                    1
                ),
                new Question(
                    "What is the average time complexity of searching for a key in a well-distributed Hash Table?",
                    "O(1)",
                    "O(log n)",
                    "O(n)",
                    "O(n log n)",
                    "A",
                    "A hash table with a good hash function and reasonable load factor achieves O(1) constant time on average for lookups.",
                    "Data Structures",
                    1
                ),
                new Question(
                    "Which non-linear data structure consists of nodes connected by edges without any cycles?",
                    "Tree",
                    "Complete Graph",
                    "Hash Set",
                    "Array",
                    "A",
                    "A tree is an undirected, connected acyclic graph where any two vertices are connected by exactly one simple path.",
                    "Data Structures",
                    1
                )
            );

            questionRepository.saveAll(defaultQuestions);
        }
    }
}
