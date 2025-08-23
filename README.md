# Bank Management System 🏦

A comprehensive, multi-bank management system built with **Java Spring Boot**, **MySQL**, and **React**. This full-stack application provides complete banking functionality with role-based access control for customers, employees, and administrators across multiple banks.

## 🚀 Features

### 🔐 Authentication & Authorization
- JWT-based authentication with role-based access control
- Three user roles: **CUSTOMER**, **EMPLOYEE**, **ADMIN**
- Secure password hashing with BCrypt
- Token-based session management
- Bank-specific admin access

### 🏦 Multi-Bank Architecture
- Support for multiple banks (Bank1, Bank2)
- Bank-specific user management
- Isolated data access per bank
- Bank-specific admin dashboards

### 👥 User Management
- User registration and login
- Profile management
- Role-based dashboard views
- User activation/deactivation (Admin)
- Bank-specific user filtering

### 💳 Account Management
- Create multiple bank accounts (Savings, Checking, Business)
- Real-time balance tracking with **Indian Rupees (₹)**
- Account status management (Active, Suspended, Closed)
- Account number auto-generation
- Multi-account support per user
- Bank-specific account filtering

### 💰 Transaction Management
- **Deposits**: Add funds to accounts
- **Withdrawals**: Withdraw funds with balance validation
- **Transfers**: Transfer funds between accounts
- Transaction history and tracking
- Real-time balance updates
- Transaction status management (Pending, Completed, Cancelled)
- All amounts displayed in **Indian Rupees (₹)**

### 🔧 Admin Features
- Bank-specific dashboard with statistics
- User management (Create, Update, Delete, Role Assignment)
- Account oversight and management
- Transaction monitoring
- Employee account creation
- Comprehensive reporting
- **Fixed Admin Panel** - Now displays all data correctly

### 🎨 Modern UI/UX
- Responsive Material-UI design
- Role-based navigation
- Interactive dashboards
- Real-time data updates
- Mobile-friendly interface
- **Indian Rupee (₹) currency formatting**

## 🛠 Technology Stack

### Backend
- **Java 17+**
- **Spring Boot 3.2.0**
- **Spring Security** (JWT Authentication)
- **Spring Data JPA** (Database ORM)
- **MySQL 8.0+** (Database)
- **Maven** (Build Tool)
- **Hibernate** (ORM with update mode for data persistence)

### Frontend
- **React 19**
- **Material-UI v7** (Component Library)
- **React Router v7** (Navigation)
- **Axios** (HTTP Client)
- **React Context** (State Management)

## 📋 Prerequisites

Before setting up the project, ensure you have:

- **Java 17 or higher**
- **Node.js 16 or higher**
- **MySQL 8.0 or higher**
- **Maven 3.6 or higher**

## 🚀 Quick Setup

### 1. Database Setup

1. Install MySQL and create a new database:
   ```sql
   CREATE DATABASE bank_management_db;
   ```

2. Update database credentials in `backend/src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/bank_management_db
   spring.datasource.username=root
   spring.datasource.password=Mysql@1234
   spring.jpa.hibernate.ddl-auto=update
   ```

### 2. Backend Setup

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Install dependencies and run the application:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

   The backend will start on `http://localhost:8080`

### 3. Frontend Setup

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm start
   ```

   The frontend will start on `http://localhost:3000`

## 🔑 Default Credentials

The system automatically creates default admin accounts during initialization:

### Admin Accounts:
- **Username**: `bank1admin`
- **Password**: `admin123`
- **Role**: ADMIN (Bank1 Administrator)

- **Username**: `bank2admin`  
- **Password**: `admin123`
- **Role**: ADMIN (Bank2 Administrator)

### 📋 How to Access Admin Panel:
1. Go to the application at `http://localhost:3000`
2. Click the account icon and log out if already logged in
3. Log in with admin credentials (`bank1admin` / `admin123`)
4. Navigate to the "Admin" section to view and manage system data

**Note**: All currency values are displayed in **Indian Rupees (₹)**.

## 📊 API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `GET /api/auth/user` - Get current user
- `PUT /api/auth/change-password` - Change password

### Accounts
- `GET /api/accounts/my-accounts` - Get user accounts
- `POST /api/accounts` - Create new account
- `GET /api/accounts/{id}` - Get account by ID
- `GET /api/accounts/{id}/balance` - Get account balance
- `PUT /api/accounts/{id}/status` - Update account status (Admin/Employee)
- `DELETE /api/accounts/{id}` - Delete account (Admin)

### Transactions
- `GET /api/transactions/my-transactions` - Get user transactions
- `POST /api/transactions/deposit` - Create deposit
- `POST /api/transactions/withdrawal` - Create withdrawal
- `POST /api/transactions/transfer` - Create transfer
- `GET /api/transactions/account/{accountId}` - Get transactions by account

### Admin (Bank-Specific)
- `GET /api/admin/dashboard/stats` - Bank-specific statistics
- `GET /api/admin/users` - Get bank users
- `GET /api/admin/accounts` - Get bank accounts
- `GET /api/admin/transactions` - Get bank transactions
- `PUT /api/admin/users/{id}/role` - Update user role
- `PUT /api/admin/users/{id}/activate` - Activate user
- `PUT /api/admin/users/{id}/deactivate` - Deactivate user
- `DELETE /api/admin/users/{id}` - Delete user

## 👤 User Roles & Permissions

### CUSTOMER
- Create and manage own accounts
- Perform transactions (deposit, withdrawal, transfer)
- View transaction history
- Update profile information

### EMPLOYEE
- View all accounts and transactions within their bank
- Manage account status (activate/suspend)
- Assist customers with account issues
- Generate reports

### ADMIN
- Full system access within their bank
- User management (create, update, delete)
- Role assignment
- Account and transaction oversight
- Employee management
- Bank-specific dashboard and reporting

## 🗂 Project Structure

```
bank-management-system/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/
│   │   │   │       └── bankmanagement/
│   │   │   │           ├── MainApplication.java
│   │   │   │           ├── config/          # Security & JWT config
│   │   │   │           ├── controller/      # REST controllers
│   │   │   │           ├── model/           # Entity classes
│   │   │   │           ├── repository/      # Data access layer
│   │   │   │           ├── service/         # Business logic
│   │   │   │           └── dto/             # Data Transfer Objects
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── components/       # Reusable React components
│   │   ├── pages/            # Page components
│   │   ├── services/         # API service functions
│   │   ├── context/          # React context providers
│   │   └── App.js
│   └── package.json
├── database-setup.sql        # Database initialization
└── README.md
```

## 🔧 Development

### Running in Development Mode

1. **Backend**: `mvn spring-boot:run` (runs on port 8080)
2. **Frontend**: `npm start` (runs on port 3002)
3. **Database**: MySQL on port 3306

### Environment Configuration

- **Development**: Frontend automatically proxies API calls to backend
- **CORS**: Backend configured to allow requests from `http://localhost:3002`
- **JWT**: Tokens expire in 24 hours (configurable)
- **Database**: Hibernate update mode preserves existing data

## 🐛 Recent Fixes & Updates

### ✅ **Currency System**
- **Fixed**: All currency displays now show Indian Rupees (₹)
- **Updated**: Currency formatting from USD to INR
- **Changed**: Locale from en-US to en-IN

### ✅ **Admin Panel Data Loading**
- **Fixed**: Admin panel empty tables issue
- **Resolved**: Data mapping between frontend and backend
- **Updated**: Proper handling of JSON response structure

### ✅ **User Status Display**
- **Fixed**: Inactive users now show correct status
- **Resolved**: Field mapping from `isActive` to `active`
- **Updated**: Status chips display properly

### ✅ **Multi-Bank Architecture**
- **Implemented**: Bank-specific data filtering
- **Added**: Bank isolation for admin access
- **Enhanced**: Security for cross-bank data access

### ✅ **Transaction Management**
- **Implemented**: Complete transaction system
- **Added**: LEFT JOIN queries for proper transaction visibility
- **Enhanced**: Real-time balance updates

## 🎯 Key Features Implemented

✅ **Complete Authentication System**  
✅ **Role-Based Access Control**  
✅ **Multi-Bank Architecture**  
✅ **Account Management**  
✅ **Transaction Processing (Deposit, Withdrawal, Transfer)**  
✅ **Admin Dashboard with Real Data**  
✅ **Indian Rupee Currency System**  
✅ **Responsive UI**  
✅ **Real-time Updates**  
✅ **Data Validation**  
✅ **Error Handling**  
✅ **Security Best Practices**  

## 🔮 Future Enhancements

- 📱 Mobile app development
- 📧 Email notifications
- 📊 Advanced reporting and analytics
- 🔔 Real-time notifications
- 💳 Card management
- 🏦 Loan management
- 📄 Statement generation
- 🔐 Two-factor authentication
- 🌐 Multi-language support
- 📈 Investment tracking

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Spring Boot community for excellent documentation
- Material-UI team for the beautiful component library
- React team for the powerful frontend framework
- MySQL team for the robust database system

---

**Made with ❤️ for modern Indian banking solutions** 🇮🇳

## 📊 API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `GET /api/auth/user` - Get current user
- `PUT /api/auth/change-password` - Change password

### Accounts
- `GET /api/accounts/my-accounts` - Get user accounts
- `POST /api/accounts` - Create new account
- `GET /api/accounts/{id}` - Get account by ID
- `GET /api/accounts/{id}/balance` - Get account balance
- `PUT /api/accounts/{id}/status` - Update account status (Admin/Employee)
- `DELETE /api/accounts/{id}` - Delete account (Admin)

### Transactions
- `GET /api/transactions/my-transactions` - Get user transactions
- `POST /api/transactions/deposit` - Create deposit
- `POST /api/transactions/withdrawal` - Create withdrawal
- `POST /api/transactions/transfer` - Create transfer
- `GET /api/transactions/account/{accountId}` - Get transactions by account

### Admin
- `GET /api/admin/dashboard/stats` - System statistics
- `GET /api/admin/users` - Get all users
- `PUT /api/admin/users/{id}/role` - Update user role
- `POST /api/admin/create-employee` - Create employee account
- `POST /api/admin/create-admin` - Create admin account

## 👤 User Roles & Permissions

### CUSTOMER
- Create and manage own accounts
- Perform transactions (deposit, withdrawal, transfer)
- View transaction history
- Update profile information

### EMPLOYEE
- View all accounts and transactions
- Manage account status (activate/suspend)
- Assist customers with account issues
- Generate reports

### ADMIN
- Full system access
- User management (create, update, delete)
- Role assignment
- System configuration
- Employee management
- Advanced reporting

## 🗂 Project Structure

```
bank-management-system/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/
│   │   │   │       └── bankmanagement/
│   │   │   │           ├── MainApplication.java
│   │   │   │           ├── config/          # Security & JWT config
│   │   │   │           ├── controller/      # REST controllers
│   │   │   │           ├── model/           # Entity classes
│   │   │   │           ├── repository/      # Data access layer
│   │   │   │           └── service/         # Business logic
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── components/       # Reusable React components
│   │   ├── pages/            # Page components
│   │   ├── services/         # API service functions
│   │   ├── context/          # React context providers
│   │   └── App.js
│   └── package.json
├── database-setup.sql        # Database initialization
└── README.md
```

## 🔧 Development

### Running in Development Mode

1. **Backend**: `mvn spring-boot:run` (runs on port 8080)
2. **Frontend**: `npm start` (runs on port 3000)
3. **Database**: MySQL on port 3306

### Environment Configuration

- **Development**: Frontend automatically proxies API calls to backend
- **CORS**: Backend configured to allow requests from `http://localhost:3000`
- **JWT**: Tokens expire in 24 hours (configurable)

## 🔍 Testing

### Default Test User

After setup, you can create an admin user:

1. Register through the frontend
2. Update user role in database:
   ```sql
   UPDATE users SET role = 'ADMIN' WHERE username = 'your_username';
   ```

### Test Data

The system includes:
- Automatic account number generation
- Sample transaction types
- Role-based access validation

## 🚀 Deployment

### Production Build

1. **Frontend**:
   ```bash
   npm run build
   ```

2. **Backend**:
   ```bash
   mvn clean package
   ```


## 🎯 Key Features Implemented

✅ **Complete Authentication System**  
✅ **Role-Based Access Control**  
✅ **Account Management**  
✅ **Transaction Processing**  
✅ **Admin Dashboard**  
✅ **Responsive UI**  
✅ **Real-time Updates**  
✅ **Data Validation**  
✅ **Error Handling**  
✅ **Security Best Practices**  



## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

**Made with ❤️ for modern banking solutions**

