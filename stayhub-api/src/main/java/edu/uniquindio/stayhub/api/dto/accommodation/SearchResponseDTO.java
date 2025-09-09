package edu.uniquindio.stayhub.api.dto.accommodation;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class SearchResponseDTO {
    private List<AccommodationResponseDTO> content;
    private int page;
    private int size;
    private long totalElements;
}