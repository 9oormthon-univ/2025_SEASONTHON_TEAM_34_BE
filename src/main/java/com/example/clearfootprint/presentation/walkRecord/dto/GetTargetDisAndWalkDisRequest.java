package com.example.clearfootprint.presentation.walkRecord.dto;

import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
public class GetTargetDisAndWalkDisRequest {
    private final Long id;
    private final Integer num;

    public GetTargetDisAndWalkDisRequest(Long userId, Long id, Integer num) {
        this.id = id;
        this.num = num;
    }
}
