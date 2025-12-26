package com.kz.magazine.dto.idea;

import com.kz.magazine.entity.IdeaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdeaReviewDto {
    private IdeaStatus status;
    private String adminComment;
}
