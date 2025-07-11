<<<<<<< HEAD
# Bank Management System

A comprehensive bank management system built with Spring Boot, MySQL, and React.

## Features

- **Authentication & Authorization**: JWT-based authentication with role-based access control
- **User Management**: Registration, login, profile management
- **Account Management**: Create and manage bank accounts
- **Transaction Management**: Deposits, withdrawals, and transfers
- **Admin Panel**: Administrative functions for managing the system
- **Responsive UI**: Modern Material-UI based frontend

## Technology Stack

### Backend
- **Spring Boot 3.2.0**
- **Spring Security** (JWT Authentication)
- **Spring Data JPA** (Database ORM)
- **MySQL** (Database)
- **Maven** (Build Tool)

### Frontend
- **React 18**
- **Material-UI** (Component Library)
- **React Router** (Navigation)
- **Axios** (HTTP Client)

## Prerequisites

Before setting up the project, ensure you have the following installed:

- **Java 17 or higher**
- **Node.js 16 or higher**
- **MySQL 8.0 or higher**
- **Maven 3.6 or higher**

## Setup Instructions

### 1. Database Setup

1. Install MySQL and create a new database:
   ```sql
   CREATE DATABASE bank_management_db;
   ```

2. Update database credentials in `backend/src/main/resources/application.properties`:
   ```properties
   spring.datasource.username=your_mysql_username
   spring.datasource.password=your_mysql_password
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

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `GET /api/auth/user` - Get current user
- `PUT /api/auth/change-password` - Change password

### Accounts (Future Implementation)
- `GET /api/customer/accounts` - Get user accounts
- `POST /api/customer/accounts` - Create new account
- `GET /api/customer/accounts/{id}` - Get account by ID
- `PUT /api/customer/accounts/{id}` - Update account
- `DELETE /api/customer/accounts/{id}` - Delete account

### Transactions (Future Implementation)
- `GET /api/customer/transactions` - Get user transactions
- `POST /api/customer/transactions/deposit` - Create deposit
- `POST /api/customer/transactions/withdrawal` - Create withdrawal
- `POST /api/customer/transactions/transfer` - Create transfer

## User Roles

- **CUSTOMER**: Regular bank customers
- **EMPLOYEE**: Bank employees with additional privileges
- **ADMIN**: System administrators with full access

## Default Credentials

After setting up the system, you can create an admin user by registering through the frontend and then manually updating the role in the database:

```sql
UPDATE users SET role = 'ADMIN' WHERE username = 'your_admin_username';
```

## Project Structure

```
bank-management-system/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/
│   │   │   │       └── bankmanagement/
│   │   │   │           ├── config/      # Security & JWT configuration
│   │   │   │           ├── controller/  # REST controllers
│   │   │   │           ├── model/       # Entity classes
│   │   │   │           ├── repository/  # Data access layer
│   │   │   │           └── service/     # Business logic layer
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── components/   # Reusable React components
│   │   ├── pages/        # Page components
│   │   ├── services/     # API service functions
│   │   ├── context/      # React context providers
│   │   └── App.js
│   └── package.json
└── README.md
```

## Development Notes

1. **Security**: The application uses JWT tokens for authentication. Tokens are stored in localStorage.

2. **Database**: Tables are automatically created by Spring Boot JPA when the application starts.

3. **CORS**: The backend is configured to allow requests from `http://localhost:3000`.

4. **Error Handling**: Basic error handling is implemented for authentication and API calls.

## Future Enhancements

- Complete implementation of account management features
- Transaction history and reporting
- Email notifications
- Account statements generation
- Advanced admin features
- Mobile responsiveness improvements
- Unit and integration tests

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License.
=======
# Bank-Management-System
>>>>>>> d830beeeaea4ca18ec81aff2d1ce29b1642ac18b
