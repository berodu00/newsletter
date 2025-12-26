package com.kz.magazine.dto.popup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PopupRequestDto {
    private String title;
    private String content;
    private Long imageFileId;
    private String linkUrl;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Boolean isActive;
    private Integer displayOrder;
}
