package com.kz.magazine.repository;

import com.kz.magazine.entity.Event;
import com.kz.magazine.entity.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    // Find active events within current date range
    Page<Event> findByStatusAndDeletedAtIsNull(EventStatus status, Pageable pageable);

    Optional<Event> findByIdAndDeletedAtIsNull(Long id);
}
