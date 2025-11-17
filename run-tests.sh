#!/bin/bash

# Task Management System - Test Runner Script
# This script runs different types of tests based on the argument provided

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    print_error "Maven is not installed. Please install Maven first."
    exit 1
fi

# Test type selection
TEST_TYPE=${1:-all}

case $TEST_TYPE in
    unit)
        print_info "Running Unit Tests..."
        mvn clean test -Dtest="*ServiceTest"
        ;;

    integration)
        print_info "Running Integration Tests..."
        mvn clean test -Dtest="*IntegrationTest"
        ;;

    repository)
        print_info "Running Repository Tests..."
        mvn clean test -Dtest="*RepositoryTest"
        ;;

    smoke)
        print_info "Running Smoke Tests..."
        mvn clean test -Dtest="SmokeTest"
        ;;

    coverage)
        print_info "Running Tests with Coverage..."
        mvn clean test jacoco:report
        print_info "Coverage report generated at: target/site/jacoco/index.html"
        ;;

    performance)
        print_info "Running Performance Tests..."
        if command -v jmeter &> /dev/null; then
            jmeter -n -t src/test/resources/jmeter/task-management-load-test.jmx \
                -l target/jmeter-results.jtl \
                -e -o target/jmeter-report
            print_info "JMeter report generated at: target/jmeter-report/index.html"
        else
            print_warning "JMeter is not installed. Skipping performance tests."
            print_info "Install JMeter from: https://jmeter.apache.org/download_jmeter.cgi"
        fi
        ;;

    all)
        print_info "Running All Tests..."
        mvn clean test
        print_info "All tests completed!"
        ;;

    quick)
        print_info "Running Quick Tests (Unit + Smoke)..."
        mvn clean test -Dtest="*ServiceTest,SmokeTest"
        ;;

    ci)
        print_info "Running CI Pipeline Tests..."
        mvn clean verify jacoco:report
        print_info "CI tests completed with coverage report"
        ;;

    help)
        echo "Usage: ./run-tests.sh [TEST_TYPE]"
        echo ""
        echo "Available test types:"
        echo "  unit         - Run only unit tests"
        echo "  integration  - Run only integration tests"
        echo "  repository   - Run only repository tests"
        echo "  smoke        - Run only smoke tests"
        echo "  coverage     - Run all tests with code coverage"
        echo "  performance  - Run performance tests (JMeter)"
        echo "  all          - Run all tests (default)"
        echo "  quick        - Run quick tests (unit + smoke)"
        echo "  ci           - Run CI pipeline tests"
        echo "  help         - Show this help message"
        echo ""
        echo "Examples:"
        echo "  ./run-tests.sh unit"
        echo "  ./run-tests.sh coverage"
        echo "  ./run-tests.sh all"
        ;;

    *)
        print_error "Unknown test type: $TEST_TYPE"
        print_info "Run './run-tests.sh help' for usage information"
        exit 1
        ;;
esac

# Exit with success
exit 0
