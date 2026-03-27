INSERT INTO members (username, password, role, created_date)
SELECT 'admin', '$2a$10$kJLfbvNNxvpz1irlsSWeoOwePW7OA8QaUgtr8xuqeEPye0wX9sEwm', 'ADMIN', NOW()
WHERE NOT EXISTS (SELECT 1 FROM members WHERE username = 'admin');

INSERT INTO members (username, password, role, created_date)
SELECT 'user', '$2a$10$ruevcYmNtJ1Pgy3Vfi15levYL.68/hAgqSF3hpYlRSUz970w2R9pu', 'USER', NOW()
WHERE NOT EXISTS (SELECT 1 FROM members WHERE username = 'user');
