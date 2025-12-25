package com.kz.magazine.dto.content;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ContentCreateRequestDto {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Body HTML is required")
    private String bodyHtml;

    private String bodyText; // Optional, can be extracted from HTML

    private String summary;

    @NotBlank(message = "Category is required")
    private String categoryName;

    private List<String> hashtags;

    // Status can be DRAFT or PUBLISHED on create
    private String status = "DRAFT";

    private Long thumbnailFileId;
}
