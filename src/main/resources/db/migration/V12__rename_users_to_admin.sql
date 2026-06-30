-- V12__rename_users_to_admin.sql
ALTER TABLE users RENAME TO admin;

ALTER TABLE refresh_tokens RENAME COLUMN user_id TO admin_id;
ALTER INDEX idx_refresh_tokens_user RENAME TO idx_refresh_tokens_admin;

ALTER SEQUENCE users_id_seq RENAME TO admin_id_seq;
