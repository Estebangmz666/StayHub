
# StayHub - Test Plan

## 1. Introduction

- **Project Name**: StayHub
- **Feature Under Test**: Authentication, Accommodations, Reservations, and Comments (MVP)
- **General Description**: This test plan defines the approach to verify that the StayHub REST API behaves as specified in the OpenAPI definition (`api-spec.yaml`). The goal is to ensure the core booking flows—user registration/login, accommodation search & management, reservation creation, and comments—work as expected.

---

## 2. Test Scope

- **Objective**: Validate that the endpoints respond correctly to valid/invalid inputs across happy paths, error handling, and authentication scenarios.
- **Endpoints in Scope** (from `api-spec.yaml`):
  - `POST /auth/register` — Register a new user (guest/host)
  - `POST /auth/login` — Authenticate and return JWT
  - `GET /accommodations` — Search accommodations with filters
  - `POST /accommodations` — Create accommodation (host) *(requires JWT)*
  - `GET /accommodations/{accommodationId}` — Get accommodation details *(requires JWT)*
  - `DELETE /accommodations/{accommodationId}` — Soft delete accommodation *(requires JWT)*
  - `POST /reservations` — Create reservation *(requires JWT)*
  - `POST /comments` — Create comment *(requires JWT)*

Out of scope for this iteration: user/profile updates, listing reservations by user, payment flows, media upload management, and advanced search ranking.

---

## 3. Test Environments

- **Development**: `http://localhost:8080/api/v1`
- **Staging**: `https://staging.stayhub-api.example.com/api/v1`
- **Production**: `https://api.stayhub.example.com/api/v1`

> Note: Environment base URLs can be adjusted via Angular environment files and Spring profiles.

---

## 4. Acceptance Criteria

A test case is **passed** when all the following apply:

1. The API returns the **expected HTTP status code** (`200`, `201`, `400`, `401`, `404`, etc.).
2. The **response schema** matches the OpenAPI definition (field names, types, required properties).
3. **Headers** are correct (e.g., `Content-Type: application/json`, `Authorization: Bearer <token>` when required).
4. **User-facing messages** in errors/validations are **Spanish**, concise, and consistent (e.g., `"La fecha de checkout debe ser después de la fecha de check-in"`).
5. **Business rules** hold, including:
   - `checkOutDate` must be strictly after `checkInDate`.
   - `numberOfGuests` must be >= 1 and (when validated server-side) must not exceed the accommodation capacity.
   - Soft delete returns confirmation and does not physically remove records.

---

## 5. Test Strategy

### 5.1 Types of Tests
1. **Happy Path**: Valid inputs produce successful outcomes.
2. **Error Validation**: Missing/invalid fields, invalid formats, out-of-range values (e.g., rating 0), and business rule violations.
3. **Authentication/Authorization**: Accessing protected endpoints without/with invalid JWT.
4. **Boundary & Edge Cases**: Date boundaries, min/max values, empty lists, nonexistent IDs.
5. **Contract Checks**: Response structure matches OpenAPI (field presence/types).

> Execution is out of scope for this deliverable (planning only). Suggested tools for future execution: Postman/Newman, REST Assured, JUnit, Mockito, Cypress.

---

## 6. Test Details

### 6.1 Test Case Structure
Each test case is documented as follows:
- **ID**
- **Endpoint**
- **HTTP Method**
- **Description**
- **Parameters/Query**
- **Headers**
- **Request Body**
- **Expected Status Code**
- **Expected Response Body**
- **Pass Criteria**

---

### Test Case TC-001 — Register User (Success)
- **Endpoint**: `POST /auth/register`
- **Method**: POST
- **Description**: Register a new guest user.
- **Parameters/Query**: None
- **Headers**: `Content-Type: application/json`
- **Request Body**:
```json
{
  "email": "guest1@example.com",
  "password": "S3cure!Pass",
  "role": "USER",
  "name": "Guest One",
  "phoneNumber": "+57 3000000000",
  "birthDate": "2000-05-01"
}
```
- **Expected Status Code**: `201 Created`
- **Expected Response Body** (example):
```json
{
  "id": 101,
  "email": "guest1@example.com",
  "role": "USER",
  "name": "Guest One",
  "phoneNumber": "+57 3000000000",
  "birthDate": "2000-05-01",
  "isDeleted": false
}
```
- **Pass Criteria**: Returns `201` and persisted user fields (excluding password) per schema.

---

### Test Case TC-002 — Register User (Duplicate Email)
- **Endpoint**: `POST /auth/register`
- **Method**: POST
- **Description**: Attempt to register an already used email.
- **Headers**: `Content-Type: application/json`
- **Request Body**: Same email as an existing user
- **Expected Status Code**: `400 Bad Request`
- **Expected Response Body** (example):
```json
{
  "message": "El correo ya está registrado",
  "code": 400
}
```
- **Pass Criteria**: Returns `400`, message in **Spanish**.

---

### Test Case TC-003 — Login (Success)
- **Endpoint**: `POST /auth/login`
- **Method**: POST
- **Description**: Authenticate a user and retrieve a JWT token.
- **Headers**: `Content-Type: application/json`
- **Request Body**:
```json
{ "email": "guest1@example.com", "password": "S3cure!Pass" }
```
- **Expected Status Code**: `200 OK`
- **Expected Response Body** (example):
```json
{ "token": "<jwt-token>" }
```
- **Pass Criteria**: Returns `200` with a non-empty `token` string.

---

### Test Case TC-004 — Login (Invalid Credentials)
- **Endpoint**: `POST /auth/login`
- **Method**: POST
- **Description**: Wrong email/password.
- **Headers**: `Content-Type: application/json`
- **Request Body**:
```json
{ "email": "guest1@example.com", "password": "wrong" }
```
- **Expected Status Code**: `401 Unauthorized`
- **Expected Response Body** (example):
```json
{
  "message": "Credenciales inválidas",
  "code": 401
}
```
- **Pass Criteria**: Returns `401`, message in **Spanish**.

---

### Test Case TC-005 — Register User (Invalid Password)

- **Endpoint**: `POST /auth/register`
- **Method**: POST
- **Description**: Attempt to register with invalid password format.
- **Headers**: `Content-Type: application/json`
- **Request Body**: 
```json
{
  "message": "La contraseña debe tener al menos 8 caracteres, mayúsculas y números",
  "code": 400
}
```
- **Expected Status Code:** `400 Bad Request`
- **Expected Response Body:**
```json
{
  "message": "La contraseña debe tener al menos 8 caracteres, mayúsculas y números",
  "code": 400
}
```
- **Pass Criteria:** Returns `400`, message in Spanish.

---

### Test Case TC-006 - Register User (Host Role)

- **Endpoint:** `POST /auth/register`
- **Method:** POST
- **Description:** Register a new host user
- **Headers:** `Content-Type: application/json`
- **Request Body:**
```json
{
  "email": "host1@example.com",
  "password": "S3cure!Pass",
  "role": "HOST",
  "name": "Host One",
  "phoneNumber": "+57 3000000000",
  "birthDate": "1990-01-01"
}
```
- **Expected Status Code:** `201 Created`
- **Expected Response Body:**
```json
{
  "email": "host1@example.com",
  "password": "S3cure!Pass",
  "role": "HOST",
  "name": "Host One",
  "phoneNumber": "+57 3000000000",
  "birthDate": "1990-01-01"
}

```
- **Pass Criteria:** Returns `201` and persisted fields (excluding password)

---

### Test Case TC-007 - Login (Invalid Credential)

- **Endpoint:** `POST /auth/login`
- **Method:** POST
- **Description:** Attempt login with incorrect password.
- **Headers:** `Content-Type: application/json`
- **Request Body:**
```json
{
  "email": "guest1@example.com",
  "password": "WrongPass"
}
```
- **Expected Status Code:** `401 Unauthorized`
- **Expected Response Body:**
```json
{
  "message": "Correo o contraseña incorrectos",
  "code": 401
}
```

---

### Test Case TC-101 — Search Accommodations (Success)
- **Endpoint**: `GET /accommodations`
- **Method**: GET
- **Description**: Search by city and dates.
- **Parameters/Query**:
  - `city=Armenia`
  - `checkInDate=2025-11-10`
  - `checkOutDate=2025-11-12`
  - `minGuests=2`
- **Headers**: `Content-Type: application/json`
- **Expected Status Code**: `200 OK`
- **Expected Response Body** (example):
```json
[
  {
    "id": 2001,
    "title": "Apto céntrico",
    "city": "Armenia",
    "capacity": 3,
    "pricePerNight": 180000.0,
    "isDeleted": false
  }
]
```
- **Pass Criteria**: Returns `200` with an array of `Accommodation` objects matching filters.

---

### Test Case TC-102 — Search Accommodations (Invalid Dates)
- **Endpoint**: `GET /accommodations`
- **Method**: GET
- **Description**: `checkOutDate` before `checkInDate`.
- **Parameters/Query**:
  - `city=Armenia`
  - `checkInDate=2025-11-12`
  - `checkOutDate=2025-11-10`
- **Expected Status Code**: `400 Bad Request`
- **Expected Response Body** (example):
```json
{
  "message": "La fecha de checkout debe ser después de la fecha de check-in",
  "code": 400
}
```
- **Pass Criteria**: Returns `400`, message in **Spanish**.

---

### Test Case TC-103 - Update Accommodation (Success)
- **Endpoint**: `PUT /accommodations/{accommodationId}`
- **Method**: PUT
- **Headers:**
  - `Authorization: Bearer <valid-host-jwt>`
- **Path**: `{accommodationId}=2001`
- **Request Body**:
```json
{
  "title": "Casa en El Poblado Actualizada",
  "description": "Nueva descripción",
  "capacity": 5,
  "longitude": -74.0,
  "latitude": 4.0,
  "locationDescription": "Nueva ubicación",
  "city": "Medellin",
  "pricePerNight": 200.000,
  "mainImage": "https://newmainimage.com",
  "images": ["https://newimage1.com", "https://newimage2.com"]
}
```
- **Expected Status Code**: `200 OK`
- **Expected Response Body**: Updated `Accommodation` object.
- **Pass Criteria**: Returns `200` and updated fields per schema.

---

### Test Case TC-104 - Update Accommodation (Invalid Capacity)
- **Endpoint**: `PUT /accommodations/{accommodationId}`
- **Method**: PUT
- **Headers:**
  - `Authorization: Bearer <valid-host-jwt>`
- **Path**: `{accommodationId}=2001`
- **Request Body**:
```json
{
  "title": "Casa en El Poblado Actualizada",
  "description": "Nueva descripción",
  "capacity": 0,
  "longitude": -74.0,
  "latitude": 4.0,
  "locationDescription": "Nueva ubicación",
  "city": "Medellin",
  "pricePerNight": 200.000,
  "mainImage": "https://newmainimage.com",
  "images": ["https://newimage1.com", "https://newimage2.com"]
}
```
- **Expected Status Code:** `400 Bad Request`
- **Expected Response Body:**
```json
{
  "message": "La capacidad debe ser mayor que cero",
  "code": 400
}
```
- **Pass Criteria:** Returns `400`, message in **Spanish**.

---

### Test Case TC-105 - Delete Accommodation (Success)
- **Endpoint**: `DELETE /accommodations/{accommodationId}`
- **Method**: DELETE
- **Headers**: `Authorization: Bearer <valid-host-jwt>`
- **Path**: `{accommodationId}=2001`
- **Expected Status Code:** `200 OK`
- **Expected Response Body:**:
```json
{
  "message": "Alojamiento eliminado exitosamente",
  "code": 200
}
```
- **Pass Criteria:** Returns `200` and confirmation subsequent fetches should show isDeleted=true.

---

### Test Case TC-106 - Delete Accommodation (Unauthorized)
- **Endpoint:** `DELETE /accommodations/{accommodationId}`
- **Method:** DELETE
- **Headers:** None
- **Path:** `{accommodationId}=2001`
- **Expected Status Code:** `401 Unauthorized
- **Expected Response Body:**
```json
{
  "message": "No autorizado",
  "code": 401
}
```
- **Pass Criteria:** Returns `401`, message in **Spanish**.

---


### Test Case TC-110 — Create Accommodation (Success)
- **Endpoint**: `POST /accommodations`
- **Method**: POST
- **Description**: Host creates a new accommodation.
- **Headers**:
  - `Content-Type: application/json`
  - `Authorization: Bearer <valid-host-jwt>`
- **Request Body** (minimum):
```json
{
  "title": "Casa campestre",
  "description": "Hermosa casa con vista a las montañas",
  "capacity": 6,
  "mainImage": "https://example.com/img/main.jpg",
  "longitude": -75.6811,
  "latitude": 4.5350,
  "locationDescription": "Vía al aeropuerto",
  "city": "Armenia",
  "pricePerNight": 250000.0,
  "images": ["https://example.com/img/1.jpg", "https://example.com/img/2.jpg"]
}
```
- **Expected Status Code**: `201 Created`
- **Expected Response Body**: A full `Accommodation` object with generated `id`.
- **Pass Criteria**: Returns `201` and persisted fields per schema.

---

### Test Case TC-111 — Create Accommodation (Missing Token)
- **Endpoint**: `POST /accommodations`
- **Method**: POST
- **Headers**: `Content-Type: application/json`
- **Request Body**: Same as TC-110
- **Expected Status Code**: `401 Unauthorized`
- **Expected Response Body** (example):
```json
{
  "message": "No autorizado",
  "code": 401
}
```
- **Pass Criteria**: Returns `401`, message in **Spanish**.

---

### Test Case TC-120 — Get Accommodation Details (Success)
- **Endpoint**: `GET /accommodations/{accommodationId}`
- **Method**: GET
- **Headers**: `Authorization: Bearer <valid-jwt>`
- **Path**: `{accommodationId}=2001`
- **Expected Status Code**: `200 OK`
- **Expected Response Body**: `Accommodation` object with `id=2001`.
- **Pass Criteria**: Returns `200` and correct resource.

---

### Test Case TC-121 — Get Accommodation Details (Not Found)
- **Endpoint**: `GET /accommodations/{accommodationId}`
- **Method**: GET
- **Headers**: `Authorization: Bearer <valid-jwt>`
- **Path**: `{accommodationId}=999999`
- **Expected Status Code**: `404 Not Found`
- **Expected Response Body** (example):
```json
{
  "message": "Alojamiento no encontrado",
  "code": 404
}
```
- **Pass Criteria**: Returns `404`, message in **Spanish**.

---

### Test Case TC-130 — Delete Accommodation (Success, Soft Delete)
- **Endpoint**: `DELETE /accommodations/{accommodationId}`
- **Method**: DELETE
- **Headers**: `Authorization: Bearer <valid-host-jwt>`
- **Path**: `{accommodationId}=2001`
- **Expected Status Code**: `200 OK`
- **Expected Response Body** (example):
```json
{
  "message": "Alojamiento eliminado",
  "id": 2001
}
```
- **Pass Criteria**: Returns `200` with confirmation fields; subsequent fetches should show `isDeleted=true`.

---

### Test Case TC-131 — Delete Accommodation (Unauthorized)
- **Endpoint**: `DELETE /accommodations/{accommodationId}`
- **Method**: DELETE
- **Headers**: *None*
- **Path**: `{accommodationId}=2001`
- **Expected Status Code**: `401 Unauthorized`
- **Expected Response Body** (example):
```json
{
  "message": "No autorizado",
  "code": 401
}
```
- **Pass Criteria**: Returns `401`, message in **Spanish**.

---

### Test Case TC-201 — Create Reservation (Success)
- **Endpoint**: `POST /reservations`
- **Method**: POST
- **Headers**:
  - `Content-Type: application/json`
  - `Authorization: Bearer <valid-guest-jwt>`
- **Request Body**:
```json
{
  "userId": 101,
  "accommodationId": 2001,
  "checkInDate": "2025-12-20",
  "checkOutDate": "2025-12-23",
  "numberOfGuests": 2,
  "status": "PENDING"
}
```
- **Expected Status Code**: `201 Created`
- **Expected Response Body**: Full `Reservation` object with generated `id`.
- **Pass Criteria**: Returns `201` and persisted fields per schema.

---

### Test Case TC-202 — Create Reservation (Invalid Dates)
- **Endpoint**: `POST /reservations`
- **Method**: POST
- **Headers**: `Authorization: Bearer <valid-guest-jwt>`
- **Request Body**: `checkOutDate` <= `checkInDate`
- **Expected Status Code**: `400 Bad Request`
- **Expected Response Body** (example):
```json
{
  "message": "La fecha de checkout debe ser después de la fecha de check-in",
  "code": 400
}
```
- **Pass Criteria**: Returns `400`, message in **Spanish**.

---

### Test Case TC-203 — Create Reservation (Guests Exceed Capacity)
- **Endpoint**: `POST /reservations`
- **Method**: POST
- **Headers**: `Authorization: Bearer <valid-guest-jwt>`
- **Request Body**: `numberOfGuests` > `accommodation.capacity`
- **Expected Status Code**: `400 Bad Request`
- **Expected Response Body** (example):
```json
{
  "message": "Número de huéspedes excede la capacidad",
  "code": 400
}
```
- **Pass Criteria**: Returns `400`, message in **Spanish**.

---

### Test Case TC-204 — Create Reservation (Missing Token)
- **Endpoint**: `POST /reservations`
- **Method**: POST
- **Headers**: *None*
- **Request Body**: Valid reservation payload
- **Expected Status Code**: `401 Unauthorized`
- **Expected Response Body** (example):
```json
{
  "message": "No autorizado",
  "code": 401
}
```
- **Pass Criteria**: Returns `401`, message in **Spanish**.

---

### Test Case TC-301 — Create Comment (Success)
- **Endpoint**: `POST /comments`
- **Method**: POST
- **Headers**:
  - `Content-Type: application/json`
  - `Authorization: Bearer <valid-guest-jwt>`
- **Request Body**:
```json
{
  "userId": 101,
  "accommodationId": 2001,
  "text": "Excelente lugar, muy cómodo",
  "rating": 5,
  "createdAt": "2025-12-24T10:00:00Z"
}
```
- **Expected Status Code**: `201 Created`
- **Expected Response Body**: Full `Comment` object with generated `id`.
- **Pass Criteria**: Returns `201` and persisted fields per schema.

---

### Test Case TC-302 — Create Comment (Rating Out of Range)
- **Endpoint**: `POST /comments`
- **Method**: POST
- **Headers**: `Authorization: Bearer <valid-guest-jwt>`
- **Request Body**: `rating=0`
- **Expected Status Code**: `400 Bad Request`
- **Expected Response Body** (example):
```json
{
  "message": "La calificación debe estar entre 1 y 5",
  "code": 400
}
```
- **Pass Criteria**: Returns `400`, message in **Spanish**.

---

### Test Case TC-303 — Create Comment (Invalid Rating)
- **Endpoint:** `POST /comments`
- **Method:** POST
- **Headers:** `Authorization: Bearer <valid-guest-jwt>`
- **Request Body:** `rating=6`
- **Expected Status Code:** `400 Bad Request`
- **Expected Response Body:**
```json
{
  "message": "La calificación debe estar entre 1 y 5",
  "code": 400
}
```
- **Pass Criteria:** Returns `400`, message in **Spanish**.

---



## 6.2 Authentication Tests (Global)

- **TC-AUTH-01** — Access `GET /accommodations/{id}` without token → Expect `401` + `"No autorizado"`.
- **TC-AUTH-02** — Access `POST /accommodations` with malformed token → Expect `401` + `"Token inválido"`.
- **TC-AUTH-03** — Access `DELETE /accommodations/{id}` with token for a different host (if enforced) → Expect `403` + `"No tiene permisos para esta acción"`.

---

## 7. Results Reporting

For each executed test (future stage), capture:
- **Test Case Name/ID**
- **Expected Result**
- **Actual Result**
- **Pass?** (Yes/No)
- **Observations** (differences, defects, logs, screenshots)

A consolidated report will include total passed/failed, defect summary, and readiness recommendation.

---

## 8. Conclusion

If all acceptance criteria are met with no critical defects, the functionality will be considered ready for staging/production deployment. Otherwise, defects will be logged and prioritized for fixes, followed by regression planning.

---

## Appendix A — Example Test Data

- **Users**:
  - *Guest*: `guest1@example.com` / `S3cure!Pass`
  - *Host*: `host1@example.com` / `S3cure!Pass`
- **JWT Tokens**: Use placeholders during planning (`<valid-guest-jwt>`, `<valid-host-jwt>`); generate real tokens via `POST /auth/login` during execution phase.
- **IDs**:
  - `accommodationId=2001` (assumed existing)
  - Nonexistent ID example: `999999`

---
