package com.example.clearfootprint.presentation.walkRecord.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class GetRankResponse {
    private final List<LeaderboardEntry> top10;
    private final Integer myRank; // null이면 유저 없음

    @Getter
    @AllArgsConstructor
    public static class LeaderboardEntry {
        private final String nickname;
        private final BigDecimal walkedDistanceM;
        private final Integer rank;
    }

}
