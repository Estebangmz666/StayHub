package edu.uniquindio.stayhub.api.repository;

import edu.uniquindio.stayhub.api.model.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    List<Amenity> findByActiveTrueOrderByName();

    /**
     * Checks if an amenity with the specified name already exists in the database.
     * @param name The name to check for.
     * @return true if an amenity with the name exists, false otherwise.
     */
    boolean existsByName(String name);
}