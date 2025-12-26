package com.kz.magazine.repository;

import com.kz.magazine.entity.Idea;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IdeaRepository extends JpaRepository<Idea, Long> {

    // Find valid ideas (not deleted)
    Page<Idea> findByDeletedAtIsNull(Pageable pageable);

    // Find my ideas
    Page<Idea> findByUser_UserIdAndDeletedAtIsNull(Long userId, Pageable pageable);
}
