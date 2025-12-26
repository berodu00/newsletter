# TechSpec.md - 고려아연 전자사보 시스템 기술 명세서

## 📌 문서 성격 및 준수 사항

**이 문서는 참고 자료가 아니라 "구속력 있는 계약서"입니다.**

### 절대 기준
- 모든 구현은 반드시 본 문서와 Plan.md를 100% 준수해야 한다
- 요구사항 충돌, 모호함, 누락 발견 시 즉시 작업 중단 후 보고
- 모든 산출물은 한국어로 작성

### 범위 통제
- 본 문서에 명시되지 않은 API, 컴포넌트, 필드, 테이블, 로직 생성 금지
- 정의되지 않은 Request/Response 필드 추가 금지
- 명시되지 않은 라이브러리, 프레임워크, 패턴 도입 금지
- 명시적 지시 없는 개선, 리팩토링, 최적화, 확장 금지

---

## 1. 프로젝트 개요

### 1.1 기본 정보
- **프로젝트명**: 고려아연 전자사보 고도화 시스템
- **버전**: v1.6
- **작성일**: 2025-12-26 (Phase 5.3 Backend Integration Tests Complete)
- **DBMS**: PostgreSQL 15+ (Port: 5433)
- **마이그레이션**: Flyway (필수)

### 1.2 목표
웹 기반 반응형 SPA로 전환하여 사보 열람 + SNS 컨텐츠 + 임직원 참여를 통합한 커뮤니티 플랫폼 구축

### 1.3 범위
**구축 대상**: 전자사보 시스템만 (웹진, 소셜, 이벤트, 아이디어, 관리자, 대시보드)  
**제외 대상**: 동호회 관리, 특허 관리, 공지 관리 시스템

---

## 2. 기술 스택

### 2.1 Backend
| 기술 | 버전 | 용도 |
|------|------|------|
| Java | 17+ | 프로그래밍 언어 |
| Spring Boot | 3.2+ | RESTful API 서버 |
| Gradle | 8.x | 빌드 도구 |
| Spring Security | 6.x | OAuth 2.0 인증 |
| Spring Data JPA | 3.x | ORM |
| Flyway | 10+ | DB 마이그레이션 (필수) |
| PostgreSQL Driver | 42.x | JDBC 드라이버 |
| OWASP Java HTML Sanitizer | 최신 | HTML Sanitization |

### 2.2 Frontend
| 기술 | 버전 | 용도 |
|------|------|------|
| React | 18+ | SPA 프레임워크 |
| React Router | 6+ | 클라이언트 라우팅 |
| Axios | 1.x | HTTP 클라이언트 |
| Bootstrap | 5.3+ | 반응형 UI |
| React-Bootstrap | 2.x | React용 Bootstrap |
| Vite | 5+ | 빌드 도구 |
| React-Quill | 2.x | 리치 텍스트 에디터 |
| Recharts | 2.x | 차트 라이브러리 |

### 2.3 Database
- **DBMS**: PostgreSQL 15+ (Port: 5433)
- **마이그레이션**: Flyway
- **Extension**: (필요시) pg_trgm (Full-Text Search)

### 2.4 External API
- YouTube Data API v3
- Instagram Graph API
- Naver Analytics (또는 Google Analytics)

---

## 3. 시스템 아키텍처

### 3.1 전체 구조
```
[React SPA (Vite)]
       ↓ (AJAX/REST)
[Spring Boot API Server]
       ↓
[PostgreSQL Database]
```

### 3.2 통신 방식
- **프로토콜**: HTTPS (운영), HTTP (개발)
- **API 형식**: RESTful JSON
- **인증**: OAuth 2.0 (OIDC) + JWT Bearer Token
- **CORS**: 개발(localhost:5173 허용), 운영(동일 도메인)

### 3.3 프로젝트 구조
```
kz-magazine-platform/
├── backend/
│   ├── src/main/java/com/kz/magazine/
│   │   ├── config/
│   │   │   ├── SecurityConfig.java
│   │   │   ├── FlywayConfig.java
│   │   │   ├── CorsConfig.java
│   │   │   └── JpaConfig.java
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   ├── dto/
│   │   ├── security/
│   │   ├── exception/
│   │   └── util/
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── db/migration/
│   │       ├── V1__create_initial_schema.sql
│   │       ├── V2__add_content_search.sql
│   │       ├── V3__add_dedup_and_audit.sql
│   │       ├── V4__add_reactions.sql
│   │       ├── V5__add_sns_columns.sql
│   │       ├── V6__add_updated_by_to_events.sql
│   │       ├── V8__add_daily_visit_log.sql
│   │       ├── V9__update_visitor_log_schema.sql
│   │       ├── V10__create_dept_stats.sql
│   │       ├── V11__add_user_deleted_at.sql
│   │       ├── V12__add_test_data.sql
│   │       ├── V13__fix_reactions_schema.sql
│   │       └── V14__delete_invalid_users.sql
│   └── build.gradle
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   │   ├── common/
│   │   │   ├── magazine/
│   │   │   ├── social/
│   │   │   ├── event/
│   │   │   └── admin/
│   │   ├── pages/
│   │   ├── contexts/
│   │   ├── services/
│   │   ├── utils/
│   │   ├── App.jsx
│   │   └── main.jsx
│   ├── package.json
│   └── vite.config.js
└── uploads/
    └── YYYY/MM/
```

---

## 4. 데이터베이스 운영 원칙

### 4.1 단일 진실 원천
- 컨텐츠/이벤트/팝업/아이디어/해시태그/반응/별점은 DB가 공식 수치
- 트래픽 분석은 Analytics 위임 가능

### 4.2 Soft Delete 원칙
**대상**: `contents`, `events`, `ideas`, `resource_files`, `social_contents`, `users`  
**컬럼**: `deleted_at TIMESTAMP`, `deleted_by BIGINT`

### 4.3 감사 추적
- 모든 테이블: `created_at`, `created_by`, `updated_at`, `updated_by`
- 모든 관리자 CRUD: `audit_logs` 기록 (append-only)

### 4.4 정합성 보장
- 조회수: `content_views_dedup` 테이블 (30분 TTL)
- 반응/별점: UNIQUE 제약 + Upsert
- 해시태그: 트랜잭션 증감 + 야간 재집계

### 4.5 Flyway 마이그레이션 의무
- DDL 직접 수정 금지
- 모든 스키마 변경은 마이그레이션으로만 수행
- 파일명 형식: `V{version}__{description}.sql`

---

## 5. 데이터베이스 스키마

### 5.1 users (사용자)
```sql
CREATE TABLE users (
  user_id        BIGSERIAL PRIMARY KEY,
  username       VARCHAR(100) NOT NULL UNIQUE,
  name           VARCHAR(100) NOT NULL,
  email          VARCHAR(255),
  department     VARCHAR(100),
  role           VARCHAR(20) NOT NULL DEFAULT 'USER',
  is_active      BOOLEAN NOT NULL DEFAULT TRUE,
  created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by     BIGINT REFERENCES users(user_id),
  updated_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by     BIGINT REFERENCES users(user_id),
  last_login_at  TIMESTAMP,
  deleted_at     TIMESTAMP,
  deleted_by     BIGINT REFERENCES users(user_id)
);
```

**제약조건**:
- `role`: 'USER', 'ADMIN' 중 하나

**인덱스**:
- `idx_users_username` ON `username`
- `idx_users_role` ON `role` WHERE `deleted_at IS NULL`

### 5.2 categories (카테고리)
```sql
CREATE TABLE categories (
  category_id    BIGSERIAL PRIMARY KEY,
  category_name  VARCHAR(50) NOT NULL UNIQUE,
  display_order  INT NOT NULL DEFAULT 0,
  is_active      BOOLEAN NOT NULL DEFAULT TRUE,
  created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by     BIGINT REFERENCES users(user_id),
  updated_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by     BIGINT REFERENCES users(user_id)
);
```

**초기 데이터**:
```sql
INSERT INTO categories (category_name, display_order) VALUES
('Special', 1),
('People', 2),
('Life', 3);
```

### 5.3 contents (사보 컨텐츠)
```sql
CREATE TABLE contents (
  content_id      BIGSERIAL PRIMARY KEY,
  title           VARCHAR(255) NOT NULL,
  summary         TEXT,
  body_html       TEXT NOT NULL,
  body_text       TEXT,
  thumbnail_file_id BIGINT REFERENCES resource_files(file_id),
  category_id     BIGINT NOT NULL REFERENCES categories(category_id),
  status          VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
  author_id       BIGINT NOT NULL REFERENCES users(user_id),
  published_at    TIMESTAMP,
  created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by      BIGINT REFERENCES users(user_id),
  updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by      BIGINT REFERENCES users(user_id),
  deleted_at      TIMESTAMP,
  deleted_by      BIGINT REFERENCES users(user_id),
  view_count      BIGINT NOT NULL DEFAULT 0,
  rating_count    BIGINT NOT NULL DEFAULT 0,
  rating_sum      BIGINT NOT NULL DEFAULT 0,
  average_rating  DECIMAL(3,2) NOT NULL DEFAULT 0.00,
  youtube_url     VARCHAR(255),
  instagram_url   VARCHAR(255),
  CONSTRAINT chk_status CHECK (status IN ('DRAFT', 'PUBLISHED', 'SCHEDULED', 'ARCHIVED'))
);
```

**인덱스**:
- `idx_contents_category_status_pub` ON `(category_id, status, published_at DESC)` WHERE `deleted_at IS NULL`
- `idx_contents_status_pub` ON `(status, published_at DESC)` WHERE `deleted_at IS NULL`
- `idx_contents_view_count` ON `view_count DESC` WHERE `status = 'PUBLISHED' AND deleted_at IS NULL`

### 5.4 content_search (전문 검색)
```sql
CREATE TABLE content_search (
  content_id      BIGINT PRIMARY KEY REFERENCES contents(content_id) ON DELETE CASCADE,
  search_vector   tsvector NOT NULL
);

CREATE INDEX idx_content_search_vector ON content_search USING GIN (search_vector);
```

### 5.5 hashtags
```sql
CREATE TABLE hashtags (
  hashtag_id     BIGSERIAL PRIMARY KEY,
  hashtag_name   VARCHAR(50) NOT NULL UNIQUE,
  usage_count    BIGINT NOT NULL DEFAULT 0,
  created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by     BIGINT REFERENCES users(user_id),
  updated_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by     BIGINT REFERENCES users(user_id)
);

CREATE INDEX idx_hashtags_usage_count ON hashtags(usage_count DESC);
```

### 5.6 content_hashtags
```sql
CREATE TABLE content_hashtags (
  content_id     BIGINT NOT NULL REFERENCES contents(content_id) ON DELETE CASCADE,
  hashtag_id     BIGINT NOT NULL REFERENCES hashtags(hashtag_id) ON DELETE RESTRICT,
  created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by     BIGINT REFERENCES users(user_id),
  PRIMARY KEY(content_id, hashtag_id)
);

CREATE INDEX idx_content_hashtags_hashtag ON content_hashtags(hashtag_id);
```

### 5.7 ratings (별점)
```sql
CREATE TABLE ratings (
  rating_id     BIGSERIAL PRIMARY KEY,
  content_id    BIGINT NOT NULL REFERENCES contents(content_id) ON DELETE CASCADE,
  user_id       BIGINT NOT NULL REFERENCES users(user_id),
  rating_value  INT NOT NULL CHECK (rating_value BETWEEN 1 AND 5),
  created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by    BIGINT REFERENCES users(user_id),
  updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by    BIGINT REFERENCES users(user_id),
  UNIQUE(content_id, user_id)
);

CREATE INDEX idx_ratings_content ON ratings(content_id);
```

### 5.8 reactions (반응 이모지)
```sql
CREATE TABLE reactions (
  reaction_id    BIGSERIAL PRIMARY KEY,
  content_id     BIGINT NOT NULL REFERENCES contents(content_id) ON DELETE CASCADE,
  user_id        BIGINT NOT NULL REFERENCES users(user_id),
  reaction_type  VARCHAR(20) NOT NULL,
  created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by     BIGINT REFERENCES users(user_id),
  updated_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by     BIGINT REFERENCES users(user_id),
  UNIQUE(content_id, user_id),
  CONSTRAINT chk_reaction_type CHECK (reaction_type IN ('LIKE', 'SAD', 'ANGRY', 'FUNNY'))
);

CREATE INDEX idx_reactions_content ON reactions(content_id);
```

**정책**: 단일 선택 + 토글
- 같은 타입 재클릭: DELETE
- 다른 타입 클릭: UPDATE

### 5.9 content_views_dedup (조회수 중복 방지)
```sql
CREATE TABLE content_views_dedup (
  content_id    BIGINT NOT NULL REFERENCES contents(content_id) ON DELETE CASCADE,
  user_id       BIGINT NOT NULL REFERENCES users(user_id),
  viewed_bucket TIMESTAMP NOT NULL,
  created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(content_id, user_id, viewed_bucket)
);

CREATE INDEX idx_content_views_dedup_content ON content_views_dedup(content_id);
CREATE INDEX idx_content_views_dedup_created ON content_views_dedup(created_at);
```

**viewed_bucket 계산**:
```sql
DATE_TRUNC('hour', NOW()) + INTERVAL '30 minutes' * FLOOR(EXTRACT(MINUTE FROM NOW()) / 30)
```



### 5.11 events (이벤트)
```sql
CREATE TABLE events (
  event_id        BIGSERIAL PRIMARY KEY,
  title           VARCHAR(255) NOT NULL,
  description     TEXT,
  thumbnail_file_id BIGINT REFERENCES resource_files(file_id),
  start_at        TIMESTAMP NOT NULL,
  end_at          TIMESTAMP NOT NULL,
  status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  created_by      BIGINT REFERENCES users(user_id),
  created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by      BIGINT REFERENCES users(user_id),
  deleted_at      TIMESTAMP,
  deleted_by      BIGINT REFERENCES users(user_id),
  CONSTRAINT chk_event_period CHECK (end_at > start_at),
  CONSTRAINT chk_event_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'CLOSED'))
);

CREATE INDEX idx_events_status_period 
  ON events(status, start_at, end_at) WHERE deleted_at IS NULL;
```

### 5.12 event_participants (이벤트 참여자)
```sql
CREATE TABLE event_participants (
  participant_id  BIGSERIAL PRIMARY KEY,
  event_id        BIGINT NOT NULL REFERENCES events(event_id) ON DELETE CASCADE,
  user_id         BIGINT NOT NULL REFERENCES users(user_id),
  comment         TEXT,
  is_winner       BOOLEAN NOT NULL DEFAULT FALSE,
  participated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE(event_id, user_id)
);

CREATE INDEX idx_event_participants_event ON event_participants(event_id);
CREATE INDEX idx_event_participants_winner ON event_participants(event_id, is_winner);
```

### 5.13 popups (팝업)
```sql
CREATE TABLE popups (
  popup_id       BIGSERIAL PRIMARY KEY,
  title          VARCHAR(255) NOT NULL,
  content        TEXT,
  image_file_id  BIGINT REFERENCES resource_files(file_id),
  link_url       VARCHAR(500),
  start_at       TIMESTAMP NOT NULL,
  end_at         TIMESTAMP NOT NULL,
  is_active      BOOLEAN NOT NULL DEFAULT TRUE,
  display_order  INT NOT NULL DEFAULT 0,
  created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by     BIGINT REFERENCES users(user_id),
  updated_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by     BIGINT REFERENCES users(user_id),
  CONSTRAINT chk_popup_period CHECK (end_at > start_at)
);

CREATE INDEX idx_popups_active_period 
  ON popups(is_active, start_at, end_at, display_order);
```

### 5.14 ideas (아이디어 제안)
```sql
CREATE TABLE ideas (
  idea_id        BIGSERIAL PRIMARY KEY,
  user_id        BIGINT NOT NULL REFERENCES users(user_id),
  title          VARCHAR(255) NOT NULL,
  description    TEXT NOT NULL,
  status         VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  admin_comment  TEXT,
  created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  reviewed_at    TIMESTAMP,
  reviewed_by    BIGINT REFERENCES users(user_id),
  deleted_at     TIMESTAMP,
  deleted_by     BIGINT REFERENCES users(user_id),
  CONSTRAINT chk_idea_status CHECK (status IN ('PENDING', 'REVIEWED', 'ACCEPTED', 'REJECTED'))
);

CREATE INDEX idx_ideas_status_created 
  ON ideas(status, created_at DESC) WHERE deleted_at IS NULL;
```

### 5.15 resource_files (파일 관리)
```sql
CREATE TABLE resource_files (
  file_id        BIGSERIAL PRIMARY KEY,
  original_name  VARCHAR(255) NOT NULL,
  stored_name    VARCHAR(255) NOT NULL UNIQUE,
  file_path      VARCHAR(500) NOT NULL,
  file_size      BIGINT NOT NULL,
  mime_type      VARCHAR(100) NOT NULL,
  sha256         VARCHAR(64) NOT NULL,
  uploaded_by    BIGINT REFERENCES users(user_id),
  uploaded_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted_at     TIMESTAMP,
  deleted_by     BIGINT REFERENCES users(user_id)
);

CREATE INDEX idx_resource_files_uploaded_at ON resource_files(uploaded_at DESC);
CREATE INDEX idx_resource_files_sha256 ON resource_files(sha256);
```

### 5.16 audit_logs (감사 로그)
```sql
CREATE TABLE audit_logs (
  audit_id      BIGSERIAL PRIMARY KEY,
  actor_user_id BIGINT REFERENCES users(user_id),
  action        VARCHAR(30) NOT NULL,
  entity_type   VARCHAR(50) NOT NULL,
  entity_id     BIGINT,
  before_json   JSONB,
  after_json    JSONB,
  created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_logs_entity 
  ON audit_logs(entity_type, entity_id, created_at DESC);
CREATE INDEX idx_audit_logs_actor 
  ON audit_logs(actor_user_id, created_at DESC);
CREATE INDEX idx_audit_logs_created 
  ON audit_logs(created_at DESC);
```

### 5.17 visitor_logs (방문 로그)
```sql
CREATE TABLE visitor_logs (
  log_id        BIGSERIAL PRIMARY KEY,
  user_id       BIGINT REFERENCES users(user_id),
  visited_at    DATE NOT NULL,
  page_views    INT DEFAULT 1,
  UNIQUE(user_id, visited_at)
);

CREATE INDEX idx_visitor_logs_date ON visitor_logs(visited_at DESC);
```

---

## 6. API 명세

### 6.1 기본 규칙
- **Base URL**: `/api`
- **응답 형식**: JSON
- **인증**: JWT Bearer Token (`Authorization: Bearer {token}`)
- **에러 형식**:
```json
{
  "timestamp": "2025-12-26T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "상세 메시지",
  "path": "/api/contents"
}
```

### 6.2 인증 API

#### POST /api/auth/login
OAuth 2.0 로그인

**Request Body**:
```json
{
  "code": "string",
  "redirectUri": "string",
  "username": "string" // (Optional) Dev/Test Login
}

**Validation**:
- `username`: Not Empty (Empty string returns 400 Bad Request)
```

**Response (200)**:
```json
{
  "accessToken": "string",
  "refreshToken": "string",
  "expiresIn": 3600,
  "user": {
    "userId": 1,
    "username": "string",
    "name": "string",
    "email": "string",
    "role": "USER"
  }
}
```

#### POST /api/auth/refresh
토큰 갱신

**Request Body**:
```json
{
  "refreshToken": "string"
}
```

**Response (200)**: login과 동일

#### POST /api/auth/logout
로그아웃

**Request**: Bearer Token 필요  
**Response (204)**: No Content

#### GET /api/auth/me
현재 사용자 정보

**Response (200)**:
```json
{
  "userId": 1,
  "username": "string",
  "name": "string",
  "email": "string",
  "department": "string",
  "role": "USER"
}
```

---

### 6.3 컨텐츠 API

#### GET /api/contents
목록 조회

**Query Parameters**:
- `page` (int, default: 0)
- `size` (int, default: 12)
- `sort` (string, default: "publishedAt,desc")
  - 허용값: "publishedAt,desc", "viewCount,desc", "averageRating,desc"
- `categoryId` (long, optional)
- `hashtag` (string, optional, 쉼표 구분)
- `q` (string, optional, 검색어)
- `status` (string, default: "PUBLISHED")

**Response (200)**:
```json
{
  "content": [
    {
      "contentId": 1,
      "title": "string",
      "summary": "string",
      "thumbnailUrl": "string",
      "categoryName": "string",
      "hashtags": ["string"],
      "viewCount": 0,
      "averageRating": 0.0,
      "publishedAt": "2025-01-01T09:00:00"
    }
  ],
  "totalElements": 0,
  "totalPages": 0
}
```

#### GET /api/contents/{id}
상세 조회 (조회수 자동 증가, 30분 dedup 적용)

**Response (200)**:
```json
{
  "contentId": 1,
  "title": "string",
  "summary": "string",
  "bodyHtml": "string",
  "thumbnailUrl": "string",
  "categoryId": 1,
  "categoryName": "string",
  "hashtags": [
    {"hashtagId": 1, "hashtagName": "string"}
  ],
  "viewCount": 0,
  "averageRating": 0.0,
  "ratingCount": 0,
  "reactions": {
    "LIKE": 0,
    "SAD": 0,
    "ANGRY": 0,
    "FUNNY": 0
  },
  "userReaction": "LIKE",
  "userRating": 5,
  "author": {
    "userId": 1,
    "name": "string"
  },
  "publishedAt": "2025-01-01T09:00:00",
  "createdAt": "2025-01-01T08:00:00",
  "updatedAt": "2025-01-01T08:30:00"
}
```

#### POST /api/contents
등록 (관리자 전용)

**Request Body**:
```json
{
  "title": "string",
  "summary": "string",
  "bodyHtml": "string",
  "thumbnailFileId": 1,
  "categoryId": 1,
  "hashtags": ["string"],
  "status": "DRAFT"
}
```

**Response (201)**:
```json
{
  "contentId": 1,
  "message": "등록 성공"
}
```

#### PUT /api/contents/{id}
수정 (관리자 전용)

**Request Body**: POST와 동일  
**Response (204)**: No Content

#### DELETE /api/contents/{id}
삭제 (관리자 전용, Soft Delete)

**Response (204)**: No Content

#### POST /api/contents/{id}/rating
별점 부여/수정

**Request Body**:
```json
{
  "rating": 5
}
```

**Response (200)**:
```json
{
  "averageRating": 4.6,
  "message": "별점 등록 완료"
}
```

#### POST /api/contents/{id}/reaction
반응 등록/토글 (단일 선택)

**Request Body**:
```json
{
  "reactionType": "LIKE"
}
```

**Response (200)**:
```json
{
  "action": "added",
  "currentReaction": "LIKE",
  "reactions": {
    "LIKE": 46,
    "SAD": 2,
    "ANGRY": 0,
    "FUNNY": 10
  }
}
```

**action 값**:
- `added`: 신규 등록
- `removed`: 토글로 삭제
- `changed`: 다른 반응으로 변경

---



### 6.5 이벤트 API

#### GET /api/events
목록 조회

**Query Parameters**:
- `status` (string): "active", "ended", "all"

**Response (200)**:
```json
{
  "content": [
    {
      "eventId": 1,
      "title": "string",
      "thumbnailUrl": "string",
      "startAt": "2025-01-10T00:00:00",
      "endAt": "2025-01-31T23:59:59",
      "participantCount": 245,
      "isParticipated": false
    }
  ]
}
```

#### GET /api/events/{id}
상세 조회

**Response (200)**: 목록 항목 + `description` 추가

#### POST /api/events/{id}/participate
참여 신청

**Request Body**:
```json
{
  "comment": "string"
}
```

**Response (200)**:
```json
{
  "message": "참여 완료"
}
```

#### POST /api/events/{id}/draw
당첨자 추첨 (관리자 전용)

**Request Body**:
```json
{
  "winnerCount": 10
}
```

  "message": "10명 추첨 완료"
}
```

### 6.6 대시보드 API (관리자 전용)
**공통**: `ROLE_ADMIN` 권한 필수

#### GET /api/dashboard/top-views
조회수 Top 10

**Response (200)**: `ContentResponseDto` List

#### GET /api/dashboard/top-ratings
평점 Top 10

**Response (200)**: `ContentResponseDto` List

#### GET /api/dashboard/visitor-trend
방문자 추이 (최근 7일)

**Response (200)**:
```json
[
  {
    "date": "2025-01-01",
    "pageViews": 150,
    "visitorCount": 0
  }
]
```

#### GET /api/dashboard/category-stats
카테고리별 컨텐츠 수

**Response (200)**:
```json
[
  {
    "categoryName": "Special",
    "count": 12
  }
]
```

#### GET /api/dashboard/reaction-stats
반응(이모지) 통계

**Response (200)**:
```json
[
  {
    "reactionType": "LIKE",
    "count": 142
  }
]
```

#### GET /api/dashboard/hashtag-stats
인기 해시태그 Top 10

**Response (200)**:
```json
[
  {
    "hashtagName": "ESG",
    "usageCount": 45
  }
]
```}
```

#### GET /api/events/{id}/winners
당첨자 목록

**Response (200)**:
```json
{
  "winners": [
    {
      "name": "string",
      "department": "string"
    }
  ]
}
```

---

### 6.6 팝업 API

#### GET /api/popups
활성 팝업 목록

**Response (200)**:
```json
[
  {
    "popupId": 1,
    "title": "string",
    "imageUrl": "string",
    "linkUrl": "string",
    "displayOrder": 1
  }
]
```

#### POST /api/popups
등록 (관리자 전용)

**Request Body**:
```json
{
  "title": "string",
  "content": "string",
  "imageFileId": 1,
  "linkUrl": "string",
  "startAt": "2025-01-15T00:00:00",
  "endAt": "2025-01-20T23:59:59",
  "displayOrder": 1
}
```

**Response (201)**:
```json
{
  "popupId": 1,
  "message": "등록 완료"
}
```

#### PUT /api/popups/{id}
수정 (관리자 전용)

#### DELETE /api/popups/{id}
삭제 (관리자 전용)

---

### 6.7 아이디어 제안 API

#### GET /api/ideas
목록 조회

**Query Parameters**:
- `status` (string, optional, 관리자만)

**Response (200)**:
```json
{
  "content": [
    {
      "ideaId": 1,
      "title": "string",
      "status": "PENDING",
      "createdAt": "2025-01-10T14:30:00"
    }
  ]
}
```

#### POST /api/ideas
제안 등록

**Request Body**:
```json
{
  "title": "string",
  "description": "string"
}
```

**Response (201)**:
```json
{
  "ideaId": 1,
  "message": "제안 완료"
}
```

#### PUT /api/ideas/{id}/review
검토 처리 (관리자 전용)

**Request Body**:
```json
{
  "status": "ACCEPTED",
  "adminComment": "string"
}
```

**Response (204)**: No Content

---

### 6.8 파일 업로드 API

#### POST /api/files/upload
파일 업로드

**Request**: `multipart/form-data`
- `file`: 파일 (최대 5MB, .jpg/.jpeg/.png/.gif/.webp)

**Response (200)**:
```json
{
  "fileId": 123,
  "url": "/uploads/2025/01/uuid.jpg",
  "originalName": "photo.jpg",
  "size": 1024000,
  "mimeType": "image/jpeg",
  "sha256": "string"
}
```

#### GET /api/files
목록 조회 (관리자 전용)

**Query Parameters**:
- `page` (int)
- `size` (int)

**Response (200)**:
```json
{
  "content": [
    {
      "fileId": 123,
      "originalName": "string",
      "url": "string",
      "size": 0,
      "uploadedBy": "string",
      "uploadedAt": "2025-01-15T10:30:00"
    }
  ]
}
```

#### DELETE /api/files/{id}
삭제 (관리자 전용, Soft Delete)

**Response (204)**: No Content

---

### 6.9 대시보드 API (관리자 전용)

#### GET /api/dashboard/top-views
조회수 TOP 10

**Response (200)**:
```json
[
  {
    "contentId": 5,
    "title": "string",
    "viewCount": 2540,
    "url": "/magazine/5"
  }
]
```

#### GET /api/dashboard/top-ratings
별점 TOP 10

**Response (200)**:
```json
[
  {
    "contentId": 12,
    "title": "string",
    "averageRating": 4.8,
    "ratingCount": 120
  }
]
```

#### GET /api/dashboard/visitor-trend
방문자 추이

**Query Parameters**:
- `period` (string): "daily", "weekly", "monthly"

**Response (200)**:
```json
{
  "labels": ["2025-01-01", "2025-01-02"],
  "data": [120, 135]
}
```

#### GET /api/dashboard/category-stats
카테고리별 통계

**Response (200)**:
```json
[
  {
    "categoryName": "Special",
    "viewCount": 5400,
    "percentage": 45
  }
]
```

#### GET /api/dashboard/hashtag-stats
해시태그 TOP 20

**Response (200)**:
```json
[
  {
    "hashtagName": "string",
    "usageCount": 120
  }
]
```

#### GET /api/dashboard/reaction-stats
반응 통계

**Response (200)**:
```json
{
  "LIKE": 2540,
  "SAD": 120,
  "ANGRY": 45,
  "FUNNY": 890
}
```

---

## 7. 트랜잭션 규칙

### 7.1 컨텐츠 저장/수정
**단일 트랜잭션**:
1. contents INSERT/UPDATE
2. body_text 생성 (HTML 태그 제거)
3. content_hashtags 전량 삭제 → usage_count 감소
4. content_hashtags 생성 → usage_count 증가
5. content_search.search_vector 갱신
6. audit_logs 기록

### 7.2 별점 등록
**단일 트랜잭션**:
1. ratings INSERT/UPDATE
2. contents.rating_count, rating_sum, average_rating 갱신
3. audit_logs 기록 (선택)

### 7.3 반응 등록
**단일 트랜잭션**:
1. 기존 반응 조회
2. 없으면 INSERT
3. 같은 타입이면 DELETE
4. 다른 타입이면 UPDATE
5. audit_logs 기록 (선택)

### 7.4 조회수 증가
**단일 트랜잭션**:
1. viewed_bucket 계산
2. content_views_dedup INSERT 시도
3. 성공 시에만 view_count += 1

---

## 8. 보안 정책

### 8.1 인증/인가
- **OAuth 2.0 (OIDC)** + JWT Bearer Token
- **Phase 1**: Mock 구현 (username/password)
- **Phase 3**: 실제 SSO 연동

### 8.2 HTML Sanitization
**허용 태그**:
```
p, br, strong, em, u, s, h1-h6, ul, ol, li, blockquote, pre, code,
a[href, target, rel], img[src, alt, width, height],
table, thead, tbody, tr, th, td, div[class], span[class]
```

**iframe 정책**:
- YouTube/Vimeo 도메인만 허용
- 도메인 화이트리스트: `www.youtube.com`, `youtube.com`, `vimeo.com`

**라이브러리**: OWASP Java HTML Sanitizer

### 8.3 파일 업로드 보안
1. 확장자 화이트리스트: `.jpg, .jpeg, .png, .gif, .webp`
2. MIME 타입 검증 (Magic Number)
3. UUID 파일명 생성
4. SHA256 무결성 검증
5. 크기 제한: 5MB
6. 경로 트래버설 방지

### 8.4 기타
- **HTTPS**: 운영 환경 필수
- **CORS**: 프론트엔드 도메인만 허용
- **SQL Injection**: JPA Parameterized Query
- **Rate Limiting**: Nginx/Gateway (선택)

---

## 9. 성능 요구사항

### 9.1 응답 시간
- 메인/목록: p95 2초 이내
- 상세: p95 2초 이내
- 업로드: 5MB 파일 5초 이내

### 9.2 동시 접속
- 500명 (동시 활성 세션)

### 9.3 캐싱
- 정적 파일: 브라우저 캐시 1주일
- API 응답: 조회성 데이터 1분 (선택)

---

## 10. 백업/보관 정책

### 10.1 PostgreSQL 백업
- 일 1회 Full Backup
- WAL 아카이빙 (가능 시)
- 보관: 30일 (기본), 월간 12개 (선택)

### 10.2 로그 보관
- `audit_logs`: 1년
- `content_views_dedup`: 90일
- `visitor_logs`: 1년

### 10.3 파티셔닝
- `audit_logs`: 월별 파티션 (권장)
- `content_views_dedup`: 월별 파티션 (권장)

---

**문서 버전**: v1.4  
**최종 수정일**: 2025-12-26 (Phase 2 Complete)  
**작성자**: Claude (Anthropic)
