#!/bin/bash

BASE_URL="http://localhost:8080"

echo "======================================"
echo "Board API 테스트"
echo "======================================"
echo ""

# 1. 헬스 체크
echo "1. 헬스 체크..."
curl -s -X GET "$BASE_URL/api/health" | jq '.'
echo ""

# 2. 회원가입
echo "2. 회원가입..."
SIGNUP_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/signup" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test1234!",
    "nickname": "테스터"
  }')
echo "$SIGNUP_RESPONSE" | jq '.'
echo ""

# 3. 로그인
echo "3. 로그인..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test1234!"
  }')
echo "$LOGIN_RESPONSE" | jq '.'
echo ""

# 토큰 추출
ACCESS_TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.data.accessToken')
REFRESH_TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.data.refreshToken')

echo "Access Token: ${ACCESS_TOKEN:0:50}..."
echo "Refresh Token: ${REFRESH_TOKEN:0:50}..."
echo ""

# 4. 토큰 재발급
echo "4. 토큰 재발급..."
curl -s -X POST "$BASE_URL/api/auth/refresh" \
  -H "Refresh-Token: $REFRESH_TOKEN" | jq '.'
echo ""

# 5. 중복 이메일 회원가입 (에러 테스트)
echo "5. 중복 이메일 회원가입 시도 (에러 예상)..."
curl -s -X POST "$BASE_URL/api/auth/signup" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test1234!",
    "nickname": "테스터2"
  }' | jq '.'
echo ""

# 6. 잘못된 비밀번호 로그인 (에러 테스트)
echo "6. 잘못된 비밀번호 로그인 시도 (에러 예상)..."
curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "WrongPassword123!"
  }' | jq '.'
echo ""

echo "======================================"
echo "테스트 완료!"
echo "======================================"
