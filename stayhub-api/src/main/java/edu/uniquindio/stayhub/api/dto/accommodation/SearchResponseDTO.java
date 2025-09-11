package edu.uniquindio.stayhub.api.dto.accommodation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Data Transfer Object for search results.
 * It provides a paginated response for accommodation search queries, including the list of accommodations
 * and pagination metadata.
 */
@Getter
@Setter
@Schema(description = "Data Transfer Object for returning paginated search results for accommodations")
public class SearchResponseDTO {
    /**
     * A list of {@link AccommodationResponseDTO} representing the search results on the current page.
     */
    @Schema(description = "A list of accommodations on the current page")
    private List<AccommodationResponseDTO> content;
    /**
     * The current page number (0-indexed).
     */
    @Schema(description = "The current page number (0-indexed)", example = "0")
    private int page;
    /**
     * The number of elements on the current page.
     */
    @Schema(description = "The number of elements on the current page", example = "10")
    private int size;
    /**
     * The total number of elements across all pages.
     */
    @Schema(description = "The total number of elements across all pages", example = "50")
    private long totalElements;
}