CREATE UNIQUE INDEX IF NOT EXISTS ux_roles_is_default_true
ON roles (is_default_role)
WHERE is_default_role = true;

CREATE UNIQUE INDEX IF NOT EXISTS ux_roles_is_admin_true
ON roles (is_admin_role)
WHERE is_admin_role = true;