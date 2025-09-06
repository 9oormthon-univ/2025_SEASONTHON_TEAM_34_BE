package com.example.clearfootprint.presentation.walkRecord.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class GetTargetDisAndWalkDisResponse {

    private final List<Daily> days;

    @Getter
    @Builder
    public static class Daily{
        private final LocalDate date;
        private final BigDecimal targetDistanceM;
        private final BigDecimal walkedDistanceM;
        private final boolean achieved;
    }
}
