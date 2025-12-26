-- Drop user_id as we are moving to system-wide logs
-- But first check if constraint exists
ALTER TABLE visitor_logs DROP CONSTRAINT IF EXISTS visitor_logs_user_id_visited_at_key;
ALTER TABLE visitor_logs DROP COLUMN IF EXISTS user_id;

-- Add visitor_count
ALTER TABLE visitor_logs ADD COLUMN IF NOT EXISTS visitor_count BIGINT DEFAULT 0;

-- Ensure unique date
ALTER TABLE visitor_logs ADD CONSTRAINT unique_visited_at UNIQUE (visited_at);
