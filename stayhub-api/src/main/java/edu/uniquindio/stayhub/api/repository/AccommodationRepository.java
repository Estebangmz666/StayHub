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

@Repository
public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {

    List<Accommodation> findByIsDeletedFalse();

    List<Accommodation> findByHostIdAndIsDeletedFalse(Long hostId);

    List<String> findDistinctCityByIsDeletedFalseOrderByCity();

    @Query("SELECT DISTINCT a.city FROM Accommodation a WHERE a.city ILIKE %:query% AND a.isDeleted = false")
    List<String> findCitiesByQuery(@Param("query") String query);

    Page<Accommodation> findByCityContainingIgnoreCaseAndIsDeletedFalse(String city, Pageable pageable);

    Page<Accommodation> findByPricePerNightBetweenAndIsDeletedFalse(
            BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    @Query("SELECT a FROM Accommodation a WHERE " +
            "(:city IS NULL OR LOWER(a.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
            "(:minPrice IS NULL OR a.pricePerNight >= :minPrice) AND " +
            "(:maxPrice IS NULL OR a.pricePerNight <= :maxPrice) AND " +
            "a.isDeleted = false")
    Page<Accommodation> findByCriteria(@Param("city") String city,
                                       @Param("minPrice") BigDecimal minPrice,
                                       @Param("maxPrice") BigDecimal maxPrice,
                                       Pageable pageable);
}