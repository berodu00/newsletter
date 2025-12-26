CREATE TABLE dept_stats (
  stat_id        BIGSERIAL PRIMARY KEY,
  department     VARCHAR(100) NOT NULL,
  stat_date      DATE NOT NULL,
  user_count     BIGINT NOT NULL DEFAULT 0,
  content_count  BIGINT NOT NULL DEFAULT 0,
  view_count     BIGINT NOT NULL DEFAULT 0,
  created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE(department, stat_date)
);

CREATE INDEX idx_dept_stats_date ON dept_stats(stat_date DESC);
