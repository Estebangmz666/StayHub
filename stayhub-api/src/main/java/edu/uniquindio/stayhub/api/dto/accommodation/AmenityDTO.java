package edu.uniquindio.stayhub.api.dto.accommodation;

import lombok.Setter;
import lombok.Getter;

@Getter @Setter
public class AmenityDTO {
    private Long id;
    private String name;
    private boolean isActive;
}