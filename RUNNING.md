# Running the Task Management System

## Quick Start (Development Mode)

```bash
./setup.sh start
```

This will:
1. Create `application-dev.properties` from template
2. Start the application with H2 in-memory database
3. No Docker/PostgreSQL/Redis required

## Known Issue: DNS Resolution in GitHub Codespaces

### Problem
Maven cannot resolve domain names like `repo1.maven.org` due to Java DNS resolution failure in GitHub Codespaces. This is an environment-level issue, not a code problem.

**Error Message:**
```
repo1.maven.org: Temporary failure in name resolution
```

### Solutions

#### Solution 1: Use Pre-Built JAR (Recommended for Codespaces)

If the application was previously built successfully:

```bash
# Find the built JAR
ls -la target/*.jar

# Run directly with Java
java -jar target/task-management-system-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

#### Solution 2: Run in Local Environment

Clone and run on your local machine where DNS works properly:

```bash
git clone <repository-url>
cd Task-Management-System
./setup.sh start
```

#### Solution 3: Use Codespace Rebuild

Sometimes rebuilding the Codespace fixes DNS issues:

1. Press `Ctrl+Shift+P` (or `Cmd+Shift+P` on Mac)
2. Type "Codespaces: Rebuild Container"
3. Wait for rebuild to complete
4. Try `./setup.sh start` again

#### Solution 4: Manual Dependency Download (Advanced)

If you have a working internet connection but Maven DNS fails:

```bash
# Clean Maven cache
rm -rf ~/.m2/repository

# Try with different DNS settings
export _JAVA_OPTIONS="-Djava.net.preferIPv4Stack=true"
mvn clean install -DskipTests
```

## Setup Commands

### Available Commands

```bash
./setup.sh setup       # Setup development environment
./setup.sh build       # Build application (skip tests)
./setup.sh test        # Run all tests
./setup.sh start       # Start in dev mode (H2 database)
./setup.sh start-prod  # Start in prod mode (PostgreSQL/Redis)
./setup.sh clean       # Clean build artifacts
./setup.sh info        # Show application information
```

## Development Mode (H2 Database)

When running in development mode:

- **Application URL:** http://localhost:8080
- **H2 Console:** http://localhost:8080/h2-console
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **API Docs:** http://localhost:8080/v3/api-docs

### H2 Database Connection

When accessing H2 Console, use:
- **Driver Class:** `org.h2.Driver`
- **JDBC URL:** `jdbc:h2:mem:taskmanagement`
- **Username:** `sa`
- **Password:** (leave empty)

### Data Persistence

⚠️ **Important:** H2 in-memory database is cleared when the application stops. Data is not persisted between restarts.

## Production Mode (PostgreSQL + Redis)

### Using Docker Compose (Recommended)

```bash
# Start PostgreSQL and Redis
docker-compose up -d postgres redis

# Copy and configure production properties
cp src/main/resources/application-prod.properties.example src/main/resources/application-prod.properties
# Edit application-prod.properties with your database credentials

# Start application
./setup.sh start-prod
```

### Manual Setup

#### PostgreSQL

```bash
# Create database
createdb taskmanagement

# Or using psql
psql -U postgres
CREATE DATABASE taskmanagement;
```

#### Redis

```bash
# Install and start Redis
sudo apt-get install redis-server
sudo systemctl start redis-server
```

#### Configuration

Edit `src/main/resources/application-prod.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/taskmanagement
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.data.redis.host=localhost
spring.data.redis.port=6379
```

## Default Data

After startup, these roles are automatically seeded:
- `ROLE_USER` - Standard user role
- `ROLE_ADMIN` - Administrator role
- `ROLE_MANAGER` - Manager role

## Troubleshooting

### Application won't start

1. **Check Java version:**
   ```bash
   java -version  # Should be 21 or higher
   ```

2. **Check Maven version:**
   ```bash
   mvn -version   # Should be 3.6 or higher
   ```

3. **DNS/Network issues:**
   - See "Known Issue: DNS Resolution" above
   - Try running pre-built JAR instead
   - Try in local environment

### Port 8080 already in use

```bash
# Find and kill process on port 8080
lsof -ti:8080 | xargs kill -9

# Or use different port
mvn spring-boot:run -Dspring-boot.run.profiles=dev -Dserver.port=8081
```

### Database connection errors

**For H2 (dev mode):**
- H2 is in-memory, no setup required
- Check that `application-dev.properties` exists

**For PostgreSQL (prod mode):**
- Ensure PostgreSQL is running: `pg_isready`
- Check database exists: `psql -l | grep taskmanagement`
- Verify credentials in `application-prod.properties`

### Redis connection errors

**For dev mode:**
- Redis is optional, simple cache is used
- No action required

**For prod mode:**
- Ensure Redis is running: `redis-cli ping`
- Should return `PONG`
- Check Redis configuration in `application-prod.properties`

## API Documentation

Once the application is running, visit:
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs

## Health Check

```bash
curl http://localhost:8080/actuator/health
```

Should return:
```json
{"status":"UP"}
```

## Next Steps

After successfully running the application:

1. Access Swagger UI to explore API endpoints
2. Register a new user via `/api/auth/signup` (when implemented)
3. Login via `/api/auth/login` to get JWT token
4. Use JWT token in Authorization header for protected endpoints

---

**Note:** Parts 1-4 are complete (Foundation, Data Layer, Security, Services). API endpoints (Part 5) are coming next.
