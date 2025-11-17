# Task Management System

A production-ready Task Management System built with Spring Boot 3.2, featuring real-time notifications, JWT authentication, and comprehensive REST APIs.

## Features

- **User Authentication & Authorization**: Secure JWT-based authentication with role-based access control
- **Project Management**: Create and manage projects with team collaboration
- **Task Management**: CRUD operations for tasks with assignment, priority, and status tracking
- **Real-time Notifications**: WebSocket-based notifications for task assignments and updates
- **Email Notifications**: Automated email alerts for important events
- **Redis Caching**: Improved performance with Redis-based caching
- **API Documentation**: Interactive Swagger/OpenAPI documentation
- **Docker Support**: Fully containerized application with Docker Compose

## Tech Stack

- **Backend**: Spring Boot 3.2.0, Java 21
- **Database**: PostgreSQL 16
- **Cache**: Redis 7
- **Security**: Spring Security 6, JWT (JJWT 0.12.3)
- **Documentation**: SpringDoc OpenAPI 2.3.0
- **Build Tool**: Maven 3.9+
- **Containerization**: Docker & Docker Compose

## Prerequisites

- Java 21 or higher
- Maven 3.9+
- Docker & Docker Compose (for containerized deployment)
- PostgreSQL 16 (if running locally without Docker)
- Redis 7 (if running locally without Docker)

## Quick Start

### Option 1: Docker Compose (Recommended)

1. Clone the repository:
```bash
git clone <repository-url>
cd Task-Management-System
```

2. Build and run with Docker Compose:
```bash
docker-compose up --build
```

3. Access the application:
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html

### Option 2: Local Development

1. Start PostgreSQL and Redis (using Docker Compose for just the databases):
```bash
docker-compose up postgres redis
```

2. Configure application.properties:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/taskmanagement
spring.datasource.username=taskuser
spring.datasource.password=taskpass123
spring.redis.host=localhost
spring.redis.port=6379
```

3. Build and run the application:
```bash
mvn clean install
mvn spring-boot:run
```

## API Endpoints

### Authentication
- `POST /api/auth/signup` - Register a new user
- `POST /api/auth/login` - Login and get JWT token
- `GET /api/auth/validate` - Validate JWT token

### Projects
- `GET /api/projects` - Get all projects
- `POST /api/projects` - Create a new project
- `GET /api/projects/{id}` - Get project by ID
- `PUT /api/projects/{id}` - Update project
- `DELETE /api/projects/{id}` - Delete project
- `POST /api/projects/{projectId}/team-members/{userId}` - Add team member
- `DELETE /api/projects/{projectId}/team-members/{userId}` - Remove team member

### Tasks
- `GET /api/tasks` - Get all tasks
- `POST /api/tasks` - Create a new task
- `GET /api/tasks/{id}` - Get task by ID
- `PUT /api/tasks/{id}` - Update task
- `DELETE /api/tasks/{id}` - Delete task
- `PATCH /api/tasks/{id}/status` - Update task status
- `PATCH /api/tasks/{taskId}/assign/{assigneeId}` - Assign task to user

### Notifications
- `GET /api/notifications` - Get user notifications
- `GET /api/notifications/unread` - Get unread notifications
- `PATCH /api/notifications/{id}/read` - Mark notification as read
- `PATCH /api/notifications/mark-all-read` - Mark all as read
- `DELETE /api/notifications/{id}` - Delete notification

## Authentication

All endpoints except `/api/auth/**` require JWT authentication.

1. Register a new user:
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "password": "password123",
    "email": "john@example.com",
    "fullName": "John Doe"
  }'
```

2. Login to get JWT token:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "password": "password123"
  }'
```

3. Use the token in subsequent requests:
```bash
curl -X GET http://localhost:8080/api/projects \
  -H "Authorization: Bearer <your-jwt-token>"
```

## Configuration

Key configuration properties in `application.properties`:

```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/taskmanagement
spring.datasource.username=taskuser
spring.datasource.password=taskpass123

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Redis
spring.redis.host=localhost
spring.redis.port=6379

# JWT
jwt.secret=mySecretKeyForJWTTokenGenerationAndValidationMustBeLongEnough
jwt.expiration=86400000

# Email (Optional)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

## Project Structure

```
src/main/java/com/taskmanagement/
├── config/              # Configuration classes
│   ├── DataInitializer.java
│   ├── MapperConfig.java
│   ├── OpenApiConfig.java
│   ├── RedisConfig.java
│   ├── SecurityConfig.java
│   └── WebSocketConfig.java
├── controller/          # REST controllers
│   ├── AuthController.java
│   ├── ProjectController.java
│   ├── TaskController.java
│   └── NotificationController.java
├── dto/                 # Data Transfer Objects
├── entity/              # JPA entities
├── mapper/              # Entity-DTO mappers
├── repository/          # Spring Data JPA repositories
├── security/            # Security components
├── service/             # Business logic services
└── util/                # Utility classes
```

## WebSocket Integration

Connect to WebSocket for real-time notifications:

```javascript
A comprehensive, enterprise-grade task management REST API built with Spring Boot 3.2, featuring JWT authentication, real-time WebSocket notifications, Redis caching, and complete CRUD operations for projects and tasks.

---

## 📋 Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [WebSocket Integration](#websocket-integration)
- [Database Schema](#database-schema)
- [Security](#security)
- [Development](#development)
- [Testing](#testing)
- [Deployment](#deployment)
- [Contributing](#contributing)
- [License](#license)

---

## ✨ Features

### Core Functionality
- ✅ **User Management** - Registration, authentication, profile management
- ✅ **Project Management** - Create, update, delete projects with status tracking
- ✅ **Task Management** - Complete CRUD with assignment, priority, and status
- ✅ **Real-time Notifications** - WebSocket-based instant notifications
- ✅ **Role-Based Access Control** - Admin, Manager, and User roles
- ✅ **Email Notifications** - Async email sending with retry logic
- ✅ **Redis Caching** - Performance optimization with distributed cache

### Security
- ✅ **JWT Authentication** - Stateless token-based authentication
- ✅ **BCrypt Password Hashing** - Secure password storage
- ✅ **CORS Configuration** - Cross-origin resource sharing support
- ✅ **Method-Level Security** - @PreAuthorize annotations
- ✅ **Exception Handling** - Global exception handler with detailed error responses

### Technical Features
- ✅ **Pagination** - Efficient data retrieval with page/size/sort params
- ✅ **Validation** - Jakarta Validation with custom validators
- ✅ **Audit Logging** - Automatic created/updated timestamps
- ✅ **API Documentation** - Swagger/OpenAPI 3.0 integration
- ✅ **Health Checks** - Spring Boot Actuator endpoints
- ✅ **H2 Database** - In-memory database for development
- ✅ **PostgreSQL Support** - Production-ready relational database

---

## 🛠 Tech Stack

### Backend Framework
- **Spring Boot** 3.2.0
- **Spring Security** 6.2.0
- **Spring Data JPA** - ORM and database access
- **Spring WebSocket** - Real-time communication
- **Spring Mail** - Email notifications

### Database
- **PostgreSQL** 14 - Production database
- **H2** - In-memory database for development
- **Redis** 7 - Caching layer

### Security
- **JJWT** 0.12.3 - JWT token generation and validation
- **BCrypt** - Password encryption

### Documentation & API
- **Springdoc OpenAPI** 2.3.0 - API documentation
- **Swagger UI** - Interactive API explorer

### Build & Dependencies
- **Maven** 3.9+ - Dependency management
- **Java** 21 - Programming language
- **Lombok** - Boilerplate code reduction

### Development Tools
- **SLF4J/Logback** - Logging
- **Jackson** - JSON serialization
- **Hibernate** - ORM implementation

---

## 🏗 Architecture

### Layered Architecture

```
┌─────────────────────────────────────────┐
│          REST Controllers               │
│   (AuthController, UserController, etc) │
├─────────────────────────────────────────┤
│              DTOs & Mappers             │
│    (SignupRequest, UserDTO, etc)        │
├─────────────────────────────────────────┤
│           Service Layer                 │
│   (AuthService, TaskService, etc)       │
├─────────────────────────────────────────┤
│          Repository Layer               │
│  (UserRepository, ProjectRepository)    │
├─────────────────────────────────────────┤
│          Database (PostgreSQL)          │
└─────────────────────────────────────────┘
```

### Package Structure

```
com.taskmanagement/
├── config/              # Configuration classes
│   ├── AsyncConfig.java
│   ├── RedisConfig.java
│   ├── SecurityConfig.java
│   └── WebSocketConfig.java
├── controller/          # REST endpoints
│   ├── AuthController.java
│   ├── UserController.java
│   ├── ProjectController.java
│   ├── TaskController.java
│   └── NotificationController.java
├── dto/                 # Data Transfer Objects
│   ├── SignupRequest.java
│   ├── LoginRequest.java
│   ├── JwtResponse.java
│   └── ...
├── entity/              # JPA entities
│   ├── User.java
│   ├── Role.java
│   ├── Project.java
│   ├── Task.java
│   └── Notification.java
├── repository/          # Data access layer
│   ├── UserRepository.java
│   ├── ProjectRepository.java
│   ├── TaskRepository.java
│   └── ...
├── service/             # Business logic
│   ├── AuthService.java
│   ├── UserService.java
│   ├── ProjectService.java
│   └── ...
├── security/            # Security components
│   ├── JwtUtils.java
│   ├── UserDetailsImpl.java
│   ├── UserDetailsServiceImpl.java
│   ├── JwtAuthenticationFilter.java
│   └── JwtAuthenticationEntryPoint.java
├── exception/           # Exception handling
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── ...
├── mapper/              # Entity-DTO mappers
│   ├── UserMapper.java
│   ├── ProjectMapper.java
│   └── ...
└── websocket/           # WebSocket services
    ├── WebSocketNotificationService.java
    └── WebSocketEventListener.java
```

---

## 🚀 Getting Started

### Prerequisites

- **Java 21** or higher
- **Maven 3.6+**
- **PostgreSQL 14+** (for production) or use H2 (development)
- **Redis 7+** (optional, falls back to simple cache)

### Quick Start (Development Mode with H2)

1. **Clone the repository**
   ```bash
   git clone https://github.com/UmangDiyora/Task-Management-System.git
   cd Task-Management-System
   ```

2. **Run the application**
   ```bash
   ./setup.sh start
   ```

3. **Access the application**
   - API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - H2 Console: http://localhost:8080/h2-console

### Production Setup with PostgreSQL

1. **Start PostgreSQL and Redis**
   ```bash
   docker-compose up -d postgres redis
   ```

2. **Configure database**
   ```bash
   cp src/main/resources/application-prod.properties.example src/main/resources/application-prod.properties
   # Edit application-prod.properties with your credentials
   ```

3. **Run in production mode**
   ```bash
   ./setup.sh start-prod
   ```

### Using Docker Compose (Full Stack)

```bash
# Start all services
docker-compose up -d

# Check logs
docker-compose logs -f app

# Stop all services
docker-compose down
```

---

## 📚 API Documentation

### Authentication Endpoints

#### Register a New User
```http
POST /api/auth/signup
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123",
  "fullName": "John Doe"
}
```

**Response (201 Created):**
```json
{
  "message": "User registered successfully!"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "johndoe",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "johndoe",
  "email": "john@example.com",
  "fullName": "John Doe",
  "roles": ["ROLE_USER"]
}
```

### User Endpoints

All user endpoints require authentication. Include JWT token in Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

#### Get Current User Profile
```http
GET /api/users/me
```

#### Update Profile
```http
PUT /api/users/me?fullName=John%20Smith&email=johnsmith@example.com
```

#### Change Password
```http
PUT /api/users/me/password?oldPassword=old123&newPassword=new123
```

### Project Endpoints

#### Create Project
```http
POST /api/projects
Content-Type: application/json
Authorization: Bearer <token>

{
  "name": "Website Redesign",
  "description": "Complete website overhaul",
  "startDate": "2025-01-01",
  "endDate": "2025-06-30"
}
```

#### Get My Projects
```http
GET /api/projects/my?page=0&size=10
```

#### Update Project
```http
PUT /api/projects/1?name=New%20Name&status=COMPLETED
```

#### Delete Project
```http
DELETE /api/projects/1
```

### Task Endpoints

#### Create Task
```http
POST /api/tasks
Content-Type: application/json
Authorization: Bearer <token>

{
  "title": "Design homepage mockup",
  "description": "Create wireframes and mockups for the new homepage",
  "projectId": 1,
  "priority": "HIGH",
  "dueDate": "2025-02-15"
}
```

#### Assign Task
```http
PUT /api/tasks/1/assign/2
Authorization: Bearer <token>
```

#### Update Task Status
```http
PUT /api/tasks/1/status?status=IN_PROGRESS
```

#### Get My Tasks
```http
GET /api/tasks/my?page=0&size=10
```

#### Get Tasks by Project
```http
GET /api/tasks/project/1?page=0&size=10
```

### Notification Endpoints

#### Get User Notifications
```http
GET /api/notifications?page=0&size=10
```

#### Get Unread Count
```http
GET /api/notifications/unread/count
```

#### Mark as Read
```http
PUT /api/notifications/1/read
```

#### Mark All as Read
```http
PUT /api/notifications/read-all
```

### Complete API Reference

Visit Swagger UI at http://localhost:8080/swagger-ui.html for interactive API documentation.

---

## 🔌 WebSocket Integration

### JavaScript Client Example

```javascript
// Include SockJS and STOMP libraries
<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@stomp/stompjs@7/dist/stomp.umd.min.js"></script>

// Connect to WebSocket
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    stompClient.subscribe('/user/queue/notifications', function(notification) {
        console.log('Received notification:', JSON.parse(notification.body));
    });
});
```

## Testing

Run tests with:
```bash
mvn test
```

Run with coverage:
```bash
mvn clean test jacoco:report
```

## Swagger Documentation

Access interactive API documentation at:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI Spec: http://localhost:8080/v3/api-docs

## Docker Commands

```bash
# Build and start all services
docker-compose up --build

# Start in detached mode
docker-compose up -d

# Stop all services
docker-compose down

# View logs
docker-compose logs -f app

# Rebuild specific service
docker-compose up --build app
```

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| SPRING_DATASOURCE_URL | PostgreSQL connection URL | jdbc:postgresql://localhost:5432/taskmanagement |
| SPRING_DATASOURCE_USERNAME | Database username | taskuser |
| SPRING_DATASOURCE_PASSWORD | Database password | taskpass123 |
| SPRING_REDIS_HOST | Redis host | localhost |
| SPRING_REDIS_PORT | Redis port | 6379 |
| JWT_SECRET | JWT secret key | (see application.properties) |
| JWT_EXPIRATION | JWT expiration time (ms) | 86400000 (24 hours) |

## Testing

The project includes comprehensive testing covering unit tests, integration tests, repository tests, and performance tests.

### Running Tests

```bash
# Run all tests
./run-tests.sh all

# Run unit tests only
./run-tests.sh unit

# Run integration tests only
./run-tests.sh integration

# Run with coverage report
./run-tests.sh coverage

# Run performance tests (requires JMeter)
./run-tests.sh performance

# Quick test suite (unit + smoke)
./run-tests.sh quick
```

### Test Coverage

- **Unit Tests**: Service layer testing with Mockito
- **Integration Tests**: Full API endpoint testing
- **Repository Tests**: Database query testing
- **Smoke Tests**: Critical path verification
- **Performance Tests**: Load and stress testing

View detailed testing documentation in [TESTING.md](TESTING.md)

## Troubleshooting

### Application won't start
- Ensure PostgreSQL and Redis are running
- Check database credentials in application.properties
- Verify Java 21 is installed: `java -version`

### Database connection errors
- Verify PostgreSQL is running: `docker-compose ps`
- Check connection URL and credentials
- Ensure database 'taskmanagement' exists

### Redis connection errors
- Verify Redis is running: `docker-compose ps`
- Check Redis host and port configuration

## License

This project is licensed under the Apache License 2.0.

## Contributors

- Task Management Team

## Support

For issues and questions, please create an issue in the repository.
