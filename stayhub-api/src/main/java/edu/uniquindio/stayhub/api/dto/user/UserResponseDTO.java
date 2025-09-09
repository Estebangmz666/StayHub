package edu.uniquindio.stayhub.api.dto.user;

import edu.uniquindio.stayhub.api.model.Role;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class UserResponseDTO {
    private Long id;
    private String email;
    private String name;
    private String phoneNumber;
    private LocalDate birthDate;
    private Role role;
    private String profilePicture;
}