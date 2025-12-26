package com.kz.magazine.service;

import com.kz.magazine.entity.DailyVisit;
import com.kz.magazine.entity.VisitorLog;
import com.kz.magazine.repository.DailyVisitRepository;
import com.kz.magazine.repository.VisitorLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class VisitorLogService {

    private final DailyVisitRepository dailyVisitRepository;
    private final VisitorLogRepository visitorLogRepository;

    @Transactional
    public void logVisit(String ipAddress, String userAgent) {
        // Simple fire-and-forget insert
        DailyVisit visit = new DailyVisit(ipAddress, userAgent);
        dailyVisitRepository.save(visit);
    }

    // Run every day at 00:05 AM to aggregate previous day's data
    @Scheduled(cron = "0 5 0 * * *")
    @Transactional
    public void aggregateDailyStats() {
        log.info("Starting daily visitor aggregation...");

        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDateTime start = yesterday.atStartOfDay();
        LocalDateTime end = yesterday.atTime(LocalTime.MAX);

        long pageViews = dailyVisitRepository.countByVisitTimeBetween(start, end);
        long uniqueVisitors = dailyVisitRepository.countDistinctIpByVisitTimeBetween(start, end);

        VisitorLog logEntry = new VisitorLog();
        logEntry.setVisitedAt(yesterday);
        logEntry.setPageViews(pageViews);
        logEntry.setVisitorCount(uniqueVisitors);

        visitorLogRepository.save(logEntry);

        // Cleanup old raw data (keep 7 days for safety)
        dailyVisitRepository.deleteOlderThan(LocalDateTime.now().minusDays(7));

        log.info("Aggregated stats for {}: PV={}, UV={}", yesterday, pageViews, uniqueVisitors);
    }
}
