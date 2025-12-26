package com.kz.magazine.service;

import com.kz.magazine.entity.AuditLog;
import com.kz.magazine.entity.User;
import com.kz.magazine.repository.AuditLogRepository;
import com.kz.magazine.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuditLogService auditLogService;

    @Test
    void logAction_Success() {
        // Given
        String username = "admin";
        User user = new User();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When
        auditLogService.logAction(username, "CREATE", "CONTENT", 1L);

        // Then
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }
}
