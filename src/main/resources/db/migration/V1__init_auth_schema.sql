-- V1__init_auth_schema.sql
-- Initial schema for authentication, RBAC, and audit logging

-- USERS
CREATE TABLE users (
                       id              BIGSERIAL PRIMARY KEY,
                       email           VARCHAR(255) NOT NULL UNIQUE,
                       password_hash   VARCHAR(255) NOT NULL,
                       status          VARCHAR(50) DEFAULT 'ACTIVE',
                       created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ROLES
CREATE TABLE roles (
                       id          BIGSERIAL PRIMARY KEY,
                       name        VARCHAR(100) NOT NULL UNIQUE,
                       description VARCHAR(255)
);

-- USER_ROLES (many-to-many)
CREATE TABLE user_roles (
                            user_id BIGINT NOT NULL,
                            role_id BIGINT NOT NULL,
                            PRIMARY KEY (user_id, role_id),
                            CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- PERMISSIONS (optional for fine-grained RBAC)
CREATE TABLE permissions (
                             id          BIGSERIAL PRIMARY KEY,
                             name        VARCHAR(100) NOT NULL UNIQUE,
                             description VARCHAR(255)
);

-- ROLE_PERMISSIONS (many-to-many)
CREATE TABLE role_permissions (
                                  role_id BIGINT NOT NULL,
                                  permission_id BIGINT NOT NULL,
                                  PRIMARY KEY (role_id, permission_id),
                                  CONSTRAINT fk_role_perm FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
                                  CONSTRAINT fk_perm FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- AUDIT LOGS
CREATE TABLE audit_logs (
                            id          BIGSERIAL PRIMARY KEY,
                            actor_id    BIGINT,
                            action      VARCHAR(255) NOT NULL,
                            entity_type VARCHAR(100),
                            entity_id   BIGINT,
                            metadata    JSONB,
                            timestamp   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            CONSTRAINT fk_actor FOREIGN KEY (actor_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Seed initial roles
INSERT INTO roles (name, description) VALUES
                                          ('ADMIN', 'System administrator with full access'),
                                          ('SUPPLIER', 'Supplier who can declare and update stock'),
                                          ('WAREHOUSE_MANAGER', 'Warehouse manager who manages inventory and shipments'),
                                          ('CUSTOMER', 'Customer who can place and track orders');
