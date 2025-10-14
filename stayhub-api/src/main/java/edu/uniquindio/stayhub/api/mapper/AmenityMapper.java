
package edu.uniquindio.stayhub.api.mapper;

import edu.uniquindio.stayhub.api.dto.amenity.AmenityRequestDTO;
import edu.uniquindio.stayhub.api.dto.amenity.AmenityResponseDTO;
import edu.uniquindio.stayhub.api.model.Amenity;
import org.mapstruct.Mapper;

/**
 * Mapper interface to map between various amenities DTO into {@link Amenity} entities, and vice versa.
 */
@Mapper(componentModel = "spring")
public interface AmenityMapper {

    /**
     * Converts an {@link AmenityRequestDTO} to an {@link Amenity} entity.
     * @param dto The DTO to convert.
     * @return A new {@link Amenity} entity.
     */
    Amenity requestToEntity(AmenityRequestDTO dto);

    /**
     * Converts an {@link Amenity} entity to an {@link AmenityResponseDTO}.
     * @param amenity The entity to convert.
     * @return A new {@link AmenityResponseDTO} with the mapped data.
     */
    AmenityResponseDTO toResponseDTO(Amenity amenity);
}