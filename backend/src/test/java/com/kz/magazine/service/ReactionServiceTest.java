package com.kz.magazine.service;

import com.kz.magazine.entity.Content;
import com.kz.magazine.entity.Reaction;
import com.kz.magazine.entity.User;
import com.kz.magazine.repository.ContentRepository;
import com.kz.magazine.repository.ReactionRepository;
import com.kz.magazine.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReactionServiceTest {

    @Mock
    private ReactionRepository reactionRepository;

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private ReactionService reactionService;

    @Test
    void toggleLike_AddLike() {
        // Given
        Long contentId = 1L;
        String username = "user";
        User user = new User();
        user.setUserId(1L);
        Content content = new Content();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(contentRepository.findById(contentId)).thenReturn(Optional.of(content));
        when(reactionRepository.findByContent_ContentIdAndUser_UserIdAndReactionType(contentId, 1L, "LIKE"))
                .thenReturn(Optional.empty());

        // When
        reactionService.toggleLike(contentId, username);

        // Then
        verify(reactionRepository, times(1)).save(any(Reaction.class));
        verify(auditLogService, times(1)).logAction(eq(username), eq("LIKE"), eq("CONTENT"), eq(contentId));
    }

    @Test
    void toggleLike_RemoveLike() {
        // Given
        Long contentId = 1L;
        String username = "user";
        User user = new User();
        user.setUserId(1L);
        Content content = new Content();
        Reaction existingLike = new Reaction();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(contentRepository.findById(contentId)).thenReturn(Optional.of(content));
        when(reactionRepository.findByContent_ContentIdAndUser_UserIdAndReactionType(contentId, 1L, "LIKE"))
                .thenReturn(Optional.of(existingLike));

        // When
        reactionService.toggleLike(contentId, username);

        // Then
        verify(reactionRepository, times(1)).delete(existingLike);
        verify(auditLogService, times(1)).logAction(eq(username), eq("UNLIKE"), eq("CONTENT"), eq(contentId));
    }
}
