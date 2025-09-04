/*어댑터(외부 API 호출 구현체), 실제 공급자 스펙이 정해지면 UsageDto/ActivityDto 필드만 바꾸면 됨*/
package com.example.clearfootprint.infrastructure.adapter;

import com.example.clearfootprint.domain.port.ActivityProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;

@Component
public class ActivityApiAdapter implements ActivityProvider {
    private final WebClient web;

    public ActivityApiAdapter(@Value("${external.activity.base-url}") String baseUrl) {
        this.web = WebClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public Activity fetchActivity(String accessToken, Instant from, Instant to) {
        var dto = web.get()
                .uri(u -> u.path("/activity").queryParam("from", from).queryParam("to", to).build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(ActivityDto.class)
                .block();

        double distance = dto != null ? dto.distanceKm : 0.0;
        long steps = dto != null ? dto.steps : 0;
        Mode mode = dto != null && "RUN".equalsIgnoreCase(dto.mode) ? Mode.RUN : Mode.WALK;
        return new Activity(from, to, distance, steps, mode);
    }
    private record ActivityDto(double distanceKm, long steps, String mode) {}
}

