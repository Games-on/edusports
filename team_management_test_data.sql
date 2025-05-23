-- Team Management Test Data
-- Insert test data for testing Team Management APIs

-- Sample roles (if not exists)
INSERT IGNORE INTO roles (id, name, description) VALUES 
(1, 'ADMIN', 'Administrator role'),
(2, 'ORGANIZER', 'Tournament organizer role'),
(3, 'USER', 'Regular user role');

-- Sample users (if not exists)
INSERT IGNORE INTO users (id, name, email, password, is_active, created_at) VALUES
(1, 'Admin User', 'admin@edusports.com', '$2a$10$encoded_password', true, UNIX_TIMESTAMP() * 1000),
(2, 'Nguyễn Văn A', 'nguyenvana@example.com', '$2a$10$encoded_password', true, UNIX_TIMESTAMP() * 1000),
(3, 'Nguyễn Văn B', 'nguyenvanb@example.com', '$2a$10$encoded_password', true, UNIX_TIMESTAMP() * 1000),
(4, 'Nguyễn Văn C', 'nguyenvanc@example.com', '$2a$10$encoded_password', true, UNIX_TIMESTAMP() * 1000);

-- Assign roles to users
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES
(1, 1), -- Admin
(2, 3), -- User
(3, 3), -- User  
(4, 3); -- User

-- Sample tournament
INSERT IGNORE INTO tournaments (id, name, sport_type, description, max_teams, current_teams, start_date, end_date, location, registration_deadline, status, created_at, created_by) VALUES
(1, 'Giải Cầu lông Sinh viên 2025', 'Cầu lông', 'Giải đấu cầu lông dành cho sinh viên', 16, 0, '2025-06-01 08:00:00', '2025-06-03 18:00:00', 'Sân thể thao trường ĐH', '2025-05-30 23:59:59', 'REGISTRATION', UNIX_TIMESTAMP() * 1000, 1);

-- Sample teams (for testing get operations)
INSERT IGNORE INTO teams (id, tournament_id, name, team_color, member_count, captain_user_id, contact_info, status, logo_url, created_at, created_by) VALUES
(1, 1, 'Team Alpha', '#FF0000', 5, 2, 'captain@teama.com', 'ACTIVE', 'https://example.com/logo1.png', UNIX_TIMESTAMP() * 1000, 2),
(2, 1, 'Team Beta', '#0000FF', 4, 3, 'captain@teamb.com', 'ACTIVE', 'https://example.com/logo2.png', UNIX_TIMESTAMP() * 1000, 3);

-- Sample tournament registrations
INSERT IGNORE INTO tournament_registrations (id, tournament_id, user_id, status, registration_date, notes, created_at, created_by) VALUES
(1, 1, 2, 'APPROVED', '2025-05-23 12:00:00', 'Đội có kinh nghiệm thi đấu', UNIX_TIMESTAMP() * 1000, 2),
(2, 1, 3, 'APPROVED', '2025-05-23 13:00:00', 'Đội mới thành lập', UNIX_TIMESTAMP() * 1000, 3);

-- Sample matches (for testing team details)
INSERT IGNORE INTO matches (id, tournament_id, round_number, round_name, team1_id, team2_id, match_date, location, status, created_at, created_by) VALUES
(1, 1, 1, 'Vòng 1', 1, 2, '2025-06-01 08:00:00', 'Sân 1', 'SCHEDULED', UNIX_TIMESTAMP() * 1000, 1);

-- Update tournament current teams count
UPDATE tournaments SET current_teams = 2 WHERE id = 1;
