package edu.uniquindio.stayhub.api.mapper;

import edu.uniquindio.stayhub.api.dto.auth.PasswordResetTokenDTO;
import edu.uniquindio.stayhub.api.dto.auth.UserDTO;
import edu.uniquindio.stayhub.api.model.PasswordResetToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface PasswordResetTokenMapper {
    PasswordResetTokenMapper INSTANCE = Mappers.getMapper(PasswordResetTokenMapper.class);

    @Mapping(target = "user", expression = "java(mapUserDto(dto.getUser()))")
    PasswordResetToken toEntity(PasswordResetTokenDTO dto);

    @Mapping(target = "user", expression = "java(mapUser(entity.getUser()))")
    PasswordResetTokenDTO toDto(PasswordResetToken entity);

    default UserDTO mapUser(edu.uniquindio.stayhub.api.model.User user) {
        if (user == null) return null;
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        return userDTO;
    }

    default edu.uniquindio.stayhub.api.model.User mapUserDto(UserDTO userDTO) {
        if (userDTO == null) return null;
        edu.uniquindio.stayhub.api.model.User user = new edu.uniquindio.stayhub.api.model.User();
        user.setId(userDTO.getId());
        user.setEmail(userDTO.getEmail());
        return user;
    }
}