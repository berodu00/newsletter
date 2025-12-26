package com.kz.magazine.controller;

import com.kz.magazine.service.ReactionService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contents")
@RequiredArgsConstructor
public class ReactionController {

    private final ReactionService reactionService;

    @PostMapping("/{contentId}/like")
    public ResponseEntity<Void> toggleLike(
            @PathVariable Long contentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        reactionService.toggleLike(contentId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{contentId}/rating")
    public ResponseEntity<Void> submitRating(
            @PathVariable Long contentId,
            @RequestBody RatingRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        reactionService.submitRating(contentId, request.getRating(), userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @Data
    public static class RatingRequestDto {
        private Integer rating;
    }
}
