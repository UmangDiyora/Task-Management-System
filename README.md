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
