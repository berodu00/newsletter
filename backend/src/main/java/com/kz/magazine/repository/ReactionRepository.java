package com.kz.magazine.repository;

import com.kz.magazine.entity.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Optional<Reaction> findByContent_ContentIdAndUser_UserIdAndReactionType(Long contentId, Long userId,
            String reactionType);
}
