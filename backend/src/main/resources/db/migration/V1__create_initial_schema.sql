-- V1__create_initial_schema.sql

-- 1. Users Table
CREATE TABLE users (
  user_id        BIGSERIAL PRIMARY KEY,
  username       VARCHAR(100) NOT NULL UNIQUE,
  name           VARCHAR(100) NOT NULL,
  email          VARCHAR(255),
  department     VARCHAR(100),
  role           VARCHAR(20) NOT NULL DEFAULT 'USER',
  is_active      BOOLEAN NOT NULL DEFAULT TRUE,
  created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by     BIGINT,
  updated_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by     BIGINT,
  last_login_at  TIMESTAMP,
  deleted_at     TIMESTAMP,
  deleted_by     BIGINT
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_role ON users(role) WHERE deleted_at IS NULL;

-- 2. Resource Files Table
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

-- 3. Categories Table
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

-- Initial Categories
INSERT INTO categories (category_name, display_order) VALUES
('Special', 1),
('People', 2),
('Life', 3);

-- 4. Contents Table
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
  CONSTRAINT chk_status CHECK (status IN ('DRAFT', 'PUBLISHED', 'SCHEDULED', 'ARCHIVED'))
);

CREATE INDEX idx_contents_category_status_pub ON contents(category_id, status, published_at DESC) WHERE deleted_at IS NULL;
CREATE INDEX idx_contents_status_pub ON contents(status, published_at DESC) WHERE deleted_at IS NULL;
CREATE INDEX idx_contents_view_count ON contents(view_count DESC) WHERE status = 'PUBLISHED' AND deleted_at IS NULL;

-- 5. Hashtags Table
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

-- 6. Content Hashtags Table
CREATE TABLE content_hashtags (
  content_id     BIGINT NOT NULL REFERENCES contents(content_id) ON DELETE CASCADE,
  hashtag_id     BIGINT NOT NULL REFERENCES hashtags(hashtag_id) ON DELETE RESTRICT,
  created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_by     BIGINT REFERENCES users(user_id),
  PRIMARY KEY(content_id, hashtag_id)
);

CREATE INDEX idx_content_hashtags_hashtag ON content_hashtags(hashtag_id);

-- 7. Ratings Table
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

-- 8. Reactions Table
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

-- 9. Social Contents Table
CREATE TABLE social_contents (
  social_content_id BIGSERIAL PRIMARY KEY,
  platform          VARCHAR(20) NOT NULL,
  external_id       VARCHAR(255) NOT NULL,
  title             VARCHAR(255),
  description       TEXT,
  thumbnail_url     VARCHAR(500),
  media_url         VARCHAR(500),
  published_at      TIMESTAMP,
  fetched_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  is_active         BOOLEAN NOT NULL DEFAULT TRUE,
  deleted_at        TIMESTAMP,
  deleted_by        BIGINT REFERENCES users(user_id),
  UNIQUE(platform, external_id),
  CONSTRAINT chk_platform CHECK (platform IN ('YOUTUBE', 'INSTAGRAM'))
);

CREATE INDEX idx_social_contents_platform_pub ON social_contents(platform, published_at DESC) WHERE is_active = TRUE AND deleted_at IS NULL;

-- 10. Events Table
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
  deleted_at      TIMESTAMP,
  deleted_by      BIGINT REFERENCES users(user_id),
  CONSTRAINT chk_event_period CHECK (end_at > start_at),
  CONSTRAINT chk_event_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'CLOSED'))
);

CREATE INDEX idx_events_status_period ON events(status, start_at, end_at) WHERE deleted_at IS NULL;

-- 11. Event Participants Table
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

-- 12. Popups Table
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

CREATE INDEX idx_popups_active_period ON popups(is_active, start_at, end_at, display_order);

-- 13. Ideas Table
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

CREATE INDEX idx_ideas_status_created ON ideas(status, created_at DESC) WHERE deleted_at IS NULL;

-- 14. Visitor Logs Table
CREATE TABLE visitor_logs (
  log_id        BIGSERIAL PRIMARY KEY,
  user_id       BIGINT REFERENCES users(user_id),
  visited_at    DATE NOT NULL,
  page_views    INT DEFAULT 1,
  UNIQUE(user_id, visited_at)
);

CREATE INDEX idx_visitor_logs_date ON visitor_logs(visited_at DESC);

-- Self-reference Foreign Keys for Users (created_by, etc) added after table creation implicitly by order, but circular refs need care.
-- In Postgres, we can define FKs inline even if table refers to itself, as long as it exists.
-- But references to 'users' in 'users' table are valid since we defined it first.

-- Initial Data
-- Admin User
INSERT INTO users (username, name, email, department, role, is_active) VALUES
('admin', 'system admin', 'admin@kz.com', 'IT', 'ADMIN', true);

-- Sample Users
INSERT INTO users (username, name, email, department, role, is_active) VALUES
('user1', 'hong gildong', 'hong@kz.com', 'Sales', 'USER', true),
('user2', 'kim chulsoo', 'kim@kz.com', 'Marketing', 'USER', true);
