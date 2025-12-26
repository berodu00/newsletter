package com.kz.magazine.service;

import com.kz.magazine.entity.Content;
import com.kz.magazine.entity.Reaction;
import com.kz.magazine.entity.User;
import com.kz.magazine.repository.ContentRepository;
import com.kz.magazine.repository.ReactionRepository;
import com.kz.magazine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReactionService {

        private final ReactionRepository reactionRepository;
        private final ContentRepository contentRepository;
        private final UserRepository userRepository;
        private final AuditLogService auditLogService;

        public void toggleLike(Long contentId, String username) {
                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

                Content content = contentRepository.findById(contentId)
                                .orElseThrow(() -> new IllegalArgumentException("Content not found: " + contentId));

                Optional<Reaction> existing = reactionRepository.findByContent_ContentIdAndUser_UserIdAndReactionType(
                                contentId, user.getUserId(), "LIKE");

                if (existing.isPresent()) {
                        reactionRepository.delete(existing.get());
                        auditLogService.logAction(username, "UNLIKE", "CONTENT", contentId);
                } else {
                        Reaction reaction = Reaction.builder()
                                        .content(content)
                                        .user(user)
                                        .reactionType("LIKE")
                                        .build();
                        reactionRepository.save(reaction);
                        auditLogService.logAction(username, "LIKE", "CONTENT", contentId);
                }
        }

        public void submitRating(Long contentId, Integer rating, String username) {
                if (rating < 1 || rating > 5) {
                        throw new IllegalArgumentException("Rating must be between 1 and 5");
                }

                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

                Content content = contentRepository.findById(contentId)
                                .orElseThrow(() -> new IllegalArgumentException("Content not found: " + contentId));

                Optional<Reaction> existing = reactionRepository.findByContent_ContentIdAndUser_UserIdAndReactionType(
                                contentId, user.getUserId(), "RATING");

                if (existing.isPresent()) {
                        existing.get().setRatingValue(rating);
                        auditLogService.logAction(username, "UPDATE_RATING", "CONTENT", contentId, null,
                                        "Rating: " + rating);
                } else {
                        Reaction reaction = Reaction.builder()
                                        .content(content)
                                        .user(user)
                                        .reactionType("RATING")
                                        .ratingValue(rating)
                                        .build();
                        reactionRepository.save(reaction);
                        auditLogService.logAction(username, "RATING", "CONTENT", contentId, null, "Rating: " + rating);
                }
        }
}
