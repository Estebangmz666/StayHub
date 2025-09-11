-- StayHub - Database Model

-- Drop existing database (optional, if you want to start fresh)
DROP DATABASE IF EXISTS stayhub;
CREATE DATABASE stayhub;
USE stayhub;

-- Create Amenities Table
CREATE TABLE amenities (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           name VARCHAR(50) UNIQUE NOT NULL,
                           is_active BOOLEAN DEFAULT TRUE
);

-- Create Users Table
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role ENUM('USER', 'HOST') NOT NULL,
                       name VARCHAR(100) NOT NULL,
                       phone_number VARCHAR(20) NOT NULL,
                       birth_date DATE NOT NULL,
                       deleted BOOLEAN DEFAULT FALSE NOT NULL, -- Cambiado de is_deleted
                       profile_picture VARCHAR(255),
                       host_profile_id BIGINT,
                       FOREIGN KEY (host_profile_id) REFERENCES host_profiles(id) ON DELETE CASCADE,
                       INDEX idx_email (email),
                       INDEX idx_role (role),
                       INDEX idx_deleted (deleted) -- Cambiado de idx_is_deleted
);

-- Create Host Profiles Table
CREATE TABLE host_profiles (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               description VARCHAR(500),
                               user_id BIGINT NOT NULL,
                               FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create Accommodations Table
CREATE TABLE accommodations (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                title VARCHAR(100) NOT NULL,
                                description VARCHAR(1000) NOT NULL,
                                capacity INT NOT NULL,
                                main_image VARCHAR(255),
                                longitude DOUBLE NOT NULL,
                                latitude DOUBLE NOT NULL,
                                location_description VARCHAR(200) NOT NULL,
                                city VARCHAR(50) NOT NULL,
                                price_per_night DECIMAL(10,2) NOT NULL,
                                deleted BOOLEAN DEFAULT FALSE NOT NULL, -- Cambiado de is_deleted
                                host_id BIGINT NOT NULL,
                                FOREIGN KEY (host_id) REFERENCES users(id) ON DELETE CASCADE,
                                INDEX idx_host_id (host_id),
                                INDEX idx_location (latitude, longitude),
                                INDEX idx_city (city),
                                INDEX idx_deleted (deleted) -- Cambiado de idx_is_deleted
);

-- Create Accommodation Amenities Join Table
CREATE TABLE accommodation_amenities (
                                         accommodation_id BIGINT NOT NULL,
                                         amenity_id BIGINT NOT NULL,
                                         PRIMARY KEY (accommodation_id, amenity_id),
                                         FOREIGN KEY (accommodation_id) REFERENCES accommodations(id) ON DELETE CASCADE,
                                         FOREIGN KEY (amenity_id) REFERENCES amenities(id) ON DELETE CASCADE
);

-- Create Reservations Table
CREATE TABLE reservations (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              user_id BIGINT NOT NULL,
                              accommodation_id BIGINT NOT NULL,
                              check_in_date DATE NOT NULL,
                              check_out_date DATE NOT NULL,
                              number_of_guests INT NOT NULL,
                              status ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED') NOT NULL,
                              deleted BOOLEAN DEFAULT FALSE NOT NULL, -- Cambiado de is_deleted
                              FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                              FOREIGN KEY (accommodation_id) REFERENCES accommodations(id) ON DELETE CASCADE,
                              INDEX idx_user_id (user_id),
                              INDEX idx_accommodation_id (accommodation_id),
                              INDEX idx_check_in_date (check_in_date),
                              INDEX idx_deleted (deleted) -- Cambiado de idx_is_deleted
);

-- Create Comments Table
CREATE TABLE comments (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          user_id BIGINT NOT NULL,
                          accommodation_id BIGINT NOT NULL,
                          text VARCHAR(500) NOT NULL,
                          rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          deleted BOOLEAN DEFAULT FALSE NOT NULL, -- Cambiado de is_deleted
                          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                          FOREIGN KEY (accommodation_id) REFERENCES accommodations(id) ON DELETE CASCADE,
                          INDEX idx_user_id (user_id),
                          INDEX idx_accommodation_id (accommodation_id),
                          INDEX idx_deleted (deleted) -- Cambiado de idx_is_deleted
);

-- Create Notifications Table
CREATE TABLE notifications (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               user_id BIGINT NOT NULL,
                               type ENUM('RESERVATION', 'COMMENT', 'SYSTEM') NOT NULL,
                               message VARCHAR(500) NOT NULL,
                               status ENUM('UNREAD', 'READ') NOT NULL,
                               deleted BOOLEAN DEFAULT FALSE NOT NULL, -- Cambiado de is_deleted
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                               INDEX idx_user_id (user_id),
                               INDEX idx_deleted (deleted)
);

-- Create Password Reset Tokens Table
CREATE TABLE password_reset_tokens (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       token VARCHAR(255) NOT NULL,
                                       user_id BIGINT NOT NULL,
                                       expiry_date TIMESTAMP NOT NULL,
                                       used BOOLEAN DEFAULT FALSE,
                                       FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                       INDEX idx_token (token)
);