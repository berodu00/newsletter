CREATE TABLE IF NOT EXISTS reactions (
  reaction_id   BIGSERIAL PRIMARY KEY,
  content_id    BIGINT NOT NULL REFERENCES contents(content_id) ON DELETE CASCADE,
  user_id       BIGINT NOT NULL REFERENCES users(user_id),
  reaction_type VARCHAR(20) NOT NULL,
  rating_value  INTEGER,
  created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE(content_id, user_id, reaction_type)
);

CREATE INDEX IF NOT EXISTS idx_reactions_content ON reactions(content_id);
