CREATE TABLE content_views_dedup (
  content_id    BIGINT NOT NULL REFERENCES contents(content_id) ON DELETE CASCADE,
  user_id       BIGINT NOT NULL REFERENCES users(user_id),
  viewed_bucket TIMESTAMP NOT NULL,
  created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(content_id, user_id, viewed_bucket)
);

CREATE INDEX idx_content_views_dedup_content ON content_views_dedup(content_id);
CREATE INDEX idx_content_views_dedup_created ON content_views_dedup(created_at);

CREATE TABLE audit_logs (
  audit_id      BIGSERIAL PRIMARY KEY,
  actor_user_id BIGINT REFERENCES users(user_id),
  action        VARCHAR(30) NOT NULL,
  entity_type   VARCHAR(50) NOT NULL,
  entity_id     BIGINT,
  before_json   TEXT,
  after_json    TEXT,
  created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_logs_entity 
  ON audit_logs(entity_type, entity_id, created_at DESC);
CREATE INDEX idx_audit_logs_actor 
  ON audit_logs(actor_user_id, created_at DESC);
CREATE INDEX idx_audit_logs_created 
  ON audit_logs(created_at DESC);
