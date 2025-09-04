package com.example.clearfootprint.domain.port;

import java.time.Instant;

public interface AiUsageProvider {
    record Usage(Instant from, Instant to, long tokens, long gpuSeconds) {}
    Usage fetchUsage(String accessToken, Instant from, Instant to);
}

/*공급자 무관(OpenAI/Claude 등, Google Fit/Apple Health/Samsung Health 등).
모바일 앱에서 OAuth(또는 SDK)로 받은 액세스 토큰을 우리 백엔드에 전달 → 백엔드가 포트로 호출.*/