/* 어댑터(외부 API 호출 구현체), 실제 공급자 스펙이 정해지면 UsageDto/ActivityDto 필드만 바꾸면 됨 */
package com.example.clearfootprint.infrastructure.adapter;

// 필요한 import 구문들을 모두 추가했습니다.
import com.example.clearfootprint.domain.port.ActivityProvider;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
public class ActivityApiAdapter implements ActivityProvider {
    private final WebClient web;

    public ActivityApiAdapter() {
        // 실제 Google Fit API의 기본 URL로 WebClient를 생성합니다.
        this.web = WebClient.builder()
                .baseUrl("https://www.googleapis.com/fitness/v1/users/me")
                .build();
    }

    @Override
    public Activity fetchActivity(String accessToken, Instant from, Instant to) {
        // Google Fit API에 보낼 요청 본문을 만듭니다.
        var requestBody = new GoogleFitAggregateRequest(
                List.of(
                        new AggregateBy("com.google.step_count.delta"),
                        new AggregateBy("com.google.distance.delta")
                ),
                new BucketByTime(24 * 60 * 60 * 1000L), // 하루 단위로 집계
                from.toEpochMilli(),
                to.toEpochMilli()
        );

        // WebClient를 사용해 Google Fit API에 POST 요청을 보냅니다.
        var response = web.post()
                .uri("/dataset:aggregate")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(GoogleFitAggregateResponse.class)
                .block();

        // API 응답 결과(response)를 안전하게 분석해서 걸음 수와 이동 거리를 추출합니다.
        long totalSteps = 0;
        double totalDistanceMeters = 0.0;

        // Optional을 사용해 NullPointerException 방지
        totalSteps = Optional.ofNullable(response)
                .map(GoogleFitAggregateResponse::bucket)
                .stream()
                .flatMap(List::stream)
                .map(Bucket::dataset)
                .flatMap(List::stream)
                .filter(d -> "com.google.step_count.delta".equals(d.dataSourceId()))
                .map(Dataset::point)
                .flatMap(List::stream)
                .map(Point::value)
                .flatMap(List::stream)
                .mapToLong(Value::intVal)
                .sum();

        totalDistanceMeters = Optional.ofNullable(response)
                .map(GoogleFitAggregateResponse::bucket)
                .stream()
                .flatMap(List::stream)
                .map(Bucket::dataset)
                .flatMap(List::stream)
                .filter(d -> "com.google.distance.delta".equals(d.dataSourceId()))
                .map(Dataset::point)
                .flatMap(List::stream)
                .map(Point::value)
                .flatMap(List::stream)
                .mapToDouble(Value::fpVal)
                .sum();

        // 최종적으로 집계된 데이터를 Activity 객체로 만들어 반환합니다.
        double totalDistanceKm = totalDistanceMeters / 1000.0;
        return new Activity(from, to, totalDistanceKm, totalSteps, Mode.WALK);
    }

    // --- Google Fit API의 복잡한 JSON 응답 구조에 맞춰 만든 DTO 클래스들 ---

    private record GoogleFitAggregateRequest(List<AggregateBy> aggregateBy, BucketByTime bucketByTime, long startTimeMillis, long endTimeMillis) {}
    private record AggregateBy(String dataTypeName) {}
    private record BucketByTime(long durationMillis) {}
    private record GoogleFitAggregateResponse(List<Bucket> bucket) {}
    private record Bucket(List<Dataset> dataset) {}
    private record Dataset(@JsonProperty("dataSourceId") String dataSourceId, List<Point> point) {}
    private record Point(List<Value> value) {}
    private record Value(@JsonProperty("intVal") long intVal, @JsonProperty("fpVal") double fpVal) {}
}