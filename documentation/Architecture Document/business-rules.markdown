# StayHub - Business Rules

**Note**: The identifier "RN-XXX" is used for Business Rules (Reglas de Negocio) to maintain consistency with the Spanish documentation, where "RN" stands for "Regla de Negocio" (Business Rule).

## Guest

### Registration and Authentication
- **RN-001**: A guest must provide a unique email during registration, and if the email already exists, the system displays "The email address is already in use."
- **RN-002**: The guest's password must be at least 8 characters long and include at least one uppercase letter and numbers. If it does not meet these requirements, the system displays an error indicating the password requirements.
- **RN-003**: The mandatory fields for guest registration are: name, email, password, phone, role, and date of birth. All must be completed to create the account.
- **RN-004**: When a guest enters incorrect credentials during login, the system displays "Email or password incorrect."
- **RN-005**: A guest can request password recovery, receiving a recovery code valid for 15 minutes sent to their email.
- **RN-006**: To change the password, the guest must correctly enter their current password. If it is incorrect, the system displays "The current password is incorrect."
- **RN-082**: Upon successful registration, the system redirects the guest to the main page.

### Accommodation Search
- **RN-007**: The accommodation search must display only accommodations with "active" status (not deleted).
- **RN-008**: Search results are displayed paginated with a defined number of results per page.
- **RN-009**: When no results are found for the search criteria, the system displays "No accommodations found for the selected criteria."
- **RN-010**: City search must include predictive functionality (e.g., typing "Bog" suggests "Bogotá D.C.").
- **RN-011**: Invalid search filters (past dates or negative price range) trigger the message "Please enter valid filters (future dates and valid price range)."
- **RN-012**: When attempting to access accommodation marked as "deleted," the system displays "The accommodation is not available."

### Reservations
- **RN-013**: A reservation must include at least one valid check-in and check-out date.
- **RN-014**: Past dates cannot be reserved. If attempted, the system displays "Past dates cannot be reserved."
- **RN-015**: The number of guests cannot exceed the accommodation's maximum capacity. If exceeded, the system displays "The number of guests exceeds the accommodation's capacity."
- **RN-016**: Reservations cannot be made for dates already booked by other reservations. If attempted, the system displays "The selected dates are not available."
- **RN-017**: Upon successfully creating a reservation, the system must send a confirmation email to the guest with the reservation details.
- **RN-018**: Reservations are created with the "Confirmed" status by default.
- **RN-019**: A guest can only cancel reservations up to 48 hours before check-in. If attempted after, the system displays "The reservation cannot be canceled within 48 hours prior to check-in."
- **RN-020**: A reservation that is already in "Canceled" status cannot be canceled again. If attempted, the system displays "The reservation is already canceled."
- **RN-021**: Upon canceling a reservation, both the guest and the host must receive a notification via email.
- **RN-022**: The guest's reservation history is displayed sorted from newest to oldest.
- **RN-023**: If a guest has no registered reservations, the system displays "You have no registered reservations."

### Comments and Ratings
- **RN-024**: A guest can only leave comments after completing a stay (check-out date in the past). If attempted before, the system displays "You can only comment after completing your stay."
- **RN-025**: A guest can leave a maximum of 1 comment per reservation. If a second comment is attempted, the system displays "You have already left a comment for this reservation."
- **RN-026**: The rating is mandatory and must be between 1 and 5 stars.
- **RN-027**: The comment has a maximum limit of 500 characters.
- **RN-028**: Comments on an accommodation are displayed sorted by date, with the most recent first.
- **RN-029**: When a guest leaves a comment, the host must receive a notification.

### Reminders
- **RN-030**: Guests must receive automatic reminders when they have active reservations with check-in within the next 7 days.
- **RN-031**: Reminders include the accommodation name, check-in/check-out dates, address, and contact details.

## Host

### Registration and Account Management
- **RN-032**: A host must provide a unique email during registration, applying the same validation as guests.
- **RN-033**: The host's password must meet the same security requirements as guests (minimum 8 characters, uppercase, numbers).
- **RN-034**: Hosts can provide additional data such as a personal description and legal documents.
- **RN-035**: Upon successful registration, the host is redirected to the host dashboard.

### Accommodation Management
- **RN-036**: A host can have multiple accommodations associated with their account.
- **RN-037**: The mandatory fields for creating an accommodation are: title, description, city, address, exact location (latitude/longitude), price per night, and maximum capacity.
- **RN-038**: An accommodation must have a minimum of 1 image and a maximum of 10 images, with one marked as the primary image.
- **RN-039**: If more than 10 images are attempted to be uploaded, the system displays "No more than 10 images can be uploaded."
- **RN-040**: If mandatory fields are missing when creating an accommodation, the system displays "Complete the mandatory fields."
- **RN-041**: The accommodation title has a maximum limit of 100 characters.
- **RN-042**: The accommodation description has a maximum limit of 1000 characters.
- **RN-043**: If the title or description exceeds the limits, the system displays "The title or description exceeds the allowed character limit."
- **RN-044**: Accommodations are created with the "active" status by default.
- **RN-045**: Only the accommodation owner can edit it. If another host attempts to edit it, the system displays "You do not have permission to edit this accommodation."
- **RN-046**: If an attempt is made to edit a non-existent accommodation, the system displays "The accommodation does not exist."
- **RN-047**: An accommodation can only be deleted if it has no future reservations. If it has future reservations, the system displays "Accommodation with future reservations cannot be deleted."
- **RN-048**: Accommodation deletion is a "soft delete" - it is marked with "deleted" status in the database without physical removal.
- **RN-049**: If a host has no registered accommodations, the system displays "You have no registered accommodations."
- **RN-083**: Accommodations marked as "deleted" do not appear in public searches or user views.

### Reservation Management
- **RN-050**: Hosts can view all reservations associated with their accommodations.
- **RN-051**: Host reservations can be filtered by dates and status (pending, confirmed, canceled, completed).
- **RN-052**: If there are no reservations associated with the host's accommodations, the system displays "There are no reservations associated with your accommodations."
- **RN-053**: Hosts receive a notification via email or in-app when a new reservation is created.
- **RN-054**: Hosts receive a notification when a reservation is canceled.

### Metrics and Analysis
- **RN-055**: Hosts can view basic metrics per accommodation: number of reservations and average rating.
- **RN-056**: Metrics can be filtered by date range.
- **RN-057**: If no metrics are available for accommodation in the selected period, the system displays "No metrics available for this accommodation in the selected range."

### Communication
- **RN-058**: Hosts can respond to guest comments.
- **RN-059**: Responses to comments have a maximum limit of 500 characters.
- **RN-060**: If the response exceeds 500 characters, the system displays "The response exceeds the 500-character limit."
- **RN-061**: If an attempt is made to respond to a non-existent comment, the system displays "The comment does not exist."
- **RN-062**: Hosts receive notifications when new comments are left on their accommodations.
- **RN-063**: Notifications for new comments include: accommodation name, comment text, rating (1-5 stars), and guest name.
- **RN-064**: Notifications for new reservations include: accommodation name, reservation ID, check-in/check-out dates, number of guests, and guest contact details.
- **RN-065**: Cancellation notifications include: reservation ID, accommodation name, and cancellation date.

## System

### Validations and Security
- **RN-066**: The system must validate that each email is unique across the platform during registration.
- **RN-067**: All passwords must be stored encrypted in the database using BCrypt.
- **RN-068**: During login, the system must compare the entered password with the stored encrypted version.
- **RN-069**: Only users with reservations in "Completed" status (check-out date in the past) can leave comments.
- **RN-070**: The system must generate a JWT token for persistent sessions after a successful login.

### Notifications and Reminders
- **RN-071**: The system must run a scheduled task to check for reservations with check-in within the next 7 days and send automatic reminders.
- **RN-072**: If a notification fails to send, the system must log the error and schedule retries until it is completed or marked as failed.
- **RN-073**: Notifications can be sent via email or in-app notification.

### Status Management
- **RN-074**: Reservations can have the following statuses: Pending, Confirmed, Canceled, Completed.
- **RN-075**: Accommodations can have the following statuses: Active, Deleted.
- **RN-076**: Only accommodations in "Active" status appear in searches and are visible to users.

### Calculations and Updates
- **RN-077**: When saving a new comment, the system must automatically update the accommodation's average rating.
- **RN-078**: Comments are sorted by date with the most recent first in the accommodation view.

### Image and Location Management
- **RN-079**: Accommodation images are stored and managed through an external service.
- **RN-080**: Mapbox must be used to display the exact location of accommodations.
- **RN-081**: Each accommodation must include mandatory latitude and longitude coordinates to display its exact location on the map.
- **RN-082**: Upon successful registration, the system redirects the host to the host dashboard.
- **RN-083**: Accommodations marked as "Deleted" do not appear in public searches or user views.