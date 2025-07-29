# Authentication API Usage

This implementation adds x-token authentication to the Tapir HTTP4s seed project.

## API Endpoints

### Login
```
POST /auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password"
}
```

**Response:**
```json
{
  "token": "uuid-token-string",
  "user": {
    "id": "user-id",
    "username": "admin",
    "email": "admin@example.com"
  }
}
```

### Secured Endpoint Example
```
GET /secured/example
x-token: your-token-here
```

**Response:**
```json
{
  "message": "This is a secured endpoint",
  "status": "success"
}
```

## Built-in Test Users

- **Username:** `admin`, **Password:** `password`
- **Username:** `user`, **Password:** `password`

## Usage Flow

1. Login to get a token
2. Use the token in the `x-token` header for secured endpoints
3. Invalid tokens return 401 Unauthorized
4. Missing tokens return 400 Bad Request

## Example with curl

```bash
# Login to get token
TOKEN=$(curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}' | jq -r '.token')

# Use token to access secured endpoint
curl -H "x-token: $TOKEN" http://localhost:8080/secured/example
```

## Integration

The authentication system is integrated into the main HTTP API and will appear in the Swagger documentation at `/docs`.