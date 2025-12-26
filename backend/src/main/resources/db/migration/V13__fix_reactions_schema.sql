-- V13__fix_reactions_schema.sql

-- 1. Add missing rating_value column if not exists
ALTER TABLE reactions ADD COLUMN IF NOT EXISTS rating_value INTEGER;

-- 2. Drop the old check constraint on reaction_type
ALTER TABLE reactions DROP CONSTRAINT IF EXISTS chk_reaction_type;

-- 3. Add new check constraint allowing 'RATING'
ALTER TABLE reactions ADD CONSTRAINT chk_reaction_type 
CHECK (reaction_type IN ('LIKE', 'SAD', 'ANGRY', 'FUNNY', 'RATING'));
