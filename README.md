# Board Project

최신 Java/Spring 기술 스택을 활용한 고성능 게시판 시스템

## 기술 스택

### Backend
- **Java 21 LTS** - Virtual Threads, Record, Pattern Matching 등 최신 기능 활용
- **Spring Boot 3.2.0** - 최신 Spring 생태계
- **Spring Data JPA** - 고급 ORM 기능 및 QueryDSL
- **Spring Security 6** - JWT 기반 인증/인가
- **MySQL 8.0** - 메인 데이터베이스
- **Redis 7** - 캐싱 및 세션 관리
- **Gradle 8** - 빌드 도구

### Monitoring & Performance
- **Spring Actuator** - 애플리케이션 메트릭
- **Prometheus** - 메트릭 수집
- **Grafana** - 모니터링 대시보드
- **Ehcache** - 로컬 캐시

### Testing
- **JUnit 5** - 테스트 프레임워크
- **Mockito** - Mock 프레임워크
- **Spring REST Docs** - API 문서 자동 생성
- **Asciidoctor** - 문서 빌드
- **AssertJ** - 유창한 assertion
- **Testcontainers** - 통합 테스트
- **H2** - 테스트용 인메모리 DB

## 주요 기능

- 사용자 인증/인가 (JWT)
- 역할 기반 접근 제어 (RBAC)
- 게시글 CRUD
- 계층형 댓글 시스템
- 좋아요/싫어요
- 카테고리 관리
- 조회수 추적
- 검색 및 필터링
- 페이징 및 정렬

## 프로젝트 구조

```
board/
├── docs/                      # 프로젝트 문서
│   ├── CLAUDE.md             # 프로젝트 개요
│   ├── 요구사항.md            # 요구사항 명세
│   ├── 기획.md                # 상세 기획
│   └── 아키텍처.md            # 아키텍처 설계
├── src/
│   ├── main/
│   │   ├── java/com/board/
│   │   │   ├── domain/       # 도메인 계층
│   │   │   │   ├── user/
│   │   │   │   ├── post/
│   │   │   │   ├── comment/
│   │   │   │   ├── category/
│   │   │   │   └── like/
│   │   │   ├── global/       # 공통 설정 및 유틸
│   │   │   │   ├── config/
│   │   │   │   ├── security/
│   │   │   │   ├── exception/
│   │   │   │   ├── common/
│   │   │   │   └── util/
│   │   │   └── api/          # API 계층
│   │   │       ├── controller/
│   │   │       ├── request/
│   │   │       └── response/
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-local.yml
│   │       ├── application-prod.yml
│   │       └── ehcache.xml
│   └── test/
├── monitoring/               # 모니터링 설정
│   └── prometheus.yml
├── build.gradle
├── settings.gradle
├── gradle.properties
└── docker-compose.yml
```

## 시작하기

### 사전 요구사항

- Java 21 이상
- Docker & Docker Compose
- Gradle 8 이상 (또는 Gradle Wrapper 사용)

### 1. 데이터베이스 및 인프라 실행

```bash
docker-compose up -d
```

실행되는 서비스:
- MySQL: `localhost:3306`
- Redis: `localhost:6379`
- Prometheus: `localhost:9090`
- Grafana: `localhost:3000` (admin/admin)

### 2. 애플리케이션 빌드

**Windows:**
```powershell
.\gradlew.bat clean build
```

**Linux/Mac:**
```bash
./gradlew clean build
```

**또는 IntelliJ IDEA:**
- Gradle 탭 → Tasks → build → build 더블클릭

### 3. 애플리케이션 실행

**Windows:**
```powershell
.\gradlew.bat bootRun
```

**Linux/Mac:**
```bash
./gradlew bootRun
```

**또는 JAR로 실행:**
```bash
java -jar build/libs/board-0.0.1-SNAPSHOT.jar
```

**또는 IntelliJ IDEA:**
- `BoardApplication.java` 우클릭 → Run

### 4. 애플리케이션 접속

- API: http://localhost:8080
- Actuator: http://localhost:8080/actuator
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000

## JVM 최적화

### 개발 환경

```bash
java -Xms512m -Xmx1g \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -jar build/libs/board-0.0.1-SNAPSHOT.jar
```

### 프로덕션 환경

```bash
java -Xms2g -Xmx4g \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath=/var/log/board/heapdump.hprof \
     -XX:+UseStringDeduplication \
     -XX:+OptimizeStringConcat \
     -Xlog:gc*:file=/var/log/board/gc.log:time,uptime:filecount=10,filesize=10M \
     -jar build/libs/board-0.0.1-SNAPSHOT.jar
```

## 환경 변수

### 로컬 개발

기본값이 `application-local.yml`에 설정되어 있습니다.

### 프로덕션

다음 환경 변수를 설정해야 합니다:

```bash
export DB_URL=jdbc:mysql://your-db-host:3306/board?useSSL=true&serverTimezone=Asia/Seoul
export DB_USERNAME=your-username
export DB_PASSWORD=your-password
export REDIS_HOST=your-redis-host
export REDIS_PORT=6379
export REDIS_PASSWORD=your-redis-password
export JWT_SECRET=your-secret-key-at-least-256-bits
```

## 테스트

### 테스트 실행

**Windows (PowerShell):**
```powershell
# 전체 테스트 실행
.\gradlew.bat test

# 또는 배치 파일 사용
.\test.bat

# Controller 테스트 (통합 테스트)
.\gradlew.bat test --tests "com.board.api.*"

# Service 테스트 (단위 테스트)
.\gradlew.bat test --tests "com.board.service.*"

# 특정 테스트 클래스
.\gradlew.bat test --tests "com.board.api.AuthControllerTest"
```

**Linux/Mac:**
```bash
# 전체 테스트 실행
./gradlew test

# 또는 스크립트 사용
./scripts/run-tests.sh

# Controller 테스트
./gradlew test --tests "com.board.api.*"
```

**IntelliJ IDEA (권장):**
- 테스트 클래스/메서드 우클릭 → Run Test
- `Ctrl + Shift + F10` (단축키)

### API 문서 생성

**Windows:**
```powershell
# 테스트 + 문서 생성
.\run-tests.bat

# 또는 문서만 생성
.\build-docs.bat

# 또는 Gradle 직접 실행
.\gradlew.bat clean test asciidoctor
```

**Linux/Mac:**
```bash
# 테스트 + 문서 생성
./scripts/run-tests.sh

# 또는
./gradlew clean test asciidoctor
```

생성된 문서 확인:
- **파일**: `build/docs/asciidoc/index.html`
- **웹**: http://localhost:8080/docs/index.html (애플리케이션 실행 후)

### 테스트 구조

- **AuthControllerTest** (7개 테스트)
  - 성공 케이스 3개 (회원가입, 로그인, 토큰 재발급)
  - 실패 케이스 4개 (Validation, 사용자 없음, 잘못된 비밀번호 등)
  - REST Docs 스니펫 자동 생성

- **AuthServiceTest** (13개 테스트)
  - 실패 케이스 10개 (이메일 중복, 닉네임 중복, DB 오류 등)
  - 성공 케이스 3개 (회원가입, 로그인, 토큰 재발급)
  - Mock 기반 단위 테스트

상세 가이드: [docs/테스트.md](docs/테스트.md)

## 모니터링

### Actuator Endpoints

- Health Check: `GET /actuator/health`
- Metrics: `GET /actuator/metrics`
- Prometheus: `GET /actuator/prometheus`

### Grafana 대시보드

1. http://localhost:3000 접속
2. admin/admin으로 로그인
3. Prometheus 데이터소스 추가
4. Spring Boot 대시보드 import

## 성능 최적화 포인트

### 1. JPA 최적화
- N+1 문제 해결 (Fetch Join, Entity Graph)
- Batch Fetch Size: 100
- Read-Only 쿼리 최적화
- 2차 캐시 활용 (Ehcache + Redis)

### 2. 동시성 처리
- Virtual Threads 활용
- 낙관적/비관적 락 전략
- Redis 분산 락

### 3. 캐싱 전략
- L1: Ehcache (로컬)
- L2: Redis (분산)
- Cache-Aside 패턴

### 4. JVM 튜닝
- G1GC 사용
- Heap 크기 최적화
- GC 로그 분석

## 기여하기

1. Fork the Project
2. Create your Feature Branch
3. Commit your Changes
4. Push to the Branch
5. Open a Pull Request

## 라이선스

이 프로젝트는 개인 포트폴리오 목적으로 제작되었습니다.

## 문서

자세한 내용은 `docs/` 디렉토리를 참고하세요.

- [프로젝트 개요](docs/CLAUDE.md)
- [요구사항 명세](docs/요구사항.md)
- [기획 문서](docs/기획.md)
- [아키텍처 설계](docs/아키텍처.md)
