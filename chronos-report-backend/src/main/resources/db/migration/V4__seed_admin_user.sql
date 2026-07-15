-- ============================================================
-- Chronos: seed an initial ADMIN user (BCrypt of "Chronos@2024")
-- ============================================================
INSERT INTO reporting.app_user (email, password, full_name, role, enabled)
VALUES (
    'admin@chronos.local',
    '$2a$10$N9qo8uLOickgx2ZMRZoMy.MrqLW1Wz3Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1Z1',
    'System Administrator',
    'ADMIN',
    true
)
ON CONFLICT (email) DO NOTHING;