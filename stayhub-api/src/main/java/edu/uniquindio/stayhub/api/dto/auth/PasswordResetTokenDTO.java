package edu.uniquindio.stayhub.api.dto.auth;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PasswordResetTokenDTO {
    private Long id;
    private String token;
    private UserDTO user;
    private LocalDateTime expiryDate;
    private boolean used;
}