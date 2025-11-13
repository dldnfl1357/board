# Board Project

## 프로젝트 개요

최신 Java/Spring 기술 스택을 활용한 고성능 게시판 시스템입니다.
단순한 CRUD 구현을 넘어, JVM 최적화, 고급 JPA 기법, 현대적인 Spring 생태계 기술을 종합적으로 활용하여
엔터프라이즈급 애플리케이션 개발 역량을 보여주는 것을 목표로 합니다.

## 핵심 어필 포인트

### 1. Java 최신 트렌드 반영
- **Java 21 LTS** 활용
  - Virtual Threads (Project Loom) 적용으로 높은 동시성 처리
  - Record 타입을 활용한 불변 DTO 설계
  - Pattern Matching과 Sealed Classes로 타입 안정성 강화
  - Text Blocks, Switch Expressions 등 최신 문법 활용

### 2. JVM 깊이 있는 이해 및 최적화
- **메모리 최적화**
  - G1GC 튜닝 전략 수립 및 적용
  - Heap Dump 분석을 통한 메모리 누수 방지
  - JVM 파라미터 최적화 (Xms, Xmx, MetaSpace 등)

- **성능 모니터링**
  - JMX를 통한 런타임 메트릭 수집
  - JFR (Java Flight Recorder) 기반 프로파일링
  - GC 로그 분석 및 튜닝

- **클래스로딩 최적화**
  - Lazy Loading 전략 적용
  - 불필요한 리플렉션 최소화

### 3. 고급 JPA 기술 활용
- **쿼리 최적화**
  - N+1 문제 해결 (Fetch Join, Entity Graph, Batch Size)
  - QueryDSL을 활용한 타입 안전 동적 쿼리
  - Projection 기법 (DTO Projection, Interface-based Projection)
  - Native Query와 JPQL 적절한 혼용

- **성능 향상 기법**
  - 2차 캐시 전략 (Ehcache, Redis)
  - Read-Only 쿼리 최적화
  - Batch Insert/Update 처리
  - Persistence Context 관리 및 최적화

- **고급 매핑 기법**
  - 복잡한 연관관계 매핑 (계층형 댓글 구조)
  - @Embedded/@Embeddable 활용
  - 상속 전략 (JOINED, SINGLE_TABLE 비교 적용)
  - Converter를 활용한 커스텀 타입 매핑

### 4. 다양한 Spring 기술 스택
- **Spring Boot 3.x**
  - Auto Configuration 이해 및 커스터마이징
  - Actuator를 통한 프로덕션 레디 기능
  - Profile 기반 환경별 설정 관리

- **Spring Security 6**
  - JWT 기반 Stateless 인증
  - Method Security로 세밀한 권한 제어
  - Custom Filter Chain 구성
  - CORS, CSRF 보안 설정

- **Spring Data**
  - Spring Data JPA의 고급 기능
  - Custom Repository 구현
  - Specification 패턴으로 동적 검색
  - Auditing 기능 (@CreatedDate, @LastModifiedDate)

- **Spring AOP**
  - 로깅, 성능 측정 Aspect
  - 트랜잭션 관리 최적화
  - 캐싱 Aspect 구현

- **Spring Cache**
  - 다단계 캐싱 전략 (Local + Redis)
  - Cache Eviction 전략
  - @Cacheable, @CacheEvict 활용

- **Spring Validation**
  - Custom Validator 구현
  - Bean Validation 2.0
  - 계층별 검증 전략

## 주요 기능

- 회원 관리 (회원가입, 로그인, 권한 관리)
- 역할 기반 접근 제어 (관리자/일반 사용자)
- 카테고리 관리 (관리자 전용)
- 게시글 CRUD
- 계층형 댓글 시스템 (대댓글 지원)
- 좋아요/싫어요 기능
- 조회수 추적
- 검색 및 필터링
- 페이징 및 정렬

## 기술적 도전 과제

1. **높은 동시성 처리**
   - 좋아요/조회수 동시성 제어 (낙관적/비관적 락)
   - Virtual Threads 활용

2. **계층형 데이터 처리**
   - 댓글의 계층 구조 효율적 조회
   - Closure Table 또는 Nested Set 패턴 검토

3. **캐싱 전략**
   - 읽기 비중이 높은 게시판 특성 활용
   - 캐시 일관성 보장

4. **쿼리 최적화**
   - 복잡한 조인 쿼리 최적화
   - 인덱스 전략 수립

5. **확장 가능한 아키텍처**
   - 도메인 주도 설계(DDD) 원칙 적용
   - Clean Architecture 레이어링
   - SOLID 원칙 준수

## 프로젝트 구조

```
board/
├── docs/               # 문서
│   ├── claude.md       # 프로젝트 개요
│   ├── 요구사항.md     # 요구사항 명세
│   ├── 기획.md         # 상세 기획
│   └── 아키텍처.md     # 아키텍처 설계
└── (향후 구현 예정)
```

## 학습 및 성장 목표

이 프로젝트를 통해 다음을 달성합니다:

- Java 생태계의 최신 기술 트렌드 습득
- JVM 내부 동작 원리에 대한 깊은 이해
- 고성능 애플리케이션 설계 및 최적화 경험
- 엔터프라이즈급 Spring 애플리케이션 개발 역량
- 실무에서 마주할 수 있는 기술적 챌린지 해결 능력

## 참고 자료

- [Java 21 Documentation](https://docs.oracle.com/en/java/javase/21/)
- [Spring Boot 3.x Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [JPA Best Practices](https://vladmihalcea.com/tutorials/hibernate/)
- [JVM Performance Tuning](https://docs.oracle.com/en/java/javase/21/gctuning/)
