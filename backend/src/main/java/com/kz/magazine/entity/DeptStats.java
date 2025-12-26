package com.kz.magazine.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "dept_stats", uniqueConstraints = @UniqueConstraint(columnNames = { "department", "stat_date" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
public class DeptStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stat_id")
    private Long statId;

    @Column(nullable = false)
    private String department;

    @Column(name = "stat_date", nullable = false)
    private LocalDate statDate;

    @Column(name = "user_count")
    @Builder.Default
    private Long userCount = 0L;

    @Column(name = "content_count")
    @Builder.Default
    private Long contentCount = 0L;

    @Column(name = "view_count")
    @Builder.Default
    private Long viewCount = 0L;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
