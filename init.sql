-- Connect to the finedge database
\c finedge;

-- Create the users table
CREATE TABLE IF NOT EXISTS users (
    username VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255) NOT NULL
);

-- Optional: Add a default test user
-- INSERT INTO users (username, password) VALUES ('admin', 'password_hash_here') ON CONFLICT DO NOTHING;