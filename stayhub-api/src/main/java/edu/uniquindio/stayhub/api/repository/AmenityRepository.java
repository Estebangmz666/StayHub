package edu.uniquindio.stayhub.api.repository;

import edu.uniquindio.stayhub.api.model.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Amenity entities.
 * This interface extends JpaRepository to provide standard CRUD operations
 * and defines custom query methods for specific data access needs.
 */
@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Long> {

    /**
     * Retrieves a list of all amenities that are currently active,
     * sorted alphabetically by their name.
     * @return A list of active Amenity entities.
     */
    List<Amenity> findByIsActiveTrueOrderByName();

    /**
     * Finds an amenity by its exact name.
     * @param name The name of the amenity to find.
     * @return An Optional containing the Amenity if found, otherwise an empty Optional.
     */
    Optional<Amenity> findByName(String name);

    /**
     * Checks if an amenity with the specified name already exists in the database.
     * @param name The name to check for.
     * @return true if an amenity with the name exists, false otherwise.
     */
    boolean existsByName(String name);

    /**
     * Retrieves a list of active amenities whose name contains the specified string,
     * ignoring case.
     * @param name The string to search for within the amenity names.
     * @return A list of matching active Amenity entities.
     */
    List<Amenity> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);
}