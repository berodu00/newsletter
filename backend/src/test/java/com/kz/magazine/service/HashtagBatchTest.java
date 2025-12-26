package com.kz.magazine.service;

import com.kz.magazine.repository.HashtagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HashtagBatchTest {

    @Mock
    private HashtagRepository hashtagRepository;

    @InjectMocks
    private HashtagBatchService hashtagBatchService;

    @Test
    @DisplayName("Recalculate Hashtag Counts - Calls Repository")
    void recalculateHashtagCounts_Success() {
        // When
        hashtagBatchService.recalculateHashtagCounts();

        // Then
        verify(hashtagRepository).updateAllUsageCounts();
    }
}
