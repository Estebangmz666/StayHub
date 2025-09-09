package edu.uniquindio.stayhub.api.mapper;

import edu.uniquindio.stayhub.api.dto.reservation.ReservationRequestDTO;
import edu.uniquindio.stayhub.api.dto.reservation.ReservationResponseDTO;
import edu.uniquindio.stayhub.api.dto.reservation.ReservationUpdateDTO;
import edu.uniquindio.stayhub.api.model.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReservationMapper {
    ReservationMapper INSTANCE = Mappers.getMapper(ReservationMapper.class);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "accommodation", ignore = true)
    Reservation toEntity(ReservationRequestDTO dto);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "accommodation", ignore = true)
    Reservation toEntity(ReservationUpdateDTO dto);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "accommodation.id", target = "accommodationId")
    @Mapping(source = "checkInDate", target = "checkInDate")
    @Mapping(source = "checkOutDate", target = "checkOutDate")
    @Mapping(source = "numberOfGuests", target = "numberOfGuests")
    @Mapping(source = "status", target = "status")
    ReservationResponseDTO toResponseDto(Reservation reservation);
}