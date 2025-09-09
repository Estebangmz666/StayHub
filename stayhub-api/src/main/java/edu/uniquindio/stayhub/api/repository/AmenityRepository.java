package edu.uniquindio.stayhub.api.repository;

import edu.uniquindio.stayhub.api.model.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Long> {

    List<Amenity> findByIsActiveTrueOrderByName();

    Optional<Amenity> findByName(String name);

    boolean existsByName(String name);

    List<Amenity> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);
}