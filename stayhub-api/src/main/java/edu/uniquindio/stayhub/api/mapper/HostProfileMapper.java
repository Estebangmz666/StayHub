package edu.uniquindio.stayhub.api.mapper;

import edu.uniquindio.stayhub.api.dto.host.HostProfileDTO;
import edu.uniquindio.stayhub.api.model.HostProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting between {@link HostProfileDTO} and {@link HostProfile} entities.
 * This interface uses MapStruct to automatically generate the implementation.
 */
@Mapper
public interface HostProfileMapper {

    /**
     * Converts a {@link HostProfileDTO} to a new {@link HostProfile} entity.
     * <p>
     * The following fields are ignored as they are managed by the system
     * and should not be set directly from the DTO:
     * <ul>
     * <li>{@code user} (assigned in a service layer)</li>
     * <li>{@code id} (auto-generated)</li>
     * <li>{@code legalDocuments} (handled separately)</li>
     * </ul>
     *
     * @param dto The DTO to convert.
     * @return A new {@link HostProfile} entity.
     */
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "legalDocuments", ignore = true)
    HostProfile toEntity(HostProfileDTO dto);

    /**
     * Converts a {@link HostProfile} entity to a {@link HostProfileDTO}.
     *
     * @param hostProfile The entity to convert.
     * @return A new {@link HostProfileDTO}.
     */
    HostProfileDTO toDto(HostProfile hostProfile);
}