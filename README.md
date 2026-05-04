# 🚀 Session Guard API

[![Java](https://img.shields.io/badge/Java-17-orange)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.14-brightgreen)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)](https://www.mysql.com/)
[![JWT](https://img.shields.io/badge/JWT-0.12.3-red)](https://github.com/jwtk/jjwt)

A robust and secure REST API for user authentication and session management built with Spring Boot. Features JWT-based authentication, session tracking, and comprehensive logout capabilities.

## ✨ Features

- 🔐 **User Authentication**: Secure registration and login with email/password
- 🎫 **JWT Tokens**: Access and refresh token implementation with configurable expiry
- 📱 **Session Management**: Track and manage user sessions across devices
- 🚪 **Flexible Logout**: Logout from single device or all devices simultaneously
- 🛡️ **Token Blacklisting**: Prevent reuse of revoked tokens
- ✅ **Input Validation**: Comprehensive validation with meaningful error messages
- 🏗️ **RESTful API**: Clean, well-structured REST endpoints

## 🛠️ Tech Stack

- **Backend**: Java 17, Spring Boot 3.5.14
- **Security**: Spring Security, JWT (JSON Web Tokens)
- **Database**: MySQL with Spring Data JPA
- **Validation**: Bean Validation (Jakarta)
- **Build Tool**: Maven
- **Utilities**: Lombok for boilerplate reduction

## 📋 Prerequisites

- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6+

## 🚀 Installation & Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/session-guard-api.git
   cd session-guard-api
   ```

2. **Set up MySQL Database**
   ```sql
   CREATE DATABASE sessionguard_db;
   ```

3. **Configure Database Connection**
   
   Update `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/sessionguard_db
   spring.datasource.username=your_mysql_username
   spring.datasource.password=your_mysql_password
   ```

4. **Build and Run**
   ```bash
   # Build the project
   ./mvnw clean compile
   
   # Run the application
   ./mvnw spring-boot:run
   ```

The API will be available at `http://localhost:8080`

## 📖 API Documentation

### Authentication Endpoints

#### Register User
```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "StrongPass123"
}
```

#### Login
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "StrongPass123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login success",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 900,
    "sessionId": 1
  }
}
```

#### Refresh Token
```http
POST /api/v1/auth/refresh
Content-Type: application/json
Authorization: Bearer <refresh_token>

{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

#### Logout (Current Session)
```http
POST /api/v1/auth/logout
Authorization: Bearer <access_token>
```

#### Logout All Sessions
```http
POST /api/v1/auth/logout-all
Authorization: Bearer <access_token>
```

### Session Management Endpoints

#### Get Active Sessions
```http
GET /api/v1/sessions
Authorization: Bearer <access_token>
```

**Response:**
```json
{
  "success": true,
  "message": "Sessions fetched",
  "data": [
    {
      "id": 1,
      "deviceInfo": "Chrome on Windows",
      "ipAddress": "192.168.1.100",
      "isActive": true,
      "createdAt": "2024-01-01T10:00:00Z"
    }
  ]
}
```

#### Delete Session
```http
DELETE /api/v1/sessions/{sessionId}
Authorization: Bearer <access_token>
```

### Test Endpoint
```http
GET /test
```
Returns: `"Working"`

## 🔧 Configuration

Key configuration properties in `application.properties`:

```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/sessionguard_db
spring.datasource.username=root
spring.datasource.password=naman

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT
jwt.secret=your-secret-key-here
jwt.access-token-expiry-ms=900000  # 15 minutes
jwt.refresh-token-expiry-ms=604800000  # 7 days
```

## 🏗️ Project Structure

```
sessionguard-api/
├── src/main/java/com/sessionguard/
│   ├── config/
│   │   └── SecurityConfig.java
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── SessionController.java
│   │   └── TestController.java
│   ├── dto/
│   │   ├── request/
│   │   │   ├── LoginRequest.java
│   │   │   ├── RefreshTokenRequest.java
│   │   │   └── RegisterRequest.java
│   │   └── response/
│   │       ├── ApiResponse.java
│   │       ├── AuthResponse.java
│   │       └── SessionResponse.java
│   ├── entity/
│   │   ├── BlacklistedToken.java
│   │   ├── RefreshToken.java
│   │   ├── Session.java
│   │   └── User.java
│   ├── exception/
│   │   ├── ErrorCode.java
│   │   ├── GlobalExceptionHandler.java
│   │   ├── SessionNotFoundException.java
│   │   ├── TokenExpiredException.java
│   │   ├── TokenInvalidException.java
│   │   └── UserAlreadyExistsException.java
│   ├── repository/
│   │   ├── BlacklistedTokenRepository.java
│   │   ├── RefreshTokenRepository.java
│   │   ├── SessionRepository.java
│   │   └── UserRepository.java
│   ├── security/
│   │   ├── JwtFilter.java
│   │   └── JwtUtil.java
│   ├── service/
│   │   ├── AuthService.java
│   │   ├── CustomUserDetailsService.java
│   │   └── SessionService.java
│   └── SessionGuardApplication.java
├── src/main/resources/
│   └── application.properties
└── pom.xml
```

## 🧪 Testing

Run tests with Maven:
```bash
./mvnw test
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📧 Contact

For questions or support, please open an issue on GitHub.

---

⭐ **Star this repo** if you found it helpful!</content>
<parameter name="filePath">f:\Projects\Springboot_Projects\Session_Guard\sessionguard-api\README.md
