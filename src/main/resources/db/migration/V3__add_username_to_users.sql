-- 1. Add column WITHOUT constraints first
ALTER TABLE users
    ADD COLUMN username VARCHAR(50);

-- 2. Populate username for existing users
UPDATE users
SET username = email
WHERE username IS NULL;

-- 3. Enforce NOT NULL
ALTER TABLE users
    ALTER COLUMN username SET NOT NULL;

-- 4. Enforce UNIQUE
ALTER TABLE users
    ADD CONSTRAINT uk_users_username UNIQUE (username);
