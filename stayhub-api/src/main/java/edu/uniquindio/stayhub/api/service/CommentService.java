package edu.uniquindio.stayhub.api.service;

import edu.uniquindio.stayhub.api.dto.comment.CommentReplyDTO;
import edu.uniquindio.stayhub.api.dto.comment.CommentRequestDTO;
import edu.uniquindio.stayhub.api.dto.comment.CommentResponseDTO;
import edu.uniquindio.stayhub.api.dto.comment.CommentUpdateDTO;
import edu.uniquindio.stayhub.api.exception.CommentNotFoundException;
import edu.uniquindio.stayhub.api.exception.UnauthorizedCommentAccessException;
import edu.uniquindio.stayhub.api.model.Accommodation;
import edu.uniquindio.stayhub.api.model.Comment;
import edu.uniquindio.stayhub.api.model.Role;
import edu.uniquindio.stayhub.api.model.User;
import edu.uniquindio.stayhub.api.repository.AccommodationRepository;
import edu.uniquindio.stayhub.api.repository.CommentRepository;
import edu.uniquindio.stayhub.api.repository.UserRepository;
import edu.uniquindio.stayhub.api.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final AccommodationRepository accommodationRepository;
    private final CommentMapper commentMapper;

    @Transactional
    public CommentResponseDTO createComment(CommentRequestDTO commentRequestDTO, String currentUserEmail) {
        // Validar que el usuario autenticado coincide con el userId del DTO
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UnauthorizedCommentAccessException("Usuario no encontrado"));
        if (!user.getId().equals(commentRequestDTO.getUserId())) {
            throw new UnauthorizedCommentAccessException("Usuario no autorizado para crear este comentario");
        }

        // Validar que el alojamiento existe
        Accommodation accommodation = accommodationRepository.findById(commentRequestDTO.getAccommodationId())
                .orElseThrow(() -> new IllegalArgumentException("Alojamiento no encontrado"));

        // Validar que el usuario no haya comentado antes sobre este alojamiento
        if (commentRepository.existsByUserIdAndAccommodationIdAndDeletedFalse(
                commentRequestDTO.getUserId(), commentRequestDTO.getAccommodationId())) {
            throw new IllegalStateException("El usuario ya ha comentado sobre este alojamiento");
        }

        Comment comment = commentMapper.toEntity(commentRequestDTO);
        // Setear manualmente user y accommodation porque el mapper los ignora
        comment.setUser(user);
        comment.setAccommodation(accommodation);
        comment = commentRepository.save(comment);
        return commentMapper.toResponseDto(comment); // Corregido: toResponseDto
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getCommentsByAccommodation(Long accommodationId) {
        // Validar que el alojamiento existe
        accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("Alojamiento no encontrado"));

        return commentRepository.findByAccommodationIdAndDeletedFalseOrderByCreatedAtDesc(accommodationId)
                .stream()
                .map(commentMapper::toResponseDto) // Corregido: toResponseDto
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<CommentResponseDTO> getCommentsByAccommodationPaginated(Long accommodationId, Pageable pageable) {
        // Validar que el alojamiento existe
        accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("Alojamiento no encontrado"));

        return commentRepository.findByAccommodationIdAndDeletedFalseOrderByCreatedAtDesc(accommodationId, pageable)
                .map(commentMapper::toResponseDto); // Corregido: toResponseDto
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getCommentsByUser(Long userId, String currentUserEmail) {
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UnauthorizedCommentAccessException("Usuario no encontrado"));
        if (!user.getId().equals(userId)) {
            throw new UnauthorizedCommentAccessException("Usuario no autorizado para ver estos comentarios");
        }
        return commentRepository.findByUserIdAndDeletedFalse(userId)
                .stream()
                .map(commentMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Double getAverageRatingByAccommodation(Long accommodationId) {
        // Validar que el alojamiento existe
        accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("Alojamiento no encontrado"));

        Double average = commentRepository.findAverageRatingByAccommodationId(accommodationId);
        return average != null ? average : 0.0;
    }

    @Transactional(readOnly = true)
    public long getCommentCountByAccommodation(Long accommodationId) {
        // Validar que el alojamiento existe
        accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new IllegalArgumentException("Alojamiento no encontrado"));

        return commentRepository.countByAccommodationIdAndDeletedFalse(accommodationId);
    }

    @Transactional
    public CommentResponseDTO updateComment(Long id, CommentUpdateDTO commentUpdateDTO, String currentUserEmail) {
        Comment comment = commentRepository.findById(id)
                .filter(c -> !c.isDeleted())
                .orElseThrow(() -> new CommentNotFoundException("Comentario no encontrado"));

        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UnauthorizedCommentAccessException("Usuario no encontrado"));
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedCommentAccessException("Usuario no autorizado para actualizar este comentario");
        }

        commentMapper.updateEntity(commentUpdateDTO, comment);
        comment = commentRepository.save(comment);
        return commentMapper.toResponseDto(comment); // Corregido: toResponseDto
    }

    @Transactional
    public void deleteComment(Long id, String currentUserEmail) {
        Comment comment = commentRepository.findById(id)
                .filter(c -> !c.isDeleted())
                .orElseThrow(() -> new CommentNotFoundException("Comentario no encontrado"));

        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UnauthorizedCommentAccessException("Usuario no encontrado"));
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedCommentAccessException("Usuario no autorizado para eliminar este comentario");
        }

        comment.setDeleted(true);
        commentRepository.save(comment);
    }

    @Transactional
    public CommentResponseDTO replyToComment(Long commentId, CommentReplyDTO replyDTO, String currentUserEmail) {
        User host = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UnauthorizedCommentAccessException("Usuario no encontrado o no autenticado"));

        if (host.getRole() != Role.HOST) {
            throw new UnauthorizedCommentAccessException("Solo los Anfitriones pueden responder comentarios");
        }
        Comment comment = commentRepository.findById(commentId)
                .filter(c -> !c.isDeleted())
                .orElseThrow(() -> new CommentNotFoundException("Comentario no encontrado"));

        Long accommodationHostId = comment.getAccommodation().getHost().getId();
        if (!accommodationHostId.equals(host.getId())) {
            throw new UnauthorizedCommentAccessException("Usuario no autorizado: solo el dueño del alojamiento puede responder");
        }

        comment.setHostReplyText(replyDTO.getReplyText());
        comment.setReplyDate(LocalDateTime.now());

        comment = commentRepository.save(comment);
        return commentMapper.toResponseDto(comment);
    }
}