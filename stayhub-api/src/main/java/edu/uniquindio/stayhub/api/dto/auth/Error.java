package edu.uniquindio.stayhub.api.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @Getter @Setter @NoArgsConstructor
public class Error {
    private String message;
    private int code;
}