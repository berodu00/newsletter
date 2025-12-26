package com.kz.magazine.controller;

import com.kz.magazine.dto.content.ContentResponseDto;
import com.kz.magazine.dto.dashboard.CategoryStatDto;
import com.kz.magazine.dto.dashboard.HashtagStatDto;
import com.kz.magazine.dto.dashboard.ReactionStatDto;
import com.kz.magazine.dto.dashboard.VisitorTrendDto;
import com.kz.magazine.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/top-views")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ContentResponseDto>> getTopViewedContents(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(dashboardService.getTopViewedContents(limit));
    }

    @GetMapping("/top-ratings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ContentResponseDto>> getTopRatedContents(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(dashboardService.getTopRatedContents(limit));
    }

    @GetMapping("/visitor-trend")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VisitorTrendDto>> getVisitorTrend(@RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(dashboardService.getVisitorTrend(days));
    }

    @GetMapping("/category-stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CategoryStatDto>> getCategoryStats() {
        return ResponseEntity.ok(dashboardService.getCategoryStats());
    }

    @GetMapping("/hashtag-stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<HashtagStatDto>> getHashtagStats(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(dashboardService.getHashtagStats(limit));
    }

    @GetMapping("/reaction-stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReactionStatDto>> getReactionStats() {
        return ResponseEntity.ok(dashboardService.getReactionStats());
    }
}
