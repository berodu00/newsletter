-- V6__add_updated_by_to_events.sql
ALTER TABLE events ADD COLUMN updated_by BIGINT REFERENCES users(user_id);
