package com.kz.magazine.service;

import com.kz.magazine.repository.ContentRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContentViewServiceTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private ContentRepository contentRepository;

    @InjectMocks
    private ContentViewService contentViewService;

    @Mock
    private Query query;

    @BeforeEach
    void setUp() {
        org.springframework.test.util.ReflectionTestUtils.setField(contentViewService, "entityManager", entityManager);
    }

    @Test
    void incrementViewCount_Success() {
        // Given
        Long contentId = 1L;
        Long userId = 1L;

        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1); // Inserted

        // When
        contentViewService.incrementViewCount(contentId, userId);

        // Then
        verify(contentRepository, times(1)).incrementViewCount(contentId);
    }

    @Test
    void incrementViewCount_Deduped() {
        // Given
        Long contentId = 1L;
        Long userId = 1L;

        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(0); // Conflict (Already viewed)

        // When
        contentViewService.incrementViewCount(contentId, userId);

        // Then
        verify(contentRepository, never()).incrementViewCount(contentId);
    }
}
