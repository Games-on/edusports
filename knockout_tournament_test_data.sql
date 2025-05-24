-- Knockout Tournament Test Data
-- Test data cho knockout tournament system

-- Clean up existing data for fresh test
DELETE FROM matches WHERE tournament_id = 1;
DELETE FROM tournament_registrations WHERE tournament_id = 1;
DELETE FROM teams WHERE tournament_id = 1;

-- Reset tournament status
UPDATE tournaments SET 
    status = 'REGISTRATION',
    winner_team_id = NULL,
    runner_up_team_id = NULL,
    current_teams = 0,
    end_date = NULL
WHERE id = 1;

-- Create 8 teams for clean knockout (power of 2)
INSERT INTO teams (id, tournament_id, name, team_color, member_count, captain_user_id, contact_info, status, logo_url, created_at, created_by) VALUES
(1, 1, 'Team Alpha', '#FF0000', 5, 2, 'captain@teama.com', 'ACTIVE', 'https://example.com/logo1.png', UNIX_TIMESTAMP() * 1000, 2),
(2, 1, 'Team Beta', '#0000FF', 4, 3, 'captain@teamb.com', 'ACTIVE', 'https://example.com/logo2.png', UNIX_TIMESTAMP() * 1000, 3),
(3, 1, 'Team Gamma', '#00FF00', 4, 4, 'captain@teamgamma.com', 'ACTIVE', 'https://example.com/logo3.png', UNIX_TIMESTAMP() * 1000, 4),
(4, 1, 'Team Delta', '#FFFF00', 6, 1, 'captain@teamdelta.com', 'ACTIVE', 'https://example.com/logo4.png', UNIX_TIMESTAMP() * 1000, 1),
(5, 1, 'Team Epsilon', '#FF00FF', 5, 2, 'captain@teamepsilon.com', 'ACTIVE', 'https://example.com/logo5.png', UNIX_TIMESTAMP() * 1000, 2),
(6, 1, 'Team Zeta', '#00FFFF', 4, 3, 'captain@teamzeta.com', 'ACTIVE', 'https://example.com/logo6.png', UNIX_TIMESTAMP() * 1000, 3),
(7, 1, 'Team Eta', '#FFA500', 5, 4, 'captain@teameta.com', 'ACTIVE', 'https://example.com/logo7.png', UNIX_TIMESTAMP() * 1000, 4),
(8, 1, 'Team Theta', '#800080', 6, 1, 'captain@teamtheta.com', 'ACTIVE', 'https://example.com/logo8.png', UNIX_TIMESTAMP() * 1000, 1);

-- Create tournament registrations for all teams
INSERT INTO tournament_registrations (id, tournament_id, user_id, status, registration_date, notes, created_at, created_by) VALUES
(1, 1, 2, 'APPROVED', '2025-05-23 12:00:00', 'Team Alpha registration', UNIX_TIMESTAMP() * 1000, 2),
(2, 1, 3, 'APPROVED', '2025-05-23 13:00:00', 'Team Beta registration', UNIX_TIMESTAMP() * 1000, 3),
(3, 1, 4, 'APPROVED', '2025-05-23 14:00:00', 'Team Gamma registration', UNIX_TIMESTAMP() * 1000, 4),
(4, 1, 1, 'APPROVED', '2025-05-23 15:00:00', 'Team Delta registration', UNIX_TIMESTAMP() * 1000, 1),
(5, 1, 2, 'APPROVED', '2025-05-23 16:00:00', 'Team Epsilon registration', UNIX_TIMESTAMP() * 1000, 2),
(6, 1, 3, 'APPROVED', '2025-05-23 17:00:00', 'Team Zeta registration', UNIX_TIMESTAMP() * 1000, 3),
(7, 1, 4, 'APPROVED', '2025-05-23 18:00:00', 'Team Eta registration', UNIX_TIMESTAMP() * 1000, 4),
(8, 1, 1, 'APPROVED', '2025-05-23 19:00:00', 'Team Theta registration', UNIX_TIMESTAMP() * 1000, 1);

-- Update tournament team count
UPDATE tournaments SET current_teams = 8 WHERE id = 1;

-- Now tournament is ready for bracket generation
-- Use API: POST /api/tournaments/1/generate-bracket

-- For testing completed rounds, you can manually insert some matches with results:
-- This is just for testing the advance round functionality

/*
-- Example: Insert completed Round 1 matches (uncomment to test advance functionality)
INSERT INTO matches (id, tournament_id, round_number, round_name, team1_id, team2_id, match_date, location, status, team1_score, team2_score, winner_team_id, match_number, referee, created_at, created_by) VALUES
(1, 1, 1, 'Vòng 1', 1, 2, '2025-06-01 08:00:00', 'Sân 1', 'COMPLETED', 2, 1, 1, 1, 'Trọng tài A', UNIX_TIMESTAMP() * 1000, 1),
(2, 1, 1, 'Vòng 1', 3, 4, '2025-06-01 10:00:00', 'Sân 2', 'COMPLETED', 1, 2, 4, 2, 'Trọng tài B', UNIX_TIMESTAMP() * 1000, 1),
(3, 1, 1, 'Vòng 1', 5, 6, '2025-06-01 12:00:00', 'Sân 3', 'COMPLETED', 3, 0, 5, 3, 'Trọng tài C', UNIX_TIMESTAMP() * 1000, 1),
(4, 1, 1, 'Vòng 1', 7, 8, '2025-06-01 14:00:00', 'Sân 4', 'COMPLETED', 0, 2, 8, 4, 'Trọng tài D', UNIX_TIMESTAMP() * 1000, 1);

-- Create skeleton matches for Round 2 (semi-final)
INSERT INTO matches (id, tournament_id, round_number, round_name, team1_id, team2_id, match_date, location, status, team1_score, team2_score, winner_team_id, match_number, referee, created_at, created_by) VALUES
(5, 1, 2, 'Bán kết', NULL, NULL, '2025-06-02 14:00:00', 'Sân chính', 'SCHEDULED', 0, 0, NULL, 1, 'Trọng tài chính', UNIX_TIMESTAMP() * 1000, 1),
(6, 1, 2, 'Bán kết', NULL, NULL, '2025-06-02 16:00:00', 'Sân chính', 'SCHEDULED', 0, 0, NULL, 2, 'Trọng tài chính', UNIX_TIMESTAMP() * 1000, 1);

-- Create skeleton match for Round 3 (final)
INSERT INTO matches (id, tournament_id, round_number, round_name, team1_id, team2_id, match_date, location, status, team1_score, team2_score, winner_team_id, match_number, referee, created_at, created_by) VALUES
(7, 1, 3, 'Chung kết', NULL, NULL, '2025-06-03 16:00:00', 'Sân chính', 'SCHEDULED', 0, 0, NULL, 1, 'Trọng tài chung kết', UNIX_TIMESTAMP() * 1000, 1);

-- Update tournament status to ONGOING for testing advance round
UPDATE tournaments SET status = 'ONGOING' WHERE id = 1;
*/

-- Test Data Summary:
-- 1. Tournament with 8 teams (power of 2) - perfect for knockout
-- 2. All teams have ACTIVE status
-- 3. Tournament status = REGISTRATION (ready for bracket generation)
-- 4. Use APIs in this order:
--    a) POST /api/tournaments/1/generate-bracket (generate full bracket)
--    b) POST /api/tournaments/1/start-knockout (start tournament)
--    c) PUT /api/matches/{id}/score (update match results)
--    d) POST /api/tournaments/1/advance-round (advance when round completes)
--    e) POST /api/tournaments/1/complete (complete tournament)
