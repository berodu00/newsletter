package com.kz.magazine.service;

import com.kz.magazine.dto.popup.PopupRequestDto;
import com.kz.magazine.dto.popup.PopupResponseDto;
import com.kz.magazine.entity.Popup;
import com.kz.magazine.entity.ResourceFile;
import com.kz.magazine.entity.User;
import com.kz.magazine.repository.PopupRepository;
import com.kz.magazine.repository.ResourceFileRepository;
import com.kz.magazine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PopupService {

    private final PopupRepository popupRepository;
    private final ResourceFileRepository resourceFileRepository;
    private final UserRepository userRepository;

    public List<PopupResponseDto> getActivePopups() {
        return popupRepository.findActivePopups(LocalDateTime.now())
                .stream()
                .map(PopupResponseDto::from)
                .collect(Collectors.toList());
    }

    // Admin: List all? Page?
    public List<PopupResponseDto> getAllPopups() {
        return popupRepository.findAll()
                .stream()
                .map(PopupResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public PopupResponseDto createPopup(String username, PopupRequestDto dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ResourceFile imageFile = null;
        if (dto.getImageFileId() != null) {
            imageFile = resourceFileRepository.findById(dto.getImageFileId())
                    .orElse(null);
        }

        Popup popup = Popup.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .imageFile(imageFile)
                .linkUrl(dto.getLinkUrl())
                .startAt(dto.getStartAt())
                .endAt(dto.getEndAt())
                .isActive(dto.getIsActive())
                .displayOrder(dto.getDisplayOrder())
                .createdBy(user)
                .updatedBy(user)
                .build();

        return PopupResponseDto.from(popupRepository.save(popup));
    }

    @Transactional
    public PopupResponseDto updatePopup(Long id, String username, PopupRequestDto dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Popup popup = popupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Popup not found"));

        if (dto.getImageFileId() != null) {
            ResourceFile imageFile = resourceFileRepository.findById(dto.getImageFileId())
                    .orElse(null);
            popup.setImageFile(imageFile);
        }

        popup.setTitle(dto.getTitle());
        popup.setContent(dto.getContent());
        popup.setLinkUrl(dto.getLinkUrl());
        popup.setStartAt(dto.getStartAt());
        popup.setEndAt(dto.getEndAt());
        popup.setIsActive(dto.getIsActive());
        popup.setDisplayOrder(dto.getDisplayOrder());
        popup.setUpdatedBy(user);

        return PopupResponseDto.from(popup);
    }

    @Transactional
    public void deletePopup(Long id) {
        popupRepository.deleteById(id);
    }
}
