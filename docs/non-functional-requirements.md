# Non-Functional Requirements - StayHub

**Note**: The identifier "RNF-XXX" is used for Non-Functional Requirements to maintain consistency with the Spanish documentation, where "RNF" stands for "Requerimiento No Funcional" (Non-Functional Requirement). These requirements complement the Business Rules (RN-XXX) defined for StayHub.

## Performance

- **RNF-001**: **Search Response Time**  
  The accommodation search must return results in less than 2 seconds under normal conditions, even with 10,000 active accommodations in the database.

- **RNF-002**: **Page Load Time**  
  All application pages must load within 3 seconds on a standard broadband connection (10 Mbps) to ensure good user experience.

- **RNF-003**: **Image Loading Performance**  
  Accommodation images must be optimized and load within 5 seconds, with progressive loading for galleries containing up to 10 images.

- **RNF-004**: **Map Rendering Performance**  
  Interactive maps using Mapbox must render accommodation locations within 4 seconds, even when displaying multiple properties simultaneously.

- **RNF-005**: **Pagination Performance**  
  Search results pagination (10 items per page) must navigate between pages in less than 1 second to maintain smooth browsing experience.

- **RNF-006**: **Database Query Optimization**  
  All database queries for reservations, accommodations, and user data must execute in less than 500ms to prevent application delays.

- **RNF-007**: **Concurrent User Handling**  
  The system must support at least 1000 concurrent users performing searches and reservations without performance degradation.

- **RNF-008**: **File Upload Performance**  
  Image uploads to external storage services (Cloudinary, AWS S3, etc.) must complete within 10 seconds per image for accommodation creation.

## Security

- **RNF-009**: **Password Encryption**  
  All user passwords must be encrypted using BCrypt with a minimum salt rounds of 12 to ensure strong password protection.

- **RNF-010**: **JWT Token Security**  
  Authentication tokens must expire after 24 hours and include secure claims to prevent unauthorized access to user accounts.

- **RNF-011**: **Input Validation**  
  All user inputs must be validated and sanitized on both client and server sides to prevent SQL injection and XSS attacks.

- **RNF-012**: **Password Recovery Security**  
  Password recovery codes must be cryptographically secure, single-use, and expire after exactly 15 minutes to prevent unauthorized account access.

- **RNF-013**: **Email Uniqueness Validation**  
  The system must enforce email uniqueness across all user roles to prevent account duplication and maintain data integrity.

- **RNF-014**: **Sensitive Data Protection**  
  Personal user information (names, emails, phone numbers) must be protected and only accessible to authorized users and system functions.

- **RNF-015**: **External Service Security**  
  All communications with external services (image storage, email services, Mapbox) must use HTTPS/TLS encryption.

- **RNF-016**: **Session Management**  
  User sessions must be properly managed with secure logout functionality that invalidates JWT tokens immediately.

- **RNF-017**: **Access Control**  
  Role-based access control must ensure hosts can only modify their own accommodations and view their own reservations.

## Usability

- **RNF-018**: **Responsive Design**  
  The application must be fully responsive and functional across desktop, tablet, and mobile devices with screen sizes from 320px to 1920px.

- **RNF-019**: **Intuitive Navigation**  
  Users must be able to complete core tasks (search, book, cancel reservations) within 5 clicks from the main page.

- **RNF-020**: **Form Validation Feedback**  
  All forms must provide immediate, clear feedback for validation errors with specific messages in Spanish as defined in the business rules.

- **RNF-021**: **Search Filter Usability**  
  Search filters must be easily accessible and allow users to refine results without losing their previous selections.

- **RNF-022**: **Calendar Interface**  
  The availability calendar must be intuitive with clear visual indicators for available, booked, and selected dates.

- **RNF-023**: **Image Gallery Experience**  
  Accommodation image galleries must support easy navigation with thumbnail previews and full-size viewing capabilities.

- **RNF-024**: **Loading States**  
  All loading processes must display appropriate loading indicators or progress bars to keep users informed of system status.

- **RNF-025**: **Error Message Clarity**  
  Error messages must be user-friendly, specific, and provide actionable guidance in Spanish following the established message formats.

- **RNF-026**: **Notification Accessibility**  
  In-app notifications must be clearly visible and easily dismissible without interfering with the main user workflow.

- **RNF-027**: **Mobile Optimization**  
  Touch interfaces must be optimized for mobile use with appropriately sized buttons (minimum 44px) and touch-friendly spacing.

- **RNF-028**: **Language Consistency**  
  The entire user interface must maintain consistent Spanish language usage with proper grammar and terminology.

## Reliability

- **RNF-029**: **System Availability**  
  The application must maintain 99% uptime during business hours (6 AM to 11 PM local time) to ensure consistent service availability.

- **RNF-030**: **Data Backup**  
  All user data, reservations, and accommodation information must be backed up daily to prevent data loss.

- **RNF-031**: **Graceful Error Handling**  
  The system must handle external service failures (image storage, email, maps) gracefully without breaking core functionality.

- **RNF-032**: **Notification Retry Logic**  
  Failed notifications must be retried up to 3 times with exponential backoff before being marked as failed.

## Scalability

- **RNF-033**: **Database Scalability**  
  The database design must support growth to at least 10,000 accommodations and 100,000 users without major architectural changes.

- **RNF-034**: **Image Storage Scalability**  
  The external image storage solution must handle up to 100,000 images (10 images × 10,000 accommodations) with efficient retrieval.

- **RNF-035**: **Search Scalability**  
  Search functionality must remain performant as the accommodation database grows, using appropriate indexing strategies.

## Compatibility

- **RNF-036**: **Browser Compatibility**  
  The application must function properly on modern browsers: Chrome 90+, Firefox 88+, Safari 14+, and Edge 90+.

- **RNF-037**: **Technology Stack Compatibility**  
  The system must use Spring Boot for backend, Angular for frontend, and MariaDB for database as specified in project requirements.

- **RNF-038**: **External Service Integration**  
  The system must properly integrate with chosen external services for image storage and Mapbox for mapping functionality.

- **RNF-039**: **WCAG 2.1 Level AA Compliance**
  The interface must comply with WCAG 2.1 Level AA accessibility guidelines, including support for screen readers, high contrast for visually impaired users, and keyboard navigation.