package edu.uniquindio.stayhub.api.mapper;

import edu.uniquindio.stayhub.api.dto.reservation.ReservationRequestDTO;
import edu.uniquindio.stayhub.api.dto.reservation.ReservationResponseDTO;
import edu.uniquindio.stayhub.api.dto.reservation.ReservationUpdateDTO;
import edu.uniquindio.stayhub.api.model.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper interface for converting between {@link Reservation} DTOs and entities.
 * This interface uses MapStruct to automatically generate the implementation.
 */
@Mapper(componentModel = "spring")
public interface ReservationMapper {

    /**
     * Converts a {@link ReservationRequestDTO} to a new {@link Reservation} entity.
     * <p>
     * The following fields are ignored as they are managed by the system and should
     * not be set directly from the DTO:
     * <ul>
     * <li>{@code id} (auto-generated)</li>
     * <li>{@code guest} and {@code accommodation} (populated by a service layer)</li>
     * <li>{@code totalPrice} (calculated)</li>
     * <li>{@code status} (a service sets initial status)</li>
     * <li>Auditing fields like {@code deleted}, {@code createdAt}, and {@code updatedAt}.</li>
     * </ul>
     * The {@code guestId} from the DTO is used to set the ID of the nested {@code guest} object.
     *
     * @param dto The DTO to convert.
     * @return A new {@link Reservation} entity.
     */
    @Mapping(target = "guest", ignore = true)
    @Mapping(target = "accommodation", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "status", ignore = true)
    Reservation toEntity(ReservationRequestDTO dto);

    default ReservationResponseDTO toResponseDTO(Reservation reservation) {
        if (reservation == null) return null;

        ReservationResponseDTO dto = new ReservationResponseDTO();
        dto.setId(reservation.getId());
        dto.setGuestId(reservation.getGuest().getId());
        dto.setAccommodationId(reservation.getAccommodation().getId());
        dto.setCheckInDate(reservation.getCheckInDate());
        dto.setCheckOutDate(reservation.getCheckOutDate());
        dto.setNumberOfGuests(reservation.getNumberOfGuests());
        dto.setTotalPrice(reservation.getTotalPrice());
        dto.setStatus(reservation.getStatus());
        dto.setCreatedAt(reservation.getCreatedAt());
        dto.setUpdatedAt(reservation.getUpdatedAt());
        if (reservation.getAccommodation() != null) {
            dto.setAccommodationTitle(reservation.getAccommodation().getTitle());
        }
        return dto;
    }

    /**
     * Updates an existing {@link Reservation} entity from a {@link ReservationUpdateDTO}.
     * <p>
     * This method uses {@code @MappingTarget} to apply changes to an existing instance
     * rather than creating a new one. All non-updatable fields and auditing fields are ignored.
     *
     * @param dto The DTO containing the update data.
     * @param reservation The target entity to update.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "guest", ignore = true)
    @Mapping(target = "accommodation", ignore = true)
    @Mapping(target = "checkInDate", ignore = true)
    @Mapping(target = "checkOutDate", ignore = true)
    @Mapping(target = "numberOfGuests", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(ReservationUpdateDTO dto, @MappingTarget Reservation reservation);
}