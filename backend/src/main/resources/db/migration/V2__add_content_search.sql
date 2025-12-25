-- Create content_search table
CREATE TABLE content_search (
  content_id      BIGINT PRIMARY KEY REFERENCES contents(content_id) ON DELETE CASCADE,
  search_vector   tsvector NOT NULL
);

-- Create GIN index
CREATE INDEX idx_content_search_vector ON content_search USING GIN (search_vector);

-- Function to update search_vector
CREATE OR REPLACE FUNCTION update_content_search_vector() RETURNS trigger AS $$
BEGIN
  INSERT INTO content_search (content_id, search_vector)
  VALUES (
    NEW.content_id,
    setweight(to_tsvector('simple', coalesce(NEW.title, '')), 'A') ||
    setweight(to_tsvector('simple', coalesce(NEW.summary, '')), 'B') ||
    setweight(to_tsvector('simple', coalesce(NEW.body_text, '')), 'C')
  )
  ON CONFLICT (content_id) DO UPDATE
  SET search_vector = EXCLUDED.search_vector;
  RETURN NEW;
END
$$ LANGUAGE plpgsql;

-- Trigger on contents table
CREATE TRIGGER trg_content_search_update
AFTER INSERT OR UPDATE OF title, summary, body_text ON contents
FOR EACH ROW
EXECUTE FUNCTION update_content_search_vector();

-- Populate existing data
INSERT INTO content_search (content_id, search_vector)
SELECT 
  content_id,
  setweight(to_tsvector('simple', coalesce(title, '')), 'A') ||
  setweight(to_tsvector('simple', coalesce(summary, '')), 'B') ||
  setweight(to_tsvector('simple', coalesce(body_text, '')), 'C')
FROM contents;
