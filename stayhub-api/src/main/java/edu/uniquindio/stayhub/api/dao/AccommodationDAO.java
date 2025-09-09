package edu.uniquindio.stayhub.api.dao;

import edu.uniquindio.stayhub.api.model.Accommodation;
import edu.uniquindio.stayhub.api.repository.AccommodationRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class AccomodationDAO {
    private final AccommodationRepository accommodationRepository;

    public AccommodationDAO(AccommodationRepository accommodationRepository) {
        this.accommodationRepository = accommodationRepository;
    }

    public Accommodation saveAccommodation(Accommodation accommodation) {
        if (accommodation.getCapacity() <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero");
        }
        return accommodationRepository.save(accommodation);
    }

    public List<Accommodation> findActiveByHostId(Long hostId) {
        return accommodationRepository.findByHostIdAndIsDeletedFalse(hostId);
    }

    public Optional<Accommodation> findActiveById(Long id) {
        return accommodationRepository.findById(id).filter(a -> !a.isDeleted());
    }
}