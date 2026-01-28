-- V18__reset_demo_passwords.sql

-- Reset password for bell_supplier
UPDATE users
SET password_hash = '$2a$10$s2ZPmXCefZPUOLkcF8aZq.ZV./Fx78Zxdld2.zA7TEtfj5Q63B4wW'
WHERE id = 21;

-- Reset password for durban_manager
UPDATE users
SET password_hash = '$2a$10$s2ZPmXCefZPUOLkcF8aZq.ZV./Fx78Zxdld2.zA7TEtfj5Q63B4wW'
WHERE id = 22;

-- Reset password for buildcorp_client
UPDATE users
SET password_hash = '$2a$10$s2ZPmXCefZPUOLkcF8aZq.ZV./Fx78Zxdld2.zA7TEtfj5Q63B4wW'
WHERE id = 23;

-- Reset password for roadworks_client
UPDATE users
SET password_hash = '$2a$10$s2ZPmXCefZPUOLkcF8aZq.ZV./Fx78Zxdld2.zA7TEtfj5Q63B4wW'
WHERE id = 24;
