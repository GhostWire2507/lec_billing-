-- =====================================================
-- LEC Billing System - Complete Database Setup
-- =====================================================
-- This script will:
-- 1. Drop all existing tables
-- 2. Create fresh tables with correct schema
-- 3. Add sample users and customers
-- =====================================================

-- =====================================================
-- STEP 1: Drop all existing tables (if they exist)
-- =====================================================
DROP TABLE IF EXISTS audit_log CASCADE;
DROP TABLE IF EXISTS bills CASCADE;
DROP TABLE IF EXISTS customers CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- =====================================================
-- STEP 2: Create Users Table
-- =====================================================
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);

-- Index for faster login queries
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_active ON users(is_active);

-- =====================================================
-- STEP 3: Create Customers Table
-- =====================================================
CREATE TABLE customers (
    id SERIAL PRIMARY KEY,
    customer_id VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    phone VARCHAR(20),
    email VARCHAR(100),
    meter_number VARCHAR(50) UNIQUE NOT NULL,
    current_usage DECIMAL(10, 2) DEFAULT 0.00,
    current_bill DECIMAL(10, 2) DEFAULT 0.00,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for faster queries
CREATE INDEX idx_customers_customer_id ON customers(customer_id);
CREATE INDEX idx_customers_meter_number ON customers(meter_number);
CREATE INDEX idx_customers_active ON customers(is_active);

-- =====================================================
-- STEP 4: Create Bills Table
-- =====================================================
CREATE TABLE bills (
    id SERIAL PRIMARY KEY,
    bill_id VARCHAR(50) UNIQUE NOT NULL,
    customer_id VARCHAR(50) NOT NULL,
    billing_period VARCHAR(20) NOT NULL,
    usage_kwh DECIMAL(10, 2) NOT NULL,
    tier1_usage DECIMAL(10, 2) DEFAULT 0.00,
    tier1_cost DECIMAL(10, 2) DEFAULT 0.00,
    tier2_usage DECIMAL(10, 2) DEFAULT 0.00,
    tier2_cost DECIMAL(10, 2) DEFAULT 0.00,
    tier3_usage DECIMAL(10, 2) DEFAULT 0.00,
    tier3_cost DECIMAL(10, 2) DEFAULT 0.00,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) DEFAULT 'unpaid',
    due_date DATE,
    paid_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
);

-- Indexes for faster queries
CREATE INDEX idx_bills_customer_id ON bills(customer_id);
CREATE INDEX idx_bills_status ON bills(status);
CREATE INDEX idx_bills_billing_period ON bills(billing_period);

-- =====================================================
-- STEP 5: Create Audit Log Table
-- =====================================================
CREATE TABLE audit_log (
    id SERIAL PRIMARY KEY,
    action VARCHAR(50) NOT NULL,
    table_name VARCHAR(50) NOT NULL,
    record_id VARCHAR(50),
    user_id VARCHAR(50),
    details TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index for faster queries
CREATE INDEX idx_audit_log_created_at ON audit_log(created_at);
CREATE INDEX idx_audit_log_user_id ON audit_log(user_id);

-- =====================================================
-- STEP 6: Insert Users
-- =====================================================
INSERT INTO users (username, password, role, full_name, email, is_active, created_at, updated_at)
VALUES 
    ('admin', 'admin123', 'admin', 'System Administrator', 'admin@lecbilling.com', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('staff', 'staff123', 'staff', 'Staff Member', 'staff@lecbilling.com', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================================================
-- STEP 7: Insert Sample Customers
-- =====================================================
INSERT INTO customers (customer_id, name, address, phone, email, meter_number, current_usage, current_bill, is_active, created_at, updated_at)
VALUES 
    ('C001', 'John Mokete', 'Maseru Central, Block A', '12345678', 'john.mokete@email.com', 'MTR001', 150.00, 195.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('C002', 'Mary Seleke', 'Thetsane, House 45', '87654321', 'mary.seleke@email.com', 'MTR002', 350.00, 550.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('C003', 'Peter Molapo', 'Ha Abia, Plot 12', '55512345', 'peter.molapo@email.com', 'MTR003', 75.00, 90.00, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================================================
-- STEP 8: Insert Sample Bills
-- =====================================================
INSERT INTO bills (bill_id, customer_id, billing_period, usage_kwh, tier1_usage, tier1_cost, tier2_usage, tier2_cost, tier3_usage, tier3_cost, total_amount, status, due_date, created_at, created_by)
VALUES 
    ('BILL001', 'C001', '2024-10', 150.00, 100.00, 120.00, 50.00, 75.00, 0.00, 0.00, 195.00, 'paid', '2024-11-15', CURRENT_TIMESTAMP, 'admin'),
    ('BILL002', 'C002', '2024-10', 350.00, 100.00, 120.00, 200.00, 300.00, 50.00, 100.00, 520.00, 'unpaid', '2024-11-15', CURRENT_TIMESTAMP, 'admin'),
    ('BILL003', 'C003', '2024-10', 75.00, 75.00, 90.00, 0.00, 0.00, 0.00, 0.00, 90.00, 'paid', '2024-11-15', CURRENT_TIMESTAMP, 'staff');

-- =====================================================
-- STEP 9: Verification Queries
-- =====================================================

-- Show all users
SELECT 'USERS TABLE' as info;
SELECT id, username, password, role, full_name, email, is_active FROM users ORDER BY id;

-- Show all customers
SELECT 'CUSTOMERS TABLE' as info;
SELECT id, customer_id, name, address, meter_number, current_usage, current_bill, is_active FROM customers ORDER BY id;

-- Show all bills
SELECT 'BILLS TABLE' as info;
SELECT id, bill_id, customer_id, billing_period, usage_kwh, total_amount, status FROM bills ORDER BY id;

-- Show table counts
SELECT 'TABLE COUNTS' as info;
SELECT 
    (SELECT COUNT(*) FROM users) as users_count,
    (SELECT COUNT(*) FROM customers) as customers_count,
    (SELECT COUNT(*) FROM bills) as bills_count,
    (SELECT COUNT(*) FROM audit_log) as audit_log_count;

-- Test login query (exactly what the app uses)
SELECT 'TEST LOGIN QUERY FOR ADMIN' as info;
SELECT id, username, password, role, full_name, email, is_active 
FROM users 
WHERE username = 'admin' AND is_active = TRUE;

SELECT 'TEST LOGIN QUERY FOR STAFF' as info;
SELECT id, username, password, role, full_name, email, is_active 
FROM users 
WHERE username = 'staff' AND is_active = TRUE;

-- =====================================================
-- SETUP COMPLETE!
-- =====================================================
-- Login Credentials:
-- Username: admin  | Password: admin123 | Role: admin
-- Username: staff  | Password: staff123 | Role: staff
-- =====================================================

