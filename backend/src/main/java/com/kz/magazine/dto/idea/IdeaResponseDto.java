package com.kz.magazine.dto.idea;

import com.kz.magazine.entity.Idea;
import com.kz.magazine.entity.IdeaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdeaResponseDto {
    private Long ideaId;
    private String title;
    private String description;
    private IdeaStatus status;
    private String adminComment;
    private LocalDateTime createdAt;
    private String authorName;

    public static IdeaResponseDto from(Idea idea) {
        return IdeaResponseDto.builder()
                .ideaId(idea.getIdeaId())
                .title(idea.getTitle())
                .description(idea.getDescription())
                .status(idea.getStatus())
                .adminComment(idea.getAdminComment())
                .createdAt(idea.getCreatedAt())
                .authorName(idea.getUser().getName())
                .build();
    }
}
