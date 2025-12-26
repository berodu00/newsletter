package com.kz.magazine.dto.popup;

import com.kz.magazine.entity.Popup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PopupResponseDto {
    private Long popupId;
    private String title;
    private String content;
    private String imageUrl;
    private String linkUrl;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Boolean isActive;
    private Integer displayOrder;

    public static PopupResponseDto from(Popup popup) {
        return PopupResponseDto.builder()
                .popupId(popup.getPopupId())
                .title(popup.getTitle())
                .content(popup.getContent())
                .imageUrl(popup.getImageFile() != null ? popup.getImageFile().getFilePath() : null)
                .linkUrl(popup.getLinkUrl())
                .startAt(popup.getStartAt())
                .endAt(popup.getEndAt())
                .isActive(popup.getIsActive())
                .displayOrder(popup.getDisplayOrder())
                .build();
    }
}
