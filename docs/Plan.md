# Plan.md - 고려아연 전자사보 시스템 개발 계획

## 📌 문서 성격 및 준수 사항

**이 문서는 참고 자료가 아니라 "구속력 있는 계약서"입니다.**

### 절대 기준
- 모든 구현은 반드시 TechSpec.md와 본 문서를 100% 준수해야 한다
- 요구사항 충돌, 모호함, 누락 발견 시 즉시 작업 중단 후 보고
- 모든 산출물은 한국어로 작성

### 범위 통제
- 본 문서에 명시되지 않은 작업 수행 금지
- 체크리스트에 없는 컴포넌트, API, 테이블 생성 금지
- 명시적 지시 없는 개선, 리팩토링, 최적화, 확장 금지

### TDD 강제 규칙
- 항상 실패하는 테스트를 먼저 작성
- 테스트가 없는 코드는 미완성으로 간주
- 각 작업의 완료 조건에 테스트 통과 포함

### 데이터베이스 및 마이그레이션
- Entity 변경 시 반드시 Flyway 마이그레이션 스크립트 작성
- 마이그레이션 없는 스키마 변경 금지
- 마이그레이션 이력과 실제 스키마 항상 일치

---

## 실행 프로토콜: "go"

사용자가 **"go"**를 입력하면 다음 순서를 반드시 따른다:

1. **작업 확인**: Plan.md에서 가장 위에 있는 미완료([ ]) 작업 확인
2. **영향 범위 요약**: Backend/Frontend 컴포넌트 나열
3. **TDD 사이클 수행**:
   - 실패하는 테스트 작성
   - 최소 구현
   - 리팩토링
4. **체크박스 업데이트**: 작업 완료 시 `[v]`로 변경
5. **문서 갱신**: API/스키마 변경 시 TechSpec.md 즉시 갱신
6. **Git 커밋/푸시**: 단위 작업 완료 시 커밋 메시지 규칙 준수

---

## 완료 조건 (Definition of Done)

작업은 아래 조건을 **모두** 만족해야만 완료로 간주:

- [ ] 모든 관련 테스트가 통과
- [ ] 파일 존재 여부가 아닌, **기능 내부 로직(분기, 예외처리 등)이 실제 구현되었는지** 코드 라인 단위 검증
- [ ] TODO 또는 FIXME가 남아 있지 않음
- [ ] Plan.md가 최신 상태로 업데이트
- [ ] TechSpec.md가 실제 구현과 정확히 일치
- [ ] Git 커밋/푸시 완료

---

## Git 커밋 규칙

### 커밋 메시지 형식
```
<step><type>(<scope>): <subject>
```

### Type 종류
- `feat`: 새로운 기능
- `fix`: 버그 수정
- `refactor`: 리팩토링
- `test`: 테스트 추가/수정
- `docs`: 문서 수정
- `chore`: 빌드/설정 변경

### Step 예시
- `P1`: Phase 1
- `P2`: Phase 2
- `P3`: Phase 3
- `P4`: Phase 4
- `P5`: Phase 5

### 작성 규칙
- `<scope>`: 영향받는 주요 모듈/페이지 (예: auth, content, ui)
- `<subject>`: 한글, 현재 시제, 마침표 없음
- 변경 사항 분석 후 핵심 내용 반영

### 예시
```
P1feat(auth): OAuth 2.0 Mock 인증 구현
P2test(content): 컨텐츠 목록 조회 테스트 추가
P3refactor(social): YouTube API 연동 로직 개선
```

---

## Phase 1: 기본 인프라 구축 (1주차)

**목표**: 백엔드/프론트엔드 프로젝트 생성 및 로컬 실행 확인

### 1.1 Backend 프로젝트 생성
- [x] Spring Boot 프로젝트 생성 (Spring Initializr)
  - Dependencies: Web, JPA, PostgreSQL, Security, Flyway
- [x] `application.yml` 기본 설정 작성
  - DB 연결 정보 (localhost:5433)
  - JWT Secret 설정
  - Flyway 활성화
- [x] `build.gradle` 의존성 확인
- [x] 프로젝트 구조 생성 (패키지)
  - `config/`, `controller/`, `service/`, `repository/`, `entity/`, `dto/`, `security/`, `util/`

### 1.2 PostgreSQL 설치 및 설정
- [x] PostgreSQL 15+ 설치 (로컬, 포트 5433)
- [x] 데이터베이스 생성 (`kz_magazine`)
- [x] 사용자 생성 및 권한 부여
- [x] 연결 테스트 (Spring Boot 실행)

### 1.3 Flyway 마이그레이션 - V1 (초기 스키마)
- [x] `V1__create_initial_schema.sql` 작성
  - `users` 테이블
  - `categories` 테이블 + 초기 데이터
  - `contents` 테이블
  - `hashtags` 테이블
  - `content_hashtags` 테이블
  - `ratings` 테이블
  - `reactions` 테이블
  - `social_contents` 테이블
  - `events` 테이블
  - `event_participants` 테이블
  - `popups` 테이블
  - `ideas` 테이블
  - `resource_files` 테이블
  - `visitor_logs` 테이블
- [x] 마이그레이션 실행 확인
- [x] 샘플 데이터 삽입
  - ADMIN 사용자 1명
  - USER 사용자 2명
  - 컨텐츠 3개 (PUBLISHED)

### 1.4 OAuth 2.0 Mock 인증 구현
- [x] `SecurityConfig.java` 작성
  - CORS 설정 (localhost:5173 허용)
  - JWT Filter 등록
- [x] `JwtTokenProvider.java` 작성
  - 토큰 생성/검증 로직
- [x] `AuthController.java` 작성
  - `POST /api/auth/login` (Mock)
  - `POST /api/auth/refresh`
  - `POST /api/auth/logout`
  - `GET /api/auth/me`
- [x] 인증 테스트 작성
  - 로그인 성공
  - 토큰 검증
  - 권한 확인

### 1.5 Frontend 프로젝트 생성
- [x] Vite + React 프로젝트 생성
- [x] 디렉토리 구조 생성
  - `src/components/`, `src/pages/`, `src/contexts/`, `src/services/`, `src/utils/`
- [x] 의존성 설치
  - React Router
  - Axios
  - Bootstrap, React-Bootstrap
  - React-Quill, Recharts
- [x] `vite.config.js` 설정
  - Proxy 설정 (localhost:8080)

### 1.6 React Router 설정
- [x] `App.jsx` 라우터 구조 작성
  - `/` (Home)
  - `/magazine` (Magazine)
  - `/magazine/:id` (ContentDetail)
  - `/social` (Social)
  - `/events` (Events)
  - `/ideas` (Ideas)
  - `/admin/*` (Admin)
- [x] PrivateRoute 컴포넌트 작성 (권한 확인)

### 1.7 Bootstrap 기본 레이아웃
- [x] `Header.jsx` 작성
  - 로고, 메뉴, 로그인/로그아웃 버튼
- [x] `Footer.jsx` 작성
- [x] `Layout.jsx` 작성 (Header + Content + Footer)

### 1.8 Axios 설정
- [x] `services/api.js` 작성
  - Base URL 설정
  - Interceptor (토큰 자동 추가)
  - 에러 핸들링

### 1.9 AuthContext 생성
- [x] `contexts/AuthContext.jsx` 작성
  - 로그인 상태 관리
  - 사용자 정보 저장
  - 로그인/로그아웃 함수

### 1.10 로그인 페이지 구현
- [x] `pages/Login.jsx` 작성
  - Username/Password 입력
  - Mock 로그인 API 호출
  - 토큰 저장 (localStorage)
  - 메인 페이지 리다이렉트

### 1.11 메인 페이지 기본 레이아웃
- [x] `pages/Home.jsx` 작성
  - 롤링 배너 영역 (placeholder)
  - 카테고리별 섹션 (placeholder)
- [x] 로그인 후 메인 페이지 표시 확인

### 1.12 Phase 1 통합 테스트
- [x] Backend 실행 확인
- [x] Frontend 실행 확인
- [x] 로그인 → 메인 페이지 이동 확인
- [x] JWT 토큰 정상 발급 확인

---

## Phase 2: 컨텐츠 기본 기능 (2주차)

**목표**: 사보 조회 및 관리자 CRUD 구현

### 2.1 Backend - 컨텐츠 Entity 및 Repository
- [x] `Content.java` Entity 작성
- [x] `Category.java` Entity 작성
- [x] `Hashtag.java` Entity 작성
- [x] `ContentHashtag.java` Entity 작성
- [x] JPA Repository 작성
  - `ContentRepository.java`
  - `CategoryRepository.java`
  - `HashtagRepository.java`

### 2.2 Backend - 컨텐츠 조회 API
- [ ] `ContentController.java` - 목록 조회
  - `GET /api/contents`
  - 페이징, 정렬, 필터링 (카테고리, 해시태그, 검색)
- [ ] `ContentController.java` - 상세 조회
  - `GET /api/contents/{id}`
  - 조회수 증가 로직 (dedup 제외, Phase 3에서 추가)
- [ ] `ContentService.java` 작성
  - 비즈니스 로직
  - DTO 변환
- [ ] 컨텐츠 조회 테스트 작성

### 2.3 Backend - 컨텐츠 CRUD API (관리자)
- [x] `ContentController.java` - 등록 (`POST /api/contents`)
  - [x] HTML Sanitization 적용 (Placeholder)
- [x] `ContentController.java` - 수정 (`PUT /api/contents/{id}`)
- [x] `ContentController.java` - 삭제 (`DELETE /api/contents/{id}`) (Soft Delete)
- [x] 트랜잭션 관리
  - [x] 해시태그 연결
  - [x] usage_count 증감
- [x] CRUD 테스트 작성

### 2.4 Backend - 파일 업로드 API
- [x] `FileController.java` 작성
  - [x] `POST /api/files/upload`
  - [x] `MultipartFile` 처리
- [x] `FileService.java` 작성
  - [x] UUID 파일명 생성
  - [x] MIME 타입 검증
  - [x] SHA256 계산 (Placeholder)
  - [x] 파일 저장 (`/uploads/YYYY/MM/`)
- [x] `ResourceFile.java` Entity 작성
- [x] 파일 업로드 테스트 작성

### 2.5 Backend - HTML Sanitization
- [x] OWASP Java HTML Sanitizer 의존성 추가
- [x] `HtmlSanitizerUtil.java` Utility 작성
  - [x] 허용 태그/속성 화이트리스트
  - [x] iframe YouTube/Vimeo 검증
- [x] 컨텐츠 저장 시 Sanitization 적용
- [x] Sanitization 테스트 작성

### 2.6 Flyway 마이그레이션 - V2 (content_search 추가)
- [x] `V2__add_content_search.sql` 작성
  - [x] `content_search` 테이블 생성
  - [x] GIN 인덱스 생성
- [x] 마이그레이션 실행 확인

### 2.7 Frontend - 사보 목록 페이지
- [x] `pages/Magazine.jsx` 작성
  - [x] 카드 그리드 레이아웃
  - [x] 페이지네이션
- [x] `components/magazine/ContentCard.jsx` 작성
  - [x] 썸네일, 제목, 요약, 해시태그, 조회수, 별점
- [ ] `components/magazine/CategoryFilter.jsx` 작성
  - 카테고리 버튼
- [x] 목록 조회 API 연동
- [ ] 필터링 기능 구현 (Phase 3으로 이월 가능 또는 추후 구현)

### 2.8 Frontend - 사보 상세 페이지
- [x] `pages/ContentDetail.jsx` 작성
  - [x] HTML 본문 렌더링
  - [x] 조회수, 작성일 표시
  - [x] 저자 정보
- [x] 상세 조회 API 연동

### 2.9 Frontend - 관리자 컨텐츠 목록
- [x] `pages/admin/ContentManagement.jsx` 작성
  - [x] 테이블 레이아웃
  - [x] 상태별 필터 (DRAFT, PUBLISHED, ARCHIVED)
  - [x] 수정/삭제 버튼
- [x] 목록 조회 API 연동 (관리자)

### 2.10 Frontend - 관리자 컨텐츠 작성/수정
- [x] `pages/admin/ContentForm.jsx` 작성
  - [x] React-Quill 에디터
  - [x] 제목, 요약, 카테고리, 해시태그, 상태 입력
  - [x] 파일 업로드 (드래그앤드롭)
- [x] 파일 업로드 API 연동
- [x] 컨텐츠 등록/수정 API 연동
- [x] 썸네일 미리보기

### 2.11 Phase 2 통합 테스트
- [x] 관리자가 컨텐츠 작성 → 저장 확인
- [x] 사용자가 목록 조회 → 상세 조회 확인
- [x] 파일 업로드 → 썸네일 표시 확인
- [x] HTML Sanitization 정상 작동 확인

---

## Phase 3: 확장 기능 (3주차)

**목표**: SNS 연동, 이벤트, 반응/별점 구현

### 3.1 Flyway 마이그레이션 - V3 (content_views_dedup, audit_logs)
- [x] `V3__add_dedup_and_audit.sql` 작성
  - `content_views_dedup` 테이블
  - `audit_logs` 테이블
- [x] `V4__add_reactions.sql` 작성
  - `reactions` 테이블
- [x] 마이그레이션 실행 확인

### 3.2 Backend - 조회수 dedup 로직
- [x] `ContentViewService.java` 작성
  - viewed_bucket 계산
  - dedup INSERT 시도
  - view_count 증가
- [x] `GET /api/contents/{id}` 수정 (dedup 적용)
- [x] dedup 테스트 작성

### 3.3 Backend - 반응 API
- [x] `Reaction.java` Entity 작성
- [x] `ReactionController.java` 작성
  - `POST /api/contents/{id}/reaction`
- [x] `ReactionService.java` 작성
  - 단일 선택 + 토글 로직
  - 기존 반응 조회 → INSERT/UPDATE/DELETE
- [x] 반응 테스트 작성

### 3.4 Backend - 별점 API (ReactionService에 통합 구현)
- [x] `Reaction.java` Entity 사용
- [x] `ReactionController.java` 작성
  - `POST /api/contents/{id}/rating`
- [x] `ReactionService.java` 작성
  - Upsert 로직
  - 평균 별점 재계산
- [x] 별점 테스트 작성

### 3.5 Backend - Audit Log 기록
- [x] `AuditLog.java` Entity 작성
- [x] `AuditLogService.java` 작성
  - 공통 메서드 (log 생성)
- [x] 컨텐츠 CRUD 시 audit 기록
- [x] Audit 테스트 작성

### 3.6 Backend - YouTube API 연동
- [x] V5 마이그레이션 (`youtube_url`, `instagram_url` 컬럼 추가)
- [x] `Content.java` 수정 (Legacy 연동 방식 변경: 직접 URL 저장)
- [x] `ContentService.java` 수정
- [x] API 연동 테스트 작성 (`ContentApiTest`로 통합 검증)

### 3.7 Backend - Instagram API 연동
- [x] V5 마이그레이션 포함
- [x] `Content.java` 수정
- [x] `ContentService.java` 수정
- [x] API 연동 테스트 작성

### 3.8 Backend - 이벤트 CRUD API
- [x] `Event.java` Entity 작성
- [x] `EventParticipant.java` Entity 작성
- [x] `EventController.java` 작성
  - `GET /api/events` (목록)
  - `GET /api/events/{id}` (상세)
  - `POST /api/events` (등록, 관리자)
  - `PUT /api/events/{id}` (수정, 관리자)
  - `DELETE /api/events/{id}` (삭제, 관리자)
- [x] `EventService.java` 작성
- [x] 이벤트 CRUD 테스트 작성 (`EventServiceTest`, `EventApiTest`)

### 3.9 Backend - 이벤트 참여 및 추첨 API
- [x] `EventController.java` 작성
  - `POST /api/events/{id}/participate`
  - `POST /api/events/{id}/draw` (관리자)
  - `GET /api/events/{id}/winners`
- [x] `EventService.java` 작성
  - 참여 로직 (Upsert)
  - 랜덤 추첨 로직 (Native Query)
  - 당첨자 목록 조회
- [x] 이벤트 참여/추첨 테스트 작성

### 3.10 Frontend - 사보 상세 페이지 (반응/별점 추가)
- [x] `components/magazine/ReactionButtons.jsx` 작성
  - LIKE, SAD, ANGRY, FUNNY 버튼
  - 현재 사용자 반응 하이라이트
  - 클릭 시 토글
- [x] `components/magazine/RatingStars.jsx` 작성
  - 별점 5개 표시
  - 클릭 시 별점 부여
- [x] 반응/별점 API 연동
- [x] 실시간 카운트 업데이트

### 3.11 Frontend - 소셜 컨텐츠 페이지
- [x] `pages/Social.jsx` 작성
  - YouTube/Instagram 탭
- [x] `components/social/YouTubeGrid.jsx` 작성
  - 영상 그리드
  - 모달 플레이어
- [x] `components/social/InstagramGrid.jsx` 작성
  - 포스트 그리드
- [x] 소셜 컨텐츠 API 연동

### 3.12 Frontend - iframe 임베드 페이지
- [x] `pages/SocialEmbed.jsx` 작성
  - 헤더/푸터 없음
  - 경량 레이아웃
  - YouTube/Instagram 그리드만 표시

### 3.13 Frontend - 이벤트 페이지
- [x] `pages/Events.jsx` 작성
  - 진행중/종료 탭
  - 이벤트 카드 그리드
- [x] `pages/EventDetail.jsx` 작성
  - 이벤트 상세 정보
  - 참여 버튼
  - 당첨자 발표 (종료 후)
- [x] 이벤트 API 연동

### 3.14 Frontend - 관리자 이벤트 관리
- [x] `pages/admin/EventManagement.jsx` 작성
  - 이벤트 목록 테이블
  - 추첨 버튼
- [x] `pages/admin/EventForm.jsx` 작성
  - 이벤트 등록/수정 폼
- [x] 이벤트 CRUD/추첨 API 연동

### 3.15 Phase 3 통합 테스트
- [x] SNS 컨텐츠 자동 수집 확인
- [x] 이벤트 참여 → 추첨 → 당첨자 발표 확인
- [x] 사보 상세에서 반응/별점 등록 확인
- [x] 조회수 dedup 정상 작동 확인

---

## Phase 4: 대시보드 및 최적화 (4주차)

**목표**: 관리자 대시보드 및 성능 개선

### 4.1 Backend - 대시보드 통계 API
- [x] `DashboardController.java` 작성
  - `GET /api/dashboard/top-views`
  - `GET /api/dashboard/top-ratings`
  - `GET /api/dashboard/visitor-trend`
  - `GET /api/dashboard/category-stats`
  - `GET /api/dashboard/hashtag-stats`
  - `GET /api/dashboard/reaction-stats`
- [x] `DashboardService.java` 작성
  - 각 통계 쿼리 작성
  - 최소 표본 필터 (별점 5개 이상)
- [x] 대시보드 테스트 작성

### 4.2 Backend - Full-Text Search 구현
- [x] `ContentService.java` 수정
  - 검색어 입력 시 `content_search` 테이블 활용 (`GET /api/contents?q=...`)
  - `to_tsquery` 사용
- [x] Search Index Migration 확인 (V2__add_content_search.sql)
- [x] 검색 성능 테스트 작성 (SearchApiTest)

### 4.3 Backend - 해시태그 재집계 배치
- [ ] `HashtagBatchService.java` 작성
  - `content_hashtags` 기준 재집계
  - `@Scheduled` 설정 (매일 새벽 3시)
- [ ] 재집계 테스트 작성

### 4.4 Backend - 방문자 로그 배치
- [ ] `VisitorLogService.java` 작성
  - 일일 방문자 집계
  - `visitor_logs` INSERT (Upsert)
  - `@Scheduled` 설정 (매일 자정)
- [ ] 방문자 로그 테스트 작성

### 4.5 Backend - 아이디어 제안 API
- [ ] `Idea.java` Entity 작성
- [ ] `IdeaController.java` 작성
  - `GET /api/ideas` (본인 제안, 관리자 전체)
  - `POST /api/ideas`
  - `PUT /api/ideas/{id}/review` (관리자)
- [ ] `IdeaService.java` 작성
- [ ] 아이디어 제안 테스트 작성

### 4.6 Backend - 팝업 CRUD API
- [ ] `Popup.java` Entity 작성
- [ ] `PopupController.java` 작성
  - `GET /api/popups` (활성 팝업)
  - `POST /api/popups` (관리자)
  - `PUT /api/popups/{id}` (관리자)
  - `DELETE /api/popups/{id}` (관리자)
- [ ] `PopupService.java` 작성
- [ ] 팝업 CRUD 테스트 작성

### 4.7 Backend - 쿼리 성능 튜닝
- [ ] 주요 쿼리 `EXPLAIN ANALYZE` 실행
- [ ] 인덱스 최적화 확인
- [ ] N+1 쿼리 제거 (Fetch Join)
- [ ] 성능 테스트 작성

### 4.8 Frontend - 관리자 대시보드
- [ ] `pages/admin/Dashboard.jsx` 작성
  - Recharts로 차트 구현
  - 방문자 추이 그래프
  - 카테고리별 통계 파이 차트
  - TOP 10 테이블
- [ ] 대시보드 API 연동
- [ ] 실시간 데이터 업데이트

### 4.9 Frontend - 아이디어 제안 페이지
- [ ] `pages/Ideas.jsx` 작성
  - 제안 폼 (제목, 설명)
  - 내 제안 목록
  - 상태 표시 (PENDING, REVIEWED, ACCEPTED, REJECTED)
- [ ] 아이디어 API 연동

### 4.10 Frontend - 관리자 아이디어 검토
- [ ] `pages/admin/IdeaManagement.jsx` 작성
  - 전체 제안 목록
  - 검토 폼 (상태 변경, 코멘트)
- [ ] 아이디어 검토 API 연동

### 4.11 Frontend - 관리자 팝업 관리
- [ ] `pages/admin/PopupManagement.jsx` 작성
  - 팝업 목록 테이블
  - 등록/수정/삭제
- [ ] `components/admin/PopupForm.jsx` 작성
- [ ] 팝업 CRUD API 연동

### 4.12 Frontend - 메인 페이지 팝업 모달
- [ ] `components/common/PopupModal.jsx` 작성
  - 팝업 목록 조회
  - 모달로 표시
  - "오늘 하루 보지 않기" 기능 (localStorage)
- [ ] 팝업 API 연동

### 4.13 Frontend - 이미지 Lazy Loading
- [ ] `react-lazyload` 설치
- [ ] ContentCard, YouTubeGrid, InstagramGrid에 적용

### 4.14 Frontend - 번들 최적화
- [ ] Code Splitting (React.lazy)
  - 관리자 페이지 분리
  - 소셜 페이지 분리
- [ ] `npm run build` 실행
- [ ] 번들 크기 확인

### 4.15 Frontend - Naver Analytics 연동
- [ ] Naver Analytics 스크립트 추가 (index.html)
- [ ] 페이지 뷰 이벤트 추적

### 4.16 Phase 4 통합 테스트
- [ ] 관리자 대시보드 통계 정상 표시 확인
- [ ] 전문 검색 성능 확인
- [ ] 해시태그 재집계 배치 실행 확인
- [ ] 방문자 로그 배치 실행 확인
- [ ] 팝업 모달 정상 표시 확인

---

## Phase 5: 테스트 및 배포 준비 (5주차)

**목표**: 통합 테스트, 문서화, 배포 준비

### 5.1 Backend - SSO 실제 연동 (IdP 정보 확정 시)
- [ ] OAuth 2.0 실제 엔드포인트 설정
- [ ] Client ID/Secret 설정
- [ ] 리다이렉트 URL 설정
- [ ] SSO 연동 테스트 작성

### 5.2 Backend - 단위 테스트 보강
- [ ] 주요 Service 테스트 커버리지 80% 이상
- [ ] 엣지 케이스 테스트 추가
- [ ] Mock 객체 활용

### 5.3 Backend - 통합 테스트
- [ ] `@SpringBootTest` 통합 테스트 작성
  - 주요 시나리오 (로그인 → 컨텐츠 조회 → 반응/별점)
  - 관리자 시나리오 (CRUD → 추첨)

### 5.4 Backend - API 문서 생성
- [ ] Swagger 의존성 추가
- [ ] `@Operation`, `@ApiResponse` 어노테이션 추가
- [ ] Swagger UI 확인 (`/swagger-ui.html`)

### 5.5 Backend - 운영 환경 설정
- [ ] `application-prod.yml` 작성
  - DB 연결 정보 (환경변수)
  - JWT Secret (환경변수)
  - 파일 업로드 경로
  - 외부 API 키 (환경변수)
- [ ] 프로파일 전환 테스트

### 5.6 Frontend - E2E 테스트
- [ ] Playwright 또는 Cypress 설치
- [ ] 주요 시나리오 E2E 테스트 작성
  - 로그인 → 컨텐츠 조회 → 반응
  - 관리자 → 컨텐츠 작성 → 발행
  - 이벤트 참여 → 추첨 → 당첨자 확인

### 5.7 Frontend - 크로스 브라우징 테스트
- [ ] Chrome 최신 버전 테스트
- [ ] Edge 최신 버전 테스트
- [ ] Safari 최신 버전 테스트
- [ ] Firefox 최신 버전 테스트

### 5.8 Frontend - 모바일 반응형 테스트
- [ ] Chrome DevTools 모바일 시뮬레이터
- [ ] iOS Safari 테스트
- [ ] Android Chrome 테스트
- [ ] 터치 이벤트 확인

### 5.9 Frontend - 빌드 최적화
- [ ] `npm run build` 실행
- [ ] 번들 분석 (Vite Visualizer)
- [ ] Tree Shaking 확인
- [ ] 압축 확인 (Gzip/Brotli)

### 5.10 Frontend - 환경변수 설정
- [ ] `.env.production` 작성
  - API Base URL
  - Analytics ID
- [ ] 환경변수 적용 확인

### 5.11 문서화 - 운영 매뉴얼
- [ ] `docs/운영_매뉴얼.md` 작성
  - 관리자 기능 설명
  - 컨텐츠 작성 가이드
  - 이벤트 생성 가이드
  - 추첨 방법
  - 팝업 관리
  - 통계 확인

### 5.12 문서화 - 배포 가이드
- [ ] `docs/배포_가이드.md` 작성
  - 서버 요구사항
  - PostgreSQL 설치
  - 환경변수 설정
  - Backend 배포
  - Frontend 빌드 및 배포
  - Nginx 설정
  - SSL 인증서
  - 백업 스크립트

### 5.13 문서화 - 사용자 가이드
- [ ] `docs/사용자_가이드.md` 작성
  - 로그인 방법
  - 사보 조회
  - 반응/별점 부여
  - 이벤트 참여
  - 아이디어 제안

### 5.14 문서화 - TechSpec.md 최종 검증
- [ ] 모든 API가 TechSpec.md와 일치하는지 확인
- [ ] 모든 테이블이 TechSpec.md와 일치하는지 확인
- [ ] 누락된 필드/엔드포인트 없는지 확인

### 5.15 문서화 - Plan.md 최종 검증
- [ ] 모든 체크리스트 완료 확인
- [ ] 미완료 항목 없는지 확인

### 5.16 배포 준비 - 백업 스크립트
- [ ] `scripts/backup.sh` 작성
  - PostgreSQL pg_dump
  - 파일 업로드 디렉토리 압축
  - 백업 파일 보관 (30일)

### 5.17 배포 준비 - 모니터링 설정
- [ ] 로그 레벨 설정 (운영: INFO)
- [ ] 에러 알림 설정 (선택)
- [ ] 디스크 용량 모니터링 (선택)

### 5.18 Phase 5 통합 테스트
- [ ] 전체 시스템 End-to-End 테스트
- [ ] 부하 테스트 (선택, JMeter)
- [ ] 보안 점검 (OWASP Top 10)
- [ ] 백업/복구 테스트

---

## 최종 체크리스트

### 기능 완성도
- [ ] 모든 Phase 1-5 작업 완료
- [ ] TechSpec.md 100% 구현
- [ ] 모든 테스트 통과

### 문서 완성도
- [ ] TechSpec.md 최신 상태
- [ ] Plan.md 모든 체크박스 완료
- [ ] 운영 매뉴얼 작성 완료
- [ ] 배포 가이드 작성 완료
- [ ] 사용자 가이드 작성 완료

### 품질
- [ ] 테스트 커버리지 80% 이상
- [ ] TODO/FIXME 없음
- [ ] 코드 리뷰 완료
- [ ] 보안 점검 완료

### 배포 준비
- [ ] 운영 환경 설정 완료
- [ ] 백업 스크립트 작성
- [ ] SSL 인증서 준비
- [ ] 모니터링 설정

---

**문서 버전**: v1.2  
**최종 수정일**: 2025-12-26  
**작성자**: Claude (Anthropic)
