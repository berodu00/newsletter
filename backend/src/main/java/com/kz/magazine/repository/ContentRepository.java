package com.kz.magazine.repository;

import com.kz.magazine.entity.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

        // Find published contents
        Page<Content> findByStatusAndDeletedAtIsNull(String status, Pageable pageable);

        // Find by category and status
        Page<Content> findByCategory_CategoryNameAndStatusAndDeletedAtIsNull(
                        String categoryName, String status, Pageable pageable);

        // Find scheduled contents ready to publish
        Page<Content> findByStatusAndPublishedAtBeforeAndDeletedAtIsNull(
                        String status, LocalDateTime now, Pageable pageable);

        // Find by status and youtube url is not null
        Page<Content> findByStatusAndYoutubeUrlIsNotNullAndDeletedAtIsNull(String status, Pageable pageable);

        // Find by status and instagram url is not null
        Page<Content> findByStatusAndInstagramUrlIsNotNullAndDeletedAtIsNull(String status, Pageable pageable);

        // Increment view count
        @Modifying
        @Query("UPDATE Content c SET c.viewCount = c.viewCount + 1 WHERE c.contentId = :contentId")
        void incrementViewCount(@Param("contentId") Long contentId);
}
