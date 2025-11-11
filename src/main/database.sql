-- Create Database
CREATE DATABASE IF NOT EXISTS lec_billing_db;
USE lec_billing_db;

-- Users table for authentication
CREATE TABLE IF NOT EXISTS users (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL,
    full_name VARCHAR(100) NOT NULL
);

-- Customers table
CREATE TABLE IF NOT EXISTS customers (
    customer_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address TEXT NOT NULL,
    meter_number VARCHAR(50) UNIQUE NOT NULL,
    electricity_usage DECIMAL(10,2) DEFAULT 0.0,
    bill_amount DECIMAL(10,2) DEFAULT 0.0,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Billing history table 
CREATE TABLE IF NOT EXISTS billing_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id VARCHAR(20) NOT NULL,
    usage_amount DECIMAL(10,2) NOT NULL,
    bill_amount DECIMAL(10,2) NOT NULL,
    billing_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
);

-- Insert sample users
INSERT INTO users (username, password, role, full_name) VALUES 
('admin', 'admin123', 'Administrator', 'System Administrator'),
('staff', 'staff123', 'Staff', 'Staff Member');

-- Insert sample customers
INSERT INTO customers (customer_id, name, address, meter_number, electricity_usage, bill_amount) VALUES 
('C001', 'John Molapo', 'Maseru West', 'MTR001', 150.0, 195.00),
('C002', 'Mary Seleke', 'Thetsane', 'MTR002', 350.0, 550.00);