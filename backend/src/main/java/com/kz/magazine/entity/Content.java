package com.kz.magazine.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "contents")
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private Long contentId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "body_html", columnDefinition = "TEXT", nullable = false)
    private String bodyHtml; // Renamed from content

    @Column(name = "body_text", columnDefinition = "TEXT")
    private String bodyText;

    @Column(name = "summary", length = 500)
    private String summary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thumbnail_file_id")
    private ResourceFile thumbnailFile;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContentStatus status; // DRAFT, PUBLISHED, ARCHIVED

    @Builder.Default
    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Builder.Default
    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContentHashtag> hashtags = new ArrayList<>();

    @Column(name = "youtube_url")
    private String youtubeUrl;

    @Column(name = "instagram_url")
    private String instagramUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Setter
    private LocalDateTime publishedAt;

    private LocalDateTime deletedAt;
    private Long deletedBy;

    // Statistics
    // viewCount defined above
    @Builder.Default
    private Long ratingCount = 0L;
    @Builder.Default
    private Long ratingSum = 0L;
    @Builder.Default
    private BigDecimal averageRating = BigDecimal.ZERO;

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void softDelete(Long deletedBy) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
        this.status = ContentStatus.ARCHIVED;
    }
}
