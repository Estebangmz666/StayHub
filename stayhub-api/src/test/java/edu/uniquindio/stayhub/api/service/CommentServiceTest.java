package edu.uniquindio.stayhub.api.service;

import edu.uniquindio.stayhub.api.dto.comment.CommentReplyDTO;
import edu.uniquindio.stayhub.api.dto.comment.CommentRequestDTO;
import edu.uniquindio.stayhub.api.dto.comment.CommentResponseDTO;
import edu.uniquindio.stayhub.api.dto.comment.CommentUpdateDTO;
import edu.uniquindio.stayhub.api.exception.CommentNotFoundException;
import edu.uniquindio.stayhub.api.exception.UnauthorizedCommentAccessException;
import edu.uniquindio.stayhub.api.mapper.CommentMapper;
import edu.uniquindio.stayhub.api.model.Accommodation;
import edu.uniquindio.stayhub.api.model.Comment;
import edu.uniquindio.stayhub.api.model.Role;
import edu.uniquindio.stayhub.api.model.User;
import edu.uniquindio.stayhub.api.repository.AccommodationRepository;
import edu.uniquindio.stayhub.api.repository.CommentRepository;
import edu.uniquindio.stayhub.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock private CommentRepository commentRepository;
    @Mock private UserRepository userRepository;
    @Mock private AccommodationRepository accommodationRepository;
    @Mock private CommentMapper commentMapper;

    @InjectMocks private CommentService commentService;

    private User guestUser;
    private User hostUser;
    private User otherUser;
    private Accommodation accommodation;
    private Comment comment;
    private CommentRequestDTO requestDTO;
    private CommentResponseDTO responseDTO;

    private final Long guestId = 1L;
    private final Long accommodationId = 10L;
    private final Long commentId = 20L;
    private final String guestEmail = "guest@test.com";
    private final String hostEmail = "host@test.com";

    @BeforeEach
    void setup() {
        // 1. Usuarios
        guestUser = new User();
        guestUser.setId(guestId);
        guestUser.setEmail(guestEmail);
        guestUser.setRole(Role.GUEST);

        hostUser = new User();
        Long hostId = 2L;
        hostUser.setId(hostId);
        hostUser.setEmail(hostEmail);
        hostUser.setRole(Role.HOST);

        otherUser = new User();
        otherUser.setId(3L);
        otherUser.setEmail("other@test.com");
        otherUser.setRole(Role.GUEST);

        // 2. Alojamiento
        accommodation = new Accommodation();
        accommodation.setId(accommodationId);
        accommodation.setHost(hostUser);

        // 3. Comentario
        comment = new Comment();
        comment.setId(commentId);
        comment.setUser(guestUser);
        comment.setAccommodation(accommodation);
        comment.setText("Great stay");
        comment.setRating(5);
        comment.setDeleted(false);
        comment.setHostReplyText(null);

        // 4. DTOs
        requestDTO = new CommentRequestDTO(guestId, accommodationId, "Nice place", 4);
        responseDTO = new CommentResponseDTO(commentId, guestId, accommodationId, "Nice place", 4, LocalDateTime.now());
    }

    // ----------------------------------------------------------------------
    // Tests para createComment
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Should create comment successfully when user is authorized and has not commented before")
    void createComment_Success() {
        // Arrange
        when(userRepository.findByEmail(guestEmail)).thenReturn(Optional.of(guestUser));
        when(accommodationRepository.findById(accommodationId)).thenReturn(Optional.of(accommodation));
        when(commentRepository.existsByUserIdAndAccommodationIdAndDeletedFalse(guestId, accommodationId)).thenReturn(false);
        when(commentMapper.toEntity(requestDTO)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toResponseDto(comment)).thenReturn(responseDTO);

        // Act
        CommentResponseDTO result = commentService.createComment(requestDTO, guestEmail);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getRating()).isEqualTo(4);
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    @DisplayName("Should throw UnauthorizedCommentAccessException when DTO userId does not match authenticated user")
    void createComment_UnauthorizedUser_ShouldThrowException() {
        // Arrange
        CommentRequestDTO unauthorizedDTO = new CommentRequestDTO(99L, accommodationId, "Test", 5);
        when(userRepository.findByEmail(guestEmail)).thenReturn(Optional.of(guestUser));

        // Act & Assert
        assertThatThrownBy(() -> commentService.createComment(unauthorizedDTO, guestEmail))
                .isInstanceOf(UnauthorizedCommentAccessException.class)
                .hasMessage("Usuario no autorizado para crear este comentario");
        verify(commentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when accommodation is not found")
    void createComment_AccommodationNotFound_ShouldThrowException() {
        // Arrange
        when(userRepository.findByEmail(guestEmail)).thenReturn(Optional.of(guestUser));
        when(accommodationRepository.findById(accommodationId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> commentService.createComment(requestDTO, guestEmail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Alojamiento no encontrado");
        verify(commentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when user has already commented")
    void createComment_AlreadyCommented_ShouldThrowException() {
        // Arrange
        when(userRepository.findByEmail(guestEmail)).thenReturn(Optional.of(guestUser));
        when(accommodationRepository.findById(accommodationId)).thenReturn(Optional.of(accommodation));
        when(commentRepository.existsByUserIdAndAccommodationIdAndDeletedFalse(guestId, accommodationId)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> commentService.createComment(requestDTO, guestEmail))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("El usuario ya ha comentado sobre este alojamiento");
        verify(commentRepository, never()).save(any());
    }

    // ----------------------------------------------------------------------
    // Tests para getCommentsByAccommodation y Paginated
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Should return a list of comments for a valid accommodation")
    void getCommentsByAccommodation_Success() {
        // Arrange
        List<Comment> comments = List.of(comment);
        when(accommodationRepository.findById(accommodationId)).thenReturn(Optional.of(accommodation));
        when(commentRepository.findByAccommodationIdAndDeletedFalseOrderByCreatedAtDesc(accommodationId)).thenReturn(comments);
        when(commentMapper.toResponseDto(comment)).thenReturn(responseDTO);

        // Act
        List<CommentResponseDTO> result = commentService.getCommentsByAccommodation(accommodationId);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(commentId);
    }

    @Test
    @DisplayName("Should return a page of comments for a valid accommodation (paginated)")
    void getCommentsByAccommodationPaginated_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> commentPage = new PageImpl<>(List.of(comment), pageable, 1);

        when(accommodationRepository.findById(accommodationId)).thenReturn(Optional.of(accommodation));
        when(commentRepository.findByAccommodationIdAndDeletedFalseOrderByCreatedAtDesc(accommodationId, pageable)).thenReturn(commentPage);
        when(commentMapper.toResponseDto(comment)).thenReturn(responseDTO);

        // Act
        Page<CommentResponseDTO> result = commentService.getCommentsByAccommodationPaginated(accommodationId, pageable);

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when accommodation is not found for comment listing")
    void getCommentsByAccommodation_AccommodationNotFound_ShouldThrowException() {
        // Arrange
        when(accommodationRepository.findById(accommodationId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> commentService.getCommentsByAccommodation(accommodationId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Alojamiento no encontrado");
        verify(commentRepository, never()).findByAccommodationIdAndDeletedFalseOrderByCreatedAtDesc(any());
    }

    // ----------------------------------------------------------------------
    // Tests para getCommentsByUser
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Should return list of comments for the authenticated user")
    void getCommentsByUser_Success() {
        // Arrange
        List<Comment> comments = List.of(comment);
        when(userRepository.findByEmail(guestEmail)).thenReturn(Optional.of(guestUser));
        when(commentRepository.findByUserIdAndDeletedFalse(guestId)).thenReturn(comments);
        when(commentMapper.toResponseDto(comment)).thenReturn(responseDTO);

        // Act
        List<CommentResponseDTO> result = commentService.getCommentsByUser(guestId, guestEmail);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getUserId()).isEqualTo(guestId);
    }

    @Test
    @DisplayName("Should throw UnauthorizedCommentAccessException when user tries to view another user's comments")
    void getCommentsByUser_UnauthorizedAccess_ShouldThrowException() {
        // Arrange
        when(userRepository.findByEmail(guestEmail)).thenReturn(Optional.of(guestUser));

        // Act & Assert
        assertThatThrownBy(() -> commentService.getCommentsByUser(otherUser.getId(), guestEmail))
                .isInstanceOf(UnauthorizedCommentAccessException.class)
                .hasMessage("Usuario no autorizado para ver estos comentarios");
        verify(commentRepository, never()).findByUserIdAndDeletedFalse(any());
    }

    // ----------------------------------------------------------------------
    // Tests para getAverageRatingByAccommodation y getCommentCountByAccommodation
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Should return the correct average rating")
    void getAverageRatingByAccommodation_Success() {
        // Arrange
        Double expectedAverage = 4.5;
        when(accommodationRepository.findById(accommodationId)).thenReturn(Optional.of(accommodation));
        when(commentRepository.findAverageRatingByAccommodationId(accommodationId)).thenReturn(expectedAverage);

        // Act
        Double result = commentService.getAverageRatingByAccommodation(accommodationId);

        // Assert
        assertThat(result).isEqualTo(expectedAverage);
    }

    @Test
    @DisplayName("Should return 0.0 when there are no comments")
    void getAverageRatingByAccommodation_NoComments_ShouldReturnZero() {
        // Arrange
        when(accommodationRepository.findById(accommodationId)).thenReturn(Optional.of(accommodation));
        when(commentRepository.findAverageRatingByAccommodationId(accommodationId)).thenReturn(null);

        // Act
        Double result = commentService.getAverageRatingByAccommodation(accommodationId);

        // Assert
        assertThat(result).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should return the correct comment count")
    void getCommentCountByAccommodation_Success() {
        // Arrange
        Long expectedCount = 5L;
        when(accommodationRepository.findById(accommodationId)).thenReturn(Optional.of(accommodation));
        when(commentRepository.countByAccommodationIdAndDeletedFalse(accommodationId)).thenReturn(expectedCount);

        // Act
        long result = commentService.getCommentCountByAccommodation(accommodationId);

        // Assert
        assertThat(result).isEqualTo(expectedCount);
    }

    // ----------------------------------------------------------------------
    // Tests para updateComment
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Should update comment successfully when user is the owner")
    void updateComment_Success() {
        // Arrange
        CommentUpdateDTO updateDTO = new CommentUpdateDTO("Updated text", 5);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(userRepository.findByEmail(guestEmail)).thenReturn(Optional.of(guestUser));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toResponseDto(comment)).thenReturn(responseDTO);

        // Act
        CommentResponseDTO result = commentService.updateComment(commentId, updateDTO, guestEmail);

        // Assert
        assertThat(result).isNotNull();
        verify(commentMapper, times(1)).updateEntity(updateDTO, comment);
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    @DisplayName("Should throw UnauthorizedCommentAccessException when a non-owner tries to update")
    void updateComment_NonOwner_ShouldThrowException() {
        // Arrange
        CommentUpdateDTO updateDTO = new CommentUpdateDTO("Updated text", 5);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(userRepository.findByEmail(hostEmail)).thenReturn(Optional.of(hostUser)); // Host trying to update guest's comment

        // Act & Assert
        assertThatThrownBy(() -> commentService.updateComment(commentId, updateDTO, hostEmail))
                .isInstanceOf(UnauthorizedCommentAccessException.class)
                .hasMessage("Usuario no autorizado para actualizar este comentario");
        verify(commentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw CommentNotFoundException when updating non-existent comment")
    void updateComment_NotFound_ShouldThrowException() {
        // Arrange
        CommentUpdateDTO updateDTO = new CommentUpdateDTO("Updated text", 5);
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> commentService.updateComment(commentId, updateDTO, guestEmail))
                .isInstanceOf(CommentNotFoundException.class)
                .hasMessage("Comentario no encontrado");
    }

    // ----------------------------------------------------------------------
    // Tests para deleteComment
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Should soft delete comment successfully when user is the owner")
    void deleteComment_Success() {
        // Arrange
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(userRepository.findByEmail(guestEmail)).thenReturn(Optional.of(guestUser));

        // Act
        commentService.deleteComment(commentId, guestEmail);

        // Assert
        assertThat(comment.isDeleted()).isTrue();
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    @DisplayName("Should throw UnauthorizedCommentAccessException when a non-owner tries to delete")
    void deleteComment_NonOwner_ShouldThrowException() {
        // Arrange
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(userRepository.findByEmail(hostEmail)).thenReturn(Optional.of(hostUser));

        // Act & Assert
        assertThatThrownBy(() -> commentService.deleteComment(commentId, hostEmail))
                .isInstanceOf(UnauthorizedCommentAccessException.class)
                .hasMessage("Usuario no autorizado para eliminar este comentario");
        verify(commentRepository, never()).save(any());
    }

    // ----------------------------------------------------------------------
    // Tests para replyToComment (Respuesta del Anfitrión)
    // ----------------------------------------------------------------------

    @Test
    @DisplayName("Should reply to comment successfully when user is the host owner")
    void replyToComment_HostOwner_Success() {
        // Arrange
        CommentReplyDTO replyDTO = new CommentReplyDTO("Thanks for your feedback!");

        when(userRepository.findByEmail(hostEmail)).thenReturn(Optional.of(hostUser));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toResponseDto(comment)).thenReturn(responseDTO);

        // Act
        CommentResponseDTO result = commentService.replyToComment(commentId, replyDTO, hostEmail);

        // Assert
        assertThat(result).isNotNull();
        // Verifica que los campos dé respuesta se hallan seteado
        assertThat(comment.getHostReplyText()).isEqualTo(replyDTO.getReplyText());
        assertThat(comment.getReplyDate()).isNotNull();
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    @DisplayName("Should throw UnauthorizedCommentAccessException when a non-host tries to reply")
    void replyToComment_NonHost_ShouldThrowException() {
        // Arrange
        CommentReplyDTO replyDTO = new CommentReplyDTO("Thanks!");
        when(userRepository.findByEmail(guestEmail)).thenReturn(Optional.of(guestUser));

        // Act & Assert
        assertThatThrownBy(() -> commentService.replyToComment(commentId, replyDTO, guestEmail))
                .isInstanceOf(UnauthorizedCommentAccessException.class)
                .hasMessage("Solo los Anfitriones pueden responder comentarios");
        verify(commentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw UnauthorizedCommentAccessException when host tries to reply to another host's accommodation")
    void replyToComment_NotOwnerHost_ShouldThrowException() {
        // Arrange
        CommentReplyDTO replyDTO = new CommentReplyDTO("Test");
        User otherHost = new User();
        otherHost.setId(99L);
        otherHost.setEmail("otherhost@test.com");
        otherHost.setRole(Role.HOST);

        when(userRepository.findByEmail(otherHost.getEmail())).thenReturn(Optional.of(otherHost));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // Act & Assert
        assertThatThrownBy(() -> commentService.replyToComment(commentId, replyDTO, otherHost.getEmail()))
                .isInstanceOf(UnauthorizedCommentAccessException.class)
                .hasMessage("Usuario no autorizado: solo el dueño del alojamiento puede responder");
        verify(commentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw CommentNotFoundException when replying to non-existent comment")
    void replyToComment_NotFound_ShouldThrowException() {
        // Arrange
        CommentReplyDTO replyDTO = new CommentReplyDTO("Test");
        when(userRepository.findByEmail(hostEmail)).thenReturn(Optional.of(hostUser));
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> commentService.replyToComment(commentId, replyDTO, hostEmail))
                .isInstanceOf(CommentNotFoundException.class)
                .hasMessage("Comentario no encontrado");
    }
}