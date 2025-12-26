package com.kz.magazine.repository;

import com.kz.magazine.entity.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    Optional<Hashtag> findByHashtagName(String hashtagName);

    java.util.List<Hashtag> findTop10ByOrderByUsageCountDesc();

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query(value = "UPDATE hashtags h " +
            "SET usage_count = (SELECT COUNT(*) FROM content_hashtags ch WHERE ch.hashtag_id = h.hashtag_id)", nativeQuery = true)
    void updateAllUsageCounts();
}
