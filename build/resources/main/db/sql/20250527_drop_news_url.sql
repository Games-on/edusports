-- Drop url column from news table if it exists
ALTER TABLE news DROP COLUMN IF EXISTS url;
