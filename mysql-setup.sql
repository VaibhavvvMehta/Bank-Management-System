-- MySQL Database Setup for Bank Management System
-- Make sure MySQL server is running before executing this script

-- Step 1: Create database
CREATE DATABASE IF NOT EXISTS bank_management_db;

-- Step 2: Use the database
USE bank_management_db;

-- Step 3: Create a dedicated user for the application (optional but recommended)
-- CREATE USER 'bankapp'@'localhost' IDENTIFIED BY 'bankapp123';
-- GRANT ALL PRIVILEGES ON bank_management_db.* TO 'bankapp'@'localhost';
-- FLUSH PRIVILEGES;

-- Note: Tables will be automatically created by Hibernate when you start the application
-- The following tables will be created:
-- - users (stores user information including customers, employees, admins)
-- - accounts (stores bank account information)
-- - transactions (stores transaction history)

-- Verify database creation
SHOW DATABASES;
SELECT 'Database bank_management_db created successfully!' AS status;
