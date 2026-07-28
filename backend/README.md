# MediCare Backend API

A Java-based backend API for the MediCare Healthcare Management System using JDBC and Servlets.

## Features

- **User Management**: Registration, login, and profile management for patients, doctors, and admins
- **Doctor Management**: Doctor registration, profile management, and specialization handling
- **Appointment System**: Create, view, update, and manage appointments
- **Database Integration**: MySQL database with JDBC for data persistence
- **RESTful API**: Clean REST endpoints for frontend integration
- **CORS Support**: Cross-origin resource sharing for frontend communication
- **Session Management**: Secure user sessions and authentication

## Technology Stack

- **Java 11**
- **Maven** - Build and dependency management
- **MySQL** - Database
- **JDBC** - Database connectivity
- **Servlets** - Web API framework
- **Gson** - JSON processing
- **Tomcat** - Application server

## Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── medicare/
│   │   │           ├── dao/           # Data Access Objects
│   │   │           ├── filter/        # Filters (CORS)
│   │   │           ├── listener/      # Application listeners
│   │   │           ├── model/         # Entity classes
│   │   │           ├── servlet/       # REST API endpoints
│   │   │           └── util/          # Utility classes
│   │   ├── resources/
│   │   │   └── database_schema.sql    # Database schema
│   │   └── webapp/
│   │       └── WEB-INF/
│   │           └── web.xml            # Web application configuration
├── pom.xml                            # Maven configuration
└── README.md                          # This file
```

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher
- Tomcat 9.0 or higher (for deployment)

## Setup Instructions

### 1. Database Setup

1. Install MySQL and start the service
2. Create a database user (optional, can use root)
3. Run the database schema:

```sql
-- Create database
CREATE DATABASE medicare_db;
USE medicare_db;

-- Run the schema from src/main/resources/database_schema.sql
```

### 2. Configuration

Update database connection details in `src/main/java/com/medicare/util/DatabaseConnection.java`:

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/medicare_db";
private static final String DB_USER = "your_username";
private static final String DB_PASSWORD = "your_password";
```

### 3. Build and Run

#### Using Maven:

```bash
# Navigate to backend directory
cd backend

# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package the application
mvn package

# Run with Tomcat Maven plugin
mvn tomcat7:run
```

#### Using IDE:

1. Import the project as a Maven project
2. Configure Tomcat server
3. Deploy the WAR file to Tomcat
4. Start the server

### 4. Access the API

The API will be available at:
- **Local**: `http://localhost:8080/medicare-api`
- **Base URL**: `http://localhost:8080/medicare-api/api`

## API Endpoints

### User Management
- `POST /api/users/` - Register new user
- `POST /api/users/login` - User login
- `POST /api/users/logout` - User logout
- `GET /api/users/current` - Get current user
- `GET /api/users/profile/{id}` - Get user profile
- `PUT /api/users/profile/{id}` - Update user profile

### Doctor Management
- `POST /api/doctors/` - Register new doctor
- `GET /api/doctors/` - Get all doctors
- `GET /api/doctors/profile/{id}` - Get doctor profile
- `GET /api/doctors/specializations` - Get all specializations
- `GET /api/doctors/specialization/{name}` - Get doctors by specialization
- `PUT /api/doctors/profile/{id}` - Update doctor profile
- `DELETE /api/doctors/{id}` - Delete doctor (admin only)

### Appointment Management
- `POST /api/appointments/` - Create new appointment
- `GET /api/appointments/` - Get appointments (based on user role)
- `GET /api/appointments/{id}` - Get appointment by ID
- `PUT /api/appointments/{id}` - Update appointment status
- `DELETE /api/appointments/{id}` - Delete appointment

## Database Schema

### Tables
- **users** - User accounts (patients, doctors, admins)
- **doctors** - Extended doctor information
- **appointments** - Appointment bookings
- **medical_records** - Medical records (for future use)

### Views
- **doctor_list** - Simplified doctor information
- **appointment_details** - Appointment details with doctor specialization

## Default Credentials

### Admin
- Email: `admin@medicare.com`
- Password: `admin123`

### Sample Doctors
- Email: `om@medicare.com` | Password: `doctor123`
- Email: `michael@medicare.com` | Password: `doctor123`
- Email: `priya@medicare.com` | Password: `doctor123`
- Email: `james@medicare.com` | Password: `doctor123`

## Frontend Integration

The backend is designed to work with the React frontend. CORS is enabled to allow cross-origin requests.

### Example API Call

```javascript
// Register a new patient
const response = await fetch('http://localhost:8080/medicare-api/api/users/', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
    },
    body: JSON.stringify({
        name: 'John Doe',
        email: 'john@example.com',
        password: 'password123',
        role: 'patient',
        age: 30,
        gender: 'male',
        mobile: '+91 9876543210'
    })
});

const data = await response.json();
```

## Development

### Adding New Features

1. Create model classes in `com.medicare.model`
2. Create DAO classes in `com.medicare.dao`
3. Create servlet classes in `com.medicare.servlet`
4. Update `web.xml` if needed
5. Test the endpoints

### Database Changes

1. Update `database_schema.sql`
2. Update model classes if needed
3. Update DAO classes for new queries
4. Test database operations

## Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Check MySQL service is running
   - Verify database credentials
   - Ensure database exists

2. **CORS Errors**
   - Check CORS filter is configured
   - Verify frontend URL is allowed

3. **Build Errors**
   - Check Java version (11+)
   - Verify Maven installation
   - Clean and rebuild project

### Logs

Check application logs for detailed error information:
- Console output during development
- Tomcat logs in production

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is part of the MediCare Healthcare Management System.
