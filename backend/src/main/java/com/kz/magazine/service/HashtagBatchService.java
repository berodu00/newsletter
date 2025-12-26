package com.kz.magazine.service;

import com.kz.magazine.repository.HashtagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashtagBatchService {

    private final HashtagRepository hashtagRepository;

    // Run every day at 3 AM
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void recalculateHashtagCounts() {
        log.info("Starting hashtag usage count recalculation...");
        long start = System.currentTimeMillis();

        hashtagRepository.updateAllUsageCounts();

        long end = System.currentTimeMillis();
        log.info("Hashtag usage count recalculation completed in {} ms", (end - start));
    }
}
