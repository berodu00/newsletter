-- V12__add_test_data.sql

-- 1. Add Mock Users
INSERT INTO users (username, name, email, department, role, is_active) VALUES
('hr_manager', 'Kim HR', 'hr@kz.com', 'HR', 'USER', true),
('fin_manager', 'Lee Finance', 'finance@kz.com', 'Finance', 'USER', true),
('plan_manager', 'Park Planning', 'plan@kz.com', 'Planning', 'USER', true);

-- 2. Add Mock Categories (if not exists, actually V1 has Special, People, Life)
-- We use existing IDs 1(Special), 2(People), 3(Life)

-- 3. Add Mock Contents
INSERT INTO contents (title, summary, body_html, category_id, status, author_id, published_at, view_count, rating_sum, rating_count, average_rating) VALUES
('HR Policy Update 2025', 'New policies for the upcoming year.', '<p>Details about 2025 HR policies...</p>', 3, 'PUBLISHED', (SELECT user_id FROM users WHERE username='hr_manager'), CURRENT_TIMESTAMP - INTERVAL '2 days', 150, 45, 10, 4.5),
('Q4 Financial Report', 'Summary of Q4 performance.', '<p>We achieved record breaking revenue...</p>', 1, 'PUBLISHED', (SELECT user_id FROM users WHERE username='fin_manager'), CURRENT_TIMESTAMP - INTERVAL '5 days', 320, 100, 20, 5.0),
('Upcoming Workshop', 'Planning team workshop schedule.', '<p>Join us for the annual strategy workshop...</p>', 2, 'DRAFT', (SELECT user_id FROM users WHERE username='plan_manager'), NULL, 0, 0, 0, 0.0),
('Employee Spotlight: Sarah', 'Meet our star performer.', '<p>Sarah has been with us for 5 years...</p>', 2, 'PUBLISHED', (SELECT user_id FROM users WHERE username='hr_manager'), CURRENT_TIMESTAMP - INTERVAL '10 days', 500, 200, 40, 5.0);

-- 4. Add Mock Events
INSERT INTO events (title, description, start_at, end_at, status, created_by) VALUES
('New Year Town Hall', 'Annual town hall meeting with CEO.', CURRENT_TIMESTAMP + INTERVAL '1 day', CURRENT_TIMESTAMP + INTERVAL '1 day' + INTERVAL '2 hours', 'ACTIVE', (SELECT user_id FROM users WHERE username='admin')),
('Health Checkup 2025', 'Mandatory health checkup for all employees.', CURRENT_TIMESTAMP + INTERVAL '10 days', CURRENT_TIMESTAMP + INTERVAL '20 days', 'ACTIVE', (SELECT user_id FROM users WHERE username='hr_manager')),
('Christmas Party 2024', 'Year-end celebration.', CURRENT_TIMESTAMP - INTERVAL '30 days', CURRENT_TIMESTAMP - INTERVAL '30 days' + INTERVAL '4 hours', 'CLOSED', (SELECT user_id FROM users WHERE username='admin'));

-- 5. Add Mock Social Contents
INSERT INTO social_contents (platform, external_id, title, description, published_at, is_active) VALUES
('YOUTUBE', 'vid_001', 'KZ Sustainability Vision', 'Our journey towards green energy.', CURRENT_TIMESTAMP - INTERVAL '3 days', true),
('YOUTUBE', 'vid_002', 'Global Operations Tour', 'Visiting our smelters around the world.', CURRENT_TIMESTAMP - INTERVAL '1 week', true),
('INSTAGRAM', 'ig_001', 'Team Building Day', 'Planning team having fun!', CURRENT_TIMESTAMP - INTERVAL '2 days', true),
('INSTAGRAM', 'ig_002', 'Best Safety Award', 'Celebrating safety milestones.', CURRENT_TIMESTAMP - INTERVAL '5 days', true);

-- 6. Add Mock Ideas
INSERT INTO ideas (user_id, title, description, status, created_at) VALUES
((SELECT user_id FROM users WHERE username='user1'), 'Cafeteria Menu improvement', 'We need more vegetarian options.', 'PENDING', CURRENT_TIMESTAMP - INTERVAL '1 day'),
((SELECT user_id FROM users WHERE username='user2'), 'Shuttle Bus Schedule', 'Add a stop at the subway station.', 'ACCEPTED', CURRENT_TIMESTAMP - INTERVAL '1 week');
