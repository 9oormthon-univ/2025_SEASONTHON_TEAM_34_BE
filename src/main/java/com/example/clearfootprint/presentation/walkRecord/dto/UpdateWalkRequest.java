package com.example.clearfootprint.presentation.walkRecord.dto;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class UpdateWalkRequest {

    private final Long userId;
    private final BigDecimal targetDistanceM;
    private final BigDecimal walkedDistanceM;

    public UpdateWalkRequest(Long userId, BigDecimal targetDistanceM, BigDecimal walkedDistanceM) {
        this.userId = userId;
        this.targetDistanceM = targetDistanceM;
        this.walkedDistanceM = walkedDistanceM;
    }



}
