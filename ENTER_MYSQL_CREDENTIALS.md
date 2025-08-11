# 🔧 MySQL Setup Instructions - Enter Your Details

## 📍 WHERE TO ENTER YOUR MYSQL CREDENTIALS

**File Location**: `backend/src/main/resources/application.properties`

**Look for these lines and replace the placeholder:**

```properties
# ENTER YOUR MYSQL USERNAME HERE (usually 'root')
spring.datasource.username=root

# ENTER YOUR MYSQL PASSWORD HERE (enter your MySQL root password)
spring.datasource.password=YOUR_MYSQL_PASSWORD_HERE
```

## ✏️ STEP-BY-STEP INSTRUCTIONS

### Step 1: Find Your MySQL Password
Common MySQL passwords to try:
- **Empty password**: Leave it blank like `spring.datasource.password=`
- **"root"**: `spring.datasource.password=root`
- **"password"**: `spring.datasource.password=password`
- **"mysql"**: `spring.datasource.password=mysql`
- **Custom password**: Whatever you set during MySQL installation

### Step 2: Update the Configuration
1. Open the file: `backend/src/main/resources/application.properties`
2. Find the line: `spring.datasource.password=YOUR_MYSQL_PASSWORD_HERE`
3. Replace `YOUR_MYSQL_PASSWORD_HERE` with your actual password

**Examples:**
```properties
# If your password is empty (XAMPP default):
spring.datasource.password=

# If your password is "root":
spring.datasource.password=root

# If your password is "password123":
spring.datasource.password=password123
```

### Step 3: Update Username (if needed)
- Default username is `root`
- If you use a different username, change: `spring.datasource.username=YOUR_USERNAME`

### Step 4: Save and Test
1. Save the file
2. Start your backend application
3. Check if it connects successfully

## 🚀 QUICK TEST COMMANDS

To test your MySQL connection, try these commands in PowerShell:

```powershell
# Test with no password:
mysql -u root

# Test with password:
mysql -u root -p
```

## 📋 COMMON SCENARIOS

### Scenario 1: XAMPP Installation
```properties
spring.datasource.username=root
spring.datasource.password=
```

### Scenario 2: MySQL Workbench Installation
```properties
spring.datasource.username=root
spring.datasource.password=YOUR_CHOSEN_PASSWORD
```

### Scenario 3: Custom Installation
```properties
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

## ❗ IMPORTANT NOTES

1. **Database Creation**: The database `bank_management_db` will be created automatically
2. **Tables**: All tables (users, accounts, transactions) will be created automatically
3. **Port**: MySQL should be running on port 3306 (default)
4. **Security**: Never commit passwords to version control in real projects

## 🆘 NEED HELP?

If you're unsure about your MySQL credentials:
1. Check your MySQL installation notes
2. Try the common passwords listed above
3. Check if you have XAMPP running (usually no password)
4. Use MySQL Workbench to verify your credentials

## ✅ AFTER SETUP

Once you enter the correct credentials:
1. Save the file
2. Restart your Spring Boot application
3. Your bank management system will use MySQL database
4. All data will persist between application restarts!
