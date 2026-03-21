INSERT INTO roles (name)
VALUES ('PRECUSTOMER')
ON CONFLICT (name) DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.name = 'CREATE_ACCOUNT'
WHERE r.name = 'PRECUSTOMER'
ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.name = 'CREATE_ACCOUNT'
WHERE r.name = 'CUSTOMER'
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT ur.user_id, pre_customer.id
FROM user_roles ur
JOIN roles registered_customer ON registered_customer.id = ur.role_id
JOIN roles pre_customer ON pre_customer.name = 'PRECUSTOMER'
WHERE registered_customer.name = 'REGISTERED_CUSTOMER'
ON CONFLICT DO NOTHING;

DELETE FROM user_roles ur
USING roles registered_customer
WHERE ur.role_id = registered_customer.id
  AND registered_customer.name = 'REGISTERED_CUSTOMER';

DELETE FROM roles
WHERE name = 'REGISTERED_CUSTOMER';

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uk_account_customer_type'
    ) THEN
        ALTER TABLE account_entity
            ADD CONSTRAINT uk_account_customer_type UNIQUE (customer_id, account_type);
    END IF;
END
$$;
