# MySQL Setup Guide for Bank Management System

## Prerequisites
1. **Install MySQL Server** (if not already installed)
   - Download from: https://dev.mysql.com/downloads/mysql/
   - Or use XAMPP/WAMP which includes MySQL

2. **Install MySQL Workbench** (optional, for GUI management)
   - Download from: https://dev.mysql.com/downloads/workbench/

## Setup Steps

### Step 1: Start MySQL Service
Make sure MySQL server is running:

**Windows (if installed as service):**
```cmd
net start mysql
```

**XAMPP Users:**
- Open XAMPP Control Panel
- Start "MySQL" service

### Step 2: Create Database
Open MySQL command line or MySQL Workbench and run:

```sql
-- Connect to MySQL (default root user, no password)
mysql -u root -p

-- Create database
CREATE DATABASE IF NOT EXISTS bank_management_db;

-- Verify creation
SHOW DATABASES;
```

### Step 3: Configure Application
The application.properties is already configured for MySQL with these settings:

- **Database**: bank_management_db
- **Host**: localhost
- **Port**: 3306
- **Username**: root
- **Password**: (empty) - change if your MySQL has a password

### Step 4: Update Password (if needed)
If your MySQL root user has a password, update `application.properties`:

```properties
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

### Step 5: Start the Application
Run your Spring Boot application. It will automatically:
- Connect to MySQL database
- Create all required tables (users, accounts, transactions)
- Set up the schema

## Database Configuration Details

**Current Configuration:**
- **URL**: jdbc:mysql://localhost:3306/bank_management_db
- **Driver**: MySQL 8.0.33
- **Hibernate DDL**: update (preserves data between restarts)
- **Dialect**: MySQLDialect

**Tables Created Automatically:**
1. **users** - User accounts (customers, employees, admins)
2. **accounts** - Bank accounts linked to users
3. **transactions** - Financial transaction records

## Benefits of MySQL vs H2
✅ **Persistent Data** - Data survives application restarts
✅ **Production Ready** - Suitable for real-world deployment
✅ **Better Performance** - Optimized for larger datasets
✅ **Advanced Features** - Full SQL capabilities
✅ **Backup Support** - Easy database backup and restore

## Troubleshooting

**Connection Error:**
- Ensure MySQL service is running
- Check username/password in application.properties
- Verify database exists

**Access Denied:**
- Update MySQL password in application.properties
- Create dedicated user with proper permissions

**Port Conflict:**
- Default MySQL port is 3306
- Change port in application.properties if needed

## Alternative Setup (Dedicated User)
For better security, create a dedicated database user:

```sql
CREATE USER 'bankapp'@'localhost' IDENTIFIED BY 'bankapp123';
GRANT ALL PRIVILEGES ON bank_management_db.* TO 'bankapp'@'localhost';
FLUSH PRIVILEGES;
```

Then update application.properties:
```properties
spring.datasource.username=bankapp
spring.datasource.password=bankapp123
```
