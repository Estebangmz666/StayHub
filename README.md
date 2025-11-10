<p align="center">
  <img src="assets/logo-git.png" alt="StayHub Logo" width="150" height="50">
</p>

<h1 align="center">StayHub</h1>

<p align="center">
  A comprehensive platform for managing accommodations, reservations, and guest reviews.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.3.2-6DB33F" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Angular-18-DD0031" alt="Angular">
  <img src="https://img.shields.io/badge/License-MIT-yellow" alt="License">
</p>

---

## Overview

StayHub streamlines the entire booking lifecycle for both hosts and guests. Built with a Spring Boot backend and Angular frontend, it handles user authentication, property listings, reservation management, and automated notifications—all through a clean REST API.

**Key capabilities:**
- Secure user authentication and role-based access (guests/hosts)
- Full CRUD operations for accommodation listings
- Reservation system with automated email reminders
- Review and rating system
- RESTful API with OpenAPI documentation

## Tech Stack

**Backend:**
- Java 21 & Spring Boot 3.3.2
- MariaDB 12.0.2
- Mailtrap for email delivery
- Spring Security with BCrypt

**Frontend:**
- Angular 18 (in development)

## Quick Start

### Prerequisites
- Java 21+
- Maven 3.6+
- MariaDB 12.0+
- Node.js 18+ (for frontend)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Estebangmz666/StayHub.git
   cd StayHub
   ```

2. **Set up the database**
   ```sql
   CREATE DATABASE stayhub;
   ```

3. **Configure application properties**
   
   Copy `stayhub-api/src/main/resources/application.properties.example` to `application.properties` and update with your credentials:
   ```properties
   spring.datasource.url=jdbc:mariadb://localhost:3306/stayhub
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   
   spring.mail.username=your_mailtrap_username
   spring.mail.password=your_mailtrap_password
   ```

4. **Run the backend**
   ```bash
   cd stayhub-api
   mvn spring-boot:run
   ```

The API will be available at `http://localhost:8080`

## API Documentation

Interactive API documentation is available at `/v3/api-docs` when the application is running. The complete OpenAPI specification can be found in `api-spec.yaml`.

## Project Structure

```
StayHub/
├── stayhub-api/      # Spring Boot backend
├── stayhub-front/    # Angular frontend
└── docs/             # Project documentation
```

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact

**Esteban Gómez**

[![Email](https://img.shields.io/badge/Email-estebangumy05@gmail.com-lightgrey?style=flat-square)](mailto:estebangumy05@gmail.com)
[![GitHub](https://img.shields.io/badge/GitHub-@estebangmz666-black?style=flat-square&logo=github)](https://github.com/estebangmz666)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-znotkayn-blue?style=flat-square&logo=linkedin)](https://linkedin.com/in/znotkayn)

---

<p align="center">Made with <3 by Esteban Gómez</p>
