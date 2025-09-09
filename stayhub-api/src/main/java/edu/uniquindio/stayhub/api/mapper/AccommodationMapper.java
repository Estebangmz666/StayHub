package edu.uniquindio.stayhub.api.mapper;

import edu.uniquindio.stayhub.api.dto.accommodation.AccommodationRequestDTO;
import edu.uniquindio.stayhub.api.dto.accommodation.AccommodationResponseDTO;
import edu.uniquindio.stayhub.api.dto.accommodation.AccommodationUpdateDTO;
import edu.uniquindio.stayhub.api.model.Accommodation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccommodationMapper {
    AccommodationMapper INSTANCE = Mappers.getMapper(AccommodationMapper.class);

    @Mapping(target = "host", ignore = true) // Handled by service layer
    Accommodation toEntity(AccommodationRequestDTO dto);

    @Mapping(target = "host", ignore = true)
    Accommodation toEntity(AccommodationUpdateDTO dto);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "capacity", target = "capacity")
    @Mapping(source = "mainImage", target = "mainImage")
    @Mapping(source = "longitude", target = "longitude")
    @Mapping(source = "latitude", target = "latitude")
    @Mapping(source = "locationDescription", target = "locationDescription")
    @Mapping(source = "city", target = "city")
    @Mapping(source = "pricePerNight", target = "pricePerNight")
    @Mapping(source = "images", target = "images")
    AccommodationResponseDTO toResponseDto(Accommodation accommodation);
}