package com.kz.magazine.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "visitor_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @Column(name = "visited_at", nullable = false, unique = true)
    private LocalDate visitedAt;

    @Column(name = "page_views")
    @Builder.Default
    private Long pageViews = 0L; // Changed to Long

    @Column(name = "visitor_count")
    @Builder.Default
    private Long visitorCount = 0L;
}
