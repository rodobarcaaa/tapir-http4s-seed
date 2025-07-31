# Authentication Guide

This project now includes JWT-based authentication. Here's how to use it:

## Endpoints

### User Registration
**POST /auth/register**

Register a new user account.

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "email": "user@example.com", 
    "password": "securepassword"
  }'
```

Response:
```json
{
  "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": "uuid-here",
    "username": "newuser",
    "email": "user@example.com",
    "createdAt": "2025-07-29T19:10:03.946Z"
  }
}
```

### User Login
**POST /auth/login**

Authenticate an existing user.

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "securepassword"
  }'
```

Response: Same as registration.

### Token Validation
**GET /auth/validate**

Validate a JWT token (useful for debugging or checking token status).

```bash
curl -X GET http://localhost:8080/auth/validate \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

Response:
```
"Token is valid for user: newuser"
```

## Features

- **JWT Tokens**: Stateless authentication using JSON Web Tokens
- **BCrypt Password Hashing**: Secure password storage with salt rounds
- **Token Expiration**: Tokens expire after 24 hours by default
- **In-Memory Storage**: User data is stored in memory for simplicity (replace with database in production)
- **Comprehensive Error Handling**: Proper HTTP status codes and error messages
- **Swagger Documentation**: All endpoints are documented in the OpenAPI spec

## Security Configuration

The JWT secret key is currently hardcoded as "your-secret-key-change-in-production". 
**Important**: Change this in production and load it from environment variables or secure configuration.

## Error Responses

The API returns appropriate HTTP status codes:

- **201 Created**: Successful registration
- **200 OK**: Successful login or token validation
- **401 Unauthorized**: Invalid credentials or expired/invalid token
- **409 Conflict**: Username or email already exists

## Integration

The authentication system is fully integrated with the existing API structure:

- Uses the same error handling patterns as other endpoints
- Follows the same code organization (domain, application, infrastructure layers)
- Included in Swagger documentation
- Uses the same testing framework

## Next Steps

To extend the authentication system:

1. **Add Database Persistence**: Replace InMemoryUserRepository with a database implementation
2. **Add Role-Based Access Control**: Extend User model with roles/permissions
3. **Add Password Reset**: Implement password reset via email
4. **Add Refresh Tokens**: Implement token refresh mechanism
5. **Add OAuth Integration**: Support for OAuth providers like Google, GitHub
6. **Add Rate Limiting**: Prevent brute force attacks
7. **Add Email Verification**: Verify email addresses during registration

The modular design makes these extensions straightforward to implement.