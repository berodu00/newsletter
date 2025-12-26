package com.kz.magazine.service;

import com.kz.magazine.entity.DailyVisit;
import com.kz.magazine.entity.VisitorLog;
import com.kz.magazine.repository.DailyVisitRepository;
import com.kz.magazine.repository.VisitorLogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VisitorLogTest {

    @Mock
    private DailyVisitRepository dailyVisitRepository;

    @Mock
    private VisitorLogRepository visitorLogRepository;

    @InjectMocks
    private VisitorLogService visitorLogService;

    @Test
    @DisplayName("Log Visit - Saves to DailyVisit")
    void logVisit_Success() {
        visitorLogService.logVisit("127.0.0.1", "Chrome");
        verify(dailyVisitRepository).save(any(DailyVisit.class));
    }

    @Test
    @DisplayName("Aggregate Daily Stats - Saves to VisitorLog")
    void aggregateDailyStats_Success() {
        given(dailyVisitRepository.countByVisitTimeBetween(any(), any())).willReturn(100L);
        given(dailyVisitRepository.countDistinctIpByVisitTimeBetween(any(), any())).willReturn(50L);

        visitorLogService.aggregateDailyStats();

        verify(visitorLogRepository).save(any(VisitorLog.class));
        verify(dailyVisitRepository).deleteOlderThan(any());
    }
}
