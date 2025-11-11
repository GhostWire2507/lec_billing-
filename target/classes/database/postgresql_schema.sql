-- =====================================================
-- LEC Billing System - PostgreSQL Database Schema
-- =====================================================

-- Drop existing tables if they exist
DROP TABLE IF EXISTS bills CASCADE;
DROP TABLE IF EXISTS customers CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS audit_log CASCADE;

-- =====================================================
-- Users Table
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
-- Customers Table
-- =====================================================
CREATE TABLE customers (
    id SERIAL PRIMARY KEY,
    customer_id VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    meter_number VARCHAR(50) UNIQUE NOT NULL,
    phone_number VARCHAR(20),
    email VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by INTEGER REFERENCES users(id)
);

-- Indexes for faster queries
CREATE INDEX idx_customers_customer_id ON customers(customer_id);
CREATE INDEX idx_customers_meter_number ON customers(meter_number);
CREATE INDEX idx_customers_active ON customers(is_active);
CREATE INDEX idx_customers_name ON customers(name);

-- =====================================================
-- Bills Table
-- =====================================================
CREATE TABLE bills (
    id SERIAL PRIMARY KEY,
    bill_number VARCHAR(20) UNIQUE NOT NULL,
    customer_id INTEGER NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    billing_period_start DATE NOT NULL,
    billing_period_end DATE NOT NULL,
    previous_reading DECIMAL(10, 2) DEFAULT 0.00,
    current_reading DECIMAL(10, 2) NOT NULL,
    electricity_usage DECIMAL(10, 2) NOT NULL,
    rate_tier_1_usage DECIMAL(10, 2) DEFAULT 0.00,
    rate_tier_2_usage DECIMAL(10, 2) DEFAULT 0.00,
    rate_tier_3_usage DECIMAL(10, 2) DEFAULT 0.00,
    rate_tier_1_amount DECIMAL(10, 2) DEFAULT 0.00,
    rate_tier_2_amount DECIMAL(10, 2) DEFAULT 0.00,
    rate_tier_3_amount DECIMAL(10, 2) DEFAULT 0.00,
    bill_amount DECIMAL(10, 2) NOT NULL,
    due_date DATE NOT NULL,
    payment_status VARCHAR(20) DEFAULT 'UNPAID',
    payment_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by INTEGER REFERENCES users(id)
);

-- Indexes for faster queries
CREATE INDEX idx_bills_customer_id ON bills(customer_id);
CREATE INDEX idx_bills_bill_number ON bills(bill_number);
CREATE INDEX idx_bills_payment_status ON bills(payment_status);
CREATE INDEX idx_bills_billing_period ON bills(billing_period_start, billing_period_end);
CREATE INDEX idx_bills_due_date ON bills(due_date);

-- =====================================================
-- Audit Log Table
-- =====================================================
CREATE TABLE audit_log (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id),
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id VARCHAR(50),
    description TEXT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index for audit queries
CREATE INDEX idx_audit_user_id ON audit_log(user_id);
CREATE INDEX idx_audit_action ON audit_log(action);
CREATE INDEX idx_audit_created_at ON audit_log(created_at);

-- =====================================================
-- Insert Default Users
-- =====================================================
INSERT INTO users (username, password, role, full_name, email) VALUES 
('admin', 'admin123', 'Administrator', 'System Administrator', 'admin@lecbilling.ls'),
('staff', 'staff123', 'Staff', 'Staff Member', 'staff@lecbilling.ls');

-- =====================================================
-- Insert Sample Customers
-- =====================================================
INSERT INTO customers (customer_id, name, address, meter_number, phone_number, created_by) VALUES 
('C001', 'John Molapo', 'Maseru West, Plot 123', 'MTR001', '+266 5800 1234', 1),
('C002', 'Mary Seleke', 'Thetsane, House 456', 'MTR002', '+266 5800 5678', 1),
('C003', 'Peter Nkuebe', 'Maseru Central, Building 789', 'MTR003', '+266 5800 9012', 1);

-- =====================================================
-- Insert Sample Bills
-- =====================================================
INSERT INTO bills (
    bill_number, customer_id, billing_period_start, billing_period_end,
    previous_reading, current_reading, electricity_usage,
    rate_tier_1_usage, rate_tier_2_usage, rate_tier_3_usage,
    rate_tier_1_amount, rate_tier_2_amount, rate_tier_3_amount,
    bill_amount, due_date, payment_status, created_by
) VALUES 
(
    'BILL-2024-001', 1, '2024-10-01', '2024-10-31',
    0, 150, 150,
    100, 50, 0,
    120.00, 75.00, 0,
    195.00, '2024-11-15', 'PAID', 1
),
(
    'BILL-2024-002', 2, '2024-10-01', '2024-10-31',
    0, 350, 350,
    100, 200, 50,
    120.00, 300.00, 100.00,
    520.00, '2024-11-15', 'UNPAID', 1
),
(
    'BILL-2024-003', 3, '2024-10-01', '2024-10-31',
    0, 250, 250,
    100, 150, 0,
    120.00, 225.00, 0,
    345.00, '2024-11-15', 'UNPAID', 1
);

-- =====================================================
-- Create Views for Reporting
-- =====================================================

-- View for customer billing summary
CREATE OR REPLACE VIEW v_customer_billing_summary AS
SELECT 
    c.customer_id,
    c.name,
    c.address,
    c.meter_number,
    COUNT(b.id) as total_bills,
    SUM(CASE WHEN b.payment_status = 'PAID' THEN 1 ELSE 0 END) as paid_bills,
    SUM(CASE WHEN b.payment_status = 'UNPAID' THEN 1 ELSE 0 END) as unpaid_bills,
    SUM(b.bill_amount) as total_billed,
    SUM(CASE WHEN b.payment_status = 'PAID' THEN b.bill_amount ELSE 0 END) as total_paid,
    SUM(CASE WHEN b.payment_status = 'UNPAID' THEN b.bill_amount ELSE 0 END) as total_outstanding
FROM customers c
LEFT JOIN bills b ON c.id = b.customer_id
WHERE c.is_active = TRUE
GROUP BY c.id, c.customer_id, c.name, c.address, c.meter_number;

-- View for monthly revenue
CREATE OR REPLACE VIEW v_monthly_revenue AS
SELECT 
    TO_CHAR(billing_period_start, 'YYYY-MM') as month,
    COUNT(*) as total_bills,
    SUM(electricity_usage) as total_usage,
    SUM(bill_amount) as total_revenue,
    SUM(CASE WHEN payment_status = 'PAID' THEN bill_amount ELSE 0 END) as collected_revenue,
    SUM(CASE WHEN payment_status = 'UNPAID' THEN bill_amount ELSE 0 END) as outstanding_revenue
FROM bills
GROUP BY TO_CHAR(billing_period_start, 'YYYY-MM')
ORDER BY month DESC;

-- =====================================================
-- Functions and Triggers
-- =====================================================

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Trigger for customers table
CREATE TRIGGER update_customers_updated_at BEFORE UPDATE ON customers
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Trigger for users table
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- Grant Permissions (adjust as needed)
-- =====================================================
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO lec_billing_user;
-- GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO lec_billing_user;

-- =====================================================
-- End of Schema
-- =====================================================

