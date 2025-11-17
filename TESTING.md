# Testing Documentation

Comprehensive testing guide for the Task Management System.

## Table of Contents

1. [Testing Strategy](#testing-strategy)
2. [Test Types](#test-types)
3. [Running Tests](#running-tests)
4. [Test Coverage](#test-coverage)
5. [Performance Testing](#performance-testing)
6. [Best Practices](#best-practices)

## Testing Strategy

Our testing strategy follows the testing pyramid:

```
      /\
     /  \    End-to-End Tests (Few)
    /____\
   /      \  Integration Tests (Some)
  /________\
 /          \ Unit Tests (Many)
/__________\
```

### Test Distribution

- **Unit Tests**: 60% - Fast, isolated tests for business logic
- **Integration Tests**: 30% - Tests for API endpoints and database interactions
- **Performance Tests**: 10% - Load and stress tests

## Test Types

### 1. Unit Tests

Unit tests verify individual components in isolation using mocks.

**Location**: `src/test/java/com/taskmanagement/service/`

**Examples**:
- `AuthServiceTest.java` - Authentication service tests
- `TaskServiceTest.java` - Task service tests
- `ProjectServiceTest.java` - Project service tests

**Running Unit Tests**:
```bash
mvn test -Dtest="*ServiceTest"
```

### 2. Integration Tests

Integration tests verify the entire request-response cycle including controllers, services, and database.

**Location**: `src/test/java/com/taskmanagement/integration/`

**Examples**:
- `AuthControllerIntegrationTest.java` - Authentication endpoints
- `TaskControllerIntegrationTest.java` - Task management endpoints
- `ProjectControllerIntegrationTest.java` - Project management endpoints

**Running Integration Tests**:
```bash
mvn test -Dtest="*IntegrationTest"
```

### 3. Repository Tests

Repository tests verify database queries and JPA operations.

**Location**: `src/test/java/com/taskmanagement/repository/`

**Examples**:
- `TaskRepositoryTest.java` - Task repository queries
- `ProjectRepositoryTest.java` - Project repository queries
- `UserRepositoryTest.java` - User repository queries

**Running Repository Tests**:
```bash
mvn test -Dtest="*RepositoryTest"
```

### 4. Smoke Tests

Smoke tests verify critical application functionality and bean initialization.

**Location**: `src/test/java/com/taskmanagement/integration/SmokeTest.java`

**Purpose**:
- Verify application context loads
- Check all critical beans are initialized
- Ensure basic functionality works

**Running Smoke Tests**:
```bash
mvn test -Dtest="SmokeTest"
```

### 5. Performance Tests

#### JMeter Load Tests

**Location**: `src/test/resources/jmeter/`

**Test Plans**:
- `task-management-load-test.jmx` - Authentication load test
- `api-stress-test.jmx` - Full API stress test

**Running JMeter Tests**:
```bash
# Using JMeter GUI
jmeter -t src/test/resources/jmeter/task-management-load-test.jmx

# Using JMeter CLI (headless)
jmeter -n -t src/test/resources/jmeter/task-management-load-test.jmx -l results.jtl -e -o reports/
```

**JMeter Test Scenarios**:

1. **Authentication Load Test**
   - Users: 100
   - Ramp-up: 30 seconds
   - Duration: 2 minutes
   - Operations: Signup → Login

2. **API Stress Test**
   - Users: 500
   - Ramp-up: 60 seconds
   - Duration: 5 minutes
   - Operations: Create Project → Create Tasks → Update Tasks

#### Gatling Performance Tests

**Location**: `src/test/java/com/taskmanagement/performance/`

**Scenarios**:
- User authentication load test
- Task management load test
- Stress test
- Endurance test

**Running Gatling Tests**:
```bash
mvn gatling:test
```

### 6. Regression Tests

Regression tests ensure that new changes don't break existing functionality.

**Strategy**:
- Run full test suite before each release
- Automated CI/CD pipeline execution
- Track test results over time

**Running All Tests**:
```bash
mvn clean test
```

### 7. Exploratory Testing

While automated tests cover most scenarios, exploratory testing helps find edge cases.

**Focus Areas**:
- User authentication edge cases
- Concurrent task updates
- Large data sets
- Network failures
- Invalid input handling

**Checklist**:
- [ ] Test with invalid JWT tokens
- [ ] Test with expired sessions
- [ ] Test concurrent project updates
- [ ] Test with special characters in input
- [ ] Test with very long strings
- [ ] Test rate limiting
- [ ] Test WebSocket reconnection

## Running Tests

### Run All Tests
```bash
mvn clean test
```

### Run Specific Test Class
```bash
mvn test -Dtest=AuthServiceTest
```

### Run Specific Test Method
```bash
mvn test -Dtest=AuthServiceTest#testRegisterUser_Success
```

### Run Tests with Coverage
```bash
mvn clean test jacoco:report
```

View coverage report: `target/site/jacoco/index.html`

### Run Tests in Parallel
```bash
mvn -T 4 test
```

### Skip Tests
```bash
mvn clean install -DskipTests
```

## Test Coverage

### Coverage Goals

- **Overall Coverage**: 80%+
- **Service Layer**: 90%+
- **Controller Layer**: 85%+
- **Repository Layer**: 70%+

### Generating Coverage Reports

```bash
mvn clean test jacoco:report
```

Reports are generated in:
- HTML: `target/site/jacoco/index.html`
- XML: `target/site/jacoco/jacoco.xml`
- CSV: `target/site/jacoco/jacoco.csv`

### SonarQube Integration

```bash
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=task-management-system \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=<token>
```

## Performance Testing

### Performance Test Scenarios

#### 1. Load Testing
**Goal**: Verify system behavior under expected load

- **Normal Load**: 100 concurrent users
- **Peak Load**: 500 concurrent users
- **Duration**: 5-10 minutes

**Acceptance Criteria**:
- Response time p95 < 500ms
- Error rate < 1%
- CPU usage < 70%
- Memory usage stable

#### 2. Stress Testing
**Goal**: Find system breaking point

- **Users**: Ramp up to 1000+
- **Duration**: 15 minutes
- **Pattern**: Gradual increase

**Metrics to Monitor**:
- Maximum concurrent users supported
- Response time degradation curve
- Error rate increase point
- Resource saturation point

#### 3. Endurance Testing
**Goal**: Verify system stability over time

- **Users**: 200 constant
- **Duration**: 30-60 minutes
- **Pattern**: Consistent load

**Watch For**:
- Memory leaks
- Database connection leaks
- Performance degradation
- Log file growth

#### 4. Spike Testing
**Goal**: Verify system handles sudden load increases

- **Pattern**: Normal → Spike → Normal
- **Spike Duration**: 2-5 minutes
- **Spike Multiplier**: 5-10x normal load

**Acceptance Criteria**:
- System recovers after spike
- No permanent performance degradation
- No data corruption

### Performance Benchmarks

| Endpoint | Operation | p50 | p95 | p99 |
|----------|-----------|-----|-----|-----|
| POST /api/auth/signup | Register | 50ms | 200ms | 500ms |
| POST /api/auth/login | Login | 30ms | 150ms | 300ms |
| GET /api/projects | List | 20ms | 100ms | 200ms |
| POST /api/projects | Create | 40ms | 180ms | 400ms |
| GET /api/tasks | List | 25ms | 120ms | 250ms |
| POST /api/tasks | Create | 45ms | 200ms | 450ms |
| PATCH /api/tasks/{id}/status | Update | 30ms | 150ms | 300ms |

## TestContainers

For integration tests requiring real databases, we use TestContainers.

**Configuration**: `src/test/java/com/taskmanagement/integration/TestContainersConfiguration.java`

**Containers**:
- PostgreSQL 16
- Redis 7

**Usage**:
```java
@SpringBootTest
@Import(TestContainersConfiguration.class)
class MyIntegrationTest {
    // Tests automatically use containerized databases
}
```

## CI/CD Integration

### GitHub Actions

```yaml
name: Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 21
        uses: actions/setup-java@v2
        with:
          java-version: '21'
      - name: Run tests
        run: mvn clean test
      - name: Generate coverage report
        run: mvn jacoco:report
      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v2
```

### Test Execution Order

1. **Pre-commit**: Unit tests (fast feedback)
2. **On Push**: All tests + coverage
3. **Nightly**: Performance tests
4. **Pre-release**: Full regression suite

## Best Practices

### 1. Test Naming Convention
```java
@Test
void testMethodName_Scenario_ExpectedBehavior()
```

Example: `testRegisterUser_DuplicateUsername_ThrowsException()`

### 2. AAA Pattern
```java
@Test
void testExample() {
    // Arrange - Set up test data
    User user = new User();

    // Act - Execute the method
    UserDTO result = service.createUser(user);

    // Assert - Verify the result
    assertNotNull(result);
}
```

### 3. Test Data Builders
```java
User.builder()
    .username("testuser")
    .email("test@example.com")
    .build();
```

### 4. Use Descriptive Assertions
```java
// Good
assertEquals("testuser", result.getUsername(),
    "Username should match the input");

// Bad
assertEquals("testuser", result.getUsername());
```

### 5. Test Independence
- Each test should be independent
- No test should depend on another
- Use @BeforeEach for setup

### 6. Mock External Dependencies
- Mock external APIs
- Mock file systems
- Mock time-dependent code

## Troubleshooting

### Tests Failing Due to Port Conflicts
```bash
# Kill processes on port 8080
lsof -ti:8080 | xargs kill -9
```

### TestContainers Issues
```bash
# Ensure Docker is running
docker ps

# Clean up containers
docker system prune -a
```

### Coverage Not Generated
```bash
# Clean and rebuild
mvn clean test jacoco:report
```

## Resources

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)
- [TestContainers](https://www.testcontainers.org/)
- [JMeter User Guide](https://jmeter.apache.org/usermanual/index.html)
- [Gatling Documentation](https://gatling.io/docs/gatling/)
