-- Database setup for Bank Management System
-- Run this script in MySQL to create the database

-- Create database
CREATE DATABASE IF NOT EXISTS bank_management_db;

-- Use the database
USE bank_management_db;

-- Create admin user (optional)
-- INSERT INTO users (username, password, email, first_name, last_name, phone_number, address, role, is_active, created_at, updated_at) 
-- VALUES ('admin', '$2a$10$N.wmfzFLfJdYhWgzBCDGNOhTqU4VfJGJhXELLlJzb6LYYmQEkgOLG', 'admin@bank.com', 'Admin', 'User', '1234567890', 'Admin Address', 'ADMIN', 1, NOW(), NOW());

-- Note: The password above is 'admin123' hashed with BCrypt
-- You can create this user after running the application for the first time
