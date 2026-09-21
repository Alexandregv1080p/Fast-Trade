-- ============================================================
--  Fix BCrypt hash prefix: $2b$ → $2a$
--  Spring's BCryptPasswordEncoder only accepts $2a$ and $2y$.
--  The seed used Node.js bcrypt which generates $2b$ hashes,
--  causing passwordEncoder.matches() to always return false.
--  New hash: 'admin123' encoded with $2a$10$ (cost 10).
-- ============================================================

UPDATE admin_users
SET    password = '$2a$10$DkVmSwW.oY0H4GiaqWXUd.Se61zMB8n9zdfer6XgEIJqmTfZ6s7Gm'
WHERE  password LIKE '$2b$%';

UPDATE users
SET    password = '$2a$10$DkVmSwW.oY0H4GiaqWXUd.Se61zMB8n9zdfer6XgEIJqmTfZ6s7Gm'
WHERE  password LIKE '$2b$%';
