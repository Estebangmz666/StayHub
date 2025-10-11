package edu.uniquindio.stayhub.api.mapper;

import edu.uniquindio.stayhub.api.dto.user.UpdateProfileDTO;
import edu.uniquindio.stayhub.api.dto.user.UserRegistrationDTO;
import edu.uniquindio.stayhub.api.dto.user.UserResponseDTO;
import edu.uniquindio.stayhub.api.model.HostProfile;
import edu.uniquindio.stayhub.api.model.Role;
import edu.uniquindio.stayhub.api.model.User;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", unmappedSourcePolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "role", source = "role")
    UserRegistrationDTO toRegistrationDto(User user);

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

    @Mapping(target = "name", source = "name")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "birthDate", source = "birthDate")
    @Mapping(target = "profilePicture", source = "profilePicture")
    @Mapping(target = "deleted", ignore = true)
    User toEntity(UpdateProfileDTO dto);

    @Mapping(target = "email", source = "email")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "birthDate", source = "birthDate")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "deleted", ignore = true)
    User toEntity(UserRegistrationDTO dto);

    /**
     * Updates an existing User entity with data from UpdateProfileDTO.
     * Handles HostProfile updates for users with the HOST role.
     *
     * @param dto The DTO containing the update data.
     * @param user The existing User entity to update.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "hostProfile", ignore = true)
    void updateUser(@Valid UpdateProfileDTO dto, @MappingTarget User user);

    @Named("setHostProfile")
    default void setHostProfile(@MappingTarget User user) {
        if (user.getRole() == Role.HOST && user.getHostProfile() == null) {
            user.setHostProfile(new HostProfile());
            user.getHostProfile().setUser(user);
        } else if (user.getRole() == Role.GUEST) {
            user.setHostProfile(null);
        }
    }

    /**
     * Updates HostProfile fields from UpdateProfileDTO for HOST users.
     */
    @Named("updateHostProfile")
    default void updateHostProfile(UpdateProfileDTO dto, @MappingTarget User user) {
        if (user.getRole() == Role.HOST) {
            HostProfile profile = user.getHostProfile();
            if (profile == null) {
                profile = new HostProfile();
                profile.setUser(user);
                user.setHostProfile(profile);
            }
            if (dto.getDescription() != null) {
                profile.setDescription(dto.getDescription());
            }
            if (dto.getLegalDocuments() != null) {
                profile.setLegalDocuments(dto.getLegalDocuments());
            }
        }
    }
}