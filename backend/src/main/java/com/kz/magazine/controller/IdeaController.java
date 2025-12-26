package com.kz.magazine.controller;

import com.kz.magazine.dto.idea.IdeaRequestDto;
import com.kz.magazine.dto.idea.IdeaResponseDto;
import com.kz.magazine.dto.idea.IdeaReviewDto;
import com.kz.magazine.service.IdeaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ideas")
@RequiredArgsConstructor
public class IdeaController {

    private final IdeaService ideaService;

    @GetMapping
    public Page<IdeaResponseDto> getIdeas(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return ideaService.getIdeas(userDetails.getUsername(), isAdmin, pageable);
    }

    @PostMapping
    public IdeaResponseDto createIdea(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody IdeaRequestDto requestDto) {
        return ideaService.createIdea(userDetails.getUsername(), requestDto);
    }

    @PutMapping("/{id}/review")
    @PreAuthorize("hasRole('ADMIN')")
    public IdeaResponseDto reviewIdea(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody IdeaReviewDto reviewDto) {
        return ideaService.reviewIdea(id, userDetails.getUsername(), reviewDto);
    }
}
