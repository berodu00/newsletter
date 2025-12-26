package com.kz.magazine.repository;

import com.kz.magazine.entity.EventParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {

    boolean existsByEvent_IdAndUser_Username(Long eventId, String username);

    Optional<EventParticipant> findByEvent_IdAndUser_Username(Long eventId, String username);

    @Query(value = "SELECT * FROM event_participants WHERE event_id = :eventId AND is_winner = false ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<EventParticipant> findRandomParticipants(@Param("eventId") Long eventId, @Param("limit") int limit);

    List<EventParticipant> findByEvent_IdAndIsWinnerTrue(Long eventId);
}
