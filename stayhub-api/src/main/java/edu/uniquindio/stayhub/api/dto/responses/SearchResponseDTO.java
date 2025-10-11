package edu.uniquindio.stayhub.api.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Data Transfer Object for paginated search results.
 * This DTO encapsulates a list of items along with pagination metadata.
 *
 * @param <T> The type of the items in the content list.
 */
@Getter
@AllArgsConstructor
@Schema(description = "DTO for paginated search results, including content and pagination metadata")
public class SearchResponseDTO<T> {

    /**
     * The list of items returned in the current page.
     */
    @Schema(description = "The list of items returned in the current page", example = "[{\"id\": 1, \"guestId\": 10, \"accommodationId\": 25, \"checkInDate\": \"2025-11-20T15:00:00\", \"checkOutDate\": \"2025-11-25T11:00:00\", \"numberOfGuests\": 2, \"totalPrice\": 250.00, \"status\": \"PENDING\", \"createdAt\": \"2025-11-15T10:00:00\", \"updatedAt\": \"2025-11-15T10:00:00\"}]")
    private List<T> content;

    /**
     * The current page number (0-indexed).
     */
    @Schema(description = "The current page number (0-indexed)", example = "0")
    private int page;

    /**
     * The number of items per page.
     */
    @Schema(description = "The number of items per page", example = "10")
    private int size;

    /**
     * The total number of elements across all pages.
     */
    @Schema(description = "The total number of elements across all pages", example = "50")
    private long totalElements;
}