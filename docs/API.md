# Board API 문서

## 인증 (Authentication)

### 1. 회원가입

**Endpoint:** `POST /api/auth/signup`

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "Password123!",
  "nickname": "사용자닉네임"
}
```

**Validation Rules:**
- email: 필수, 이메일 형식
- password: 필수, 8-20자, 영문/숫자/특수문자 포함
- nickname: 필수, 2-20자, 한글/영문/숫자/언더스코어/하이픈만 허용

**Response (201 Created):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "user@example.com",
    "nickname": "사용자닉네임",
    "role": "USER",
    "createdAt": "2024-01-01T10:00:00"
  },
  "message": "회원가입이 완료되었습니다.",
  "timestamp": "2024-01-01T10:00:00"
}
```

**Error Response (400 Bad Request):**
```json
{
  "code": "U002",
  "message": "Email already exists",
  "status": 409,
  "timestamp": "2024-01-01T10:00:00",
  "errors": []
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "Password123!",
    "nickname": "사용자닉네임"
  }'
```

---

### 2. 로그인

**Endpoint:** `POST /api/auth/login`

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "Password123!"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400000
  },
  "message": "로그인 성공",
  "timestamp": "2024-01-01T10:00:00"
}
```

**Error Response (404 Not Found):**
```json
{
  "code": "U001",
  "message": "User not found",
  "status": 404,
  "timestamp": "2024-01-01T10:00:00",
  "errors": []
}
```

**Error Response (400 Bad Request - Invalid Password):**
```json
{
  "code": "U003",
  "message": "Invalid password",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00",
  "errors": []
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "Password123!"
  }'
```

---

### 3. 토큰 재발급

**Endpoint:** `POST /api/auth/refresh`

**Request Headers:**
```
Refresh-Token: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response (200 OK):**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400000
  },
  "message": "토큰이 재발급되었습니다.",
  "timestamp": "2024-01-01T10:00:00"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Refresh-Token: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## 인증된 요청 방법

로그인 후 받은 `accessToken`을 다음과 같이 사용합니다:

**Header:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**cURL Example:**
```bash
curl -X GET http://localhost:8080/api/posts \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## 에러 코드

### 공통 에러

| 코드 | 메시지 | HTTP Status | 설명 |
|------|--------|-------------|------|
| C001 | Internal server error | 500 | 서버 내부 오류 |
| C002 | Invalid input value | 400 | 잘못된 입력 값 |
| C003 | Method not allowed | 405 | 허용되지 않은 HTTP 메서드 |
| C004 | Entity not found | 404 | 엔티티를 찾을 수 없음 |
| C005 | Invalid type value | 400 | 잘못된 타입 값 |
| C006 | Access denied | 403 | 접근 거부 |

### 사용자 에러

| 코드 | 메시지 | HTTP Status | 설명 |
|------|--------|-------------|------|
| U001 | User not found | 404 | 사용자를 찾을 수 없음 |
| U002 | Email already exists | 409 | 이미 존재하는 이메일 |
| U003 | Invalid password | 400 | 잘못된 비밀번호 |
| U004 | Unauthorized | 401 | 인증되지 않음 |

---

## Postman Collection

### 환경 변수 설정

```json
{
  "baseUrl": "http://localhost:8080",
  "accessToken": "",
  "refreshToken": ""
}
```

### 1. 회원가입 요청

```
POST {{baseUrl}}/api/auth/signup
Content-Type: application/json

{
  "email": "test@example.com",
  "password": "Test1234!",
  "nickname": "테스터"
}
```

### 2. 로그인 요청

```
POST {{baseUrl}}/api/auth/login
Content-Type: application/json

{
  "email": "test@example.com",
  "password": "Test1234!"
}
```

**Tests (토큰 자동 저장):**
```javascript
var jsonData = pm.response.json();
pm.environment.set("accessToken", jsonData.data.accessToken);
pm.environment.set("refreshToken", jsonData.data.refreshToken);
```

### 3. 인증된 요청 예시

```
GET {{baseUrl}}/api/posts
Authorization: Bearer {{accessToken}}
```

---

## 테스트 시나리오

### 1. 정상 플로우

1. 회원가입
   ```bash
   POST /api/auth/signup
   ```

2. 로그인
   ```bash
   POST /api/auth/login
   ```

3. 토큰으로 인증된 요청
   ```bash
   GET /api/posts
   Header: Authorization: Bearer {accessToken}
   ```

4. 토큰 만료 전 재발급
   ```bash
   POST /api/auth/refresh
   Header: Refresh-Token: {refreshToken}
   ```

### 2. 에러 케이스

1. 중복 이메일로 회원가입
   - Expected: 409 Conflict

2. 존재하지 않는 이메일로 로그인
   - Expected: 404 Not Found

3. 잘못된 비밀번호로 로그인
   - Expected: 400 Bad Request

4. 유효하지 않은 토큰으로 요청
   - Expected: 401 Unauthorized

5. 토큰 없이 인증 필요 API 호출
   - Expected: 403 Forbidden

---

## Validation 규칙

### 회원가입

**이메일:**
- 필수 입력
- 이메일 형식 검증
- 중복 검증

**비밀번호:**
- 필수 입력
- 8~20자
- 영문 대소문자 포함
- 숫자 포함
- 특수문자 포함 (@$!%*#?&)

**닉네임:**
- 필수 입력
- 2~20자
- 한글, 영문, 숫자, 언더스코어(_), 하이픈(-) 허용
- 중복 검증

### Validation Error Response 예시

```json
{
  "code": "C002",
  "message": "Invalid input value",
  "status": 400,
  "timestamp": "2024-01-01T10:00:00",
  "errors": [
    {
      "field": "email",
      "value": "invalid-email",
      "reason": "올바른 이메일 형식이 아닙니다."
    },
    {
      "field": "password",
      "value": "123",
      "reason": "비밀번호는 8~20자 사이여야 합니다."
    }
  ]
}
```
