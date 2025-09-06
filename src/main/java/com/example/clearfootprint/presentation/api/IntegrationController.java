package com.example.clearfootprint.presentation.api;

import com.example.clearfootprint.application.service.IntegrationSyncService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/integrations")
public class IntegrationController {

    private final IntegrationSyncService service;

    public IntegrationController(IntegrationSyncService service) {
        this.service = service;
    }

    // 무로그인: 선택적 디바이스 식별자 헤더(있으면 받기)
    private static final String DEVICE_HEADER = "X-Device-Id";

    @PostMapping("/ai/sync")
    public Map<String, Object> syncAi(
            @RequestHeader(value = DEVICE_HEADER, required = false) String deviceId,
            @RequestParam @NotBlank String accessToken,
            @RequestParam @NotNull @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @NotNull @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) Instant to
    ) {
        var r = service.syncAiUsage(accessToken, from, to);
        return Map.of("deviceId", deviceId, "carbon_g", r.carbon_g(), "offset_km", r.offset_km());
    }

    @PostMapping("/activity/sync")
    public Map<String, Object> syncActivity(
            @RequestHeader(value = DEVICE_HEADER, required = false) String deviceId,
            @RequestParam @NotBlank String accessToken,
            @RequestParam @NotNull @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @NotNull @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) Instant to
    ) {
        var r = service.syncActivity(accessToken, from, to);
        return Map.of("deviceId", deviceId, "distance_km", r.distance_km(), "steps", r.steps(), "mode", r.mode());
    }
}