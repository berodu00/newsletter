package com.kz.magazine.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "contents")
public class Content extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contentId;

    @Setter
    @Column(nullable = false)
    private String title;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String summary;

    @Setter
    @Column(columnDefinition = "TEXT", nullable = false)
    private String bodyHtml;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String bodyText;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thumbnail_file_id")
    private ResourceFile thumbnailFile;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Setter
    @Column(nullable = false)
    private String status; // "DRAFT", "PUBLISHED", "SCHEDULED", "ARCHIVED"

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Setter
    private LocalDateTime publishedAt;

    private LocalDateTime deletedAt;
    private Long deletedBy;

    // Statistics
    private Long viewCount = 0L;
    private Long ratingCount = 0L;
    private Long ratingSum = 0L;
    private BigDecimal averageRating = BigDecimal.ZERO;

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void softDelete(Long deletedBy) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
        this.status = "ARCHIVED";
    }
}
