package com.example.clearfootprint.application.service;

import com.example.clearfootprint.domain.model.Distance;
import com.example.clearfootprint.domain.model.Emission;
import com.example.clearfootprint.domain.port.AiUsageProvider;
import com.example.clearfootprint.domain.port.ActivityProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
public class IntegrationSyncService {

    private final AiUsageProvider ai;
    private final ActivityProvider activity;

    public IntegrationSyncService(AiUsageProvider ai, ActivityProvider activity) {
        this.ai = ai; this.activity = activity;
    }

    public AiResult syncAiUsage(String token, Instant from, Instant to) {
        var u = ai.fetchUsage(token, from, to);
        double g = u.tokens() * 1e-4 + u.gpuSeconds() * 0.02; // 임시 계수
        var e = Emission.ofGram(g);
        var d = Distance.ofKm(e.gram().doubleValue() / 50.0);
        return new AiResult(e.gram().doubleValue(), d.km().doubleValue());
    }

    public ActivityResult syncActivity(String token, Instant from, Instant to) {
        var a = activity.fetchActivity(token, from, to);
        return new ActivityResult(a.distanceKm(), a.steps(), a.mode().name());
    }

    public record AiResult(double carbon_g, double offset_km) {}
    public record ActivityResult(double distance_km, long steps, String mode) {}
}