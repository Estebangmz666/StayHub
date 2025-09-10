# StayHub - Coding Standards

This document outlines the coding standards and naming conventions for the StayHub application, ensuring consistency, readability, and maintainability. These standards align with Java Code Conventions (Oracle), Spring Boot best practices, and the bilingual nature of the project (English code, Spanish user-facing messages).

## 1. Naming Conventions

### 1.1 Packages
- **Rule**: Use lowercase names with a hierarchical structure based on the domain (e.g., `edu.uniquindio.stayhub`).
- **Examples**:
    - `edu.uniquindio.stayhub.api` (controllers, DTOs)
    - `edu.uniquindio.stayhub.api.model` (entities)
    - `edu.uniquindio.stayhub.api.repository` (repositories)
    - `edu.uniquindio.stayhub.api.dao` (DAOs)
    - `edu.uniquindio.stayhub.api.mapper` (mappers)
- **Standard**: Avoid abbreviations unless common (e.g., `api` instead of `applicationInterface`).

### 1.2 Classes and Interfaces
- **Rule**: Use `PascalCase` with descriptive names.
- **Examples**:
    - `AccommodationRepository`, `UserMapper`, `CommentDAO`
    - Interfaces may use `-able` suffix if applicable (e.g., `Serializable`).
- **Standard**: Avoid unnecessary prefixes or suffixes (e.g., no `IUserMapper`).

### 1.3 Methods
- **Rule**: Use `camelCase` with verb-based, descriptive names in infinitive form.
- **Examples**:
    - `saveComment()`, `findActiveByAccommodationId()`, `deleteByExpiryDateBefore()`
- **Standard**: Names should reflect the action (e.g., `getUserByEmail` instead of `findUser`).

### 1.4 Variables and Parameters
- **Rule**: Use `camelCase` with meaningful names.
- **Examples**:
    - `userId`, `accommodation`, `maxPrice`
- **Standard**: Avoid generic names like `x` or `temp`; use type hints if helpful (e.g., `userEmail`).

### 1.5 Constants
- **Rule**: Use `UPPER_CASE` with underscores (`_`).
- **Examples**:
    - `MAX_RATING = 5`, `DEFAULT_PAGE_SIZE = 10`
- **Standard**: Apply to fixed values only, not configurable settings.

### 1.6 DTOs and Entities
- **Rule**: Use `PascalCase` with `DTO` suffix for DTOs (e.g., `UserRegistrationDTO`), no suffix for entities (e.g., `User`).
- **Examples**:
    - `AccommodationResponseDTO`, `Reservation`
- **Standard**: Names should reflect purpose (e.g., `ResponseDTO` for API responses).

### 1.7 Validation Messages
- **Rule**: Use Spanish for user-facing validation messages.
- **Examples**:
    - `@NotBlank(message = "El nombre es obligatorio")`
- **Standard**: Keep messages short, clear, and consistent in Spanish.

## 2. Code Standards

### 2.1 Code Formatting
- **Indentation**: 4 spaces (no tabs), following Spring and IDE defaults (e.g., Eclipse, IntelliJ).
- **Line Length**: Maximum 120 characters for readability.
- **Spacing**: One space after commas, around operators (e.g., `x + y`), and before/after braces (e.g., `if (condition) {`).
- **Tool**: Use a formatter like Checkstyle or IDE settings with Google Java Style.

### 2.2 Comments
- **Classes and Methods**: Use Javadoc for purpose, parameters, and exceptions.
    - **Example**:
      ```java
      /**
       * Saves a user, checking for email uniqueness.
       * @param user The user to save
       * @return The saved user
       * @throws IllegalStateException if email already exists
       */
      public User saveUser(User user) {
          // ...
      }
      ```  
### Inline Comments
- **Rule**: Use only for complex logic, not obvious code.
- **Standard**: Comments in English, except validation messages in Spanish.

---

## 2.3 Modularity and reuse
- **Rule**: Separate logic into layers (controllers, services, DAOs, repositories).
- **Example**: `UserService` uses `UserDAO` and `UserMapper`, avoiding controller logic.
- **Standard**: Avoid code duplication; create reusable methods.

---

## 2.4 Exception Handling
- **Rule**: Throw specific exceptions with clear messages.
- **Example**:
  ```java
  throw new IllegalStateException("El email ya existe");
  ```

## 2.5 Versioning and Consistency
- **Rule**: Use consistent annotations (e.g., `@Getter`/`@Setter` from Lombok).
- **Example**: Apply `@Entity` and `@Table` to all entities.
- **Tool**: Use a linter like **Spotless** to enforce standards.

---

## 2.6 Testing
- **Rule**: Include unit tests for DAOs and services (e.g., JUnit, Mockito).
- **Example**: Test `saveComment` in `CommentDAO` for rating validation.
- **Standard**: Aim for high coverage of critical logic.

---

## 3. Example Applied to StayHub

### UserDAO Example
```java
package edu.uniquindio.stayhub.api.dao;

import edu.uniquindio.stayhub.api.model.User;
import edu.uniquindio.stayhub.api.repository.UserRepository;
import org.springframework.stereotype.Component;

/**
 * Data Access Object for User entity, handling user-specific operations.
 */
@Component
public class UserDAO {
    private final UserRepository userRepository;

    public UserDAO(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Saves a user, checking for email uniqueness.
     * @param user The user to save
     * @return The saved user
     * @throws IllegalStateException if email already exists
     */
    public User saveUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalStateException("Email already exists");
        }
        return userRepository.save(user);
    }
}
```
## Notes
- Uses `camelCase` for methods.
- Javadoc in English.
- Validation message in English for internal logic.

---

## 4. Implementation Guidelines
- **IDE Setup**: Configure formatter (e.g., IntelliJ: *Settings > Editor > Code Style > Java*).
- **Checkstyle**: Add a `checkstyle.xml` file with Google Java Style rules.
- **Review**: Apply these standards to existing repositories, DAOs, and mappers.
- **Documentation**: Maintain this file in the project root or `docs/` folder.  


## 5. Angular Coding Standards

### 5.1 Naming Conventions
- **Components**: Use `PascalCase` and suffix with `Component` (e.g., `BookingFormComponent`).
- **Services**: Use `PascalCase` and suffix with `Service` (e.g., `UserService`).
- **Modules**: Use `PascalCase` and suffix with `Module` (e.g., `AccommodationModule`).
- **Variables and Methods**: Use `camelCase`.

### 5.2 Folder Structure
- Group by feature (e.g., `/accommodation`, `/reservation`).
- Each feature folder contains `components`, `services`, `models`, and optionally `guards`.

### 5.3 HTML & CSS
- Use semantic HTML tags.
- Use Angular bindings (`[value]`, `(click)`) instead of direct DOM manipulation.
- Use SCSS with BEM naming convention.

### 5.4 Internationalization
- Use Spanish for user-facing messages.
- Store messages in `i18n` files or constants.

---

## 6. Git Commit Standards

### 6.1 Commit Message Format
Use [Conventional Commits](https://www.conventionalcommits.org):
- `feat:` for new features
- `fix:` for bug fixes
- `docs:` for documentation changes
- `style:` for formatting (no code change)
- `refactor:` for code refactoring
- `test:` for adding or updating tests
- `chore:` for maintenance tasks

### 6.2 Examples
- `feat: add reservation cancellation feature`
- `fix: correct date validation in booking form`

---

## 7. Testing Guidelines

### 7.1 Backend (Spring Boot)
- Use JUnit 5 and Mockito.
- Cover services and DAOs.
- Use `@WebMvcTest` for controller tests.
- Validate Spanish messages in exceptions.

### 7.2 Frontend (Angular)
- Use Jasmine and Karma.
- Write unit tests for components and services.
- Use `HttpTestingController` for API mocks.

### 7.3 Coverage Goals
- Aim for 80%+ coverage on critical modules.
- Use tools like JaCoCo (Java) and Istanbul (Angular).

---
