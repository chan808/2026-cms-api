# CMS REST API

2026 신입 백엔드 개발자 코딩 과제 - 콘텐츠 관리 시스템 REST API

## 기술 스택

- Java 25
- Spring Boot 4.0.3
- Spring Security + JWT
- Spring Data JPA
- H2 (in-memory)
- Lombok

## 실행 방법

```bash
./gradlew bootRun
```

서버 실행 후 `http://localhost:8080`으로 접근 가능합니다.

H2 콘솔: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:test`, username: `sa`)

## 테스트 실행

```bash
./gradlew test
```

## 인증 방식

JWT(JSON Web Token) 기반 인증을 사용합니다.

- `POST /auth/login`으로 로그인하면 JWT 토큰을 발급받습니다.
- 이후 모든 요청에 `Authorization: Bearer {token}` 헤더를 포함해야 합니다.
- Access Token만 사용하며, 토큰 만료 시 재로그인합니다.

## 테스트 계정

| username | password | role  |
|----------|----------|-------|
| admin    | admin    | ADMIN |
| user     | user     | USER  |

## 구현 내용

- **콘텐츠 CRUD** — 생성, 목록 조회(페이징), 상세 조회(조회수 증가), 수정, 삭제
- **로그인** — Spring Security + JWT 기반 인증
- **접근 권한** — 작성자 본인만 수정/삭제 가능, ADMIN은 전체 수정/삭제 가능
- **예외 처리** — GlobalExceptionHandler를 통한 일관된 에러 응답
- **입력값 검증** — `@Valid` + Bean Validation 활용
- **JPA Auditing** — `@CreatedBy`, `@LastModifiedBy`로 생성자/수정자 자동 주입
- **통합 테스트** — 인증, CRUD, 권한 체크 전체 흐름 테스트

## 접근 권한

| 기능 | 권한 |
|------|------|
| 로그인 | 전체 |
| 콘텐츠 생성 | 인증된 사용자 |
| 콘텐츠 조회 | 인증된 사용자 |
| 콘텐츠 수정 | 작성자 본인 또는 ADMIN |
| 콘텐츠 삭제 | 작성자 본인 또는 ADMIN |

## 패키지 구조

```
com.malgn/
├── auth/               # 인증 (Controller, DTO)
├── member/             # 회원 (Entity, Repository)
├── content/            # 콘텐츠 (Controller, Service, Entity, Repository, DTO)
├── common/
│   ├── exception/      # 예외처리 (GlobalExceptionHandler, ErrorCode)
│   └── security/       # JWT (JwtProvider, JwtAuthenticationFilter)
└── configure/          # 설정 (Security, JPA Auditing)
```

## API 문서

- API 명세: [docs/API.md](docs/API.md)
- Postman Collection: [docs/CMS-API.postman_collection.json](docs/CMS-API.postman_collection.json)

## 사용 도구

- Claude Code (AI 코딩 어시스턴트) - 설계, 코드 구현 보조
