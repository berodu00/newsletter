package com.kz.magazine.repository;

import com.kz.magazine.entity.Content;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class ContentRepositoryImpl implements ContentRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Content> searchContents(String keyword, String status, Pageable pageable) {
        String sql = "SELECT c.* FROM contents c " +
                "JOIN content_search cs ON c.content_id = cs.content_id " +
                "WHERE cs.search_vector @@ to_tsquery('simple', :keyword) " +
                "AND c.status = :status AND c.deleted_at IS NULL";

        String countSql = "SELECT count(*) FROM contents c " +
                "JOIN content_search cs ON c.content_id = cs.content_id " +
                "WHERE cs.search_vector @@ to_tsquery('simple', :keyword) " +
                "AND c.status = :status AND c.deleted_at IS NULL";

        Query query = entityManager.createNativeQuery(sql, Content.class);
        query.setParameter("keyword", keyword);
        query.setParameter("status", status);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<Content> results = query.getResultList();

        Query countQuery = entityManager.createNativeQuery(countSql);
        countQuery.setParameter("keyword", keyword);
        countQuery.setParameter("status", status);
        long total = ((Number) countQuery.getSingleResult()).longValue();

        return new PageImpl<>(results, pageable, total);
    }
}
