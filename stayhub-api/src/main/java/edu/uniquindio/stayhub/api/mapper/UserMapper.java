package edu.uniquindio.stayhub.api.mapper;

import edu.uniquindio.stayhub.api.dto.user.UpdateProfileDTO;
import edu.uniquindio.stayhub.api.dto.user.UserRegistrationDTO;
import edu.uniquindio.stayhub.api.dto.user.UserResponseDTO;
import edu.uniquindio.stayhub.api.model.HostProfile;
import edu.uniquindio.stayhub.api.model.Role;
import edu.uniquindio.stayhub.api.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

/**
 * Mapper interface for converting between {@link User} DTOs and entities.
 * This interface uses MapStruct to automatically generate the implementation
 * for various user-related data transfer operations.
 */
@Mapper(componentModel = "spring", unmappedSourcePolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface UserMapper {

    /**
     * Converts a {@link User} entity to a {@link UserRegistrationDTO}.
     *
     * @param user The entity to convert.
     * @return The mapped {@link UserRegistrationDTO}.
     */
    @Mapping(target = "role", source = "role")
    UserRegistrationDTO toRegistrationDto(User user);

    /**
     * Converts a {@link User} entity to a {@link UserResponseDTO}.
     * This mapping flattens fields from the nested {@code HostProfile} object,
     * such as description and legal documents, directly into the response DTO.
     *
     * @param user The entity to convert.
     * @return The mapped {@link UserResponseDTO}.
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "birthDate", source = "birthDate")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "profilePicture", source = "profilePicture")
    @Mapping(target = "description", source = "hostProfile.description")
    @Mapping(target = "legalDocuments", source = "hostProfile.legalDocuments")
    UserResponseDTO toResponseDto(User user);

    /**
     * Converts an {@link UpdateProfileDTO} to a {@link User} entity.
     * This method is used for updating user profiles.
     *
     * @param dto The DTO containing the update data.
     * @return The mapped {@link User} entity.
     */
    @Mapping(target = "name", source = "name")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "birthDate", source = "birthDate")
    @Mapping(target = "profilePicture", source = "profilePicture")
    @Mapping(target = "deleted", ignore = true)
    User toEntity(UpdateProfileDTO dto);

    /**
     * Converts a {@link UserRegistrationDTO} to a {@link User} entity.
     *
     * @param dto The DTO to convert.
     * @return The mapped {@link User} entity.
     */
    @Mapping(target = "email", source = "email")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "birthDate", source = "birthDate")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "deleted", ignore = true)
    User toEntity(UserRegistrationDTO dto);

    /**
     * A named method that conditionally sets or un-sets the {@code HostProfile}
     * on a {@link User} entity based on their role. This is called from other mappers.
     * <p>
     * If the user's role is {@link Role#HOST} and they don't have a profile, a new
     * {@code HostProfile} is created and linked. If the role is {@link Role#GUEST},
     * the profile is set to {@code null}.
     *
     * @param user The target {@link User} entity to modify.
     */
    @Named("setHostProfile")
    default void setHostProfile(@MappingTarget User user) {
        if (user.getRole() == Role.HOST && user.getHostProfile() == null) {
            user.setHostProfile(new HostProfile());
            user.getHostProfile().setUser(user);
        } else if (user.getRole() == Role.GUEST) {
            user.setHostProfile(null);
        }
    }
}