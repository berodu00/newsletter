package com.kz.magazine.repository;

import com.kz.magazine.entity.ContentHashtag;
import com.kz.magazine.entity.ContentHashtagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentHashtagRepository extends JpaRepository<ContentHashtag, ContentHashtagId> {

    @Query("SELECT ch.hashtag.hashtagName FROM ContentHashtag ch WHERE ch.content.contentId = :contentId")
    List<String> findHashtagNamesByContentId(@Param("contentId") Long contentId);
}
