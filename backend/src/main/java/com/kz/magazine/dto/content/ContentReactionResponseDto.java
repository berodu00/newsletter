package com.kz.magazine.dto.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentReactionResponseDto {
    private String action; // "added", "removed", "changed"
    private String currentReaction; // user's current reaction type
    private Map<String, Integer> reactions; // Updated counts
}
