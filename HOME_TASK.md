# Java Developer Home Task - User Registration & Authentication Service

## Overview
Create a RESTful web service that implements user registration and authentication functionality based on SAP Customer Data Cloud (Gigya) concepts.

## Task Description
Implement a Spring Boot application that provides user management capabilities including registration, login, profile management, and email verification.

## Requirements

### Technical Stack
- **Java**: 11 or higher
- **Framework**: Spring Boot 2.7+ or 3.x
- **Database**: H2 (in-memory) or any SQL database
- **Build Tool**: Maven or Gradle
- **API Documentation**: Swagger/OpenAPI
- **Testing**: Postman collection or Swagger UI

### Functional Requirements

#### 1. User Registration (`/api/accounts/register`)
- Accept user registration data (email, password, profile information)
- Validate input data:
  - Email format validation
  - Password strength requirements (min 8 characters, at least 1 uppercase, 1 lowercase, 1 digit)
  - Required fields: email, password, firstName, lastName
- Store user data securely (hash passwords)
- Generate email verification token
- Return response with user UID and registration status
- Handle duplicate email registration

**Request Example:**
```json
{
  "email": "user@example.com",
  "password": "SecurePass123",
  "profile": {
    "firstName": "John",
    "lastName": "Doe",
    "birthDate": "1990-01-15",
    "phoneNumber": "+972501234567"
  }
}
```

**Response Example:**
```json
{
  "UID": "550e8400-e29b-41d4-a716-446655440000",
  "statusCode": 200,
  "statusMessage": "OK",
  "emailVerificationRequired": true,
  "verificationToken": "abc123xyz"
}
```

#### 2. User Login (`/api/accounts/login`)
- Accept email/password credentials
- Authenticate user
- Generate session token/JWT
- Return user profile data on successful login
- Handle account not verified, locked, or invalid credentials

**Request Example:**
```json
{
  "loginID": "user@example.com",
  "password": "SecurePass123"
}
```

**Response Example:**
```json
{
  "UID": "550e8400-e29b-41d4-a716-446655440000",
  "sessionToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "profile": {
    "firstName": "John",
    "lastName": "Doe",
    "email": "user@example.com",
    "isVerified": true
  },
  "statusCode": 200
}
```

#### 3. Email Verification (`/api/accounts/verify`)
- Accept verification token
- Verify user email
- Update user status to verified
- Return verification result

**Request Example:**
```json
{
  "token": "abc123xyz",
  "email": "user@example.com"
}
```

#### 4. Get Account Info (`/api/accounts/info`)
- Require authentication (session token/JWT)
- Return current user profile information
- Include registration date, last login, verification status

**Headers:**
```
Authorization: Bearer {sessionToken}
```

**Response Example:**
```json
{
  "UID": "550e8400-e29b-41d4-a716-446655440000",
  "profile": {
    "firstName": "John",
    "lastName": "Doe",
    "email": "user@example.com",
    "birthDate": "1990-01-15",
    "phoneNumber": "+972501234567"
  },
  "isVerified": true,
  "registrationDate": "2026-01-15T10:30:00Z",
  "lastLogin": "2026-01-18T08:45:00Z"
}
```

#### 5. Update Profile (`/api/accounts/profile`)
- Require authentication
- Allow updating profile information
- Validate updated data
- Return updated profile

**Request Example:**
```json
{
  "profile": {
    "firstName": "John",
    "lastName": "Smith",
    "phoneNumber": "+972509876543"
  }
}
```

#### 6. Password Reset Request (`/api/accounts/password/reset-request`)
- Accept email address
- Generate password reset token
- Simulate sending reset email (log token)
- Return success status

**Request Example:**
```json
{
  "email": "user@example.com"
}
```

#### 7. Password Reset (`/api/accounts/password/reset`)
- Accept reset token and new password
- Validate token and expiration
- Update password
- Return success status

**Request Example:**
```json
{
  "token": "reset123xyz",
  "newPassword": "NewSecurePass456"
}
```

### Data Model

#### User Entity
```
- uid (UUID, Primary Key)
- email (String, Unique, Not Null)
- passwordHash (String, Not Null)
- firstName (String)
- lastName (String)
- birthDate (LocalDate)
- phoneNumber (String)
- isVerified (Boolean, Default: false)
- isActive (Boolean, Default: true)
- registrationDate (LocalDateTime)
- lastLoginDate (LocalDateTime)
- verificationToken (String, Nullable)
- verificationTokenExpiry (LocalDateTime, Nullable)
- passwordResetToken (String, Nullable)
- passwordResetTokenExpiry (LocalDateTime, Nullable)
```

### Error Handling
Implement proper error responses for:
- Invalid credentials (401)
- Account not verified (403)
- Account locked/inactive (403)
- Duplicate email registration (400)
- Invalid token (400)
- Expired token (400)
- Validation errors (400)
- Not found (404)
- Server errors (500)

**Error Response Format:**
```json
{
  "statusCode": 400,
  "errorCode": "INVALID_EMAIL",
  "errorMessage": "The email address is invalid",
  "timestamp": "2026-01-18T10:30:00Z"
}
```

### Security Requirements
1. **Password Storage**: Use BCrypt or similar hashing algorithm
2. **Token Security**: Generate secure random tokens for verification/reset
3. **Token Expiration**: 
   - Email verification token: 24 hours
   - Password reset token: 1 hour
   - Session token/JWT: 30 minutes (with refresh capability)
4. **Input Validation**: Validate and sanitize all inputs
5. **SQL Injection Prevention**: Use parameterized queries/JPA
6. **CORS Configuration**: Configure appropriate CORS settings

### Testing Requirements
1. **Unit Tests**: 
   - Service layer logic
   - Validation logic
   - Password hashing/verification
   
2. **Integration Tests**:
   - API endpoints
   - Database operations
   - Authentication flow

3. **Postman Collection** or **Swagger UI**:
   - Include all API endpoints
   - Provide example requests
   - Document expected responses

### Bonus Points (Optional)
1. Implement refresh token mechanism
2. Add rate limiting for login attempts
3. Implement account lockout after failed login attempts
4. Add audit logging for security events
5. Implement social login (Google/Facebook mock)
6. Add profile picture upload functionality
7. Implement user session management (view/revoke sessions)
8. Docker configuration for easy deployment
9. Use Redis for token storage
10. Implement 2FA (Two-Factor Authentication)

## Deliverables

1. **Source Code**:
   - Clean, well-structured code
   - Proper package organization
   - Meaningful variable and method names
   - Code comments where necessary

2. **README.md**:
   - Project setup instructions
   - How to run the application
   - How to run tests
   - API documentation or Swagger UI URL
   - Database schema description
   - Any assumptions made

3. **API Documentation**:
   - Swagger/OpenAPI specification
   - Postman collection (JSON export)
   - Example requests and responses

4. **Database**:
   - Schema creation scripts (if not using JPA auto-generation)
   - Sample data (optional)

5. **Tests**:
   - Unit tests with good coverage
   - Integration tests for main flows

## Evaluation Criteria

1. **Functionality** (30%):
   - All required endpoints work correctly
   - Proper error handling
   - Business logic implementation

2. **Code Quality** (25%):
   - Clean code principles
   - SOLID principles
   - Design patterns usage
   - Code organization

3. **Security** (20%):
   - Proper password handling
   - Token management
   - Input validation
   - Security best practices

4. **Testing** (15%):
   - Test coverage
   - Test quality
   - Edge cases handling

5. **Documentation** (10%):
   - Clear README
   - API documentation
   - Code comments

## Time Estimate
**3-5 days** for completion (including testing and documentation)

## Submission
Please submit:
1. Git repository URL (GitHub/GitLab/Bitbucket) or ZIP file
2. README with setup instructions
3. Postman collection or Swagger UI URL
4. Any additional notes or assumptions

## Questions?
If you have any questions or need clarifications, please contact [recruiter email/contact].

---

**Good luck! We look forward to reviewing your submission.**
