# creadev. (Backend)

A comprehensive Spring Boot backend service for **creadev.** specializing in custom software development (functional platforms, corporate websites, mobile apps, etc.). This backend provides:

- A categories & products catalog
- An AI chatbot assistant for general info and project requests
- Full admin/editor authentication, authorization & admin endpoints

---

## Features

- **`creadev.ai`**:
  - **Project Request**: AI-powered parsing of freeform project requests and sending the structured summary via email.
  - **General Assistant**: Ask questions about the company, categories or products.

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

- **Java 21** / **Gradle (Groovy DSL)**
- **Spring Boot 3.x / Spring Security / Spring Data JPA**
- **Spring Boot Starter Mail** (SMTP email dispatch)
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
├── config/                 # Application configuration
│   ├── jwt/                # JWT security setup
│   ├── logging/            # AOP logging aspect
│   ├── cloudinary/         # Cloudinary config
│   ├── admin/              # Admin properties & data seeding
│   ├── openai/             # OpenAI config
│   ├── phone/              # Phone properties
│   ├── jackson/            # Jackson config to allow complex JSON
│   └── swagger/            # Swagger/OpenAPI config
├── controller/             # REST API controllers
├── domain/                 # JPA entity classes
├── dto/                    # Data Transfer Objects
│   ├── request/            # Incoming payloads
│   └── response/           # Outgoing payloads
├── external/               # Third-party service integrations
├── repository/             # Spring Data repositories
├── service/                # Business logic
│   └── impl/               # Service implementations
└── util/                   # Utilities
```

---  

*Built with modern technologies to meet all client requirements and deliver scalable, maintainable solutions.* 
