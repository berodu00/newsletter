# 배포 가이드 (Deployment Guide)

본 문서는 고려아연 전자사보 시스템의 배포 방법을 설명합니다.

## 1. 사전 요구사항 (Prerequisites)
- **Java**: JDK 17 이상
- **Node.js**: v18.17.0 이상
- **Database**: PostgreSQL 14 이상
- **Browser**: Chrome, Edge 등 최신 브라우저

## 2. 환경 변수 설정 (Configuration)

### Backend (`application-prod.yml`)
운영 환경을 위한 설정 파일을 준비합니다.
```yaml
spring:
  profiles:
    active: prod
  datasource:
    url: jdbc:postgresql://localhost:5433/newsletter_prod
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate # 운영 환경에서는 create/update 지양

jwt:
  secret: ${JWT_SECRET_KEY} # 32자 이상 강력한 키 사용
```

### Frontend (`.env.production`)
```env
VITE_API_BASE_URL=http://api.koreazinc.co.kr/api/v1
VITE_ANALYTICS_ID=UA-XXXXXX-X
```

## 3. 빌드 (Build)

### Backend
```bash
cd backend
./gradlew clean bootJar
# 결과물: build/libs/magazine-0.0.1-SNAPSHOT.jar
```

### Frontend
```bash
cd frontend
npm install
npm run build
# 결과물: dist/ 폴더 (정적 파일)
```

## 4. 실행 (Running)

### Backend
```bash
java -jar -Dspring.profiles.active=prod build/libs/magazine-0.0.1-SNAPSHOT.jar
```

### Frontend
Nginx 또는 Apache 웹 서버를 사용하여 `dist/` 폴더를 호스팅합니다.
**Nginx 설정 예시**:
```nginx
server {
    listen 80;
    server_name newsletter.koreazinc.co.kr;

    location / {
        root /path/to/dist;
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://localhost:8080;
    }
}
```

## 5. API 문서 (Swagger)
배포 후 API 문서는 다음 주소에서 확인 가능합니다.
- `http://localhost:8080/swagger-ui/index.html`

## 6. 백업 및 유지보수
- **DB 백업**: `pg_dump -U postgres newsletter_prod > backup_YYYYMMDD.sql`
- **로그 확인**: `logs/application.log`
