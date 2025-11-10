# StayHub - Use Cases

## Table of Contents
- Guest Role
  - [UC-001: Register User](#uc-001-register-user)
  - [UC-002: Log In](#uc-002-log-in)
  - [UC-003: Change Password](#uc-003-change-password)
  - [UC-004: Search Accommodations](#uc-004-search-accommodations)
  - [UC-005: View Accommodation Details](#uc-005-view-accommodation-details)
  - [UC-006: Make Reservation](#uc-006-make-reservation)
  - [UC-007: Cancel Reservation](#uc-007-cancel-reservation)
  - [UC-008: View Reservation History](#uc-008-view-reservation-history)
  - [UC-009: Leave Comment and Rating](#uc-009-leave-comment-and-rating)
  - [UC-010: Receive Reminders](#uc-010-receive-reminders)

- Host Role
  - [UC-011: Register Host](#uc-011-register-host)
  - [UC-012: Log In](#uc-012-log-in)
  - [UC-013: View Accommodations](#uc-013-view-accommodations)
  - [UC-014: Create Accommodation](#uc-014-create-accommodation)
  - [UC-015: Edit Accommodation](#uc-015-edit-accommodation)
  - [UC-016: Delete Accommodation](#uc-016-delete-accommodation)
  - [UC-017: View Reservations](#uc-017-view-reservations)
  - [UC-018: View Metrics](#uc-018-view-metrics)
  - [UC-019: Respond to Comments](#uc-019-respond-to-comments)
  - [UC-020: Receive Notifications](#uc-020-receive-notifications)

- System Role
  - [UC-021: Validate Unique Email](#uc-021-validate-unique-email)
  - [UC-022: Encrypt Password](#uc-022-encrypt-password)
  - [UC-023: Validate Completed Stay for Comments](#uc-023-validate-completed-stay-for-comments)
  - [UC-024: Send Automatic Reminders (Guest)](#uc-024-send-automatic-reminders-guest)
  - [UC-025: Send Notifications (Host)](#uc-025-send-notifications-host)

## Notes on Use Case Relationships
- **Includes**: Indicates a mandatory sub-process reused across multiple use cases. For example, UC-021 (Validate Unique Email) and UC-022 (Encrypt Password) are included in UC-001, UC-003, UC-011, and UC-012 to ensure email uniqueness and password security. UC-009 includes UC-023 (Validate Completed Stay for Comments) to ensure only completed stays can be reviewed. UC-010 includes UC-024 (Send Automatic Reminders) and UC-020 includes UC-025 (Send Notifications) for notification delivery.
- **Extends**: Indicates optional or conditional behavior. For example, UC-002 (Log In) and UC-012 (Log In) extend to "Forgot Password" in case of incorrect credentials.

## Introduction
Use cases are numbered sequentially within each role (Guest: UC-001 to UC-010, Host: UC-011 to UC-020, System: UC-021 to UC-025) to maintain clarity and align with role-specific functionality.

## Glossary
- **JWT Token**: A JSON Web Token used for secure user authentication, generated upon successful login and included in API requests.
- **Soft Delete**: A mechanism to mark records as "deleted" in the database without physically removing them, preserving historical data.
- **Mapbox**: A third-party service used to display interactive maps for accommodation locations.

## 1. Guest Role ()

### UC-001: Register User
- **Actor**: Guest
- **Description**: Allows a guest to create an account by providing personal details.
- **Preconditions**: The guest is not registered in the system.
- **Basic Flow**:
  1. The guest navigates to the registration page.
  2. The guest enters their name, email, password (minimum 8 characters, uppercase, numbers), phone, role, and birthdate.
  3. The system validates the email uniqueness (includes UC-021: Validate Unique Email).
  4. The system encrypts the password (includes UC-022: Encrypt Password).
  5. The system creates the account and assigns the "Guest" role.
  6. The system displays a confirmation message and redirects the guest to the main page.
- **Alternative Flows**:
  - A1: Email already registered
    - In step 3, if the email exists, the system displays "El correo electrónico ya está en uso" and prompts the guest to try another email.
  - A2: Password does not meet requirements
    - In step 2, if the password is invalid, the system displays an error message indicating the password requirements and prompts the guest to correct it.
- **Postconditions**: The guest account is created, and the guest is redirected to the main page.
- **Exceptions**:
  - If the system fails to save the account, an error message is shown: "Error al crear la cuenta, intenta de nuevo".

### UC-002: Log In
- **Actor**: Guest
- **Description**: Allows a guest to log in to access their account and reservations.
- **Preconditions**: The guest has a registered account.
- **Basic Flow**:
  1. The guest navigates to the login page.
  2. The guest enters their email and password.
  3. The system validates the credentials (includes UC-022: Encrypt Password for comparison).
  4. The system generates a JWT token and redirects the guest to the main page.
- **Alternative Flows**:
  - A1: Incorrect credentials
    - In step 3, if credentials are incorrect, the system displays "Correo electrónico o contraseña incorrectos" and prompts the guest to retry.
  - A2: Forgot password
    - In step 2, the guest selects "Forgot my password", enters their email, and the system sends a recovery code valid for 15 minutes.
- **Postconditions**: The guest is authenticated and redirected to the main page with an active session.
- **Exceptions**:
  - If the system fails to validate credentials, an error message is shown: "Error al iniciar sesión, intenta de nuevo".
  - If the system fails to validate credentials due to a server timeout or network error, display "Error de conexión, intenta de nuevo más tarde" and log the error for debugging.

### UC-003: Change Password
- **Actor**: Guest
- **Description**: Allows a guest to change their password by providing the current and new password, aligning with US-010.
- **Preconditions**: The guest is authenticated and has a valid session.
- **Basic Flow**:
  1. The guest navigates to the profile settings page.
  2. The guest enters their current password and a new password meeting security requirements (minimum 8 characters, uppercase, numbers).
  3. The system validates the current password against the stored encrypted version (includes UC-022: Encrypt Password).
  4. The system validates the new password meets security criteria.
  5. The system encrypts the new password and updates the account.
  6. The system displays a confirmation message: "Contraseña actualizada con éxito".
- **Alternative Flows**:
  - A1: Incorrect current password
    - In step 3, if the current password is incorrect, the system displays "La contraseña actual es incorrecta" and prompts the guest to retry.
  - A2: New password does not meet requirements
    - In step 4, if the new password is invalid, the system displays an error message indicating the password requirements and prompts the guest to correct it.
- **Postconditions**: The guest's password is updated, and a confirmation is shown. The session remains active.
- **Exceptions**:
  - If the system fails to update the password, an error message is shown: "Error al cambiar la contraseña, intenta de nuevo".

### UC-004: Search Accommodations
- **Actor**: Guest
- **Description**: Allows a guest to search for available accommodations using filters.
- **Preconditions**: The guest is on the search page.
- **Basic Flow**:
  1. The guest enters filters (city, dates, price, services).
  2. The system queries the database for available accommodations (active status only).
  3. The system returns a paginated list with cards showing details.
- **Alternative Flows**:
  - A1: No results
    - In step 2, if no matches, display "No se encontraron alojamientos para los criterios seleccionados"  and prompts the guest to adjust the filters.
  - A2: Predictive city search
    - In step 1, when the guest starts typing a city name (e.g., "Bog"), the system provides predictive suggestions (e.g., "Bogotá D.C.") based on a database of valid cities.
    - The guest selects a suggestion or continues typing, then proceeds with the search.
  - A3: Invalid filters
    - In step 1, if the guest enters invalid filters (e.g., past dates or negative price range), the system displays "Por favor, ingrese filtros válidos (fechas futuras y rango de precios válido)" and prompts the guest to correct them.
- **Postconditions**: Results are displayed.
- **Exceptions**: 
  - If query fails, show "Error en la búsqueda".

### UC-005: View Accommodation Details
- **Actor**: Guest
- **Description**: Allows a guest to view the complete details of an accommodation (gallery, calendar, comments, etc.) to evaluate if it meets their expectations.
- **Preconditions**: The guest is on the search results page or has a direct link to accommodation.
- **Basic Flow**:
  1. The guest clicks on an accommodation card from the search results or accesses via URL.
  2. The system fetches the accommodation details (images, description, location, calendar, comments, average rating).
  3. The system displays the accommodation details page with a gallery (1-10 images), description, location on a map (Mapbox), availability calendar, comments sorted by date, and average rating.
- **Alternative Flows**:
  - A1: Accommodation not available (deleted)
    - In step 2, if the accommodation is marked as "deleted", the system displays "El alojamiento no está disponible".
- **Postconditions**: The guest views the accommodation details.
- **Exceptions**: 
  - If the system fails to load the accommodation, show "Error al cargar el alojamiento".

### UC-006: Make Reservation
- **Actor**: Guest
- **Description**: Allows a guest to make a reservation by selecting dates and confirming details, to secure accommodation for their trip.
- **Preconditions**: The guest is authenticated and on accommodation details page.
- **Basic Flow**:
  1. The guest selects available dates on the calendar and specifies the number of guests (within maximum capacity).
  2. The system validates the dates (available, not past) and guest count.
  3. The guest confirms the reservation.
  4. The system creates the reservation with "Confirmed" status.
  5. The system sends a confirmation email to the guest with reservation details.
  6. The reservation appears in the guest's history.
- **Alternative Flows**:
  - A1: Unavailable dates
    - In step 2, if dates are already booked, display "Las fechas seleccionadas no están disponibles".
  - A2: Exceeding maximum capacity
    - In step 2, if guest count exceeds capacity, display "El número de huéspedes excede la capacidad del alojamiento".
  - A3: Past dates
    - In step 2, if dates are before current date, display "No se pueden reservar fechas pasadas".
- **Postconditions**: The reservation is created, and the guest receives a confirmation email.
- **Exceptions**: 
  - If reservation creation fails, show "Error al crear la reserva".

### UC-007: Cancel Reservation
- **Actor**: Guest
- **Description**: Allows a guest to cancel a reservation (subject to policies) to adjust plans if needed.
- **Preconditions**: The guest is authenticated and has an active reservation.
- **Basic Flow**:
  1. The guest navigates to the reservation history page.
  2. The guest selects an active reservation and chooses to cancel.
  3. The system checks if cancellation is allowed (within 48 hours before check-in).
  4. If allowed, the system updates the reservation status to "Cancelled".
  5. The system sends cancellation emails to the guest and host.
- **Alternative Flows**:
  - A1: Cancellation outside policy (less than 48 hours before check-in)
    - In step 3, if not allowed, display "No se puede cancelar la reserva dentro de las 48 horas previas al check-in".
  - A2: Already cancelled reservation
    - In step 2, if the reservation is already cancelled, display "La reserva ya está cancelada".
- **Postconditions**: The reservation is cancelled, and both guest and host are notified.
- **Exceptions**: 
  - If cancellation fails, show "Error al cancelar la reserva".

### UC-008: View Reservation History
- **Actor**: Guest
- **Description**: Allows a guest to view their reservation history (active, past, cancelled) to manage trips and expenses.
- **Preconditions**: The guest is authenticated.
- **Basic Flow**:
  1. The guest navigates to the reservation history page
  2. The system fetches the guest's reservations sorted from newest to oldest
  3. The system displays the list with details (accommodation, dates, status, total cost).
- **Alternative Flows**:
  - A1: Filtered reservations
    - The guest applies filters by status (active, past, cancelled).
    - The system returns only reservations matching the filter.
  - A2: No reservations
    - If no reservations exist, display "No tienes reservas registradas".
- **Postconditions**: The guest views their reservation history.
- **Exceptions**: 
  - If loading fails, show "Error al cargar el historial".

### UC-009: Leave Comment and Rating
- **Actor**: Guest
- **Description**: Allows a guest to leave a comment and rating after a completed stay to share their experience.
- **Preconditions**: The guest is authenticated and has a completed reservation (past check-out).
- **Basic Flow**:
  1. The guest navigates to the reservation history page
  2. The guest selects a completed reservation and chooses to leave a comment
  3. The guest enters a rating (1-5 stars) and a comment (max 500 characters)
  4. The system validates that the reservation is completed and no comment exists (includes UC-023: Validate Completed Stay for Comments)
  5. The system saves the comment and updates the accommodation's average rating
  6. The host receives a notification.
- **Alternative Flows**:
  - A1: Reservation not completed
    - In step 4, if the reservation is not completed, display "Solo puedes comentar después de completar tu estadía".
  - A2: Comment already submitted
    - In step 4, if a comment already exists, display "Ya has dejado un comentario para esta reserva".
- **Postconditions**: The comment is saved, displayed on the accommodation page, and the host is notified.
- **Exceptions**:
  - If saving fails, show "Error al guardar el comentario".

### UC-010: Receive Reminders
- **Actor**: Guest
- **Description**: The guest receives automatic reminders about upcoming reservations to avoid forgetting important stay dates.
- **Preconditions**: The guest has an active reservation with check-in within the next 7 days.
- **Basic Flow**:
  1. The system runs a scheduled task to check for upcoming reservations (includes UC-024: Send Automatic Reminders (Guest)).
  2. For each qualifying reservation, the system generates a reminder including accommodation name, check-in/check-out dates, address, and contact details.
  3. The system sends the reminder via email (with subject "Recordatorio: Tu reserva en [Accommodation Name]") or in-app notification (displayed in the notification panel).
- **Alternative Flows**:
  - A1: No upcoming reservations
    - No notifications are sent.
- **Postconditions**: The guest receives the reminder.
- **Exceptions**: 
  - If sending fails, the system logs the error for retry.

## 2. Host Role

### UC-011: Register Host
- **Actor**: Host
- **Description**: Allows a host to create an account to manage accommodations.
- **Preconditions**: The host is not registered in the system.
- **Basic Flow**:
  1. The host navigates to the registration page.
  2. The host enters their name, email, password (minimum 8 characters, uppercase, numbers), phone, selects the "Host" role, and provides additional details (personal description, legal documents if applicable).
  3. The system validates the email uniqueness (includes UC-021: Validate Unique Email).
  4. The system encrypts the password (includes UC-022: Encrypt Password).
  5. The system creates the account and assigns the "Host" role.
  6. The system redirects the host to the host dashboard.
- **Alternative Flows**:
  - A1: Email already registered
    - In step 3, if the email exists, the system displays "El correo electrónico ya está en uso" and prompts the host to try another email.
  - A2: Password does not meet requirements
    - In step 2, if the password is invalid, the system displays an error message indicating the password requirements.
- **Postconditions**: The host account is created, and the host is redirected to the dashboard.
- **Exceptions**:
  - If the system fails to save the account, an error message is shown: "Error al crear la cuenta, intenta de nuevo".

### UC-012: Log In
- **Actor**: Host
- **Description**: Allows a host to log in to access their dashboard.
- **Preconditions**: The host has a registered account.
- **Basic Flow**:
  1. The host navigates to the login page.
  2. The host enters their email and password.
  3. The system validates the credentials (includes UC-022: Encrypt Password for comparison).
  4. The system generates a JWT token and redirects to the host dashboard.
- **Alternative Flows**:
  - A1: Incorrect credentials
    - The system displays "Correo electrónico o contraseña incorrectos".
  - A2: Forgot password
    - In step 2, the host selects "Forgot my password", enters their email, and the system sends a recovery code valid for 15 minutes.
- **Postconditions**: The host is authenticated and redirected to the host dashboard with an active session.
- **Exceptions**:
  - If the system fails to validate credentials, show "Error al iniciar sesión, intenta de nuevo".
  - If the system fails to validate credentials due to a server timeout or network error, display "Error de conexión, intenta de nuevo más tarde" and log the error for debugging.

### UC-013: View Accommodations
- **Actor**: Host
- **Description**: Allows a host to view the list of their registered accommodations to have centralized control of their properties.
- **Preconditions**: The host is authenticated and has the "Host" role.
- **Basic Flow**:
  1. The host navigates to the accommodation management page.
  2. The system fetches the host's accommodations (active and non-deleted only).
  3. The system displays a list of accommodations with details (title, city, price per night, status).
- **Alternative Flows**:
  - A1: No accommodations registered
    - If no accommodations exist, display "No tienes alojamientos registrados".
- **Postconditions**: The host views their list of accommodations.
- **Exceptions**: 
  - If loading fails, show "Error al cargar la lista de alojamientos".

### UC-014: Create Accommodation
- **Actor**: Host
- **Description**: Allows a host to create new accommodation with details like name, location, price, photos, description, and availability to offer it to potential guests.
- **Preconditions**: The host is authenticated and on the accommodation creation page.
- **Basic Flow**:
  1. The host enters accommodation details (title, description, city, address, latitude/longitude, price per night, capacity, services).
  2. The host uploads 1 to 10 images (selecting one as primary).
  3. The host submits the form.
  4. The system validates all mandatory fields (title, price, location, etc.).
  5. The system creates the accommodation with "active" status.
  6. The accommodation appears in the host's management list.
- **Alternative Flows**:
  - A1: Missing mandatory fields
    - In step 4, if mandatory fields are missing, display "Complete los campos obligatorios" and indicate which fields are required.
  - A2: Exceeding image limit
    - In step 2, if more than 10 images are uploaded, display "No se pueden subir más de 10 imágenes".
  - A3: Invalid field lengths
    - In step 4, if fields like title (max 100 characters) or description (max 1000 characters) exceed limits, display "El título o descripción excede el límite de caracteres permitido".
- **Postconditions**: The accommodation is created and active in the system.
- **Exceptions**: 
  - If creation fails, show "Error al crear el alojamiento".

### UC-015: Edit Accommodation
- **Actor**: Host
- **Description**: Allows a host to edit an existing accommodation's information to keep data up to date.
- **Preconditions**: The host is authenticated and owns the accommodation.
- **Basic Flow**:
  1. The host navigates to the accommodation management page.
  2. The host selects an accommodation and chooses to edit.
  3. The system loads the accommodation's current details.
  4. The host updates the desired fields (title, price, services, etc.).
  5. The host saves the changes.
  6. The system validates and updates the accommodation in the database.
- **Alternative Flows**:
  - A1: Accommodation not found or no permission
    - In step 3, if the accommodation doesn't exist, display "El alojamiento no existe".
  - A2: No permission
    - In step 3, if the accommodation exists but does not belong to the host, display "No tienes permisos para editar este alojamiento".
- **Postconditions**: The accommodation details are updated and reflected on the accommodation page.
- **Exceptions**: 
  - If update fails, show "Error al actualizar el alojamiento".

### UC-016: Delete Accommodation
- **Actor**: Host
- **Description**: Allows a host to delete an accommodation if it has no future reservations, using soft delete.
- **Preconditions**: The host is authenticated and owns the accommodation.
- **Basic Flow**:
  1. The host navigates to the accommodation management page.
  2. The host selects an accommodation and chooses to delete it.
  3. The system checks for future reservations.
  4. If no future reservations, the system updates the accommodation status to "deleted" in the database (soft delete, not physical removal).
  5. The system confirms the action and removes the accommodation from public views and searches.
- **Alternative Flows**:
  - A1: Accommodation has future reservations
    - In step 3, if future reservations exist, the system displays "No se puede eliminar un alojamiento con reservas futuras" and aborts the deletion.
- **Postconditions**: The accommodation is marked as "deleted" and no longer visible in searches, but historical data is preserved in the database.
- **Exceptions**:
  - If the update fails, show "Error al eliminar el alojamiento, intenta de nuevo".

### UC-017: View Reservations
- **Actor**: Host
- **Description**: Allows a host to view reservations associated with their accommodations with filters by date and status to manage bookings efficiently.
- **Preconditions**: The host is authenticated and has at least one accommodation.
- **Basic Flow**:
  1. The host navigates to the reservation management page.
  2. The system fetches reservations for the host's accommodations.
  3. The system displays a sorted list of reservations with details (accommodation, dates, number of guests, status).
- **Alternative Flows**:
  - A1: Filtered reservations
    - The host applies filters by date or status (pending, confirmed, cancelled, completed).
    - The system returns only reservations matching the filters.
  - A2: No reservations
    - If no reservations exist, display "No hay reservas asociadas a tus alojamientos".

- **Postconditions**: The host views their reservations.
- **Exceptions**:
  - If loading fails, show "Error al cargar las reservas, intenta de nuevo" and log the error for debugging.

### UC-018: View Metrics
- **Actor**: Host
- **Description**: Allows a host to view basic metrics for each accommodation (number of reservations, average rating) with date filters to analyze performance.
- **Preconditions**: The host is authenticated and has at least one accommodation.
- **Basic Flow**:
  1. The host navigates to the metrics page.
  2. The host selects an accommodation and applies a date range filter.
  3. The system calculates metrics (total reservations, average rating) for the selected period.
  4. The system displays the metrics.
- **Alternative Flows**:
  - A1: No data available
    - If no reservations or comments exist for the selected period, display "No hay métricas disponibles para este alojamiento en el rango seleccionado".
- **Postconditions**: The host views the accommodation's metrics.
- **Exceptions**:
  - If calculation fails, show "Error al calcular las métricas, intenta de nuevo" and log the error for debugging.

### UC-019: Respond To Comments
- **Actor**: Host
- **Description**: Allows a host to respond to guest comments to maintain a good reputation and respond to feedback.
- **Preconditions**: The host is authenticated and owns the accommodation.
- **Basic Flow**:
  1. The host accesses the accommodation details page.
  2. The host selects a guest comment and chooses to respond.
  3. The host enters a response text (maximum 500 characters).
  4. The system saves the answer and displays it next to the comment.
- **Alternative Flows**:
  - A1: Comment not found
    - If the comment does not exist, it displays "Comment does not exist."
  - A2: Response text too long
    - In step 3, if the answer exceeds 500 characters, it displays "Answer exceeds the 500-character limit."
- **Postconditions**: The response is saved and displayed on the accommodation page.
- **Exceptions**:
  - If the response cannot be saved, it displays "Error saving response, please try again" and logs the error for debugging.

### UC-020: Receive Notifications
- **Actor**: Host
- **Description**: The host receives notifications when someone books, cancels, or comments on their accommodations to stay informed in real-time.
- **Preconditions**: The host has at least one active accommodation.
- **Basic Flow**:
  1. A triggering event occurs (new reservation, cancellation, or new comment) (includes UC-025: Send Notifications (Host)).
  2. The system generates a notification with relevant details (e.g., reservation ID, dates, guest name for a new reservation).
  3. The system sends the notification via email (with subject specific to the event, e.g., "Nueva reserva para [Accommodation Name]") or in-app notification (displayed in the notification panel).
- **Alternative Flows**:
  - A1: New reservation
    - In step 2, the notification includes accommodation name, reservation ID, check-in/check-out dates, number of guests, and guest contact details.
    - In step 3, the system sends the notification with the message: "Nueva reserva confirmada para tu alojamiento" via email (subject: "Nueva reserva para [Accommodation Name]") or in-app.
  - A2: Cancellation
    - In step 2, the notification includes reservation ID, accommodation name, and cancellation date.
    - In step 3, the system sends the notification with the message: "Una reserva para tu alojamiento ha sido cancelada" via email or in-app.
  - A3: New comment
    - In step 2, the notification includes accommodation name, comment text, rating (1-5 stars), and guest name.
    - In step 3, the system sends the notification with the message: "Nuevo comentario recibido para tu alojamiento" via email or in-app.
- **Postconditions**: The host receives the notification.
- **Exceptions**:
  - If the notification fails to send (e.g., email server error), the system logs the error and schedules a retry (up to 3 attempts before marking as failed).

## 3. System Role

### UC-024: Send Automatic Reminders (Guest)
- **Actor**: System
- **Description**: Sends reminders to guests about upcoming reservations.
- **Preconditions**: A reservation has a check-in date within the next 7 days.
- **Basic Flow**:
  1. The system runs a scheduled task to check for upcoming reservations.
  2. For each qualifying reservation, the system generates a reminder with details (accommodation, dates, address).
  3. The system sends the reminder via email or in-app notification to the guest.
- **Alternative Flows**:
  - A1: No upcoming reservations
    - In step 1, if no reservations qualify, no notifications are sent.
- **Postconditions**: The guest receives the reminder.
- **Exceptions**:
  - If sending fails, the system logs the error for retry.

### UC-025: Send Notifications (Host)
- **Actor**: System
- **Description**: Sends notifications to hosts for specific events: new reservations, cancellations, or new comments on their accommodations, as defined in HOST-010.
- **Preconditions**: A triggering event occurs (new reservation, cancellation, or new comment).
- **Basic Flow**:
  1. The system detects a triggering event (e.g., new reservation).
  2. The system generates a notification with relevant details (e.g., reservation ID, dates, guest name for a new reservation).
  3. The system sends the notification via email or in-app notification.
- **Alternative Flows**:
  - A1: New reservation
    - In step 2, the notification includes accommodation name, reservation ID, check-in/check-out dates, number of guests, and guest contact details.
    - In step 3, the system sends the notification with the message: "Nueva reserva confirmada para tu alojamiento" via email (subject: "Nueva reserva para [Accommodation Name]") or in-app notification.
  - A2: Cancellation
    - In step 2, the notification includes reservation ID, accommodation name, and cancellation date.
    - In step 3, the system sends the notification with the message: "Una reserva para tu alojamiento ha sido cancelada" via email or in-app.
  - A3: New comment:
    - In step 2, the notification includes accommodation name, comment text, rating (1-5 stars), and guest name.
    - In step 3, the system sends the notification with the message: "Nuevo comentario recibido para tu alojamiento" via email or in-app.
- **Postconditions**: The host receives the notification with details of the event.
- **Exceptions**:
  - If the notification fails to send (e.g., email server error), the system logs the error and schedules a retry.

### UC-021: Validate Unique Email
- **Actor**: System
- **Description**: Validates that an email is unique during registration to prevent duplicate accounts.
- **Preconditions**: A user or host attempts to register with an email.
- **Basic Flow**:
  1. The system receives an email from the registration process.
  2. The system checks the database for existing emails.
  3. If the email is unique, the system allows the registration to proceed.
- **Alternative Flows**:
  -   A1: Email already exists
    - In step 2, if the email exists, the system returns an error: "El correo electrónico ya está en uso".
- **Postconditions**: The email is validated as unique, or an error is returned.
- **Exceptions**:
  - If the database is unavailable, the system logs the error and notifies the user to try again.

### UC-022: Encrypt Password
- **Actor**: System
- **Description**: Encrypts passwords to ensure the security of user information.
- **Preconditions**: A user registers or updates their password.
- **Basic Flow**:
  1. The system receives a plain text password.
  2. The system encrypts the password using BCrypt.
  3. The system stores the encrypted password in the database.
- **Alternative Flows**:
  - A1: Password verification (during login)
    - Given a login attempt, the system compares the entered password with the stored encrypted version.
    - If they match, access is granted; otherwise, access is denied.
- **Postconditions**: The password is stored securely encrypted.
- **Exceptions**:
  - If encryption fails, the system logs the error and prevents account creation/update.

### UC-023: Validate Completed Stay for Comments
- **Actor**: System
- **Description**: Validates that only users who completed a stay can leave a comment to ensure the authenticity of reviews.
- **Preconditions**: A user attempts to leave a comment on accommodation.
- **Basic Flow**:
  1. The system receives a comment submission associated with a reservation.
  2. The system checks the reservation's status and check-out date.
  3. If the reservation is in "Completed" status (check-out date is in the past), the comment is allowed.
  4. The system saves the comment and updates the accommodation's average rating.
- **Alternative Flows**:
  - A1: Reservation not completed
    - In step 2, if the reservation is not completed (check-out date is in the future or status is not "Completed"), the system returns an error: "Solo puedes comentar después de completar tu estadía".
- **Postconditions**: The comment is saved only if the stay is completed.
- **Exceptions**:
  - If validation fails due to database error, the system logs the error and notifies the user to try again.