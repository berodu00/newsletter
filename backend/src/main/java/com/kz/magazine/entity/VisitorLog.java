package com.kz.magazine.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "visitor_logs", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "visited_at" }))
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitorLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @Column(name = "user_id")
    private Long userId; // Nullable for anonymous if needed, but schema says REFERENCES users

    @Column(name = "visited_at", nullable = false)
    private LocalDate visitedAt;

    @Builder.Default
    @Column(name = "page_views")
    private Integer pageViews = 1;

    public void incrementPageViews() {
        this.pageViews++;
    }
}
