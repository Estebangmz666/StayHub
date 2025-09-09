# StayHub API DTOs Documentation

This document outlines the Data Transfer Objects (DTOs) used in the StayHub application to facilitate data exchange between layers. DTOs are organized under the `edu.uniquindio.stayhub.api.dto` package with sub-packages per entity or functionality.

## Package Structure
- `edu.uniquindio.stayhub.api.dto.accommodation`: DTOs related to Accommodation management.
- `edu.uniquindio.stayhub.api.dto.auth`: DTOs related to Authentication and security.
- `edu.uniquindio.stayhub.api.dto.comment`: DTOs related to Comment management.
- `edu.uniquindio.stayhub.api.dto.host`: DTOs related to Host profile management.
- `edu.uniquindio.stayhub.api.dto.reservation`: DTOs related to Reservation management.
- `edu.uniquindio.stayhub.api.dto.user`: DTOs related to User management.

## DTO Listings

### Accommodation DTOs
- **AccommodationRequestDTO**: Used for creating accommodations (`POST /api/v1/accommodations`).
    - Fields: `title` (String), `description` (String), `capacity` (Integer), `longitude` (Double), `latitude` (Double), `locationDescription` (String), `city` (String), `pricePerNight` (BigDecimal), `mainImage` (String), `images` (List<String>).
    - Validation: `@NotBlank`, `@Size`, `@NotNull`, `@Positive`, `@URL` (messages in Spanish for user-facing errors).
- **AccommodationResponseDTO**: Response for accommodation details (`GET /api/v1/accommodations`, `GET /api/v1/accommodations/{accommodationId}`).
    - Fields: `id` (Long), `title` (String), `description` (String), `capacity` (Integer), `mainImage` (String), `longitude` (Double), `latitude` (Double), `locationDescription` (String), `city` (String), `pricePerNight` (BigDecimal), `images` (List<String>).
- **AccommodationUpdateDTO**: Used for updating accommodations (`PUT /api/v1/accommodations/{accommodationId}`).
    - Fields: `title` (String), `description` (String), `capacity` (Integer), `longitude` (Double), `latitude` (Double), `locationDescription` (String), `city` (String), `pricePerNight` (BigDecimal), `mainImage` (String), `images` (List<String>).
    - Validation: `@NotBlank`, `@Size`, `@NotNull`, `@Positive`, `@URL` (messages in Spanish for user-facing errors).
- **SearchResponseDTO**: Response for paginated search results (`GET /api/v1/accommodations`).
    - Fields: `content` (List<AccommodationResponseDTO>), `page` (int), `size` (int), `totalElements` (long).

### Authentication DTOs
- **PasswordResetDTO**: Used for password reset requests (e.g., `POST /api/v1/auth/reset-password`).
    - Fields: `email` (String), `newPassword` (String).
    - Validation: `@NotBlank`, `@Email`, `@Pattern` (password strength, messages in Spanish for user-facing errors).
- **TokenResponseDTO**: Response for login (`POST /api/v1/auth/login`).
    - Fields: `token` (String).

### Comment DTOs
- **CommentRequestDTO**: Used for creating comments (`POST /api/v1/comments`).
    - Fields: `userId` (Long), `accommodationId` (Long), `text` (String), `rating` (Integer).
    - Validation: `@NotNull`, `@NotBlank`, `@Size` (max 500, messages in Spanish for user-facing errors).
- **CommentResponseDTO**: Response for comment details.
    - Fields: `id` (Long), `userId` (Long), `accommodationId` (Long), `text` (String), `rating` (Integer), `createdAt` (LocalDateTime).
- **CommentUpdateDTO**: Used for updating comments (e.g., `PUT /api/v1/comments/{id}`).
    - Fields: `text` (String), `rating` (Integer).
    - Validation: `@NotBlank`, `@NotNull`, `@Size` (max 500, messages in Spanish for user-facing errors).

### Host DTOs
- **HostProfileDTO**: Used for creating or updating host profiles (e.g., `POST /api/v1/host-profiles`).
    - Fields: `description` (String).
    - Validation: `@NotBlank`, `@Size` (max 500, messages in Spanish for user-facing errors).

### Reservation DTOs
- **ReservationRequestDTO**: Used for creating reservations (`POST /api/v1/reservations`).
    - Fields: `userId` (Long), `accommodationId` (Long), `checkInDate` (LocalDate), `checkOutDate` (LocalDate), `numberOfGuests` (Integer), `status` (ReservationStatus).
    - Validation: `@NotNull`, `@FutureOrPresent`, `@Future`, `@Positive` (messages in Spanish for user-facing errors).
- **ReservationResponseDTO**: Response for reservation details.
    - Fields: `id` (Long), `userId` (Long), `accommodationId` (Long), `checkInDate` (LocalDate), `checkOutDate` (LocalDate), `numberOfGuests` (Integer), `status` (ReservationStatus).
- **ReservationUpdateDTO**: Used for updating reservation status (e.g., `PATCH /api/v1/reservations/{id}`).
    - Fields: `status` (ReservationStatus).
    - Validation: `@NotNull` (messages in Spanish for user-facing errors).

### User DTOs
- **UpdateProfileDTO**: Used for updating user profile (`PUT /api/v1/users/{id}`).
    - Fields: `name` (String), `phoneNumber` (String), `birthDate` (LocalDate), `profilePicture` (String), `description` (String), `legalDocuments` (List<String>).
    - Validation: `@NotNull`, `@Pattern` (phone), `@Size`, `@URL`, `@Valid` (messages in Spanish for user-facing errors).
- **UserRegistrationDTO**: Used for user registration (`POST /api/v1/auth/register`).
    - Fields: `email` (String), `password` (String), `name` (String), `phoneNumber` (String), `birthDate` (LocalDate), `role` (Role).
    - Validation: `@NotBlank`, `@Email`, `@Pattern` (password, phone), `@Past` (messages in Spanish for user-facing errors).
- **UserResponseDTO**: Response for user details.
    - Fields: `id` (Long), `email` (String), `name` (String), `phoneNumber` (String), `birthDate` (LocalDate), `role` (Role), `profilePicture` (String).
- **UserUpdateDTO**: [Deprecated] Used for updating user details (alternative to UpdateProfileDTO).
    - Fields: `name` (String), `phoneNumber` (String), `birthDate` (LocalDate).
    - Validation: `@NotBlank`, `@Pattern` (phone), `@Size` (messages in Spanish for user-facing errors).
    - Note: Overlaps with `UpdateProfileDTO`; consider removing or merging.

## Notes
- All DTOs use Lombok (`@Getter`, `@Setter`) for simplicity.
- Validation is enforced with Jakarta Validation annotations (e.g., `@NotNull`, `@Size`, `@Email`, `@Pattern`, `@URL`) with Spanish messages for user-facing errors.
- Mapping between DTOs and entities will be handled with MapStruct.
- `UserUpdateDTO` is marked as deprecated due to overlap with `UpdateProfileDTO`; consolidate into `UpdateProfileDTO` if possible.
- Optional fields in `UpdateProfileDTO` use `@Nullable` to indicate flexibility.