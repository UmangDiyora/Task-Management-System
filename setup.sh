#!/bin/bash

echo "=========================================="
echo "Task Management System - Development Setup"
echo "=========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Function to check if Java is installed
check_java() {
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | head -n 1 | awk -F '"' '{print $2}')
        echo -e "${GREEN}✓${NC} Java is installed: $JAVA_VERSION"
        return 0
    else
        echo -e "${RED}✗${NC} Java is not installed"
        echo "  Please install Java 21 or higher"
        return 1
    fi
}

# Function to check if Maven is installed
check_maven() {
    if command -v mvn &> /dev/null; then
        MVN_VERSION=$(mvn -version 2>&1 | head -n 1)
        echo -e "${GREEN}✓${NC} Maven is installed: $MVN_VERSION"
        return 0
    else
        echo -e "${RED}✗${NC} Maven is not installed"
        echo "  Please install Maven 3.6 or higher"
        return 1
    fi
}

# Function to setup development environment
setup_dev_env() {
    echo ""
    echo "Setting up development environment..."
    echo ""

    # Create development properties file if it doesn't exist
    if [ ! -f "src/main/resources/application-dev.properties" ]; then
        echo -e "${YELLOW}Creating application-dev.properties from template...${NC}"
        cp src/main/resources/application-dev.properties.example src/main/resources/application-dev.properties
        echo -e "${GREEN}✓${NC} Created application-dev.properties"
    else
        echo -e "${GREEN}✓${NC} application-dev.properties already exists"
    fi

    echo ""
    echo -e "${GREEN}Development environment configured!${NC}"
    echo ""
    echo "Configuration:"
    echo "  - Database: H2 In-Memory"
    echo "  - Cache: Simple (no Redis required)"
    echo "  - H2 Console: http://localhost:8080/h2-console"
    echo "  - JDBC URL: jdbc:h2:mem:taskmanagement"
    echo "  - Username: sa"
    echo "  - Password: (empty)"
    echo ""
}

# Function to build the application
build_app() {
    echo ""
    echo "Building application..."
    echo ""

    mvn clean install -DskipTests

    if [ $? -eq 0 ]; then
        echo ""
        echo -e "${GREEN}✓${NC} Build successful!"
        return 0
    else
        echo ""
        echo -e "${RED}✗${NC} Build failed!"
        return 1
    fi
}

# Function to run tests
run_tests() {
    echo ""
    echo "Running tests..."
    echo ""

    mvn test

    if [ $? -eq 0 ]; then
        echo ""
        echo -e "${GREEN}✓${NC} All tests passed!"
        return 0
    else
        echo ""
        echo -e "${YELLOW}!${NC} Some tests may have failed"
        return 1
    fi
}

# Function to start the application
start_app() {
    echo ""
    echo "Starting application with dev profile (H2 database)..."
    echo ""
    echo -e "${YELLOW}Note:${NC} Using H2 in-memory database. Data will be lost when application stops."
    echo ""

    # Try to run with offline mode first if dependencies are cached
    echo "Attempting to start with cached dependencies (offline mode)..."
    mvn spring-boot:run -Dspring-boot.run.profiles=dev -o 2>/dev/null

    # If offline mode fails, try online mode
    if [ $? -ne 0 ]; then
        echo ""
        echo -e "${YELLOW}Offline mode failed, trying online mode...${NC}"
        echo -e "${YELLOW}Note:${NC} If you see DNS errors, dependencies may need to be pre-downloaded."
        echo ""
        mvn spring-boot:run -Dspring-boot.run.profiles=dev
    fi
}

# Function to start with production profile (requires PostgreSQL/Redis)
start_app_prod() {
    echo ""
    echo -e "${YELLOW}Warning:${NC} Production mode requires PostgreSQL and Redis to be running!"
    echo ""

    # Create production properties if needed
    if [ ! -f "src/main/resources/application-prod.properties" ]; then
        echo -e "${YELLOW}Creating application-prod.properties from template...${NC}"
        cp src/main/resources/application-prod.properties.example src/main/resources/application-prod.properties
        echo -e "${GREEN}✓${NC} Created application-prod.properties"
        echo -e "${YELLOW}!${NC} Please configure database credentials in application-prod.properties"
        echo ""
        return 1
    fi

    mvn spring-boot:run -Dspring-boot.run.profiles=prod
}

# Function to clean build artifacts
clean_build() {
    echo ""
    echo "Cleaning build artifacts..."
    mvn clean
    echo -e "${GREEN}✓${NC} Cleaned successfully"
}

# Function to show application info
show_info() {
    echo ""
    echo "=========================================="
    echo "Application Information"
    echo "=========================================="
    echo ""
    echo "Development Mode (H2 Database):"
    echo "  - Application URL: http://localhost:8080"
    echo "  - H2 Console: http://localhost:8080/h2-console"
    echo "  - Swagger UI: http://localhost:8080/swagger-ui.html"
    echo "  - API Docs: http://localhost:8080/v3/api-docs"
    echo ""
    echo "H2 Database Connection:"
    echo "  - Driver: org.h2.Driver"
    echo "  - JDBC URL: jdbc:h2:mem:taskmanagement"
    echo "  - Username: sa"
    echo "  - Password: (leave empty)"
    echo ""
    echo "Default Users (after data initialization):"
    echo "  - Roles seeded: ROLE_USER, ROLE_ADMIN, ROLE_MANAGER"
    echo ""
}

# Main menu
case "$1" in
    setup)
        if check_java && check_maven; then
            setup_dev_env
        else
            exit 1
        fi
        ;;

    build)
        if check_java && check_maven; then
            build_app
        else
            exit 1
        fi
        ;;

    test)
        if check_java && check_maven; then
            run_tests
        else
            exit 1
        fi
        ;;

    start)
        if check_java && check_maven; then
            setup_dev_env
            start_app
        else
            exit 1
        fi
        ;;

    start-prod)
        if check_java && check_maven; then
            start_app_prod
        else
            exit 1
        fi
        ;;

    clean)
        if check_maven; then
            clean_build
        else
            exit 1
        fi
        ;;

    info)
        show_info
        ;;

    *)
        echo "Usage: $0 {setup|build|test|start|start-prod|clean|info}"
        echo ""
        echo "Commands:"
        echo "  setup      - Setup development environment (H2 database)"
        echo "  build      - Build the application (skip tests)"
        echo "  test       - Run all tests"
        echo "  start      - Start application in development mode (H2)"
        echo "  start-prod - Start application in production mode (PostgreSQL/Redis)"
        echo "  clean      - Clean build artifacts"
        echo "  info       - Show application information"
        echo ""
        echo "Quick Start:"
        echo "  1. ./setup.sh setup"
        echo "  2. ./setup.sh start"
        echo ""
        exit 1
        ;;
esac

exit 0
