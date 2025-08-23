<img src="assets/logo-git.png" alt="StayHub Logo" width="150" height="50">  
A platform to manage accommodations, reservations, and reviews with efficiency and reliability.

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com/Estebangmz666/StayHub/actions)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.2-6DB33F)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange)](https://www.oracle.com/java/)
[![MariaDB](https://img.shields.io/badge/MariaDB-12.0.2-blue)](https://mariadb.org/)
[![Mailtrap](https://img.shields.io/badge/Mailtrap-Email%20Testing-8757F4)](https://mailtrap.io/)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

## Overview

StayHub is a platform designed to streamline the management of accommodations, reservations, and user reviews. The backend is built with Spring Boot, utilizing MariaDB for data persistence and Mailtrap for email notifications. The frontend is currently under development and will provide a seamless user experience.

**Current Status**: The backend is fully functional with automated email reminders via Mailtrap. The project is ready for REST API development and OpenAPI documentation.

## Project Structure

- **stayhub-api**: Backend developed with Spring Boot, Java, MariaDB, and Mailtrap integration.
- **stayhub-front**: Frontend (in development).

## Requirements

- Java 21 ([Download](https://www.oracle.com/java/technologies/downloads/))
- Maven ([Install](https://maven.apache.org/install.html))
- MariaDB 12.0.2 ([Setup](https://mariadb.org/download/))
- Mailtrap Account ([Sign Up](https://mailtrap.io/))

## Backend Setup

1. **Install MariaDB** and create the `stayhub` database:
   ```sql
   CREATE DATABASE stayhub;
   CREATE USER 'stayhub_user'@'localhost' IDENTIFIED BY 'stayhub_pass123';
   GRANT ALL PRIVILEGES ON stayhub.* TO 'stayhub_user'@'localhost';
   FLUSH PRIVILEGES;
   ```

2. **Configure `application.properties`**:
   Copy `stayhub-api/src/main/resources/application.properties.example` to `application.properties` and add your MariaDB and Mailtrap credentials:
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

3. **Run the backend**:
   ```bash
   cd stayhub-api
   mvn spring-boot:run
   ```

4. **Test email notifications**:
   Insert test data in MariaDB:
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
   Verify emails in Mailtrap's project inbox.

## Features

- **Accommodation Management**: Create, update, and delete accommodations.
- **Reservation System**: Book and manage reservations with automated email reminders.
- **User Profiles**: Support for hosts and guests with secure password handling using BCrypt.
- **Email Notifications**: Automated check-in reminders sent via Mailtrap.
- **REST API**: Planned for implementation with OpenAPI documentation.

## Roadmap

- [x] MariaDB database setup
- [x] Email notifications with Mailtrap
- [ ] REST API controllers
- [ ] OpenAPI documentation
- [ ] Frontend development

## Contributing

Contributions are welcome. To contribute:
1. Fork the repository.
2. Create a branch (`git checkout -b feature/awesome-feature`).
3. Commit your changes (`git commit -m 'Add awesome feature'`).
4. Push to the branch (`git push origin feature/awesome-feature`).
5. Open a pull request.

## Contact

[![Email](https://img.shields.io/badge/Email-estebangumy05@gmail.com-lightgrey?style=for-the-badge)](mailto:estebangumy05@gmail.com)  
[![GitHub](https://img.shields.io/badge/GitHub-@estebangmz666-black?style=for-the-badge&logo=github)](https://github.com/estebangmz666)  
[![LinkedIn](https://img.shields.io/badge/LinkedIn-znotkayn-blue?style=for-the-badge&logo=linkedin)](https://linkedin.com/in/znotkayn)

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

StayHub: The smart way to book, host and manage stays.
