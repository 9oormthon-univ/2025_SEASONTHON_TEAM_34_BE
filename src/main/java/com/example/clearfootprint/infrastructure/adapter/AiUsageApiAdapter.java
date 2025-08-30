/*어댑터(외부 API 호출 구현체), 실제 공급자 스펙이 정해지면 UsageDto/ActivityDto 필드만 바꾸면 됨*/
package com.example.clearfootprint.infrastructure.adapter;

import com.example.clearfootprint.domain.port.AiUsageProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;

@Component
public class AiUsageApiAdapter implements AiUsageProvider {
    private final WebClient web;

    public AiUsageApiAdapter(@Value("${external.ai.base-url}") String baseUrl) {
        this.web = WebClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public Usage fetchUsage(String accessToken, Instant from, Instant to) {
        var dto = web.get()
                .uri(u -> u.path("/usage").queryParam("from", from).queryParam("to", to).build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(UsageDto.class)
                .block();
        long tokens = dto != null ? dto.tokens : 0;
        long gpuSec = dto != null ? dto.gpuSeconds : 0;
        return new Usage(from, to, tokens, gpuSec);
    }
    private record UsageDto(long tokens, long gpuSeconds) {}
}

