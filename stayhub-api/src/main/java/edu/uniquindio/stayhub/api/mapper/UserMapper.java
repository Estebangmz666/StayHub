package edu.uniquindio.stayhub.api.mapper;

import edu.uniquindio.stayhub.api.dto.user.UpdateProfileDTO;
import edu.uniquindio.stayhub.api.dto.user.UserRegistrationDTO;
import edu.uniquindio.stayhub.api.dto.user.UserResponseDTO;
import edu.uniquindio.stayhub.api.model.HostProfile;
import edu.uniquindio.stayhub.api.model.Role;
import edu.uniquindio.stayhub.api.model.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper (unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "role", source = "role")
    UserRegistrationDTO toRegistrationDto(User user);

    @Mapping(target = "role", source = "role")
    UserResponseDTO toResponseDto(User user);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "birthDate", source = "birthDate")
    @Mapping(target = "profilePicture", source = "profilePicture")
    User toEntity(UpdateProfileDTO dto);

    @AfterMapping
    default void mapHostProfile(@MappingTarget User user, UpdateProfileDTO dto) {
        if (dto.getDescription() != null && user.getRole() == Role.HOST) {
            if (user.getHostProfile() == null) {
                user.setHostProfile(new HostProfile());
                user.getHostProfile().setUser(user);
            }
            user.getHostProfile().setDescription(dto.getDescription());
        }
    }
}