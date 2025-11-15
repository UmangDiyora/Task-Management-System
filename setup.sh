#!/bin/bash

echo "=========================================="
echo "Task Management System - Setup Script"
echo "=========================================="
echo ""

# Function to check if docker-compose is available
check_docker_compose() {
    if command -v docker-compose &> /dev/null; then
        echo "✓ docker-compose is installed"
        return 0
    else
        echo "✗ docker-compose is not installed"
        echo "  Please install Docker and docker-compose first"
        return 1
    fi
}

# Start services
start_services() {
    echo "Starting PostgreSQL and Redis services..."
    docker-compose up -d postgres redis
    echo ""
    echo "Waiting for services to be ready..."
    sleep 5
    echo ""
    echo "✓ Services started successfully!"
    echo ""
    echo "PostgreSQL:"
    echo "  Host: localhost"
    echo "  Port: 5432"
    echo "  Database: taskmanagement"
    echo "  Username: postgres"
    echo "  Password: postgres"
    echo ""
    echo "Redis:"
    echo "  Host: localhost"
    echo "  Port: 6379"
    echo ""
}

# Stop services
stop_services() {
    echo "Stopping services..."
    docker-compose down
    echo "✓ Services stopped"
}

# Show services status
show_status() {
    echo "Services status:"
    docker-compose ps
}

# Show logs
show_logs() {
    docker-compose logs -f
}

# Main menu
case "$1" in
    start)
        if check_docker_compose; then
            start_services
        fi
        ;;
    stop)
        stop_services
        ;;
    status)
        show_status
        ;;
    logs)
        show_logs
        ;;
    restart)
        stop_services
        if check_docker_compose; then
            start_services
        fi
        ;;
    *)
        echo "Usage: $0 {start|stop|status|logs|restart}"
        echo ""
        echo "Commands:"
        echo "  start   - Start PostgreSQL and Redis services"
        echo "  stop    - Stop all services"
        echo "  status  - Show services status"
        echo "  logs    - Show services logs"
        echo "  restart - Restart all services"
        exit 1
        ;;
esac

exit 0
