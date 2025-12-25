package com.kz.magazine.dto.content;

import com.kz.magazine.entity.Content;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ContentDetailResponseDto {
    private Long contentId;
    private String title;
    private String bodyHtml;
    private String categoryName;
    private String status;
    private String authorName;
    private String department;
    private LocalDateTime publishedAt;
    private Long viewCount;
    private Long ratingCount;
    private Double averageRating;
    private List<String> hashtags;
    // TODO: Add Reaction info later

    public static ContentDetailResponseDto from(Content content, List<String> hashtags) {
        return ContentDetailResponseDto.builder()
                .contentId(content.getContentId())
                .title(content.getTitle())
                .bodyHtml(content.getBodyHtml())
                .categoryName(content.getCategory().getCategoryName())
                .status(content.getStatus())
                .authorName(content.getAuthor().getName())
                .department(content.getAuthor().getDepartment())
                .publishedAt(content.getPublishedAt())
                .viewCount(content.getViewCount())
                .ratingCount(content.getRatingCount())
                .averageRating(content.getAverageRating() != null ? content.getAverageRating().doubleValue() : 0.0)
                .hashtags(hashtags)
                .build();
    }
}
