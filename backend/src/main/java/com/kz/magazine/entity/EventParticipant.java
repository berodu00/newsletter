package com.kz.magazine.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_participants", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "event_id", "user_id" })
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "is_winner", nullable = false)
    @Builder.Default
    private boolean isWinner = false;

    @CreationTimestamp
    @Column(name = "participated_at", nullable = false, updatable = false)
    private LocalDateTime participatedAt;

    public void win() {
        this.isWinner = true;
    }
}
