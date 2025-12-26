package com.kz.magazine.controller;

import com.kz.magazine.dto.content.ContentDetailResponseDto;
import com.kz.magazine.dto.content.ContentFilterDto;
import com.kz.magazine.dto.content.ContentResponseDto;

import com.kz.magazine.entity.User;
import com.kz.magazine.repository.UserRepository;
import com.kz.magazine.service.ContentService;
import com.kz.magazine.service.ContentViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import com.kz.magazine.dto.content.ContentCreateRequestDto;
import com.kz.magazine.dto.content.ContentUpdateRequestDto;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contents")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;
    private final ContentViewService contentViewService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<Page<ContentResponseDto>> getContents(
            @ModelAttribute ContentFilterDto filter,
            @PageableDefault(size = 10, sort = "publishedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(contentService.getContents(filter, pageable));
    }

    @GetMapping("/{contentId}")
    public ResponseEntity<ContentDetailResponseDto> getContent(
            @PathVariable Long contentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = null;
        if (userDetails != null) {
            User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
            if (user != null) {
                userId = user.getUserId();
            }
        }

        contentViewService.incrementViewCount(contentId, userId);

        return ResponseEntity
                .ok(contentService.getContent(contentId, userDetails != null ? userDetails.getUsername() : null));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContentDetailResponseDto> createContent(
            @Valid @RequestBody ContentCreateRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(contentService.createContent(request, userDetails.getUsername()));
    }

    @PutMapping("/{contentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContentDetailResponseDto> updateContent(
            @PathVariable Long contentId,
            @RequestBody ContentUpdateRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(contentService.updateContent(contentId, request, userDetails.getUsername()));
    }

    @DeleteMapping("/{contentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteContent(
            @PathVariable Long contentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        contentService.deleteContent(contentId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
