-- V14__seed_demo_users.sql

-- Bell Equipment Supplier
INSERT INTO users (id, username, email, password_hash, status, primary_role)
VALUES (21, 'bell_supplier', 'supplier@bell.co.za', '$2a$10$XxBellHashExample1234567890', 'ACTIVE', 'SUPPLIER');

INSERT INTO user_roles (user_id, role_id)
VALUES (21, (SELECT id FROM roles WHERE name = 'SUPPLIER'));

-- Warehouse Manager
INSERT INTO users (id, username, email, password_hash, status, primary_role)
VALUES (22, 'durban_manager', 'manager@durbanhub.co.za', '$2a$10$XxManagerHashExample1234567890', 'ACTIVE', 'WAREHOUSE_MANAGER');

INSERT INTO user_roles (user_id, role_id)
VALUES (22, (SELECT id FROM roles WHERE name = 'WAREHOUSE_MANAGER'));

-- Construction Customer 1
INSERT INTO users (id, username, email, password_hash, status, primary_role)
VALUES (23, 'buildcorp_client', 'client@buildcorp.co.za', '$2a$10$XxClientHashExample1234567890', 'ACTIVE', 'CUSTOMER');

INSERT INTO user_roles (user_id, role_id)
VALUES (23, (SELECT id FROM roles WHERE name = 'CUSTOMER'));

-- Construction Customer 2
INSERT INTO users (id, username, email, password_hash, status, primary_role)
VALUES (24, 'roadworks_client', 'client@roadworks.co.za', '$2a$10$XxClientHashExample0987654321', 'ACTIVE', 'CUSTOMER');

INSERT INTO user_roles (user_id, role_id)
VALUES (24, (SELECT id FROM roles WHERE name = 'CUSTOMER'));
