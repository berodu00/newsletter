CREATE TABLE daily_visit_log (
    visit_id BIGSERIAL PRIMARY KEY,
    ip_address VARCHAR(45) NOT NULL,
    user_agent VARCHAR(255),
    visit_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_daily_visit_time ON daily_visit_log(visit_time);
