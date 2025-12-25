package com.kz.magazine.dto.content;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ContentFilterDto {
    private String category;
    private String hashtag;
    private String search;
    private String status = "PUBLISHED";
}
