package com.kz.magazine.repository;

import com.kz.magazine.entity.DailyVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface DailyVisitRepository extends JpaRepository<DailyVisit, Long> {

    @Query("SELECT COUNT(v) FROM DailyVisit v WHERE v.visitTime >= :start AND v.visitTime < :end")
    long countByVisitTimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(DISTINCT v.ipAddress) FROM DailyVisit v WHERE v.visitTime >= :start AND v.visitTime < :end")
    long countDistinctIpByVisitTimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Modifying
    @Query("DELETE FROM DailyVisit v WHERE v.visitTime < :cutoff")
    void deleteOlderThan(@Param("cutoff") LocalDateTime cutoff);
}
