-- ======================================================
-- PayBot - Recharge and Payment System Database Setup
-- ======================================================
-- Run this script in MySQL Workbench, Command Line, or any MySQL client
-- Command: mysql -u root -p < database_setup.sql
-- ======================================================

-- Step 1: Create Database
CREATE DATABASE IF NOT EXISTS paybot_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Use the database
USE paybot_db;

-- ======================================================
-- Step 2: Create Tables
-- ======================================================

-- NOTE: If you're using Spring Boot with ddl-auto=update,
-- these tables will be auto-created by Hibernate.
-- This script is for manual setup or verification.

-- 2.1 Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    
    INDEX idx_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2.2 Wallets Table (One-to-One with Users)
CREATE TABLE IF NOT EXISTS wallets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    balance DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    
    CONSTRAINT fk_wallet_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_wallets_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2.3 Transactions Table
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_ref VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    type VARCHAR(30) NOT NULL COMMENT 'WALLET_TOPUP, MOBILE_RECHARGE, DTH_RECHARGE, ELECTRICITY_BILL, WATER_BILL, GAS_BILL, INTERNET_BILL, BROADBAND_RECHARGE',
    status VARCHAR(20) NOT NULL DEFAULT 'SUCCESS' COMMENT 'SUCCESS, FAILED, PENDING',
    amount DECIMAL(12,2) NOT NULL,
    service_provider VARCHAR(50) COMMENT 'JIO, AIRTEL, VI, BSNL, TATA_PLAY, etc.',
    account_number VARCHAR(50),
    description VARCHAR(255),
    balance_before DECIMAL(12,2),
    balance_after DECIMAL(12,2),
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    
    CONSTRAINT fk_transaction_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_txn_user_id (user_id),
    INDEX idx_txn_type (type),
    INDEX idx_txn_created_at (created_at DESC),
    INDEX idx_txn_user_type (user_id, type),
    INDEX idx_txn_ref (transaction_ref)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ======================================================
-- Step 3: Verify Tables
-- ======================================================
SHOW TABLES;

-- Show table structures
DESCRIBE users;
DESCRIBE wallets;
DESCRIBE transactions;

-- ======================================================
-- Step 4: Sample Data (Optional - for testing)
-- ======================================================
-- Uncomment the lines below to insert sample data
-- Password below is BCrypt hash of "password123"

-- INSERT INTO users (email, password, full_name, phone, created_at, updated_at) VALUES
-- ('ankit@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Ankit Kumar', '9876543210', NOW(), NOW());

-- INSERT INTO wallets (user_id, balance, created_at, updated_at) VALUES
-- (1, 5000.00, NOW(), NOW());

-- INSERT INTO transactions (transaction_ref, user_id, type, status, amount, description, balance_before, balance_after, created_at) VALUES
-- ('TXN-SAMPLE001', 1, 'WALLET_TOPUP', 'SUCCESS', 5000.00, 'Wallet top-up of ₹5000.00', 0.00, 5000.00, NOW()),
-- ('RCH-SAMPLE001', 1, 'MOBILE_RECHARGE', 'SUCCESS', 249.00, 'Mobile recharge of ₹249 for 9876543210 via JIO', 5000.00, 4751.00, NOW()),
-- ('BIL-SAMPLE001', 1, 'ELECTRICITY_BILL', 'SUCCESS', 1500.00, 'Electricity bill payment of ₹1500 for account ACC123 via TATA POWER', 4751.00, 3251.00, NOW());

-- ======================================================
-- Useful Queries for Verification
-- ======================================================

-- Check all users:
-- SELECT id, email, full_name, phone, created_at FROM users;

-- Check all wallets with user info:
-- SELECT w.id, u.full_name, u.email, w.balance, w.updated_at 
-- FROM wallets w JOIN users u ON w.user_id = u.id;

-- Check all transactions:
-- SELECT t.id, t.transaction_ref, u.full_name, t.type, t.amount, t.status, t.description, t.created_at 
-- FROM transactions t JOIN users u ON t.user_id = u.id 
-- ORDER BY t.created_at DESC;

-- Check wallet balance for a specific user:
-- SELECT u.full_name, w.balance FROM wallets w JOIN users u ON w.user_id = u.id WHERE u.email = 'ankit@example.com';

SELECT 'PayBot Database Setup Complete!' AS Status;
