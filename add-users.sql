-- ============================================
-- Add Users to LEC Billing System
-- ============================================
-- Run this in Supabase SQL Editor
-- ============================================

-- First, let's check if users table exists and see current users
SELECT * FROM users;

-- Delete existing users (if any)
DELETE FROM users;

-- Insert Admin User
-- Username: admin
-- Password: admin123
INSERT INTO users (username, password, full_name, role, email, is_active, created_at, updated_at)
VALUES (
    'admin',
    'admin123',  -- Plain text password (not hashed for simplicity)
    'System Administrator',
    'admin',
    'admin@lecbilling.com',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Insert Staff User
-- Username: staff
-- Password: staff123
INSERT INTO users (username, password, full_name, role, email, is_active, created_at, updated_at)
VALUES (
    'staff',
    'staff123',  -- Plain text password (not hashed for simplicity)
    'Staff Member',
    'staff',
    'staff@lecbilling.com',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Insert Test User
-- Username: test
-- Password: test123
INSERT INTO users (username, password, full_name, role, email, is_active, created_at, updated_at)
VALUES (
    'test',
    'test123',
    'Test User',
    'staff',
    'test@lecbilling.com',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Verify users were inserted
SELECT 
    user_id,
    username,
    password,
    full_name,
    role,
    email,
    is_active,
    created_at
FROM users
ORDER BY user_id;

-- ============================================
-- Add Sample Customers
-- ============================================

-- Delete existing customers (if any)
DELETE FROM customers;

-- Insert Sample Customer 1
INSERT INTO customers (customer_id, name, address, phone, email, meter_number, current_usage, current_bill, is_active, created_at, updated_at)
VALUES (
    'C001',
    'John Mokete',
    'Maseru Central',
    '12345678',
    'john.mokete@email.com',
    'MTR001',
    150.00,
    195.00,  -- Tier 1: 100 * 1.20 = 120, Tier 2: 50 * 1.50 = 75, Total = 195
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Insert Sample Customer 2
INSERT INTO customers (customer_id, name, address, phone, email, meter_number, current_usage, current_bill, is_active, created_at, updated_at)
VALUES (
    'C002',
    'Mary Seleke',
    'Thetsane',
    '87654321',
    'mary.seleke@email.com',
    'MTR002',
    350.00,
    550.00,  -- Tier 1: 100 * 1.20 = 120, Tier 2: 200 * 1.50 = 300, Tier 3: 50 * 2.00 = 100, Total = 520
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Insert Sample Customer 3
INSERT INTO customers (customer_id, name, address, phone, email, meter_number, current_usage, current_bill, is_active, created_at, updated_at)
VALUES (
    'C003',
    'Peter Molapo',
    'Ha Abia',
    '55512345',
    'peter.molapo@email.com',
    'MTR003',
    75.00,
    90.00,  -- Tier 1: 75 * 1.20 = 90
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Verify customers were inserted
SELECT 
    customer_id,
    name,
    address,
    meter_number,
    current_usage,
    current_bill,
    is_active
FROM customers
ORDER BY customer_id;

-- ============================================
-- Summary Query
-- ============================================

-- Show all users
SELECT 'USERS' as table_name, COUNT(*) as count FROM users
UNION ALL
SELECT 'CUSTOMERS' as table_name, COUNT(*) as count FROM customers
UNION ALL
SELECT 'BILLS' as table_name, COUNT(*) as count FROM bills
UNION ALL
SELECT 'AUDIT_LOG' as table_name, COUNT(*) as count FROM audit_log;

-- ============================================
-- Test Login Credentials
-- ============================================
-- Username: admin    | Password: admin123
-- Username: staff    | Password: staff123
-- Username: test     | Password: test123
-- ============================================

