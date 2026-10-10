-- Seed Users for EstateX User Management System
-- Demonstrates the 3 system roles using role-identifying username formats (e.g. name@user, name@agent, name@admin)
-- All users have distinct unique passwords and unique usernames:

-- 1. Standard Customer User (format: name@user -> navigates to customer portal: ../user/dashboard.html)
INSERT INTO users (username, password, email, full_name, phone, role)
SELECT 'dilshan@user', '$2a$10$MD8eQKpnijwO7jA0YkA4huwhw1Rp2P6GsbklgHv0vjUncES5U/sFu', 'dilshan.perera@gmail.com', 'Dilshan Perera', '+94 77 123 4567', 'CUSTOMER'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'dilshan@user');

-- 2. Certified Real Estate Agent (format: name@agent -> navigates to agent portal: ../agent/dashboard.html)
INSERT INTO users (username, password, email, full_name, phone, role)
SELECT 'chaminda@agent', '$2a$10$JDnaY5HWbKJakc95PWLVI.yVMWhD.3Lpn8PBFuS3TY02YcZ19Qcla', 'chaminda.agent@estatex.lk', 'Chaminda Wijesekara', '+94 71 888 9900', 'AGENT'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'chaminda@agent');

-- 3. System Administrator (format: name@admin -> navigates to admin portal: ../admin/dashboard.html)
INSERT INTO users (username, password, email, full_name, phone, role)
SELECT 'admin@admin', '$2a$10$leUbl1uZXQ/faYDIOHB1G.co1cnaYdVQnwhgwgxKuvuFUqyIPX2Ra', 'admin@estatex.lk', 'System Administrator', '+94 11 234 5678', 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin@admin');

-- 4. Test Agent (format: name@agent)
INSERT INTO users (username, password, email, full_name, phone, role)
SELECT 'john@agent', '$2a$10$JDnaY5HWbKJakc95PWLVI.yVMWhD.3Lpn8PBFuS3TY02YcZ19Qcla', 'john.agent@estatex.lk', 'John Agent', '+94 71 555 6666', 'AGENT'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'john@agent');

-- 5. Test Admin (format: name@admin)
INSERT INTO users (username, password, email, full_name, phone, role)
SELECT 'john@admin', '$2a$10$leUbl1uZXQ/faYDIOHB1G.co1cnaYdVQnwhgwgxKuvuFUqyIPX2Ra', 'john.admin@estatex.lk', 'John Admin', '+94 11 555 6666', 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'john@admin');
