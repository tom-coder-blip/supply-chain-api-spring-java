INSERT INTO users (email, password_hash, status, created_at, updated_at)
VALUES (
           'admin@logisticshub.com',
           '$2a$10$Mo0kNnjAqhHK3zwVDT6wKOAcgEQJAOayrtSlaHC7gCTAlKKTT4abm',
           'ACTIVE',
           CURRENT_TIMESTAMP,
           CURRENT_TIMESTAMP
       );

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'admin@logisticshub.com'
  AND r.name = 'ADMIN';
