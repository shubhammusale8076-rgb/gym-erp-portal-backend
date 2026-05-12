-- V2024_05_10__Unified_Attendance.sql
-- Migration for Unified Enterprise Attendance Platform

-- 1. Update Attendance Table
ALTER TABLE attendance RENAME COLUMN member_id TO actor_id;
ALTER TABLE attendance ADD COLUMN actor_type VARCHAR(20) DEFAULT 'MEMBER';

-- 2. Update Attendance Audit Table
ALTER TABLE attendance_audit RENAME COLUMN member_id TO actor_id;
ALTER TABLE attendance_audit ADD COLUMN actor_type VARCHAR(20) DEFAULT 'MEMBER';

-- 3. Update Attendance Events Table
ALTER TABLE attendance_events RENAME COLUMN member_id TO actor_id;
ALTER TABLE attendance_events ADD COLUMN actor_type VARCHAR(20) DEFAULT 'MEMBER';

-- 4. Initial Migration of existing Member records
UPDATE attendance SET actor_type = 'MEMBER' WHERE actor_type IS NULL;
UPDATE attendance_audit SET actor_type = 'MEMBER' WHERE actor_type IS NULL;
UPDATE attendance_events SET actor_type = 'MEMBER' WHERE actor_type IS NULL;

-- 5. Add Constraints
ALTER TABLE attendance ALTER COLUMN actor_type SET NOT NULL;
ALTER TABLE attendance_audit ALTER COLUMN actor_type SET NOT NULL;
ALTER TABLE attendance_events ALTER COLUMN actor_type SET NOT NULL;

-- 6. Update Indexes
DROP INDEX IF EXISTS idx_att_member;
CREATE INDEX idx_att_actor ON attendance (actor_id, actor_type);
CREATE INDEX idx_audit_actor ON attendance_audit (actor_id, actor_type);
CREATE INDEX idx_evt_actor ON attendance_events (actor_id, actor_type);
