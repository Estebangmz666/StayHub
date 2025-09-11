package edu.uniquindio.stayhub.api.repository;

import edu.uniquindio.stayhub.api.model.Accommodation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository interface for managing Accommodation entities.
 * This interface extends JpaRepository to provide standard CRUD operations
 * and defines custom query methods for specific data access needs.
 */
@Repository
public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {

    /**
     * Retrieves all accommodations that have not been soft-deleted.
     * @return A list of non-deleted Accommodation entities.
     */
    List<Accommodation> findByDeletedFalse();

    /**
     * Retrieves a list of non-deleted accommodations for a specific host.
     * @param hostId The ID of the host.
     * @return A list of non-deleted Accommodation entities belonging to the specified host.
     */
    List<Accommodation> findByHostIdAndDeletedFalse(Long hostId);

    /**
     * Retrieves a distinct, alphabetically sorted list of cities from all non-deleted accommodations.
     * @return A list of unique city names.
     */
    @Query("SELECT DISTINCT a.city FROM Accommodation a WHERE a.deleted = false ORDER BY a.city")
    List<String> findDistinctCities();

    /**
     * Finds cities containing the specified query string, ignoring case, from non-deleted accommodations.
     * @param query The city search query.
     * @return A list of matching city names.
     */
    @Query("SELECT DISTINCT a.city FROM Accommodation a WHERE a.city ILIKE %:query% AND a.deleted = false")
    List<String> findCitiesByQuery(@Param("query") String query);

    /**
     * Finds a paginated list of non-deleted accommodations located in a specific city,
     * with a case-insensitive search.
     * @param city The city to search for.
     * @param pageable The pagination information.
     * @return A Page of matching Accommodation entities.
     */
    Page<Accommodation> findByCityContainingIgnoreCaseAndDeletedFalse(String city, Pageable pageable);

    /**
     * Finds a paginated list of non-deleted accommodations within a specific price range.
     * @param minPrice The minimum price per night.
     * @param maxPrice The maximum price per night.
     * @param pageable The pagination information.
     * @return A Page of matching Accommodation entities.
     */
    Page<Accommodation> findByPricePerNightBetweenAndDeletedFalse(
            BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    /**
     * Finds a paginated list of non-deleted accommodations based on a combination of
     * optional criteria, including city and price range.
     * @param city An optional city to filter by.
     * @param minPrice An optional minimum price per night.
     * @param maxPrice An optional maximum price per night.
     * @param pageable The pagination information.
     * @return A Page of matching Accommodation entities.
     */
    @Query("SELECT a FROM Accommodation a WHERE " +
            "(:city IS NULL OR LOWER(a.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
            "(:minPrice IS NULL OR a.pricePerNight >= :minPrice) AND " +
            "(:maxPrice IS NULL OR a.pricePerNight <= :maxPrice) AND " +
            "a.deleted = false")
    Page<Accommodation> findByCriteria(@Param("city") String city,
                                       @Param("minPrice") BigDecimal minPrice,
                                       @Param("maxPrice") BigDecimal maxPrice,
                                       Pageable pageable);
}