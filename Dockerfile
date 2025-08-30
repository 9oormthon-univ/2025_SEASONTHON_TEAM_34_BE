# ---------- 1) Build stage ----------
FROM gradle:8.10.2-jdk17 AS build
WORKDIR /workspace
# 캐시 활용을 위해 순서 고정
COPY build.gradle settings.gradle gradle.properties* ./
COPY gradle ./gradle
COPY src ./src
# Gradle 캐시 경로 고정(권한/캐시 안정)
RUN gradle -g /workspace/.gradle clean bootJar -x test --no-daemon

# ---------- 2) Runtime stage ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# (선택) 타임존을 서울로 + 헬스체크용 curl 설치
RUN apt-get update \
 && apt-get install -y --no-install-recommends tzdata curl \
 && ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime \
 && rm -rf /var/lib/apt/lists/*

# 보안상 비루트 유저로 실행
RUN useradd -m -u 10001 app
USER app

# JAR 복사
COPY --from=build /workspace/build/libs/*.jar app.jar

# 환경 변수
ENV SPRING_PROFILES_ACTIVE=prod \
    SERVER_PORT=8080 \
    JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseG1GC"

EXPOSE 8080

# 헬스체크(Actuator 필요)
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s \
  CMD curl -sf http://localhost:${SERVER_PORT}/actuator/health | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar --server.port=${SERVER_PORT}"]

# 개발 모드(핫리로드): docker compose --profile dev up -d
# 배포 모드(이미지 빌드→실행): docker compose --profile prod up -d --build
