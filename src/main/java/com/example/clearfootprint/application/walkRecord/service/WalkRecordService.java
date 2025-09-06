package com.example.clearfootprint.application.walkRecord.service;

import com.example.clearfootprint.domain.user.UserRepository;
import com.example.clearfootprint.domain.walkRecord.WalkRecord;
import com.example.clearfootprint.domain.walkRecord.WalkRecordRepository;
import com.example.clearfootprint.presentation.walkRecord.dto.GetTargetDisAndWalkDisResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WalkRecordService {

    private final WalkRecordRepository walkRecordRepository;
    private final UserRepository userRepository;

    private BigDecimal defaultTargetM; // 기록이 없는 날의 기본 목표

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    @Transactional
    public GetTargetDisAndWalkDisResponse getTargetDisAndWalkDis(Long userId, int num){
        if (userId == null) throw new IllegalArgumentException("userId는 필수입니다.");
        if (num <= 0) throw new IllegalArgumentException("num은 1 이상이어야 합니다.");

        LocalDate end = LocalDate.now(KST);
        LocalDate start = end.minusDays((num - 1));

        var aggs = walkRecordRepository.aggregateByUserAndDateRange(userId, start, end);

        Map<LocalDate, WalkRecordRepository.WalkDailyAgg> byDate = new HashMap<>();
        for (var a : aggs) byDate.put(a.getDate(), a);

        var days = new java.util.ArrayList<GetTargetDisAndWalkDisResponse.Daily>(num);
        for (int i = 0; i < num; i++) {
            LocalDate d = start.plusDays(i);
            var a = byDate.get(d);

            BigDecimal walked = (a == null || a.getWalkedDistanceM() == null)
                    ? BigDecimal.ZERO : a.getWalkedDistanceM();

            BigDecimal target = (a == null || a.getTargetDistanceM() == null)
                    ? defaultTargetM : a.getTargetDistanceM();

            days.add(GetTargetDisAndWalkDisResponse.Daily.builder()
                    .date(d)
                    .targetDistanceM(target)
                    .walkedDistanceM(walked)
                    .achieved(walked.compareTo(target) >= 0)
                    .build());
        }

        return GetTargetDisAndWalkDisResponse.builder()
                .days(days)
                .build();

    }

    @Transactional
    public void updateWalk(Long userId, BigDecimal targetDistanceM, BigDecimal walkedDistanceM){
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        var record = walkRecordRepository.findByUserIdAndDate(userId, today)
                .orElseGet(() -> {
                    var wr = new WalkRecord();
                    wr.setUser(userRepository.getReferenceById(userId));
                    wr.setDate(today);
                    wr.setTargetDistanceM(BigDecimal.ZERO);
                    wr.setDistanceM(BigDecimal.ZERO);
                    return wr;
                });

        var t = (targetDistanceM);
        var w = (walkedDistanceM);
        if (t != null) record.setTargetDistanceM(t);
        if (w != null) record.setDistanceM(w);

        var saved = walkRecordRepository.save(record);
        boolean achieved = saved.getDistanceM().compareTo(saved.getTargetDistanceM()) >= 0;

    }

}
