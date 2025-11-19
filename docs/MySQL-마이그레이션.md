# PostgreSQL → MySQL 마이그레이션

## 변경 사항 요약

PostgreSQL에서 MySQL 8.0으로 데이터베이스를 변경했습니다.

## 변경된 파일

### 1. Docker Compose (docker-compose.yml)

**변경 전:**
```yaml
postgres:
  image: postgres:16-alpine
  ports:
    - "5432:5432"
```

**변경 후:**
```yaml
mysql:
  image: mysql:8.0
  ports:
    - "3306:3306"
  environment:
    MYSQL_DATABASE: board
    MYSQL_USER: board
    MYSQL_PASSWORD: board123
    MYSQL_ROOT_PASSWORD: root123
  command:
    - --character-set-server=utf8mb4
    - --collation-server=utf8mb4_unicode_ci
```

### 2. Gradle 의존성 (build.gradle)

**변경 전:**
```gradle
runtimeOnly 'org.postgresql:postgresql'
```

**변경 후:**
```gradle
runtimeOnly 'com.mysql:mysql-connector-j'
```

### 3. 로컬 환경 설정 (application-local.yml)

**변경 전:**
```yaml
datasource:
  url: jdbc:postgresql://localhost:5432/board
  driver-class-name: org.postgresql.Driver
jpa:
  properties:
    hibernate:
      dialect: org.hibernate.dialect.PostgreSQLDialect
```

**변경 후:**
```yaml
datasource:
  url: jdbc:mysql://localhost:3306/board?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
  driver-class-name: com.mysql.cj.jdbc.Driver
jpa:
  properties:
    hibernate:
      dialect: org.hibernate.dialect.MySQLDialect
```

### 4. 프로덕션 환경 설정 (application-prod.yml)

**변경 전:**
```yaml
driver-class-name: org.postgresql.Driver
dialect: org.hibernate.dialect.PostgreSQLDialect
```

**변경 후:**
```yaml
driver-class-name: com.mysql.cj.jdbc.Driver
dialect: org.hibernate.dialect.MySQLDialect
```

### 5. 테스트 설정 (application-test.yml)

**변경 전:**
```yaml
url: jdbc:h2:mem:testdb;MODE=PostgreSQL
```

**변경 후:**
```yaml
url: jdbc:h2:mem:testdb;MODE=MySQL
```

### 6. 환경 변수 예시 (.env.example)

**변경 전:**
```
DB_URL=jdbc:postgresql://localhost:5432/board
```

**변경 후:**
```
DB_URL=jdbc:mysql://localhost:3306/board?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Seoul&characterEncoding=UTF-8
```

### 7. 신규 파일

- **init-db.sql** - MySQL 초기화 스크립트 (타임존 설정)

## MySQL 설정 상세

### 문자 인코딩

- **Character Set**: UTF-8 (utf8mb4)
- **Collation**: utf8mb4_unicode_ci
- 이모지 및 다국어 완벽 지원

### 타임존

- **서버 타임존**: Asia/Seoul (UTC+9)
- **JDBC URL 파라미터**: serverTimezone=Asia/Seoul

### 인증 방식

- **Authentication Plugin**: mysql_native_password
- Spring Boot와 호환성을 위한 설정

## 연결 정보

### 로컬 개발

```
Host: localhost
Port: 3306
Database: board
Username: board
Password: board123
Root Password: root123
```

### Docker Container

```bash
# MySQL 컨테이너 접속
docker exec -it board-mysql mysql -u board -pboard123 board

# 또는 root로 접속
docker exec -it board-mysql mysql -u root -proot123

# 데이터베이스 확인
SHOW DATABASES;
USE board;
SHOW TABLES;
```

## JDBC URL 파라미터 설명

```
jdbc:mysql://localhost:3306/board
  ?useSSL=false                    # 로컬에서 SSL 비활성화
  &allowPublicKeyRetrieval=true    # 공개 키 검색 허용
  &serverTimezone=Asia/Seoul       # 서버 타임존 설정
  &characterEncoding=UTF-8         # 문자 인코딩
```

**프로덕션 환경:**
```
jdbc:mysql://your-host:3306/board
  ?useSSL=true                     # SSL 활성화
  &serverTimezone=Asia/Seoul
  &characterEncoding=UTF-8
```

## 마이그레이션 체크리스트

- [x] Docker Compose 설정 변경
- [x] Gradle 의존성 변경 (MySQL Connector)
- [x] application-local.yml 변경
- [x] application-prod.yml 변경
- [x] application-test.yml 변경 (H2 MySQL 모드)
- [x] .env.example 업데이트
- [x] init-db.sql 생성
- [x] README.md 업데이트

## 실행 방법

### 1. 기존 PostgreSQL 컨테이너 정리 (필요시)

```bash
# 컨테이너 중지 및 삭제
docker-compose down

# 볼륨까지 삭제 (데이터 초기화)
docker-compose down -v
```

### 2. MySQL 컨테이너 시작

```bash
docker-compose up -d mysql
```

### 3. MySQL 연결 확인

```bash
# 헬스 체크
docker-compose ps

# 로그 확인
docker-compose logs mysql

# 직접 접속
docker exec -it board-mysql mysql -u board -pboard123 board
```

### 4. 애플리케이션 실행

```bash
# Windows
.\gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

## 데이터 타입 차이점

### PostgreSQL vs MySQL

| 기능 | PostgreSQL | MySQL |
|------|-----------|-------|
| 자동 증가 | SERIAL | AUTO_INCREMENT |
| Boolean | BOOLEAN | TINYINT(1) |
| Text | TEXT | TEXT, LONGTEXT |
| JSON | JSONB | JSON |
| 배열 | ARRAY | JSON (배열 저장) |

JPA를 사용하므로 대부분 자동 매핑되지만, 네이티브 쿼리 사용 시 주의 필요

## 성능 최적화

### Connection Pool (HikariCP)

```yaml
hikari:
  maximum-pool-size: 10      # 로컬
  maximum-pool-size: 20      # 프로덕션
  minimum-idle: 5
  connection-timeout: 30000
```

### JPA 설정

```yaml
jpa:
  properties:
    hibernate:
      dialect: org.hibernate.dialect.MySQLDialect
      format_sql: true
      default_batch_fetch_size: 100
```

## 문제 해결

### "Public Key Retrieval is not allowed"

**원인:** MySQL 8.0의 보안 정책

**해결:** JDBC URL에 `allowPublicKeyRetrieval=true` 추가

### "The server time zone value is unrecognized"

**원인:** 타임존 설정 누락

**해결:** JDBC URL에 `serverTimezone=Asia/Seoul` 추가

### 한글 깨짐

**원인:** 문자 인코딩 설정 누락

**해결:**
1. JDBC URL에 `characterEncoding=UTF-8` 추가
2. MySQL 서버 설정: `--character-set-server=utf8mb4`

### 컨테이너 시작 실패

```bash
# 포트 충돌 확인
netstat -ano | findstr :3306

# 기존 MySQL 프로세스 종료 또는 포트 변경
```

## 백업 및 복원

### 데이터 백업

```bash
# MySQL 덤프
docker exec board-mysql mysqldump -u board -pboard123 board > backup.sql

# 또는 모든 데이터베이스
docker exec board-mysql mysqldump -u root -proot123 --all-databases > backup-all.sql
```

### 데이터 복원

```bash
# MySQL 복원
docker exec -i board-mysql mysql -u board -pboard123 board < backup.sql
```

## 참고 자료

- [MySQL 8.0 Documentation](https://dev.mysql.com/doc/refman/8.0/en/)
- [Hibernate MySQL Dialect](https://docs.jboss.org/hibernate/orm/current/javadocs/org/hibernate/dialect/MySQLDialect.html)
- [Spring Boot with MySQL](https://spring.io/guides/gs/accessing-data-mysql/)
