package edu.uniquindio.stayhub.api.dao;

import edu.uniquindio.stayhub.api.model.Amenity;
import edu.uniquindio.stayhub.api.repository.AmenityRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class AmenityDAO {
    private final AmenityRepository amenityRepository;

    public AmenityDAO(AmenityRepository amenityRepository) {
        this.amenityRepository = amenityRepository;
    }

    public Amenity saveAmenity(Amenity amenity) {
        if (amenityRepository.existsByName(amenity.getName())) {
            throw new IllegalStateException("Amenity name already exists");
        }
        return amenityRepository.save(amenity);
    }

    public List<Amenity> findActiveAmenities() {
        return amenityRepository.findByIsActiveTrueOrderByName();
    }

    public Optional<Amenity> findActiveByName(String name) {
        return amenityRepository.findByName(name).filter(Amenity::isActive);
    }
}