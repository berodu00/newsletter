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
    private String youtubeUrl;
    private String instagramUrl;
    private java.util.Map<String, Integer> reactions;
    private String userReaction;
    private Integer userRating;

    public static ContentDetailResponseDto from(Content content, List<String> hashtags) {
        return from(content, hashtags, null, null, null);
    }

    public static ContentDetailResponseDto from(Content content, List<String> hashtags,
            java.util.Map<String, Integer> reactions, String userReaction, Integer userRating) {
        return ContentDetailResponseDto.builder()
                .contentId(content.getContentId())
                .title(content.getTitle())
                .bodyHtml(content.getBodyHtml())
                .categoryName(content.getCategory().getCategoryName())
                .status(content.getStatus().name())
                .authorName(content.getAuthor().getName())
                .department(content.getAuthor().getDepartment())
                .publishedAt(content.getPublishedAt())
                .viewCount(content.getViewCount())
                .ratingCount(content.getRatingCount())
                .averageRating(content.getAverageRating() != null ? content.getAverageRating().doubleValue() : 0.0)
                .hashtags(hashtags)
                .youtubeUrl(content.getYoutubeUrl())
                .instagramUrl(content.getInstagramUrl())
                .reactions(reactions)
                .userReaction(userReaction)
                .userRating(userRating)
                .build();
    }
}
