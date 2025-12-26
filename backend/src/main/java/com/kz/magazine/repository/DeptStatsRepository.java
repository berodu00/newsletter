package com.kz.magazine.repository;

import com.kz.magazine.entity.DeptStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DeptStatsRepository extends JpaRepository<DeptStats, Long> {
    Optional<DeptStats> findByDepartmentAndStatDate(String department, LocalDate statDate);
}
