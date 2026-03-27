# REST API 문서

- Base URL: `http://localhost:8080`
- 인증: `Authorization: Bearer {token}` 헤더 필요 (로그인 제외)
- Content-Type: `application/json`

---

## 로그인

```
POST /auth/login
```

**요청**
```json
{
    "username": "admin",
    "password": "admin"
}
```

**응답 (200)**
```json
{
    "token": "eyJhbGciOiJIUzM4NCJ9..."
}
```

---

## 콘텐츠 생성

```
POST /contents
Authorization: Bearer {token}
```

**요청**
```json
{
    "title": "콘텐츠 제목",
    "description": "콘텐츠 내용"
}
```

**응답 (201)**
```json
{
    "id": 1,
    "title": "콘텐츠 제목",
    "description": "콘텐츠 내용",
    "viewCount": 0,
    "createdBy": "admin",
    "lastModifiedBy": "admin",
    "createdDate": "2026-03-27T10:00:00",
    "lastModifiedDate": "2026-03-27T10:00:00"
}
```

---

## 콘텐츠 목록 조회

```
GET /contents?page=0&size=10
Authorization: Bearer {token}
```

**응답 (200)** — Spring Data Page 형식 (`content`, `totalElements`, `totalPages` 등 포함)

---

## 콘텐츠 상세 조회

```
GET /contents/{id}
Authorization: Bearer {token}
```

조회 시 `viewCount`가 1 증가합니다.

---

## 콘텐츠 수정

```
PUT /contents/{id}
Authorization: Bearer {token}
```

작성자 본인 또는 ADMIN만 가능합니다.

**요청**
```json
{
    "title": "수정된 제목",
    "description": "수정된 내용"
}
```

---

## 콘텐츠 삭제

```
DELETE /contents/{id}
Authorization: Bearer {token}
```

작성자 본인 또는 ADMIN만 가능합니다.

**응답 (204)** No Content

---

## 에러 응답

```json
{
    "code": "CONTENT_NOT_FOUND",
    "message": "콘텐츠를 찾을 수 없습니다."
}
```

- `INVALID_INPUT` (400) — 입력값 검증 실패
- `AUTHENTICATION_FAILED` (401) — 인증 실패
- `ACCESS_DENIED` (403) — 접근 권한 없음
- `CONTENT_NOT_FOUND` (404) — 콘텐츠 없음
- `INTERNAL_SERVER_ERROR` (500) — 서버 내부 오류
