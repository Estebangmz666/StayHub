# StayHub 🏡✨

![StayHub Logo](assets/logo.png)  
**A platform to manage accommodations, reservations, and reviews with style and efficiency.**

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com/Estebangmz666/StayHub/actions)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.2-6DB33F)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange)](https://www.oracle.com/java/)
[![MariaDB](https://img.shields.io/badge/MariaDB-12.0.2-blue)](https://mariadb.org/)
[![Mailtrap](https://img.shields.io/badge/Mailtrap-Email%20Testing-8757F4)](https://mailtrap.io/)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

## 🚀 Overview

StayHub is a modern platform designed to streamline the management of accommodations, reservations, and user reviews. The backend is powered by **Spring Boot** with **MariaDB** for data persistence and **Mailtrap** for email notifications. The frontend (coming soon) will provide a seamless user experience.

> **Current Status**: Backend fully functional with email reminders via Mailtrap. Ready for REST API development and OpenAPI documentation.

## 🛠️ Project Structure

- **stayhub-api**: Backend built with Spring Boot, Java, MariaDB, and Mailtrap.
- **stayhub-front**: Frontend (under development).

## 📋 Requirements

- **Java 21** [](https://www.oracle.com/java/technologies/downloads/)
- **Maven** [](https://maven.apache.org/install.html)
- **MariaDB 12.0.2** [](https://mariadb.org/download/)
- **Mailtrap Account** [](https://mailtrap.io/)

## ⚙️ Backend Setup

1. **Install MariaDB** and create the `stayhub` database:
   ```sql
   CREATE DATABASE stayhub;
   CREATE USER 'stayhub_user'@'localhost' IDENTIFIED BY 'stayhub_pass123';
   GRANT ALL PRIVILEGES ON stayhub.* TO 'stayhub_user'@'localhost';
   FLUSH PRIVILEGES;