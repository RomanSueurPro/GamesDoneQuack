CREATE UNIQUE INDEX IF NOT EXISTS ux_roles_is_default_true
ON roles (is_default_role)
WHERE is_default_role = true;