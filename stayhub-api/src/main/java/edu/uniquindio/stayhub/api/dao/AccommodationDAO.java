package edu.uniquindio.stayhub.api.dao;

import edu.uniquindio.stayhub.api.model.Accommodation;
import edu.uniquindio.stayhub.api.repository.AccommodationRepository;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object (DAO) for the Accommodation entity.
 * This component handles direct interaction with the database for Accommodation entities.
 * It provides methods for saving, retrieving, and querying accommodation data.
 */
@Component
public class AccommodationDAO {

    private final AccommodationRepository accommodationRepository;

    /**
     * Constructs a new AccommodationDAO with the provided repository.
     * @param accommodationRepository The repository for managing Accommodation entities.
     */
    public AccommodationDAO(AccommodationRepository accommodationRepository) {
        this.accommodationRepository = accommodationRepository;
    }

    /**
     * Saves accommodation to the database.
     * @param accommodation The accommodation entity to save.
     * @return The saved accommodation entity.
     */
    public Accommodation saveAccommodation(Accommodation accommodation) {
        return accommodationRepository.save(accommodation);
    }

    /**
     * Finds a list of active accommodations associated with a specific host.
     * Accommodation is considered active if its 'deleted' flag is false.
     * @param hostId The ID of the host.
     * @return A list of active accommodations.
     */
    public List<Accommodation> findActiveByHostId(Long hostId) {
        return accommodationRepository.findByHostIdAndDeletedFalse(hostId);
    }

    /**
     * Finds single active accommodation by its ID.
     * @param id The ID of the accommodation.
     * @return An Optional containing the accommodation if found and not deleted, otherwise an empty Optional.
     */
    public Optional<Accommodation> findActiveById(Long id) {
        return accommodationRepository.findById(id).filter(a -> !a.isDeleted());
    }
}