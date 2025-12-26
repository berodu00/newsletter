package com.kz.magazine.service;

import com.kz.magazine.entity.DeptStats;
import com.kz.magazine.repository.ContentRepository;
import com.kz.magazine.repository.DeptStatsRepository;
import com.kz.magazine.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class DeptStatsTest {

    @Mock
    private DeptStatsRepository deptStatsRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ContentRepository contentRepository;

    @InjectMocks
    private DeptStatsService deptStatsService;

    @Test
    @DisplayName("Calculate Dept Stats - Success")
    void calculateDeptStats_Success() {
        // Given
        given(userRepository.findDistinctDepartments()).willReturn(List.of("IT"));
        given(userRepository.countByDepartmentAndDeletedAtIsNull("IT")).willReturn(10L);
        given(contentRepository.getStatsByDepartment("IT")).willReturn(new Object[] { 5L, 100L });
        given(deptStatsRepository.findByDepartmentAndStatDate(anyString(), any())).willReturn(Optional.empty());

        // When
        deptStatsService.calculateDeptStats();

        // Then
        verify(deptStatsRepository, times(1)).save(any(DeptStats.class));
    }
}
