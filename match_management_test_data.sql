-- Match Management Test Data
-- Insert additional test data for testing Match Management APIs

-- Additional teams for more comprehensive testing
INSERT IGNORE INTO teams (id, tournament_id, name, team_color, member_count, captain_user_id, contact_info, status, logo_url, created_at, created_by) VALUES
(3, 1, 'Team Gamma', '#00FF00', 4, 4, 'captain@teamgamma.com', 'ACTIVE', 'https://example.com/logo3.png', UNIX_TIMESTAMP() * 1000, 4),
(4, 1, 'Team Delta', '#FFFF00', 6, 1, 'captain@teamdelta.com', 'ACTIVE', 'https://example.com/logo4.png', UNIX_TIMESTAMP() * 1000, 1);

-- Additional tournament registrations
INSERT IGNORE INTO tournament_registrations (id, tournament_id, user_id, status, registration_date, notes, created_at, created_by) VALUES
(3, 1, 4, 'APPROVED', '2025-05-23 14:00:00', 'Đội mạnh', UNIX_TIMESTAMP() * 1000, 4),
(4, 1, 1, 'APPROVED', '2025-05-23 15:00:00', 'Đội admin', UNIX_TIMESTAMP() * 1000, 1);

-- Comprehensive match data for bracket testing
-- Round 1 (Vòng 1) - 4 matches
INSERT IGNORE INTO matches (id, tournament_id, round_number, round_name, team1_id, team2_id, match_date, location, status, team1_score, team2_score, winner_team_id, match_number, referee, created_at, created_by) VALUES
-- Match 1: Team Alpha vs Team Beta (completed)
(1, 1, 1, 'Vòng 1', 1, 2, '2025-06-01 08:00:00', 'Sân A1', 'COMPLETED', 2, 1, 1, 1, 'Trọng tài A', UNIX_TIMESTAMP() * 1000, 1),
-- Match 2: Team Gamma vs Team Delta (completed)
(2, 1, 1, 'Vòng 1', 3, 4, '2025-06-01 10:00:00', 'Sân A2', 'COMPLETED', 1, 3, 4, 2, 'Trọng tài B', UNIX_TIMESTAMP() * 1000, 1);

-- Round 2 (Bán kết) - 1 match (since we only have 4 teams)
INSERT IGNORE INTO matches (id, tournament_id, round_number, round_name, team1_id, team2_id, match_date, location, status, team1_score, team2_score, winner_team_id, match_number, referee, created_at, created_by) VALUES
-- Match 3: Team Alpha vs Team Delta (scheduled)
(3, 1, 2, 'Bán kết', 1, 4, '2025-06-02 14:00:00', 'Sân chính', 'SCHEDULED', 0, 0, NULL, 1, 'Trọng tài chính', UNIX_TIMESTAMP() * 1000, 1);

-- Round 3 (Chung kết) - will be created after semi-final
-- This match will have placeholder teams until semi-final is completed
INSERT IGNORE INTO matches (id, tournament_id, round_number, round_name, team1_id, team2_id, match_date, location, status, team1_score, team2_score, winner_team_id, match_number, referee, created_at, created_by) VALUES
-- Match 4: TBD vs TBD (waiting for semi-final result)
(4, 1, 3, 'Chung kết', NULL, NULL, '2025-06-03 16:00:00', 'Sân chính', 'SCHEDULED', 0, 0, NULL, 1, 'Trọng tài chính', UNIX_TIMESTAMP() * 1000, 1);

-- Additional matches for testing different statuses
INSERT IGNORE INTO matches (id, tournament_id, round_number, round_name, team1_id, team2_id, match_date, location, status, team1_score, team2_score, winner_team_id, match_number, referee, notes, created_at, created_by) VALUES
-- Live match example
(5, 1, 1, 'Vòng 1', 1, 3, '2025-06-01 12:00:00', 'Sân B1', 'LIVE', 1, 0, NULL, 3, 'Trọng tài C', 'Trận đấu đang diễn ra hấp dẫn', UNIX_TIMESTAMP() * 1000, 1),
-- Cancelled match example  
(6, 1, 1, 'Vòng 1', 2, 4, '2025-06-01 14:00:00', 'Sân B2', 'CANCELLED', 0, 0, NULL, 4, 'Trọng tài D', 'Hủy do thời tiết xấu', UNIX_TIMESTAMP() * 1000, 1);

-- Update tournament current teams count
UPDATE tournaments SET current_teams = 4 WHERE id = 1;

-- Sample data for a second tournament to test isolation
INSERT IGNORE INTO tournaments (id, name, sport_type, description, max_teams, current_teams, start_date, end_date, location, registration_deadline, status, created_at, created_by) VALUES
(2, 'Giải Bóng đá Sinh viên 2025', 'Bóng đá', 'Giải đấu bóng đá dành cho sinh viên', 8, 2, '2025-07-01 08:00:00', '2025-07-05 18:00:00', 'Sân bóng trường ĐH', '2025-06-25 23:59:59', 'REGISTRATION', UNIX_TIMESTAMP() * 1000, 1);

-- Teams for second tournament
INSERT IGNORE INTO teams (id, tournament_id, name, team_color, member_count, captain_user_id, contact_info, status, logo_url, created_at, created_by) VALUES
(5, 2, 'FC Alpha', '#FF0000', 11, 2, 'captain@fcalpha.com', 'ACTIVE', 'https://example.com/fc-logo1.png', UNIX_TIMESTAMP() * 1000, 2),
(6, 2, 'FC Beta', '#0000FF', 11, 3, 'captain@fcbeta.com', 'ACTIVE', 'https://example.com/fc-logo2.png', UNIX_TIMESTAMP() * 1000, 3);

-- Match for second tournament (to test isolation)
INSERT IGNORE INTO matches (id, tournament_id, round_number, round_name, team1_id, team2_id, match_date, location, status, team1_score, team2_score, winner_team_id, match_number, referee, created_at, created_by) VALUES
(7, 2, 1, 'Vòng 1', 5, 6, '2025-07-01 08:00:00', 'Sân chính', 'SCHEDULED', 0, 0, NULL, 1, 'Trọng tài bóng đá', UNIX_TIMESTAMP() * 1000, 1);
