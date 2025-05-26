-- Verification script after migration
-- Run this after the application starts successfully

-- 1. Check data integrity
SELECT 
    COUNT(*) as total_matches,
    COUNT(team1_id) as team1_not_null,
    COUNT(team2_id) as team2_not_null,
    COUNT(*) - COUNT(team1_id) as team1_null_count,
    COUNT(*) - COUNT(team2_id) as team2_null_count
FROM matches;

-- 2. Verify constraints were modified
DESCRIBE matches;

-- 3. Check if we can insert null values now (test)
INSERT INTO matches (
    tournament_id, round_number, round_name, 
    team1_id, team2_id, match_date, status,
    team1_score, team2_score, created_at, created_by
) VALUES (
    1, 2, 'Test Round', 
    NULL, NULL, NOW(), 'SCHEDULED',
    0, 0, UNIX_TIMESTAMP() * 1000, 1
);

-- If the above insert works, the migration was successful
-- Clean up test data:
DELETE FROM matches WHERE round_name = 'Test Round';
