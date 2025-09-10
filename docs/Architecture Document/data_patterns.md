# StayHub - Data Access Patterns Documentation

This document defines the **Repository**, **Data Access Object (DAO)**, and **Data Mapper** patterns implemented in the StayHub application to manage data persistence and mapping. These patterns are integral to the **Data Access Layer** within the Extended MVC architecture, utilizing **Spring Boot, JPA/Hibernate, and MariaDB**.

---

## Table of Contents
- [Repository Pattern](#repository-pattern)
    - [Definition](#definition)
    - [Purpose in StayHub](#purpose-in-stayhub)
    - [Implementation](#implementation)
    - [Example](#example)
    - [Benefits](#benefits)
- [Data Access Object (DAO) Pattern](#data-access-object-dao-pattern)
    - [Definition](#definition-1)
    - [Purpose in StayHub](#purpose-in-stayhub-1)
    - [Implementation](#implementation-1)
    - [Example](#example-1)
    - [Benefits](#benefits-1)
- [Data Mapper Pattern](#data-mapper-pattern)
    - [Definition](#definition-2)
    - [Purpose in StayHub](#purpose-in-stayhub-2)
    - [Implementation](#implementation-2)
    - [Example](#example-2)
    - [Benefits](#benefits-2)
- [Integration in StayHub](#integration-in-stayhub)
    - [Layering](#layering)
    - [Workflow](#workflow)
    - [Considerations](#considerations)
    - [Current Status in StayHub](#current-status-in-stayhub)

---

## Repository Pattern

### Definition
The **Repository pattern** provides a collection-like interface for accessing domain entities, abstracting the data layer. It encapsulates basic CRUD (Create, Read, Update, Delete) operations and supports custom queries, offering a clean API for the service layer.

### Purpose in StayHub
- Simplifies database interactions by providing methods to manage entities such as `User`, `Accommodation`, and `Comment`.
- Enables query derivation (e.g., `findByCity`) and custom JPQL queries (e.g., `findByCriteria`) to support features like accommodation search (**RNF-006**).
- Ensures soft deletion logic (e.g., `findByIsDeletedFalse`) is consistently applied.

### Implementation
- Leverages Spring Data JPA’s `JpaRepository` interface, extended with custom methods using naming conventions or `@Query` annotations.
- Annotated with `@Repository` for Spring component scanning.

### Example
```java
@Repository
public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {
    List<Accommodation> findByCityContainingIgnoreCaseAndIsDeletedFalse(String city);

    @Query("SELECT a FROM Accommodation a WHERE a.isDeleted = false AND (:city IS NULL OR LOWER(a.city) LIKE LOWER(CONCAT('%', :city, '%')))")
    Page<Accommodation> findByCriteria(@Param("city") String city, Pageable pageable);
}
```

# Explanation

The `AccommodationRepository` provides a paginated search by city, filtering out deleted records, aligning with the search endpoint requirements.

## Benefits
- Reduces boilerplate code with built-in CRUD methods.
- Enhances testability by allowing mock implementations.

## Data Access Object (DAO) Pattern

### Definition
The DAO pattern defines an object that encapsulates the data access logic, providing an abstraction over the data source. It separates data access from business logic, allowing for custom operations beyond repository capabilities.

### Purpose in StayHub
- Adds entity-specific business rules (e.g., validating rating for Comment per RN-026).
- Filters soft-deleted records or enforces constraints (e.g., unique email for User).
- Provides flexibility to switch data sources or add complex logic.

### Implementation
- Implemented as `@Component` classes injecting the corresponding Repository.
- Includes methods for save operations with validation and custom queries.

### Example

```java
@Component
public class CommentDAO {
    private final CommentRepository commentRepository;

    public CommentDAO(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment saveComment(Comment comment) {
        if (comment.getRating() < 1 || comment.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        return commentRepository.save(comment);
    }

    public List<Comment> findActiveByAccommodationId(Long accommodationId) {
        return commentRepository.findByAccommodationIdAndIsDeletedFalseOrderByCreatedAtDesc(accommodationId);
    }
}
```
# Explanation
The `CommentDAO` ensures ratings are within 1–5 and retrieves active comments, enhancing the `CommentRepository`.

## Benefits
- Improves maintainability by centralizing data access logic.
- Supports complex business rules without cluttering repositories.

## Data Mapper Pattern

### Definition
The Data Mapper pattern maps between the in-memory object model (domain entities) and the database schema, handling the transformation of data. It separates the object-oriented domain from the relational database structure.

### Purpose in StayHub
- Manages ORM mapping using Hibernate/JPA annotations (e.g., `@Entity`, `@ManyToOne`).
- Converts entities to DTOs (e.g., `Accommodation` to `AccommodationResponseDTO`) using MapStruct for the Presentation Layer.
- Handles nested relationships and ensures data consistency.

### Implementation
- Uses JPA annotations in entity classes for database mapping.
- Employs MapStruct interfaces with `@Mapper` to define DTO-to-entity and entity-to-DTO transformations.

### Example

**Entity:**
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    // Other fields and annotations
}
```

## Mapper

```java
@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    UserResponseDTO toResponseDto(User user);
}
```
### Explanation
The `User` entity is mapped to the database, and the `UserMapper` transforms it into `UserResponseDTO` for API responses.

---

### Benefits
- Decouples domain model from database schema.  
- Simplifies DTO mapping with MapStruct’s code generation.  

---

## Integration in StayHub

### Layering
- **Repository**: Interfaces (e.g., `UserRepository`) are injected into DAOs or services for data access.  
- **DAO**: Classes (e.g., `UserDAO`) add business logic and call repository methods.  
- **Data Mapper**: Mappers (e.g., `UserMapper`) transform entities to DTOs for the controller layer.  

### Workflow
1. Service layer uses DAOs to perform operations, which interact with repositories.  
2. Mappers convert results to DTOs before returning them via controllers.  

### Considerations
- Use `@Transactional` in services to ensure data integrity.  
- Validate entity constraints in DAOs (e.g., **RN-001** for unique emails).  

---

## Current Status in StayHub
- **Repositories**: Implemented for `Accommodation`, `Amenity`, `Comment`, `PasswordResetToken`, `Reservation`, and `User`.  
- **DAOs**: Implemented for `Accommodation`, `Comment`, `Reservation`, and `User`; pending for `Amenity` and `PasswordResetToken`.  
- **Data Mappers**: Implemented for `User`; pending for `Accommodation`, `Comment`, `Reservation`, `HostProfile`, `Amenity`, and `PasswordResetToken`.  

