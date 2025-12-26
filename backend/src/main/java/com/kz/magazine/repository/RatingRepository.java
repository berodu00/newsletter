package com.kz.magazine.repository;

import com.kz.magazine.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByContent_ContentIdAndUser_UserId(Long contentId, Long userId);

    @Query("SELECT AVG(r.ratingValue) FROM Rating r WHERE r.content.contentId = :contentId")
    Double getAverageRating(@Param("contentId") Long contentId);

    long countByContent_ContentId(Long contentId);
}
