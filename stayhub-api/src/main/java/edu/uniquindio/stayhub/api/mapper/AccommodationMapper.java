package edu.uniquindio.stayhub.api.mapper;

import edu.uniquindio.stayhub.api.dto.accommodation.AccommodationRequestDTO;
import edu.uniquindio.stayhub.api.dto.accommodation.AccommodationResponseDTO;
import edu.uniquindio.stayhub.api.dto.accommodation.AccommodationUpdateDTO;
import edu.uniquindio.stayhub.api.model.Accommodation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper interface for converting between Accommodation DTOs and entities.
 * This interface uses MapStruct to automatically generate the implementation.
 */
@Mapper(componentModel = "spring")
public interface AccommodationMapper {

    /**
     * Converts an {@link AccommodationRequestDTO} to an {@link Accommodation} entity.
     * <p>
     * The following fields are ignored and will not be mapped automatically:
     * <ul>
     * <li>{@code id}</li>
     * <li>{@code host}</li>
     * <li>{@code amenities}</li>
     * <li>{@code reservations}</li>
     * <li>{@code comments}</li>
     * <li>{@code deleted}</li>
     * <li>{@code createdAt}</li>
     * <li>{@code updatedAt}</li>
     * </ul>
     *
     * @param dto The DTO containing the data to convert.
     * @return A new {@link Accommodation} entity with the mapped data.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "host", ignore = true)
    @Mapping(target = "amenities", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Accommodation toEntity(AccommodationRequestDTO dto);

    /**
     * Converts an {@link Accommodation} entity to an {@link AccommodationResponseDTO}.
     *
     * @param accommodation The entity containing the data to convert.
     * @return A new {@link AccommodationResponseDTO} with the mapped data.
     */
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
    AccommodationResponseDTO toResponseDTO(Accommodation accommodation);

    /**
     * Updates an existing {@link Accommodation} entity with data from an {@link AccommodationUpdateDTO}.
     * <p>
     * The following fields are ignored and will not be updated automatically:
     * <ul>
     * <li>{@code id}</li>
     * <li>{@code host}</li>
     * <li>{@code amenities}</li>
     * <li>{@code reservations}</li>
     * <li>{@code comments}</li>
     * <li>{@code deleted}</li>
     * <li>{@code createdAt}</li>
     * <li>{@code updatedAt}</li>
     * </ul>
     *
     * @param dto The DTO containing the updated data.
     * @param accommodation The target entity to update.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "host", ignore = true)
    @Mapping(target = "amenities", ignore = true)
    @Mapping(target = "reservations", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(AccommodationUpdateDTO dto, @MappingTarget Accommodation accommodation);
}