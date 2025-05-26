-- Backup script before running migration
-- Run this before starting the application

-- 1. Backup current matches data
CREATE TABLE matches_backup AS SELECT * FROM matches;

-- 2. Check current data
SELECT 
    COUNT(*) as total_matches,
    COUNT(team1_id) as team1_not_null,
    COUNT(team2_id) as team2_not_null
FROM matches;

-- 3. Show current constraints
SHOW CREATE TABLE matches;

-- To restore data if needed (ONLY if something goes wrong):
-- DROP TABLE matches;
-- RENAME TABLE matches_backup TO matches;
-- Then re-run the original schema creation
