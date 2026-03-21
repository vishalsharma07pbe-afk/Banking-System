CREATE SEQUENCE IF NOT EXISTS permissions_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS roles_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS users_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS branches (
    branch_id BIGSERIAL PRIMARY KEY,
    branch_code VARCHAR(255) NOT NULL UNIQUE,
    branch_name VARCHAR(255) NOT NULL,
    ifsc_code VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    city VARCHAR(255) NOT NULL,
    state VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL,
    postal_code VARCHAR(255) NOT NULL,
    status VARCHAR(255),
    created_at TIMESTAMP(6)
);

CREATE TABLE IF NOT EXISTS customers (
    customer_id BIGSERIAL PRIMARY KEY,
    customer_number VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(255) NOT NULL,
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    city VARCHAR(255) NOT NULL,
    state VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL,
    postal_code VARCHAR(255) NOT NULL,
    status VARCHAR(255),
    created_at TIMESTAMP(6)
);

CREATE TABLE IF NOT EXISTS employees (
    employee_id BIGSERIAL PRIMARY KEY,
    employee_code VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(255),
    role VARCHAR(255),
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP(6),
    branch_id BIGINT,
    CONSTRAINT fk_employees_branch
        FOREIGN KEY (branch_id) REFERENCES branches (branch_id)
);

CREATE TABLE IF NOT EXISTS permissions (
    id BIGINT PRIMARY KEY DEFAULT nextval('permissions_seq'),
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS roles (
    id BIGINT PRIMARY KEY DEFAULT nextval('roles_seq'),
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY DEFAULT nextval('users_seq'),
    user_name VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL,
    account_locked BOOLEAN NOT NULL,
    failed_login_attempts INTEGER NOT NULL,
    lock_until TIMESTAMP(6),
    post_lock_challenge_required BOOLEAN NOT NULL,
    admin_unlock_required BOOLEAN NOT NULL,
    account_expiry_date DATE NOT NULL,
    password_changed_at DATE NOT NULL,
    password_expiry_date DATE NOT NULL,
    customer_id BIGINT UNIQUE,
    employee_id BIGINT UNIQUE,
    CONSTRAINT fk_users_customer
        FOREIGN KEY (customer_id) REFERENCES customers (customer_id),
    CONSTRAINT fk_users_employee
        FOREIGN KEY (employee_id) REFERENCES employees (employee_id)
);

CREATE TABLE IF NOT EXISTS account_entity (
    account_id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(255) NOT NULL UNIQUE,
    account_type VARCHAR(255),
    balance NUMERIC(38, 2) NOT NULL,
    status VARCHAR(255),
    customer_id BIGINT NOT NULL,
    branch_id BIGINT NOT NULL,
    CONSTRAINT fk_accounts_customer
        FOREIGN KEY (customer_id) REFERENCES customers (customer_id),
    CONSTRAINT fk_accounts_branch
        FOREIGN KEY (branch_id) REFERENCES branches (branch_id)
);

CREATE TABLE IF NOT EXISTS transactions (
    transaction_id BIGSERIAL PRIMARY KEY,
    reference_number VARCHAR(255) NOT NULL UNIQUE,
    transaction_type VARCHAR(255),
    amount NUMERIC(38, 2),
    status VARCHAR(255),
    transaction_date TIMESTAMP(6),
    from_account_id BIGINT,
    to_account_id BIGINT,
    CONSTRAINT fk_transactions_from_account
        FOREIGN KEY (from_account_id) REFERENCES account_entity (account_id),
    CONSTRAINT fk_transactions_to_account
        FOREIGN KEY (to_account_id) REFERENCES account_entity (account_id)
);

CREATE TABLE IF NOT EXISTS role_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permissions_role
        FOREIGN KEY (role_id) REFERENCES roles (id),
    CONSTRAINT fk_role_permissions_permission
        FOREIGN KEY (permission_id) REFERENCES permissions (id)
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE TABLE IF NOT EXISTS user_session (
    id BIGSERIAL PRIMARY KEY,
    refresh_token_hash VARCHAR(64) NOT NULL UNIQUE,
    expiry_date TIMESTAMP(6) NOT NULL,
    revoked BOOLEAN NOT NULL,
    device_info VARCHAR(255),
    ip_address VARCHAR(255),
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_user_session_user
        FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS user_session_rotated_tokens (
    session_id BIGINT NOT NULL,
    token_hash VARCHAR(64) NOT NULL,
    PRIMARY KEY (session_id, token_hash),
    CONSTRAINT fk_user_session_rotated_tokens_session
        FOREIGN KEY (session_id) REFERENCES user_session (id)
);

CREATE INDEX IF NOT EXISTS idx_user_session_user_id
    ON user_session (user_id);

CREATE INDEX IF NOT EXISTS idx_user_session_expiry_date
    ON user_session (expiry_date);
