package com.example.clearfootprint.domain.walkRecord;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        INSERT INTO walk_records (user_id, date, distance_m, target_distance_m)
        VALUES (:userId, :date, :distanceM, :targetDistanceM)
        ON DUPLICATE KEY UPDATE
          distance_m = VALUES(distance_m),
          target_distance_m = VALUES(target_distance_m)
        """, nativeQuery = true)
    void upsertByUserAndDate(
            @Param("userId") Long userId,
            @Param("date") java.time.LocalDate date,
            @Param("distanceM") java.math.BigDecimal distanceM,
            @Param("targetDistanceM") java.math.BigDecimal targetDistanceM
    );


    interface WeeklyRankRow {
        Long getUserId();
        String getNickname();
        BigDecimal getTotalDistanceM(); // total_distance_m 별칭과 매핑
        Integer getRnk();               // rnk 별칭과 매핑
    }

    @Query(value = """
        WITH weekly AS (
          SELECT u.id AS user_id,
                 u.nickname AS nickname,
                 COALESCE(SUM(wr.distance_m), 0) AS total_distance_m
          FROM users u
          LEFT JOIN walk_records wr
            ON wr.user_id = u.id
           AND wr.date BETWEEN :startDate AND :endDate
          GROUP BY u.id, u.nickname
        ),
        ranked AS (
          SELECT user_id, nickname, total_distance_m,
                 RANK() OVER (ORDER BY total_distance_m DESC, user_id ASC) AS rnk
          FROM weekly
        )
        SELECT user_id, nickname, total_distance_m, rnk
        FROM ranked
        ORDER BY rnk ASC, user_id ASC
        LIMIT 10
        """, nativeQuery = true)
    List<WeeklyRankRow> findWeeklyTop10(@Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);

    @Query(value = """
        WITH weekly AS (
          SELECT u.id AS user_id,
                 u.nickname AS nickname,
                 COALESCE(SUM(wr.distance_m), 0) AS total_distance_m
          FROM users u
          LEFT JOIN walk_records wr
            ON wr.user_id = u.id
           AND wr.date BETWEEN :startDate AND :endDate
          GROUP BY u.id, u.nickname
        ),
        ranked AS (
          SELECT user_id, nickname, total_distance_m,
                 RANK() OVER (ORDER BY total_distance_m DESC, user_id ASC) AS rnk
          FROM weekly
        )
        SELECT user_id, nickname, total_distance_m, rnk
        FROM ranked
        WHERE user_id = :userId
        """, nativeQuery = true)
    Optional<WeeklyRankRow> findMyWeeklyRank(@Param("userId") Long userId,
                                             @Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);
}
