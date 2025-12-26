package com.kz.magazine.repository;

import com.kz.magazine.entity.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
        Optional<Reaction> findByContent_ContentIdAndUser_UserIdAndReactionType(Long contentId, Long userId,
                        String reactionType);

        java.util.List<Reaction> findByContent_ContentId(Long contentId);

        java.util.List<Reaction> findByContent_ContentIdAndUser_UserId(Long contentId, Long userId);

        @Query("SELECT new com.kz.magazine.dto.dashboard.ReactionStatDto(r.reactionType, COUNT(r)) " +
                        "FROM Reaction r GROUP BY r.reactionType")
        java.util.List<com.kz.magazine.dto.dashboard.ReactionStatDto> countReactionsByType();
}
