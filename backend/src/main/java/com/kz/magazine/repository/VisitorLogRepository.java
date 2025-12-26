package com.kz.magazine.repository;

import com.kz.magazine.entity.VisitorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VisitorLogRepository extends JpaRepository<VisitorLog, Long> {

    @Query("SELECT v FROM VisitorLog v WHERE v.visitedAt >= :startDate ORDER BY v.visitedAt ASC")
    List<VisitorLog> findVisitorTrend(@Param("startDate") LocalDate startDate);
}
