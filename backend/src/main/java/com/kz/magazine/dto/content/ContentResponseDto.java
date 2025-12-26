package com.kz.magazine.dto.content;

import com.kz.magazine.entity.Content;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ContentResponseDto {
    private Long contentId;
    private String title;
    private String summary;
    private String thumbnailUrl; // URL from ResourceFile
    private String categoryName;
    private String status;
    private String authorName;
    private LocalDateTime publishedAt;
    private Long viewCount;
    private Long ratingCount;
    private Double averageRating;

    private String youtubeUrl;
    private String instagramUrl;

    public static ContentResponseDto from(Content content) {
        return ContentResponseDto.builder()
                .contentId(content.getContentId())
                .title(content.getTitle())
                .summary(content.getSummary())
                .thumbnailUrl(content.getThumbnailFile() != null ? content.getThumbnailFile().getFilePath() : null)
                .categoryName(content.getCategory().getCategoryName())
                .status(content.getStatus().name())
                .authorName(content.getAuthor().getName())
                .publishedAt(content.getPublishedAt())
                .viewCount(content.getViewCount())
                .ratingCount(content.getRatingCount())
                .averageRating(content.getAverageRating() != null ? content.getAverageRating().doubleValue() : 0.0)
                .youtubeUrl(content.getYoutubeUrl())
                .instagramUrl(content.getInstagramUrl())
                .build();
    }
}
