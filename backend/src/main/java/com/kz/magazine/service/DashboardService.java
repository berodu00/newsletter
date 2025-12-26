package com.kz.magazine.service;

import com.kz.magazine.dto.content.ContentResponseDto;
import com.kz.magazine.dto.dashboard.CategoryStatDto;
import com.kz.magazine.dto.dashboard.HashtagStatDto;
import com.kz.magazine.dto.dashboard.ReactionStatDto;
import com.kz.magazine.dto.dashboard.VisitorTrendDto;
import com.kz.magazine.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final ContentRepository contentRepository;
    private final CategoryRepository categoryRepository;
    private final HashtagRepository hashtagRepository;
    private final ReactionRepository reactionRepository;
    private final VisitorLogRepository visitorLogRepository;

    public List<ContentResponseDto> getTopViewedContents(int limit) {
        return contentRepository
                .findTop10ByStatusAndDeletedAtIsNullOrderByViewCountDesc(com.kz.magazine.entity.ContentStatus.PUBLISHED)
                .stream()
                .limit(limit)
                .map(ContentResponseDto::from)
                .toList();
    }

    public List<ContentResponseDto> getTopRatedContents(int limit) {
        // Assuming Rating logic is integrated in Content.averageRating
        return contentRepository
                .findTop10ByStatusAndDeletedAtIsNullOrderByAverageRatingDesc(
                        com.kz.magazine.entity.ContentStatus.PUBLISHED)
                .stream()
                .limit(limit)
                .map(ContentResponseDto::from)
                .toList();
    }

    public List<VisitorTrendDto> getVisitorTrend(int days) {
        // Calculate startDate based on days
        java.time.LocalDate startDate = java.time.LocalDate.now().minusDays(days);
        return visitorLogRepository.findVisitorTrend(startDate)
                .stream()
                .map(v -> new VisitorTrendDto(v.getVisitedAt(), v.getPageViews().longValue(), v.getVisitorCount()))
                .toList();
    }

    public List<CategoryStatDto> getCategoryStats() {
        return contentRepository.countContentsByCategory(com.kz.magazine.entity.ContentStatus.PUBLISHED);
    }

    public List<HashtagStatDto> getHashtagStats(int limit) {
        return hashtagRepository.findTop10ByOrderByUsageCountDesc()
                .stream()
                .limit(limit)
                .map(h -> new HashtagStatDto(h.getHashtagName(), h.getUsageCount()))
                .toList();
    }

    public List<ReactionStatDto> getReactionStats() {
        return reactionRepository.countReactionsByType();
    }
}
