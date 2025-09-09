# User Stories Backlog — StayHub

## User (Guest)

### Account Management

**ID:** US-001  
**Title:** User Registration  
**As** a user  
**I want** to register with my name, email, and password  
**So that** I can create an account and access the application  

**Acceptance Criteria:**
1. Given: I am on the registration page  
   When: I enter a name, valid email, secure password (min 8 chars, uppercase, numbers), phone, role, and birth date  
   Then: my account is created successfully, I receive confirmation, and I'm redirected to main page

2. Given: I am on the registration page  
   When: I attempt to register with an existing email  
   Then: I receive error "El correo electrónico ya está en uso"

3. Given: I am on the registration page  
   When: I enter password not meeting requirements  
   Then: I receive error message indicating password requirements

**Notes:**
- UI: Registration form with all required fields and validation
- Password validation: min 8 characters, uppercase, numbers
- Email uniqueness validation required

**Dependencies:** SYS-001 (Email validation), SYS-002 (Password encryption)  
**Priority:** P0  
**Estimation:** 5 SP  
**Status:** Backlog

---

**ID:** US-002  
**Title:** User Login  
**As** a user  
**I want** to log in with my credentials  
**So that** I can access my account and reservations  

**Acceptance Criteria:**
1. Given: I am on the login page  
   When: I enter valid email and password  
   Then: I'm redirected to main page and JWT token is generated

2. Given: I am on the login page  
   When: I enter incorrect credentials  
   Then: I receive error "Correo electrónico o contraseña incorrectos"

3. Given: I am on the login page  
   When: I select "Forgot my password" and enter email  
   Then: I receive email with recovery code valid for 15 minutes

**Notes:**
- UI: Login form with email/password fields and forgot password link
- JWT token generation for session management
- Password recovery functionality

**Dependencies:** SYS-002 (Password encryption)  
**Priority:** P0  
**Estimation:** 3 SP  
**Status:** Backlog

---

**ID:** US-010  
**Title:** Change Password  
**As** a user  
**I want** to change my password by entering current and new password  
**So that** I can keep my account secure  

**Acceptance Criteria:**
1. Given: I am authenticated on profile settings page  
   When: I enter correct current password and valid new password  
   Then: password is updated and I receive confirmation message

2. Given: I am authenticated on profile settings page  
   When: I enter incorrect current password  
   Then: I receive error "La contraseña actual es incorrecta"

3. Given: I am authenticated on profile settings page  
   When: new password doesn't meet requirements  
   Then: I receive error indicating password requirements

**Notes:**
- UI: Change password form in profile settings
- Same password requirements as registration
- Current password verification required

**Dependencies:** SYS-002 (Password encryption)  
**Priority:** P1  
**Estimation:** 3 SP  
**Status:** Backlog

---

### Accommodation Search and Selection

**ID:** US-003  
**Title:** Search Accommodations with Filters  
**As** a user  
**I want** to search for available accommodations by filtering by city, dates, and price  
**So that** I can find options that meet my needs  

**Acceptance Criteria:**
1. Given: I am on the search page  
   When: I enter city, date range, price range, and service filters  
   Then: I see paginated list (10 per page) with cards showing image, price, location, rating

2. Given: I am on the search page  
   When: I enter filters matching no accommodations  
   Then: I receive message "No se encontraron alojamientos para los criterios seleccionados"

3. Given: I am on the search page  
   When: I start typing city name (e.g., "Bog")  
   Then: I see predictive suggestions like "Bogotá D.C."

**Notes:**
- UI: Search page with filter controls and results grid
- Pagination: 10 results per page
- Predictive city search functionality
- Only show active (non-deleted) accommodations

**Dependencies:** None  
**Priority:** P0  
**Estimation:** 8 SP  
**Status:** Backlog

---

**ID:** US-004  
**Title:** View Accommodation Details  
**As** a user  
**I want** to view the details of an accommodation (gallery, calendar, comments)  
**So that** I can evaluate if it meets my expectations  

**Acceptance Criteria:**
1. Given: I am on search results page  
   When: I click on accommodation card  
   Then: I see page with gallery (1-10 images), description, map location, calendar, comments, average rating

2. Given: I am on search results page  
   When: I attempt to view deleted accommodation  
   Then: I receive error "El alojamiento no está disponible"

**Notes:**
- UI: Detailed accommodation page with image gallery, map (Mapbox), calendar
- Comments sorted by date (newest first)
- Show average rating and individual comments

**Dependencies:** External Mapbox integration  
**Priority:** P0  
**Estimation:** 8 SP  
**Status:** Backlog

---

### Reservation Management

**ID:** US-005  
**Title:** Make Reservation  
**As** a user  
**I want** to make a reservation by selecting dates and confirming details  
**So that** I can secure accommodation for my trip  

**Acceptance Criteria:**
1. Given: I am on accommodation details page  
   When: I select available dates, specify guests (within capacity), and confirm  
   Then: I receive email confirmation and reservation appears in history with "Confirmed" status

2. Given: I am on accommodation details page  
   When: I select already booked dates  
   Then: I receive error "Las fechas seleccionadas no están disponibles"

3. Given: I am on accommodation details page  
   When: I specify more guests than capacity  
   Then: I receive error "El número de huéspedes excede la capacidad del alojamiento"

4. Given: I am on accommodation details page  
   When: I attempt to book past dates  
   Then: I receive error "No se pueden reservar fechas pasadas"

**Notes:**
- UI: Booking form with date picker and guest counter
- Email confirmation with reservation details
- Minimum 1 night stay required
- Validate date availability and guest capacity

**Dependencies:** Email service integration  
**Priority:** P0  
**Estimation:** 10 SP  
**Status:** Backlog

---

**ID:** US-006  
**Title:** Cancel Reservation  
**As** a user  
**I want** to cancel my reservations (subject to policies)  
**So that** I can adjust my plans if needed  

**Acceptance Criteria:**
1. Given: I am on reservation history page  
   When: I cancel active reservation more than 48h before check-in  
   Then: reservation changes to "Cancelled", I receive email, host is notified

2. Given: I am on reservation history page  
   When: I attempt to cancel less than 48h before check-in  
   Then: I receive error "No se puede cancelar la reserva dentro de las 48 horas previas al check-in"

3. Given: I am on reservation history page  
   When: I attempt to cancel already cancelled reservation  
   Then: I receive error "La reserva ya está cancelada"

**Notes:**
- UI: Cancel button on reservation history with confirmation dialog
- 48-hour cancellation policy enforcement
- Email notifications to both user and host

**Dependencies:** Email service integration  
**Priority:** P0  
**Estimation:** 5 SP  
**Status:** Backlog

---

**ID:** US-007  
**Title:** View Reservation History  
**As** a user  
**I want** to view my reservation history (active, past, cancelled)  
**So that** I can manage my trips and expenses  

**Acceptance Criteria:**
1. Given: I am authenticated on reservation history page  
   When: I access reservations section  
   Then: I see list sorted newest to oldest with accommodation, dates, status, cost

2. Given: I am on reservation history page  
   When: I apply status filters (active, past, cancelled)  
   Then: I see only reservations matching selected filter

3. Given: I am on reservation history page  
   When: I have no registered reservations  
   Then: I see message "No tienes reservas registradas"

**Notes:**
- UI: Reservation history page with filters and sorting
- Show all reservation details and current status
- Filter by reservation status

**Dependencies:** None  
**Priority:** P0  
**Estimation:** 5 SP  
**Status:** Backlog

---

### Notifications and Reminders

**ID:** US-008  
**Title:** Receive Reservation Reminders  
**As** a user  
**I want** to receive reminders about upcoming reservations  
**So that** I can avoid forgetting important stay dates  

**Acceptance Criteria:**
1. Given: I have active reservation with check-in within 7 days  
   When: system runs automatic notification process  
   Then: I receive email or in-app notification with reservation details

2. Given: I have no active reservations within 7 days  
   When: system runs automatic notification process  
   Then: I receive no notifications

**Notes:**
- Automated system process running daily
- Reminders include accommodation, dates, address
- Both email and in-app notification options

**Dependencies:** Email service, notification system  
**Priority:** P1  
**Estimation:** 5 SP  
**Status:** Backlog

---

### Experience and Feedback

**ID:** US-009  
**Title:** Leave Comments and Ratings  
**As** a user  
**I want** to leave comments and ratings after a completed stay  
**So that** I can share my experience and help other users  

**Acceptance Criteria:**
1. Given: I have completed reservation (past check-out)  
   When: I enter rating (1-5 stars) and comment (max 500 chars)  
   Then: comment is saved, displayed on accommodation page, host receives notification

2. Given: I am on reservation history page  
   When: I attempt to comment on uncompleted reservation  
   Then: I receive error "Solo puedes comentar después de completar tu estadía"

3. Given: I am on reservation history page  
   When: I attempt to comment on reservation I already commented  
   Then: I receive error "Ya has dejado un comentario para esta reserva"

**Notes:**
- UI: Comment form with star rating and text area (500 char limit)
- Only allow comments after check-out date
- One comment per reservation limit
- Host notification when comment is submitted

**Dependencies:** SYS-003 (Completed stay validation), notification system  
**Priority:** P0  
**Estimation:** 8 SP  
**Status:** Backlog

---

## Host (Property Owner)

### Account Management

**ID:** HOST-001  
**Title:** Host Registration and Login  
**As** a host  
**I want** to register and log in with a host role  
**So that** I can manage my accommodations  

**Acceptance Criteria:**
1. Given: I am on registration page  
   When: I enter details and select "host" role with additional info  
   Then: account is created, assigned "host" role, redirected to host dashboard

2. Given: I am on registration page  
   When: I attempt to register with existing email  
   Then: I receive error "El correo electrónico ya está en uso"

3. Given: I am on login page  
   When: I enter valid host credentials  
   Then: I'm redirected to host dashboard with accommodation management access

**Notes:**
- UI: Registration form with host-specific fields (description, legal docs)
- Separate host dashboard interface
- Same email uniqueness and password requirements as users

**Dependencies:** SYS-001 (Email validation), SYS-002 (Password encryption)  
**Priority:** P0  
**Estimation:** 5 SP  
**Status:** Backlog

---

### Accommodation Management

**ID:** HOST-002  
**Title:** View Accommodation List  
**As** a host  
**I want** to view the list of my registered accommodations  
**So that** I can have centralized control of my properties  

**Acceptance Criteria:**
1. Given: I am authenticated host on accommodation management page  
   When: I access accommodations section  
   Then: I see list of active accommodations with title, city, price, status

2. Given: I am authenticated host on accommodation management page  
   When: I have no registered accommodations  
   Then: I see message "No tienes alojamientos registrados"

**Notes:**
- UI: Accommodation management dashboard
- Show only active (non-deleted) accommodations
- Display key accommodation details

**Dependencies:** None  
**Priority:** P0  
**Estimation:** 3 SP  
**Status:** Backlog

---

**ID:** HOST-003  
**Title:** Create New Accommodation  
**As** a host  
**I want** to create a new accommodation with details  
**So that** I can offer it to potential guests  

**Acceptance Criteria:**
1. Given: I am on accommodation creation page  
   When: I enter title, description, location, price, capacity, services, upload 1-10 images  
   Then: accommodation is created with "active" status and appears in my list

2. Given: I am on accommodation creation page  
   When: I attempt to save without mandatory fields  
   Then: I receive error indicating missing fields

3. Given: I am on accommodation creation page  
   When: I attempt to upload more than 10 images  
   Then: I receive error "No se pueden subir más de 10 imágenes"

**Notes:**
- UI: Comprehensive accommodation creation form
- Mandatory fields: title, description, city, address, lat/lng, price, capacity
- Image upload: 1-10 images, one marked as primary
- External image storage service integration

**Dependencies:** External image storage service  
**Priority:** P0  
**Estimation:** 10 SP  
**Status:** Backlog

---

**ID:** HOST-004  
**Title:** Edit Accommodation Information  
**As** a host  
**I want** to edit an existing accommodation's information  
**So that** I can keep my data up to date  

**Acceptance Criteria:**
1. Given: I am on accommodation management page  
   When: I select accommodation and update details  
   Then: changes are saved and reflected on accommodation details page

2. Given: I am authenticated as host  
   When: I attempt to edit accommodation that doesn't exist or isn't mine  
   Then: I receive error "El alojamiento no existe o no tienes permisos"

**Notes:**
- UI: Edit form pre-populated with current accommodation data
- Ownership validation required
- Same validation rules as creation

**Dependencies:** None  
**Priority:** P0  
**Estimation:** 5 SP  
**Status:** Backlog

---

**ID:** HOST-005  
**Title:** Delete Accommodation (Soft Delete)  
**As** a host  
**I want** to delete an accommodation only if it has no future reservations  
**So that** I can maintain history but hide it from users  

**Acceptance Criteria:**
1. Given: I am on accommodation management page  
   When: I delete accommodation with no future reservations  
   Then: accommodation changes to "deleted" status and no longer appears in searches

2. Given: I am authenticated as host  
   When: I attempt to delete accommodation with future reservations  
   Then: I receive error "No se puede eliminar un alojamiento con reservas futuras"

**Notes:**
- UI: Delete confirmation dialog with future reservation check
- Soft delete implementation (status change, not physical deletion)
- Check for future reservations before allowing deletion

**Dependencies:** None  
**Priority:** P1  
**Estimation:** 3 SP  
**Status:** Backlog

---

### Reservation Management

**ID:** HOST-006  
**Title:** View Accommodation Reservations  
**As** a host  
**I want** to view reservations associated with my accommodations with filters  
**So that** I can manage my bookings efficiently  

**Acceptance Criteria:**
1. Given: I am on reservation management page  
   When: I access reservations and apply filters by date/status  
   Then: I see sorted list with accommodation, dates, guests, status

2. Given: I am authenticated as host  
   When: there are no reservations for my accommodations  
   Then: I see message "No hay reservas asociadas a tus alojamientos"

**Notes:**
- UI: Reservation management page with filters
- Filters: date range, status (pending, confirmed, cancelled, completed)
- Show reservations for all host's accommodations

**Dependencies:** None  
**Priority:** P0  
**Estimation:** 5 SP  
**Status:** Backlog

---

### Analytics and Metrics

**ID:** HOST-008  
**Title:** View Accommodation Metrics  
**As** a host  
**I want** to view basic metrics for each accommodation  
**So that** I can analyze my properties' performance  

**Acceptance Criteria:**
1. Given: I am on metrics page  
   When: I select accommodation and apply date range filter  
   Then: I see total reservations and average rating for that period

2. Given: I am authenticated as host  
   When: I select accommodation with no reservations/comments  
   Then: I see message "No hay métricas disponibles para este alojamiento en el rango seleccionado"

**Notes:**
- UI: Metrics dashboard with accommodation selector and date filters
- Metrics: number of reservations, average rating
- Date range filtering capability

**Dependencies:** None  
**Priority:** P1  
**Estimation:** 5 SP  
**Status:** Backlog

---

### Communication with Guests

**ID:** HOST-009  
**Title:** Respond to Guest Comments  
**As** a host  
**I want** to respond to guest comments  
**So that** I can maintain a good reputation and address feedback  

**Acceptance Criteria:**
1. Given: I am on accommodation details page  
   When: I enter response to guest's comment  
   Then: response is saved and displayed alongside comment

2. Given: I am authenticated as host  
   When: I attempt to respond to non-existent comment  
   Then: I receive error "El comentario no existe"

**Notes:**
- UI: Reply functionality on accommodation page comments
- Response character limit: 500 characters
- Display responses alongside original comments

**Dependencies:** None  
**Priority:** P1  
**Estimation:** 3 SP  
**Status:** Backlog

---

**ID:** HOST-010  
**Title:** Receive Booking Notifications  
**As** a host  
**I want** to receive notifications when someone books, cancels, or comments  
**So that** I can stay informed in real-time  

**Acceptance Criteria:**
1. Given: user makes reservation for my accommodation  
   When: reservation is registered  
   Then: I receive email/in-app notification with reservation details

2. Given: user cancels reservation for my accommodation  
   When: cancellation is processed  
   Then: I receive email/in-app notification indicating cancellation

3. Given: user leaves comment on my accommodation  
   When: comment is registered  
   Then: I receive email/in-app notification with comment content

**Notes:**
- Multiple notification types: new reservation, cancellation, new comment
- Both email and in-app notification options
- Include relevant details in each notification type

**Dependencies:** Email service, notification system  
**Priority:** P1  
**Estimation:** 8 SP  
**Status:** Backlog

---

## System

### Security and Validations

**ID:** SYS-001  
**Title:** Email Uniqueness Validation  
**As** a system  
**I want** to validate that the email is unique during registration  
**So that** I can prevent duplicate accounts  

**Acceptance Criteria:**
1. Given: user attempts to register  
   When: system checks provided email  
   Then: if email exists, show error "El correo electrónico ya está en uso"

2. Given: user attempts to register  
   When: system checks email not previously registered  
   Then: registration proceeds without errors

**Notes:**
- Database email uniqueness constraint
- Real-time validation during registration
- Apply to both user and host registrations

**Dependencies:** None  
**Priority:** P0  
**Estimation:** 2 SP  
**Status:** Backlog

---

**ID:** SYS-002  
**Title:** Password Encryption  
**As** a system  
**I want** to encrypt passwords  
**So that** I can ensure the security of user information  

**Acceptance Criteria:**
1. Given: user registers or updates password  
   When: system processes password  
   Then: password is stored encrypted (BCrypt) in database

2. Given: user attempts to log in  
   When: system compares entered password with stored one  
   Then: access granted only if password matches encrypted version

**Notes:**
- Use BCrypt for password encryption
- Apply to all password operations (registration, login, password change)
- Secure password comparison for authentication

**Dependencies:** None  
**Priority:** P0  
**Estimation:** 3 SP  
**Status:** Backlog

---

**ID:** SYS-003  
**Title:** Completed Stay Validation for Comments  
**As** a system  
**I want** to validate that only users who completed a stay can leave a comment  
**So that** I can ensure the authenticity of reviews  

**Acceptance Criteria:**
1. Given: user attempts to leave comment  
   When: system checks associated reservation's status  
   Then: if reservation is "Completed" (past check-out), comment is allowed

2. Given: user attempts to leave comment  
   When: system checks reservation not in "Completed" status  
   Then: error shown "Solo puedes comentar después de completar tu estadía"

**Notes:**
- Validate reservation status and check-out date
- Only allow comments after stay completion
- Ensure comment authenticity

**Dependencies:** None  
**Priority:** P0  
**Estimation:** 3 SP  
**Status:** Backlog