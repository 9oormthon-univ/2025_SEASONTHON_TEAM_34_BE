package com.example.clearfootprint.domain.walkRecord;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WalkRecordRepository extends JpaRepository<WalkRecord, Long> {

    Optional<WalkRecord> findByUserIdAndDate(Long userId, LocalDate today);

    interface WalkDailyAgg {
        LocalDate getDate();
        BigDecimal getWalkedDistanceM();
        BigDecimal getTargetDistanceM();
    }

    @Query("""
           select w.date as date,
                  coalesce(sum(w.distanceM), 0)  as walkedDistanceM,
                  max(w.targetDistanceM)         as targetDistanceM
           from WalkRecord w
           where w.user.id = :userId
             and w.date between :start and :end
           group by w.date
           """)
    List<WalkDailyAgg> aggregateByUserAndDateRange(@Param("userId") Long userId,
                                                   @Param("start") LocalDate start,
                                                   @Param("end") LocalDate end);

}
