package com.kz.magazine.dto;

import com.kz.magazine.entity.Content;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class ContentDto {

    @Getter
    @Builder
    public static class ListResponse {
        private Long contentId;
        private String title;
        private String summary;
        private String thumbnailUrl;
        private String categoryName;
        private String status;
        private String authorName;
        private Long viewCount;
        private Double averageRating;
        private LocalDateTime publishedAt;

        public static ListResponse from(Content content) {
            return ListResponse.builder()
                    .contentId(content.getContentId())
                    .title(content.getTitle())
                    .summary(content.getSummary())
                    .thumbnailUrl(content.getThumbnailFile() != null ? "/uploads/" + content.getThumbnailFile().getStoredName() : null)
                    .categoryName(content.getCategory().getCategoryName())
                    .status(content.getStatus())
                    .authorName(content.getAuthor().getName())
                    .viewCount(content.getViewCount())
                    .averageRating(content.getAverageRating().doubleValue())
                    .publishedAt(content.getPublishedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class DetailResponse {
        private Long contentId;
        private String title;
        private String summary;
        private String bodyHtml;
        private String thumbnailUrl;
        private String categoryName;
        private String status;
        private String authorName;
        private String authorDepartment;
        private Long viewCount;
        private Double averageRating;
        private LocalDateTime publishedAt;
        private List<String> hashtags;

        public static DetailResponse from(Content content, List<String> hashtags) {
            return DetailResponse.builder()
                    .contentId(content.getContentId())
                    .title(content.getTitle())
                    .summary(content.getSummary())
                    .bodyHtml(content.getBodyHtml())
                    .thumbnailUrl(content.getThumbnailFile() != null ? "/uploads/" + content.getThumbnailFile().getStoredName() : null)
                    .categoryName(content.getCategory().getCategoryName())
                    .status(content.getStatus())
                    .authorName(content.getAuthor().getName())
                    .authorDepartment(content.getAuthor().getDepartment())
                    .viewCount(content.getViewCount())
                    .averageRating(content.getAverageRating().doubleValue())
                    .publishedAt(content.getPublishedAt())
                    .hashtags(hashtags)
                    .build();
        }
    }
}
