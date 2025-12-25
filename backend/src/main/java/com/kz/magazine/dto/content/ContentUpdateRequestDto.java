package com.kz.magazine.dto.content;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ContentUpdateRequestDto {
    private String title;
    private String bodyHtml;
    private String bodyText;
    private String summary;
    private String categoryName;
    private List<String> hashtags;
    private String status;
    private Long thumbnailFileId;
}
