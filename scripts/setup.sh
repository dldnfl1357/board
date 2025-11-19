#!/bin/bash

echo "======================================"
echo "Board Project Setup"
echo "======================================"
echo ""

# Java 버전 확인
echo "1. Checking Java version..."
if command -v java &> /dev/null; then
    java -version
    echo ""
else
    echo "ERROR: Java is not installed. Please install Java 21."
    exit 1
fi

# Docker 확인
echo "2. Checking Docker..."
if command -v docker &> /dev/null; then
    docker --version
    echo ""
else
    echo "ERROR: Docker is not installed."
    exit 1
fi

# Docker Compose 확인
echo "3. Checking Docker Compose..."
if command -v docker-compose &> /dev/null; then
    docker-compose --version
    echo ""
else
    echo "ERROR: Docker Compose is not installed."
    exit 1
fi

# 인프라 시작
echo "4. Starting infrastructure (PostgreSQL, Redis, Prometheus, Grafana)..."
docker-compose up -d
echo ""

# 헬스 체크 대기
echo "5. Waiting for services to be ready..."
sleep 10

# PostgreSQL 헬스 체크
echo "Checking PostgreSQL..."
docker-compose exec -T postgres pg_isready -U board
echo ""

# Redis 헬스 체크
echo "Checking Redis..."
docker-compose exec -T redis redis-cli ping
echo ""

echo "======================================"
echo "Setup Complete!"
echo "======================================"
echo ""
echo "Services running:"
echo "  - PostgreSQL: localhost:5432"
echo "  - Redis: localhost:6379"
echo "  - Prometheus: http://localhost:9090"
echo "  - Grafana: http://localhost:3000 (admin/admin)"
echo ""
echo "To start the application:"
echo "  ./gradlew bootRun"
echo ""
echo "To stop infrastructure:"
echo "  docker-compose down"
echo ""
