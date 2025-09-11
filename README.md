<p align="center">
  <img src="assets/logo-git.png" alt="StayHub Logo" width="150" height="50">
</p>

A platform to manage accommodations, reservations, and user reviews with efficiency and reliability.

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com/Estebangmz666/StayHub/actions)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.2-6DB33F)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange)](https://www.oracle.com/java/)
[![MariaDB](https://img.shields.io/badge/MariaDB-12.0.2-blue)](https://mariadb.org/)
[![Mailtrap](https://img.shields.io/badge/Mailtrap-Email%20Testing-8757F4)](https://mailtrap.io/)
[![Angular](https://img.shields.io/badge/Angular-18-DD0031)](https://angular.io/)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

## Overview
StayHub is a platform designed to streamline the management of accommodations, reservations, and user reviews. The backend is built with Spring Boot and MariaDB, integrated with Mailtrap for email notifications. The frontend, developed using Angular, is currently in progress. The platform aims to provide a seamless experience for hosts and guests.

### Current Status
- **Phase 1 Completed**: Backend fully functional, including user authentication (register/login), accommodation management (CRUD), reservations, comments, and automated email reminders via Mailtrap.
- **REST API**: Implemented with OpenAPI documentation available at `api-spec.yaml`.
- **Next Steps**: Frontend development and wireframe design are in progress.

## Project Structure
| Folder            | Description                                              |
|-------------------|----------------------------------------------------------|
| `stayhub-api`     | Spring Boot backend with Java 21, MariaDB, and Mailtrap.  |
| `stayhub-front`   | Angular frontend (in development, wireframes planned).    |
| `docs`            | Project documentation (architecture, test plan, stories). |

## Requirements
To set up the project, ensure the following are installed:
- Java 21 ([Download](https://www.oracle.com/java/technologies/downloads/))
- Maven ([Install](https://maven.apache.org/install.html))
- MariaDB 12.0.2 ([Setup](https://mariadb.org/download/))
- Mailtrap Account ([Sign Up](https://mailtrap.io/))
- Node.js & npm (optional for frontend) ([Download](https://nodejs.org/))

## Backend Setup
Follow these steps to configure and run the StayHub backend:

1. **Install MariaDB** and create the `stayhub` database:
   ```sql
   CREATE DATABASE stayhub;
   CREATE USER 'stayhub_user'@'localhost' IDENTIFIED BY 'stayhub_pass123';
   GRANT ALL PRIVILEGES ON stayhub.* TO 'stayhub_user'@'localhost';
   FLUSH PRIVILEGES;
   ```

2. **Configure `application.properties`**:
   Copy the example configuration file:
   ```bash
   cd stayhub-api/src/main/resources/application.properties.example stayhub-api/src/main/resources/application.properties
   ```
   Update `application.properties` with your MariaDB and Mailtrap credentials:
   ```properties
   spring.datasource.url=jdbc:mariadb://localhost:3306/stayhub
   spring.datasource.username=stayhub_user
   spring.datasource.password=stayhub_pass123
   spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDBDialect
   spring.mail.host=sandbox.smtp.mailtrap.io
   spring.mail.port=587
   spring.mail.username=your_mailtrap_username
   spring.mail.password=your_mailtrap_password
   spring.mail.properties.mail.smtp.auth=true
   spring.mail.properties.mail.smtp.starttls.enable=true
   ```

3. **Run the Backend**:
   Navigate to the backend folder and start the application:
   ```bash
   cd stayhub-api
   mvn spring-boot:run
   ```

4. **Test Email Notifications**:
   Insert test data into MariaDB to verify email functionality:
   ```sql
   INSERT INTO users (email, name, phone_number, birth_date, password, role, is_deleted)
   VALUES ('test@example.com', 'Test User', '+57 3001234567', '1990-01-01', '$2a$10$GESMt3mBlTxbX7ONVJdfG.YkEc1HLrGEteEbYNAZ3O0Ef7bU0eD2a', 'USER', FALSE);
   INSERT INTO users (email, name, phone_number, birth_date, password, role, is_deleted)
   VALUES ('host@example.com', 'Test Host', '+57 3001234568', '1985-01-01', '$2a$10$6sIeddB2Ujcl4ltoLwQui.JiZRA8aJgaCOAb9xVyaUM9d3.xULdWe', 'HOST', FALSE);
   INSERT INTO accommodations (host_id, title, description, price_per_night, capacity, latitude, longitude, is_deleted)
   VALUES (2, 'Test House', 'A cozy house', 100.00, 4, 4.6097, -74.0817, FALSE);
   INSERT INTO reservations (user_id, accommodation_id, check_in_date, check_out_date, number_of_guests, status, is_deleted)
   VALUES (1, 1, '2025-08-24', '2025-08-26', 2, 'PENDING', FALSE);
   ```
   Verify emails in your Mailtrap project inbox.

## Features
- **Accommodation Management**: Create, update, and delete accommodations via REST endpoints.
- **Reservation System**: Book and manage reservations with automated check-in reminders.
- **User Profiles**: Secure registration and login for guests and hosts using BCrypt password hashing.
- **Email Notifications**: Automated email notifications via Mailtrap.
- **REST API**: Fully implemented with OpenAPI documentation accessible at `/v3/api-docs`.

## Roadmap
| Task                    | Status         |
|-------------------------|----------------|
| MariaDB database setup  | Completed      |
| Email notifications     | Completed      |
| REST API controllers    | Completed      |
| OpenAPI documentation   | Completed      |
| Backend testing         | Completed      |
| Frontend development    | In Progress    |
| User interface polish   | Planned        |

## Contributing
Contributions are welcome. To contribute:
1. Fork the repository.
2. Create a branch: `git checkout -b feature/awesome-feature`
3. Commit your changes: `git commit -m 'Add awesome feature'`
4. Push to the branch: `git push origin feature/awesome-feature`
5. Open a pull request.

## Contact
[![Email](https://img.shields.io/badge/Email-estebangumy05@gmail.com-lightgrey?style=for-the-badge)](mailto:estebangumy05@gmail.com)  
[![GitHub](https://img.shields.io/badge/GitHub-@estebangmz666-black?style=for-the-badge&logo=github)](https://github.com/estebangmz666)  
[![LinkedIn](https://img.shields.io/badge/LinkedIn-znotkayn-blue?style=for-the-badge&logo=linkedin)](https://linkedin.com/in/znotkayn)

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

StayHub: The smart way to book, host and manage stays.
