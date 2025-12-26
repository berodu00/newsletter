package com.kz.magazine.service;

import com.kz.magazine.repository.ContentRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ContentViewService {
    @PersistenceContext
    private EntityManager entityManager;

    private final ContentRepository contentRepository;

    @Transactional
    public void incrementViewCount(Long contentId, Long userId) {
        if (userId == null) {
            // If anonymous user, maybe just increment? Or skip dedup?
            // For now, let's assume we skip viewing count for anonymous or just increment
            // without dedup?
            // TechSpec says "viewed_bucket" calculation.
            // Let's increment for anonymous directly or do nothing?
            // Usually we want to count anonymous views too, but dedup is hard.
            // Let's simply return if userId is null for now as per schema requirements
            // (user_id NOT NULL).
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        int minute = now.getMinute();
        LocalDateTime bucket = now.withMinute(minute < 30 ? 0 : 30).withSecond(0).withNano(0);

        String sql = "INSERT INTO content_views_dedup (content_id, user_id, viewed_bucket) VALUES (:contentId, :userId, :bucket) ON CONFLICT DO NOTHING";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("contentId", contentId);
        query.setParameter("userId", userId);
        query.setParameter("bucket", bucket);

        int affectedRows = query.executeUpdate();

        if (affectedRows > 0) {
            // Deduplication successful (new view), increment view count
            contentRepository.incrementViewCount(contentId);
        }
    }
}
