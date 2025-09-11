package edu.uniquindio.stayhub.api.mapper;

import edu.uniquindio.stayhub.api.dto.accommodation.AmenityDTO;
import edu.uniquindio.stayhub.api.model.Amenity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for converting between {@link AmenityDTO} and {@link Amenity} entities.
 * This interface uses MapStruct to automatically generate the implementation.
 */
@Mapper
public interface AmenityMapper {
    AmenityMapper INSTANCE = Mappers.getMapper(AmenityMapper.class);

    /**
     * Converts an {@link AmenityDTO} to an {@link Amenity} entity.
     *
     * @param dto The DTO to convert.
     * @return A new {@link Amenity} entity.
     */
    Amenity toEntity(AmenityDTO dto);

    /**
     * Converts an {@link Amenity} entity to an {@link AmenityDTO}.
     *
     * @param amenity The entity to convert.
     * @return A new {@link AmenityDTO}.
     */
    AmenityDTO toDto(Amenity amenity);
}