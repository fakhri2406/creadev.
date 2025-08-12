# creadev. (Backend)

A comprehensive Spring Boot backend service for **creadev.** specializing in custom software development (functional platforms, corporate websites, mobile apps, etc.). This backend provides:

- A categories & products catalog
- An AI assistant chatbot
- Full admin/editor authentication, authorization & admin endpoints

---

## Features

- **`creadev.ai`**: Unified AI assistant
  - Automatically classifies input as either a general company/product query or a project request.
  - For general queries: returns direct, concise answers.
  - For project requests: performs follow-up prompts as needed; once complete, emails a structured summary and returns a confirmation message.

- **Role-Based Access Control**: Three distinct roles (ADMIN, EDITOR, Public) with different access levels.

- **JWT Authentication**: Secure, token-based auth with endpoints for login, token refresh & logout.

- **User Management**: ADMIN-only CRUD for user accounts.

- **Category Management**: Public read; ADMIN-only create, update, delete.

- **Product Management**: Public read, search & filter; ADMIN/EDITOR create-update (with image upload), ADMIN delete.

- **Cloudinary Integration**: Secure cloud-based storage for product images.

- **API Documentation**: Interactive Swagger UI powered by springdoc-openapi.

- **Database Migrations**: Flyway for version-controlled schema evolution.

- **AOP-Based Logging & Global Error Handling**: Aspect-oriented logging and centralized exception responses.

- **Initial Data Seeding**: Automatic creation of default roles & admin user on startup.

---

## Tech Stack

- **Java 21** / **Gradle (Kotlin DSL)**
- **Spring Boot 3.5.3 / Spring Security / Spring Data JPA**
- **Spring Boot Starter Mail** (SMTP email dispatch)
- **GraalVM Native Image** (For lower memory footprint)
- **Flyway** (DB migrations)
- **Cloudinary** (File storage)
- **OpenAI** (Trained chatbot)
- **MapStruct** (DTO ↔ Entity mapping)
- **Lombok**
- **Swagger/OpenAPI**
- **SonarQube** (Static code quality analysis)
- **JUnit & Spring Boot Test**

---

## Project Structure

```plaintext
src/main/java/com/creadev/
├── config/                 # Configuration classes
├── controller/             # REST API controllers
├── domain/                 # JPA entity classes
├── dto/                    # Data Transfer Objects
│   ├── request/            # Incoming payloads
│   └── response/           # Outgoing payloads
├── external/               # Third-party service integrations
├── repository/             # Spring Data repositories
├── service/                # Business logic
│   └── impl/               # Service implementations
└── util/                   # Utility classes
```

---  

*Built with modern technologies to meet all client requirements and deliver scalable, maintainable solutions.* 
