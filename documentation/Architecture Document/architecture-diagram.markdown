# StayHub - Architecture Diagram and Justification

**Note**: This document describes the architecture diagram for StayHub, a modern accommodation booking platform, and justifies the chosen architecture. The design follows a monolithic architecture with an Extended MVC pattern, incorporating a logical security layer to handle JWT, password encryption, and validations. It aligns with user stories (US-001 to US-020, HOST-001 to HOST-010), use cases (UC-001 to UC-025), business rules (RN-001 to RN-083), non-functional requirements (RNF-001 to RNF-039), component system (component-system.md), wireframes (wireframes.md), and mockups (mockups.md). The interface uses Spanish for user-facing elements (RNF-028), with documentation in English for technical consistency.

## Architecture Diagram

The following diagram represents the monolithic architecture with Extended MVC for StayHub, including a logical security layer.
``` ascii
+-------------------------------------------+
|                Client (User)              |
|  - Browser (Angular Frontend)             |
|  - HTTP Requests (REST API, Bearer Token) |
+-------------------------------------------+
                    |
                    v
+------------------------------------------------------+
|          Presentation Layer                          |
|  - Controllers (UserController, etc.)                |
|  - REST Endpoints (POST /api/users/register, etc.)   |
|  - Security Filter (JWT Validation)                  |
+------------------------------------------------------+
                    |
                    v
+-----------------------------------------------+
|          Service Layer                        |
|  - Services (UserService, ReservationService) |
|  - Business Logic (RN-001 to RN-083)          |
|  - Security: Password Encryption (BCrypt)     |
|  - DTOs (UserDTO, ReservationDTO)             |
+-----------------------------------------------+
                    |
                    v
+--------------------------------------------+
|          Data Access Layer                 |
|  - Repositories (UserRepository, etc.)     |
|  - JPA/Hibernate (SQL Queries)             |
|  - Security: Email Uniqueness Validation   |
+--------------------------------------------+
                    |
                    v
+------------------------------------------------+
|          Database (MariaDB)                    |
|  - Tables: Users, Accommodations, Reservations |
+------------------------------------------------+
                    |
                    v
+---------------------------------------------------+
|          External Services                        |
|  - Email Service (Notifications, RN-017)          |
|  - Mapbox (Location, US-004)                      | 
|  - Image Storage (Accommodation Images, HOST-003) |
+---------------------------------------------------+

+-------------------------------------------+
|          Security Components              |
|  - SecurityConfig (Spring Security, JWT)  |
|  - JwtUtil (Token Generation/Validation)  |
|  - PasswordEncoder (BCrypt, RN-067)       |
+-------------------------------------------+
```
### Diagram Explanation
- **Client (User)**: Browser with Angular frontend, consuming REST APIs (e.g., `POST /api/users/register`) with JWT tokens in headers (`Authorization: Bearer <token>`).
- **Presentation Layer**: REST controllers (`UserController`, `AccommodationController`) in the `controller` folder, handling HTTP requests/responses. A `SecurityFilter` validates JWT tokens for protected endpoints.
- **Service Layer**: Services (`UserService`, `ReservationService`) in the `service` folder, implementing business logic (RN-001 to RN-083) and password encryption (BCrypt, RN-067). Uses DTOs (`UserDTO`, `ReservationDTO`).
- **Data Access Layer**: JPA repositories (`UserRepository`, `AccommodationRepository`) in the `repository` folder, executing SQL queries via Hibernate. Validates email uniqueness (RN-001).
- **Database**: MariaDB with tables (`Users`, `Accommodations`, `Reservations`) for data storage.
- **External Services**: Integrations with email service (RN-017), Mapbox (US-004), and image storage (HOST-003).
- **Security Components**: Logical layer with `SecurityConfig` (Spring Security setup in `config`), `JwtUtil` (token generation/validation in `util`), and `PasswordEncoder` (BCrypt in `util`).

## Architecture Justification

The monolithic architecture with an Extended MVC pattern, enhanced with a logical security layer, is the optimal choice for StayHub due to the following reasons:

### 1. Simplicity and Rapid Development
- A monolithic architecture allows developing, testing, and deploying StayHub as a single application, reducing complexity compared to microservices.
- The Extended MVC pattern organizes code into clear layers (`controller`, `service`, `repository`, `dto`, `util`, `config`), with security integrated in `config` (SecurityConfig) and `util` (JwtUtil, PasswordEncoder) without adding unnecessary layers.
- Spring Security simplifies JWT, password encryption, and authorization, minimizing development effort.

### 2. Alignment with Functional Requirements
- **User Stories**: Supports US-001 (registration), US-002 (login with JWT), US-010 (password change), HOST-001 (host registration), HOST-002 (host login), and HOST-003 to HOST-010 (accommodation and reservation management with authorization).
- **Use Cases**: Covers UC-001, UC-002, UC-003, UC-011, UC-012 (authentication), and UC-021, UC-022 (security validations).
- **Business Rules**: Implements RN-001, RN-066 (unique email), RN-004 (incorrect credentials), RN-006 (password verification), RN-067, RN-068 (BCrypt encryption), RN-045, RN-046 (host authorization).

### 3. Compliance with Non-Functional Requirements
- **RNF-012 (Security)**: The security layer includes:
  - **JWT**: Generates and validates tokens for authentication (US-002, HOST-002) in the presentation layer (`SecurityFilter`).
  - **BCrypt**: Encrypts passwords in the service layer (`UserService`, SYS-002, RN-067).
  - **Email Uniqueness**: Validates emails in the data access layer (`UserRepository`, SYS-001, RN-001).
  - **Authorization**: Restricts endpoint access based on roles (e.g., user vs. host, RN-045, RN-046).
- **RNF-001 (Performance)**: Security operations (JWT validation, BCrypt) are efficient and do not impact the 5s API timeout.
- **RNF-028 (Spanish GUI)**: Error messages (e.g., “El correo electrónico ya está en uso”, “Credenciales incorrectas”) are in Spanish.
- **RNF-039 (Accessibility)**: The Angular UI includes ARIA labels and keyboard navigation, unaffected by security.

### 4. Sufficient Initial Scalability
- The monolithic architecture with security handles moderate user volumes with SQLite and Spring Boot.
- The modular structure allows refactoring to microservices if StayHub scales, with security components reusable in each service.

### 5. Seamless Integration with External Services
- The security layer does not interfere with integrations like Mapbox (US-004), email service (US-005, HOST-010), or image storage (HOST-003), as it only protects endpoints.

### 6. Leveraging Developer Skills
- Spring Security and Angular align with expertise in Spring Boot and dynamic frontends.
- Standard security configurations (JWT, BCrypt) are well-documented, reducing development time.

### 7. Alternatives Considered
- **No Security Layer**: Discarded due to violation of RNF-012, exposing sensitive data (passwords, reservations).
- **Microservices with Distributed Security**: Too complex for StayHub’s scope, requiring service management and inter-service communication.
- **OAuth2**: More complex than JWT for this case, where a simple token-based authentication is sufficient.

### 8. Lightweight Security Implementation
- **SecurityConfig**: Configures Spring Security with JWT and BCrypt in `config`.
- **JwtUtil**: Handles token generation/validation in `util`.
- **PasswordEncoder**: Uses BCrypt for password encryption in `util`.
- **SecurityFilter**: Validates tokens in the presentation layer, ensuring protected endpoints require authentication.
- This approach adds security without overloading development, maintaining the monolithic simplicity.

This architecture ensures a balance of simplicity, functionality, security, and maintainability, making it ideal for StayHub’s current needs while allowing future scalability.