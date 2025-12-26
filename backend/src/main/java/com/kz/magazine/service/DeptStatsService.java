package com.kz.magazine.service;

import com.kz.magazine.entity.DeptStats;
import com.kz.magazine.repository.ContentRepository;
import com.kz.magazine.repository.DeptStatsRepository;
import com.kz.magazine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeptStatsService {

    private final DeptStatsRepository deptStatsRepository;
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;

    // Run daily at 1 AM
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void calculateDeptStats() {
        log.info("Starting department stats calculation...");
        LocalDate yesterday = LocalDate.now().minusDays(1);

        // 1. Get all departments
        List<String> departments = userRepository.findDistinctDepartments(); // Need to add this method to
                                                                             // UserRepository

        for (String dept : departments) {
            if (dept == null || dept.isEmpty())
                continue;

            // 2. Count users
            long userCount = userRepository.countByDepartmentAndDeletedAtIsNull(dept); // Need to add

            // 3. Count contents and views by dept users
            // This is complex. Contents are linked to Users. Users have department.
            // "SELECT count(c), sum(c.viewCount) FROM Content c JOIN c.author u WHERE
            // u.department = :dept AND c.status = 'PUBLISHED'"

            Object[] contentStats = contentRepository.getStatsByDepartment(dept); // Need to add
            long contentCount = (long) contentStats[0];
            long viewCount = contentStats[1] != null ? ((Number) contentStats[1]).longValue() : 0L;

            // 4. Save
            DeptStats stats = deptStatsRepository.findByDepartmentAndStatDate(dept, yesterday)
                    .orElse(DeptStats.builder()
                            .department(dept)
                            .statDate(yesterday)
                            .build());

            stats.setUserCount(userCount);
            stats.setContentCount(contentCount);
            stats.setViewCount(viewCount);

            deptStatsRepository.save(stats);
        }
        log.info("Finished department stats calculation.");
    }
}
