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

        // Top 10 by View Count
        java.util.List<Content> findTop10ByStatusAndDeletedAtIsNullOrderByViewCountDesc(
                        com.kz.magazine.entity.ContentStatus status);

        // Top 10 by Rating
        java.util.List<Content> findTop10ByStatusAndDeletedAtIsNullOrderByAverageRatingDesc(
                        com.kz.magazine.entity.ContentStatus status);

        @Query("SELECT new com.kz.magazine.dto.dashboard.CategoryStatDto(c.category.categoryName, COUNT(c)) " +
                        "FROM Content c WHERE c.status = :status AND c.deletedAt IS NULL GROUP BY c.category.categoryName")
        java.util.List<com.kz.magazine.dto.dashboard.CategoryStatDto> countContentsByCategory(
                        @Param("status") com.kz.magazine.entity.ContentStatus status);

        // Full-Text Search
        @Query(value = "SELECT c.* FROM contents c " +
                        "JOIN content_search cs ON c.content_id = cs.content_id " +
                        "WHERE cs.search_vector @@ to_tsquery('simple', :keyword) " +
                        "AND c.status = :status AND c.deleted_at IS NULL", countQuery = "SELECT count(*) FROM contents c "
                                        +
                                        "JOIN content_search cs ON c.content_id = cs.content_id " +
                                        "WHERE cs.search_vector @@ to_tsquery('simple', :keyword) " +
                                        "AND c.status = :status AND c.deleted_at IS NULL", nativeQuery = true)
        Page<Content> searchContents(@Param("keyword") String keyword, @Param("status") String status,
                        Pageable pageable);

        @Query("SELECT count(c), sum(c.viewCount) FROM Content c JOIN c.author u WHERE u.department = :department AND c.status = com.kz.magazine.entity.ContentStatus.PUBLISHED AND c.deletedAt IS NULL")
        Object[] getStatsByDepartment(@Param("department") String department);
}
