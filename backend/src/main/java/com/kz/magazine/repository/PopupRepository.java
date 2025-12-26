package com.kz.magazine.repository;

import com.kz.magazine.entity.Popup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PopupRepository extends JpaRepository<Popup, Long> {

    // Check active
    @Query("SELECT p FROM Popup p WHERE p.isActive = true AND p.startAt <= :now AND p.endAt >= :now ORDER BY p.displayOrder ASC")
    List<Popup> findActivePopups(LocalDateTime now);
}
