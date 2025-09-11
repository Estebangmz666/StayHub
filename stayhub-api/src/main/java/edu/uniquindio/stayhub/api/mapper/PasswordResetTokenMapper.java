package edu.uniquindio.stayhub.api.mapper;

import edu.uniquindio.stayhub.api.dto.auth.PasswordResetTokenDTO;
import edu.uniquindio.stayhub.api.dto.auth.UserDTO;
import edu.uniquindio.stayhub.api.model.PasswordResetToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper interface for converting between {@link PasswordResetTokenDTO} and {@link PasswordResetToken} entities.
 * <p>
 * This mapper handles the conversion of password reset tokens and includes custom mapping logic
 * for the associated {@code User} object, which is a nested property.
 */
@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface PasswordResetTokenMapper {

    /**
     * Converts a {@link PasswordResetTokenDTO} to a {@link PasswordResetToken} entity.
     * <p>
     * The mapping for the {@code user} field is handled by a custom expression that
     * calls the {@code mapUserDto} default method.
     *
     * @param dto The DTO to convert.
     * @return The mapped {@link PasswordResetToken} entity.
     */
    @Mapping(target = "user", expression = "java(mapUserDto(dto.getUser()))")
    PasswordResetToken toEntity(PasswordResetTokenDTO dto);

    /**
     * Converts a {@link PasswordResetToken} entity to a {@link PasswordResetTokenDTO}.
     * <p>
     * The mapping for the nested {@code user} entity is handled by a custom expression that
     * calls the {@code mapUser} default method to map it to a {@link UserDTO}.
     *
     * @param entity The entity to convert.
     * @return The mapped {@link PasswordResetTokenDTO}.
     */
    @Mapping(target = "user", expression = "java(mapUser(entity.getUser()))")
    PasswordResetTokenDTO toDto(PasswordResetToken entity);

    /**
     * Maps a {@link edu.uniquindio.stayhub.api.model.User} entity to a {@link UserDTO}.
     * This default method provides explicit mapping for the nested user object.
     *
     * @param user The user entity.
     * @return The mapped user DTO, or {@code null} if the input is {@code null}.
     */
    default UserDTO mapUser(edu.uniquindio.stayhub.api.model.User user) {
        if (user == null) return null;
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        return userDTO;
    }

    /**
     * Maps a {@link UserDTO} to a {@link edu.uniquindio.stayhub.api.model.User} entity.
     * This default method provides explicit mapping for the user object within the DTO.
     *
     * @param userDTO The user DTO.
     * @return The mapped user entity, or {@code null} if the input is {@code null}.
     */
    default edu.uniquindio.stayhub.api.model.User mapUserDto(UserDTO userDTO) {
        if (userDTO == null) return null;
        edu.uniquindio.stayhub.api.model.User user = new edu.uniquindio.stayhub.api.model.User();
        user.setId(userDTO.getId());
        user.setEmail(userDTO.getEmail());
        return user;
    }
}