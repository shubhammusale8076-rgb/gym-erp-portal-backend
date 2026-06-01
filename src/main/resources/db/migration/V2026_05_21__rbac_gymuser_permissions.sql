-- RBAC: permissions, role-permission mapping, member/trainer user links, member backfill

CREATE TABLE IF NOT EXISTS gym_permission (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    permission_code VARCHAR(255) NOT NULL UNIQUE,
    permission_description VARCHAR(512) NOT NULL
);

CREATE TABLE IF NOT EXISTS gym_role_permission (
    role_id UUID NOT NULL REFERENCES gym_authority(id) ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES gym_permission(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

INSERT INTO gym_permission (permission_code, permission_description) VALUES
    ('CREATE_MEMBER', 'Create gym members'),
    ('UPDATE_MEMBER', 'Update gym members'),
    ('DELETE_MEMBER', 'Delete gym members'),
    ('VIEW_PAYMENTS', 'View payment records'),
    ('MARK_ATTENDANCE', 'Mark member attendance'),
    ('UPDATE_WORKOUT', 'Update workout plans'),
    ('MANAGE_ROLES', 'Manage roles and permissions'),
    ('MANAGE_USERS', 'Manage staff users')
ON CONFLICT (permission_code) DO NOTHING;

ALTER TABLE gym_members ADD COLUMN IF NOT EXISTS user_id UUID UNIQUE REFERENCES gym_users(id);
ALTER TABLE trainers ADD COLUMN IF NOT EXISTS user_id UUID UNIQUE REFERENCES gym_users(id);

-- Drop global email unique on gym_users if present (tenant-scoped email)
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'ukgym_users_email' OR conname LIKE '%gym_users%email%'
    ) THEN
        ALTER TABLE gym_users DROP CONSTRAINT IF EXISTS ukgym_users_email;
    END IF;
EXCEPTION WHEN OTHERS THEN
    NULL;
END $$;

CREATE UNIQUE INDEX IF NOT EXISTS uq_gym_users_tenant_email ON gym_users (tenant_id, email);

-- Backfill gym_users for existing members with passwords (skip conflicts)
INSERT INTO gym_users (id, full_name, email, password, tenant_id, enabled, authority_id, created_on, updated_on, token_version)
SELECT
    gen_random_uuid(),
    m.full_name,
    LOWER(TRIM(m.email)),
    m.password,
    m.tenant_id,
    COALESCE(m.active, false),
    r.id,
    COALESCE(m.created_on, NOW()),
    COALESCE(m.updated_on, NOW()),
    0
FROM gym_members m
JOIN gym_authority r ON r.tenant_id = m.tenant_id AND r.role_code = 'MEMBER'
WHERE m.password IS NOT NULL
  AND m.user_id IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM gym_users u
      WHERE u.tenant_id = m.tenant_id AND LOWER(u.email) = LOWER(TRIM(m.email))
  );

UPDATE gym_members m
SET user_id = u.id
FROM gym_users u
WHERE m.user_id IS NULL
  AND u.tenant_id = m.tenant_id
  AND LOWER(u.email) = LOWER(TRIM(m.email))
  AND EXISTS (
      SELECT 1 FROM gym_authority r
      WHERE r.id = u.authority_id AND r.role_code = 'MEMBER'
  );
