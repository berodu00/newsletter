package com.kz.magazine.service;

import com.kz.magazine.entity.AuditLog;
import com.kz.magazine.entity.User;
import com.kz.magazine.repository.AuditLogRepository;
import com.kz.magazine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public void logAction(String username, String action, String entityType, Long entityId, String beforeJson,
            String afterJson) {
        User actor = null;
        if (username != null) {
            actor = userRepository.findByUsername(username).orElse(null);
        }

        AuditLog auditLog = AuditLog.builder()
                .actor(actor)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .beforeJson(beforeJson)
                .afterJson(afterJson)
                .build();

        auditLogRepository.save(auditLog);
    }

    public void logAction(String username, String action, String entityType, Long entityId) {
        logAction(username, action, entityType, entityId, null, null);
    }
}
