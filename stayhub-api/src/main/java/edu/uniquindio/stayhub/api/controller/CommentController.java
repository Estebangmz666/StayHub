package edu.uniquindio.stayhub.api.controller;

import edu.uniquindio.stayhub.api.dto.comment.CommentReplyDTO;
import edu.uniquindio.stayhub.api.dto.comment.CommentRequestDTO;
import edu.uniquindio.stayhub.api.dto.comment.CommentResponseDTO;
import edu.uniquindio.stayhub.api.dto.comment.CommentUpdateDTO;
import edu.uniquindio.stayhub.api.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for managing comments/reviews in the StayHub application.
 * Provides endpoints for creating, retrieving, updating, and deleting comments,
 * as well as calculating average ratings and comment counts for accommodations.
 */
@RestController
@RequestMapping("/api/v1/comments")
@Tag(name = "Comment Management", description = "Operations related to accommodation reviews")
@SecurityRequirement(name = "bearerAuth")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @Operation(summary = "Create a new comment", description = "Allows an authenticated guest to create a comment/review for an accommodation")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Comment created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or user already commented"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User or accommodation not found")
    })
    @PostMapping
    public ResponseEntity<CommentResponseDTO> createComment(
            @Valid @RequestBody CommentRequestDTO commentRequestDTO,
            Authentication authentication) {
        CommentResponseDTO createdComment = commentService.createComment(commentRequestDTO, authentication.getName());
        return ResponseEntity.status(201).body(createdComment);
    }

    @Operation(summary = "Get comments by accommodation", description = "Retrieves all non-deleted comments for a specific accommodation, ordered by creation date")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comments retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Accommodation not found")
    })
    @GetMapping("/accommodation/{accommodationId}")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByAccommodation(
            @Parameter(description = "ID of the accommodation") @PathVariable Long accommodationId) {
        List<CommentResponseDTO> comments = commentService.getCommentsByAccommodation(accommodationId);
        return ResponseEntity.ok(comments);
    }

    @Operation(summary = "Get paginated comments by accommodation", description = "Retrieves paginated non-deleted comments for a specific accommodation, ordered by creation date")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comments retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Accommodation not found")
    })
    @GetMapping("/accommodation/{accommodationId}/paged")
    public ResponseEntity<Page<CommentResponseDTO>> getCommentsByAccommodationPaginated(
            @Parameter(description = "ID of the accommodation") @PathVariable Long accommodationId,
            @Parameter(description = "Pagination parameters (page, size, sort)") Pageable pageable) {
        Page<CommentResponseDTO> comments = commentService.getCommentsByAccommodationPaginated(accommodationId, pageable);
        return ResponseEntity.ok(comments);
    }

    @Operation(summary = "Get comments by user", description = "Retrieves all non-deleted comments made by a specific user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comments retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByUser(
            @Parameter(description = "ID of the user") @PathVariable Long userId,
            Authentication authentication) {
        List<CommentResponseDTO> comments = commentService.getCommentsByUser(userId, authentication.getName());
        return ResponseEntity.ok(comments);
    }

    @Operation(summary = "Get average rating for an accommodation", description = "Calculates the average rating of non-deleted comments for a specific accommodation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Average rating retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Accommodation not found")
    })
    @GetMapping("/accommodation/{accommodationId}/average-rating")
    public ResponseEntity<Double> getAverageRatingByAccommodation(
            @Parameter(description = "ID of the accommodation") @PathVariable Long accommodationId) {
        Double averageRating = commentService.getAverageRatingByAccommodation(accommodationId);
        return ResponseEntity.ok(averageRating);
    }

    @Operation(summary = "Get comment count for an accommodation", description = "Counts the number of non-deleted comments for a specific accommodation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comment count retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Accommodation not found")
    })
    @GetMapping("/accommodation/{accommodationId}/count")
    public ResponseEntity<Long> getCommentCountByAccommodation(
            @Parameter(description = "ID of the accommodation") @PathVariable Long accommodationId) {
        long count = commentService.getCommentCountByAccommodation(accommodationId);
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "Update a comment", description = "Allows an authenticated guest to update their own comment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comment updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CommentResponseDTO> updateComment(
            @Parameter(description = "ID of the comment to update") @PathVariable Long id,
            @Valid @RequestBody CommentUpdateDTO commentUpdateDTO,
            Authentication authentication) {
        CommentResponseDTO updatedComment = commentService.updateComment(id, commentUpdateDTO, authentication.getName());
        return ResponseEntity.ok(updatedComment);
    }

    @Operation(summary = "Delete a comment", description = "Allows an authenticated guest to delete their own comment (soft delete)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Comment deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "ID of the comment to delete") @PathVariable Long id,
            Authentication authentication) {
        commentService.deleteComment(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reply to a comment", description = "Allows an authenticated host to reply to a guest's comment on their accommodation")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reply created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "User is not the host of the accommodation"),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    @PostMapping("/{id}/reply")
    public ResponseEntity<CommentResponseDTO> replyToComment(
            @Parameter(description = "ID of the guest's comment") @PathVariable Long id,
            @Valid @RequestBody CommentReplyDTO replyDTO,
            Authentication authentication) {

        CommentResponseDTO commented = commentService.replyToComment(id, replyDTO, authentication.getName());
        return ResponseEntity.status(201).body(commented);
    }

    @Operation(summary = "Ping endpoint for health checks", description = "Returns PONG if the controller is alive")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Controller is alive", content = @Content(mediaType = "text/plain"))
    })
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("PONG");
    }
}